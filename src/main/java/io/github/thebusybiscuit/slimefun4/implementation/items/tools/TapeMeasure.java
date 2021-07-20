package io.github.thebusybiscuit.slimefun4.implementation.items.tools;

import java.text.DecimalFormat;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotPlaceable;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;

/**
 * The {@link TapeMeasure} is used to measure the distance between two {@link Block Blocks}.
 * 
 * @author TheBusyBiscuit
 *
 */
public class TapeMeasure extends SimpleSlimefunItem<ItemUseHandler> implements NotPlaceable {

    private final NamespacedKey key = new NamespacedKey(Slimefun.instance(), "anchor");
    private final DecimalFormat format = new DecimalFormat("##.###");

    @ParametersAreNonnullByDefault
    public TapeMeasure(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    @Override
    public @Nonnull ItemUseHandler getItemHandler() {
        return e -> {
            e.cancel();

            if (e.getClickedBlock().isPresent()) {
                Block block = e.getClickedBlock().get();

                if (e.getPlayer().isSneaking()) {
                    setAnchor(e.getPlayer(), e.getItem(), block);
                } else {
                    measure(e.getPlayer(), e.getItem(), block);
                }
            }
        };
    }

    @ParametersAreNonnullByDefault
    private void setAnchor(Player p, ItemStack item, Block block) {
        ItemMeta meta = item.getItemMeta();

        JsonObject json = new JsonObject();
        json.addProperty("world", block.getWorld().getUID().toString());
        json.addProperty("x", block.getX());
        json.addProperty("y", block.getY());
        json.addProperty("z", block.getZ());

        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, json.toString());

        String anchor = block.getX() + " | " + block.getY() + " | " + block.getZ();
        Slimefun.getLocalization().sendMessage(p, "messages.tape-measure.anchor-set", msg -> msg.replace("%anchor%", anchor));

        item.setItemMeta(meta);
    }

    @ParametersAreNonnullByDefault
    private void measure(Player p, ItemStack item, Block block) {
        OptionalDouble distance = getDistance(p, item, block);

        if (distance.isPresent()) {
            p.playSound(block.getLocation(), Sound.ITEM_BOOK_PUT, 1, 0.7F);
            String label = format.format(distance.getAsDouble());
            Slimefun.getLocalization().sendMessage(p, "messages.tape-measure.distance", msg -> msg.replace("%distance%", label));
        }
    }

    @ParametersAreNonnullByDefault
    public @Nonnull Optional<Location> getAnchor(Player p, ItemStack item) {
        ItemMeta meta = item.getItemMeta();

        String data = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);

        if (data != null) {
            JsonObject json = new JsonParser().parse(data).getAsJsonObject();

            UUID uuid = UUID.fromString(json.get("world").getAsString());

            if (p.getWorld().getUID().equals(uuid)) {
                int x = json.get("x").getAsInt();
                int y = json.get("y").getAsInt();
                int z = json.get("z").getAsInt();
                Location loc = new Location(p.getWorld(), x, y, z);
                return Optional.of(loc);
            } else {
                Slimefun.getLocalization().sendMessage(p, "messages.tape-measure.wrong-world");
                return Optional.empty();
            }
        } else {
            Slimefun.getLocalization().sendMessage(p, "messages.tape-measure.no-anchor");
            return Optional.empty();
        }
    }

    @ParametersAreNonnullByDefault
    public @Nonnull OptionalDouble getDistance(Player p, ItemStack item, Block block) {
        Optional<Location> anchor = getAnchor(p, item);

        if (anchor.isPresent()) {
            Location loc = anchor.get();
            return OptionalDouble.of(loc.distance(block.getLocation()));
        } else {
            return OptionalDouble.empty();
        }
    }

}
