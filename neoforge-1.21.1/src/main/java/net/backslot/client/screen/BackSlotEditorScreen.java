package net.backslot.client.screen;

import net.backslot.client.ClientTransformSync;
import net.backslot.client.data.ClientBackSlotData;
import net.backslot.client.profile.TransformProfile;
import net.backslot.client.profile.TransformProfileManager;
import net.backslot.client.widget.PlayerPreviewWidget;
import net.backslot.config.BackSlotClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.ItemStack;

public class BackSlotEditorScreen extends Screen {

    private enum Target {
        BACK,
        BELT
    }

    private enum EditScope {
        GLOBAL,
        ITEM
    }

    private enum Parameter {
        OFFSET_X("Offset X"),
        OFFSET_Y("Offset Y"),
        OFFSET_Z("Offset Z"),
        ROTATION_X("Rotation X"),
        ROTATION_Y("Rotation Y"),
        ROTATION_Z("Rotation Z"),
        SCALE("Scale");

        private final String label;

        Parameter(String label) {
            this.label = label;
        }
    }

    private Target target = Target.BACK;
    private EditScope editScope = EditScope.GLOBAL;
    private Parameter selectedParameter = Parameter.OFFSET_X;
    private float previewYaw = 0.0F;
    private boolean saved = false;

    private double originalBackScale;
    private double originalBackOffsetX;
    private double originalBackOffsetY;
    private double originalBackOffsetZ;
    private double originalBackRotationX;
    private double originalBackRotationY;
    private double originalBackRotationZ;

    private double originalBeltScale;
    private double originalBeltOffsetX;
    private double originalBeltOffsetY;
    private double originalBeltOffsetZ;
    private double originalBeltRotationX;
    private double originalBeltRotationY;
    private double originalBeltRotationZ;
    private TransformProfileManager.Snapshot originalItemProfiles;

    public BackSlotEditorScreen() {
        super(Component.literal("NeoBackSlot Editor"));
    }

    @Override
    protected void init() {
        snapshotOriginalValues();
        buildEditor();
    }

    private void buildEditor() {
        clearWidgets();

        boolean compact = isCompactLayout();
        int centerX = this.width / 2;
        int topY = compact ? 18 : 38;
        int footerY = this.height - (compact ? 42 : 54);
        int stepY = footerY - (compact ? 78 : 92);

        int previewX = centerX - (compact ? 230 : 255);
        int previewY = topY;
        int previewWidth = compact ? 150 : 180;
        int previewHeight = compact ? 160 : 240;

        int controlX = centerX - 45;
        int controlY = topY;
        int scopeY = controlY + (compact ? 34 : 44);
        int parameterY = controlY + (compact ? 88 : 122);

        addRenderableWidget(new PlayerPreviewWidget(
                previewX,
                previewY,
                previewWidth,
                previewHeight,
                compact ? 42 : 55,
                previewYaw
        ));

        addPreviewRotationButtons(previewX, previewY + previewHeight + 8);
        addTargetButtons(controlX, controlY);
        addScopeButtons(controlX, scopeY);
        addParameterButtons(controlX, parameterY);
        addStepButtons(controlX, stepY);
        addFooterButtons(centerX, footerY);
    }

    private void addPreviewRotationButtons(int x, int y) {
        if (this.height < 320) {
            return;
        }

        boolean compact = isCompactLayout();
        int buttonWidth = compact ? 72 : 84;
        int buttonHeight = compact ? 18 : 20;
        int secondColumnX = compact ? 78 : 96;
        int secondRowY = compact ? 22 : 26;

        addRenderableWidget(Button.builder(
                Component.literal(formatPreviewDirectionLabel("Left", 270.0F)),
                button -> {
                    previewYaw = 270.0F;
                    buildEditor();
                }
        ).bounds(x, y, buttonWidth, buttonHeight).build());

        addRenderableWidget(Button.builder(
                Component.literal(formatPreviewDirectionLabel("Right", 90.0F)),
                button -> {
                    previewYaw = 90.0F;
                    buildEditor();
                }
        ).bounds(x + secondColumnX, y, buttonWidth, buttonHeight).build());

        addRenderableWidget(Button.builder(
                Component.literal(formatPreviewDirectionLabel("Front", 0.0F)),
                button -> {
                    previewYaw = 0.0F;
                    buildEditor();
                }
        ).bounds(x, y + secondRowY, buttonWidth, buttonHeight).build());

        addRenderableWidget(Button.builder(
                Component.literal(formatPreviewDirectionLabel("Back", 180.0F)),
                button -> {
                    previewYaw = 180.0F;
                    buildEditor();
                }
        ).bounds(x + secondColumnX, y + secondRowY, buttonWidth, buttonHeight).build());
    }

