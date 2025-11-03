package net.jadenxgamer.elysium_api;

import net.jadenxgamer.elysium_api.impl.client.animation.AnimationControllers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = Elysium.MOD_ID, dist = {Dist.CLIENT})
public class ElysiumClient {

    public ElysiumClient(IEventBus eventBus, ModContainer container) {
        AnimationControllers.boostrap();
    }

}
