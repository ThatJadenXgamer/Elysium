package net.jadenxgamer.elysium_api.tartarus_scripting.scripting;

import java.io.IOException;
import java.io.Reader;

public interface ScriptSource {
    Reader getScriptReader(String relativePath) throws IOException;
}