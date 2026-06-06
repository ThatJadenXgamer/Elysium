package net.jadenxgamer.elysium_api.tartarus_scripting.impl.plugin;

import net.jadenxgamer.elysium_api.tartarus_scripting.scripting.TartarusScriptManager;
import net.jadenxgamer.elysium_api.tartarus_scripting.api.plugin.TartarusExposure;
import net.jadenxgamer.elysium_api.tartarus_scripting.api.plugin.TartarusPlugin;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.moddiscovery.ModFileInfo;
import net.neoforged.neoforgespi.language.IModInfo;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import java.lang.reflect.Constructor;
import java.util.Collection;

import static net.jadenxgamer.elysium_api.tartarus_scripting.TartarusScripting.LOGGER;

public final class TartarusPluginLoader {
    private static final Type PLUGIN_ANNOTATION = Type.getType(TartarusPlugin.class);
    private static final ExposureApiImpl API_IMPL = new ExposureApiImpl();

    private TartarusPluginLoader() {}

    public static void registerAllPlugins() {
        if (TartarusScriptManager.getGlobalScope() == null) {
            LOGGER.warn("Tartarus scripting engine not initialized, cannot load plugins");
            return;
        }

        for (ModFileInfo fileInfo : FMLLoader.getLoadingModList().getModFiles()) {
            Collection<ModFileScanData.AnnotationData> annotations = fileInfo.getFile().getScanResult().getAnnotations();
            if (annotations.isEmpty()) continue;

            String modId = fileInfo.getMods().stream()
                    .findFirst()
                    .map(IModInfo::getModId)
                    .orElse("unknown");

            for (ModFileScanData.AnnotationData annotation : annotations) {
                if (!PLUGIN_ANNOTATION.equals(annotation.annotationType())) continue;

                String className = annotation.clazz().getClassName();
                try {
                    Class<?> pluginClass = Class.forName(className);
                    if (!TartarusExposure.class.isAssignableFrom(pluginClass)) {
                        LOGGER.error("Plugin class {} does not implement TartarusExposure", className);
                        continue;
                    }

                    Constructor<?> ctor = pluginClass.getDeclaredConstructor();
                    ctor.setAccessible(true);
                    TartarusExposure plugin = (TartarusExposure) ctor.newInstance();
                    plugin.registerExposures(API_IMPL);

                    LOGGER.info("Registered Tartarus plugin {} from mod {}", className, modId);
                } catch (Exception e) {
                    LOGGER.error("Failed to load Tartarus plugin class {} from mod {}", className, modId, e);
                }
            }
        }
    }

    private static class ExposureApiImpl implements TartarusExposure {
        @Override
        public void exposeClass(String jsName, Class<?> clazz) {
            TartarusScriptManager.exposeClass(jsName, clazz);
        }

        @Override
        public void exposeObject(String name, Object object) {
            TartarusScriptManager.exposeObject(name, object);
        }

        @Override
        public void registerExposures(TartarusExposure tartarus) {
            throw new UnsupportedOperationException("Internal API should not be used as a plugin");
        }
    }
}