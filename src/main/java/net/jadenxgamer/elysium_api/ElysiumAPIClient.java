package net.jadenxgamer.elysium_api;

import net.jadenxgamer.elysium_api.impl.client.animation.AnimationLayers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = ElysiumAPI.MOD_ID, dist = {Dist.CLIENT})
public class ElysiumAPIClient {

    public ElysiumAPIClient(IEventBus eventBus, ModContainer container) {
        AnimationLayers.boostrap();
    }
}
