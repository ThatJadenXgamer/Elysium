package net.jadenxgamer.elysium_api.tartarus_scripting.api.plugin;


public interface TartarusExposure {

    /**
     * Called by TartarusScripting to let the plugin register its custom Java classes/objects.
     * @param tartarus provides methods to expose classes and objects to JavaScript
     */
    void registerExposures(TartarusExposure tartarus);

    /**
     * Makes a Java class available in scripts under the given JavaScript name.
     * @param jsName name used in scripts (e.g. "MyCustomClass")
     * @param clazz the Java class to expose
     * @implSpec This method is intended to be called by the API implementation, not by plugin authors. Plugins should not override it.
     */
    default void exposeClass(String jsName, Class<?> clazz) {
        throw new UnsupportedOperationException("This method should only be called by the Tartarus API implementation");
    }

    /**
     * Places a specific Java object into the global script scope.
     * @param name variable name in scripts
     * @param object the object to expose
     * @implSpec This method is intended to be called by the API implementation, not by plugin authors. Plugins should not override it.
     */
    default void exposeObject(String name, Object object) {
        throw new UnsupportedOperationException("This method should only be called by the Tartarus API implementation");
    }
}