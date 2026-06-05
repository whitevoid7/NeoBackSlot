package net.backslot.attachment;

import net.backslot.BackSlotMain;
import net.backslot.data.BackSlotData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class BackSlotAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, BackSlotMain.MOD_ID);

    public static final Supplier<AttachmentType<BackSlotData>> BACK_SLOT_DATA =
            ATTACHMENT_TYPES.register("back_slot_data", () ->
                    AttachmentType.serializable(BackSlotData::new).build()
            );

    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }
}