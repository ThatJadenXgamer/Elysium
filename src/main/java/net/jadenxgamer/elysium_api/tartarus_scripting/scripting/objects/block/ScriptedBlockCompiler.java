package net.jadenxgamer.elysium_api.tartarus_scripting.scripting.objects.block;

import net.jadenxgamer.elysium_api.tartarus_scripting.TartarusScripting;
import net.jadenxgamer.elysium_api.tartarus_scripting.scripting.ScriptSource;
import org.mozilla.javascript.*;

import java.io.Reader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Compiles a JavaScript script into a ScriptedBlockBehavior proxy.
 * The proxy delegates all method calls to the script's exported object.
 */
public final class ScriptedBlockCompiler {

    public static ScriptedBlockBehavior wrap(Scriptable behaviorObj) {
        return (ScriptedBlockBehavior) Proxy.newProxyInstance(
                ScriptedBlockBehavior.class.getClassLoader(),
                new Class<?>[]{ScriptedBlockBehavior.class},
                new JSBehaviorInvocationHandler(behaviorObj)
        );
    }

    public static ScriptedBlockBehavior compile(String scriptId, Reader script, ScriptSource source) {
        Context cx = Context.enter();
        try {
            cx.setInterpretedMode(true);
            Scriptable scope = cx.initStandardObjects();
            Scriptable module = cx.newObject(scope);
            Scriptable exports = cx.newObject(scope);

            ScriptableObject.putProperty(module, "exports", exports);
            ScriptableObject.putProperty(scope, "module", module);
            ScriptableObject.putProperty(scope, "exports", exports);

            Script compiledScript = cx.compileReader(script, scriptId, 1, null);
            compiledScript.exec(cx, scope);

            Object exportedObj = scope.get("exports", scope);
            if (exportedObj == Scriptable.NOT_FOUND || exportedObj == exports) {
                exportedObj = scope.get("module", scope);
                if (exportedObj instanceof Scriptable) {
                    Object moduleExports = ((Scriptable) exportedObj).get("exports", (Scriptable) exportedObj);
                    if (moduleExports != Scriptable.NOT_FOUND) {
                        exportedObj = moduleExports;
                    }
                }
            }

            if (!(exportedObj instanceof Scriptable behaviorObj)) {
                throw new RuntimeException("Script must export an object in script: " + scriptId);
            }

            return wrap(behaviorObj);
        } catch (Exception e) {
            TartarusScripting.LOGGER.error("Failed to compile script: {}", scriptId, e);
            throw new RuntimeException("Compilation failed for script: " + scriptId, e);
        } finally {
            Context.exit();
        }
    }

    private record JSBehaviorInvocationHandler(Scriptable behaviorObj) implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object fn = ScriptableObject.getProperty(behaviorObj, method.getName());

            if (fn instanceof Callable jsCallable) {
                Context cx = Context.enter();
                try {
                    Object[] jsArgs = args != null ? args : new Object[0];
                    Object result = jsCallable.call(cx, behaviorObj, behaviorObj, jsArgs);
                    if (result instanceof NativeJavaObject njo) {
                        result = njo.unwrap();
                    }
                    return result;
                } finally {
                    Context.exit();
                }
            }

            if (method.isDefault()) {
                return InvocationHandler.invokeDefault(proxy, method, args);
            }

            if (method.getDeclaringClass() == Object.class) {
                return method.invoke(this, args);
            }

            throw new UnsupportedOperationException("Method '" + method.getName() + "' is not implemented in the script.");
        }
    }
}