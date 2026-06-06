package net.jadenxgamer.elysium_api.tartarus_scripting.scripting;

import net.jadenxgamer.elysium_api.tartarus_scripting.impl.plugin.TartarusPluginLoader;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaClass;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.util.HashSet;
import java.util.Set;

public class TartarusScriptManager {
    private static Scriptable globalScope;
    private static final TartarusRegistryBridge TARTARUS_REGISTRY = new TartarusRegistryBridge();
    private static final Set<String> ALLOWED_CLASSES = new HashSet<>();

    static {
        ALLOWED_CLASSES.add(TartarusRegistryBridge.class.getName());
    }

    public static void initializeEngine() {
        Context cx = Context.enter();
        try {
            cx.setInterpretedMode(true);
            cx.setClassShutter(ALLOWED_CLASSES::contains);
            globalScope = cx.initSafeStandardObjects();
            deleteJavaPackages(globalScope);
        } finally {
            Context.exit();
        }
    }

    public static void registerPlugins() {
        TartarusPluginLoader.registerAllPlugins();
    }

    public static void exposeClass(String jsName, Class<?> clazz) {
        if (globalScope == null) throw new IllegalStateException("Tartarus engine not initialized");

        ALLOWED_CLASSES.add(clazz.getName());
        if (clazz.getCanonicalName() != null) ALLOWED_CLASSES.add(clazz.getCanonicalName());

        Context cx = Context.enter();
        try {
            ScriptableObject.putProperty(globalScope, jsName, new NativeJavaClass(globalScope, clazz));
        } finally {
            Context.exit();
        }
    }

    public static void exposeObject(String name, Object obj) {
        if (globalScope == null) throw new IllegalStateException("Tartarus engine not initialized");

        Class<?> objClass = obj.getClass();
        ALLOWED_CLASSES.add(objClass.getName());
        if (objClass.getCanonicalName() != null) ALLOWED_CLASSES.add(objClass.getCanonicalName());

        Context cx = Context.enter();
        try {
            ScriptableObject.putProperty(globalScope, name, Context.javaToJS(obj, globalScope));
        } finally {
            Context.exit();
        }
    }

    private static void deleteJavaPackages(Scriptable scope) {
        String[] dangerous = { "Java", "Packages", "java", "javax", "org", "com", "edu", "net" };
        for (String prop : dangerous) {
            ScriptableObject.deleteProperty(scope, prop);
        }
    }

    public static Scriptable getGlobalScope() {
        return globalScope;
    }

    public static TartarusRegistryBridge getTartarusRegistry() {
        return TARTARUS_REGISTRY;
    }
}