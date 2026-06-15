package net.jadenxgamer.elysium_api;

import com.mojang.logging.LogUtils;
import net.jadenxgamer.elysium_api.impl.client.assetdriven.fog_settings.FogSettingsManager;
import net.jadenxgamer.elysium_api.impl.client.assetdriven.lightmap_settings.LightmapSettingsManager;
import net.jadenxgamer.elysium_api.impl.registry.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(ElysiumAPI.MOD_ID)
public final class ElysiumAPI {
    public static final String MOD_ID = "elysium_api";
    public static final Logger LOGGER = LoggerFactory.getLogger("ElysiumAPI");
    public static final FogSettingsManager FOG_SETTINGS = new FogSettingsManager();
    public static final LightmapSettingsManager LIGHTMAP_SETTINGS = new LightmapSettingsManager();

    public ElysiumAPI(IEventBus modEventBus, ModContainer modContainer) {
        ElysiumRegistries.init(modEventBus);
        ElysiumAttributes.init(modEventBus);
        ElysiumAttachmentTypes.init(modEventBus);
        ElysiumBlocks.init(modEventBus);
        ElysiumItems.init(modEventBus);
    }

    public static ResourceLocation elysiumPath(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static ResourceLocation idPath(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }
}
