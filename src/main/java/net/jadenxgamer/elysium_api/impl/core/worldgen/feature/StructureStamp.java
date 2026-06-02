package net.jadenxgamer.elysium_api.impl.core.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.jadenxgamer.elysium_api.impl.registry.ElysiumBlocks;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        Config config = context.config();

        if (config.canPlaceOn().isPresent() && !level.getBlockState(origin.below()).is(config.canPlaceOn().get())) return false;
        StructureTemplate template = level.getLevel().getServer().getStructureManager().getOrCreate(config.template());
        Rotation rotation = config.rotation().isEmpty() ? Rotation.getRandom(random) : config.rotation().get();

        BlockPos placementPos;
        switch (config.originType()) {
            case CENTERED -> placementPos = centeredPlacementPos(origin, rotation, template.getSize());
            case ANCHORED -> {
                BlockPos anchorLocal = findAnchor(template);
                if (anchorLocal == null) placementPos = origin; //fallback to CORNER type
                else {
                    BlockPos rotatedAnchor = anchorLocal.rotate(rotation);
                    placementPos = origin.subtract(rotatedAnchor);
                }
            }
            default -> placementPos = origin;
        }
        placementPos = placementPos.offset(0, config.originOffset(), 0);

        StructurePlaceSettings settings = new StructurePlaceSettings()
                .setRotation(rotation)
                .setRandom(random)
                .setLiquidSettings(config.liquidSettings);
        for (StructureProcessor processor : config.processors().value().list()) settings.addProcessor(processor);
        if (config.originType() == OriginType.ANCHORED) settings.addProcessor(new AnchorRemovalProcessor());

        template.placeInWorld(level, placementPos, placementPos, settings, random, 3);
        return true;
    }

    private static BlockPos centeredPlacementPos(BlockPos origin, Rotation rotation, Vec3i size) {
        int halfX = size.getX() / 2;
        int halfZ = size.getZ() / 2;
        BlockPos offset;
        switch (rotation) {
            case NONE -> offset = new BlockPos(-halfX, 0, -halfZ);
            case CLOCKWISE_90 -> offset = new BlockPos(-halfZ, 0, halfX);
            case CLOCKWISE_180 -> offset = new BlockPos(halfX, 0, halfZ);
            case COUNTERCLOCKWISE_90 -> offset = new BlockPos(halfZ, 0, -halfX);
            default -> offset = BlockPos.ZERO;
        }
        return origin.offset(offset);
    }

    @Nullable
    private static BlockPos findAnchor(StructureTemplate template) {
        for (StructureTemplate.Palette palette : template.palettes) {
            for (StructureTemplate.StructureBlockInfo info : palette.blocks()) {
                if (info.state().is(ElysiumBlocks.STRUCTURE_STAMP_ANCHOR.get())) return info.pos();
            }
        }
        return null;
    }

    public record Config(ResourceLocation template, Optional<HolderSet<Block>> canPlaceOn, Holder<StructureProcessorList> processors,
            Optional<Rotation> rotation, LiquidSettings liquidSettings, OriginType originType, int originOffset) implements FeatureConfiguration {

        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("template").forGetter(Config::template),
                RegistryCodecs.homogeneousList(Registries.BLOCK).optionalFieldOf("can_place_on").forGetter(Config::canPlaceOn),
                StructureProcessorType.LIST_CODEC.fieldOf("processors").forGetter(Config::processors),
                Rotation.CODEC.optionalFieldOf("rotation").forGetter(Config::rotation),
                LiquidSettings.CODEC.fieldOf("liquid_settings").orElse(LiquidSettings.APPLY_WATERLOGGING).forGetter(Config::liquidSettings),
                OriginType.CODEC.optionalFieldOf("origin_type", OriginType.CORNER).forGetter(Config::originType),
                Codec.INT.optionalFieldOf("origin_offset", 0).forGetter(Config::originOffset)
        ).apply(instance, Config::new));
    }

    public enum OriginType implements StringRepresentable {
        CORNER("corner"),
        CENTERED("centered"),
        ANCHORED("anchored");

        private final String name;
        public static final StringRepresentableCodec<OriginType> CODEC = StringRepresentable.fromEnum(OriginType::values);

        OriginType(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private static class AnchorRemovalProcessor extends StructureProcessor {
        @Override
        public @Nullable StructureTemplate.StructureBlockInfo processBlock(LevelReader level, BlockPos offset, BlockPos pos, StructureTemplate.StructureBlockInfo blockInfo, StructureTemplate.StructureBlockInfo relativeBlockInfo, StructurePlaceSettings settings) {
            if (relativeBlockInfo.state().is(ElysiumBlocks.STRUCTURE_STAMP_ANCHOR.get())) return null;
            return super.processBlock(level, offset, pos, blockInfo, relativeBlockInfo, settings);
        }

        @Override
        protected StructureProcessorType<?> getType() {
            return StructureProcessorType.BLOCK_IGNORE;
        }
    }
}