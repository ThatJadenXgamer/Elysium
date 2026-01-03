package net.jadenxgamer.elysium_api.api.util;

import net.minecraft.core.RegistryAccess;

import java.util.Optional;

public class RegistryAccessHelper {

    private static Optional<RegistryAccess> registryAccess;

    public static RegistryAccess getAccessOrThrow() {
        if (!isRegistryAccessible()) return null;
        return registryAccess.get();
    }

    public static void updateAccess(RegistryAccess instance) {
        registryAccess = Optional.of(instance);
    }

    public static boolean isRegistryAccessible() {
        return registryAccess.isPresent();
    }
}
