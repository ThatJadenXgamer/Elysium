package net.jadenxgamer.elysium_api.impl.event;

import net.jadenxgamer.elysium_api.Elysium;
import net.jadenxgamer.elysium_api.api.client.screen_flash.ScreenFlash;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.level.material.FogType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.common.NeoForgeMod;

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
    public static void fogRender(ViewportEvent.RenderFog event) {
        if (event.getCamera().getFluidInCamera() == FogType.NONE && event.getMode() == FogRenderer.FogMode.FOG_TERRAIN && (event.getCamera().getEntity().getEyeInFluidType() == NeoForgeMod.EMPTY_TYPE.value())) {
            var settings = Elysium.FOG_SETTINGS.getSettings(Minecraft.getInstance().player, event.getNearPlaneDistance(), event.getFarPlaneDistance());
            if (settings != null) {
                event.setCanceled(true);
                event.setNearPlaneDistance(settings.getLeft());
                event.setFarPlaneDistance(settings.getRight());
            }
        }
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
