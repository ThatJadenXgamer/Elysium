package net.jadenxgamer.elysium_api.impl.registry;

import net.jadenxgamer.elysium_api.ElysiumAPI;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ElysiumBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, ElysiumAPI.MOD_ID);

    public static final Supplier<Block> STRUCTURE_STAMP_ANCHOR = BLOCKS.register("structure_stamp_anchor", () ->
            new Block(new BlockBehaviour.Properties().mapColor(MapColor.COLOR_LIGHT_GRAY).requiresCorrectToolForDrops().strength(-1.0f, 3600000.0F).noLootTable()));

    public static void init(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}