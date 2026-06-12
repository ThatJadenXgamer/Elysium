package net.jadenxgamer.elysium_api.api.surface_rules.condition;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceRules.Context;

public record BlockMatchConditionSource(Block targetBlock) implements SurfaceRules.ConditionSource {
    public static final KeyDispatchDataCodec<BlockMatchConditionSource> CODEC =
            KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec(instance -> instance.group(
                    BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").forGetter(BlockMatchConditionSource::targetBlock)
            ).apply(instance, BlockMatchConditionSource::new)));

    @Override
    public KeyDispatchDataCodec<? extends SurfaceRules.ConditionSource> codec() {
        return CODEC;
    }

    @Override
    public SurfaceRules.Condition apply(Context ctx) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        return () -> {
            pos.set(ctx.blockX, ctx.blockY, ctx.blockZ);
            return ctx.chunk.getBlockState(pos).is(targetBlock);
        };
    }

    @Override
    public String toString() {
        return "BlockMatchConditionSource[targetBlock=" + this.targetBlock + "]";
    }
}