package net.jadenxgamer.elysium_api.impl.core.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;

import java.util.Optional;


public class StructureStamp extends Feature<StructureStamp.Config> {
    public StructureStamp(Codec<Config> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        BlockPos origin = context.origin();
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos.MutableBlockPos pos = origin.below().mutable();
        var config = context.config();

        if (!level.getBlockState(origin.below()).is(config.canPlaceOn())) return false;
        StructureTemplate template = level.getLevel().getServer().getStructureManager().getOrCreate(config.template);
        Rotation rotation = config.rotation.isEmpty() ? Rotation.getRandom(random) : config.rotation().get();
        BlockPos placementPos = offsetChunkPos(pos, rotation, template.getSize());
        StructurePlaceSettings settings = new StructurePlaceSettings()
                .setRotation(rotation)
                .setRandom(random)
                .setLiquidSettings(config.liquidSettings);
        for (StructureProcessor processor : config.processors().value().list()) settings.addProcessor(processor);
        template.placeInWorld(level, placementPos, placementPos, settings, random, 3);
        return true;
    }

    private static BlockPos offsetChunkPos(BlockPos.MutableBlockPos pos, Rotation rotation, Vec3i size) {
        int halfX = size.getX() / 2, halfZ = size.getZ() / 2;
        int offsetX = 0, offsetZ = 0;

        switch (rotation) {
            case NONE -> {
                offsetX = -halfX;
                offsetZ = -halfZ;
            }
            case CLOCKWISE_90 -> {
                offsetX = halfZ;
                offsetZ = -halfX;
            }
            case CLOCKWISE_180 -> {
                offsetX = halfX;
                offsetZ = halfZ;
            }
            case COUNTERCLOCKWISE_90 -> {
                offsetX = -halfZ;
                offsetZ = halfX;
            }
        }

        return pos.offset(offsetX, 0, offsetZ);
    }

    public record Config(ResourceLocation template, HolderSet<Block> canPlaceOn,
                         Holder<StructureProcessorList> processors, Optional<Rotation> rotation, LiquidSettings liquidSettings) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("template").forGetter(Config::template),
                RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("can_place_on").forGetter(Config::canPlaceOn),
                StructureProcessorType.LIST_CODEC.fieldOf("processors").forGetter(Config::processors),
                Rotation.CODEC.optionalFieldOf("rotation").forGetter(Config::rotation),
                LiquidSettings.CODEC.fieldOf("liquid_settings").orElse(LiquidSettings.APPLY_WATERLOGGING).forGetter(Config::liquidSettings)
        ).apply(instance, Config::new));
    }
}
