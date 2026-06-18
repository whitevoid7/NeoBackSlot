package net.backslot.client.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class PlayerPreviewWidget extends AbstractWidget {

    private final int entityScale;
    private final float previewYaw;

    public PlayerPreviewWidget(int x, int y, int width, int height, int entityScale, float previewYaw) {
        super(x, y, width, height, Component.empty());
        this.entityScale = entityScale;
        this.previewYaw = previewYaw;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // White preview background
        guiGraphics.fill(
                getX(),
                getY(),
                getX() + getWidth(),
                getY() + getHeight(),
                0xFFFFFFFF
        );

        // Border
        guiGraphics.fill(getX(), getY(), getX() + getWidth(), getY() + 1, 0xFF555555);
        guiGraphics.fill(getX(), getY() + getHeight() - 1, getX() + getWidth(), getY() + getHeight(), 0xFF555555);
        guiGraphics.fill(getX(), getY(), getX() + 1, getY() + getHeight(), 0xFF555555);
        guiGraphics.fill(getX() + getWidth() - 1, getY(), getX() + getWidth(), getY() + getHeight(), 0xFF555555);

        LivingEntity player = Minecraft.getInstance().player;

        if (player == null) {
            return;
        }

        renderFixedPlayer(guiGraphics, player);
    }

    private void renderFixedPlayer(GuiGraphics guiGraphics, LivingEntity player) {
        float oldBodyRot = player.yBodyRot;
        float oldYRot = player.getYRot();
        float oldXRot = player.getXRot();
        float oldHeadRotO = player.yHeadRotO;
        float oldHeadRot = player.yHeadRot;

        float yaw = 180.0F + previewYaw;
        player.yBodyRot = yaw;
        player.setYRot(yaw);
        player.setXRot(0.0F);
        player.yHeadRot = yaw;
        player.yHeadRotO = yaw;

        float centerX = (float) (getX() + getX() + getWidth()) / 2.0F;
        float centerY = (float) (getY() + getY() + getHeight()) / 2.0F;
        float scale = (float) entityScale / player.getScale();
        Vector3f translation = new Vector3f(0.0F, player.getBbHeight() / 2.0F + 0.0625F * player.getScale(), 0.0F);
        Quaternionf pose = new Quaternionf().rotateZ((float) Math.PI);
        Quaternionf camera = new Quaternionf();

        InventoryScreen.renderEntityInInventory(guiGraphics, centerX, centerY, scale, translation, pose, camera, player);

        player.yBodyRot = oldBodyRot;
        player.setYRot(oldYRot);
        player.setXRot(oldXRot);
        player.yHeadRotO = oldHeadRotO;
        player.yHeadRot = oldHeadRot;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public void playDownSound(SoundManager handler) {
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        // No narration needed for preview-only widget.
    }
}
