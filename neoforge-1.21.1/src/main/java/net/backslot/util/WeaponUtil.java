package net.backslot.util;

import net.backslot.BackSlotMain;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.HoeItem;

public class WeaponUtil {
    private static final TagKey<Item> BACK_SLOT_ITEMS = TagKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(BackSlotMain.MOD_ID, "backslot_items")
    );

    private static final TagKey<Item> BELT_SLOT_ITEMS = TagKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(BackSlotMain.MOD_ID, "beltslot_items")
    );

    public static boolean isValidBackSlot(ItemStack stack) {
        Item item = stack.getItem();

        return stack.is(BACK_SLOT_ITEMS)
                || isLikelyRangedWeapon(item)
                || isLikelyMeleeWeapon(item)
                || item instanceof SwordItem
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

    public static boolean isPackLikeItem(ItemStack stack) {
        Item item = stack.getItem();
        return isLikelyRangedWeapon(item)
                || item instanceof BowItem
                || item instanceof CrossbowItem
                || item instanceof ShieldItem;
    }

    public static boolean isValidBeltSlot(ItemStack stack) {
        Item item = stack.getItem();

        return stack.is(BELT_SLOT_ITEMS)
                || isLikelyMeleeWeapon(item)
                || item instanceof SwordItem
                || item instanceof AxeItem
                || item instanceof PickaxeItem
                || item instanceof ShovelItem
                || item instanceof HoeItem
                || item instanceof MaceItem
                || item instanceof TridentItem;
    }

    private static boolean isLikelyRangedWeapon(Item item) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
        String path = itemId.getPath();

        if (path.endsWith("bow") || path.endsWith("crossbow") || path.endsWith("gun")) {
            return true;
        }

        String[] tokens = path.split("[_\\-/]");
        for (String token : tokens) {
            if (isRangedWeaponToken(token)) {
                return true;
            }
        }

        return false;
    }

    private static boolean isLikelyMeleeWeapon(Item item) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
        String path = itemId.getPath();

        if (path.endsWith("sword")
                || path.endsWith("blade")
                || path.endsWith("dagger")
                || path.endsWith("knife")
                || path.endsWith("staff")
                || path.endsWith("wand")
                || path.endsWith("spear")
                || path.endsWith("lance")
                || path.endsWith("hammer")
                || path.endsWith("mace")) {
            return true;
        }

        String[] tokens = path.split("[_\\-/]");
        for (String token : tokens) {
            if (isMeleeWeaponToken(token)) {
                return true;
            }
        }

        return false;
    }

    private static boolean isRangedWeaponToken(String token) {
        return token.equals("bow")
                || token.equals("crossbow")
                || token.equals("longbow")
                || token.equals("shortbow")
                || token.equals("greatbow")
                || token.equals("blunderbuss")
                || token.equals("musket")
                || token.equals("rifle")
                || token.equals("pistol")
                || token.equals("revolver")
                || token.equals("shotgun")
                || token.equals("handgun")
                || token.equals("minigun")
                || token.equals("firearm")
                || token.equals("arquebus")
                || token.equals("cannon")
                || token.equals("launcher")
                || token.equals("slingshot")
                || token.equals("sling")
                || token.equals("blowgun")
                || token.equals("boomerang")
                || token.equals("javelin");
    }

    private static boolean isMeleeWeaponToken(String token) {
        return token.equals("sword")
                || token.equals("greatsword")
                || token.equals("longsword")
                || token.equals("shortsword")
                || token.equals("broadsword")
                || token.equals("blade")
                || token.equals("katana")
                || token.equals("nodachi")
                || token.equals("tachi")
                || token.equals("rapier")
                || token.equals("saber")
                || token.equals("sabre")
                || token.equals("scimitar")
                || token.equals("claymore")
                || token.equals("dagger")
                || token.equals("knife")
                || token.equals("dirk")
                || token.equals("stiletto")
                || token.equals("staff")
                || token.equals("wand")
                || token.equals("rod")
                || token.equals("scepter")
                || token.equals("sceptre")
                || token.equals("spear")
                || token.equals("lance")
                || token.equals("pike")
                || token.equals("polearm")
                || token.equals("glaive")
                || token.equals("halberd")
                || token.equals("naginata")
                || token.equals("scythe")
                || token.equals("hammer")
                || token.equals("warhammer")
                || token.equals("mace")
                || token.equals("maul")
                || token.equals("club")
                || token.equals("flail")
                || token.equals("morningstar")
                || token.equals("battleaxe")
                || token.equals("greataxe")
                || token.equals("tomahawk")
                || token.equals("sickle")
                || token.equals("khopesh");
    }
}
