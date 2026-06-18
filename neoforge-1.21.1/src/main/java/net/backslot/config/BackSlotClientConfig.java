package net.backslot.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class BackSlotClientConfig {

    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.DoubleValue BACK_SLOT_SCALE;
    public static final ModConfigSpec.DoubleValue BACK_SLOT_OFFSET_X;
    public static final ModConfigSpec.DoubleValue BACK_SLOT_OFFSET_Y;
    public static final ModConfigSpec.DoubleValue BACK_SLOT_OFFSET_Z;
    public static final ModConfigSpec.DoubleValue BACK_SLOT_ROTATION_X;
    public static final ModConfigSpec.DoubleValue BACK_SLOT_ROTATION_Y;
    public static final ModConfigSpec.DoubleValue BACK_SLOT_ROTATION_Z;

    public static final ModConfigSpec.DoubleValue BELT_SLOT_SCALE;
    public static final ModConfigSpec.DoubleValue BELT_SLOT_OFFSET_X;
    public static final ModConfigSpec.DoubleValue BELT_SLOT_OFFSET_Y;
    public static final ModConfigSpec.DoubleValue BELT_SLOT_OFFSET_Z;
    public static final ModConfigSpec.DoubleValue BELT_SLOT_ROTATION_X;
    public static final ModConfigSpec.DoubleValue BELT_SLOT_ROTATION_Y;
    public static final ModConfigSpec.DoubleValue BELT_SLOT_ROTATION_Z;

    public static final ModConfigSpec.IntValue INVENTORY_SLOT_OFFSET_X;
    public static final ModConfigSpec.IntValue INVENTORY_SLOT_OFFSET_Y;
    public static final ModConfigSpec.IntValue HUD_SLOT_OFFSET_X;
    public static final ModConfigSpec.IntValue HUD_SLOT_OFFSET_Y;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("render");

        builder.push("back");

        BACK_SLOT_SCALE = builder
                .comment("Scale of items rendered in the Back Slot.", "Default: 0.95")
                .defineInRange("scale", 0.95D, 0.1D, 5.0D);

        BACK_SLOT_OFFSET_X = builder
                .comment("Additional X offset for Back Slot item rendering.", "Default: 0.0")
                .defineInRange("offsetX", 0.0D, -2.0D, 2.0D);

        BACK_SLOT_OFFSET_Y = builder
                .comment("Additional Y offset for Back Slot item rendering.", "Default: 0.0")
                .defineInRange("offsetY", 0.0D, -2.0D, 2.0D);

        BACK_SLOT_OFFSET_Z = builder
                .comment("Additional Z offset for Back Slot item rendering.", "Default: 0.0")
                .defineInRange("offsetZ", 0.0D, -2.0D, 2.0D);

        BACK_SLOT_ROTATION_X = builder
                .comment("Additional X rotation for Back Slot item rendering in degrees.", "Default: 0.0")
                .defineInRange("rotationX", 0.0D, -360.0D, 360.0D);

        BACK_SLOT_ROTATION_Y = builder
                .comment("Additional Y rotation for Back Slot item rendering in degrees.", "Default: 0.0")
                .defineInRange("rotationY", 0.0D, -360.0D, 360.0D);

        BACK_SLOT_ROTATION_Z = builder
                .comment("Additional Z rotation for Back Slot item rendering in degrees.", "Default: 0.0")
                .defineInRange("rotationZ", 0.0D, -360.0D, 360.0D);

        builder.pop();

        builder.push("belt");

        BELT_SLOT_SCALE = builder
                .comment("Scale of items rendered in the Belt Slot.", "Default: 0.85")
                .defineInRange("scale", 0.85D, 0.1D, 5.0D);

        BELT_SLOT_OFFSET_X = builder
                .comment("Additional X offset for Belt Slot item rendering.", "Default: 0.0")
                .defineInRange("offsetX", 0.0D, -2.0D, 2.0D);

        BELT_SLOT_OFFSET_Y = builder
                .comment("Additional Y offset for Belt Slot item rendering.", "Default: 0.0")
                .defineInRange("offsetY", 0.0D, -2.0D, 2.0D);

        BELT_SLOT_OFFSET_Z = builder
                .comment("Additional Z offset for Belt Slot item rendering.", "Default: 0.0")
                .defineInRange("offsetZ", 0.0D, -2.0D, 2.0D);

        BELT_SLOT_ROTATION_X = builder
                .comment("Additional X rotation for Belt Slot item rendering in degrees.", "Default: 0.0")
                .defineInRange("rotationX", 0.0D, -360.0D, 360.0D);

        BELT_SLOT_ROTATION_Y = builder
                .comment("Additional Y rotation for Belt Slot item rendering in degrees.", "Default: 0.0")
                .defineInRange("rotationY", 0.0D, -360.0D, 360.0D);

        BELT_SLOT_ROTATION_Z = builder
                .comment("Additional Z rotation for Belt Slot item rendering in degrees.", "Default: 0.0")
                .defineInRange("rotationZ", 0.0D, -360.0D, 360.0D);

        builder.pop();

        builder.push("ui");

        builder.push("inventory");

        INVENTORY_SLOT_OFFSET_X = builder
                .comment(
                        "Moves the NeoBackSlot Back/Belt slots shown inside the player inventory screen.",
                        "X axis in pixels. Positive values move the slots right, negative values move them left.",
                        "Default: 0"
                )
                .defineInRange("slotOffsetX", 0, -300, 300);

        INVENTORY_SLOT_OFFSET_Y = builder
                .comment(
                        "Moves the NeoBackSlot Back/Belt slots shown inside the player inventory screen.",
                        "Y axis in pixels. Positive values move the slots down, negative values move them up.",
                        "Default: 0"
                )
                .defineInRange("slotOffsetY", 0, -300, 300);

        builder.pop();

        builder.push("hud");

        HUD_SLOT_OFFSET_X = builder
                .comment(
                        "Moves the NeoBackSlot HUD slots displayed next to the hotbar.",
                        "X axis in pixels. Positive values move the slots right, negative values move them left.",
                        "Default: 0"
                )
                .defineInRange("slotOffsetX", 0, -500, 500);

        HUD_SLOT_OFFSET_Y = builder
                .comment(
                        "Moves the NeoBackSlot HUD slots displayed next to the hotbar.",
                        "Y axis in pixels. Positive values move the slots down, negative values move them up.",
                        "Default: 0"
                )
                .defineInRange("slotOffsetY", 0, -300, 300);

        builder.pop();

        builder.pop();

        builder.pop();

        SPEC = builder.build();
    }
}
