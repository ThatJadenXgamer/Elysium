package net.jadenxgamer.elysium_api.api.surface_rules;

import net.jadenxgamer.elysium_api.Elysium;
import net.jadenxgamer.elysium_api.impl.compat.ElysiumTerrablenderHelper;
import net.jadenxgamer.elysium_api.impl.surface_rules.ElysiumSurfaceRulesManager;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraftforge.fml.loading.FMLLoader;

public class SurfaceRulesRegistry {

    /**
     * Add your own custom {@link SurfaceRules} that gets applied to The Overworld
     */
    public static void registerOverworldSurfaceRule(SurfaceRules.RuleSource rule, String namespace) {
        if (FMLLoader.getLoadingModList().getModFileById("terrablender") != null) {
            ElysiumTerrablenderHelper.addOverworldSurfaceRule(rule, namespace);
        } else {
            ElysiumSurfaceRulesManager.OVERWORLD_SURFACE_RULES.add(rule);
        }
    }

    /**
     * Add your own custom {@link SurfaceRules} that gets applied to The Nether
     */
    public static void registerNetherSurfaceRule(SurfaceRules.RuleSource rule, String namespace) {
        if (FMLLoader.getLoadingModList().getModFileById("terrablender") != null) {
            ElysiumTerrablenderHelper.addNetherSurfaceRule(rule, namespace);
        } else {
            ElysiumSurfaceRulesManager.NETHER_SURFACE_RULES.add(rule);
        }
    }

    /**
     * Add your own custom {@link SurfaceRules} that gets applied to The End
     */
    public static void registerEndSurfaceRule(SurfaceRules.RuleSource rule, String namespace) {
        ElysiumSurfaceRulesManager.END_SURFACE_RULES.add(rule);
    }

    /**
     * <u>DO NOT</u> use this method to register your Surface Rules it is not Terrablender compatible
     * and exists only for backwards compatibility.
     * <p>
     * use these methods to register Surface Rules instead:
     * <p> {@link SurfaceRulesRegistry#registerOverworldSurfaceRule(SurfaceRules.RuleSource, String)}
     * <p> {@link SurfaceRulesRegistry#registerNetherSurfaceRule(SurfaceRules.RuleSource, String)}
     * <p> {@link SurfaceRulesRegistry#registerEndSurfaceRule(SurfaceRules.RuleSource, String)}
     */
    @Deprecated(forRemoval = true)
    public static void registerSurfaceRule(SurfaceRules.RuleSource rule) {
        Elysium.LOGGER.warn("A SurfaceRule was registered using the deprecated method SurfaceRulesRegistry#registerSurfaceRule()");
        ElysiumSurfaceRulesManager.OVERWORLD_SURFACE_RULES.add(rule);
        ElysiumSurfaceRulesManager.NETHER_SURFACE_RULES.add(rule);
        ElysiumSurfaceRulesManager.END_SURFACE_RULES.add(rule);
    }
}