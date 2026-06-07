package net.jadenxgamer.elysium_api.tartarus_scripting.callers;

import net.jadenxgamer.elysium_api.tartarus_scripting.scripting.ScriptSource;
import net.jadenxgamer.elysium_api.tartarus_scripting.util.CurrentScriptSource;
import org.mozilla.javascript.*;

import java.io.Reader;

public class LoadScriptFunction implements Callable {
    @Override
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (args.length == 0 || !(args[0] instanceof String path))
            throw new RuntimeException("loadScript requires a path string");

        ScriptSource source = CurrentScriptSource.get();
        if (source == null)
            throw new RuntimeException("loadScript called outside of a pack context");

        try (Reader reader = source.getScriptReader(path)) {
            Scriptable global = ScriptableObject.getTopLevelScope(scope);
            Scriptable isolated = cx.newObject(global);
            isolated.setPrototype(global);
            isolated.setParentScope(null);
            cx.evaluateReader(isolated, reader, path, 1, null);
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load script: " + path, e);
        }
    }
}