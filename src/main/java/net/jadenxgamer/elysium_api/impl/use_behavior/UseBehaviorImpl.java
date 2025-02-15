package net.jadenxgamer.elysium_api.impl.use_behavior;

import net.jadenxgamer.elysium_api.Elysium;
import net.jadenxgamer.elysium_api.api.util.ResourceKeyRegistryHelper;
import net.jadenxgamer.elysium_api.impl.ElysiumRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

public class UseBehaviorImpl {

    public static void init(PlayerInteractEvent.RightClickBlock event) {
        if (Elysium.registryAccess == null) return;
        Level level = event.getLevel();
        BlockState state = level.getBlockState(event.getPos());
        Player player = event.getEntity();
        ItemStack stack = player.getItemInHand(event.getHand());

        Optional<UseBehavior> useBehavior = Elysium.registryAccess.registryOrThrow(ElysiumRegistries.BLOCK_USE_BEHAVIORS).stream()
                .filter(s -> s.blocks().contains(state.getBlockHolder()) && s.itemCondition().contains(stack.getItemHolder())).findFirst();

        if (level.isClientSide()) return;

        // returns if no registry was found
        if (useBehavior.isEmpty()) return;

        UseBehavior registry = useBehavior.get();
        BlockPos pos = getPosFromCodec(registry.behavior().pos(), registry.behavior().posOffset(), event);
        // returns if the current block in pos cannot be replaced
        if (placeRelated(registry) && !registry.behavior().canReplace() && !level.getBlockState(pos).canBeReplaced()) return;

        if (registry.blockstateCondition().isPresent()) {
            if (!registry.blockstateCondition().get().equals(state)) return;
        }

        if (!player.getAbilities().instabuild) {
            handleItemAfterUse(registry.behavior().afterUseItem(), stack, event);
        }

        if (registry.behavior().sounds().isPresent()) {
            level.playSound(null, event.getPos(), registry.behavior().sounds().get().soundEvent(), SoundSource.BLOCKS, registry.behavior().sounds().get().volume(), registry.behavior().sounds().get().pitch());
        }
        if (registry.behavior().particles().isPresent()) {
            spawnParticles((ServerLevel) level, pos, registry.behavior().particles().get().particleType(), registry.behavior().particles().get().count(), registry.behavior().particles().get().speed(), registry.behavior().particles().get().xOffset(), registry.behavior().particles().get().yOffset(), registry.behavior().particles().get().zOffset());
        }

        int chanceToFail = registry.chanceToFail();
        if (chanceToFail > 0) {
            if (level.random.nextInt(chanceToFail) != 0) {
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
                return;
            }
        }

        switch (registry.behavior().type()) {
            case PLACE -> placeBlock(level, pos, registry.behavior().block().get(), event);
            case PLACE_ITSELF -> placeBlock(level, pos, state, event);
            case DROP -> dropStack(level, pos, event.getFace(), registry.behavior().item().get(), registry.behavior().itemCount());
            case DROP_ITSELF -> dropStack(level, pos, event.getFace(), ForgeRegistries.BLOCKS.getKey(state.getBlock()), registry.behavior().itemCount());
            case FEATURE -> placeFeature(level, pos, registry.behavior().feature().get());
            case INSERT_STACK -> insertStack(player, registry.behavior().item().get(), registry.behavior().itemCount());
        }

        if (registry.behavior().breakParticles()) {
            level.levelEvent(2001, pos, Block.getId(state));
        }

        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }

    private static void placeBlock(Level level, BlockPos pos, BlockState state, PlayerInteractEvent.RightClickBlock event) {
        if (state != null && state.getBlock().canSurvive(level.getBlockState(pos), level, pos)) {
            level.setBlock(pos, state, Block.UPDATE_ALL);
        }
    }

    private static void dropStack(Level level, BlockPos pos, Direction direction, ResourceLocation location, int count) {
        Item item = ResourceKeyRegistryHelper.getItem(location);
        Block.popResourceFromFace(level, pos, direction, new ItemStack(item, count));
    }

    private static void insertStack(Player player, ResourceLocation location, int count) {
        Item item = ResourceKeyRegistryHelper.getItem(location);
        player.getInventory().add(new ItemStack(item, count));
    }

    private static void placeFeature(Level level, BlockPos pos, ResourceLocation location) {
        if (level instanceof ServerLevel serverLevel) {
            ResourceKey<ConfiguredFeature<?, ?>> featureKey = ResourceKey.create(Registries.CONFIGURED_FEATURE, location);
            serverLevel.registryAccess().registry(Registries.CONFIGURED_FEATURE).flatMap(registry -> registry.getHolder(featureKey)).ifPresent(holder -> holder.value()
                    .place(serverLevel, serverLevel.getChunkSource().getGenerator(), serverLevel.random, pos));
        }
    }

    private static void handleItemAfterUse(AfterUseItemEnum afterUse, ItemStack stack, PlayerInteractEvent.RightClickBlock event) {
        switch (afterUse) {
            case CONSUME -> stack.shrink(1);
            case DAMAGE -> stack.hurtAndBreak(1, event.getEntity(), p -> p.broadcastBreakEvent(event.getHand()));
        }
    }

    private static BlockPos getPosFromCodec(PosEnum pos, int offset, PlayerInteractEvent.RightClickBlock event) {
        BlockPos basePos = event.getPos();
        return switch (pos) {
            case ABOVE -> basePos.above(offset);
            case BELOW -> basePos.below(offset);
            case NORTH -> basePos.north(offset);
            case SOUTH -> basePos.south(offset);
            case EAST -> basePos.east(offset);
            case WEST -> basePos.west(offset);
            case RANDOM_HORIZONTAL -> {
                Direction randomDirection = Direction.Plane.HORIZONTAL.getRandomDirection(event.getLevel().random);
                yield basePos.relative(randomDirection);
            }
            default -> basePos;
        };
    }

    private static void spawnParticles(ServerLevel level, BlockPos pos, ResourceLocation location, int count, double speed, double xOffset, double yOffset, double zOffset) {
        ParticleType<?> particleType = ResourceKeyRegistryHelper.getParticleType(location);
        if (particleType instanceof SimpleParticleType simple) {
            level.sendParticles(simple, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, count, xOffset, yOffset, zOffset, speed);
        }
    }

    private static boolean placeRelated(UseBehavior registry) {
        UseBehaviorTypeEnum type = registry.behavior().type();
        return type == UseBehaviorTypeEnum.PLACE || type == UseBehaviorTypeEnum.PLACE_ITSELF || type == UseBehaviorTypeEnum.FEATURE;
    }
}
