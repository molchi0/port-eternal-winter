package ichttt.mods.eternalwinter;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EternalWinter implements ModInitializer {
    public static final String MOD_ID = "eternalwinter";
    public static final Logger LOGGER = LoggerFactory.getLogger("EternalWinter");

    @Override
    public void onInitialize() {
        LOGGER.info("Starting up eternal winter mod");
        // The config has to be loaded before the biome registry is built (world load).
        EWConfig.load();
        BiomesModifier.register();
    }
}
