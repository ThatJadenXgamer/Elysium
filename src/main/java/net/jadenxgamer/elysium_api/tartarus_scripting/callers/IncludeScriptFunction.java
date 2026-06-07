package net.jadenxgamer.elysium_api.tartarus_scripting.callers;

import net.jadenxgamer.elysium_api.tartarus_scripting.scripting.ScriptSource;
import net.jadenxgamer.elysium_api.tartarus_scripting.util.CurrentScriptSource;
import org.mozilla.javascript.*;

import java.io.Reader;

public class IncludeScriptFunction implements Callable {
    @Override
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (args.length == 0 || !(args[0] instanceof String path))
            return null;

        ScriptSource source = CurrentScriptSource.get();
        if (source == null)
            throw new RuntimeException("includeScript called outside of a pack context");

        try (Reader reader = source.getScriptReader(path)) {
            return cx.evaluateReader(scope, reader, path, 1, null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to include script: " + path, e);
        }
    }
}