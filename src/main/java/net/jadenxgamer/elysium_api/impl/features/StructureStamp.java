package net.jadenxgamer.elysium_api.impl.features;

import com.mojang.serialization.Codec;
import net.jadenxgamer.elysium_api.impl.features.configs.StructureStampConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;


public class StructureStamp extends Feature<StructureStampConfiguration> {
    public StructureStamp(Codec<StructureStampConfiguration> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<StructureStampConfiguration> context) {
        BlockPos origin = context.origin();
        WorldGenLevel genLevel = context.level();
        RandomSource random = context.random();
        BlockPos.MutableBlockPos pos = origin.below().mutable();

        if (!genLevel.getBlockState(origin.below()).is(context.config().canPlaceOn())) return false;
        StructureTemplate template = genLevel.getLevel().getServer().getStructureManager().getOrCreate(context.config().structures().get(random.nextInt(context.config().structures().size())));
        ChunkPos chunkPos = new ChunkPos(pos);
        BoundingBox boundingBox = new BoundingBox(
                chunkPos.getMinBlockX() - context.config().boundingBoxScale() + template.getSize().getX(),
                genLevel.getMinBuildHeight(),
                chunkPos.getMinBlockZ() - context.config().boundingBoxScale() + template.getSize().getZ(),
                chunkPos.getMaxBlockX() + context.config().boundingBoxScale() + template.getSize().getX(),
                genLevel.getMaxBuildHeight(),
                chunkPos.getMaxBlockZ() + context.config().boundingBoxScale() + template.getSize().getZ()
        );
        Rotation rotation = context.config().fixedRotation().isEmpty() ? Rotation.getRandom(random) : context.config().fixedRotation().get();
        BlockPos placementPos = offsetChunkPos(pos, rotation, template.getSize());
        StructurePlaceSettings placeSettings = new StructurePlaceSettings().setRotation(rotation).setBoundingBox(boundingBox).setRandom(random);
        placeSettings.clearProcessors().addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
        template.placeInWorld(genLevel, placementPos, placementPos, placeSettings, random, 3);
        placeSettings.clearProcessors();

        return true;
    }
    private static BlockPos offsetChunkPos(BlockPos.MutableBlockPos pos, Rotation rotation, Vec3i size) {
        int halfX = size.getX() / 2;
        int halfZ = size.getZ() / 2;
        int offsetX = 0;
        int offsetZ = 0;

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
}