    private void addTargetButtons(int x, int y) {
        int buttonHeight = isCompactLayout() ? 18 : 20;

        addRenderableWidget(Button.builder(
                Component.literal(target == Target.BACK ? "> Back <" : "Back"),
                button -> {
                    target = Target.BACK;
                    if (getCurrentItem().isEmpty()) {
                        editScope = EditScope.GLOBAL;
                    }
                    buildEditor();
                }
        ).bounds(x, y, 95, buttonHeight).build());

        addRenderableWidget(Button.builder(
                Component.literal(target == Target.BELT ? "> Belt <" : "Belt"),
                button -> {
                    target = Target.BELT;
                    if (getCurrentItem().isEmpty()) {
                        editScope = EditScope.GLOBAL;
                    }
                    buildEditor();
                }
        ).bounds(x + 105, y, 95, buttonHeight).build());
    }

    private void addScopeButtons(int x, int y) {
        boolean hasItem = !getCurrentItem().isEmpty();
        int buttonHeight = isCompactLayout() ? 18 : 20;

        addRenderableWidget(Button.builder(
                Component.literal(editScope == EditScope.GLOBAL ? "> All Items <" : "All Items"),
                button -> {
                    editScope = EditScope.GLOBAL;
                    buildEditor();
                }
        ).bounds(x, y, 95, buttonHeight).build());

        Button itemButton = Button.builder(
                Component.literal(editScope == EditScope.ITEM ? "> This Item <" : "This Item"),
                button -> {
                    if (!getCurrentItem().isEmpty()) {
                        editScope = EditScope.ITEM;
                        buildEditor();
                    }
                }
        ).bounds(x + 105, y, 95, buttonHeight).build();

        itemButton.active = hasItem;
        addRenderableWidget(itemButton);
    }

    private void addParameterButtons(int x, int y) {
        boolean compact = isCompactLayout();
        int buttonWidth = 200;
        int buttonHeight = compact ? 14 : 18;
        int rowGap = compact ? 16 : 21;
        int sectionGap = compact ? 3 : 8;

        addParameterButton(Parameter.OFFSET_X, x, y, buttonWidth, buttonHeight);
        addParameterButton(Parameter.OFFSET_Y, x, y + rowGap, buttonWidth, buttonHeight);
        addParameterButton(Parameter.OFFSET_Z, x, y + rowGap * 2, buttonWidth, buttonHeight);

        int rotationY = y + rowGap * 3 + sectionGap;
        addParameterButton(Parameter.ROTATION_X, x, rotationY, buttonWidth, buttonHeight);
        addParameterButton(Parameter.ROTATION_Y, x, rotationY + rowGap, buttonWidth, buttonHeight);
        addParameterButton(Parameter.ROTATION_Z, x, rotationY + rowGap * 2, buttonWidth, buttonHeight);

        addParameterButton(Parameter.SCALE, x, rotationY + rowGap * 3 + sectionGap, buttonWidth, buttonHeight);
    }

    private void addParameterButton(Parameter parameter, int x, int y, int width, int height) {
        addRenderableWidget(Button.builder(
                Component.literal(selectedParameter == parameter ? "> " + parameter.label : "  " + parameter.label),
                button -> {
                    selectedParameter = parameter;
                    buildEditor();
                }
        ).bounds(x, y, width, height).build());
    }

    private void addStepButtons(int x, int y) {
        double[] steps = getStepsForSelectedParameter();

        boolean compact = isCompactLayout();
        int buttonWidth = 62;
        int buttonHeight = compact ? 17 : 20;
        int gap = 68;
        int rowGap = compact ? 20 : 24;

        for (int i = 0; i < steps.length; i++) {
            double step = steps[i];

            int row = i / 3;
            int col = i % 3;

            addRenderableWidget(Button.builder(
                    Component.literal(formatStep(step)),
                    button -> adjustSelectedValue(step)
            ).bounds(
                    x + col * gap,
                    y + row * rowGap,
                    buttonWidth,
                    buttonHeight
            ).build());
        }

        addRenderableWidget(Button.builder(
                Component.literal("Reset Parameter"),
                button -> resetSelectedParameter()
        ).bounds(x, y + (compact ? 44 : 54), 198, buttonHeight).build());
    }

