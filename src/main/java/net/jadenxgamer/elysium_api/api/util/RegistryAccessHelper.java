package net.jadenxgamer.elysium_api.api.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.RegistryAccess;
import net.neoforged.fml.loading.FMLEnvironment;

import java.util.Optional;

public class RegistryAccessHelper {

    private static RegistryAccess serverRegAccess;

    public static void updateAccess(RegistryAccess instance) {
        serverRegAccess = instance;
    }

    public static Optional<RegistryAccess> getServerAccess() {
        return Optional.of(serverRegAccess);
    }

    public static Optional<RegistryAccess> getClientAccess() {
        if (!FMLEnvironment.dist.isClient()) return Optional.empty();
        return Optional.ofNullable(Minecraft.getInstance().getConnection()).map(ClientPacketListener::registryAccess);
    }

    public static boolean hasAccess() {
        return serverRegAccess != null;
    }
}
