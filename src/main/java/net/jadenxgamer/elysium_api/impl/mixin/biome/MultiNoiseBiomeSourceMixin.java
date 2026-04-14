package net.jadenxgamer.elysium_api.impl.mixin.biome;

import com.mojang.datafixers.util.Either;
import net.jadenxgamer.elysium_api.api.util.RegistryAccessHelper;
import net.jadenxgamer.elysium_api.impl.core.biome.ElysiumBiomeHelper;
import net.jadenxgamer.elysium_api.impl.core.biome.ElysiumBiomeSource;
import net.jadenxgamer.elysium_api.impl.core.biome.ElysiumTerrablenderHelper;
import net.jadenxgamer.elysium_api.impl.core.datadriven.biome_replacer.BiomeReplacerDataDriven;
import net.jadenxgamer.elysium_api.impl.registry.ElysiumRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.neoforged.fml.ModList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Mixin(value = MultiNoiseBiomeSource.class, priority = 996)
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
        Holder<Biome> currentBiome = getCurrentBiome(x, y, z, sampler);

        if (this instanceof ElysiumBiomeSource sourceElysium && sourceElysium.getDimension() != null) {
            List<ElysiumBiomeHelper.BiomeReplacer> biomeReplacers = ElysiumBiomeHelper.biomesForDimension(sourceElysium.getDimension());
            List<BiomeReplacerDataDriven> dataDrivenReplacers = RegistryAccessHelper.getServer()
                    .map(registryAccess -> registryAccess.registryOrThrow(ElysiumRegistries.BIOME_REPLACER).stream().toList())
                    .orElse(List.of());

            Holder<Biome> replacedBiome = replaceBiomeIfNeeded(x, z, currentBiome, biomeReplacers, dataDrivenReplacers, sourceElysium.getWorldSeed());

            if (!replacedBiome.equals(currentBiome)) {
                cir.setReturnValue(replacedBiome);
            }
        }
    }

    @Unique
    private Holder<Biome> getCurrentBiome(int x, int y, int z, Climate.Sampler sampler) {
        if (ModList.get().isLoaded("terrablender")) {
            return ElysiumTerrablenderHelper.getCurrentBiome((MultiNoiseBiomeSource) (Object) this, x, y, z, sampler);
        }
        return this.parameters().findValue(sampler.sample(x, y, z));
    }

    @Unique
    private Holder<Biome> replaceBiomeIfNeeded(int x, int z, Holder<Biome> currentBiome, List<ElysiumBiomeHelper.BiomeReplacer> biomeReplacers, List<BiomeReplacerDataDriven> dataDrivenReplacers, long worldSeed) {
        Set<Holder<Biome>> processedBiomes = new HashSet<>();
        Random random = new Random();

        while (currentBiome != null && processedBiomes.add(currentBiome)) {
            for (ElysiumBiomeHelper.BiomeReplacer replacer : biomeReplacers) {
                if (replacer.replaceBiomes().contains(currentBiome)) {
                    long uniqueSeed = makeCoordinatesIntoSeed(x / replacer.size(), z / replacer.size(), worldSeed) ^ replacer.uniqueId().hashCode();
                    random.setSeed(uniqueSeed);

                    if (random.nextDouble() < replacer.rarity()) {
                        return RegistryAccessHelper.getServer()
                                .flatMap(access -> access.registryOrThrow(Registries.BIOME).getHolder(replacer.withBiome()))
                                .map(holder -> (Holder<Biome>) holder)
                                .orElse(currentBiome);
                    }
                }
            }

            for (BiomeReplacerDataDriven replacer : dataDrivenReplacers) {
                if (replacer.replaceBiomes().contains(currentBiome)) {
                    long uniqueSeed = makeCoordinatesIntoSeed(x / replacer.size(), z / replacer.size(), worldSeed) ^ replacer.uniqueId().hashCode();
                    random.setSeed(uniqueSeed);

                    if (random.nextDouble() < replacer.rarity()) {
                        return replacer.withBiome();
                    }
                }
            }
        }
        return currentBiome;
    }

    @Unique
    private long makeCoordinatesIntoSeed(int scaledX, int scaledZ, long worldSeed) {
        return ((31L * scaledX + 17) ^ (37L * scaledZ + 23) ^ worldSeed) * 0x5DEECE66DL;
    }
}