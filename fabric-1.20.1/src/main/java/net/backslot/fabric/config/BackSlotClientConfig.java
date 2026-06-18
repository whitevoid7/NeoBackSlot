package net.backslot.fabric.config;

import net.backslot.fabric.NeoBackSlotFabric;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class BackSlotClientConfig {
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir()
            .resolve(NeoBackSlotFabric.MOD_ID)
            .resolve("client.properties");
    private static final Path LEGACY_CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("neobackslot-client.properties");

    public static final DoubleValue BACK_SLOT_SCALE = new DoubleValue("back.scale", 0.95D);
    public static final DoubleValue BACK_SLOT_OFFSET_X = new DoubleValue("back.offsetX", 0.0D);
    public static final DoubleValue BACK_SLOT_OFFSET_Y = new DoubleValue("back.offsetY", 0.0D);
    public static final DoubleValue BACK_SLOT_OFFSET_Z = new DoubleValue("back.offsetZ", 0.0D);
    public static final DoubleValue BACK_SLOT_ROTATION_X = new DoubleValue("back.rotationX", 0.0D);
    public static final DoubleValue BACK_SLOT_ROTATION_Y = new DoubleValue("back.rotationY", 0.0D);
    public static final DoubleValue BACK_SLOT_ROTATION_Z = new DoubleValue("back.rotationZ", 0.0D);

    public static final DoubleValue BELT_SLOT_SCALE = new DoubleValue("belt.scale", 0.85D);
    public static final DoubleValue BELT_SLOT_OFFSET_X = new DoubleValue("belt.offsetX", 0.0D);
    public static final DoubleValue BELT_SLOT_OFFSET_Y = new DoubleValue("belt.offsetY", 0.0D);
    public static final DoubleValue BELT_SLOT_OFFSET_Z = new DoubleValue("belt.offsetZ", 0.0D);
    public static final DoubleValue BELT_SLOT_ROTATION_X = new DoubleValue("belt.rotationX", 0.0D);
    public static final DoubleValue BELT_SLOT_ROTATION_Y = new DoubleValue("belt.rotationY", 0.0D);
    public static final DoubleValue BELT_SLOT_ROTATION_Z = new DoubleValue("belt.rotationZ", 0.0D);

    public static final IntValue INVENTORY_SLOT_OFFSET_X = new IntValue("inventory.slotOffsetX", 0);
    public static final IntValue INVENTORY_SLOT_OFFSET_Y = new IntValue("inventory.slotOffsetY", 0);
    public static final IntValue HUD_SLOT_OFFSET_X = new IntValue("hud.slotOffsetX", 0);
    public static final IntValue HUD_SLOT_OFFSET_Y = new IntValue("hud.slotOffsetY", 0);

    public static final Spec SPEC = new Spec();

    private BackSlotClientConfig() {
    }

    public static void load() {
        migrateLegacyConfig();

        Properties properties = new Properties();
        Path readPath = Files.exists(CONFIG_PATH) ? CONFIG_PATH : LEGACY_CONFIG_PATH;
        if (Files.exists(readPath)) {
            try (Reader reader = Files.newBufferedReader(readPath)) {
                properties.load(reader);
            } catch (IOException ignored) {
            }
        }

        for (DoubleValue value : DoubleValue.VALUES) {
            value.load(properties);
        }
        for (IntValue value : IntValue.VALUES) {
            value.load(properties);
        }
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

    public static final class Spec {
        public void save() {
            Properties properties = new Properties();
            for (DoubleValue value : DoubleValue.VALUES) {
                properties.setProperty(value.key, Double.toString(value.get()));
            }
            for (IntValue value : IntValue.VALUES) {
                properties.setProperty(value.key, Integer.toString(value.get()));
            }

            try {
                Files.createDirectories(CONFIG_PATH.getParent());
                try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                    properties.store(writer, "NeoBackSlot client render config");
                }
            } catch (IOException ignored) {
            }
        }
    }

    public static final class DoubleValue {
        private static final java.util.List<DoubleValue> VALUES = new java.util.ArrayList<>();

        private final String key;
        private final double defaultValue;
        private double value;

        private DoubleValue(String key, double defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
            this.value = defaultValue;
            VALUES.add(this);
        }

        public double get() {
            return value;
        }

        public void set(double value) {
            this.value = value;
        }

        private void load(Properties properties) {
            try {
                value = Double.parseDouble(properties.getProperty(key, Double.toString(defaultValue)));
            } catch (NumberFormatException ignored) {
                value = defaultValue;
            }
        }
    }

    public static final class IntValue {
        private static final java.util.List<IntValue> VALUES = new java.util.ArrayList<>();

        private final String key;
        private final int defaultValue;
        private int value;

        private IntValue(String key, int defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
            this.value = defaultValue;
            VALUES.add(this);
        }

        public int get() {
            return value;
        }

        public void set(int value) {
            this.value = value;
        }

        private void load(Properties properties) {
            try {
                value = Integer.parseInt(properties.getProperty(key, Integer.toString(defaultValue)));
            } catch (NumberFormatException ignored) {
                value = defaultValue;
            }
        }
    }
}
