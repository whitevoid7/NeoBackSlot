package net.backslot.client;

import net.backslot.BackSlotMain;
import net.backslot.client.config.BackSlotNeoForgeConfigScreen;
import net.backslot.client.screen.BackSlotEditorScreen;
import net.backslot.network.SwapBackSlotPayload;
import net.backslot.network.SwapBeltSlotPayload;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.network.PacketDistributor;

@Mod(value = BackSlotMain.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = BackSlotMain.MOD_ID, value = Dist.CLIENT)
public class BackSlotClient {

    public BackSlotClient(ModContainer modContainer) {
        IConfigScreenFactory configScreenFactory = (container, parent) -> new BackSlotNeoForgeConfigScreen(parent);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, configScreenFactory);
    }

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(BackSlotKeybinds.SWAP_BACK_SLOT);
        event.register(BackSlotKeybinds.SWAP_BELT_SLOT);
        event.register(BackSlotKeybinds.OPEN_EDITOR);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        while (BackSlotKeybinds.SWAP_BACK_SLOT.consumeClick()) {
            PacketDistributor.sendToServer(new SwapBackSlotPayload());
        }

        while (BackSlotKeybinds.SWAP_BELT_SLOT.consumeClick()) {
            PacketDistributor.sendToServer(new SwapBeltSlotPayload());
        }

        while (BackSlotKeybinds.OPEN_EDITOR.consumeClick()) {
            Minecraft minecraft = Minecraft.getInstance();

            if (minecraft.screen == null) {
                minecraft.setScreen(new BackSlotEditorScreen());
            }
        }
    }
}
