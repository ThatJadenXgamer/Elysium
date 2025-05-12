package net.jadenxgamer.elysium_api;

import com.mojang.logging.LogUtils;
import net.jadenxgamer.elysium_api.impl.biome.ElysiumBiomeHelper;
import net.jadenxgamer.elysium_api.impl.biome.ElysiumBiomeSource;
import net.jadenxgamer.elysium_api.impl.biome_replacer.BiomeReplacerDataDriven;
import net.jadenxgamer.elysium_api.impl.registry.ElysiumFeature;
import net.jadenxgamer.elysium_api.impl.registry.ElysiumRegistries;
import net.jadenxgamer.elysium_api.impl.surface_rules.ElysiumNoiseGeneratorSettings;
import net.jadenxgamer.elysium_api.impl.use_behavior.UseBehaviorImpl;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.Optional;

import static net.jadenxgamer.elysium_api.impl.biome.ElysiumBiomeHelper.netherPossibleBiomes;
import static net.jadenxgamer.elysium_api.impl.biome.ElysiumBiomeHelper.overworldPossibleBiomes;

@Mod(Elysium.MOD_ID)
public class Elysium {
    public static final String MOD_ID = "elysium_api";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static RegistryAccess registryAccess;

    public Elysium() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(ElysiumRegistries::datapackRegistry);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(this::rightClickBlock);
        ElysiumFeature.init(modEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        //event.enqueueWork(() -> SurfaceRulesRegistry.registerNetherSurfaceRule(JNESurfaceRules.init())); // register surface rules like so
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onServerAboutToStart(ServerAboutToStartEvent event) {
        registryAccess = event.getServer().registryAccess();

        //ElysiumBiomeRegistry.replaceNetherBiome(Biomes.SOUL_SAND_VALLEY, Biomes.BADLANDS, 0.5, 128, new ResourceLocation(Elysium.MOD_ID, "example"), registryAccess); // example of how you can use BiomeReplacer
        //ElysiumBiomeRegistry.replaceNetherBiome(Biomes.BADLANDS, Biomes.DESERT, 0.5, 24, new ResourceLocation(Elysium.MOD_ID, "replace_replaced_example"), registryAccess); // and yes, you can replace already replaced biomes too

        addDataDrivenPossibleBiomes();
        Registry<LevelStem> levelStems = registryAccess.registryOrThrow(Registries.LEVEL_STEM);
        for (LevelStem dimension : levelStems.stream().toList()) {
            ChunkGenerator generator = dimension.generator();
            Optional<ResourceKey<LevelStem>> dimensionKey = levelStems.getResourceKey(dimension);
            if (dimensionKey.isEmpty()) continue;

            if (dimension.generator().getBiomeSource() instanceof ElysiumBiomeSource biomeSource) {
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

            if (generator instanceof NoiseBasedChunkGenerator noiseGenerator) {
                ElysiumNoiseGeneratorSettings elysiumSettings = ElysiumNoiseGeneratorSettings.class.cast(noiseGenerator.generatorSettings().get());
                if (dimensionKey.get().equals(LevelStem.OVERWORLD)) {
                    elysiumSettings.initElysiumSurfaceRules(LevelStem.OVERWORLD);
                }
                else if (dimensionKey.get().equals(LevelStem.NETHER)) {
                    elysiumSettings.initElysiumSurfaceRules(LevelStem.NETHER);
                }
                else if (dimensionKey.get().equals(LevelStem.END)) {
                    elysiumSettings.initElysiumSurfaceRules(LevelStem.END);
                }
            }
        }
    }

    private void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        UseBehaviorImpl.init(event);
    }

    private static void addDataDrivenPossibleBiomes() {
        Registry<BiomeReplacerDataDriven> biomeReplacer = Elysium.registryAccess.registryOrThrow(ElysiumRegistries.BIOME_REPLACER);

        biomeReplacer.stream().forEach(replacer -> {
            if (replacer.dimension().equals(new ResourceLocation("minecraft", "overworld"))) {
                overworldPossibleBiomes.add(replacer.withBiome());
            } else if (replacer.dimension().equals(new ResourceLocation("minecraft", "the_nether"))) {
                netherPossibleBiomes.add(replacer.withBiome());
            }
        });
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
        }
    }
}
