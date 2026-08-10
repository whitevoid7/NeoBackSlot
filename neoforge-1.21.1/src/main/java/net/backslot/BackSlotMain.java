package net.backslot;

import net.backslot.attachment.BackSlotAttachments;
import net.backslot.client.config.BackSlotNeoForgeConfigScreen;
import net.backslot.command.BackSlotCommands;
import net.backslot.inventory.BackSlotInventoryHelper;
import net.backslot.network.BackSlotNetworking;
import net.backslot.network.ServerTransformSync;
import net.backslot.sound.BackSlotSounds;
import net.backslot.data.BackSlotData;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.backslot.config.BackSlotConfig;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.backslot.config.BackSlotClientConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Mod(BackSlotMain.MOD_ID)
public class BackSlotMain {
    public static final String MOD_ID = "neobackslot";

    public BackSlotMain(IEventBus eventBus, ModContainer modContainer) {
        BackSlotAttachments.register(eventBus);
        BackSlotSounds.register(eventBus);
        migrateConfigFile(MOD_ID + "-common.toml", MOD_ID + "/common.toml");
        migrateConfigFile(MOD_ID + "-client.toml", MOD_ID + "/client.toml");
        modContainer.registerConfig(ModConfig.Type.COMMON, BackSlotConfig.SPEC, MOD_ID + "/common.toml");
        modContainer.registerConfig(ModConfig.Type.CLIENT, BackSlotClientConfig.SPEC, MOD_ID + "/client.toml");
        modContainer.registerExtensionPoint(IConfigScreenFactory.class,
                (IConfigScreenFactory) (container, parent) -> new BackSlotNeoForgeConfigScreen(parent));
        eventBus.register(BackSlotNetworking.class);

        NeoForge.EVENT_BUS.register(this);

        System.out.println("NeoBackSlot loaded.");
    }

    private static void migrateConfigFile(String legacyFileName, String newFileName) {
        Path configDir = FMLPaths.CONFIGDIR.get();
        Path legacyPath = configDir.resolve(legacyFileName);
        Path newPath = configDir.resolve(newFileName);

        if (Files.exists(newPath)) {
            deleteLegacyConfigFile(legacyPath);
            return;
        }

        if (Files.notExists(legacyPath)) {
            return;
        }

        try {
            Files.createDirectories(newPath.getParent());
            Files.move(legacyPath, newPath);
        } catch (IOException exception) {
            System.err.println("Failed to migrate NeoBackSlot config file: " + exception.getMessage());
        }
    }

    private static void deleteLegacyConfigFile(Path legacyPath) {
        try {
            Files.deleteIfExists(legacyPath);
        } catch (IOException exception) {
            System.err.println("Failed to delete old NeoBackSlot config file: " + exception.getMessage());
        }
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        BackSlotCommands.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        BackSlotInventoryHelper.sync(event.getEntity());

        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            ServerTransformSync.syncKnownTransformsTo(serverPlayer);
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        ServerTransformSync.remove(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public void onPlayerStartTracking(PlayerEvent.StartTracking event) {
        if (event.getEntity() instanceof ServerPlayer viewer && event.getTarget() instanceof ServerPlayer target) {
            BackSlotInventoryHelper.syncTo(viewer, target);
            ServerTransformSync.syncTo(viewer, target.getUUID());
        }
    }

    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        BackSlotData data = player.getData(BackSlotAttachments.BACK_SLOT_DATA.get());

        boolean keepInventory = player.level()
                .getGameRules()
                .getBoolean(GameRules.RULE_KEEPINVENTORY);

        if (keepInventory || BackSlotConfig.KEEP_ITEMS_ON_DEATH.get()) {
            BackSlotInventoryHelper.sync(player);
            return;
        }

        addDrop(event, player, data.getBackSlot());
        addDrop(event, player, data.getBeltSlot());

        data.setBackSlot(ItemStack.EMPTY);
        data.setBeltSlot(ItemStack.EMPTY);

        BackSlotInventoryHelper.sync(player);
    }

    private void addDrop(LivingDropsEvent event, Player player, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }

        ItemEntity itemEntity = new ItemEntity(
                player.level(),
                player.getX(),
                player.getY(),
                player.getZ(),
                stack.copy()
        );

        event.getDrops().add(itemEntity);
    }
}
