package net.backslot.network;

import net.backslot.BackSlotMain;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SwapBackSlotPayload() implements CustomPacketPayload {
    public static final Type<SwapBackSlotPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(BackSlotMain.MOD_ID, "swap_back_slot"));

    public static final StreamCodec<FriendlyByteBuf, SwapBackSlotPayload> STREAM_CODEC =
            StreamCodec.unit(new SwapBackSlotPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}