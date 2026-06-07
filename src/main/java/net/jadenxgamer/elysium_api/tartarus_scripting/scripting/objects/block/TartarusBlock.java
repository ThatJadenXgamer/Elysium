package net.jadenxgamer.elysium_api.tartarus_scripting.scripting.objects.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.mozilla.javascript.Scriptable;

/**
 * A block that delegates its behavior to a compiled ScriptedBlockBehavior from javascript
 */
public class TartarusBlock extends Block {
    private final ScriptedBlockBehavior behavior;

    private static final ThreadLocal<ScriptedBlockBehavior> PENDING_BEHAVIOR = new ThreadLocal<>();

    /**
     * Factory method called from JavaScript.
     * @param properties BlockBehaviour.Properties
     * @param behaviorObj JavaScript object containing methods like neighborChanged, animateTick, etc.
     * @return a new TartarusBlock instance
     */
    public static TartarusBlock create(Properties properties, Scriptable behaviorObj) {
        ScriptedBlockBehavior compiledBehavior = ScriptedBlockCompiler.wrap(behaviorObj);
        PENDING_BEHAVIOR.set(compiledBehavior);
        try {
            return new TartarusBlock(properties, compiledBehavior);
        } finally {
            PENDING_BEHAVIOR.remove();
        }
    }

    private TartarusBlock(Properties properties, ScriptedBlockBehavior behavior) {
        super(properties);
        this.behavior = behavior;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        ScriptedBlockBehavior activeBehavior = PENDING_BEHAVIOR.get();
        if (activeBehavior != null) {
            try {
                activeBehavior.createBlockStateDefinition(builder);
            } catch (UnsupportedOperationException ignored) {}
        }
    }

