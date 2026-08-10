package net.backslot.fabric.client.gui;

import net.backslot.fabric.client.data.ClientBackSlotData;
import net.backslot.fabric.config.BackSlotClientConfig;
import net.backslot.fabric.mixin.AbstractContainerScreenAccessor;
import net.backslot.fabric.network.BackSlotPackets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public final class BackSlotInventoryOverlay {
    private static final ResourceLocation INVENTORY_BACKGROUND =
            ResourceLocation.withDefaultNamespace("textures/gui/container/inventory.png");

    private static final ResourceLocation EMPTY_BACK_SLOT =
            ResourceLocation.fromNamespaceAndPath("neobackslot", "textures/gui/empty_back_slot.png");

    private static final ResourceLocation EMPTY_BELT_SLOT =
            ResourceLocation.fromNamespaceAndPath("neobackslot", "textures/gui/empty_belt_slot.png");

    private BackSlotInventoryOverlay() {
    }

    public static void render(Screen screen, GuiGraphics graphics, int mouseX, int mouseY, float tickDelta) {
        if (!(screen instanceof InventoryScreen inventoryScreen)) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }

        AbstractContainerScreenAccessor inventoryAccessor = (AbstractContainerScreenAccessor) inventoryScreen;
        int left = inventoryAccessor.neobackslot$getGuiLeft();
        int top = inventoryAccessor.neobackslot$getGuiTop();
        int x = left + 76 + BackSlotClientConfig.INVENTORY_SLOT_OFFSET_X.get();
        int y = top + 42 + BackSlotClientConfig.INVENTORY_SLOT_OFFSET_Y.get();

        drawSlot(graphics, x, y - 20);
        drawSlot(graphics, x, y);

        ItemStack back = ClientBackSlotData.getBackSlot(minecraft.player.getUUID());
        ItemStack belt = ClientBackSlotData.getBeltSlot(minecraft.player.getUUID());

        if (back.isEmpty()) {
            graphics.blit(EMPTY_BACK_SLOT, x + 1, y - 19, 0, 0, 16, 16, 16, 16);
        } else {
            graphics.renderItem(back, x + 1, y - 19);
        }

        if (belt.isEmpty()) {
            graphics.blit(EMPTY_BELT_SLOT, x + 1, y + 1, 0, 0, 16, 16, 16, 16);
        } else {
            graphics.renderItem(belt, x + 1, y + 1);
        }

        if (isMouseOver(mouseX, mouseY, x, y - 20)) {
            if (!back.isEmpty()) {
                graphics.renderTooltip(minecraft.font, back, mouseX, mouseY);
            } else {
                graphics.renderTooltip(minecraft.font, Component.literal("Back Slot"), mouseX, mouseY);
            }
        }

        if (isMouseOver(mouseX, mouseY, x, y)) {
            if (!belt.isEmpty()) {
                graphics.renderTooltip(minecraft.font, belt, mouseX, mouseY);
            } else {
                graphics.renderTooltip(minecraft.font, Component.literal("Belt Slot"), mouseX, mouseY);
            }
        }
    }

    public static boolean allowMouseClick(Screen screen, double mouseX, double mouseY, int button) {
        if (!(screen instanceof InventoryScreen inventoryScreen) || button != 0) {
            return true;
        }

        AbstractContainerScreenAccessor inventoryAccessor = (AbstractContainerScreenAccessor) inventoryScreen;
        int left = inventoryAccessor.neobackslot$getGuiLeft();
        int top = inventoryAccessor.neobackslot$getGuiTop();
        int x = left + 76 + BackSlotClientConfig.INVENTORY_SLOT_OFFSET_X.get();
        int y = top + 42 + BackSlotClientConfig.INVENTORY_SLOT_OFFSET_Y.get();

        if (isMouseOver(mouseX, mouseY, x, y - 20)) {
            ClientPlayNetworking.send(new BackSlotPackets.ClickBackSlotPayload());
            return false;
        }

        if (isMouseOver(mouseX, mouseY, x, y)) {
            ClientPlayNetworking.send(new BackSlotPackets.ClickBeltSlotPayload());
            return false;
        }

        return true;
    }

    private static void drawSlot(GuiGraphics graphics, int x, int y) {
        graphics.blit(INVENTORY_BACKGROUND, x, y, 7, 83, 18, 18);
    }

    private static boolean isMouseOver(double mouseX, double mouseY, int x, int y) {
        return mouseX >= x && mouseX < x + 18 && mouseY >= y && mouseY < y + 18;
    }

}
