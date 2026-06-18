package net.backslot.fabric.network;

import net.backslot.fabric.client.profile.TransformProfile;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public final class BackSlotPackets {
    public static final ResourceLocation SWAP_BACK = id("swap_back_slot");
    public static final ResourceLocation SWAP_BELT = id("swap_belt_slot");
    public static final ResourceLocation CLICK_BACK = id("click_back_slot");
    public static final ResourceLocation CLICK_BELT = id("click_belt_slot");
    public static final ResourceLocation SYNC_SLOT = id("sync_slot");
    public static final ResourceLocation UPDATE_TRANSFORM = id("update_transform");
    public static final ResourceLocation SYNC_TRANSFORM = id("sync_transform");

    private BackSlotPackets() {
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation("neobackslot", path);
    }

    public static void writeSlotSync(FriendlyByteBuf buffer, UUID playerId, ItemStack back, ItemStack belt) {
        buffer.writeUUID(playerId);
        buffer.writeItem(back);
        buffer.writeItem(belt);
    }

    public static SlotSync readSlotSync(FriendlyByteBuf buffer) {
        return new SlotSync(buffer.readUUID(), buffer.readItem(), buffer.readItem());
    }

    public static void writeTransformSync(FriendlyByteBuf buffer, UUID playerId, TransformProfile back, TransformProfile belt) {
        buffer.writeUUID(playerId);
        writeTransform(buffer, back);
        writeTransform(buffer, belt);
    }

    public static TransformSync readTransformSync(FriendlyByteBuf buffer) {
        return new TransformSync(buffer.readUUID(), readTransform(buffer), readTransform(buffer));
    }

    public static void writeTransform(FriendlyByteBuf buffer, TransformProfile profile) {
        buffer.writeDouble(profile.offsetX());
        buffer.writeDouble(profile.offsetY());
        buffer.writeDouble(profile.offsetZ());
        buffer.writeDouble(profile.rotationX());
        buffer.writeDouble(profile.rotationY());
        buffer.writeDouble(profile.rotationZ());
        buffer.writeDouble(profile.scale());
    }

    public static TransformProfile readTransform(FriendlyByteBuf buffer) {
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

    public record SlotSync(UUID playerId, ItemStack back, ItemStack belt) {
    }

    public record TransformSync(UUID playerId, TransformProfile back, TransformProfile belt) {
    }
}
