package net.jadenxgamer.elysium_api.impl.registry;

import net.jadenxgamer.elysium_api.ElysiumAPI;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ElysiumAttributes {

    public static final DeferredRegister<Attribute> ATTRIBUTES =DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, ElysiumAPI.MOD_ID);

    public static void init(IEventBus eventBus) {
        ATTRIBUTES.register(eventBus);
    }
}