    private void addFooterButtons(int centerX, int y) {
        boolean compact = isCompactLayout();
        int buttonHeight = compact ? 17 : 20;
        int saveY = y + (compact ? 21 : 26);

        Button resetTargetButton = Button.builder(
                getResetTargetMessage(),
                button -> {
                    resetCurrentTarget();
                    buildEditor();
                }
        ).bounds(centerX - 170, y, 340, buttonHeight).build();

        resetTargetButton.active = editScope != EditScope.ITEM || hasCurrentItemProfile();
        addRenderableWidget(resetTargetButton);

        addRenderableWidget(Button.builder(
                Component.literal("Save"),
                button -> {
                    BackSlotClientConfig.SPEC.save();
                    TransformProfileManager.saveProfiles();
                    ClientTransformSync.sendActiveTransformsToServer();
                    saved = true;
                    Minecraft.getInstance().setScreen(null);
                }
        ).bounds(centerX - 170, saveY, 165, buttonHeight).build());

        addRenderableWidget(Button.builder(
                Component.literal("Cancel"),
                button -> {
                    restoreOriginalValues();
                    ClientTransformSync.sendActiveTransformsToServer();
                    Minecraft.getInstance().setScreen(null);
                }
        ).bounds(centerX + 5, saveY, 165, buttonHeight).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        boolean compact = isCompactLayout();
        int centerX = this.width / 2;
        int topY = compact ? 18 : 38;
        int titleY = compact ? 1 : 18;
        int controlX = centerX - 45;
        int controlY = topY;
        int previewX = centerX - (compact ? 230 : 255);
        int previewY = topY;
        int previewHeight = compact ? 160 : 240;
        int parameterLabelY = controlY + (compact ? 72 : 106);
        int parameterButtonY = controlY + (compact ? 88 : 122);
        int footerY = this.height - (compact ? 42 : 54);
        int stepY = footerY - (compact ? 78 : 92);
        int valueX = compact ? previewX : controlX;
        int valueCenterX = compact ? previewX + 75 : controlX + 100;
        int valueY = compact ? previewY + previewHeight + 54 : stepY - 48;

        guiGraphics.drawCenteredString(
                this.font,
                this.title,
                centerX,
                titleY,
                0xFFFFFF
        );

        if (!compact) {
            guiGraphics.drawString(
                    this.font,
                    "Target",
                    controlX,
                    controlY - 11,
                    0xFFFFFF
            );
        }

        guiGraphics.drawString(
                this.font,
                "Scope",
                controlX,
                controlY + (compact ? 23 : 33),
                0xFFFFFF
        );

        drawClippedScrollingString(
                guiGraphics,
                getScopeLabel(),
                controlX,
                controlY + (compact ? 58 : 74),
                200,
                editScope == EditScope.ITEM ? 0xFFFFAA : 0xCCCCCC
        );

        String profileStatusLabel = getProfileStatusLabel();
        if (!profileStatusLabel.isEmpty()) {
            guiGraphics.drawString(
                    this.font,
                    profileStatusLabel,
                    controlX,
                    controlY + (compact ? 68 : 86),
                    getProfileStatusColor()
            );
        }

        guiGraphics.drawString(
                this.font,
                "Transform",
                controlX,
                parameterLabelY,
                0xFFFFFF
        );

        guiGraphics.hLine(controlX, controlX + 199, parameterButtonY + (compact ? 50 : 68), 0xFF777777);
        guiGraphics.hLine(controlX, controlX + 199, parameterButtonY + (compact ? 103 : 139), 0xFF777777);

        guiGraphics.drawString(
                this.font,
                "Current",
                valueX,
                valueY,
                0xCCCCCC
        );

        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(2.0F, 2.0F, 1.0F);
        guiGraphics.drawCenteredString(
                this.font,
                formatValue(getSelectedValue()),
                valueCenterX / 2,
                (valueY + 15) / 2,
                0xFFFFAA
        );
        guiGraphics.pose().popPose();
    }

    private boolean isCompactLayout() {
        return this.height < 390;
    }

    private double[] getStepsForSelectedParameter() {
        return switch (selectedParameter) {
            case OFFSET_X, OFFSET_Y, OFFSET_Z -> new double[]{
                    -1.0D, -0.10D, -0.01D,
                    0.01D, 0.10D, 1.0D
            };
            case ROTATION_X, ROTATION_Y, ROTATION_Z -> new double[]{
                    -45.0D, -5.0D, -1.0D,
                    1.0D, 5.0D, 45.0D
            };
            case SCALE -> new double[]{
                    -0.10D, -0.01D,
                    0.01D, 0.10D
            };
        };
    }

    private void adjustSelectedValue(double step) {
        setSelectedValue(clamp(getSelectedValue() + step, getMinValue(), getMaxValue()));
    }

    private void resetSelectedParameter() {
        if (editScope == EditScope.ITEM && !hasCurrentItemProfile()) {
            return;
        }

        setSelectedValue(getDefaultValue());
    }

    private double getSelectedValue() {
        if (editScope == EditScope.ITEM) {
            TransformProfile profile = getCurrentItemProfile();
            return switch (selectedParameter) {
                case OFFSET_X -> profile.offsetX();
                case OFFSET_Y -> profile.offsetY();
                case OFFSET_Z -> profile.offsetZ();
                case ROTATION_X -> profile.rotationX();
                case ROTATION_Y -> profile.rotationY();
                case ROTATION_Z -> profile.rotationZ();
                case SCALE -> profile.scale();
            };
        }

        return switch (selectedParameter) {
            case OFFSET_X -> getOffsetX();
            case OFFSET_Y -> getOffsetY();
            case OFFSET_Z -> getOffsetZ();
            case ROTATION_X -> getRotationX();
            case ROTATION_Y -> getRotationY();
            case ROTATION_Z -> getRotationZ();
            case SCALE -> getScale();
        };
    }

    private void setSelectedValue(double value) {
        if (editScope == EditScope.ITEM) {
            setCurrentItemProfileValue(value);
            ClientTransformSync.sendActiveTransformsToServer();
            return;
        }

        switch (selectedParameter) {
            case OFFSET_X -> setOffsetX(value);
            case OFFSET_Y -> setOffsetY(value);
            case OFFSET_Z -> setOffsetZ(value);
            case ROTATION_X -> setRotationX(value);
            case ROTATION_Y -> setRotationY(value);
            case ROTATION_Z -> setRotationZ(value);
            case SCALE -> setScale(value);
        }

        ClientTransformSync.sendActiveTransformsToServer();
    }

    private double getMinValue() {
        return switch (selectedParameter) {
            case OFFSET_X, OFFSET_Y, OFFSET_Z -> -2.0D;
            case ROTATION_X, ROTATION_Y, ROTATION_Z -> -360.0D;
            case SCALE -> 0.1D;
        };
    }

    private double getMaxValue() {
        return switch (selectedParameter) {
            case OFFSET_X, OFFSET_Y, OFFSET_Z -> 2.0D;
            case ROTATION_X, ROTATION_Y, ROTATION_Z -> 360.0D;
            case SCALE -> 5.0D;
        };
    }

    private double getDefaultValue() {
        if (editScope == EditScope.ITEM) {
            return selectedParameter == Parameter.SCALE ? 1.0D : 0.0D;
        }

        return switch (selectedParameter) {
            case OFFSET_X, OFFSET_Y, OFFSET_Z -> 0.0D;
            case ROTATION_X, ROTATION_Y, ROTATION_Z -> 0.0D;
            case SCALE -> target == Target.BACK ? 0.95D : 0.85D;
        };
    }

    private void snapshotOriginalValues() {
        originalBackScale = BackSlotClientConfig.BACK_SLOT_SCALE.get();
        originalBackOffsetX = BackSlotClientConfig.BACK_SLOT_OFFSET_X.get();
        originalBackOffsetY = BackSlotClientConfig.BACK_SLOT_OFFSET_Y.get();
        originalBackOffsetZ = BackSlotClientConfig.BACK_SLOT_OFFSET_Z.get();
        originalBackRotationX = BackSlotClientConfig.BACK_SLOT_ROTATION_X.get();
        originalBackRotationY = BackSlotClientConfig.BACK_SLOT_ROTATION_Y.get();
        originalBackRotationZ = BackSlotClientConfig.BACK_SLOT_ROTATION_Z.get();

        originalBeltScale = BackSlotClientConfig.BELT_SLOT_SCALE.get();
        originalBeltOffsetX = BackSlotClientConfig.BELT_SLOT_OFFSET_X.get();
        originalBeltOffsetY = BackSlotClientConfig.BELT_SLOT_OFFSET_Y.get();
        originalBeltOffsetZ = BackSlotClientConfig.BELT_SLOT_OFFSET_Z.get();
        originalBeltRotationX = BackSlotClientConfig.BELT_SLOT_ROTATION_X.get();
        originalBeltRotationY = BackSlotClientConfig.BELT_SLOT_ROTATION_Y.get();
        originalBeltRotationZ = BackSlotClientConfig.BELT_SLOT_ROTATION_Z.get();

        originalItemProfiles = TransformProfileManager.snapshotItemProfiles();
    }

    private void restoreOriginalValues() {
        BackSlotClientConfig.BACK_SLOT_SCALE.set(originalBackScale);
        BackSlotClientConfig.BACK_SLOT_OFFSET_X.set(originalBackOffsetX);
        BackSlotClientConfig.BACK_SLOT_OFFSET_Y.set(originalBackOffsetY);
        BackSlotClientConfig.BACK_SLOT_OFFSET_Z.set(originalBackOffsetZ);
        BackSlotClientConfig.BACK_SLOT_ROTATION_X.set(originalBackRotationX);
        BackSlotClientConfig.BACK_SLOT_ROTATION_Y.set(originalBackRotationY);
        BackSlotClientConfig.BACK_SLOT_ROTATION_Z.set(originalBackRotationZ);

        BackSlotClientConfig.BELT_SLOT_SCALE.set(originalBeltScale);
        BackSlotClientConfig.BELT_SLOT_OFFSET_X.set(originalBeltOffsetX);
        BackSlotClientConfig.BELT_SLOT_OFFSET_Y.set(originalBeltOffsetY);
        BackSlotClientConfig.BELT_SLOT_OFFSET_Z.set(originalBeltOffsetZ);
        BackSlotClientConfig.BELT_SLOT_ROTATION_X.set(originalBeltRotationX);
        BackSlotClientConfig.BELT_SLOT_ROTATION_Y.set(originalBeltRotationY);
        BackSlotClientConfig.BELT_SLOT_ROTATION_Z.set(originalBeltRotationZ);

        if (originalItemProfiles != null) {
            TransformProfileManager.restoreItemProfiles(originalItemProfiles);
        }
    }

    private void resetCurrentTarget() {
        if (editScope == EditScope.ITEM) {
            removeCurrentItemProfile();
            ClientTransformSync.sendActiveTransformsToServer();
            return;
        }

        if (target == Target.BACK) {
            BackSlotClientConfig.BACK_SLOT_SCALE.set(0.95D);
            BackSlotClientConfig.BACK_SLOT_OFFSET_X.set(0.0D);
            BackSlotClientConfig.BACK_SLOT_OFFSET_Y.set(0.0D);
            BackSlotClientConfig.BACK_SLOT_OFFSET_Z.set(0.0D);
            BackSlotClientConfig.BACK_SLOT_ROTATION_X.set(0.0D);
            BackSlotClientConfig.BACK_SLOT_ROTATION_Y.set(0.0D);
            BackSlotClientConfig.BACK_SLOT_ROTATION_Z.set(0.0D);
        } else {
            BackSlotClientConfig.BELT_SLOT_SCALE.set(0.85D);
            BackSlotClientConfig.BELT_SLOT_OFFSET_X.set(0.0D);
            BackSlotClientConfig.BELT_SLOT_OFFSET_Y.set(0.0D);
            BackSlotClientConfig.BELT_SLOT_OFFSET_Z.set(0.0D);
            BackSlotClientConfig.BELT_SLOT_ROTATION_X.set(0.0D);
            BackSlotClientConfig.BELT_SLOT_ROTATION_Y.set(0.0D);
            BackSlotClientConfig.BELT_SLOT_ROTATION_Z.set(0.0D);
        }

        ClientTransformSync.sendActiveTransformsToServer();
    }

    private double getScale() {
        return target == Target.BACK ? BackSlotClientConfig.BACK_SLOT_SCALE.get() : BackSlotClientConfig.BELT_SLOT_SCALE.get();
    }

    private double getOffsetX() {
        return target == Target.BACK ? BackSlotClientConfig.BACK_SLOT_OFFSET_X.get() : BackSlotClientConfig.BELT_SLOT_OFFSET_X.get();
    }

    private double getOffsetY() {
        return target == Target.BACK ? BackSlotClientConfig.BACK_SLOT_OFFSET_Y.get() : BackSlotClientConfig.BELT_SLOT_OFFSET_Y.get();
    }

    private double getOffsetZ() {
        return target == Target.BACK ? BackSlotClientConfig.BACK_SLOT_OFFSET_Z.get() : BackSlotClientConfig.BELT_SLOT_OFFSET_Z.get();
    }

    private double getRotationX() {
        return target == Target.BACK ? BackSlotClientConfig.BACK_SLOT_ROTATION_X.get() : BackSlotClientConfig.BELT_SLOT_ROTATION_X.get();
    }

    private double getRotationY() {
        return target == Target.BACK ? BackSlotClientConfig.BACK_SLOT_ROTATION_Y.get() : BackSlotClientConfig.BELT_SLOT_ROTATION_Y.get();
    }

    private double getRotationZ() {
        return target == Target.BACK ? BackSlotClientConfig.BACK_SLOT_ROTATION_Z.get() : BackSlotClientConfig.BELT_SLOT_ROTATION_Z.get();
    }

    private void setScale(double value) {
        if (target == Target.BACK) {
            BackSlotClientConfig.BACK_SLOT_SCALE.set(value);
        } else {
            BackSlotClientConfig.BELT_SLOT_SCALE.set(value);
        }
    }

    private void setOffsetX(double value) {
        if (target == Target.BACK) {
            BackSlotClientConfig.BACK_SLOT_OFFSET_X.set(value);
        } else {
            BackSlotClientConfig.BELT_SLOT_OFFSET_X.set(value);
        }
    }

    private void setOffsetY(double value) {
        if (target == Target.BACK) {
            BackSlotClientConfig.BACK_SLOT_OFFSET_Y.set(value);
        } else {
            BackSlotClientConfig.BELT_SLOT_OFFSET_Y.set(value);
        }
    }

    private void setOffsetZ(double value) {
        if (target == Target.BACK) {
            BackSlotClientConfig.BACK_SLOT_OFFSET_Z.set(value);
        } else {
            BackSlotClientConfig.BELT_SLOT_OFFSET_Z.set(value);
        }
    }

    private void setRotationX(double value) {
        if (target == Target.BACK) {
            BackSlotClientConfig.BACK_SLOT_ROTATION_X.set(value);
        } else {
            BackSlotClientConfig.BELT_SLOT_ROTATION_X.set(value);
        }
    }

    private void setRotationY(double value) {
        if (target == Target.BACK) {
            BackSlotClientConfig.BACK_SLOT_ROTATION_Y.set(value);
        } else {
            BackSlotClientConfig.BELT_SLOT_ROTATION_Y.set(value);
        }
    }

    private void setRotationZ(double value) {
        if (target == Target.BACK) {
            BackSlotClientConfig.BACK_SLOT_ROTATION_Z.set(value);
        } else {
            BackSlotClientConfig.BELT_SLOT_ROTATION_Z.set(value);
        }
    }

    private ItemStack getCurrentItem() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return ItemStack.EMPTY;
        }

