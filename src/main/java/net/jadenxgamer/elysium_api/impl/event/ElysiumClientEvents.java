package net.jadenxgamer.elysium_api.impl.event;

import net.jadenxgamer.elysium_api.Elysium;
import net.jadenxgamer.elysium_api.api.client.screen_flash.ScreenFlash;
import net.jadenxgamer.elysium_api.impl.client.fog.FogManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FogType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = Elysium.MOD_ID, value = Dist.CLIENT)
public class ElysiumClientEvents {

    @SubscribeEvent
    public static void onClientTickPre(ClientTickEvent.Pre event) {

    }

    @SubscribeEvent
    public static void onClientTickPost(ClientTickEvent.Post event) {
        Minecraft client = Minecraft.getInstance();
        Player player = client.player;
        if (player != null) FogManager.tick(player);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void fogRender(ViewportEvent.RenderFog event) {
        if (event.getCamera().getFluidInCamera() == FogType.NONE && FogManager.canRenderCustomFog()) {
            FogManager.applyFog(event.getRenderer().getRenderDistance());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void fogColor(ViewportEvent.ComputeFogColor event) {
        if (FogManager.canRenderCaveFog() && FogManager.canRenderCustomFog()) {
            FogManager.applyFogColor(event);
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
