package net.jadenxgamer.elysium_api.impl.registry;

import com.mojang.serialization.MapCodec;
import net.jadenxgamer.elysium_api.Elysium;
import net.jadenxgamer.elysium_api.impl.core.item.PanoramaScreenshotItem;
import net.jadenxgamer.elysium_api.impl.core.misc.neoforge.EffectsBiomeModifier;
import net.jadenxgamer.elysium_api.impl.core.worldgen.feature.StructureStamp;
import net.jadenxgamer.elysium_api.impl.core.worldgen.structure.MultilayerJigsawStructure;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.GameMasterBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ElysiumItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, Elysium.MOD_ID);

    public static final Supplier<Item> PANORAMA_CAMERA = ITEMS.register("panorama_camera", () ->
            new PanoramaScreenshotItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)));

    public static final Supplier<Item> STRUCTURE_STAMP_ANCHOR = ITEMS.register("structure_stamp_anchor", () ->
            new GameMasterBlockItem(ElysiumBlocks.STRUCTURE_STAMP_ANCHOR.get(), new Item.Properties().rarity(Rarity.EPIC)));

    public static void init(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}