    // Delegated Behaviors //

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        try {
            behavior.animateTick(state, level, pos, random);
        } catch (UnsupportedOperationException e) {
            super.animateTick(state, level, pos, random);
        }
    }

    @Override
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        try {
            behavior.attack(state, level, pos, player);
        } catch (UnsupportedOperationException e) {
            super.attack(state, level, pos, player);
        }
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
        try {
            return behavior.canBeReplaced(state, useContext);
        } catch (UnsupportedOperationException e) {
            return super.canBeReplaced(state, useContext);
        }
    }

    @Override
    public boolean canBeReplaced(BlockState state, Fluid fluid) {
        try {
            return behavior.canBeReplaced(state, fluid);
        } catch (UnsupportedOperationException e) {
            return super.canBeReplaced(state, fluid);
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        try {
            return behavior.canSurvive(state, level, pos);
        } catch (UnsupportedOperationException e) {
            return super.canSurvive(state, level, pos);
        }
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        try {
            behavior.destroy(level, pos, state);
        } catch (UnsupportedOperationException e) {
            super.destroy(level, pos, state);
        }
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        try {
            behavior.entityInside(state, level, pos, entity);
        } catch (UnsupportedOperationException e) {
            super.entityInside(state, level, pos, entity);
        }
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        try {
            behavior.fallOn(level, state, pos, entity, fallDistance);
        } catch (UnsupportedOperationException e) {
            super.fallOn(level, state, pos, entity, fallDistance);
        }
    }

    @Override
    public void handlePrecipitation(BlockState state, Level level, BlockPos pos, net.minecraft.world.level.biome.Biome.Precipitation precipitation) {
        try {
            behavior.handlePrecipitation(state, level, pos, precipitation);
        } catch (UnsupportedOperationException e) {
            super.handlePrecipitation(state, level, pos, precipitation);
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        try {
            behavior.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        } catch (UnsupportedOperationException e) {
            super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        }
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        try {
            behavior.onPlace(state, level, pos, oldState, movedByPiston);
        } catch (UnsupportedOperationException e) {
            super.onPlace(state, level, pos, oldState, movedByPiston);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        try {
            behavior.onRemove(state, level, pos, newState, movedByPiston);
        } catch (UnsupportedOperationException e) {
            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable net.minecraft.world.level.block.entity.BlockEntity blockEntity, ItemStack tool) {
        try {
            behavior.playerDestroy(level, player, pos, state, blockEntity, tool);
        } catch (UnsupportedOperationException e) {
            super.playerDestroy(level, player, pos, state, blockEntity, tool);
        }
    }

    @Override
    public void popExperience(ServerLevel level, BlockPos pos, int amount) {
        try {
            behavior.popExperience(level, pos, amount);
        } catch (UnsupportedOperationException e) {
            super.popExperience(level, pos, amount);
        }
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        try {
            behavior.randomTick(state, level, pos, random);
        } catch (UnsupportedOperationException e) {
            super.randomTick(state, level, pos, random);
        }
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        try {
            behavior.setPlacedBy(level, pos, state, placer, stack);
        } catch (UnsupportedOperationException e) {
            super.setPlacedBy(level, pos, state, placer, stack);
        }
    }

    @Override
    public void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack stack, boolean dropExperience) {
        try {
            behavior.spawnAfterBreak(state, level, pos, stack, dropExperience);
        } catch (UnsupportedOperationException e) {
            super.spawnAfterBreak(state, level, pos, stack, dropExperience);
        }
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        try {
            behavior.stepOn(level, pos, state, entity);
        } catch (UnsupportedOperationException e) {
            super.stepOn(level, pos, state, entity);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        try {
            behavior.tick(state, level, pos, random);
        } catch (UnsupportedOperationException e) {
            super.tick(state, level, pos, random);
        }
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter level, Entity entity) {
        try {
            behavior.updateEntityAfterFallOn(level, entity);
        } catch (UnsupportedOperationException e) {
            super.updateEntityAfterFallOn(level, entity);
        }
    }

    @Override
    public void wasExploded(Level level, BlockPos pos, Explosion explosion) {
        try {
            behavior.wasExploded(level, pos, explosion);
        } catch (UnsupportedOperationException e) {
            super.wasExploded(level, pos, explosion);
        }
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        try {
            return behavior.getCloneItemStack(level, pos, state);
        } catch (UnsupportedOperationException e) {
            return super.getCloneItemStack(level, pos, state);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        try {
            return behavior.getShape(state, level, pos, context);
        } catch (UnsupportedOperationException e) {
            return super.getShape(state, level, pos, context);
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        try {
            return behavior.getCollisionShape(state, level, pos, context);
        } catch (UnsupportedOperationException e) {
            return super.getCollisionShape(state, level, pos, context);
        }
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        try {
            return behavior.getOcclusionShape(state, level, pos);
        } catch (UnsupportedOperationException e) {
            return super.getOcclusionShape(state, level, pos);
        }
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        try {
            return behavior.getVisualShape(state, level, pos, context);
        } catch (UnsupportedOperationException e) {
            return super.getVisualShape(state, level, pos, context);
        }
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
        try {
            return behavior.getBlockSupportShape(state, level, pos);
        } catch (UnsupportedOperationException e) {
            return super.getBlockSupportShape(state, level, pos);
        }
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        try {
            return behavior.getInteractionShape(state, level, pos);
        } catch (UnsupportedOperationException e) {
            return super.getInteractionShape(state, level, pos);
        }
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        try {
            return behavior.getLightBlock(state, level, pos);
        } catch (UnsupportedOperationException e) {
            return super.getLightBlock(state, level, pos);
        }
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        try {
            return behavior.getDestroyProgress(state, player, level, pos);
        } catch (UnsupportedOperationException e) {
            return super.getDestroyProgress(state, player, level, pos);
        }
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        try {
            return behavior.getSignal(state, level, pos, direction);
        } catch (UnsupportedOperationException e) {
            return super.getSignal(state, level, pos, direction);
        }
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        try {
            return behavior.getDirectSignal(state, level, pos, direction);
        } catch (UnsupportedOperationException e) {
            return super.getDirectSignal(state, level, pos, direction);
        }
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        try {
            return behavior.isSignalSource(state);
        } catch (UnsupportedOperationException e) {
            return super.isSignalSource(state);
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        try {
            return behavior.hasAnalogOutputSignal(state);
        } catch (UnsupportedOperationException e) {
            return super.hasAnalogOutputSignal(state);
        }
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        try {
            return behavior.getAnalogOutputSignal(state, level, pos);
        } catch (UnsupportedOperationException e) {
            return super.getAnalogOutputSignal(state, level, pos);
        }
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        try {
            return behavior.useWithoutItem(state, level, pos, player, hitResult);
        } catch (UnsupportedOperationException e) {
            return super.useWithoutItem(state, level, pos, player, hitResult);
        }
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        try {
            return behavior.useItemOn(stack, state, level, pos, player, hand, hitResult);
        } catch (UnsupportedOperationException e) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }
    }

    @Override
    public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
        try {
            return behavior.triggerEvent(state, level, pos, id, param);
        } catch (UnsupportedOperationException e) {
            return super.triggerEvent(state, level, pos, id, param);
        }
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacentState, Direction direction) {
        try {
            return behavior.skipRendering(state, adjacentState, direction);
        } catch (UnsupportedOperationException e) {
            return super.skipRendering(state, adjacentState, direction);
        }
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        try {
            return behavior.propagatesSkylightDown(state, level, pos);
        } catch (UnsupportedOperationException e) {
            return super.propagatesSkylightDown(state, level, pos);
        }
    }

    @Nullable
    @Override
    public net.minecraft.world.MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        try {
            return behavior.getMenuProvider(state, level, pos);
        } catch (UnsupportedOperationException e) {
            return super.getMenuProvider(state, level, pos);
        }
    }

    @Override
    public void onProjectileHit(Level level, BlockState state, BlockHitResult hit, net.minecraft.world.entity.projectile.Projectile projectile) {
        try {
            behavior.onProjectileHit(level, state, hit, projectile);
        } catch (UnsupportedOperationException e) {
            super.onProjectileHit(level, state, hit, projectile);
        }
    }
}