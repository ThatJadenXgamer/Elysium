package net.jadenxgamer.elysium_api.api.surface_rules;

import net.jadenxgamer.elysium_api.impl.compat.ElysiumTerrablenderHelper;
import net.jadenxgamer.elysium_api.impl.surface_rules.ElysiumSurfaceRulesManager;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraftforge.fml.loading.FMLLoader;

public class SurfaceRulesRegistry {

    /**
     * Add your own custom {@link SurfaceRules} that gets applied globally to All Dimensions
     */
    public static void registerSurfaceRule(SurfaceRules.RuleSource rule) {
        ElysiumSurfaceRulesManager.GLOBAL_SURFACE_RULES.add(rule);
    }

    /**
     * Add your own custom {@link SurfaceRules} that gets applied to The Overworld
     */
    public static void registerOverworldSurfaceRule(SurfaceRules.RuleSource rule) {
        ElysiumSurfaceRulesManager.OVERWORLD_SURFACE_RULES.add(rule);
    }

    /**
     * Add your own custom {@link SurfaceRules} that gets applied to The Nether
     */
    public static void registerNetherSurfaceRule(SurfaceRules.RuleSource rule) {
        ElysiumSurfaceRulesManager.NETHER_SURFACE_RULES.add(rule);
    }

    /**
     * Add your own custom {@link SurfaceRules} that gets applied to The End
     */
    public static void registerEndSurfaceRule(SurfaceRules.RuleSource rule) {
        ElysiumSurfaceRulesManager.END_SURFACE_RULES.add(rule);
    }
}