package net.jadenxgamer.elysium_api.tartarus_scripting.callers;

import net.jadenxgamer.elysium_api.tartarus_scripting.scripting.ScriptSource;
import net.jadenxgamer.elysium_api.tartarus_scripting.util.CurrentScriptSource;
import org.mozilla.javascript.*;

import java.io.Reader;

public class ImportModuleFunction implements Callable {
    @Override
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (args.length == 0 || !(args[0] instanceof String path))
            throw new RuntimeException("importModule requires a path string");

        ScriptSource source = CurrentScriptSource.get();
        if (source == null)
            throw new RuntimeException("importModule called outside of a pack context");

        try (Reader reader = source.getScriptReader(path)) {
            Scriptable moduleScope = cx.newObject(scope);
            moduleScope.setPrototype(scope);
            moduleScope.setParentScope(null);
            Scriptable moduleObj = cx.newObject(moduleScope);
            Scriptable exports = cx.newObject(moduleScope);
            moduleObj.put("exports", moduleObj, exports);
            moduleScope.put("module", moduleScope, moduleObj);
            cx.evaluateReader(moduleScope, reader, path, 1, null);
            return moduleScope.get("module", moduleScope);
        } catch (Exception e) {
            throw new RuntimeException("Failed to import module: " + path, e);
        }
    }
}