package net.jadenxgamer.elysium_api.impl.misc_registry;

import net.jadenxgamer.elysium_api.Elysium;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;

public class ElysiumTags {

    public static class Blocks {
        public static final TagKey<Block> NON_SOLID_FIRE_SUPPORT = createBlockTag("non_solid_fire_support"); // blocks in this tag can have soul fire lit on faces that are not solid
        public static final TagKey<Block> ROOTS_PLANTABLE_ON = createBlockTag("roots_plantable_on"); // Nether Roots are Plantable on these Blocks
        public static final TagKey<Block> NETHER_SPROUTS_PLANTABLE_ON = createBlockTag("nether_sprouts_plantable_on"); // Nether Sprouts are Plantable on these Blocks
        public static final TagKey<Block> NETHER_WART_PLANTABLE_ON = createBlockTag("nether_wart_plantable_on"); // Nether Warts are Plantable on these Blocks

        private static TagKey<Block> createBlockTag(String name) {
            return TagKey.create(Registries.BLOCK, new ResourceLocation(Elysium.MOD_ID, name));
        }
    }

    public static class EntityTypes {
        public static final TagKey<EntityType<?>> PIGLINS_AFRAID_OF = createEntityTypeTag("piglins_afraid_of"); // Piglins will flee from mobs in this tag (this use to be in vanilla but mojang removed it for some reason????)

        private static TagKey<EntityType<?>> createEntityTypeTag(String name) {
            return TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(Elysium.MOD_ID, name));
        }
    }

    public static class DamageTypes {
        public static final TagKey<DamageType> CANT_DAMAGE_ARMOR = createDamageTypeTag("cant_damage_armor"); // DamageTypes in this tag won't take durability away from armor

        private static TagKey<DamageType> createDamageTypeTag(String name) {
            return TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Elysium.MOD_ID, name));
        }
    }
}
