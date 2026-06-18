package net.backslot.client.gui;

import net.backslot.BackSlotMain;
import net.backslot.client.data.ClientBackSlotData;
import net.backslot.config.BackSlotClientConfig;
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

    private static final ResourceLocation HOTBAR_SELECTION =
            ResourceLocation.withDefaultNamespace("hud/hotbar_selection");

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
        int y = screenHeight - 22 + BackSlotClientConfig.HUD_SLOT_OFFSET_Y.get();
        int x = hotbarLeft - 80 + BackSlotClientConfig.HUD_SLOT_OFFSET_X.get();

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
