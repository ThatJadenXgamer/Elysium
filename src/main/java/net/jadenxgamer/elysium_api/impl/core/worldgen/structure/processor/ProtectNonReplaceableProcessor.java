package net.jadenxgamer.elysium_api.impl.core.worldgen.structure.processor;

import com.mojang.serialization.MapCodec;
import net.jadenxgamer.elysium_api.impl.registry.ElysiumRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.Nullable;

public class ProtectNonReplaceableProcessor extends StructureProcessor {

    public static final MapCodec<ProtectNonReplaceableProcessor> CODEC = MapCodec.unit(ProtectNonReplaceableProcessor::new);

    @Override
    public @Nullable StructureTemplate.StructureBlockInfo processBlock(LevelReader level, BlockPos offset, BlockPos pos, StructureTemplate.StructureBlockInfo blockInfo, StructureTemplate.StructureBlockInfo relativeBlockInfo, StructurePlaceSettings settings) {
        return level.getBlockState(relativeBlockInfo.pos()).canBeReplaced() ? relativeBlockInfo : null;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return ElysiumRegistries.PROTECT_NON_REPLACEABLE.get();
    }
}