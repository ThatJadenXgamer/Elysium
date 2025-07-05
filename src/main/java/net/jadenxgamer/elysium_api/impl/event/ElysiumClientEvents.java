package net.jadenxgamer.elysium_api.impl.event;

import net.jadenxgamer.elysium_api.Elysium;
import net.jadenxgamer.elysium_api.api.client.screen_flash.ScreenFlash;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = Elysium.MOD_ID, value = Dist.CLIENT)
public class ElysiumClientEvents {

    @SubscribeEvent
    public static void onClientTickPre(ClientTickEvent.Pre event) {

    }

    @SubscribeEvent
    public static void onClientTickPost(ClientTickEvent.Post event) {

    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Pre event) {
        ScreenFlash.handle(event.getGuiGraphics(), event.getPartialTick().getGameTimeDeltaPartialTick(false));
    }

    @EventBusSubscriber(modid = Elysium.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModBusClientEvents {

        @SubscribeEvent
        public static void clientSetup(FMLClientSetupEvent event) {

        }
    }
}
