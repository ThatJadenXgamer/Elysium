package net.jadenxgamer.elysium_api.tartarus_scripting.callers;

import net.jadenxgamer.elysium_api.tartarus_scripting.scripting.ScriptSource;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import java.io.Reader;

public record IncludeScriptFunction(ScriptSource source) implements Callable {
    private static final int LINE_NUMBER = 1;

    @Override
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (args.length == 0 || !(args[0] instanceof String path)) return null;
        try (Reader reader = source.getScriptReader(path)) {
            return cx.evaluateReader(scope, reader, path, LINE_NUMBER, null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to include script: " + path, e);
        }
    }
}