package net.jadenxgamer.elysium_api.scripting;

import com.electronwill.nightconfig.core.Config;
import org.apache.maven.artifact.versioning.VersionRange;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public record TartarusPackInfo(String scriptId, String version, String displayName, String authors, String description,
                               String minecraftVersionSpec, String elysiumVersionSpec, String license,
                               List<ModDependency> modDependencies, List<ScriptDependency> scriptDependencies,
                               Path packPath, boolean isZip) {

    static TartarusPackInfo parsePackInfo(Config config, Path packPath, boolean isZip) {
        String scriptId = config.get("scriptId");
        String version = config.get("version");
        String displayName = config.get("displayName");
        String authors = config.get("authors");
        String description = config.get("description");
        String minecraftVersionSpec = config.get("minecraftVersion");
        String elysiumVersionSpec = config.get("elysiumVersion");
        String license = config.get("license");

        List<ModDependency> modDependencies = new ArrayList<>();
        List<Config> modDepsList = config.get("mod_dependencies");
        if (modDepsList != null) {
            for (Config entry : modDepsList) {
                String modId = entry.get("modId");
                String type = entry.get("type");
                String versionRangeStr = entry.get("versionRange");
                boolean required = "required".equalsIgnoreCase(type);
                VersionRange range = TartarusLoader.parseVersionRange(versionRangeStr);
                modDependencies.add(new ModDependency(modId, required, range));
            }
        }

        List<ScriptDependency> scriptDependencies = new ArrayList<>();
        List<Config> scriptDepsList = config.get("script_dependencies");
        if (scriptDepsList != null) {
            for (Config entry : scriptDepsList) {
                String scriptIdDep = entry.get("scriptId");
                String type = entry.get("type");
                String versionRangeStr = entry.get("versionRange");
                boolean required = "required".equalsIgnoreCase(type);
                VersionRange range = TartarusLoader.parseVersionRange(versionRangeStr);
                scriptDependencies.add(new ScriptDependency(scriptIdDep, required, range));
            }
        }

        return new TartarusPackInfo(
                scriptId, version, displayName, authors, description,
                minecraftVersionSpec, elysiumVersionSpec, license,
                modDependencies, scriptDependencies, packPath, isZip
        );
    }

    public record ModDependency(String modId, boolean required, VersionRange versionRange) {
    }

    public record ScriptDependency(String scriptId, boolean required, VersionRange versionRange) {
    }
}