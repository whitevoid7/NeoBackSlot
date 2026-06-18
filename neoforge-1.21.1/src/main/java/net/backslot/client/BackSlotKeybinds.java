package net.backslot.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class BackSlotKeybinds {
    public static final String CATEGORY = "key.categories.neobackslot";

    public static final KeyMapping SWAP_BACK_SLOT = new KeyMapping(
            "key.neobackslot.swap_back_slot",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            CATEGORY
    );

    public static final KeyMapping SWAP_BELT_SLOT = new KeyMapping(
            "key.neobackslot.swap_belt_slot",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            CATEGORY
    );

    public static final KeyMapping OPEN_EDITOR = new KeyMapping(
            "key.neobackslot.open_editor",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_N,
            CATEGORY
    );
}