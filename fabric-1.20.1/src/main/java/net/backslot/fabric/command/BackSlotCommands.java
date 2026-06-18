package net.backslot.fabric.command;

import com.mojang.brigadier.CommandDispatcher;
import net.backslot.fabric.data.BackSlotAccess;
import net.backslot.fabric.inventory.BackSlotInventoryHelper;
import net.backslot.fabric.util.WeaponUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class BackSlotCommands {
    private BackSlotCommands() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("neobackslot")
                        .then(Commands.literal("get")
                                .executes(context -> get(context.getSource())))
                        .then(Commands.literal("setback")
                                .executes(context -> setBack(context.getSource())))
                        .then(Commands.literal("setbelt")
                                .executes(context -> setBelt(context.getSource())))
                        .then(Commands.literal("clear")
                                .executes(context -> clear(context.getSource())))
        );
    }

    private static int get(CommandSourceStack source) {
        Player player = source.getPlayer();

        if (player == null) {
            source.sendFailure(Component.literal("This command must be used by a player."));
            return 0;
        }

        BackSlotAccess data = (BackSlotAccess) player;
        source.sendSuccess(() -> Component.literal(
                "Back Slot: " + itemName(data.neobackslot$getBackSlot()) +
                        " | Belt Slot: " + itemName(data.neobackslot$getBeltSlot())
        ), false);

        return 1;
    }

    private static int setBack(CommandSourceStack source) {
        Player player = source.getPlayer();

        if (player == null) {
            source.sendFailure(Component.literal("This command must be used by a player."));
            return 0;
        }

        ItemStack held = player.getMainHandItem();

        if (held.isEmpty()) {
            source.sendFailure(Component.literal("Hold an item first."));
            return 0;
        }

        if (!WeaponUtil.isValidBackSlot(held)) {
            source.sendFailure(Component.literal("Item cannot be stored in Back Slot."));
            return 0;
        }

        BackSlotAccess data = (BackSlotAccess) player;
        data.neobackslot$setBackSlot(held.copy());
        BackSlotInventoryHelper.sync(player);

        source.sendSuccess(() -> Component.literal("Back Slot set to: " + itemName(held)), false);
        return 1;
    }

    private static int setBelt(CommandSourceStack source) {
        Player player = source.getPlayer();

        if (player == null) {
            source.sendFailure(Component.literal("This command must be used by a player."));
            return 0;
        }

        ItemStack held = player.getMainHandItem();

        if (held.isEmpty()) {
            source.sendFailure(Component.literal("Hold an item first."));
            return 0;
        }

        if (!WeaponUtil.isValidBeltSlot(held)) {
            source.sendFailure(Component.literal("Item cannot be stored in Belt Slot."));
            return 0;
        }

        BackSlotAccess data = (BackSlotAccess) player;
        data.neobackslot$setBeltSlot(held.copy());
        BackSlotInventoryHelper.sync(player);

        source.sendSuccess(() -> Component.literal("Belt Slot set to: " + itemName(held)), false);
        return 1;
    }

    private static int clear(CommandSourceStack source) {
        Player player = source.getPlayer();

        if (player == null) {
            source.sendFailure(Component.literal("This command must be used by a player."));
            return 0;
        }

        BackSlotAccess data = (BackSlotAccess) player;
        data.neobackslot$setBackSlot(ItemStack.EMPTY);
        data.neobackslot$setBeltSlot(ItemStack.EMPTY);
        BackSlotInventoryHelper.sync(player);

        source.sendSuccess(() -> Component.literal("NeoBackSlot data cleared."), false);
        return 1;
    }

    private static String itemName(ItemStack stack) {
        return stack.isEmpty() ? "Empty" : stack.getHoverName().getString();
    }
}
