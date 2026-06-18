package net.backslot.network;

import net.backslot.BackSlotMain;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public record SyncBackSlotPayload(UUID playerId, ItemStack backSlot, ItemStack beltSlot) implements CustomPacketPayload {
    public static final Type<SyncBackSlotPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(BackSlotMain.MOD_ID, "sync_back_slot"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncBackSlotPayload> STREAM_CODEC =
            StreamCodec.composite(
                    net.minecraft.network.codec.ByteBufCodecs.STRING_UTF8.map(UUID::fromString, UUID::toString),
                    SyncBackSlotPayload::playerId,
                    ItemStack.OPTIONAL_STREAM_CODEC,
                    SyncBackSlotPayload::backSlot,
                    ItemStack.OPTIONAL_STREAM_CODEC,
                    SyncBackSlotPayload::beltSlot,
                    SyncBackSlotPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}