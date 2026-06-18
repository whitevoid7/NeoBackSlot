package net.backslot.fabric.data;

import net.minecraft.world.item.ItemStack;

public interface BackSlotAccess {
    ItemStack neobackslot$getBackSlot();

    void neobackslot$setBackSlot(ItemStack stack);

    ItemStack neobackslot$getBeltSlot();

    void neobackslot$setBeltSlot(ItemStack stack);
}
