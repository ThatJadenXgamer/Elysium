package net.jadenxgamer.elysium_api;

import com.mojang.logging.LogUtils;
import net.jadenxgamer.elysium_api.api.biome.ElysiumBiomeRegistry;
import net.jadenxgamer.elysium_api.api.surface_rules.SurfaceRulesRegistry;
import net.jadenxgamer.elysium_api.impl.ElysiumRegistries;
import net.jadenxgamer.elysium_api.impl.biome.ElysiumBiomeHelper;
import net.jadenxgamer.elysium_api.impl.biome.ElysiumBiomeSource;
import net.jadenxgamer.elysium_api.impl.use_behavior.UseBehaviorImpl;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.dimension.LevelStem;
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
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onServerAboutToStart(ServerAboutToStartEvent event) {
        registryAccess = event.getServer().registryAccess();

//        ElysiumBiomeRegistry.replaceNetherBiome(Biomes.BADLANDS, Biomes.SOUL_SAND_VALLEY, 0.5, 16, registryAccess); // example of how you use BiomeReplacer
//        ElysiumBiomeRegistry.replaceNetherBiome(Biomes.DESERT, Biomes.BADLANDS, 0.5, 24, registryAccess); // and yes, you can replace already replaced biomes lmfao

        Registry<LevelStem> levelStems = registryAccess.registryOrThrow(Registries.LEVEL_STEM);
        for (ResourceKey<LevelStem> dimension : levelStems.registryKeySet()) {
            Optional<Holder.Reference<LevelStem>> optionalLevelStem = levelStems.getHolder(dimension);
            if (optionalLevelStem.isPresent() && optionalLevelStem.get().value().generator().getBiomeSource() instanceof ElysiumBiomeSource biomeSource) {
                if (dimension.equals(LevelStem.OVERWORLD)) {
                    // initializes BiomeReplacer for the Overworld
                    biomeSource.setDimension(LevelStem.OVERWORLD);
                    biomeSource.addPossibleBiomes(ElysiumBiomeHelper.overworldPossibleBiomes);
                }
                else if (dimension.equals(LevelStem.NETHER)) {
                    // initializes BiomeReplacer for the Nether
                    biomeSource.setDimension(LevelStem.NETHER);
                    biomeSource.addPossibleBiomes(ElysiumBiomeHelper.netherPossibleBiomes);
                }
                //TODO: End Biomes
            }
        }
    }

    private void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        UseBehaviorImpl.init(event);
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
        }
    }
}
