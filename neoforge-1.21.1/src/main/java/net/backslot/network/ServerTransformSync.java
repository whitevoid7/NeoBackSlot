package net.backslot.network;

import net.backslot.client.profile.TransformProfile;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

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
            PacketDistributor.sendToPlayer(player, new SyncTransformPayload(
                    entry.getKey(),
                    transforms.backTransform(),
                    transforms.beltTransform()
            ));
        }
    }

    public static void remove(UUID playerId) {
        ACTIVE_TRANSFORMS.remove(playerId);
    }

    private static void broadcast(ServerPlayer player, ActiveTransforms transforms) {
        SyncTransformPayload payload = new SyncTransformPayload(
                player.getUUID(),
                transforms.backTransform(),
                transforms.beltTransform()
        );

        PacketDistributor.sendToPlayer(player, payload);
        PacketDistributor.sendToPlayersTrackingEntity(player, payload);
    }

    private record ActiveTransforms(TransformProfile backTransform, TransformProfile beltTransform) {
    }
}
