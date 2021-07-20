package io.github.thebusybiscuit.slimefun4.utils.tags;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import io.github.bakedlibs.dough.common.CommonPatterns;
import io.github.thebusybiscuit.slimefun4.api.exceptions.TagMisconfigurationException;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.PatternUtils;

/**
 * The {@link TagParser} is responsible for parsing a JSON input into a {@link SlimefunTag}.
 * 
 * @author TheBusyBiscuit
 * 
 * @see SlimefunTag
 *
 */
public class TagParser implements Keyed {

    /**
     * Every {@link Tag} has a {@link NamespacedKey}.
     * This is the {@link NamespacedKey} for the resulting {@link Tag}.
     */
    private final NamespacedKey key;

    /**
     * This constructs a new {@link TagParser}.
     * 
     * @param key
     *            The {@link NamespacedKey} of the resulting {@link SlimefunTag}
     */
    public TagParser(@Nonnull NamespacedKey key) {
        this.key = key;
    }

    /**
     * This constructs a new {@link TagParser} for the given {@link SlimefunTag}
     * 
     * @param tag
     *            The {@link SlimefunTag} to parse inputs for
     */
    TagParser(@Nonnull SlimefunTag tag) {
        this(tag.getKey());
    }

    void parse(@Nonnull SlimefunTag tag, @Nonnull BiConsumer<Set<Material>, Set<Tag<Material>>> callback) throws TagMisconfigurationException {
        String path = "/tags/" + tag.getKey().getKey() + ".json";

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Slimefun.class.getResourceAsStream(path), StandardCharsets.UTF_8))) {
            parse(reader.lines().collect(Collectors.joining("")), callback);
        } catch (IOException x) {
            throw new TagMisconfigurationException(key, x);
        }
    }

    /**
     * This will parse the given JSON {@link String} and run the provided callback with {@link Set Sets} of
     * matched {@link Material Materials} and {@link Tag Tags}.
     * 
     * @param json
     *            The JSON {@link String} to parse
     * @param callback
     *            A callback to run after successfully parsing the input
     * 
     * @throws TagMisconfigurationException
     *             This is thrown whenever the given input is malformed or no adequate
     *             {@link Material} or {@link Tag} could be found
     */
    public void parse(@Nonnull String json, @Nonnull BiConsumer<Set<Material>, Set<Tag<Material>>> callback) throws TagMisconfigurationException {
        Validate.notNull(json, "Cannot parse a null String");

        try {
            Set<Material> materials = EnumSet.noneOf(Material.class);
            Set<Tag<Material>> tags = new HashSet<>();

            JsonParser parser = new JsonParser();
            JsonObject root = parser.parse(json).getAsJsonObject();
            JsonElement child = root.get("values");

            if (child instanceof JsonArray) {
                JsonArray values = child.getAsJsonArray();

                for (JsonElement element : values) {
                    if (element instanceof JsonPrimitive && ((JsonPrimitive) element).isString()) {
                        // Strings will be parsed directly
                        parsePrimitiveValue(element.getAsString(), materials, tags, true);
                    } else if (element instanceof JsonObject) {
                        /*
                         * JSONObjects can have a "required" property which can
                         * make it optional to resolve the underlying value
                         */
                        parseComplexValue(element.getAsJsonObject(), materials, tags);
                    } else {
                        throw new TagMisconfigurationException(key, "Unexpected value format: " + element.getClass().getSimpleName() + " - " + element.toString());
                    }
                }

                // Run the callback with the filled-in materials and tags
                callback.accept(materials, tags);
            } else {
                // The JSON seems to be empty yet valid
                throw new TagMisconfigurationException(key, "No values array specified");
            }
        } catch (IllegalStateException | JsonParseException x) {
            throw new TagMisconfigurationException(key, x);
        }
    }

    @ParametersAreNonnullByDefault
    private void parsePrimitiveValue(String value, Set<Material> materials, Set<Tag<Material>> tags, boolean throwException) throws TagMisconfigurationException {
        if (PatternUtils.MINECRAFT_MATERIAL.matcher(value).matches()) {
            // Match the NamespacedKey against Materials
            Material material = Material.matchMaterial(value);

            if (material != null) {
                // If the Material could be matched, simply add it to our Set
                materials.add(material);
            } else if (throwException) {
                throw new TagMisconfigurationException(key, "Minecraft Material '" + value + "' seems to not exist!");
            }
        } else if (PatternUtils.MINECRAFT_TAG.matcher(value).matches()) {
            // Get the actual Key portion and match it to item and block tags.
            String keyValue = CommonPatterns.COLON.split(value)[1];
            NamespacedKey namespacedKey = NamespacedKey.minecraft(keyValue);
            Tag<Material> itemsTag = Bukkit.getTag(Tag.REGISTRY_ITEMS, namespacedKey, Material.class);
            Tag<Material> blocksTag = Bukkit.getTag(Tag.REGISTRY_BLOCKS, namespacedKey, Material.class);

            if (itemsTag != null) {
                // We will prioritize the item tag
                tags.add(itemsTag);
            } else if (blocksTag != null) {
                // If no item tag exists, fall back to the block tag
                tags.add(blocksTag);
            } else if (throwException) {
                // If both fail, then the tag does not exist.
                throw new TagMisconfigurationException(key, "There is no '" + value + "' tag in Minecraft.");
            }
        } else if (PatternUtils.SLIMEFUN_TAG.matcher(value).matches()) {
            // Get a SlimefunTag enum value for the given key
            String keyValue = CommonPatterns.COLON.split(value)[1].toUpperCase(Locale.ROOT);
            SlimefunTag tag = SlimefunTag.getTag(keyValue);

            if (tag != null) {
                tags.add(tag);
            } else if (throwException) {
                throw new TagMisconfigurationException(key, "There is no '" + value + "' tag in Slimefun");
            }
        } else if (throwException) {
            // If no RegEx pattern matched, it's malformed.
            throw new TagMisconfigurationException(key, "Could not recognize value '" + value + "'");
        }
    }

    @ParametersAreNonnullByDefault
    private void parseComplexValue(JsonObject entry, Set<Material> materials, Set<Tag<Material>> tags) throws TagMisconfigurationException {
        JsonElement id = entry.get("id");
        JsonElement required = entry.get("required");

        // Check if the entry contains elements of the correct type
        if (id instanceof JsonPrimitive && ((JsonPrimitive) id).isString() && required instanceof JsonPrimitive && ((JsonPrimitive) required).isBoolean()) {
            boolean isRequired = required.getAsBoolean();

            /*
             * If the Tag is required, an exception may be thrown.
             * Otherwise it will just ignore the value
             */
            parsePrimitiveValue(id.getAsString(), materials, tags, isRequired);
        } else {
            throw new TagMisconfigurationException(key, "Found a JSON Object value without an id!");
        }
    }

    @Override
    public @Nonnull NamespacedKey getKey() {
        return key;
    }

}
