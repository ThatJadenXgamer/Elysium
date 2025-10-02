package net.jadenxgamer.elysium_api.impl.core.datadriven.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.jadenxgamer.elysium_api.impl.core.datadriven.item.remainder_transformer.RemainderType;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public record RemainderTransformer(HolderSet<Item> items, RemainderType remainderType, ItemStack changeItem) {
    public static final Codec<RemainderTransformer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegistryCodecs.homogeneousList(Registries.ITEM).fieldOf("items").forGetter(RemainderTransformer::items),
            RemainderType.CODEC.fieldOf("remainder_type").forGetter(RemainderTransformer::remainderType),
            ItemStack.CODEC.optionalFieldOf("change_item", ItemStack.EMPTY).forGetter(RemainderTransformer::changeItem)
    ).apply(instance, RemainderTransformer::new));
}