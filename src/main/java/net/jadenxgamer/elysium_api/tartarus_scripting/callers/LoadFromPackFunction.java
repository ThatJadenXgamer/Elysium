package net.jadenxgamer.elysium_api.tartarus_scripting.callers;

import net.jadenxgamer.elysium_api.tartarus_scripting.scripting.ScriptSource;
import net.jadenxgamer.elysium_api.tartarus_scripting.impl.pack.TartarusLoader;
import net.jadenxgamer.elysium_api.tartarus_scripting.scripting.TartarusScriptManager;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import java.io.Reader;

public record LoadFromPackFunction() implements Callable {
    private static final int LINE_NUMBER = 1;

    @Override
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (args.length < 2 || !(args[0] instanceof String scriptId) || !(args[1] instanceof String path))
            throw new RuntimeException("loadFromPack requires two arguments: scriptId (string) and path (string)");
        ScriptSource source = TartarusLoader.getScriptSource(scriptId);
        if (source == null) throw new RuntimeException("No loaded pack found with scriptId: " + scriptId);

        try (Reader reader = source.getScriptReader(path)) {
            Scriptable global = TartarusScriptManager.getGlobalScope();
            Scriptable isolated = cx.newObject(global);
            isolated.setPrototype(global);
            isolated.setParentScope(null);
            return cx.evaluateReader(isolated, reader, scriptId + "/" + path, LINE_NUMBER, null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load script from pack " + scriptId + ": " + path, e);
        }
    }
}