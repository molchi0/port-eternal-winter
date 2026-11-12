package ichttt.mods.eternalwinter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Simple JSON config ({@code config/eternalwinter.json}). Fabric has no built-in config system like Forge's
 * {@code ForgeConfigSpec}, so this replaces it. The keys and defaults are the same as in the Forge version.
 */
public final class EWConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "eternalwinter.json";

    private static final List<String> DEFAULT_BIOME_LIST = List.of(
            "river", "ocean", "deep_ocean", "warm_ocean", "lukewarm_ocean", "cold_ocean",
            "deep_warm_ocean", "deep_lukewarm_ocean", "deep_cold_ocean", "deep_frozen_ocean");

    private static volatile ListMode listMode = ListMode.BLACKLIST;
    private static volatile List<String> biomeList = DEFAULT_BIOME_LIST;
    private static volatile float downfall = -1F;

    private EWConfig() {
    }

    public static ListMode listMode() {
        return listMode;
    }

    public static List<String> biomeList() {
        return biomeList;
    }

    /** -1 means "keep the original downfall of the biome", otherwise a value between 0 and 1. */
    public static float downfall() {
        return downfall;
    }

    public static void load() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);

        if (Files.notExists(path)) {
            writeDefaults(path);
            return;
        }

        try (Reader reader = Files.newBufferedReader(path)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            parse(json);
        } catch (Exception e) {
            EternalWinter.LOGGER.error("Could not read {}, using default values", path, e);
        }
    }

    private static void parse(JsonObject json) {
        if (json.has("listMode")) {
            try {
                listMode = ListMode.valueOf(json.get("listMode").getAsString().trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                EternalWinter.LOGGER.error("Invalid listMode {}, expected BLACKLIST or WHITELIST. Using BLACKLIST",
                        json.get("listMode"));
            }
        }

        if (json.has("biomeList")) {
            List<String> list = new ArrayList<>();
            for (JsonElement element : json.getAsJsonArray("biomeList")) {
                list.add(element.getAsString());
            }
            biomeList = List.copyOf(list);
        }

        if (json.has("downfall")) {
            float value = json.get("downfall").getAsFloat();
            if (value != -1F && (value < 0F || value > 1F)) {
                EternalWinter.LOGGER.error("Invalid downfall {}, must be -1 or between 0 and 1. Using -1", value);
                value = -1F;
            }
            downfall = value;
        }
    }

    private static void writeDefaults(Path path) {
        JsonObject json = new JsonObject();
        json.addProperty("_comment_listMode",
                "If BLACKLIST, biomeList is treated as a blacklist of biomes not to touch. "
                        + "If WHITELIST, biomeList is treated as a whitelist of the only biomes to touch.");
        json.addProperty("listMode", ListMode.BLACKLIST.name());

        json.addProperty("_comment_biomeList", "The blacklist or whitelist, depending on listMode. "
                + "Entries are biome ids, either with namespace (minecraft:river) or just the path (river).");
        JsonArray array = new JsonArray();
        DEFAULT_BIOME_LIST.forEach(array::add);
        json.add("biomeList", array);

        json.addProperty("_comment_downfall", "The downfall value each biome should get. "
                + "If -1, EternalWinter keeps the original downfall of the biome, otherwise a value between 0 and 1.");
        json.addProperty("downfall", -1.0);

        try (Writer writer = Files.newBufferedWriter(path)) {
            GSON.toJson(json, writer);
        } catch (IOException e) {
            EternalWinter.LOGGER.error("Could not write default config to {}", path, e);
        }
    }
}
