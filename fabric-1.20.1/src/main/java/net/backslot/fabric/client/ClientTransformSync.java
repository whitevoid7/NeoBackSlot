package net.backslot.fabric.client;

import net.backslot.fabric.client.data.ClientBackSlotData;
import net.backslot.fabric.client.data.ClientTransformData;
import net.backslot.fabric.client.profile.TransformProfile;
import net.backslot.fabric.client.profile.TransformProfileManager;
import net.backslot.fabric.config.BackSlotClientConfig;
import net.backslot.fabric.network.BackSlotPackets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public final class ClientTransformSync {
    private ClientTransformSync() {
    }

    public static void sendActiveTransformsToServer() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.getConnection() == null) {
            return;
        }

        TransformProfile back = getActiveBackTransform();
        TransformProfile belt = getActiveBeltTransform();
        ClientTransformData.setBoth(minecraft.player.getUUID(), back, belt);

        FriendlyByteBuf buffer = new FriendlyByteBuf(io.netty.buffer.Unpooled.buffer());
        BackSlotPackets.writeTransform(buffer, back);
        BackSlotPackets.writeTransform(buffer, belt);
        ClientPlayNetworking.send(BackSlotPackets.UPDATE_TRANSFORM, buffer);
    }

    public static TransformProfile getActiveBackTransform() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null) {
            ItemStack stack = ClientBackSlotData.getBackSlot(minecraft.player.getUUID());
            if (!stack.isEmpty()) {
                return TransformProfileManager.getProfile(stack, TransformProfileManager.Slot.BACK)
                        .orElseGet(ClientTransformSync::getGlobalBackTransform);
            }
        }
        return getGlobalBackTransform();
    }

    public static TransformProfile getActiveBeltTransform() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null) {
            ItemStack stack = ClientBackSlotData.getBeltSlot(minecraft.player.getUUID());
            if (!stack.isEmpty()) {
                return TransformProfileManager.getProfile(stack, TransformProfileManager.Slot.BELT)
                        .orElseGet(ClientTransformSync::getGlobalBeltTransform);
            }
        }
        return getGlobalBeltTransform();
    }

    private static TransformProfile getGlobalBackTransform() {
        return new TransformProfile(
                BackSlotClientConfig.BACK_SLOT_OFFSET_X.get(),
                BackSlotClientConfig.BACK_SLOT_OFFSET_Y.get(),
                BackSlotClientConfig.BACK_SLOT_OFFSET_Z.get(),
                BackSlotClientConfig.BACK_SLOT_ROTATION_X.get(),
                BackSlotClientConfig.BACK_SLOT_ROTATION_Y.get(),
                BackSlotClientConfig.BACK_SLOT_ROTATION_Z.get(),
                BackSlotClientConfig.BACK_SLOT_SCALE.get()
        );
    }

    private static TransformProfile getGlobalBeltTransform() {
        return new TransformProfile(
                BackSlotClientConfig.BELT_SLOT_OFFSET_X.get(),
                BackSlotClientConfig.BELT_SLOT_OFFSET_Y.get(),
                BackSlotClientConfig.BELT_SLOT_OFFSET_Z.get(),
                BackSlotClientConfig.BELT_SLOT_ROTATION_X.get(),
                BackSlotClientConfig.BELT_SLOT_ROTATION_Y.get(),
                BackSlotClientConfig.BELT_SLOT_ROTATION_Z.get(),
                BackSlotClientConfig.BELT_SLOT_SCALE.get()
        );
    }
}
