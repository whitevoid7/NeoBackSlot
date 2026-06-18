package net.backslot.fabric.config;

import net.backslot.fabric.NeoBackSlotFabric;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class BackSlotCommonConfig {
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir()
            .resolve(NeoBackSlotFabric.MOD_ID)
            .resolve("common.properties");

    public static final BooleanValue KEEP_ITEMS_ON_DEATH = new BooleanValue("death.keepItemsOnDeath", false);

    private BackSlotCommonConfig() {
    }

    public static void load() {
        Properties properties = new Properties();
        if (Files.exists(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                properties.load(reader);
            } catch (IOException ignored) {
            }
        }

        KEEP_ITEMS_ON_DEATH.load(properties);
        save();
    }

    public static void save() {
        Properties properties = new Properties();
        properties.setProperty(KEEP_ITEMS_ON_DEATH.key, Boolean.toString(KEEP_ITEMS_ON_DEATH.get()));

        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                properties.store(writer, "NeoBackSlot common config");
            }
        } catch (IOException ignored) {
        }
    }

    public static final class BooleanValue {
        private final String key;
        private final boolean defaultValue;
        private boolean value;

        private BooleanValue(String key, boolean defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
            this.value = defaultValue;
        }

        public boolean get() {
            return value;
        }

        public void set(boolean value) {
            this.value = value;
        }

        private void load(Properties properties) {
            value = Boolean.parseBoolean(properties.getProperty(key, Boolean.toString(defaultValue)));
        }
    }
}
