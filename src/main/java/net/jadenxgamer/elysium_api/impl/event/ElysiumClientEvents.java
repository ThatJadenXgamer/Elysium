package net.jadenxgamer.elysium_api.impl.event;

import com.mojang.brigadier.CommandDispatcher;
import net.jadenxgamer.elysium_api.ElysiumAPI;
import net.jadenxgamer.elysium_api.api.client.screen_flash.ScreenFlash;
import net.jadenxgamer.elysium_api.impl.client.commands.DodgeRollCommand;
import net.jadenxgamer.elysium_api.impl.registry.ElysiumItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.FogType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = ElysiumAPI.MOD_ID, value = Dist.CLIENT)
public class ElysiumClientEvents {

    @SubscribeEvent
    public static void onClientTickPre(ClientTickEvent.Pre event) {

    }

    @SubscribeEvent
    public static void onClientTickPost(ClientTickEvent.Post event) {

    }

    @SubscribeEvent
    public static void onClientCommandsRegister(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        DodgeRollCommand.register(dispatcher);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void fogRender(ViewportEvent.RenderFog event) {
        if (event.getCamera().getFluidInCamera() == FogType.NONE && event.getMode() == FogRenderer.FogMode.FOG_TERRAIN && (event.getCamera().getEntity().getEyeInFluidType() == NeoForgeMod.EMPTY_TYPE.value())) {
            var settings = ElysiumAPI.FOG_SETTINGS.getSettings(Minecraft.getInstance().player, event.getNearPlaneDistance(), event.getFarPlaneDistance());
            if (settings != null) {
                event.setCanceled(true);
                event.setNearPlaneDistance(settings.getLeft());
                event.setFarPlaneDistance(settings.getRight());
            }
        }
    }

    @SubscribeEvent
    public static void addToExistingTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.hasPermissions() && event.getTabKey() == CreativeModeTabs.OP_BLOCKS) {
            event.insertAfter(Items.DEBUG_STICK.getDefaultInstance(), ElysiumItems.PANORAMA_CAMERA.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(Items.JIGSAW.getDefaultInstance(), ElysiumItems.STRUCTURE_STAMP_ANCHOR.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Pre event) {
        ScreenFlash.handle(event.getGuiGraphics(), event.getPartialTick().getGameTimeDeltaPartialTick(false));
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {

    }
}
