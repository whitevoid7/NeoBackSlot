package net.backslot.fabric.client.config;

import net.backslot.fabric.client.screen.BackSlotEditorScreen;
import net.backslot.fabric.config.BackSlotClientConfig;
import net.backslot.fabric.config.BackSlotCommonConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public final class BackSlotModMenuConfigScreen extends Screen {
    private static final double SCALE_STEP = 0.05D;
    private static final int OFFSET_STEP = 1;

    private final Screen parent;
    private int positionSectionTitleY;

    public BackSlotModMenuConfigScreen(Screen parent) {
        super(Component.literal("NeoBackSlot Config"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int y = 44;

        addRenderableWidget(Button.builder(keepItemsLabel(), button -> {
            BackSlotCommonConfig.KEEP_ITEMS_ON_DEATH.set(!BackSlotCommonConfig.KEEP_ITEMS_ON_DEATH.get());
            button.setMessage(keepItemsLabel());
        }).bounds(centerX - 100, y, 200, 20).build());

        y += 32;
        addScaleRow(centerX, y, "Back Scale", BackSlotClientConfig.BACK_SLOT_SCALE);

        y += 28;
        addScaleRow(centerX, y, "Belt Scale", BackSlotClientConfig.BELT_SLOT_SCALE);

        y += 32;
        positionSectionTitleY = y;

        y += 14;
        addOffsetRow(centerX, y, "Inventory Slot X", BackSlotClientConfig.INVENTORY_SLOT_OFFSET_X);

        y += 28;
        addOffsetRow(centerX, y, "Inventory Slot Y", BackSlotClientConfig.INVENTORY_SLOT_OFFSET_Y);

        y += 28;
        addOffsetRow(centerX, y, "HUD Slot X", BackSlotClientConfig.HUD_SLOT_OFFSET_X);

        y += 28;
        addOffsetRow(centerX, y, "HUD Slot Y", BackSlotClientConfig.HUD_SLOT_OFFSET_Y);

        y += 34;
        Button editorButton = Button.builder(Component.literal("Open Editor"), button -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player != null) {
                minecraft.setScreen(new BackSlotEditorScreen());
            }
        }).bounds(centerX - 100, y, 200, 20).build();
        editorButton.active = Minecraft.getInstance().player != null;
        addRenderableWidget(editorButton);

        y = this.height - 56;
        addRenderableWidget(Button.builder(Component.literal("Reset Slot Positions (0)"), button -> {
            BackSlotClientConfig.INVENTORY_SLOT_OFFSET_X.set(0);
            BackSlotClientConfig.INVENTORY_SLOT_OFFSET_Y.set(0);
            BackSlotClientConfig.HUD_SLOT_OFFSET_X.set(0);
            BackSlotClientConfig.HUD_SLOT_OFFSET_Y.set(0);
            refreshWidgets();
        }).bounds(centerX - 181, y, 150, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Save"), button -> {
            BackSlotCommonConfig.save();
            BackSlotClientConfig.SPEC.save();
            this.minecraft.setScreen(parent);
        }).bounds(centerX - 24, y, 98, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Cancel"), button -> {
            BackSlotCommonConfig.load();
            BackSlotClientConfig.load();
            this.minecraft.setScreen(parent);
        }).bounds(centerX + 81, y, 98, 20).build());
    }

    private void addScaleRow(int centerX, int y, String label, BackSlotClientConfig.DoubleValue value) {
        addRenderableWidget(Button.builder(Component.literal("-"), button -> {
            value.set(clampScale(value.get() - SCALE_STEP));
            refreshWidgets();
        }).bounds(centerX - 100, y, 40, 20).build());

        addRenderableWidget(Button.builder(Component.literal(label + ": " + format(value.get())), button -> {
        }).bounds(centerX - 54, y, 108, 20).build());

        addRenderableWidget(Button.builder(Component.literal("+"), button -> {
            value.set(clampScale(value.get() + SCALE_STEP));
            refreshWidgets();
        }).bounds(centerX + 60, y, 40, 20).build());
    }

    private void addOffsetRow(int centerX, int y, String label, BackSlotClientConfig.IntValue value) {
        addRenderableWidget(Button.builder(Component.literal("-"), button -> {
            value.set(value.get() - OFFSET_STEP);
            refreshWidgets();
        }).bounds(centerX - 130, y, 40, 20).build());

        addRenderableWidget(Button.builder(Component.literal(label + ": " + value.get()), button -> {
        }).bounds(centerX - 84, y, 168, 20).build());

        addRenderableWidget(Button.builder(Component.literal("+"), button -> {
            value.set(value.get() + OFFSET_STEP);
            refreshWidgets();
        }).bounds(centerX + 90, y, 40, 20).build());
    }

    private void refreshWidgets() {
        clearWidgets();
        init();
    }

    private Component keepItemsLabel() {
        return Component.literal("Keep Items On Death: "
                + (BackSlotCommonConfig.KEEP_ITEMS_ON_DEATH.get() ? "ON" : "OFF (Default)"));
    }

    private static double clampScale(double value) {
        return Math.max(0.1D, Math.min(5.0D, Math.round(value * 100.0D) / 100.0D));
    }

    private static String format(double value) {
        return String.format(Locale.ROOT, "%.2f", value);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        graphics.drawCenteredString(this.font, "Detailed item transforms are edited in the NeoBackSlot Editor.", this.width / 2, 34, 0xA0A0A0);
        graphics.drawCenteredString(this.font, "Inventory & HUD Slot Positions", this.width / 2, positionSectionTitleY, 0xFFFF55);
    }

    @Override
    public void onClose() {
        BackSlotCommonConfig.load();
        BackSlotClientConfig.load();
        this.minecraft.setScreen(parent);
    }
}
