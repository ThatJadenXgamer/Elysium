package net.jadenxgamer.elysium_api.impl.mixin;

import net.jadenxgamer.elysium_api.impl.biome.ElysiumBiomeHelper;
import net.jadenxgamer.elysium_api.impl.biome.ElysiumBiomeSource;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.dimension.LevelStem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Random;

@Mixin(value = MultiNoiseBiomeSource.class, priority = 800)
public abstract class MultiNoiseBiomeSourceMixin {
    @Shadow
    protected abstract Climate.ParameterList<Holder<Biome>> parameters();


    @Inject(
            method = "getNoiseBiome(IIILnet/minecraft/world/level/biome/Climate$Sampler;)Lnet/minecraft/core/Holder;",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void elysium$getNoiseBiome(int x, int y, int z, Climate.Sampler sampler, CallbackInfoReturnable<Holder<Biome>> cir) {
        Holder<Biome> currentBiome = this.parameters().findValue(sampler.sample(x, y, z));

        if (this instanceof ElysiumBiomeSource biomeSource && isReplaceableDimension(biomeSource.getDimension())) {
            List<ElysiumBiomeHelper.BiomeReplacer> matchingReplaces = ElysiumBiomeHelper.biomesForDimension(biomeSource.getDimension()).stream().filter(p -> p.canReplace().is(currentBiome.unwrapKey().get())).toList();
            if (matchingReplaces.isEmpty()) {
                return;
            }

            for (ElysiumBiomeHelper.BiomeReplacer replacer : matchingReplaces) {
                int scaledX = x / replacer.size();
                int scaledZ = z / replacer.size();
                Random random = new Random(makeCoordinatesIntoSeed(scaledX, scaledZ));

                if (random.nextDouble() < replacer.rarity()) {
                    cir.setReturnValue(replacer.biome());
                    return;
                }
            }
        }
    }

    @Unique
    private long makeCoordinatesIntoSeed(int regionX, int regionZ) {
        // Generates a seed depending on the region quad coordinates for consistent randomness
        long x = 31L * regionX + 17;
        long z = 37L * regionZ + 23;
        return (x ^ z) * 0x5DEECE66DL;
    }

    @Unique
    private boolean isReplaceableDimension(ResourceKey<LevelStem> dimension) {
        return dimension.equals(LevelStem.OVERWORLD) || dimension.equals(LevelStem.NETHER);
    }
}
