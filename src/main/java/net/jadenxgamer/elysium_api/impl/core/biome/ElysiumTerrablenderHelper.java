package net.jadenxgamer.elysium_api.impl.core.biome;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.levelgen.SurfaceRules;
import terrablender.api.SurfaceRuleManager;
import terrablender.worldgen.IExtendedParameterList;

public class ElysiumTerrablenderHelper {

    public static void addOverworldSurfaceRule(SurfaceRules.RuleSource rules, String namespace) {
        SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, namespace, rules);
    }

    public static void addNetherSurfaceRule(SurfaceRules.RuleSource rules, String namespace) {
        SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.NETHER, namespace, rules);
    }
}
