package net.backslot.client.render;

import net.backslot.BackSlotMain;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = BackSlotMain.MOD_ID, value = Dist.CLIENT)
public class BackSlotRenderEvents {

    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        event.getSkins().forEach(model -> {
            EntityRenderer<? extends AbstractClientPlayer> renderer = event.getSkin(model);

            if (renderer instanceof PlayerRenderer playerRenderer) {
                playerRenderer.addLayer(new BackSlotRenderLayer(playerRenderer));
            }
        });
    }
}