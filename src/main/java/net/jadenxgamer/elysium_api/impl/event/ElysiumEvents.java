package net.jadenxgamer.elysium_api.impl.event;

import net.jadenxgamer.elysium_api.Elysium;
import net.jadenxgamer.elysium_api.impl.client.fog_settings.FogSettingsManager;
import net.jadenxgamer.elysium_api.impl.core.biome.ElysiumBiomeHelper;
import net.jadenxgamer.elysium_api.impl.core.biome.ElysiumBiomeSource;
import net.jadenxgamer.elysium_api.impl.core.datadriven.biome_replacer.BiomeReplacerDataDriven;
import net.jadenxgamer.elysium_api.impl.core.datadriven.block.use_behaviors.UseBehavior;
import net.jadenxgamer.elysium_api.impl.core.datadriven.block.use_behaviors.UseBehaviorImpl;
import net.jadenxgamer.elysium_api.impl.core.datadriven.block.BlockSoundTransformer;
import net.jadenxgamer.elysium_api.impl.core.datadriven.item.RemainderTransformer;
import net.jadenxgamer.elysium_api.impl.core.surface_rules.ElysiumSurfaceRulesManager;
import net.jadenxgamer.elysium_api.impl.registry.ElysiumRegistries;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

import java.util.Optional;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = Elysium.MOD_ID)
public class ElysiumEvents {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onServerAboutToStart(ServerAboutToStartEvent event) {
        Elysium.registryAccess = event.getServer().registryAccess();

        //ElysiumBiomeRegistry.replaceNetherBiome(Biomes.SOUL_SAND_VALLEY, Biomes.BADLANDS, 0.5, 128, new ResourceLocation(Elysium.MOD_ID, "example"), Elysium.registryAccess); // example of how you can use BiomeReplacer
        //ElysiumBiomeRegistry.replaceNetherBiome(Biomes.BADLANDS, Biomes.DESERT, 0.5, 24, new ResourceLocation(Elysium.MOD_ID, "replace_replaced_example"), registryAccess); // and yes, you can replace already replaced biomes too

        BiomeReplacerDataDriven.addDataDrivenPossibleBiomes();
        Registry<LevelStem> levelStems = Elysium.registryAccess.registryOrThrow(Registries.LEVEL_STEM);
        for (LevelStem dimension : levelStems.stream().toList()) {
            Optional<ResourceKey<LevelStem>> dimensionKey = levelStems.getResourceKey(dimension);
            if (dimensionKey.isPresent() && dimension.generator().getBiomeSource() instanceof ElysiumBiomeSource biomeSource) {
                if (dimensionKey.get().equals(LevelStem.OVERWORLD)) {
                    biomeSource.setDimension(LevelStem.OVERWORLD);
                    biomeSource.addPossibleBiomes(ElysiumBiomeHelper.overworldPossibleBiomes);
                    biomeSource.setWorldSeed(event.getServer().getWorldData().worldGenOptions().seed());
                }
                else if (dimensionKey.get().equals(LevelStem.NETHER)) {
                    biomeSource.setDimension(LevelStem.NETHER);
                    biomeSource.addPossibleBiomes(ElysiumBiomeHelper.netherPossibleBiomes);
                    biomeSource.setWorldSeed(event.getServer().getWorldData().worldGenOptions().seed());
                }
                //TODO: End Biomes
            }

            ChunkGenerator generator = dimension.generator();
            if (dimensionKey.isPresent() && generator instanceof NoiseBasedChunkGenerator noiseGenerator) {
                ElysiumSurfaceRulesManager.handleSurfaceRules(dimensionKey.get(), noiseGenerator);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Elysium.registryAccess = event.getEntity().registryAccess();
    }

    @SubscribeEvent
    public static void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        UseBehaviorImpl.init(event);
    }

    @EventBusSubscriber(modid = Elysium.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
    public static class ModBusEvents {

        @SubscribeEvent
        public static void commonSetup(final FMLCommonSetupEvent event) {

        }

        @SubscribeEvent
        public static void registerReloadListener(RegisterClientReloadListenersEvent event) {
            event.registerReloadListener(new FogSettingsManager());
        }

        @SubscribeEvent
        public static void datapackRegistry(DataPackRegistryEvent.NewRegistry event) {
            event.dataPackRegistry(ElysiumRegistries.USE_BEHAVIORS, UseBehavior.CODEC);
            event.dataPackRegistry(ElysiumRegistries.BLOCK_SOUND_TRANSFORMERS, BlockSoundTransformer.CODEC);
            event.dataPackRegistry(ElysiumRegistries.BIOME_REPLACER, BiomeReplacerDataDriven.CODEC);
            event.dataPackRegistry(ElysiumRegistries.REMAINDER_TRANSFORMERS, RemainderTransformer.CODEC);
        }
    }
}
