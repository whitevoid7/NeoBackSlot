package net.backslot.fabric.mixin;

import net.backslot.fabric.data.BackSlotAccess;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin implements BackSlotAccess {
    @Unique
    private ItemStack neobackslot$backSlot = ItemStack.EMPTY;

    @Unique
    private ItemStack neobackslot$beltSlot = ItemStack.EMPTY;

    @Override
    public ItemStack neobackslot$getBackSlot() {
        return neobackslot$backSlot;
    }

    @Override
    public void neobackslot$setBackSlot(ItemStack stack) {
        neobackslot$backSlot = stack.copy();
    }

    @Override
    public ItemStack neobackslot$getBeltSlot() {
        return neobackslot$beltSlot;
    }

    @Override
    public void neobackslot$setBeltSlot(ItemStack stack) {
        neobackslot$beltSlot = stack.copy();
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void neobackslot$saveData(CompoundTag tag, CallbackInfo ci) {
        HolderLookup.Provider provider = ((Player) (Object) this).registryAccess();
        if (!neobackslot$backSlot.isEmpty()) {
            tag.put("NeoBackSlotBack", neobackslot$backSlot.save(provider));
        }

        if (!neobackslot$beltSlot.isEmpty()) {
            tag.put("NeoBackSlotBelt", neobackslot$beltSlot.save(provider));
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void neobackslot$readData(CompoundTag tag, CallbackInfo ci) {
        HolderLookup.Provider provider = ((Player) (Object) this).registryAccess();
        neobackslot$backSlot = tag.contains("NeoBackSlotBack")
                ? ItemStack.parseOptional(provider, tag.getCompound("NeoBackSlotBack"))
                : ItemStack.EMPTY;
        neobackslot$beltSlot = tag.contains("NeoBackSlotBelt")
                ? ItemStack.parseOptional(provider, tag.getCompound("NeoBackSlotBelt"))
                : ItemStack.EMPTY;
    }
}
