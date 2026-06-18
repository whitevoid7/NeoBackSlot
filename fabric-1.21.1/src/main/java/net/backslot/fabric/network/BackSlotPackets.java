package net.backslot.fabric.network;

import net.backslot.fabric.NeoBackSlotFabric;
import net.backslot.fabric.client.profile.TransformProfile;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public final class BackSlotPackets {
    private BackSlotPackets() {
    }

    public static void register() {
        PayloadTypeRegistry.playC2S().register(SwapBackSlotPayload.TYPE, SwapBackSlotPayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(SwapBeltSlotPayload.TYPE, SwapBeltSlotPayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(ClickBackSlotPayload.TYPE, ClickBackSlotPayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(ClickBeltSlotPayload.TYPE, ClickBeltSlotPayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(UpdateTransformPayload.TYPE, UpdateTransformPayload.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(SyncSlotPayload.TYPE, SyncSlotPayload.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(SyncTransformPayload.TYPE, SyncTransformPayload.STREAM_CODEC);
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(NeoBackSlotFabric.MOD_ID, path);
    }

    private static void writeTransform(FriendlyByteBuf buffer, TransformProfile profile) {
        buffer.writeDouble(profile.offsetX());
        buffer.writeDouble(profile.offsetY());
        buffer.writeDouble(profile.offsetZ());
        buffer.writeDouble(profile.rotationX());
        buffer.writeDouble(profile.rotationY());
        buffer.writeDouble(profile.rotationZ());
        buffer.writeDouble(profile.scale());
    }

    private static TransformProfile readTransform(FriendlyByteBuf buffer) {
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

    private static final StreamCodec<FriendlyByteBuf, TransformProfile> TRANSFORM_PROFILE_CODEC = new StreamCodec<>() {
        @Override
        public TransformProfile decode(FriendlyByteBuf buffer) {
            return readTransform(buffer);
        }

        @Override
        public void encode(FriendlyByteBuf buffer, TransformProfile profile) {
            writeTransform(buffer, profile);
        }
    };

    public record SwapBackSlotPayload() implements CustomPacketPayload {
        public static final Type<SwapBackSlotPayload> TYPE = new Type<>(id("swap_back_slot"));
        public static final StreamCodec<FriendlyByteBuf, SwapBackSlotPayload> STREAM_CODEC =
                StreamCodec.unit(new SwapBackSlotPayload());

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record SwapBeltSlotPayload() implements CustomPacketPayload {
        public static final Type<SwapBeltSlotPayload> TYPE = new Type<>(id("swap_belt_slot"));
        public static final StreamCodec<FriendlyByteBuf, SwapBeltSlotPayload> STREAM_CODEC =
                StreamCodec.unit(new SwapBeltSlotPayload());

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record ClickBackSlotPayload() implements CustomPacketPayload {
        public static final Type<ClickBackSlotPayload> TYPE = new Type<>(id("click_back_slot"));
        public static final StreamCodec<FriendlyByteBuf, ClickBackSlotPayload> STREAM_CODEC =
                StreamCodec.unit(new ClickBackSlotPayload());

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record ClickBeltSlotPayload() implements CustomPacketPayload {
        public static final Type<ClickBeltSlotPayload> TYPE = new Type<>(id("click_belt_slot"));
        public static final StreamCodec<FriendlyByteBuf, ClickBeltSlotPayload> STREAM_CODEC =
                StreamCodec.unit(new ClickBeltSlotPayload());

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record SyncSlotPayload(UUID playerId, ItemStack back, ItemStack belt) implements CustomPacketPayload {
        public static final Type<SyncSlotPayload> TYPE = new Type<>(id("sync_slot"));
        public static final StreamCodec<RegistryFriendlyByteBuf, SyncSlotPayload> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.STRING_UTF8.map(UUID::fromString, UUID::toString),
                        SyncSlotPayload::playerId,
                        ItemStack.OPTIONAL_STREAM_CODEC,
                        SyncSlotPayload::back,
                        ItemStack.OPTIONAL_STREAM_CODEC,
                        SyncSlotPayload::belt,
                        SyncSlotPayload::new
                );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record UpdateTransformPayload(TransformProfile back, TransformProfile belt) implements CustomPacketPayload {
        public static final Type<UpdateTransformPayload> TYPE = new Type<>(id("update_transform"));
        public static final StreamCodec<FriendlyByteBuf, UpdateTransformPayload> STREAM_CODEC =
                StreamCodec.composite(
                        TRANSFORM_PROFILE_CODEC,
                        UpdateTransformPayload::back,
                        TRANSFORM_PROFILE_CODEC,
                        UpdateTransformPayload::belt,
                        UpdateTransformPayload::new
                );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record SyncTransformPayload(UUID playerId, TransformProfile back, TransformProfile belt) implements CustomPacketPayload {
        public static final Type<SyncTransformPayload> TYPE = new Type<>(id("sync_transform"));
        public static final StreamCodec<FriendlyByteBuf, SyncTransformPayload> STREAM_CODEC = new StreamCodec<>() {
            @Override
            public SyncTransformPayload decode(FriendlyByteBuf buffer) {
                return new SyncTransformPayload(buffer.readUUID(), readTransform(buffer), readTransform(buffer));
            }

            @Override
            public void encode(FriendlyByteBuf buffer, SyncTransformPayload payload) {
                buffer.writeUUID(payload.playerId());
                writeTransform(buffer, payload.back());
                writeTransform(buffer, payload.belt());
            }
        };

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
