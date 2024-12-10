package net.jadenxgamer.elysium_api.impl.mixin;

import net.jadenxgamer.elysium_api.api.surface_rules.SurfaceRulesRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(NoiseBasedChunkGenerator.class)
public class NoiseBasedChunkGeneratorMixin {

    @Unique
    private boolean elysium$hasCustomRulesApplied = false;
    @Shadow
    public Holder<NoiseGeneratorSettings> settings;

    @Inject(
            at = @At("HEAD"),
            method = "buildSurface(Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/world/level/levelgen/WorldGenerationContext;Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/biome/BiomeManager;Lnet/minecraft/core/Registry;Lnet/minecraft/world/level/levelgen/blending/Blender;)V"
    )
    private void elysium$handleBuildSurface(ChunkAccess chunk, WorldGenerationContext context, RandomState randomState, StructureManager structureManager, BiomeManager biomeManager, Registry<Biome> biomeRegistry, Blender blender, CallbackInfo ci) {
        if (!elysium$hasCustomRulesApplied) {
            elysium$hasCustomRulesApplied = true;
            NoiseGeneratorSettings original = settings.value();
            settings = Holder.direct(new NoiseGeneratorSettings(
                    original.noiseSettings(),
                    original.defaultBlock(),
                    original.defaultFluid(),
                    original.noiseRouter(),
                    mergeSurfaceRules(settings),
                    original.spawnTarget(),
                    original.seaLevel(),
                    original.disableMobGeneration(),
                    original.aquifersEnabled(),
                    original.oreVeinsEnabled(),
                    original.useLegacyRandomSource()
            ));
        }
    }

    @Unique
    private static SurfaceRules.RuleSource mergeSurfaceRules(Holder<NoiseGeneratorSettings> generatorSettings) {
        // deals with merging surface rules with ones in registry
        if (SurfaceRulesRegistry.RULES.isEmpty()) {
            return generatorSettings.value().surfaceRule();
        }

        List<SurfaceRules.RuleSource> combinedRules = new ArrayList<>(SurfaceRulesRegistry.RULES);
        combinedRules.add(generatorSettings.value().surfaceRule());
        return SurfaceRules.sequence(combinedRules.toArray(SurfaceRules.RuleSource[]::new));
    }
}