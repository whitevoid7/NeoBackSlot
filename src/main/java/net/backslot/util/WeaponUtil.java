package net.backslot.util;

import net.minecraft.world.item.*;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.HoeItem;

public class WeaponUtil {

    public static boolean isValidBackSlot(ItemStack stack) {
        Item item = stack.getItem();

        return item instanceof SwordItem
                || item instanceof AxeItem
                || item instanceof PickaxeItem
                || item instanceof ShovelItem
                || item instanceof HoeItem
                || item instanceof BowItem
                || item instanceof CrossbowItem
                || item instanceof TridentItem
                || item instanceof MaceItem
                || item instanceof ShieldItem;
    }

    public static boolean isValidBeltSlot(ItemStack stack) {
        Item item = stack.getItem();

        return item instanceof SwordItem
                || item instanceof AxeItem
                || item instanceof PickaxeItem
                || item instanceof ShovelItem
                || item instanceof HoeItem
                || item instanceof MaceItem
                || item instanceof TridentItem;
    }
}