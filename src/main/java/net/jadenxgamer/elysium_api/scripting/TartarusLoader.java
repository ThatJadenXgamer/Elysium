package net.jadenxgamer.elysium_api.scripting;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.toml.TomlParser;
import net.minecraft.SharedConstants;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;
import org.apache.maven.artifact.versioning.VersionRange;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class TartarusLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger("TartarusLoader");
    private static final String METADATA_FILE = "tartarus.toml";
    private static final String SCRIPTS_FOLDER = "scripts";
    private static final String MAIN_SCRIPT = "main.js";
    private static final int SCRIPT_LINE_NUMBER = 1;

    private TartarusLoader() {}

    public static void loadAllPacks() {
        Path packsDir = FMLPaths.GAMEDIR.get().resolve("tartarus_packs");
        if (!Files.exists(packsDir)) {
            try {
                Files.createDirectories(packsDir);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create directory: " + packsDir, e);
            }
        }

        Map<String, TartarusPackInfo> allPacks = new LinkedHashMap<>();
        try (Stream<Path> paths = Files.list(packsDir)) {
            paths.forEach(path -> {
                TartarusPackInfo info = readPackMetadata(path);
                if (info != null) {
                    if (allPacks.containsKey(info.scriptId())) {
                        LOGGER.warn("Duplicate scriptId '{}' found in {}, skipping", info.scriptId(), path);
                    } else allPacks.put(info.scriptId(), info);
                }
            });
        } catch (IOException e) {
            LOGGER.error("Failed to read tartarus_packs directory", e);
        }

        List<TartarusPackInfo> sortedPacks = resolveDependencyOrder(allPacks);
        Set<String> loadedScriptIds = new HashSet<>();

        for (TartarusPackInfo pack : sortedPacks) {
            if (validatePack(pack, allPacks, loadedScriptIds)) {
                executePack(pack);
                loadedScriptIds.add(pack.scriptId());
            } else {
                LOGGER.warn("Pack '{}' failed validation, skipped", pack.scriptId());
            }
        }
    }

    private static TartarusPackInfo readPackMetadata(Path packPath) {
        if (Files.isDirectory(packPath)) return readDirectoryMetadata(packPath);
        else if (packPath.toString().toLowerCase().endsWith(".zip")) return readZipMetadata(packPath);
        return null;
    }

    private static TartarusPackInfo readDirectoryMetadata(Path packDir) {
        Path tomlPath = packDir.resolve(METADATA_FILE);
        if (!Files.exists(tomlPath)) {
            LOGGER.debug("No {} found in directory {}", METADATA_FILE, packDir);
            return null;
        }
        try (Reader reader = Files.newBufferedReader(tomlPath)) {
            Config config = new TomlParser().parse(reader);
            return TartarusPackInfo.parsePackInfo(config, packDir, false);
        } catch (Exception e) {
            LOGGER.error("Failed to parse {} in {}", METADATA_FILE, packDir, e);
            return null;
        }
    }

    private static TartarusPackInfo readZipMetadata(Path zipPath) {
        try (ZipFile zipFile = new ZipFile(zipPath.toFile())) {
            ZipEntry entry = zipFile.getEntry(METADATA_FILE);
            if (entry == null) return null;
            try (Reader reader = new InputStreamReader(zipFile.getInputStream(entry))) {
                Config config = new TomlParser().parse(reader);
                return TartarusPackInfo.parsePackInfo(config, zipPath, true);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to read {} from zip {}", METADATA_FILE, zipPath, e);
            return null;
        }
    }

    static VersionRange parseVersionRange(String spec) {
        if (spec == null || spec.isBlank()) return null;
        try {
            return VersionRange.createFromVersionSpec(spec);
        } catch (Exception e) {
            LOGGER.warn("Invalid version range '{}'", spec, e);
            return null;
        }
    }

    private static List<TartarusPackInfo> resolveDependencyOrder(Map<String, TartarusPackInfo> allPacks) {
        Map<String, Set<String>> graph = new HashMap<>();
        for (TartarusPackInfo pack : allPacks.values()) {
            graph.putIfAbsent(pack.scriptId(), new HashSet<>());
            for (var dep : pack.scriptDependencies()) {
                if (dep.required()) {
                    graph.computeIfAbsent(pack.scriptId(), k -> new HashSet<>()).add(dep.scriptId());
                }
            }
        }

        List<TartarusPackInfo> sorted = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Set<String> visiting = new HashSet<>();

        for (String id : graph.keySet()) {
            if (!visited.contains(id)) {
                if (hasCycle(id, graph, visited, visiting, sorted, allPacks))
                    LOGGER.warn("Circular dependency detected involving {}", id);
            }
        }
        return sorted;
    }

    private static boolean hasCycle(String id, Map<String, Set<String>> graph, Set<String> visited, Set<String> visiting, List<TartarusPackInfo> sorted, Map<String, TartarusPackInfo> allPacks) {
        if (visiting.contains(id)) return true;
        if (visited.contains(id)) return false;
        visiting.add(id);
        for (String dep : graph.getOrDefault(id, Set.of())) {
            if (hasCycle(dep, graph, visited, visiting, sorted, allPacks)) return true;
        }
        visiting.remove(id);
        visited.add(id);
        TartarusPackInfo pack = allPacks.get(id);
        if (pack != null) sorted.add(pack);
        return false;
    }

    private static boolean validatePack(TartarusPackInfo pack, Map<String, TartarusPackInfo> allPacks, Set<String> loadedScriptIds) {
        String currentMcVersion = SharedConstants.getCurrentVersion().getName();
        if (!matchesVersion(currentMcVersion, pack.minecraftVersionSpec())) {
            LOGGER.warn("Pack {} requires Minecraft {}, but we have {}", pack.scriptId(), pack.minecraftVersionSpec(), currentMcVersion);
            return false;
        }
        String elysiumVersion = ModList.get().getModContainerById("elysium_api")
                .map(container -> container.getModInfo().getVersion().toString())
                .orElse("0.0.0");
        if (!matchesVersion(elysiumVersion, pack.elysiumVersionSpec())) {
            LOGGER.warn("Pack {} requires Elysium API {}, but we have {}", pack.scriptId(), pack.elysiumVersionSpec(), elysiumVersion);
            return false;
        }

        for (var modDependency : pack.modDependencies()) {
            boolean modPresent = ModList.get().isLoaded(modDependency.modId());
            if (modDependency.required() && !modPresent) {
                LOGGER.warn("Pack {} requires mod {} but it is missing", pack.scriptId(), modDependency.modId());
                return false;
            }
            if (modPresent && modDependency.versionRange() != null) {
                String modVersion = ModList.get().getModContainerById(modDependency.modId())
                        .get().getModInfo().getVersion().toString();
                if (!modDependency.versionRange().containsVersion(new org.apache.maven.artifact.versioning.DefaultArtifactVersion(modVersion))) {
                    LOGGER.warn("Pack {} requires mod {} version {}, but found {}", pack.scriptId(), modDependency.modId(), modDependency.versionRange(), modVersion);
                    return false;
                }
            }
        }

        for (var scriptDependency : pack.scriptDependencies()) {
            TartarusPackInfo depPack = allPacks.get(scriptDependency.scriptId());
            boolean depLoaded = loadedScriptIds.contains(scriptDependency.scriptId());
            if (scriptDependency.required() && !depLoaded && depPack == null) {
                LOGGER.warn("Pack {} requires tartarus pack {} but it is missing", pack.scriptId(), scriptDependency.scriptId());
                return false;
            }
            if (depLoaded && scriptDependency.versionRange() != null) {
                TartarusPackInfo existing = allPacks.get(scriptDependency.scriptId());
                if (existing != null && !matchesVersion(existing.version(), String.valueOf(scriptDependency.versionRange()))) {
                    LOGGER.warn("Pack {} requires script {} version {}, but loaded version is {}",
                            pack.scriptId(), scriptDependency.scriptId(), scriptDependency.versionRange(), existing.version());
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean matchesVersion(String version, String spec) {
        if (spec == null || spec.isBlank()) return true;
        VersionRange range = parseVersionRange(spec);
        if (range == null) return version.equals(spec);
        return range.containsVersion(new org.apache.maven.artifact.versioning.DefaultArtifactVersion(version));
    }

    private static void executePack(TartarusPackInfo pack) {
        if (pack.isZip()) executeZipPack(pack);
        else executeDirectoryPack(pack);
    }

    private static void executeDirectoryPack(TartarusPackInfo pack) {
        Path scriptsDir = pack.packPath().resolve(SCRIPTS_FOLDER);
        Path mainScript = scriptsDir.resolve(MAIN_SCRIPT);
        if (!Files.exists(mainScript)) {
            LOGGER.warn("No main.js found in directory pack {}", pack.scriptId());
            return;
        }
        ScriptSource source = new DirectoryScriptSource(scriptsDir);
        try (Reader reader = Files.newBufferedReader(mainScript)) {
            runScript(source, reader, pack.scriptId());
        } catch (IOException e) {
            LOGGER.error("Failed to execute directory pack {}", pack.scriptId(), e);
        }
    }

    private static void executeZipPack(TartarusPackInfo pack) {
        try (ZipFile zipFile = new ZipFile(pack.packPath().toFile())) {
            ZipEntry mainEntry = zipFile.getEntry(SCRIPTS_FOLDER + "/" + MAIN_SCRIPT);
            if (mainEntry == null) {
                LOGGER.warn("No main.js found in zip pack {}", pack.scriptId());
                return;
            }
            ScriptSource source = new ZipScriptSource(zipFile);
            try (Reader reader = new BufferedReader(new InputStreamReader(zipFile.getInputStream(mainEntry)))) {
                runScript(source, reader, pack.scriptId());
            }
        } catch (IOException e) {
            LOGGER.error("Failed to execute zip pack {}", pack.scriptId(), e);
        }
    }

    private static void runScript(ScriptSource source, Reader mainScriptReader, String packName) {
        Context cx = Context.enter();
        try {
            Scriptable globalScope = TartarusScriptManager.getGlobalScope();
            Scriptable packScope = createChildScope(cx, globalScope);
            attachLoadFunctions(source, packScope);
            cx.evaluateReader(packScope, mainScriptReader, packName + "/" + MAIN_SCRIPT, SCRIPT_LINE_NUMBER, null);
        } catch (IOException e) {
            throw new RuntimeException("Failed to evaluate pack: " + packName, e);
        } finally {
            Context.exit();
        }
    }

    private static Scriptable createChildScope(Context cx, Scriptable parent) {
        Scriptable child = cx.newObject(parent);
        child.setPrototype(parent);
        child.setParentScope(parent);
        return child;
    }

    private static void attachLoadFunctions(ScriptSource source, Scriptable scope) {
        ScriptableObject.putProperty(scope, "includeScript", new IncludeScriptFunction(source));
        ScriptableObject.putProperty(scope, "loadScript", new LoadScriptFunction(source));
    }

    private interface ScriptSource {
        Reader getScriptReader(String relativePath) throws IOException;
    }

    private record DirectoryScriptSource(Path scriptsDir) implements ScriptSource {
        @Override
        public Reader getScriptReader(String relativePath) throws IOException {
            Path resolved = scriptsDir.resolve(relativePath).normalize();
            if (!resolved.startsWith(scriptsDir)) throw new SecurityException("Access denied: " + relativePath);
            if (!Files.exists(resolved)) throw new FileNotFoundException("Script not found: " + relativePath);
            return Files.newBufferedReader(resolved);
        }
    }

    private record ZipScriptSource(ZipFile zipFile) implements ScriptSource {
        @Override
        public Reader getScriptReader(String relativePath) throws IOException {
            String safePath = SCRIPTS_FOLDER + "/" + relativePath.replace('\\', '/');
            if (safePath.contains("..")) throw new SecurityException("Invalid script path: " + relativePath);
            ZipEntry entry = zipFile.getEntry(safePath);
            if (entry == null) throw new FileNotFoundException("Script not found in zip: " + relativePath);
            return new InputStreamReader(zipFile.getInputStream(entry));
        }
    }

    /*
     * SCRIPT LOADERS
     */

    private record IncludeScriptFunction(ScriptSource source) implements Callable {
        @Override
        public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
            if (args.length == 0 || !(args[0] instanceof String path)) return null;
            try (Reader reader = source.getScriptReader(path)) {
                return cx.evaluateReader(scope, reader, path, SCRIPT_LINE_NUMBER, null);
            } catch (Exception e) {
                throw new RuntimeException("Failed to include script: " + path, e);
            }
        }
    }

    private record LoadScriptFunction(ScriptSource source) implements Callable {
        @Override
        public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
            if (args.length == 0 || !(args[0] instanceof String path)) return null;
            try (Reader reader = source.getScriptReader(path)) {
                Scriptable global = TartarusScriptManager.getGlobalScope();
                Scriptable isolated = cx.newObject(global);
                isolated.setPrototype(global);
                isolated.setParentScope(global);
                return cx.evaluateReader(isolated, reader, path, SCRIPT_LINE_NUMBER, null);
            } catch (Exception e) {
                throw new RuntimeException("Failed to load script: " + path, e);
            }
        }
    }
}