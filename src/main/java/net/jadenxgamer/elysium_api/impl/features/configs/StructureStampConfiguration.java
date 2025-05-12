package net.jadenxgamer.elysium_api.impl.features.configs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import java.util.List;
import java.util.Optional;

public record StructureStampConfiguration(List<ResourceLocation> structures, HolderSet<Block> canPlaceOn, Optional<Rotation> fixedRotation, int boundingBoxScale) implements FeatureConfiguration {
    public static final Codec<StructureStampConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.listOf().fieldOf("structures").forGetter(StructureStampConfiguration::structures),
            RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("can_place_on").forGetter(StructureStampConfiguration::canPlaceOn),
            Rotation.CODEC.optionalFieldOf("fixed_rotation").forGetter(StructureStampConfiguration::fixedRotation),
            Codec.INT.fieldOf("bounding_box_scale").orElse(16).forGetter(StructureStampConfiguration::boundingBoxScale)
    ).apply(instance, StructureStampConfiguration::new));
}