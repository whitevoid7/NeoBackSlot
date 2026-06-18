package net.backslot.fabric.client.data;

import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ClientBackSlotData {
    private static final Map<UUID, ItemStack> BACK_SLOTS = new HashMap<>();
    private static final Map<UUID, ItemStack> BELT_SLOTS = new HashMap<>();

    private ClientBackSlotData() {
    }

    public static ItemStack getBackSlot(UUID playerId) {
        return BACK_SLOTS.getOrDefault(playerId, ItemStack.EMPTY);
    }

    public static ItemStack getBeltSlot(UUID playerId) {
        return BELT_SLOTS.getOrDefault(playerId, ItemStack.EMPTY);
    }

    public static void setBoth(UUID playerId, ItemStack back, ItemStack belt) {
        BACK_SLOTS.put(playerId, back.copy());
        BELT_SLOTS.put(playerId, belt.copy());
    }

    public static void remove(UUID playerId) {
        BACK_SLOTS.remove(playerId);
        BELT_SLOTS.remove(playerId);
    }
}
