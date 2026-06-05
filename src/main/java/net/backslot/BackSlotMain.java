package net.backslot;

import net.backslot.attachment.BackSlotAttachments;
import net.backslot.command.BackSlotCommands;
import net.backslot.inventory.BackSlotInventoryHelper;
import net.backslot.network.BackSlotNetworking;
import net.backslot.sound.BackSlotSounds;
import net.backslot.data.BackSlotData;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.backslot.config.BackSlotConfig;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;

@Mod(BackSlotMain.MOD_ID)
public class BackSlotMain {
    public static final String MOD_ID = "neobackslot";

    public BackSlotMain(IEventBus eventBus, ModContainer modContainer) {
        BackSlotAttachments.register(eventBus);
        BackSlotSounds.register(eventBus);
        modContainer.registerConfig(ModConfig.Type.COMMON, BackSlotConfig.SPEC);
        eventBus.register(BackSlotNetworking.class);

        NeoForge.EVENT_BUS.register(this);

        System.out.println("NeoBackSlot loaded.");
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        BackSlotCommands.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        BackSlotInventoryHelper.sync(event.getEntity());
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