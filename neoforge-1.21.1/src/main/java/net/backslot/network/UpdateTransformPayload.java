package net.backslot.network;

import net.backslot.BackSlotMain;
import net.backslot.client.profile.TransformProfile;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record UpdateTransformPayload(TransformProfile backTransform, TransformProfile beltTransform) implements CustomPacketPayload {
    public static final Type<UpdateTransformPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(BackSlotMain.MOD_ID, "update_transform"));

    public static final StreamCodec<FriendlyByteBuf, UpdateTransformPayload> STREAM_CODEC =
            StreamCodec.composite(
                    TransformPayloadCodecs.TRANSFORM_PROFILE,
                    UpdateTransformPayload::backTransform,
                    TransformPayloadCodecs.TRANSFORM_PROFILE,
                    UpdateTransformPayload::beltTransform,
                    UpdateTransformPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
