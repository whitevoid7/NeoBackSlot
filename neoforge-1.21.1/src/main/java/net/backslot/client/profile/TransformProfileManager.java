package net.backslot.client.profile;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.backslot.BackSlotMain;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class TransformProfileManager {

    public enum Slot {
        BACK,
        BELT
    }

    public record Snapshot(
            Map<ResourceLocation, TransformProfile> backProfiles,
            Map<ResourceLocation, TransformProfile> beltProfiles
    ) {
    }

    private static final Path CONFIG_PATH = FMLPaths.CONFIGDIR.get().resolve(BackSlotMain.MOD_ID).resolve("item-transforms.toml");
    private static final Path LEGACY_CONFIG_PATH = FMLPaths.CONFIGDIR.get().resolve(BackSlotMain.MOD_ID + "-item-transforms.toml");

    private static Map<ResourceLocation, TransformProfile> backProfiles = new HashMap<>();
    private static Map<ResourceLocation, TransformProfile> beltProfiles = new HashMap<>();
    private static long lastModified = Long.MIN_VALUE;

    private TransformProfileManager() {
    }

    public static Optional<TransformProfile> getProfile(ItemStack stack, Slot slot) {
        loadIfNeeded();

        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return getProfiles(slot).containsKey(itemId)
                ? Optional.of(getProfiles(slot).get(itemId))
                : Optional.empty();
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
        ensureConfigExists();

        try (CommentedFileConfig config = CommentedFileConfig.builder(CONFIG_PATH).build()) {
            Config itemTransforms = Config.inMemory();
            itemTransforms.set("back", createProfilesConfig(backProfiles));
            itemTransforms.set("belt", createProfilesConfig(beltProfiles));

            config.set("itemTransforms", itemTransforms);
            config.save();
            lastModified = getLastModified();
        } catch (Exception exception) {
            System.err.println("Failed to save NeoBackSlot item transform profiles: " + exception.getMessage());
        }
    }

    private static void loadIfNeeded() {
        ensureConfigExists();

        long modified = getLastModified();
        if (modified == lastModified) {
            return;
        }

        lastModified = modified;
        loadProfiles();
    }

    private static void loadProfiles() {
        backProfiles = new HashMap<>();
        beltProfiles = new HashMap<>();

        try (CommentedFileConfig config = CommentedFileConfig.builder(CONFIG_PATH).build()) {
            config.load();

            Config itemTransforms = config.get("itemTransforms");
            if (itemTransforms == null) {
                return;
            }

            loadSlotProfiles(itemTransforms.get("back"), backProfiles);
            loadSlotProfiles(itemTransforms.get("belt"), beltProfiles);
            loadLegacyProfiles(itemTransforms, backProfiles);
        } catch (Exception exception) {
            System.err.println("Failed to load NeoBackSlot item transform profiles: " + exception.getMessage());
            backProfiles = new HashMap<>();
            beltProfiles = new HashMap<>();
        }
    }

    private static void loadSlotProfiles(Object value, Map<ResourceLocation, TransformProfile> profiles) {
        if (!(value instanceof Config slotConfig)) {
            return;
        }

        for (String key : slotConfig.valueMap().keySet()) {
            Object profileValue = slotConfig.get(key);
            if (!(profileValue instanceof Config profileConfig)) {
                continue;
            }

            ResourceLocation itemId = ResourceLocation.tryParse(key);
            if (itemId != null) {
                profiles.put(itemId, readProfile(profileConfig));
            }
        }
    }

    private static void loadLegacyProfiles(Config itemTransforms, Map<ResourceLocation, TransformProfile> profiles) {
        for (String key : itemTransforms.valueMap().keySet()) {
            if ("back".equals(key) || "belt".equals(key) || "default".equals(key)) {
                continue;
            }

            Object value = itemTransforms.get(key);
            if (!(value instanceof Config profileConfig)) {
                continue;
            }

            ResourceLocation itemId = ResourceLocation.tryParse(key);
            if (itemId != null && !profiles.containsKey(itemId)) {
                profiles.put(itemId, readProfile(profileConfig));
            }
        }
    }

    private static Map<ResourceLocation, TransformProfile> getProfiles(Slot slot) {
        return slot == Slot.BACK ? backProfiles : beltProfiles;
    }

    private static TransformProfile readProfile(Config config) {
        return new TransformProfile(
                getDouble(config, "offsetX", 0.0D),
                getDouble(config, "offsetY", 0.0D),
                getDouble(config, "offsetZ", 0.0D),
                getDouble(config, "rotationX", 0.0D),
                getDouble(config, "rotationY", 0.0D),
                getDouble(config, "rotationZ", 0.0D),
                getDouble(config, "scale", 1.0D)
        );
    }

    private static double getDouble(Config config, String key, double defaultValue) {
        Object value = config.get(key);
        return value instanceof Number number ? number.doubleValue() : defaultValue;
    }

    private static Config createProfilesConfig(Map<ResourceLocation, TransformProfile> profiles) {
        Config config = Config.inMemory();

        for (Map.Entry<ResourceLocation, TransformProfile> entry : profiles.entrySet()) {
            config.set(entry.getKey().toString(), createProfileConfig(entry.getValue()));
        }

        return config;
    }

    private static Config createProfileConfig(TransformProfile profile) {
        Config config = Config.inMemory();
        config.set("offsetX", profile.offsetX());
        config.set("offsetY", profile.offsetY());
        config.set("offsetZ", profile.offsetZ());
        config.set("rotationX", profile.rotationX());
        config.set("rotationY", profile.rotationY());
        config.set("rotationZ", profile.rotationZ());
        config.set("scale", profile.scale());
        return config;
    }

    private static void ensureConfigExists() {
        if (Files.exists(CONFIG_PATH)) {
            deleteLegacyConfig();
            return;
        }

        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            if (Files.exists(LEGACY_CONFIG_PATH)) {
                Files.move(LEGACY_CONFIG_PATH, CONFIG_PATH);
                return;
            }

            Files.writeString(CONFIG_PATH, """
                    # NeoBackSlot item transform profiles.
                    # Back Slot and Belt Slot profiles are separate, even for the same item ID.
                    #
                    # Example:
                    #
                    # [itemTransforms.back."minecraft:shield"]
                    # offsetX = 0.0
                    # offsetY = 0.0
                    # offsetZ = 0.0
                    # rotationX = 0.0
                    # rotationY = 0.0
                    # rotationZ = 0.0
                    # scale = 1.0
                    #
                    # [itemTransforms.belt."minecraft:shield"]
                    # offsetX = 0.0
                    # offsetY = 0.0
                    # offsetZ = 0.0
                    # rotationX = 0.0
                    # rotationY = 0.0
                    # rotationZ = 0.0
                    # scale = 1.0
                    """);
        } catch (IOException exception) {
            System.err.println("Failed to create NeoBackSlot item transform profile config: " + exception.getMessage());
        }
    }

    private static long getLastModified() {
        try {
            return Files.getLastModifiedTime(CONFIG_PATH).toMillis();
        } catch (IOException exception) {
            return Long.MIN_VALUE;
        }
    }

    private static void deleteLegacyConfig() {
        try {
            Files.deleteIfExists(LEGACY_CONFIG_PATH);
        } catch (IOException exception) {
            System.err.println("Failed to delete old NeoBackSlot item transform profile config: " + exception.getMessage());
        }
    }
}
