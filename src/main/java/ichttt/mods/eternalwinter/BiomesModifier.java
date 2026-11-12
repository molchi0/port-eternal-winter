package ichttt.mods.eternalwinter;

import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.biome.Biome;

/**
 * Replacement for the Forge {@code BiomeModifier}: makes every (selected) biome cold enough for snow.
 */
public final class BiomesModifier {
    private BiomesModifier() {
    }

    public static void register() {
        BiomeModifications.create(Identifier.fromNamespaceAndPath(EternalWinter.MOD_ID, "change_to_snow"))
                // POST_PROCESSING runs last, so other mods can't undo our climate change.
                .add(ModificationPhase.POST_PROCESSING, BiomesModifier::shouldModify, BiomesModifier::modify);
    }

    private static boolean shouldModify(BiomeSelectionContext context) {
        Identifier id = context.getBiomeKey().identifier();
        boolean listed = EWConfig.biomeList().stream()
                .anyMatch(entry -> entry.equals(id.getPath()) || entry.equals(id.toString()));

        return switch (EWConfig.listMode()) {
            case BLACKLIST -> !listed;
            case WHITELIST -> listed;
        };
    }

    private static void modify(BiomeSelectionContext selection, BiomeModificationContext modification) {
        BiomeModificationContext.WeatherContext weather = modification.getWeather();

        float downfall = EWConfig.downfall();
        if (downfall != -1F) {
            weather.setDownfall(downfall);
        }
        weather.setTemperature(0F);
        weather.setTemperatureModifier(Biome.TemperatureModifier.NONE);
        weather.setPrecipitation(true);

        EternalWinter.LOGGER.debug("Modified Biome {} successful!", selection.getBiomeKey().identifier());
    }
}
