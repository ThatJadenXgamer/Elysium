package net.jadenxgamer.elysium_api.api.surface_rules;

import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.ArrayList;
import java.util.List;

public class SurfaceRulesRegistry {
    public static final List<SurfaceRules.RuleSource> RULES = new ArrayList<>();

    /**
     * Elysium allows you to add your own custom SurfaceRules that gets applied globally to all dimensions
     * @param rule - SurfaceRules specified here get added to the list
     */
    public static void registerSurfaceRule(SurfaceRules.RuleSource rule) {
        RULES.add(rule);
    }
}