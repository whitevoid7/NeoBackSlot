package net.backslot.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class BackSlotConfig {

    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.BooleanValue KEEP_ITEMS_ON_DEATH;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("death");

        KEEP_ITEMS_ON_DEATH = builder
                .comment(
                        "Keep Back Slot and Belt Slot items after death.",
                        "Default: false",
                        "When false, items will be dropped normally on death.",
                        "Compatible grave mods such as YIGD may store these items inside graves.",
                        "Warning: If you die and cannot recover your dropped items or grave, the items may be permanently lost.",
                        "When true, items will remain equipped after respawn."
                )
                .define("keepItemsOnDeath", false);

        builder.pop();

        SPEC = builder.build();
    }
}