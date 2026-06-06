package net.jadenxgamer.elysium_api.tartarus_scripting.api.plugin;

import java.lang.annotation.*;

/**
 * This annotation lets TartarusScripting detect mod plugins.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TartarusPlugin {
}