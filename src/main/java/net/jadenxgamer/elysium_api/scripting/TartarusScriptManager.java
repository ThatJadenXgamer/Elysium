package net.jadenxgamer.elysium_api.scripting;

import net.jadenxgamer.elysium_api.api.reflection.ElysiumReflection;
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
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaClass;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import java.util.HashSet;
import java.util.Set;

public class TartarusScriptManager {
    private static Scriptable globalScope;
    private static final TartarusRegistryBridge TARTARUS_REGISTRY = new TartarusRegistryBridge();
    private static final Set<String> ALLOWED_CLASSES = new HashSet<>();

    static {
        ALLOWED_CLASSES.add(TartarusRegistryBridge.class.getName());
    }


    public static void initializeEngine() {
        Context cx = Context.enter();
        try {
            cx.setInterpretedMode(true);
            cx.setClassShutter(ALLOWED_CLASSES::contains);
            globalScope = cx.initSafeStandardObjects();

            deleteJavaPackages(globalScope);
            javaAPIExposures();
        } finally {
            Context.exit();
        }
    }

    public static void javaAPIExposures() {
        exposeClass("ElysiumReflection", ElysiumReflection.class);
        ScriptableObject.putProperty(globalScope, "TartarusRegistry", Context.javaToJS(TARTARUS_REGISTRY, globalScope));

        // Common Block Classes
        exposeClass("Block", Block.class);
        exposeClass("SlabBlock", SlabBlock.class);
        exposeClass("StairBlock", StairBlock.class);
        exposeClass("WallBlock", WallBlock.class);
        exposeClass("FenceBlock", FenceBlock.class);
        exposeClass("FallingBlock", FallingBlock.class);

        // Common Item Classes
        exposeClass("Item", Item.class);
        exposeClass("BlockItem", BlockItem.class);
        exposeClass("SwordItem", SwordItem.class);
        exposeClass("PickaxeItem", PickaxeItem.class);
        exposeClass("AxeItem", AxeItem.class);
        exposeClass("ShovelItem", ShovelItem.class);
        exposeClass("HoeItem", HoeItem.class);
        exposeClass("ArmorItem", BucketItem.class);
        exposeClass("BowItem", BowItem.class);
        exposeClass("SpawnEggItem", SpawnEggItem.class);
        exposeClass("BucketItem", BucketItem.class);

        // Properties
        exposeClass("BlockBehaviour", BlockBehaviour.class);
        exposeClass("BlockBehaviour$Properties", BlockBehaviour.Properties.class);
        exposeClass("Item$Properties", Item.Properties.class);

        // Block helpers
        exposeClass("MapColor", MapColor.class);
        exposeClass("PushReaction", PushReaction.class);
        exposeClass("SoundType", SoundType.class);
        exposeClass("NoteBlockInstrument", NoteBlockInstrument.class);
        exposeClass("BlockSetType", BlockSetType.class);
        exposeClass("WoodType", WoodType.class);
        exposeClass("DyeColor", DyeColor.class);

        // Item Helpers
        exposeClass("Foods", Foods.class);
        exposeClass("FoodProperties", FoodProperties.class);
        exposeClass("FoodProperties$Builder", FoodProperties.Builder.class);
        exposeClass("Rarity", Rarity.class);
        exposeClass("Tiers", Tiers.class);
        exposeClass("ArmorMaterials", ArmorMaterials.class);
    }

    private static void exposeClass(String jsName, Class<?> clazz) {
        ALLOWED_CLASSES.add(clazz.getName());
        if (clazz.getCanonicalName() != null) ALLOWED_CLASSES.add(clazz.getCanonicalName());

        ScriptableObject.putProperty(globalScope, jsName, new NativeJavaClass(globalScope, clazz));
    }

    private static void deleteJavaPackages(Scriptable scope) {
        String[] dangerousProps = { "Java", "Packages", "java", "javax", "org", "com", "edu", "net" };
        for (String prop : dangerousProps) {
            ScriptableObject.deleteProperty(scope, prop);
        }
    }

    public static Scriptable getGlobalScope() {
        return globalScope;
    }

    public static TartarusRegistryBridge getTartarusRegistry() {
        return TARTARUS_REGISTRY;
    }
}