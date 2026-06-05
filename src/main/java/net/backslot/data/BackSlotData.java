package net.backslot.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class BackSlotData implements INBTSerializable<CompoundTag> {

    private ItemStack backSlot = ItemStack.EMPTY;
    private ItemStack beltSlot = ItemStack.EMPTY;

    public ItemStack getBackSlot() {
        return backSlot;
    }

    public void setBackSlot(ItemStack stack) {
        this.backSlot = stack;
    }

    public ItemStack getBeltSlot() {
        return beltSlot;
    }

    public void setBeltSlot(ItemStack stack) {
        this.beltSlot = stack;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();

        if (!backSlot.isEmpty()) {
            tag.put("BackSlot", backSlot.save(provider));
        }

        if (!beltSlot.isEmpty()) {
            tag.put("BeltSlot", beltSlot.save(provider));
        }

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        this.backSlot = ItemStack.parseOptional(provider, tag.getCompound("BackSlot"));
        this.beltSlot = ItemStack.parseOptional(provider, tag.getCompound("BeltSlot"));
    }
}