package net.backslot.client.gui;

import net.backslot.BackSlotMain;
import net.backslot.client.data.ClientBackSlotData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.backslot.network.ClickBackSlotPayload;
import net.backslot.network.ClickBeltSlotPayload;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = BackSlotMain.MOD_ID, value = Dist.CLIENT)
public class BackSlotInventoryOverlay {

    @SubscribeEvent
    public static void onRenderScreen(ScreenEvent.Render.Post event) {
        if (!(event.getScreen() instanceof InventoryScreen inventoryScreen)) {
            return;
        }

        GuiGraphics graphics = event.getGuiGraphics();

        int left = inventoryScreen.getGuiLeft();
        int top = inventoryScreen.getGuiTop();

        int x = left + 76;
        int y = top + 42;

        drawSlot(graphics, x, y - 20); // Back Slot
        drawSlot(graphics, x, y);      // Belt Slot

        ItemStack back = ClientBackSlotData.getBackSlot(
                Minecraft.getInstance().player.getUUID()
        );

        ItemStack belt = ClientBackSlotData.getBeltSlot(
                Minecraft.getInstance().player.getUUID()
        );

        double mouseX = event.getMouseX();
        double mouseY = event.getMouseY();

        if (back.isEmpty()) {
            graphics.blit(
                    EMPTY_BACK_SLOT,
                    x + 1,
                    y - 19,
                    0,
                    0,
                    16,
                    16,
                    16,
                    16
            );
        } else {
            graphics.renderItem(back, x + 1, y - 19);
        }

        if (belt.isEmpty()) {
            graphics.blit(
                    EMPTY_BELT_SLOT,
                    x + 1,
                    y + 1,
                    0,
                    0,
                    16,
                    16,
                    16,
                    16
            );
        } else {
            graphics.renderItem(belt, x + 1, y + 1);
        }

        if (isMouseOver(mouseX, mouseY, x, y - 20)) {

            if (!back.isEmpty()) {
                graphics.renderTooltip(
                        Minecraft.getInstance().font,
                        back,
                        (int) mouseX,
                        (int) mouseY
                );
            } else {
                graphics.renderTooltip(
                        Minecraft.getInstance().font,
                        Component.literal("Back Slot"),
                        (int) mouseX,
                        (int) mouseY
                );
            }
        }

        if (isMouseOver(mouseX, mouseY, x, y)) {

            if (!belt.isEmpty()) {
                graphics.renderTooltip(
                        Minecraft.getInstance().font,
                        belt,
                        (int) mouseX,
                        (int) mouseY
                );
            } else {
                graphics.renderTooltip(
                        Minecraft.getInstance().font,
                        Component.literal("Belt Slot"),
                        (int) mouseX,
                        (int) mouseY
                );
            }
        }
    }

    private static final ResourceLocation EMPTY_BACK_SLOT =
            ResourceLocation.fromNamespaceAndPath(
                    BackSlotMain.MOD_ID,
                    "textures/gui/empty_back_slot.png"
            );

    private static final ResourceLocation EMPTY_BELT_SLOT =
            ResourceLocation.fromNamespaceAndPath(
                    BackSlotMain.MOD_ID,
                    "textures/gui/empty_belt_slot.png"
            );

    private static void drawSlot(GuiGraphics graphics, int x, int y) {
        graphics.fill(x, y, x + 18, y + 18, 0xFF8B8B8B);
        graphics.fill(x + 1, y + 1, x + 17, y + 17, 0xFF373737);
        graphics.fill(x + 2, y + 2, x + 16, y + 16, 0xFF8B8B8B);
    }

    private static boolean isMouseOver(double mouseX, double mouseY, int x, int y) {
        return mouseX >= x
                && mouseX < x + 18
                && mouseY >= y
                && mouseY < y + 18;
    }

    @SubscribeEvent
    public static void onMouseClick(ScreenEvent.MouseButtonPressed.Pre event) {
        if (!(event.getScreen() instanceof InventoryScreen inventoryScreen)) {
            return;
        }

        if (event.getButton() != 0) {
            return;
        }

        int left = inventoryScreen.getGuiLeft();
        int top = inventoryScreen.getGuiTop();

        int x = left + 76;
        int y = top + 42;

        double mouseX = event.getMouseX();
        double mouseY = event.getMouseY();

        if (isMouseOver(mouseX, mouseY, x, y - 20)) {
            PacketDistributor.sendToServer(new ClickBackSlotPayload());
            event.setCanceled(true);
            return;
        }

        if (isMouseOver(mouseX, mouseY, x, y)) {
            PacketDistributor.sendToServer(new ClickBeltSlotPayload());
            event.setCanceled(true);
        }
    }
}