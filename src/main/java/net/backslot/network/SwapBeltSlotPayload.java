package net.backslot.network;

import net.backslot.BackSlotMain;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SwapBeltSlotPayload() implements CustomPacketPayload {
    public static final Type<SwapBeltSlotPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(BackSlotMain.MOD_ID, "swap_belt_slot"));

    public static final StreamCodec<FriendlyByteBuf, SwapBeltSlotPayload> STREAM_CODEC =
            StreamCodec.unit(new SwapBeltSlotPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}