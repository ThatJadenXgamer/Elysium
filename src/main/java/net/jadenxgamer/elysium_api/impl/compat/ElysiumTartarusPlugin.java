package net.jadenxgamer.elysium_api.impl.compat;

import net.jadenxgamer.elysium_api.api.reflection.ElysiumReflection;
import net.jadenxgamer.elysium_api.tartarus_scripting.scripting.TartarusScriptManager;
import net.jadenxgamer.elysium_api.tartarus_scripting.api.plugin.TartarusExposure;
import net.jadenxgamer.elysium_api.tartarus_scripting.api.plugin.TartarusPlugin;
import net.jadenxgamer.elysium_api.tartarus_scripting.callers.IncludeFromPackFunction;
import net.jadenxgamer.elysium_api.tartarus_scripting.callers.LoadFromPackFunction;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

@TartarusPlugin
public class ElysiumTartarusPlugin implements TartarusExposure {

    @Override
    public void registerExposures(TartarusExposure tartarus) {
        // Core Elysium API utilities
        tartarus.exposeClass("ElysiumReflection", ElysiumReflection.class);
        tartarus.exposeObject("TartarusRegistry", TartarusScriptManager.getTartarusRegistry());

        // Common block classes
        tartarus.exposeClass("Block", Block.class);
        tartarus.exposeClass("SlabBlock", SlabBlock.class);
        tartarus.exposeClass("StairBlock", StairBlock.class);
        tartarus.exposeClass("WallBlock", WallBlock.class);
        tartarus.exposeClass("FenceBlock", FenceBlock.class);
        tartarus.exposeClass("FallingBlock", FallingBlock.class);

        // Common item classes
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

        // Property builders
        tartarus.exposeClass("BlockBehaviour", BlockBehaviour.class);
        tartarus.exposeClass("BlockBehaviour$Properties", BlockBehaviour.Properties.class);
        tartarus.exposeClass("Item$Properties", Item.Properties.class);

        // Block helpers & enums
        tartarus.exposeClass("MapColor", MapColor.class);
        tartarus.exposeClass("PushReaction", PushReaction.class);
        tartarus.exposeClass("SoundType", SoundType.class);
        tartarus.exposeClass("NoteBlockInstrument", NoteBlockInstrument.class);
        tartarus.exposeClass("BlockSetType", BlockSetType.class);
        tartarus.exposeClass("WoodType", WoodType.class);
        tartarus.exposeClass("DyeColor", DyeColor.class);

        // Item helpers
        tartarus.exposeClass("Foods", Foods.class);
        tartarus.exposeClass("FoodProperties", FoodProperties.class);
        tartarus.exposeClass("FoodProperties$Builder", FoodProperties.Builder.class);
        tartarus.exposeClass("Rarity", Rarity.class);
        tartarus.exposeClass("Tiers", Tiers.class);
        tartarus.exposeClass("ArmorMaterials", ArmorMaterials.class);

        // Cross‑pack script functions
        tartarus.exposeObject("loadFromPack", new LoadFromPackFunction());
        tartarus.exposeObject("includeFromPack", new IncludeFromPackFunction());
    }
}
