package net.backslot.client.data;

import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClientBackSlotData {

    private static final Map<UUID, ItemStack> BACK_SLOTS = new HashMap<>();
    private static final Map<UUID, ItemStack> BELT_SLOTS = new HashMap<>();

    public static ItemStack getBackSlot(UUID playerId) {
        return BACK_SLOTS.getOrDefault(playerId, ItemStack.EMPTY);
    }

    public static ItemStack getBeltSlot(UUID playerId) {
        return BELT_SLOTS.getOrDefault(playerId, ItemStack.EMPTY);
    }

    public static void setBackSlot(UUID playerId, ItemStack stack) {
        BACK_SLOTS.put(playerId, stack.copy());
    }

    public static void setBeltSlot(UUID playerId, ItemStack stack) {
        BELT_SLOTS.put(playerId, stack.copy());
    }

    public static void setBoth(UUID playerId, ItemStack back, ItemStack belt) {
        setBackSlot(playerId, back);
        setBeltSlot(playerId, belt);
    }

    public static void remove(UUID playerId) {
        BACK_SLOTS.remove(playerId);
        BELT_SLOTS.remove(playerId);
    }
}