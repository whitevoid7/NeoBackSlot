package net.backslot.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.backslot.client.data.ClientBackSlotData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TridentItem;

public class BackSlotRenderLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public BackSlotRenderLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer) {
        super(renderer);
    }

    @Override
    public void render(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            AbstractClientPlayer player,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        ItemStack back = ClientBackSlotData.getBackSlot(player.getUUID());
        ItemStack belt = ClientBackSlotData.getBeltSlot(player.getUUID());

        if (back.isEmpty() && belt.isEmpty()) {
            return;
        }

        if (!back.isEmpty()) {
            poseStack.pushPose();
            this.getParentModel().body.translateAndRotate(poseStack);

            if (back.getItem() instanceof ShieldItem) {
                boolean vanillaShield = back.getItem() == net.minecraft.world.item.Items.SHIELD;

                poseStack.translate(0.0D, 0.18D, 0.26D);
                poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));

                if (vanillaShield) {
                    poseStack.scale(1.30F, 1.30F, 1.30F);
                } else {
                    poseStack.scale(0.45F, 0.45F, 0.45F);
                }

                renderItem(poseStack, buffer, packedLight, player, back, ItemDisplayContext.FIXED);

            } else if (back.getItem() instanceof TridentItem) {
                poseStack.mulPose(Axis.YP.rotationDegrees(52.0F));
                poseStack.mulPose(Axis.XP.rotationDegrees(40.0F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(-25.0F));
                poseStack.translate(-0.26D, 0.0D, 0.0D);

                if (!player.hasItemInSlot(EquipmentSlot.CHEST)) {
                    poseStack.translate(0.05D, 0.0D, 0.0D);
                }

                poseStack.scale(1.0F, -1.0F, -1.0F);

                renderItem(poseStack, buffer, packedLight, player, back, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND);

            } else {
                // fixed position
                poseStack.translate(0.0D, 0.0D, 0.16D);

                if (player.hasItemInSlot(EquipmentSlot.CHEST)) {
                    poseStack.translate(0.0D, 0.0D, 0.06D);
                }

                poseStack.scale(0.95F, 0.95F, 0.95F);

                renderItem(poseStack, buffer, packedLight, player, back, ItemDisplayContext.HEAD);
            }

            poseStack.popPose();
        }

        if (!belt.isEmpty()) {
            poseStack.pushPose();
            this.getParentModel().body.translateAndRotate(poseStack);

            poseStack.translate(0.29D, 0.50D, 0.05D);
            poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
            poseStack.scale(0.85F, 0.85F, 0.85F);

            renderItem(poseStack, buffer, packedLight, player, belt, ItemDisplayContext.HEAD);

            poseStack.popPose();
        }
    }

    private static void renderItem(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            AbstractClientPlayer player,
            ItemStack stack,
            ItemDisplayContext context
    ) {
        Minecraft.getInstance().getItemRenderer().renderStatic(
                stack,
                context,
                packedLight,
                0,
                poseStack,
                buffer,
                player.level(),
                0
        );
    }
}