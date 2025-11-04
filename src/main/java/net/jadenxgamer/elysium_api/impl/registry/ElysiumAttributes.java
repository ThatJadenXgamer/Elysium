package net.jadenxgamer.elysium_api.impl.registry;

import net.jadenxgamer.elysium_api.Elysium;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ElysiumAttributes {

    public static final DeferredRegister<Attribute> ATTRIBUTES =DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, Elysium.MOD_ID);

    public static final Holder<Attribute> DODGE_POWER = ATTRIBUTES.register(
            "dodge_power", () -> new RangedAttribute(
                    "attributes.elysium.dodge_power",
                    1.7d,
                    0,
                    10
            ).setSyncable(true));

    public static void init(IEventBus eventBus) {
        ATTRIBUTES.register(eventBus);
    }

}
