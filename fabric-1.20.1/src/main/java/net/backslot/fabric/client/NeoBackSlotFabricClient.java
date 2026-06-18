package net.backslot.fabric.client;

import net.backslot.fabric.client.data.ClientBackSlotData;
import net.backslot.fabric.client.data.ClientTransformData;
import net.backslot.fabric.client.gui.BackSlotHudOverlay;
import net.backslot.fabric.client.gui.BackSlotInventoryOverlay;
import net.backslot.fabric.client.render.BackSlotRenderLayer;
import net.backslot.fabric.client.screen.BackSlotEditorScreen;
import net.backslot.fabric.config.BackSlotClientConfig;
import net.backslot.fabric.network.BackSlotPackets;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;

public class NeoBackSlotFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BackSlotClientConfig.load();

        KeyBindingHelper.registerKeyBinding(BackSlotKeybinds.SWAP_BACK_SLOT);
        KeyBindingHelper.registerKeyBinding(BackSlotKeybinds.SWAP_BELT_SLOT);
        KeyBindingHelper.registerKeyBinding(BackSlotKeybinds.OPEN_EDITOR);

        ClientPlayNetworking.registerGlobalReceiver(BackSlotPackets.SYNC_SLOT, (client, handler, buffer, responseSender) -> {
            BackSlotPackets.SlotSync payload = BackSlotPackets.readSlotSync(buffer);
            client.execute(() -> {
                ClientBackSlotData.setBoth(payload.playerId(), payload.back(), payload.belt());
                if (client.player != null && payload.playerId().equals(client.player.getUUID())) {
                    ClientTransformSync.sendActiveTransformsToServer();
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(BackSlotPackets.SYNC_TRANSFORM, (client, handler, buffer, responseSender) -> {
            BackSlotPackets.TransformSync payload = BackSlotPackets.readTransformSync(buffer);
            client.execute(() -> ClientTransformData.setBoth(payload.playerId(), payload.back(), payload.belt()));
        });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> ClientTransformSync.sendActiveTransformsToServer());

        HudRenderCallback.EVENT.register((graphics, tickDelta) -> BackSlotHudOverlay.render(graphics));

        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof InventoryScreen) {
                ScreenEvents.afterRender(screen).register(BackSlotInventoryOverlay::render);
                ScreenMouseEvents.allowMouseClick(screen).register(BackSlotInventoryOverlay::allowMouseClick);
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (BackSlotKeybinds.SWAP_BACK_SLOT.consumeClick()) {
                ClientPlayNetworking.send(BackSlotPackets.SWAP_BACK, PacketByteBufs.empty());
            }

            while (BackSlotKeybinds.SWAP_BELT_SLOT.consumeClick()) {
                ClientPlayNetworking.send(BackSlotPackets.SWAP_BELT, PacketByteBufs.empty());
            }

            while (BackSlotKeybinds.OPEN_EDITOR.consumeClick()) {
                Minecraft minecraft = Minecraft.getInstance();
                if (minecraft.screen == null) {
                    minecraft.setScreen(new BackSlotEditorScreen());
                }
            }
        });

        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
            if (entityRenderer instanceof PlayerRenderer playerRenderer) {
                registrationHelper.register(new BackSlotRenderLayer(playerRenderer));
            }
        });
    }
}
