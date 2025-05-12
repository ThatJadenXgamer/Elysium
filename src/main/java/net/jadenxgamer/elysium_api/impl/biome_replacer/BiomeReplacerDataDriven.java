package net.jadenxgamer.elysium_api.impl.biome_replacer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

public record BiomeReplacerDataDriven(HolderSet<Biome> replaceBiomes, Holder<Biome> withBiome, double rarity, int size, ResourceLocation uniqueId, ResourceLocation dimension) {
    public static final Codec<BiomeReplacerDataDriven> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegistryCodecs.homogeneousList(Registries.BIOME).fieldOf("replace_biomes").forGetter(BiomeReplacerDataDriven::replaceBiomes),
            Biome.CODEC.fieldOf("with_biome").forGetter(BiomeReplacerDataDriven::withBiome),
            Codec.DOUBLE.fieldOf("rarity").forGetter(BiomeReplacerDataDriven::rarity),
            Codec.INT.fieldOf("size").forGetter(BiomeReplacerDataDriven::size),
            ResourceLocation.CODEC.fieldOf("unique_id").forGetter(BiomeReplacerDataDriven::uniqueId),
            ResourceLocation.CODEC.fieldOf("dimension").forGetter(BiomeReplacerDataDriven::dimension)
    ).apply(instance, BiomeReplacerDataDriven::new));
}