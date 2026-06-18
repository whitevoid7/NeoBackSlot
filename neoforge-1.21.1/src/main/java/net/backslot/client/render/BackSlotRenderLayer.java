package net.backslot.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.backslot.client.data.ClientBackSlotData;
import net.backslot.client.data.ClientTransformData;
import net.backslot.client.profile.TransformProfile;
import net.backslot.client.profile.TransformProfileManager;
import net.backslot.config.BackSlotClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TridentItem;

public class BackSlotRenderLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    private static final float DEFAULT_BACK_SCALE = 0.95F;
    private static final float DEFAULT_BELT_SCALE = 0.85F;

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

        boolean localPlayer = isLocalPlayer(player);

        TransformProfile backTransform = localPlayer
                ? new TransformProfile(
                BackSlotClientConfig.BACK_SLOT_OFFSET_X.get(),
                BackSlotClientConfig.BACK_SLOT_OFFSET_Y.get(),
                BackSlotClientConfig.BACK_SLOT_OFFSET_Z.get(),
                BackSlotClientConfig.BACK_SLOT_ROTATION_X.get(),
                BackSlotClientConfig.BACK_SLOT_ROTATION_Y.get(),
                BackSlotClientConfig.BACK_SLOT_ROTATION_Z.get(),
                BackSlotClientConfig.BACK_SLOT_SCALE.get()
        )
                : ClientTransformData.getBackTransform(player.getUUID())
                .orElseGet(() -> new TransformProfile(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, DEFAULT_BACK_SCALE));

        TransformProfile beltTransform = localPlayer
                ? new TransformProfile(
                BackSlotClientConfig.BELT_SLOT_OFFSET_X.get(),
                BackSlotClientConfig.BELT_SLOT_OFFSET_Y.get(),
                BackSlotClientConfig.BELT_SLOT_OFFSET_Z.get(),
                BackSlotClientConfig.BELT_SLOT_ROTATION_X.get(),
                BackSlotClientConfig.BELT_SLOT_ROTATION_Y.get(),
                BackSlotClientConfig.BELT_SLOT_ROTATION_Z.get(),
                BackSlotClientConfig.BELT_SLOT_SCALE.get()
        )
                : ClientTransformData.getBeltTransform(player.getUUID())
                .orElseGet(() -> new TransformProfile(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, DEFAULT_BELT_SCALE));

        float backScale = (float) backTransform.scale();
        float beltScale = (float) beltTransform.scale();
        double backOffsetX = backTransform.offsetX();
        double backOffsetY = backTransform.offsetY();
        double backOffsetZ = backTransform.offsetZ();

        double beltOffsetX = beltTransform.offsetX();
        double beltOffsetY = beltTransform.offsetY();
        double beltOffsetZ = beltTransform.offsetZ();

        float backRotationX = (float) backTransform.rotationX();
        float backRotationY = (float) backTransform.rotationY();
        float backRotationZ = (float) backTransform.rotationZ();

        float beltRotationX = (float) beltTransform.rotationX();
        float beltRotationY = (float) beltTransform.rotationY();
        float beltRotationZ = (float) beltTransform.rotationZ();

        if (!back.isEmpty()) {
            TransformProfile backProfile = localPlayer
                    ? TransformProfileManager.getProfile(back, TransformProfileManager.Slot.BACK).orElse(null)
                    : null;
            if (backProfile != null) {
                backScale = (float) backProfile.scale();
                backOffsetX = backProfile.offsetX();
                backOffsetY = backProfile.offsetY();
                backOffsetZ = backProfile.offsetZ();
                backRotationX = (float) backProfile.rotationX();
                backRotationY = (float) backProfile.rotationY();
                backRotationZ = (float) backProfile.rotationZ();
            }

            poseStack.pushPose();
            this.getParentModel().body.translateAndRotate(poseStack);

            if (back.getItem() instanceof ShieldItem) {
                boolean vanillaShield = back.getItem() == net.minecraft.world.item.Items.SHIELD;

                poseStack.translate(backOffsetX, 0.18D + backOffsetY, 0.26D + backOffsetZ);
                poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
                poseStack.mulPose(Axis.XP.rotationDegrees(backRotationX));
                poseStack.mulPose(Axis.YP.rotationDegrees(backRotationY));
                poseStack.mulPose(Axis.ZP.rotationDegrees(backRotationZ));

                if (vanillaShield) {
                    poseStack.scale(1.30F * backScale, 1.30F * backScale, 1.30F * backScale);
                } else {
                    poseStack.scale(0.45F * backScale, 0.45F * backScale, 0.45F * backScale);
                }

                renderItem(poseStack, buffer, packedLight, player, back, ItemDisplayContext.FIXED);

            } else if (back.getItem() instanceof TridentItem) {

                // Original transform
                poseStack.mulPose(Axis.YP.rotationDegrees(52.0F));
                poseStack.mulPose(Axis.XP.rotationDegrees(40.0F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(-25.0F));

                poseStack.translate(
                        -0.26D + backOffsetX,
                        backOffsetY,
                        backOffsetZ
                );

                if (!player.hasItemInSlot(EquipmentSlot.CHEST)) {
                    poseStack.translate(0.05D, 0.0D, 0.0D);
                }

                // User configurable rotation
                poseStack.mulPose(Axis.XP.rotationDegrees(backRotationX));
                poseStack.mulPose(Axis.YP.rotationDegrees(backRotationY));
                poseStack.mulPose(Axis.ZP.rotationDegrees(backRotationZ));

                poseStack.scale(
                        1.0F * backScale,
                        -1.0F * backScale,
                        -1.0F * backScale
                );

                renderItem(
                        poseStack,
                        buffer,
                        packedLight,
                        player,
                        back,
                        ItemDisplayContext.THIRD_PERSON_RIGHT_HAND
                );
            } else {
                poseStack.translate(backOffsetX, backOffsetY, 0.16D + backOffsetZ);

                if (player.hasItemInSlot(EquipmentSlot.CHEST)) {
                    poseStack.translate(0.0D, 0.0D, 0.06D);
                }

                poseStack.mulPose(Axis.XP.rotationDegrees(backRotationX));
                poseStack.mulPose(Axis.YP.rotationDegrees(backRotationY));
                poseStack.mulPose(Axis.ZP.rotationDegrees(backRotationZ));

                poseStack.scale(backScale, backScale, backScale);

                renderItem(poseStack, buffer, packedLight, player, back, ItemDisplayContext.HEAD);
            }

            poseStack.popPose();
        }

        if (!belt.isEmpty()) {
            TransformProfile beltProfile = localPlayer
                    ? TransformProfileManager.getProfile(belt, TransformProfileManager.Slot.BELT).orElse(null)
                    : null;
            if (beltProfile != null) {
                beltScale = (float) beltProfile.scale();
                beltOffsetX = beltProfile.offsetX();
                beltOffsetY = beltProfile.offsetY();
                beltOffsetZ = beltProfile.offsetZ();
                beltRotationX = (float) beltProfile.rotationX();
                beltRotationY = (float) beltProfile.rotationY();
                beltRotationZ = (float) beltProfile.rotationZ();
            }

            poseStack.pushPose();
            this.getParentModel().body.translateAndRotate(poseStack);

            if (belt.getItem() instanceof TridentItem) {
                // Default transform for Trident in Belt Slot
                // Tuned for vanilla trident model
                poseStack.translate(
                        -0.05D + beltOffsetX,
                        0.30D + beltOffsetY,
                        -0.85D + beltOffsetZ
                );

                poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(135.0F));

                poseStack.mulPose(Axis.XP.rotationDegrees(beltRotationX));
                poseStack.mulPose(Axis.YP.rotationDegrees(beltRotationY));
                poseStack.mulPose(Axis.ZP.rotationDegrees(beltRotationZ));

                poseStack.scale(0.75F * beltScale, 0.75F * beltScale, 0.75F * beltScale);

                renderItem(poseStack, buffer, packedLight, player, belt, ItemDisplayContext.HEAD);
            } else {
                poseStack.translate(
                        0.29D + beltOffsetX,
                        0.50D + beltOffsetY,
                        0.05D + beltOffsetZ
                );

                poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
                poseStack.mulPose(Axis.XP.rotationDegrees(beltRotationX));
                poseStack.mulPose(Axis.YP.rotationDegrees(beltRotationY));
                poseStack.mulPose(Axis.ZP.rotationDegrees(beltRotationZ));
                poseStack.scale(beltScale, beltScale, beltScale);

                renderItem(poseStack, buffer, packedLight, player, belt, ItemDisplayContext.HEAD);
            }

            poseStack.popPose();
        }
    }

    private static boolean isLocalPlayer(AbstractClientPlayer player) {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.player != null && player.getUUID().equals(minecraft.player.getUUID());
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
                OverlayTexture.NO_OVERLAY,
                poseStack,
                buffer,
                player.level(),
                0
        );
    }
}
