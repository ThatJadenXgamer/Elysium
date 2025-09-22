package net.jadenxgamer.elysium_api.api.reflection;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unchecked")
public final class ElysiumReflection {
    private static final Map<String, ConstructorInvoker> CACHE = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Constructor<?>[]> CONSTRUCTORS_CACHE = new ConcurrentHashMap<>();

    /**
     * Creates an instance of a {@link Block} subclass using reflection
     * <p>
     * This method dynamically instantiates a block from any mod using reflection to find and invoke the appropriate constructor,
     * This is particularly useful when you need to register blocks from different mods without direct compile-time dependencies.
     *
     * <p><b>Usage Example:</b>
     * <pre>{@code
     * Block lesionBlock = BlockReflection.createBlock(
     *     "net.jadenxgamer.netherexp.core.block.LesionBlock",
     *     () -> Items.ROTTEN_FLESH, BlockBehaviour.Properties.of().strength(2.0f)
     * );
     * }</pre>
     *
     * @param className the fully qualified class name of the block to make a reflection out of
     * @param args the constructor arguments to pass into the reflected block's constructor
     * @return a new instance of the specified block class
     * @param <T> the type of block to create (must extend Block)
     */
    public static <T extends Block> T createBlock(String className, Object... args) {
        return createInstance(Block.class, className, args);
    }

    /**
     * Creates an instance of an {@link Item} subclass using reflection
     * <p>
     * See {@link ElysiumReflection#createBlock} for usage example and explanation for reflection's use purpose
     *
     * @param className the fully qualified class name of the item to make a reflection out of
     * @param args the constructor arguments to pass into the reflected item's constructor
     * @return a new instance of the specified item class
     * @param <T> the type of item to create (must extend Item)
     */
    public static <T extends Item> T createItem(String className, Object... args) {
        return createInstance(Item.class, className, args);
    }

    /**
     * Creates an instance of an {@link MobEffect} subclass using reflection
     * <p>
     * See {@link ElysiumReflection#createBlock} for usage example and explanation for reflection's use purpose
     *
     * @param className the fully qualified class name of the effect to make a reflection out of
     * @param args the constructor arguments to pass into the reflected effect's constructor
     * @return a new instance of the specified effect class
     * @param <T> the type of item to create (must extend MobEffect)
     */
    public static <T extends MobEffect> T createMobEffect(String className, Object... args) {
        return createInstance(MobEffect.class, className, args);
    }

    /**
     * Generic method to create an instance of any class using reflection
     *
     * @param superType the super class type that the target class must extend/implement
     * @param className the fully qualified class name of the class to instantiate
     * @param args the constructor arguments to pass into the reflected class's constructor
     * @return a new instance of the specified class
     * @param <T> the type of object to create
     * @param <S> the super type that T must extend/implement
     */
    private static <T, S> T createInstance(Class<S> superType, String className, Object... args) {
        Class<?>[] providedTypes = buildTypesArray(args);
        String cacheKey = buildCacheKey(className, providedTypes);

        ConstructorInvoker invoker = CACHE.computeIfAbsent(cacheKey, key -> createInvoker(superType, className, providedTypes));

        return returnInstance(className, invoker, args);
    }

