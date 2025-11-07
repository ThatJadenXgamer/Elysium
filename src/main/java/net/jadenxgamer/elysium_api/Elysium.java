package net.jadenxgamer.elysium_api;

import com.mojang.logging.LogUtils;
import net.jadenxgamer.elysium_api.impl.client.fog_settings.FogSettingsManager;
import net.jadenxgamer.elysium_api.impl.registry.ElysiumAttachmentTypes;
import net.jadenxgamer.elysium_api.impl.registry.ElysiumAttributes;
import net.jadenxgamer.elysium_api.impl.registry.ElysiumMiscRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(Elysium.MOD_ID)
public final class Elysium {
    public static final String MOD_ID = "elysium_api";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final FogSettingsManager FOG_SETTINGS = new FogSettingsManager();

    public Elysium(IEventBus modEventBus, ModContainer modContainer) {
        ElysiumMiscRegistries.init(modEventBus);
        ElysiumAttributes.init(modEventBus);
        ElysiumAttachmentTypes.init(modEventBus);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static ResourceLocation idPath(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }
}
