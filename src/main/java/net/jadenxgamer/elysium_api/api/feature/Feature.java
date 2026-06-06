package net.jadenxgamer.elysium_api.api.feature;

import net.jadenxgamer.elysium_api.ElysiumAPI;
import net.minecraft.resources.ResourceLocation;

public class Feature<T> {

    private final ResourceLocation id;
    private boolean enabled;
    private final T config;


    public Feature(ResourceLocation id, T config) {
        this.id = id;
        this.config = config;
    }

    public void enable() {
        if (!enabled) {
            ElysiumAPI.LOGGER.info("Enabling feature {}...", id);
        }
        enabled = true;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public T config() {
        return config;
    }
}
