package net.jadenxgamer.elysium_api.impl.core.datadriven.item.remainder_transformer;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum RemainderType implements StringRepresentable {
    NONE("none"),
    CHANGE_ITEM("change_item"),
    NON_CONSUMABLE("non_consumable");

    private final String name;
    public static final StringRepresentableCodec<RemainderType> CODEC = StringRepresentable.fromEnum(RemainderType::values);

    RemainderType(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    @Override
    public @NotNull String getSerializedName() {
        return name;
    }
}
