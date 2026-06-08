package net.backslot.inventory;

import net.backslot.attachment.BackSlotAttachments;
import net.backslot.data.BackSlotData;
import net.backslot.network.SyncBackSlotPayload;
import net.backslot.sound.BackSlotSounds;
import net.backslot.util.WeaponUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public class BackSlotInventoryHelper {

    public static void swapBackSlot(Player player) {
        BackSlotData data = player.getData(BackSlotAttachments.BACK_SLOT_DATA.get());

        ItemStack hand = player.getMainHandItem();
        ItemStack back = data.getBackSlot();

        if (back.isEmpty()) {
            if (!hand.isEmpty() && !WeaponUtil.isValidBackSlot(hand)) {
                return;
            }

            if (hand.isEmpty()) {
                sync(player, data);
                return;
            }

            data.setBackSlot(hand.copy());
            player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);

            playSwapSound(player, true, hand);
            sync(player, data);
            return;
        }

        if (!hand.isEmpty() && WeaponUtil.isValidBackSlot(hand)) {

            data.setBackSlot(hand.copy());
            player.setItemInHand(InteractionHand.MAIN_HAND, back.copy());

            playSwapSound(player, false, back);
            sync(player, data);
            return;
        }

        if (!hand.isEmpty()) {
            if (!moveToInventory(player, hand.copy())) {
                player.sendSystemMessage(Component.literal("Inventory is full."));
                return;
            }
        }

        player.setItemInHand(InteractionHand.MAIN_HAND, back.copy());
        data.setBackSlot(ItemStack.EMPTY);

        playSwapSound(player, false, back);
        sync(player, data);
    }

    public static void swapBeltSlot(Player player) {
        BackSlotData data = player.getData(BackSlotAttachments.BACK_SLOT_DATA.get());

        ItemStack hand = player.getMainHandItem();
        ItemStack belt = data.getBeltSlot();

        if (belt.isEmpty()) {
            if (!hand.isEmpty() && !WeaponUtil.isValidBeltSlot(hand)) {
                return;
            }

            if (hand.isEmpty()) {
                sync(player, data);
                return;
            }

            data.setBeltSlot(hand.copy());
            player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);

            playSwapSound(player, true, hand);
            sync(player, data);
            return;
        }

        if (!hand.isEmpty() && WeaponUtil.isValidBeltSlot(hand)) {

            data.setBeltSlot(hand.copy());
            player.setItemInHand(InteractionHand.MAIN_HAND, belt.copy());

            playSwapSound(player, false, belt);
            sync(player, data);
            return;
        }

        if (!hand.isEmpty()) {
            if (!moveToInventory(player, hand.copy())) {
                player.sendSystemMessage(Component.literal("Inventory is full."));
                return;
            }
        }

        player.setItemInHand(InteractionHand.MAIN_HAND, belt.copy());
        data.setBeltSlot(ItemStack.EMPTY);

        playSwapSound(player, false, belt);
        sync(player, data);
    }

    public static void clickBackSlot(Player player) {
        BackSlotData data = player.getData(BackSlotAttachments.BACK_SLOT_DATA.get());

        ItemStack carried = player.containerMenu.getCarried();
        ItemStack back = data.getBackSlot();

        if (!carried.isEmpty() && !WeaponUtil.isValidBackSlot(carried)) {
            return;
        }

        boolean hadItemInSlot = !back.isEmpty();
        boolean sheathing = back.isEmpty() && !carried.isEmpty();
        ItemStack soundStack = !back.isEmpty() ? back : carried;

        data.setBackSlot(carried.copy());
        player.containerMenu.setCarried(back.copy());
        player.containerMenu.broadcastChanges();

        if (hadItemInSlot) {
            playSwapSound(player, sheathing, soundStack);
        }

        sync(player, data);
    }

    public static void clickBeltSlot(Player player) {
        BackSlotData data = player.getData(BackSlotAttachments.BACK_SLOT_DATA.get());

        ItemStack carried = player.containerMenu.getCarried();
        ItemStack belt = data.getBeltSlot();

        if (!carried.isEmpty() && !WeaponUtil.isValidBeltSlot(carried)) {
            return;
        }

        boolean hadItemInSlot = !belt.isEmpty();
        boolean sheathing = belt.isEmpty() && !carried.isEmpty();
        ItemStack soundStack = !belt.isEmpty() ? belt : carried;

        data.setBeltSlot(carried.copy());
        player.containerMenu.setCarried(belt.copy());
        player.containerMenu.broadcastChanges();

        if (hadItemInSlot) {
            playSwapSound(player, sheathing, soundStack);
        }

        sync(player, data);
    }

    private static boolean moveToInventory(Player player, ItemStack stack) {
        if (stack.isEmpty()) {
            return true;
        }

        var inventory = player.getInventory();

        // Hotbar dulu: slot 0-8
        for (int i = 0; i < 9; i++) {
            if (inventory.getItem(i).isEmpty()) {
                inventory.setItem(i, stack.copy());
                inventory.setChanged();
                return true;
            }
        }

        // Inventory utama: slot 9-35
        for (int i = 9; i < inventory.items.size(); i++) {
            if (inventory.getItem(i).isEmpty()) {
                inventory.setItem(i, stack.copy());
                inventory.setChanged();
                return true;
            }
        }

        return false;
    }

    private static void playSwapSound(Player player, boolean sheathing, ItemStack stack) {
        boolean bowLike = stack.getItem() instanceof BowItem
                || stack.getItem() instanceof CrossbowItem;

        if (sheathing) {
            player.level().playSound(
                    null,
                    player.blockPosition(),
                    BackSlotSounds.PACK_UP_ITEM.get(),
                    player.getSoundSource(),
                    1.0F,
                    1.0F
            );
        } else {
            player.level().playSound(
                    null,
                    player.blockPosition(),
                    bowLike ? BackSlotSounds.PACK_UP_ITEM.get() : BackSlotSounds.SHEATH_SWORD.get(),
                    player.getSoundSource(),
                    1.0F,
                    1.0F
            );
        }
    }

    public static void sync(Player player) {
        BackSlotData data = player.getData(BackSlotAttachments.BACK_SLOT_DATA.get());
        sync(player, data);
    }

    private static void sync(Player player, BackSlotData data) {
        if (player instanceof ServerPlayer serverPlayer) {
            SyncBackSlotPayload payload = new SyncBackSlotPayload(
                    player.getUUID(),
                    data.getBackSlot(),
                    data.getBeltSlot()
            );

            PacketDistributor.sendToPlayer(serverPlayer, payload);
            PacketDistributor.sendToPlayersTrackingEntity(serverPlayer, payload);
        }
    }
}