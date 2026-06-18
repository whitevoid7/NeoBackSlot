package net.backslot.fabric.client.gui;

import net.backslot.fabric.client.data.ClientBackSlotData;
import net.backslot.fabric.config.BackSlotClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public final class BackSlotHudOverlay {
    private static final ResourceLocation HOTBAR_SELECTION =
            ResourceLocation.withDefaultNamespace("hud/hotbar_selection");

    private BackSlotHudOverlay() {
    }

    public static void render(GuiGraphics graphics) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null || minecraft.options.hideGui) {
            return;
        }

        ItemStack back = ClientBackSlotData.getBackSlot(minecraft.player.getUUID());
        ItemStack belt = ClientBackSlotData.getBeltSlot(minecraft.player.getUUID());

        if (back.isEmpty() && belt.isEmpty()) {
            return;
        }

        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();

        int hotbarLeft = screenWidth / 2 - 91;
        int x = hotbarLeft - 80 + BackSlotClientConfig.HUD_SLOT_OFFSET_X.get();
        int y = screenHeight - 22 + BackSlotClientConfig.HUD_SLOT_OFFSET_Y.get();

        if (!back.isEmpty()) {
            drawHudSlot(graphics, x, y);
            graphics.renderItem(back, x + 3, y + 3);
        }

        if (!belt.isEmpty()) {
            drawHudSlot(graphics, x + 22, y);
            graphics.renderItem(belt, x + 25, y + 3);
        }
    }

    private static void drawHudSlot(GuiGraphics graphics, int x, int y) {
        graphics.blitSprite(HOTBAR_SELECTION, x - 1, y - 1, 24, 24);
    }
}
