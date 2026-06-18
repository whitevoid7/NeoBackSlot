package net.backslot.fabric.sound;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public final class BackSlotSounds {
    public static final ResourceLocation SHEATH_SWORD_ID = new ResourceLocation("neobackslot", "sheath_sword");
    public static final ResourceLocation PACK_UP_ITEM_ID = new ResourceLocation("neobackslot", "pack_up_item");

    public static final SoundEvent SHEATH_SWORD = SoundEvent.createVariableRangeEvent(SHEATH_SWORD_ID);
    public static final SoundEvent PACK_UP_ITEM = SoundEvent.createVariableRangeEvent(PACK_UP_ITEM_ID);

    private BackSlotSounds() {
    }

    public static void register() {
        Registry.register(BuiltInRegistries.SOUND_EVENT, SHEATH_SWORD_ID, SHEATH_SWORD);
        Registry.register(BuiltInRegistries.SOUND_EVENT, PACK_UP_ITEM_ID, PACK_UP_ITEM);
    }
}
