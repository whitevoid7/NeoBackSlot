package net.backslot.fabric;

import net.backslot.fabric.client.profile.TransformProfile;
import net.backslot.fabric.command.BackSlotCommands;
import net.backslot.fabric.config.BackSlotCommonConfig;
import net.backslot.fabric.data.BackSlotAccess;
import net.backslot.fabric.inventory.BackSlotInventoryHelper;
import net.backslot.fabric.network.BackSlotPackets;
import net.backslot.fabric.network.ServerTransformSync;
import net.backslot.fabric.sound.BackSlotSounds;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;

public class NeoBackSlotFabric implements ModInitializer {
    public static final String MOD_ID = "neobackslot";

    @Override
    public void onInitialize() {
        BackSlotCommonConfig.load();
        BackSlotSounds.register();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                BackSlotCommands.register(dispatcher)
        );

        ServerPlayNetworking.registerGlobalReceiver(BackSlotPackets.SWAP_BACK, (server, player, handler, buffer, responseSender) ->
                server.execute(() -> BackSlotInventoryHelper.swapBackSlot(player))
        );

        ServerPlayNetworking.registerGlobalReceiver(BackSlotPackets.SWAP_BELT, (server, player, handler, buffer, responseSender) ->
                server.execute(() -> BackSlotInventoryHelper.swapBeltSlot(player))
        );

        ServerPlayNetworking.registerGlobalReceiver(BackSlotPackets.CLICK_BACK, (server, player, handler, buffer, responseSender) ->
                server.execute(() -> BackSlotInventoryHelper.clickBackSlot(player))
        );

        ServerPlayNetworking.registerGlobalReceiver(BackSlotPackets.CLICK_BELT, (server, player, handler, buffer, responseSender) ->
                server.execute(() -> BackSlotInventoryHelper.clickBeltSlot(player))
        );

        ServerPlayNetworking.registerGlobalReceiver(BackSlotPackets.UPDATE_TRANSFORM, (server, player, handler, buffer, responseSender) -> {
            TransformProfile back = BackSlotPackets.readTransform(buffer);
            TransformProfile belt = BackSlotPackets.readTransform(buffer);
            server.execute(() -> ServerTransformSync.update(player, back, belt));
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.player;
            BackSlotInventoryHelper.sync(player);
            ServerTransformSync.syncKnownTransformsTo(player);
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
                ServerTransformSync.remove(handler.player.getUUID())
        );

        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof ServerPlayer player) {
                BackSlotInventoryHelper.sync(player);
                ServerTransformSync.syncKnownTransformsTo(player);
            }
        });

        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            BackSlotAccess oldData = (BackSlotAccess) oldPlayer;
            BackSlotAccess newData = (BackSlotAccess) newPlayer;
            boolean keepInventory = oldPlayer.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);

            if (alive || keepInventory || BackSlotCommonConfig.KEEP_ITEMS_ON_DEATH.get()) {
                newData.neobackslot$setBackSlot(oldData.neobackslot$getBackSlot());
                newData.neobackslot$setBeltSlot(oldData.neobackslot$getBeltSlot());
            } else {
                drop(oldPlayer, oldData.neobackslot$getBackSlot());
                drop(oldPlayer, oldData.neobackslot$getBeltSlot());
                newData.neobackslot$setBackSlot(ItemStack.EMPTY);
                newData.neobackslot$setBeltSlot(ItemStack.EMPTY);
            }

            BackSlotInventoryHelper.sync(newPlayer);
        });
    }

    private static void drop(ServerPlayer player, ItemStack stack) {
        if (!stack.isEmpty()) {
            player.drop(stack.copy(), true, false);
        }
    }
}