    private static Class<?>[] buildTypesArray(Object[] args) {
        Class<?>[] types = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            types[i] = args[i] != null ? args[i].getClass() : null;
        }
        return types;
    }

    private static String buildCacheKey(String className, Class<?>[] types) {
        StringBuilder keyBuilder = new StringBuilder(className).append('(');
        for (int i = 0; i < types.length; i++) {
            if (i > 0) keyBuilder.append(',');
            keyBuilder.append(types[i] != null ? types[i].getName() : "null");
        }
        return keyBuilder.append(')').toString();
    }

    private static <S> ConstructorInvoker createInvoker(Class<S> superType, String className, Class<?>[] providedTypes) {
        try {
            Class<? extends S> targetClass = Class.forName(className).asSubclass(superType);
            Constructor<? extends S> constructor = findCompatibleConstructor(targetClass, providedTypes);
            constructor.setAccessible(true);

            try {
                MethodHandle methodHandle = MethodHandles.lookup().unreflectConstructor(constructor);
                return new ConstructorInvoker(methodHandle);
            } catch (IllegalAccessException e) {
                return new ConstructorInvoker(constructor);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(superType.getSimpleName() + " class not found: " + className, e);
        }
    }

    private static <S> Constructor<? extends S> findCompatibleConstructor(Class<? extends S> targetClass, Class<?>[] providedTypes) {
        Constructor<?>[] constructors = CONSTRUCTORS_CACHE.computeIfAbsent(targetClass, Class::getDeclaredConstructors);

        return (Constructor<? extends S>) Arrays.stream(constructors)
                .filter(constructor -> constructor.getParameterCount() == providedTypes.length)
                .min(Comparator.comparingInt(c -> calculateCompatibilityScore(c.getParameterTypes(), providedTypes)))
                .orElseThrow(() -> createConstructorNotFoundException(targetClass, providedTypes, constructors));
    }

    private static <S> RuntimeException createConstructorNotFoundException(Class<? extends S> targetClass, Class<?>[] providedTypes, Constructor<?>[] availableConstructors) {
        StringBuilder errorMessage = new StringBuilder()
                .append("No compatible constructor found for ")
                .append(targetClass.getName())
                .append(" with parameter types: ")
                .append(Arrays.toString(providedTypes))
                .append("\nAvailable constructors:\n");

        for (Constructor<?> constructor : availableConstructors) {
            errorMessage.append("  -")
                    .append(Arrays.toString(constructor.getParameterTypes()))
                    .append("\n");
        }

        return new RuntimeException(errorMessage.toString());
    }

    private static int calculateCompatibilityScore(Class<?>[] paramTypes, Class<?>[] providedTypes) {
        int score = 0;
        for (int i = 0; i < paramTypes.length; i++) {
            int paramScore = getTypeCompatibility(paramTypes[i], providedTypes[i]);
            if (paramScore < 0) return Integer.MAX_VALUE;
            score += paramScore;
        }
        return score;
    }

    private static int getTypeCompatibility(Class<?> expected, Class<?> actual) {
        if (actual == null) return expected.isPrimitive() ? -1 : 0;
        if (expected.equals(actual)) return 0;
        if (expected.isAssignableFrom(actual)) return 2;

        // Handle primitive/boxed type compatibility
        if (PrimitiveTypes.isWrapper(expected, actual)) return 1;

        return -1;
    }

    private static <T> T returnInstance(String className, ConstructorInvoker invoker, Object[] args) {
        try {
            return (T) invoker.invoke(args);
        } catch (Throwable t) {
            throw new RuntimeException("Failed to instantiate class: " + className, t);
        }
    }

    private static final class ConstructorInvoker {
        private final MethodHandle methodHandle;
        private final Constructor<?> constructor;

        ConstructorInvoker(MethodHandle methodHandle) {
            this.methodHandle = methodHandle;
            this.constructor = null;
        }

        ConstructorInvoker(Constructor<?> constructor) {
            this.constructor = constructor;
            this.methodHandle = null;
        }

        Object invoke(Object[] args) throws Throwable {
            if (methodHandle != null) {
                return methodHandle.invokeWithArguments(args);
            }
            return constructor.newInstance(args);
        }
    }

    private static final class PrimitiveTypes {
        private static final Map<Class<?>, Class<?>> WRAPPER_TO_PRIMITIVE = Map.of(
                Boolean.class, boolean.class,
                Byte.class, byte.class,
                Character.class, char.class,
                Short.class, short.class,
                Integer.class, int.class,
                Long.class, long.class,
                Float.class, float.class,
                Double.class, double.class
        );

        private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER = Map.of(
                boolean.class, Boolean.class,
                byte.class, Byte.class,
                char.class, Character.class,
                short.class, Short.class,
                int.class, Integer.class,
                long.class, Long.class,
                float.class, Float.class,
                double.class, Double.class
        );

        static boolean isWrapper(Class<?> type1, Class<?> type2) {
            return WRAPPER_TO_PRIMITIVE.get(type1) == type2 || PRIMITIVE_TO_WRAPPER.get(type1) == type2;
        }
    }
}