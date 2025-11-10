package net.jadenxgamer.elysium_api.api.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

import java.util.Optional;
import java.util.function.Supplier;

public class RegistryAccessHelper {

    private static Supplier<RegistryAccess> accessSupplier;

    public static void updateServer(MinecraftServer server) {
        accessSupplier = server::registryAccess;
    }

    public static Optional<RegistryAccess> getServerAccess() {
        return Optional.ofNullable(accessSupplier.get());
    }

    public static Optional<RegistryAccess> getClientAccess() {
        return Optional.ofNullable(Minecraft.getInstance().getConnection()).map(ClientPacketListener::registryAccess);
    }

    public static Optional<RegistryAccess> getAccess() {
        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) return getServerAccess();
        return getServerAccess().or(RegistryAccessHelper::getClientAccess);
    }

    public static RegistryAccess getServerAccessOrThrow() {
        return getServerAccess().orElseThrow();
    }

    public static RegistryAccess getClientAccessOrThrow() {
        return getClientAccess().orElseThrow();
    }

    public static RegistryAccess getAccessOrThrow() {
        return getAccess().orElseThrow();
    }


    public static boolean hasServerAccess() {
        return getServerAccess().isPresent();
    }

    public static boolean hasClientAccess() {
        return getClientAccess().isPresent();
    }

    public static boolean hasAccess() {
        return getAccess().isPresent();
    }

}
