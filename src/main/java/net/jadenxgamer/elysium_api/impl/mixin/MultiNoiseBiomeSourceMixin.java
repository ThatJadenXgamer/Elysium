package net.jadenxgamer.elysium_api.impl.mixin;

import com.mojang.datafixers.util.Either;
import net.jadenxgamer.elysium_api.Elysium;
import net.jadenxgamer.elysium_api.impl.biome.ElysiumBiomeHelper;
import net.jadenxgamer.elysium_api.impl.biome.ElysiumBiomeSource;
import net.jadenxgamer.elysium_api.impl.compat.ElysiumTerrablenderHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraftforge.fml.loading.FMLLoader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Random;

@Mixin(value = MultiNoiseBiomeSource.class, priority = -800)
public abstract class MultiNoiseBiomeSourceMixin {
    @Shadow
    public abstract Climate.ParameterList<Holder<Biome>> parameters();

    @Shadow @Final private Either<Climate.ParameterList<Holder<Biome>>, Holder<MultiNoiseBiomeSourceParameterList>> parameters;

    @Inject(
            method = "getNoiseBiome(IIILnet/minecraft/world/level/biome/Climate$Sampler;)Lnet/minecraft/core/Holder;",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void elysium$getNoiseBiome(int x, int y, int z, Climate.Sampler sampler, CallbackInfoReturnable<Holder<Biome>> cir) {
        Holder<Biome> currentBiome;
        if (FMLLoader.getLoadingModList().getModFileById("terrablender") != null) {
            // if Terrablender is present then obtain its climate parameters for more accurate replacements
            currentBiome = ElysiumTerrablenderHelper.getCurrentBiome(((MultiNoiseBiomeSource) (Object) this), x, y, z, sampler);
        } else {
            currentBiome = this.parameters().findValue(sampler.sample(x, y, z));
        }

        if (this instanceof ElysiumBiomeSource sourceElysium && sourceElysium.getDimension() != null) {
            List<ElysiumBiomeHelper.BiomeReplacer> biomeReplacers = ElysiumBiomeHelper.biomesForDimension(sourceElysium.getDimension());

            boolean biomeReplaced = false;
            boolean biomeReplacedInThisIteration;
            do {
                biomeReplacedInThisIteration = false;
                for (ElysiumBiomeHelper.BiomeReplacer replacer : biomeReplacers) {
                    if (currentBiome.is(replacer.canReplace())) {
                        // scales x and z coordinates based on the replacer size
                        int scaledX = x / replacer.size();
                        int scaledZ = z / replacer.size();
                        long uniqueSeed = makeCoordinatesIntoSeed(scaledX, scaledZ) ^ replacer.id().hashCode();
                        double random = new Random(uniqueSeed).nextDouble();

                        if (random < replacer.rarity()) {
                            currentBiome = Elysium.registryAccess.registryOrThrow(Registries.BIOME).getHolderOrThrow(replacer.biome());
                            biomeReplacedInThisIteration = true;
                            biomeReplaced = true;
                            break;
                        }
                    }
                }
            } while (biomeReplacedInThisIteration); // we do this to allow other BiomeReplacers to replace already biomeReplaced biomes

            if (biomeReplaced) {
                cir.setReturnValue(currentBiome);
            }
        }
    }

    @Unique
    private long makeCoordinatesIntoSeed(int scaledX, int scaledZ) {
        // Generates a seed depending on the scaled coordinates for consistent randomness
        long x = 31L * scaledX + 17;
        long z = 37L * scaledZ + 23;
        return (x ^ z) * 0x5DEECE66DL;
    }

//    Something for future me to mess around with
//    @Unique
//    private boolean shouldReplaceBiome(int x, int z, double threshold) {
//        double scale = 0.01;
//        double noiseValue = elysium$noise.getValue(x * scale, z * scale);
//
//        return (noiseValue + 1) / 2.0 < threshold;
//    }
}
