package net.backslot.client.gui;

import net.backslot.BackSlotMain;
import net.backslot.client.data.ClientBackSlotData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid = BackSlotMain.MOD_ID, value = Dist.CLIENT)
public class BackSlotHudOverlay {

    private static final ResourceLocation HOTBAR_BACK_SLOT =
            ResourceLocation.fromNamespaceAndPath(
                    BackSlotMain.MOD_ID,
                    "textures/gui/hotbar_back_slot.png"
            );

    private static final ResourceLocation HOTBAR_BELT_SLOT =
            ResourceLocation.fromNamespaceAndPath(
                    BackSlotMain.MOD_ID,
                    "textures/gui/hotbar_belt_slot.png"
            );

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null || mc.options.hideGui) {
            return;
        }

        ItemStack back = ClientBackSlotData.getBackSlot(mc.player.getUUID());
        ItemStack belt = ClientBackSlotData.getBeltSlot(mc.player.getUUID());

        if (back.isEmpty() && belt.isEmpty()) {
            return;
        }

        GuiGraphics graphics = event.getGuiGraphics();

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        int hotbarLeft = screenWidth / 2 - 91;
        int y = screenHeight - 22;
        int x = hotbarLeft - 80;

        if (!back.isEmpty()) {
            drawHudSlot(graphics, HOTBAR_BACK_SLOT, x, y);
            graphics.renderItem(back, x + 3, y + 3);
        }

        if (!belt.isEmpty()) {
            drawHudSlot(graphics, HOTBAR_BELT_SLOT, x + 22, y);
            graphics.renderItem(belt, x + 25, y + 3);
        }
    }

    private static void drawHudSlot(GuiGraphics graphics, ResourceLocation texture, int x, int y) {
        graphics.blit(
                texture,
                x,
                y,
                0,
                0,
                22,
                22,
                22,
                22
        );
    }
}