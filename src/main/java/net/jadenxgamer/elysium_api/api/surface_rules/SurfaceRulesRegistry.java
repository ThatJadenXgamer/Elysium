package net.jadenxgamer.elysium_api.api.surface_rules;

import net.jadenxgamer.elysium_api.impl.core.biome.biome_replacer.ElysiumTerrablenderHelper;
import net.jadenxgamer.elysium_api.impl.core.surface_rules.ElysiumSurfaceRulesManager;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.neoforged.fml.ModList;

public class SurfaceRulesRegistry {

    /**
     * Add your own custom {@link SurfaceRules} that gets applied to The Overworld
     */
    public static void registerOverworldSurfaceRule(SurfaceRules.RuleSource rule, String namespace) {
        if (ModList.get().isLoaded("terrablender")) {
            ElysiumTerrablenderHelper.addOverworldSurfaceRule(rule, namespace);
        } else {
            ElysiumSurfaceRulesManager.OVERWORLD_SURFACE_RULES.add(rule);
        }
    }

    /**
     * Add your own custom {@link SurfaceRules} that gets applied to The Nether
     */
    public static void registerNetherSurfaceRule(SurfaceRules.RuleSource rule, String namespace) {
        if (ModList.get().isLoaded("terrablender")) {
            ElysiumTerrablenderHelper.addNetherSurfaceRule(rule, namespace);
        } else {
            ElysiumSurfaceRulesManager.NETHER_SURFACE_RULES.add(rule);
        }
    }

    /**
     * Add your own custom {@link SurfaceRules} that gets applied to The End
     */
    public static void registerEndSurfaceRule(SurfaceRules.RuleSource rule, String namespace) {
        if (ModList.get().isLoaded("terrablender")) {
            ElysiumTerrablenderHelper.addEndSurfaceRule(rule, namespace);
        } else {
            ElysiumSurfaceRulesManager.END_SURFACE_RULES.add(rule);
        }
    }
}