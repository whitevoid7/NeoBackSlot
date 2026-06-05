package net.backslot.network;

import net.backslot.inventory.BackSlotInventoryHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.backslot.client.data.ClientBackSlotData;

public class BackSlotNetworking {

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToServer(
                SwapBackSlotPayload.TYPE,
                SwapBackSlotPayload.STREAM_CODEC,
                (payload, context) -> {
                    context.enqueueWork(() -> {
                        if (context.player() == null) {
                            return;
                        }

                        BackSlotInventoryHelper.swapBackSlot(context.player());
                    });
                }
        );

        registrar.playToServer(
                SwapBeltSlotPayload.TYPE,
                SwapBeltSlotPayload.STREAM_CODEC,
                (payload, context) -> {
                    context.enqueueWork(() -> {
                        if (context.player() == null) {
                            return;
                        }

                        BackSlotInventoryHelper.swapBeltSlot(context.player());
                    });
                }
        );

        registrar.playToClient(
                SyncBackSlotPayload.TYPE,
                SyncBackSlotPayload.STREAM_CODEC,
                (payload, context) -> {
                    context.enqueueWork(() -> {
                        ClientBackSlotData.setBoth(
                                payload.playerId(),
                                payload.backSlot(),
                                payload.beltSlot()
                        );
                    });
                }
        );

        registrar.playToServer(
                ClickBackSlotPayload.TYPE,
                ClickBackSlotPayload.STREAM_CODEC,
                (payload, context) -> {
                    context.enqueueWork(() -> {
                        if (context.player() == null) {
                            return;
                        }

                        BackSlotInventoryHelper.clickBackSlot(context.player());
                    });
                }
        );

        registrar.playToServer(
                ClickBeltSlotPayload.TYPE,
                ClickBeltSlotPayload.STREAM_CODEC,
                (payload, context) -> {
                    context.enqueueWork(() -> {
                        if (context.player() == null) {
                            return;
                        }

                        BackSlotInventoryHelper.clickBeltSlot(context.player());
                    });
                }
        );
    }
}