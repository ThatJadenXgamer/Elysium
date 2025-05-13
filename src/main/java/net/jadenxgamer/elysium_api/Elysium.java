package net.jadenxgamer.elysium_api;

import com.mojang.logging.LogUtils;
import net.jadenxgamer.elysium_api.impl.biome.ElysiumBiomeHelper;
import net.jadenxgamer.elysium_api.impl.biome.ElysiumBiomeSource;
import net.jadenxgamer.elysium_api.impl.biome_replacer.BiomeReplacerDataDriven;
import net.jadenxgamer.elysium_api.impl.compat.ElysiumTerrablenderHelper;
import net.jadenxgamer.elysium_api.impl.registry.ElysiumFeature;
import net.jadenxgamer.elysium_api.impl.registry.ElysiumRegistries;
import net.jadenxgamer.elysium_api.impl.surface_rules.ElysiumSurfaceRulesManager;
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
    public static int elysiumSurfaceRuleNumber = 0;

    public Elysium() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(ElysiumRegistries::datapackRegistry);
        ElysiumFeature.init(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static void addDataDrivenPossibleBiomes() {
        Registry<BiomeReplacerDataDriven> biomeReplacer = Elysium.registryAccess.registryOrThrow(ElysiumRegistries.BIOME_REPLACER);

        biomeReplacer.stream().forEach(replacer -> {
            if (replacer.dimension().equals(new ResourceLocation("minecraft", "overworld"))) {
                overworldPossibleBiomes.add(replacer.withBiome());
            }
            else if (replacer.dimension().equals(new ResourceLocation("minecraft", "the_nether"))) {
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
