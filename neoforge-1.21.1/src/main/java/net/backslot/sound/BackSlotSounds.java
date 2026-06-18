package net.backslot.sound;

import net.backslot.BackSlotMain;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BackSlotSounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, BackSlotMain.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> PACK_UP_ITEM =
            SOUND_EVENTS.register(
                    "pack_up_item",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(
                                    BackSlotMain.MOD_ID,
                                    "pack_up_item"
                            )
                    )
            );

    public static final DeferredHolder<SoundEvent, SoundEvent> SHEATH_SWORD =
            SOUND_EVENTS.register(
                    "sheath_sword",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(
                                    BackSlotMain.MOD_ID,
                                    "sheath_sword"
                            )
                    )
            );

    public static void register(IEventBus bus) {
        SOUND_EVENTS.register(bus);
    }
}