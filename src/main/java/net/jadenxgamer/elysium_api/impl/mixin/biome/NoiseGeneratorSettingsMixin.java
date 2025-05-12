package net.jadenxgamer.elysium_api.impl.mixin.biome;

import net.jadenxgamer.elysium_api.Elysium;
import net.jadenxgamer.elysium_api.impl.surface_rules.ElysiumNoiseGeneratorSettings;
import net.jadenxgamer.elysium_api.impl.surface_rules.SurfaceRulesManager;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = NoiseGeneratorSettings.class, priority = -996)
public class NoiseGeneratorSettingsMixin implements ElysiumNoiseGeneratorSettings {

    @Unique
    private ResourceKey<LevelStem> elysium$currentDimension = null;

    @Inject(
            method = "surfaceRule",
            at = @At(value = "RETURN"),
            cancellable = true
    )
    private void elysium$surfaceRule(CallbackInfoReturnable<SurfaceRules.RuleSource> cir) {
        SurfaceRules.RuleSource newRules = SurfaceRulesManager.mergeSurfaceRules(getDimension(), cir.getReturnValue());
        if (newRules == null) return;
        cir.setReturnValue(newRules);
    }

    @Override
    public void initElysiumSurfaceRules(ResourceKey<LevelStem> dimension) {
        elysium$currentDimension = dimension;
        Elysium.LOGGER.info("Elysium SurfaceRules successfully merged for " + dimension.location());
    }

    @Override
    public ResourceKey<LevelStem> getDimension() {
        return this.elysium$currentDimension;
    }
}