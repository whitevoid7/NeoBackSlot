package net.backslot.fabric.network;

import net.backslot.fabric.client.profile.TransformProfile;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ServerTransformSync {
    private static final Map<UUID, ActiveTransforms> ACTIVE_TRANSFORMS = new HashMap<>();

    private ServerTransformSync() {
    }

    public static void update(ServerPlayer player, TransformProfile backTransform, TransformProfile beltTransform) {
        ActiveTransforms transforms = new ActiveTransforms(backTransform, beltTransform);
        ACTIVE_TRANSFORMS.put(player.getUUID(), transforms);
        broadcast(player, transforms);
    }

    public static void syncKnownTransformsTo(ServerPlayer player) {
        for (Map.Entry<UUID, ActiveTransforms> entry : ACTIVE_TRANSFORMS.entrySet()) {
            ActiveTransforms transforms = entry.getValue();
            send(player, entry.getKey(), transforms);
        }
    }

    public static void remove(UUID playerId) {
        ACTIVE_TRANSFORMS.remove(playerId);
    }

    private static void broadcast(ServerPlayer player, ActiveTransforms transforms) {
        send(player, player.getUUID(), transforms);
        for (ServerPlayer viewer : PlayerLookup.tracking(player)) {
            send(viewer, player.getUUID(), transforms);
        }
    }

    private static void send(ServerPlayer player, UUID ownerId, ActiveTransforms transforms) {
        ServerPlayNetworking.send(player, new BackSlotPackets.SyncTransformPayload(ownerId, transforms.backTransform(), transforms.beltTransform()));
    }

    private record ActiveTransforms(TransformProfile backTransform, TransformProfile beltTransform) {
    }
}
