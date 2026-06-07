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

public interface ScriptedBlockBehavior {

    default void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        throw new UnsupportedOperationException();
    }

    default void attack(BlockState state, Level level, BlockPos pos, Player player) {
        throw new UnsupportedOperationException();
    }

    default boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
        throw new UnsupportedOperationException();
    }

    default boolean canBeReplaced(BlockState state, Fluid fluid) {
        throw new UnsupportedOperationException();
    }

    default boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    default void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        throw new UnsupportedOperationException();
    }

    default void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        throw new UnsupportedOperationException();
    }

    default void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        throw new UnsupportedOperationException();
    }

    default void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        throw new UnsupportedOperationException();
    }

    default void handlePrecipitation(BlockState state, Level level, BlockPos pos, net.minecraft.world.level.biome.Biome.Precipitation precipitation) {
        throw new UnsupportedOperationException();
    }

    default void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        throw new UnsupportedOperationException();
    }

    default void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        throw new UnsupportedOperationException();
    }

    default void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        throw new UnsupportedOperationException();
    }

    default void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable net.minecraft.world.level.block.entity.BlockEntity blockEntity, ItemStack tool) {
        throw new UnsupportedOperationException();
    }

    default void popExperience(ServerLevel level, BlockPos pos, int amount) {
        throw new UnsupportedOperationException();
    }

    default void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        throw new UnsupportedOperationException();
    }

    default void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        throw new UnsupportedOperationException();
    }

    default void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack stack, boolean dropExperience) {
        throw new UnsupportedOperationException();
    }

    default void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        throw new UnsupportedOperationException();
    }

    default void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        throw new UnsupportedOperationException();
    }

    default void updateEntityAfterFallOn(BlockGetter level, Entity entity) {
        throw new UnsupportedOperationException();
    }

    default void wasExploded(Level level, BlockPos pos, Explosion explosion) {
        throw new UnsupportedOperationException();
    }

    default ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        throw new UnsupportedOperationException();
    }

    default VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        throw new UnsupportedOperationException();
    }

    default VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        throw new UnsupportedOperationException();
    }

    default VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    default VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        throw new UnsupportedOperationException();
    }

    default VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    default VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    default int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    default float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    default int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        throw new UnsupportedOperationException();
    }

    default int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        throw new UnsupportedOperationException();
    }

    default boolean isSignalSource(BlockState state) {
        throw new UnsupportedOperationException();
    }

    default boolean hasAnalogOutputSignal(BlockState state) {
        throw new UnsupportedOperationException();
    }

    default int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    default InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        throw new UnsupportedOperationException();
    }

    default ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        throw new UnsupportedOperationException();
    }

    default boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
        throw new UnsupportedOperationException();
    }

    default boolean skipRendering(BlockState state, BlockState adjacentState, Direction direction) {
        throw new UnsupportedOperationException();
    }

    default boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    default @Nullable net.minecraft.world.MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    default void onProjectileHit(Level level, BlockState state, BlockHitResult hit, net.minecraft.world.entity.projectile.Projectile projectile) {
        throw new UnsupportedOperationException();
    }
}