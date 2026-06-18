package net.backslot.client;

import net.backslot.client.data.ClientBackSlotData;
import net.backslot.client.data.ClientTransformData;
import net.backslot.client.profile.TransformProfile;
import net.backslot.client.profile.TransformProfileManager;
import net.backslot.config.BackSlotClientConfig;
import net.backslot.network.UpdateTransformPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public final class ClientTransformSync {
    private ClientTransformSync() {
    }

    public static void sendActiveTransformsToServer() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.getConnection() == null) {
            return;
        }

        TransformProfile backTransform = getActiveBackTransform();
        TransformProfile beltTransform = getActiveBeltTransform();

        ClientTransformData.setBoth(minecraft.player.getUUID(), backTransform, beltTransform);
        PacketDistributor.sendToServer(new UpdateTransformPayload(backTransform, beltTransform));
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
