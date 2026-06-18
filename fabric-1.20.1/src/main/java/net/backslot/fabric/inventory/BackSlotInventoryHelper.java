package net.backslot.fabric.inventory;

import net.backslot.fabric.data.BackSlotAccess;
import net.backslot.fabric.network.BackSlotPackets;
import net.backslot.fabric.sound.BackSlotSounds;
import net.backslot.fabric.util.WeaponUtil;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;

public final class BackSlotInventoryHelper {
    private BackSlotInventoryHelper() {
    }

    public static void swapBackSlot(Player player) {
        BackSlotAccess data = (BackSlotAccess) player;
        ItemStack hand = player.getMainHandItem();
        ItemStack back = data.neobackslot$getBackSlot();

        if (back.isEmpty()) {
            if (!hand.isEmpty() && !WeaponUtil.isValidBackSlot(hand)) {
                return;
            }
            if (hand.isEmpty()) {
                sync(player);
                return;
            }
            data.neobackslot$setBackSlot(hand);
            player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            playSwapSound(player, true, hand);
            sync(player);
            return;
        }

        if (!hand.isEmpty() && WeaponUtil.isValidBackSlot(hand)) {
            data.neobackslot$setBackSlot(hand);
            player.setItemInHand(InteractionHand.MAIN_HAND, back.copy());
            playSwapSound(player, false, back);
            sync(player);
            return;
        }

        if (!hand.isEmpty() && !moveToInventory(player, hand.copy())) {
            player.sendSystemMessage(Component.literal("Inventory is full."));
            return;
        }

        player.setItemInHand(InteractionHand.MAIN_HAND, back.copy());
        data.neobackslot$setBackSlot(ItemStack.EMPTY);
        playSwapSound(player, false, back);
        sync(player);
    }

    public static void swapBeltSlot(Player player) {
        BackSlotAccess data = (BackSlotAccess) player;
        ItemStack hand = player.getMainHandItem();
        ItemStack belt = data.neobackslot$getBeltSlot();

        if (belt.isEmpty()) {
            if (!hand.isEmpty() && !WeaponUtil.isValidBeltSlot(hand)) {
                return;
            }
            if (hand.isEmpty()) {
                sync(player);
                return;
            }
            data.neobackslot$setBeltSlot(hand);
            player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            playSwapSound(player, true, hand);
            sync(player);
            return;
        }

        if (!hand.isEmpty() && WeaponUtil.isValidBeltSlot(hand)) {
            data.neobackslot$setBeltSlot(hand);
            player.setItemInHand(InteractionHand.MAIN_HAND, belt.copy());
            playSwapSound(player, false, belt);
            sync(player);
            return;
        }

        if (!hand.isEmpty() && !moveToInventory(player, hand.copy())) {
            player.sendSystemMessage(Component.literal("Inventory is full."));
            return;
        }

        player.setItemInHand(InteractionHand.MAIN_HAND, belt.copy());
        data.neobackslot$setBeltSlot(ItemStack.EMPTY);
        playSwapSound(player, false, belt);
        sync(player);
    }

    public static void clickBackSlot(Player player) {
        BackSlotAccess data = (BackSlotAccess) player;
        ItemStack carried = player.containerMenu.getCarried();
        ItemStack back = data.neobackslot$getBackSlot();

        if (!carried.isEmpty() && !WeaponUtil.isValidBackSlot(carried)) {
            return;
        }

        boolean hadItemInSlot = !back.isEmpty();
        boolean sheathing = back.isEmpty() && !carried.isEmpty();
        ItemStack soundStack = !back.isEmpty() ? back : carried;

        data.neobackslot$setBackSlot(carried);
        player.containerMenu.setCarried(back.copy());
        player.containerMenu.broadcastChanges();

        if (hadItemInSlot) {
            playSwapSound(player, sheathing, soundStack);
        }

        sync(player);
    }

    public static void clickBeltSlot(Player player) {
        BackSlotAccess data = (BackSlotAccess) player;
        ItemStack carried = player.containerMenu.getCarried();
        ItemStack belt = data.neobackslot$getBeltSlot();

        if (!carried.isEmpty() && !WeaponUtil.isValidBeltSlot(carried)) {
            return;
        }

        boolean hadItemInSlot = !belt.isEmpty();
        boolean sheathing = belt.isEmpty() && !carried.isEmpty();
        ItemStack soundStack = !belt.isEmpty() ? belt : carried;

        data.neobackslot$setBeltSlot(carried);
        player.containerMenu.setCarried(belt.copy());
        player.containerMenu.broadcastChanges();

        if (hadItemInSlot) {
            playSwapSound(player, sheathing, soundStack);
        }

        sync(player);
    }

    public static void sync(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        BackSlotAccess data = (BackSlotAccess) player;
        send(serverPlayer, serverPlayer, data);
        for (ServerPlayer viewer : PlayerLookup.tracking(serverPlayer)) {
            send(viewer, serverPlayer, data);
        }
    }

    private static void send(ServerPlayer viewer, ServerPlayer owner, BackSlotAccess data) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(io.netty.buffer.Unpooled.buffer());
        BackSlotPackets.writeSlotSync(buffer, owner.getUUID(), data.neobackslot$getBackSlot(), data.neobackslot$getBeltSlot());
        ServerPlayNetworking.send(viewer, BackSlotPackets.SYNC_SLOT, buffer);
    }

    private static boolean moveToInventory(Player player, ItemStack stack) {
        for (int i = 0; i < player.getInventory().items.size(); i++) {
            if (player.getInventory().getItem(i).isEmpty()) {
                player.getInventory().setItem(i, stack.copy());
                player.getInventory().setChanged();
                return true;
            }
        }
        return false;
    }

    private static void playSwapSound(Player player, boolean sheathing, ItemStack stack) {
        boolean packLike = WeaponUtil.isPackLikeItem(stack);
        player.level().playSound(
                null,
                player.blockPosition(),
                sheathing || packLike ? BackSlotSounds.PACK_UP_ITEM : BackSlotSounds.SHEATH_SWORD,
                SoundSource.PLAYERS,
                1.0F,
                1.0F
        );
    }
}
