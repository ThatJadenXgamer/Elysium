package net.jadenxgamer.elysium_api.impl.core.datadriven.mosaic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

public record MosaicBiomeEntry(ResourceLocation dimension, int climatePoint, Holder<Biome> biome, int weight) {
    public static final Codec<MosaicBiomeEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("dimension").forGetter(MosaicBiomeEntry::dimension),
            Codec.INT.fieldOf("climate_point").forGetter(MosaicBiomeEntry::climatePoint),
            Biome.CODEC.fieldOf("biome").forGetter(MosaicBiomeEntry::biome),
            Codec.INT.fieldOf("weight").forGetter(MosaicBiomeEntry::weight)
    ).apply(instance, MosaicBiomeEntry::new));
}