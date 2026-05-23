package net.jadenxgamer.elysium_api.impl.event;

import net.jadenxgamer.elysium_api.Elysium;
import net.jadenxgamer.elysium_api.impl.client.assetdriven.fog_settings.FogSettingsManager;
import net.jadenxgamer.elysium_api.impl.client.assetdriven.lightmap_settings.LightmapSettingsManager;
import net.jadenxgamer.elysium_api.impl.core.biome.MosaicBiomeSource;
import net.jadenxgamer.elysium_api.impl.core.datadriven.block.use_behaviors.UseBehaviorImpl;
import net.jadenxgamer.elysium_api.impl.core.datadriven.mosaic.MosaicBiomeEntry;
import net.jadenxgamer.elysium_api.impl.core.surface_rules.ElysiumSurfaceRulesManager;
import net.jadenxgamer.elysium_api.impl.networking.ElysiumPayloads;
import net.jadenxgamer.elysium_api.impl.registry.ElysiumAttachmentTypes;
import net.jadenxgamer.elysium_api.impl.registry.ElysiumAttributes;
import net.jadenxgamer.elysium_api.impl.registry.ElysiumRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = Elysium.MOD_ID)
public class ElysiumEvents {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onServerAboutToStart(ServerAboutToStartEvent event) {
        RegistryAccess registryAccess = event.getServer().registryAccess();

        Registry<LevelStem> levelStems = registryAccess.registryOrThrow(Registries.LEVEL_STEM);
        for (LevelStem dimension : levelStems.stream().toList()) {
            Optional<ResourceKey<LevelStem>> dimensionKey = levelStems.getResourceKey(dimension);

            if (dimensionKey.isPresent() && dimension.generator().getBiomeSource() instanceof MosaicBiomeSource biomeSource) {
                Set<Holder<Biome>> biomesToAdd = new HashSet<>();
                var seed = event.getServer().getWorldData().worldGenOptions().seed();
                for (MosaicBiomeEntry entry : registryAccess.registryOrThrow(ElysiumRegistries.Keys.MOSAIC_BIOME_ENTRY))
                    if (entry.dimension().equals(dimensionKey.get().location())) biomesToAdd.add(entry.biome());
                biomeSource.initialize(seed, dimensionKey.get(), biomesToAdd);
            }

            ChunkGenerator generator = dimension.generator();
            if (dimensionKey.isPresent() && generator instanceof NoiseBasedChunkGenerator noiseGenerator)
                ElysiumSurfaceRulesManager.handleSurfaceRules(dimensionKey.get(), noiseGenerator);
        }
    }

    @SubscribeEvent
    public static void tickPlayerPre(PlayerTickEvent.Pre event) {
        event.getEntity().setData(ElysiumAttachmentTypes.COOLDOWN_TICK, event.getEntity().getData(ElysiumAttachmentTypes.COOLDOWN_TICK) + 1);
    }

    @SubscribeEvent
    public static void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        UseBehaviorImpl.init(event);
    }

    public static void modifyDefaultAttributes(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, ElysiumAttributes.DODGE_POWER);
    }

    @SubscribeEvent
    public static void registerPayloads(final RegisterPayloadHandlersEvent event) {
        ElysiumPayloads.registerPayloads(event);
    }

    @SubscribeEvent
    public static void commonSetup(final FMLCommonSetupEvent event) {

    }

    @SubscribeEvent
    public static void registerReloadListener(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new FogSettingsManager());
        event.registerReloadListener(new LightmapSettingsManager());
    }

    @SubscribeEvent
    public static void datapackRegistry(DataPackRegistryEvent.NewRegistry event) {
        ElysiumRegistries.datapackInit(event);
    }
}
