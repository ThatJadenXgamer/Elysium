package net.jadenxgamer.elysium_api.api.feature;

import net.jadenxgamer.elysium_api.Elysium;
import net.minecraft.resources.ResourceLocation;

public class Feature {

    private final ResourceLocation id;
    private boolean enabled;

    public Feature(ResourceLocation id) {
        this.id = id;
    }

    public void enable() {
        if (!enabled) {
            Elysium.LOGGER.info("Enabling feature {}...", id);
        }
        enabled = true;
    }

    public boolean isEnabled() {
        return enabled;
    }

}
