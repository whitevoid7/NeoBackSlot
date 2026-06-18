package net.backslot.network;

import net.backslot.client.profile.TransformProfile;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

final class TransformPayloadCodecs {
    static final StreamCodec<FriendlyByteBuf, TransformProfile> TRANSFORM_PROFILE = new StreamCodec<>() {
        @Override
        public TransformProfile decode(FriendlyByteBuf buffer) {
            return new TransformProfile(
                    buffer.readDouble(),
                    buffer.readDouble(),
                    buffer.readDouble(),
                    buffer.readDouble(),
                    buffer.readDouble(),
                    buffer.readDouble(),
                    buffer.readDouble()
            );
        }

        @Override
        public void encode(FriendlyByteBuf buffer, TransformProfile profile) {
            buffer.writeDouble(profile.offsetX());
            buffer.writeDouble(profile.offsetY());
            buffer.writeDouble(profile.offsetZ());
            buffer.writeDouble(profile.rotationX());
            buffer.writeDouble(profile.rotationY());
            buffer.writeDouble(profile.rotationZ());
            buffer.writeDouble(profile.scale());
        }
    };

    private TransformPayloadCodecs() {
    }
}
