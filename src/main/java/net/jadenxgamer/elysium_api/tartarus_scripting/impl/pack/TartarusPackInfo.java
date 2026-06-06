package net.jadenxgamer.elysium_api.tartarus_scripting.impl.pack;

import com.electronwill.nightconfig.core.Config;
import org.apache.maven.artifact.versioning.VersionRange;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public record TartarusPackInfo(String scriptId, String version, String displayName, String authors, String description,
                               String minecraftVersionSpec, VersionRange minecraftVersionRange,
                               String elysiumVersionSpec, VersionRange elysiumVersionRange,
                               String license,
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

        VersionRange minecraftRange = TartarusLoader.parseVersionRange(minecraftVersionSpec);
        VersionRange elysiumRange = TartarusLoader.parseVersionRange(elysiumVersionSpec);

        List<ModDependency> modDependencies = new ArrayList<>();
        List<Config> mdList = config.get("mod_dependencies");
        if (mdList != null) {
            for (Config entry : mdList) {
                String modId = entry.get("modId");
                String type = entry.get("type");
                String versionRangeStr = entry.get("versionRange");
                boolean required = "required".equalsIgnoreCase(type);
                VersionRange range = TartarusLoader.parseVersionRange(versionRangeStr);
                modDependencies.add(new ModDependency(modId, required, range));
            }
        }

        List<ScriptDependency> scriptDependencies = new ArrayList<>();
        List<Config> sdList = config.get("script_dependencies");
        if (sdList != null) {
            for (Config entry : sdList) {
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
                minecraftVersionSpec, minecraftRange,
                elysiumVersionSpec, elysiumRange,
                license,
                modDependencies, scriptDependencies, packPath, isZip
        );
    }

    public record ModDependency(String modId, boolean required, VersionRange versionRange) {}
    public record ScriptDependency(String scriptId, boolean required, VersionRange versionRange) {}
}