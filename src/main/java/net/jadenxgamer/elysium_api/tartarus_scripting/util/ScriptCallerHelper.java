package net.jadenxgamer.elysium_api.tartarus_scripting.util;

import net.jadenxgamer.elysium_api.tartarus_scripting.callers.IncludeScriptFunction;
import net.jadenxgamer.elysium_api.tartarus_scripting.callers.LoadScriptFunction;
import net.jadenxgamer.elysium_api.tartarus_scripting.scripting.ScriptSource;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public final class ScriptCallerHelper {
    private static final Map<String, Function<ScriptSource, ? extends org.mozilla.javascript.Callable>> CALLER_FACTORIES = new LinkedHashMap<>();

    static {
        register("includeScript", IncludeScriptFunction::new);
        register("loadScript", LoadScriptFunction::new);
    }

    private static void register(String name, Function<ScriptSource, ? extends org.mozilla.javascript.Callable> factory) {
        CALLER_FACTORIES.put(name, factory);
    }

    public static void attachStandardFunctions(Scriptable scope, ScriptSource source) {
        for (var entry : CALLER_FACTORIES.entrySet()) {
            ScriptableObject.putProperty(scope, entry.getKey(), entry.getValue().apply(source));
        }
    }
}