        return target == Target.BACK
                ? ClientBackSlotData.getBackSlot(minecraft.player.getUUID())
                : ClientBackSlotData.getBeltSlot(minecraft.player.getUUID());
    }

    private ResourceLocation getCurrentItemId() {
        ItemStack stack = getCurrentItem();
        return stack.isEmpty() ? null : BuiltInRegistries.ITEM.getKey(stack.getItem());
    }

    private TransformProfile getCurrentItemProfile() {
        ResourceLocation itemId = getCurrentItemId();
        if (itemId == null) {
            return new TransformProfile(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D);
        }

        return TransformProfileManager.getItemProfile(itemId, getProfileSlot())
                .orElseGet(() -> new TransformProfile(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D));
    }

    private void setCurrentItemProfile(TransformProfile profile) {
        ResourceLocation itemId = getCurrentItemId();
        if (itemId != null) {
            TransformProfileManager.setItemProfile(itemId, getProfileSlot(), profile);
        }
    }

    private void removeCurrentItemProfile() {
        ResourceLocation itemId = getCurrentItemId();
        if (itemId != null) {
            TransformProfileManager.removeItemProfile(itemId, getProfileSlot());
        }
    }

    private void setCurrentItemProfileValue(double value) {
        TransformProfile profile = getCurrentItemProfile();
        setCurrentItemProfile(switch (selectedParameter) {
            case OFFSET_X -> new TransformProfile(value, profile.offsetY(), profile.offsetZ(), profile.rotationX(), profile.rotationY(), profile.rotationZ(), profile.scale());
            case OFFSET_Y -> new TransformProfile(profile.offsetX(), value, profile.offsetZ(), profile.rotationX(), profile.rotationY(), profile.rotationZ(), profile.scale());
            case OFFSET_Z -> new TransformProfile(profile.offsetX(), profile.offsetY(), value, profile.rotationX(), profile.rotationY(), profile.rotationZ(), profile.scale());
            case ROTATION_X -> new TransformProfile(profile.offsetX(), profile.offsetY(), profile.offsetZ(), value, profile.rotationY(), profile.rotationZ(), profile.scale());
            case ROTATION_Y -> new TransformProfile(profile.offsetX(), profile.offsetY(), profile.offsetZ(), profile.rotationX(), value, profile.rotationZ(), profile.scale());
            case ROTATION_Z -> new TransformProfile(profile.offsetX(), profile.offsetY(), profile.offsetZ(), profile.rotationX(), profile.rotationY(), value, profile.scale());
            case SCALE -> new TransformProfile(profile.offsetX(), profile.offsetY(), profile.offsetZ(), profile.rotationX(), profile.rotationY(), profile.rotationZ(), value);
        });
    }

    private String getScopeLabel() {
        if (editScope == EditScope.GLOBAL) {
            return target == Target.BACK ? "Applies to all Back Slot items" : "Applies to all Belt Slot items";
        }

        ResourceLocation itemId = getCurrentItemId();
        return itemId == null ? "No item selected" : "Only " + itemId;
    }

    private Component getResetTargetMessage() {
        if (editScope == EditScope.ITEM) {
            Component message = Component.literal("Remove Item Profile");
            return hasCurrentItemProfile() ? message.copy().withStyle(ChatFormatting.RED) : message;
        }

        return Component.literal(target == Target.BACK ? "Reset Back Defaults" : "Reset Belt Defaults");
    }

    private String getProfileStatusLabel() {
        if (editScope == EditScope.ITEM) {
            return hasCurrentItemProfile() ? "Custom Profile Active" : "No Custom Profile";
        }

        return hasCurrentItemProfile() ? "This item uses a custom profile" : "";
    }

    private int getProfileStatusColor() {
        return editScope == EditScope.ITEM && hasCurrentItemProfile() ? 0xFF5555 : 0xFFFFAA;
    }

    private boolean hasCurrentItemProfile() {
        ResourceLocation itemId = getCurrentItemId();
        return itemId != null && TransformProfileManager.getItemProfile(itemId, getProfileSlot()).isPresent();
    }

    private TransformProfileManager.Slot getProfileSlot() {
        return target == Target.BACK ? TransformProfileManager.Slot.BACK : TransformProfileManager.Slot.BELT;
    }

    private void drawClippedScrollingString(GuiGraphics guiGraphics, String text, int x, int y, int width, int color) {
        int textWidth = this.font.width(text);
        guiGraphics.enableScissor(x, y, x + width, y + 10);

        if (textWidth <= width) {
            guiGraphics.drawString(this.font, text, x, y, color);
        } else {
            int overflow = textWidth - width;
            long cycle = 4000L;
            long hold = 900L;
            long time = System.currentTimeMillis() % cycle;
            int offset;

            if (time < hold) {
                offset = 0;
            } else if (time > cycle - hold) {
                offset = overflow;
            } else {
                double progress = (double) (time - hold) / (double) (cycle - hold * 2L);
                offset = (int) Math.round(progress * overflow);
            }

            guiGraphics.drawString(this.font, text, x - offset, y, color);
        }

        guiGraphics.disableScissor();
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private static String formatValue(double value) {
        return String.format("%.2f", value);
    }

    private String formatPreviewDirectionLabel(String label, float yaw) {
        return previewYaw == yaw ? "> " + label + " <" : label;
    }

    private static String formatStep(double value) {
        if (Math.abs(value) >= 1.0D) {
            return value > 0.0D ? "+" + String.format("%.0f", value) : String.format("%.0f", value);
        }

        return value > 0.0D ? "+" + String.format("%.2f", value) : String.format("%.2f", value);
    }

    @Override
    public void onClose() {
        if (!saved) {
            restoreOriginalValues();
            ClientTransformSync.sendActiveTransformsToServer();
        }

        super.onClose();
    }
}
