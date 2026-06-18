package net.backslot.network;

import net.backslot.BackSlotMain;
import net.backslot.client.profile.TransformProfile;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record SyncTransformPayload(UUID playerId, TransformProfile backTransform, TransformProfile beltTransform) implements CustomPacketPayload {
    public static final Type<SyncTransformPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(BackSlotMain.MOD_ID, "sync_transform"));

    public static final StreamCodec<FriendlyByteBuf, SyncTransformPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public SyncTransformPayload decode(FriendlyByteBuf buffer) {
            UUID playerId = buffer.readUUID();
            TransformProfile backTransform = TransformPayloadCodecs.TRANSFORM_PROFILE.decode(buffer);
            TransformProfile beltTransform = TransformPayloadCodecs.TRANSFORM_PROFILE.decode(buffer);
            return new SyncTransformPayload(playerId, backTransform, beltTransform);
        }

        @Override
        public void encode(FriendlyByteBuf buffer, SyncTransformPayload payload) {
            buffer.writeUUID(payload.playerId());
            TransformPayloadCodecs.TRANSFORM_PROFILE.encode(buffer, payload.backTransform());
            TransformPayloadCodecs.TRANSFORM_PROFILE.encode(buffer, payload.beltTransform());
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
