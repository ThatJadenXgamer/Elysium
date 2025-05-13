package net.jadenxgamer.elysium_api.impl.compat;

import net.jadenxgamer.elysium_api.impl.surface_rules.ElysiumSurfaceRulesManager;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
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

    public static void addSurfaceRules() {
        // I have no idea why calling "netherexp" causes the surface rule registry to work while "elysium_api" doesn't.
        // does it work? ofcourse! is this cursed? very. do I care? fuck no lmao
        SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, "netherexp",
                ElysiumSurfaceRulesManager.getForTerrablenderRules(ElysiumSurfaceRulesManager.OVERWORLD_SURFACE_RULES));
        SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.NETHER, "netherexp",
                ElysiumSurfaceRulesManager.getForTerrablenderRules(ElysiumSurfaceRulesManager.NETHER_SURFACE_RULES));
    }
}
