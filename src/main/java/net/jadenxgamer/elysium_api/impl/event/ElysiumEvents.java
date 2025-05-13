package net.jadenxgamer.elysium_api.impl.event;

import net.jadenxgamer.elysium_api.Elysium;
import net.jadenxgamer.elysium_api.impl.biome.ElysiumBiomeHelper;
import net.jadenxgamer.elysium_api.impl.biome.ElysiumBiomeSource;
import net.jadenxgamer.elysium_api.impl.compat.ElysiumTerrablenderHelper;
import net.jadenxgamer.elysium_api.impl.surface_rules.ElysiumSurfaceRulesManager;
import net.jadenxgamer.elysium_api.impl.use_behavior.UseBehaviorImpl;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.loading.FMLLoader;

import java.util.Optional;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = Elysium.MOD_ID)
public class ElysiumEvents {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onServerAboutToStart(ServerAboutToStartEvent event) {
        Elysium.registryAccess = event.getServer().registryAccess();

        //ElysiumBiomeRegistry.replaceNetherBiome(Biomes.SOUL_SAND_VALLEY, Biomes.BADLANDS, 0.5, 128, new ResourceLocation(Elysium.MOD_ID, "example"), Elysium.registryAccess); // example of how you can use BiomeReplacer
        //ElysiumBiomeRegistry.replaceNetherBiome(Biomes.BADLANDS, Biomes.DESERT, 0.5, 24, new ResourceLocation(Elysium.MOD_ID, "replace_replaced_example"), registryAccess); // and yes, you can replace already replaced biomes too

        Elysium.addDataDrivenPossibleBiomes();
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
    public static void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        UseBehaviorImpl.init(event);
    }

    @Mod.EventBusSubscriber(modid = Elysium.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBusEvents {

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void commonSetup(final FMLCommonSetupEvent event) {
            event.enqueueWork(() -> {
                if (FMLLoader.getLoadingModList().getModFileById("terrablender") != null) {
                    ElysiumTerrablenderHelper.addSurfaceRules();
                }
            });
        }
    }
}
