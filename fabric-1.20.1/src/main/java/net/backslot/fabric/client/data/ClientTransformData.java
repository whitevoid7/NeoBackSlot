package net.backslot.fabric.client.data;

import net.backslot.fabric.client.profile.TransformProfile;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class ClientTransformData {
    private static final Map<UUID, TransformProfile> BACK_TRANSFORMS = new HashMap<>();
    private static final Map<UUID, TransformProfile> BELT_TRANSFORMS = new HashMap<>();

    private ClientTransformData() {
    }

    public static Optional<TransformProfile> getBackTransform(UUID playerId) {
        return Optional.ofNullable(BACK_TRANSFORMS.get(playerId));
    }

    public static Optional<TransformProfile> getBeltTransform(UUID playerId) {
        return Optional.ofNullable(BELT_TRANSFORMS.get(playerId));
    }

    public static void setBoth(UUID playerId, TransformProfile backTransform, TransformProfile beltTransform) {
        BACK_TRANSFORMS.put(playerId, backTransform);
        BELT_TRANSFORMS.put(playerId, beltTransform);
    }

    public static void remove(UUID playerId) {
        BACK_TRANSFORMS.remove(playerId);
        BELT_TRANSFORMS.remove(playerId);
    }
}
