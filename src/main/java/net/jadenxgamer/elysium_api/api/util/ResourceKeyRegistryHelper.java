package net.jadenxgamer.elysium_api.api.util;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.ForgeRegistries;

@SuppressWarnings("deprecation")
public class ResourceKeyRegistryHelper {

    /**
     * Lets you easily retrieve Registries with ResourceLocation
     * @param id - the namespaced uniqueId of the registry
     * @return - returns the Registry if it exists. but might return null if it doesn't
     */

    public static Block getBlock(ResourceLocation id) {
        return BuiltInRegistries.BLOCK.get(id);
    }

    public static Item getItem(ResourceLocation id) {
        return BuiltInRegistries.ITEM.get(id);
    }

    public static EntityType<?> getEntityType(ResourceLocation id) {
        return BuiltInRegistries.ENTITY_TYPE.get(id);
    }

    public static BlockEntityType<?> getBlockEntityType(ResourceLocation id) {
        return BuiltInRegistries.BLOCK_ENTITY_TYPE.get(id);
    }

    public static ParticleType<?> getParticleType(ResourceLocation id) {
        return BuiltInRegistries.PARTICLE_TYPE.get(id);
    }

    public static SoundEvent getSoundEvent(ResourceLocation id) {
        return BuiltInRegistries.SOUND_EVENT.get(id);
    }

    public static Enchantment getEnchantment(ResourceLocation id) {
        return BuiltInRegistries.ENCHANTMENT.get(id);
    }

    public static MobEffect getMobEffect(ResourceLocation id) {
        return BuiltInRegistries.MOB_EFFECT.get(id);
    }

    public static Potion getPotion(ResourceLocation id) {
        return BuiltInRegistries.POTION.get(id);
    }

    public static Fluid getFluid(ResourceLocation id) {
        return BuiltInRegistries.FLUID.get(id);
    }

    public static FluidType getFluidType(ResourceLocation id) {
        return ForgeRegistries.FLUID_TYPES.get().getValue(id);
    }
}
