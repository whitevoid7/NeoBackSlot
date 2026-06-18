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

        ClientPlayNetworking.registerGlobalReceiver(BackSlotPackets.SyncSlotPayload.TYPE, (payload, context) -> {
            ClientBackSlotData.setBoth(payload.playerId(), payload.back(), payload.belt());
            if (context.player() != null && payload.playerId().equals(context.player().getUUID())) {
                ClientTransformSync.sendActiveTransformsToServer();
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(BackSlotPackets.SyncTransformPayload.TYPE, (payload, context) ->
                ClientTransformData.setBoth(payload.playerId(), payload.back(), payload.belt())
        );

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
                ClientPlayNetworking.send(new BackSlotPackets.SwapBackSlotPayload());
            }

            while (BackSlotKeybinds.SWAP_BELT_SLOT.consumeClick()) {
                ClientPlayNetworking.send(new BackSlotPackets.SwapBeltSlotPayload());
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
