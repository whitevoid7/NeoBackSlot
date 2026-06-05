package net.backslot.client;

import net.backslot.BackSlotMain;
import net.backslot.network.SwapBackSlotPayload;
import net.backslot.network.SwapBeltSlotPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = BackSlotMain.MOD_ID, value = Dist.CLIENT)
public class BackSlotClient {

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(BackSlotKeybinds.SWAP_BACK_SLOT);
        event.register(BackSlotKeybinds.SWAP_BELT_SLOT);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        while (BackSlotKeybinds.SWAP_BACK_SLOT.consumeClick()) {
            PacketDistributor.sendToServer(new SwapBackSlotPayload());
        }

        while (BackSlotKeybinds.SWAP_BELT_SLOT.consumeClick()) {
            PacketDistributor.sendToServer(new SwapBeltSlotPayload());
        }
    }
}