package net.jadenxgamer.elysium_api.impl.surface_rules;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.ArrayList;
import java.util.List;

public class SurfaceRulesManager {
    public static final List<SurfaceRules.RuleSource> GLOBAL_SURFACE_RULES = new ArrayList<>();
    public static final List<SurfaceRules.RuleSource> OVERWORLD_SURFACE_RULES = new ArrayList<>();
    public static final List<SurfaceRules.RuleSource> NETHER_SURFACE_RULES = new ArrayList<>();
    public static final List<SurfaceRules.RuleSource> END_SURFACE_RULES = new ArrayList<>();

    public static SurfaceRules.RuleSource mergeSurfaceRules(ResourceKey<LevelStem> dimension, SurfaceRules.RuleSource original) {
        if (dimension.equals(LevelStem.OVERWORLD)) {
            return getSurfaceRules(OVERWORLD_SURFACE_RULES, original);
        }
        else if (dimension.equals(LevelStem.NETHER)) {
            return getSurfaceRules(NETHER_SURFACE_RULES, original);
        }
        else if (dimension.equals(LevelStem.END)) {
            return getSurfaceRules(END_SURFACE_RULES, original);
        }
        return null;
    }

    private static SurfaceRules.RuleSource getSurfaceRules(List<SurfaceRules.RuleSource> dimensionRules, SurfaceRules.RuleSource originalRules) {
        if (dimensionRules.isEmpty() && GLOBAL_SURFACE_RULES.isEmpty()) return null;

        List<SurfaceRules.RuleSource> combinedRules = new ArrayList<>();
        combinedRules.addAll(dimensionRules);
        combinedRules.addAll(GLOBAL_SURFACE_RULES);
        combinedRules.add(originalRules);
        return SurfaceRules.sequence(combinedRules.toArray(SurfaceRules.RuleSource[]::new));
    }
}
