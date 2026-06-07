package net.jadenxgamer.elysium_api.tartarus_scripting.util;

import net.jadenxgamer.elysium_api.tartarus_scripting.scripting.ScriptSource;

public final class CurrentScriptSource {
    private static final ThreadLocal<ScriptSource> CURRENT = new ThreadLocal<>();

    public static void set(ScriptSource source) {
        CURRENT.set(source);
    }

    public static ScriptSource get() {
        return CURRENT.get();
    }

    public static void clear() {
        CURRENT.remove();
    }
}