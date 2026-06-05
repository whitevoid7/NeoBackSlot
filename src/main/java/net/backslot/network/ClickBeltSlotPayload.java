package net.backslot.network;

import net.backslot.BackSlotMain;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ClickBeltSlotPayload() implements CustomPacketPayload {
    public static final Type<ClickBeltSlotPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(BackSlotMain.MOD_ID, "click_belt_slot"));

    public static final StreamCodec<FriendlyByteBuf, ClickBeltSlotPayload> STREAM_CODEC =
            StreamCodec.unit(new ClickBeltSlotPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}