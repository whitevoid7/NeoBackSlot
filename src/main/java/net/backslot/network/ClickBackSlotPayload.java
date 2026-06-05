package net.backslot.network;

import net.backslot.BackSlotMain;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ClickBackSlotPayload() implements CustomPacketPayload {
    public static final Type<ClickBackSlotPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(BackSlotMain.MOD_ID, "click_back_slot"));

    public static final StreamCodec<FriendlyByteBuf, ClickBackSlotPayload> STREAM_CODEC =
            StreamCodec.unit(new ClickBackSlotPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}