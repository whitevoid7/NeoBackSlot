package net.backslot.fabric.client.profile;

import net.backslot.fabric.NeoBackSlotFabric;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

public final class TransformProfileManager {
    public enum Slot {
        BACK,
        BELT
    }

    public record Snapshot(Map<ResourceLocation, TransformProfile> backProfiles, Map<ResourceLocation, TransformProfile> beltProfiles) {
    }

    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir()
            .resolve(NeoBackSlotFabric.MOD_ID)
            .resolve("item-transforms.properties");
    private static final Path LEGACY_CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("neobackslot-item-transforms.properties");
    private static Map<ResourceLocation, TransformProfile> backProfiles = new HashMap<>();
    private static Map<ResourceLocation, TransformProfile> beltProfiles = new HashMap<>();
    private static boolean loaded;

    private TransformProfileManager() {
    }

    public static Optional<TransformProfile> getProfile(ItemStack stack, Slot slot) {
        loadIfNeeded();
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return Optional.ofNullable(getProfiles(slot).get(itemId));
    }

    public static Optional<TransformProfile> getItemProfile(ResourceLocation itemId, Slot slot) {
        loadIfNeeded();
        return Optional.ofNullable(getProfiles(slot).get(itemId));
    }

    public static void setItemProfile(ResourceLocation itemId, Slot slot, TransformProfile profile) {
        loadIfNeeded();
        getProfiles(slot).put(itemId, profile);
    }

    public static void removeItemProfile(ResourceLocation itemId, Slot slot) {
        loadIfNeeded();
        getProfiles(slot).remove(itemId);
    }

    public static Snapshot snapshotItemProfiles() {
        loadIfNeeded();
        return new Snapshot(new HashMap<>(backProfiles), new HashMap<>(beltProfiles));
    }

    public static void restoreItemProfiles(Snapshot snapshot) {
        backProfiles = new HashMap<>(snapshot.backProfiles());
        beltProfiles = new HashMap<>(snapshot.beltProfiles());
    }

    public static void saveProfiles() {
        loadIfNeeded();
        Properties properties = new Properties();
        writeProfiles(properties, "back", backProfiles);
        writeProfiles(properties, "belt", beltProfiles);

        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            if (Files.notExists(CONFIG_PATH) && Files.exists(LEGACY_CONFIG_PATH)) {
                Files.move(LEGACY_CONFIG_PATH, CONFIG_PATH);
            } else if (Files.exists(CONFIG_PATH)) {
                deleteLegacyConfig();
            }

            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                properties.store(writer, "NeoBackSlot item transform profiles");
            }
        } catch (IOException ignored) {
        }
    }

    private static void loadIfNeeded() {
        if (loaded) {
            return;
        }

        loaded = true;
        migrateLegacyConfig();

        Properties properties = new Properties();
        Path readPath = Files.exists(CONFIG_PATH) ? CONFIG_PATH : LEGACY_CONFIG_PATH;
        if (Files.exists(readPath)) {
            try (Reader reader = Files.newBufferedReader(readPath)) {
                properties.load(reader);
            } catch (IOException ignored) {
            }
        }

        readProfiles(properties, "back", backProfiles);
        readProfiles(properties, "belt", beltProfiles);
    }

    private static void readProfiles(Properties properties, String slot, Map<ResourceLocation, TransformProfile> profiles) {
        String prefix = slot + ".";
        for (String key : properties.stringPropertyNames()) {
            if (!key.startsWith(prefix) || !key.endsWith(".scale")) {
                continue;
            }

            String itemIdText = key.substring(prefix.length(), key.length() - ".scale".length());
            ResourceLocation itemId = ResourceLocation.tryParse(itemIdText);
            if (itemId != null) {
                profiles.put(itemId, readProfile(properties, prefix + itemId));
            }
        }
    }

    private static TransformProfile readProfile(Properties properties, String prefix) {
        return new TransformProfile(
                getDouble(properties, prefix + ".offsetX", 0.0D),
                getDouble(properties, prefix + ".offsetY", 0.0D),
                getDouble(properties, prefix + ".offsetZ", 0.0D),
                getDouble(properties, prefix + ".rotationX", 0.0D),
                getDouble(properties, prefix + ".rotationY", 0.0D),
                getDouble(properties, prefix + ".rotationZ", 0.0D),
                getDouble(properties, prefix + ".scale", 1.0D)
        );
    }

    private static double getDouble(Properties properties, String key, double defaultValue) {
        try {
            return Double.parseDouble(properties.getProperty(key, Double.toString(defaultValue)));
        } catch (NumberFormatException ignored) {
            return defaultValue;
        }
    }

    private static void writeProfiles(Properties properties, String slot, Map<ResourceLocation, TransformProfile> profiles) {
        for (Map.Entry<ResourceLocation, TransformProfile> entry : profiles.entrySet()) {
            String prefix = slot + "." + entry.getKey();
            TransformProfile profile = entry.getValue();
            properties.setProperty(prefix + ".offsetX", Double.toString(profile.offsetX()));
            properties.setProperty(prefix + ".offsetY", Double.toString(profile.offsetY()));
            properties.setProperty(prefix + ".offsetZ", Double.toString(profile.offsetZ()));
            properties.setProperty(prefix + ".rotationX", Double.toString(profile.rotationX()));
            properties.setProperty(prefix + ".rotationY", Double.toString(profile.rotationY()));
            properties.setProperty(prefix + ".rotationZ", Double.toString(profile.rotationZ()));
            properties.setProperty(prefix + ".scale", Double.toString(profile.scale()));
        }
    }

    private static Map<ResourceLocation, TransformProfile> getProfiles(Slot slot) {
        return slot == Slot.BACK ? backProfiles : beltProfiles;
    }

    private static void migrateLegacyConfig() {
        if (Files.exists(CONFIG_PATH)) {
            deleteLegacyConfig();
            return;
        }

        if (Files.notExists(LEGACY_CONFIG_PATH)) {
            return;
        }

        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.move(LEGACY_CONFIG_PATH, CONFIG_PATH);
        } catch (IOException ignored) {
        }
    }

    private static void deleteLegacyConfig() {
        try {
            Files.deleteIfExists(LEGACY_CONFIG_PATH);
        } catch (IOException ignored) {
        }
    }
}
