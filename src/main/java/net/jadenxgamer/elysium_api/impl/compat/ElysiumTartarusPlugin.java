package net.jadenxgamer.elysium_api.impl.compat;

import net.jadenxgamer.elysium_api.api.reflection.ElysiumReflection;
import net.jadenxgamer.elysium_api.tartarus_scripting.scripting.TartarusScriptManager;
import net.jadenxgamer.elysium_api.tartarus_scripting.api.plugin.TartarusExposure;
import net.jadenxgamer.elysium_api.tartarus_scripting.api.plugin.TartarusPlugin;
import net.jadenxgamer.elysium_api.tartarus_scripting.callers.IncludeFromPackFunction;
import net.jadenxgamer.elysium_api.tartarus_scripting.callers.LoadFromPackFunction;
import net.jadenxgamer.elysium_api.tartarus_scripting.scripting.objects.block.TartarusBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.util.RandomSource;

@TartarusPlugin
public class ElysiumTartarusPlugin implements TartarusExposure {

    @Override
    public void registerExposures(TartarusExposure tartarus) {
        // Core Elysium API & TartarusScripting Utilities
        tartarus.exposeClass("ElysiumReflection", ElysiumReflection.class);
        tartarus.exposeObject("TartarusRegistry", TartarusScriptManager.getTartarusRegistry());

        // Common Block Classes
        tartarus.exposeClass("Block", Block.class);
        tartarus.exposeClass("SlabBlock", SlabBlock.class);
        tartarus.exposeClass("StairBlock", StairBlock.class);
        tartarus.exposeClass("WallBlock", WallBlock.class);
        tartarus.exposeClass("FenceBlock", FenceBlock.class);
        tartarus.exposeClass("FallingBlock", FallingBlock.class);

        // Common Item Classes
        tartarus.exposeClass("Item", Item.class);
        tartarus.exposeClass("BlockItem", BlockItem.class);
        tartarus.exposeClass("SwordItem", SwordItem.class);
        tartarus.exposeClass("PickaxeItem", PickaxeItem.class);
        tartarus.exposeClass("AxeItem", AxeItem.class);
        tartarus.exposeClass("ShovelItem", ShovelItem.class);
        tartarus.exposeClass("HoeItem", HoeItem.class);
        tartarus.exposeClass("ArmorItem", ArmorItem.class);
        tartarus.exposeClass("BowItem", BowItem.class);
        tartarus.exposeClass("SpawnEggItem", SpawnEggItem.class);
        tartarus.exposeClass("BucketItem", BucketItem.class);

        // Property Builders
        tartarus.exposeClass("BlockBehaviour", BlockBehaviour.class);
        tartarus.exposeClass("BlockBehaviour$Properties", BlockBehaviour.Properties.class);
        tartarus.exposeClass("Item$Properties", Item.Properties.class);

        // Block Helpers
        tartarus.exposeClass("MapColor", MapColor.class);
        tartarus.exposeClass("PushReaction", PushReaction.class);
        tartarus.exposeClass("SoundType", SoundType.class);
        tartarus.exposeClass("NoteBlockInstrument", NoteBlockInstrument.class);
        tartarus.exposeClass("BlockSetType", BlockSetType.class);
        tartarus.exposeClass("WoodType", WoodType.class);
        tartarus.exposeClass("DyeColor", DyeColor.class);

        // Item Helpers
        tartarus.exposeClass("Foods", Foods.class);
        tartarus.exposeClass("FoodProperties", FoodProperties.class);
        tartarus.exposeClass("FoodProperties$Builder", FoodProperties.Builder.class);
        tartarus.exposeClass("Rarity", Rarity.class);
        tartarus.exposeClass("Tiers", Tiers.class);
        tartarus.exposeClass("ArmorMaterials", ArmorMaterials.class);

        // The Custom Scripting Block
        tartarus.exposeClass("TartarusBlock", TartarusBlock.class);

        // Level & Spatial Contexts
        tartarus.exposeClass("BlockPos", BlockPos.class);
        tartarus.exposeClass("Direction", Direction.class);
        tartarus.exposeClass("Level", Level.class);
        tartarus.exposeClass("ServerLevel", ServerLevel.class);
        tartarus.exposeClass("LevelReader", LevelReader.class);
        tartarus.exposeClass("LevelAccessor", LevelAccessor.class);
        tartarus.exposeClass("BlockGetter", BlockGetter.class);


        // Blockstates & Properties
        tartarus.exposeClass("BlockState", BlockState.class);
        tartarus.exposeClass("BooleanProperty", BooleanProperty.class);
        tartarus.exposeClass("IntegerProperty", IntegerProperty.class);
        tartarus.exposeClass("EnumProperty", EnumProperty.class);

        // Interaction
        tartarus.exposeClass("InteractionResult", InteractionResult.class);
        tartarus.exposeClass("ItemInteractionResult", ItemInteractionResult.class);
        tartarus.exposeClass("InteractionHand", InteractionHand.class);
        tartarus.exposeClass("Player", Player.class);
        tartarus.exposeClass("BlockHitResult", BlockHitResult.class);

        // Entities
        tartarus.exposeClass("Entity", Entity.class);
        tartarus.exposeClass("LivingEntity", LivingEntity.class);
        tartarus.exposeClass("Projectile", Projectile.class);

        // Items & fluids
        tartarus.exposeClass("Fluid", Fluid.class);

        // Block entity
        tartarus.exposeClass("BlockEntity", BlockEntity.class);

        // Shapes & collision
        tartarus.exposeClass("Shapes", Shapes.class);
        tartarus.exposeClass("VoxelShape", VoxelShape.class);
        tartarus.exposeClass("CollisionContext", CollisionContext.class);

        // Other
        tartarus.exposeClass("Explosion", Explosion.class);
        tartarus.exposeClass("Component", Component.class);
        tartarus.exposeClass("ItemStack", ItemStack.class);
        tartarus.exposeClass("RandomSource", RandomSource.class);

        // Vanilla Registry Classes
        tartarus.exposeClass("ParticleTypes", ParticleTypes.class);
        tartarus.exposeClass("Items", Items.class);
        tartarus.exposeClass("Blocks", Blocks.class);
    }
}