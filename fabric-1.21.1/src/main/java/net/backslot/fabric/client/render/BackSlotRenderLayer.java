package net.backslot.fabric.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.backslot.fabric.client.data.ClientBackSlotData;
import net.backslot.fabric.client.data.ClientTransformData;
import net.backslot.fabric.client.profile.TransformProfile;
import net.backslot.fabric.client.profile.TransformProfileManager;
import net.backslot.fabric.config.BackSlotClientConfig;
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
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, AbstractClientPlayer player,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        ItemStack back = ClientBackSlotData.getBackSlot(player.getUUID());
        ItemStack belt = ClientBackSlotData.getBeltSlot(player.getUUID());

        if (back.isEmpty() && belt.isEmpty()) {
            return;
        }

        boolean localPlayer = isLocalPlayer(player);
        TransformProfile backTransform = getBackTransform(player, back, localPlayer);
        TransformProfile beltTransform = getBeltTransform(player, belt, localPlayer);

        renderBackItem(poseStack, buffer, packedLight, player, back, backTransform);
        renderBeltItem(poseStack, buffer, packedLight, player, belt, beltTransform);
    }

    private void renderBackItem(PoseStack poseStack, MultiBufferSource buffer, int packedLight, AbstractClientPlayer player,
                                ItemStack stack, TransformProfile transform) {
        if (stack.isEmpty()) {
            return;
        }

        float scale = (float) transform.scale();
        double offsetX = transform.offsetX();
        double offsetY = transform.offsetY();
        double offsetZ = transform.offsetZ();
        float rotationX = (float) transform.rotationX();
        float rotationY = (float) transform.rotationY();
        float rotationZ = (float) transform.rotationZ();

        poseStack.pushPose();
        this.getParentModel().body.translateAndRotate(poseStack);

        if (stack.getItem() instanceof ShieldItem) {
            boolean vanillaShield = stack.getItem() == net.minecraft.world.item.Items.SHIELD;
            poseStack.translate(offsetX, 0.18D + offsetY, 0.26D + offsetZ);
            poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(rotationX));
            poseStack.mulPose(Axis.YP.rotationDegrees(rotationY));
            poseStack.mulPose(Axis.ZP.rotationDegrees(rotationZ));
            float shieldScale = vanillaShield ? 1.30F : 0.45F;
            poseStack.scale(shieldScale * scale, shieldScale * scale, shieldScale * scale);
            renderItem(poseStack, buffer, packedLight, player, stack, ItemDisplayContext.FIXED);
        } else if (stack.getItem() instanceof TridentItem) {
            poseStack.mulPose(Axis.YP.rotationDegrees(52.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(40.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(-25.0F));
            poseStack.translate(-0.26D + offsetX, offsetY, offsetZ);
            if (!player.hasItemInSlot(EquipmentSlot.CHEST)) {
                poseStack.translate(0.05D, 0.0D, 0.0D);
            }
            poseStack.mulPose(Axis.XP.rotationDegrees(rotationX));
            poseStack.mulPose(Axis.YP.rotationDegrees(rotationY));
            poseStack.mulPose(Axis.ZP.rotationDegrees(rotationZ));
            poseStack.scale(scale, -scale, -scale);
            renderItem(poseStack, buffer, packedLight, player, stack, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND);
        } else {
            poseStack.translate(offsetX, offsetY, 0.16D + offsetZ);
            if (player.hasItemInSlot(EquipmentSlot.CHEST)) {
                poseStack.translate(0.0D, 0.0D, 0.06D);
            }
            poseStack.mulPose(Axis.XP.rotationDegrees(rotationX));
            poseStack.mulPose(Axis.YP.rotationDegrees(rotationY));
            poseStack.mulPose(Axis.ZP.rotationDegrees(rotationZ));
            poseStack.scale(scale, scale, scale);
            renderItem(poseStack, buffer, packedLight, player, stack, ItemDisplayContext.HEAD);
        }

        poseStack.popPose();
    }

    private void renderBeltItem(PoseStack poseStack, MultiBufferSource buffer, int packedLight, AbstractClientPlayer player,
                                ItemStack stack, TransformProfile transform) {
        if (stack.isEmpty()) {
            return;
        }

        float scale = (float) transform.scale();
        double offsetX = transform.offsetX();
        double offsetY = transform.offsetY();
        double offsetZ = transform.offsetZ();
        float rotationX = (float) transform.rotationX();
        float rotationY = (float) transform.rotationY();
        float rotationZ = (float) transform.rotationZ();

        poseStack.pushPose();
        this.getParentModel().body.translateAndRotate(poseStack);

        if (stack.getItem() instanceof TridentItem) {
            poseStack.translate(-0.05D + offsetX, 0.30D + offsetY, -0.85D + offsetZ);
            poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(135.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(rotationX));
            poseStack.mulPose(Axis.YP.rotationDegrees(rotationY));
            poseStack.mulPose(Axis.ZP.rotationDegrees(rotationZ));
            poseStack.scale(0.75F * scale, 0.75F * scale, 0.75F * scale);
        } else {
            poseStack.translate(0.29D + offsetX, 0.50D + offsetY, 0.05D + offsetZ);
            poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(rotationX));
            poseStack.mulPose(Axis.YP.rotationDegrees(rotationY));
            poseStack.mulPose(Axis.ZP.rotationDegrees(rotationZ));
            poseStack.scale(scale, scale, scale);
        }

        renderItem(poseStack, buffer, packedLight, player, stack, ItemDisplayContext.HEAD);
        poseStack.popPose();
    }

    private static TransformProfile getBackTransform(AbstractClientPlayer player, ItemStack stack, boolean localPlayer) {
        if (localPlayer) {
            TransformProfile global = new TransformProfile(
                    BackSlotClientConfig.BACK_SLOT_OFFSET_X.get(),
                    BackSlotClientConfig.BACK_SLOT_OFFSET_Y.get(),
                    BackSlotClientConfig.BACK_SLOT_OFFSET_Z.get(),
                    BackSlotClientConfig.BACK_SLOT_ROTATION_X.get(),
                    BackSlotClientConfig.BACK_SLOT_ROTATION_Y.get(),
                    BackSlotClientConfig.BACK_SLOT_ROTATION_Z.get(),
                    BackSlotClientConfig.BACK_SLOT_SCALE.get()
            );
            return stack.isEmpty() ? global : TransformProfileManager.getProfile(stack, TransformProfileManager.Slot.BACK).orElse(global);
        }

        return ClientTransformData.getBackTransform(player.getUUID())
                .orElseGet(() -> new TransformProfile(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, DEFAULT_BACK_SCALE));
    }

    private static TransformProfile getBeltTransform(AbstractClientPlayer player, ItemStack stack, boolean localPlayer) {
        if (localPlayer) {
            TransformProfile global = new TransformProfile(
                    BackSlotClientConfig.BELT_SLOT_OFFSET_X.get(),
                    BackSlotClientConfig.BELT_SLOT_OFFSET_Y.get(),
                    BackSlotClientConfig.BELT_SLOT_OFFSET_Z.get(),
                    BackSlotClientConfig.BELT_SLOT_ROTATION_X.get(),
                    BackSlotClientConfig.BELT_SLOT_ROTATION_Y.get(),
                    BackSlotClientConfig.BELT_SLOT_ROTATION_Z.get(),
                    BackSlotClientConfig.BELT_SLOT_SCALE.get()
            );
            return stack.isEmpty() ? global : TransformProfileManager.getProfile(stack, TransformProfileManager.Slot.BELT).orElse(global);
        }

        return ClientTransformData.getBeltTransform(player.getUUID())
                .orElseGet(() -> new TransformProfile(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, DEFAULT_BELT_SCALE));
    }

    private static boolean isLocalPlayer(AbstractClientPlayer player) {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.player != null && player.getUUID().equals(minecraft.player.getUUID());
    }

    private static void renderItem(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                                   AbstractClientPlayer player, ItemStack stack, ItemDisplayContext context) {
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
