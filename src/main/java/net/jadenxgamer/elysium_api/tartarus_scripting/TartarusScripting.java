package net.jadenxgamer.elysium_api.tartarus_scripting;

import net.jadenxgamer.elysium_api.tartarus_scripting.impl.pack.TartarusLoader;
import net.jadenxgamer.elysium_api.tartarus_scripting.scripting.TartarusScriptManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TartarusScripting {

    public static final Logger LOGGER = LoggerFactory.getLogger("TartarusScripting");

    public static void init() {
        TartarusScriptManager.initializeEngine();
        TartarusScriptManager.registerPlugins();
        TartarusLoader.loadAllPacks();
    }
}
