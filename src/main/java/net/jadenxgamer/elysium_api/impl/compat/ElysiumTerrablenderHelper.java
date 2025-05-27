package net.jadenxgamer.elysium_api.impl.compat;

import net.jadenxgamer.elysium_api.impl.surface_rules.ElysiumSurfaceRulesManager;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.levelgen.SurfaceRules;
import terrablender.api.SurfaceRuleManager;
import terrablender.worldgen.IExtendedParameterList;

public class ElysiumTerrablenderHelper {

    public static Holder<Biome> getCurrentBiome(MultiNoiseBiomeSource source, int x, int y, int z, Climate.Sampler sampler) {
        if (source.parameters() instanceof IExtendedParameterList<?> terrablenderParameters) {
            if (terrablenderParameters.isInitialized()) {
                return (Holder) terrablenderParameters.findValuePositional(sampler.sample(x, y, z), x, y, z);
            }
        }

        return source.parameters().findValue(sampler.sample(x, y, z));
    }

    public static void addOverworldSurfaceRule(SurfaceRules.RuleSource rules, String namespace) {
        SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, namespace, rules);
    }

    public static void addNetherSurfaceRule(SurfaceRules.RuleSource rules, String namespace) {
        SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.NETHER, namespace, rules);
    }
}
