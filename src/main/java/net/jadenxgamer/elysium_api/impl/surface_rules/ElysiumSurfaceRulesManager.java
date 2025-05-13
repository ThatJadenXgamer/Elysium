package net.jadenxgamer.elysium_api.impl.surface_rules;

import net.jadenxgamer.elysium_api.impl.mixin.biome.NoiseGeneratorSettingsAccessor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraftforge.fml.loading.FMLLoader;

import java.util.ArrayList;
import java.util.List;

public class ElysiumSurfaceRulesManager {
    public static final List<SurfaceRules.RuleSource> GLOBAL_SURFACE_RULES = new ArrayList<>();
    public static final List<SurfaceRules.RuleSource> OVERWORLD_SURFACE_RULES = new ArrayList<>();
    public static final List<SurfaceRules.RuleSource> NETHER_SURFACE_RULES = new ArrayList<>();
    public static final List<SurfaceRules.RuleSource> END_SURFACE_RULES = new ArrayList<>();

    public static SurfaceRules.RuleSource getForMergingRules(List<SurfaceRules.RuleSource> dimensionRules, SurfaceRules.RuleSource originalRules) {
        if (dimensionRules.isEmpty() && GLOBAL_SURFACE_RULES.isEmpty()) return null;

        List<SurfaceRules.RuleSource> combinedRules = new ArrayList<>();
        combinedRules.addAll(dimensionRules);
        combinedRules.addAll(GLOBAL_SURFACE_RULES);
        combinedRules.add(originalRules);
        return SurfaceRules.sequence(combinedRules.toArray(SurfaceRules.RuleSource[]::new));
    }

    public static SurfaceRules.RuleSource getForTerrablenderRules(List<SurfaceRules.RuleSource> dimensionRules) {
        if (dimensionRules.isEmpty() && GLOBAL_SURFACE_RULES.isEmpty()) return null;

        List<SurfaceRules.RuleSource> combinedRules = new ArrayList<>();
        combinedRules.addAll(dimensionRules);
        combinedRules.addAll(GLOBAL_SURFACE_RULES);
        return SurfaceRules.sequence(combinedRules.toArray(SurfaceRules.RuleSource[]::new));
    }

    public static void handleSurfaceRules(ResourceKey<LevelStem> dimension, NoiseBasedChunkGenerator noiseGenerator) {
        if (FMLLoader.getLoadingModList().getModFileById("terrablender") != null) return;

        SurfaceRules.RuleSource newRules = null;
        SurfaceRules.RuleSource originalRules = noiseGenerator.settings.get().surfaceRule();

        if (dimension.equals(LevelStem.OVERWORLD)) {
            newRules = ElysiumSurfaceRulesManager.getForMergingRules(ElysiumSurfaceRulesManager.OVERWORLD_SURFACE_RULES, originalRules);
        }
        else if (dimension.equals(LevelStem.NETHER)) {
            newRules = ElysiumSurfaceRulesManager.getForMergingRules(ElysiumSurfaceRulesManager.NETHER_SURFACE_RULES, originalRules);
        }
        else if (dimension.equals(LevelStem.END)) {
            newRules = ElysiumSurfaceRulesManager.getForMergingRules(ElysiumSurfaceRulesManager.END_SURFACE_RULES, originalRules);
        }

        if (newRules != null) {
            ((NoiseGeneratorSettingsAccessor) (Object) noiseGenerator.settings.get()).elysium$setSurfaceRule(newRules);
        }
    }
}
