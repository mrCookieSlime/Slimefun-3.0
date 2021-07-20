package io.github.thebusybiscuit.slimefun4.utils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import io.github.thebusybiscuit.cscorelib2.item.ImmutableItemMeta;
import io.github.thebusybiscuit.cscorelib2.skull.SkullItem;
import io.github.thebusybiscuit.slimefun4.api.MinecraftVersion;
import io.github.thebusybiscuit.slimefun4.api.events.SlimefunItemSpawnEvent;
import io.github.thebusybiscuit.slimefun4.api.exceptions.PrematureCodeException;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSpawnReason;
import io.github.thebusybiscuit.slimefun4.core.attributes.Radioactive;
import io.github.thebusybiscuit.slimefun4.core.attributes.Soulbound;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.implementation.items.altar.AncientPedestal;
import io.github.thebusybiscuit.slimefun4.implementation.tasks.CapacitorTextureUpdateTask;
import io.github.thebusybiscuit.slimefun4.utils.itemstack.ItemStackWrapper;

import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;

/**
 * This utility class holds method that are directly linked to Slimefun.
 * It provides a very crucial method for {@link ItemStack} comparison, as well as a simple method
 * to check if an {@link ItemStack} is {@link Soulbound} or not.
 * 
 * @author TheBusyBiscuit
 * @author Walshy
 *
 */
public final class SlimefunUtils {

    private static final String NO_PICKUP_METADATA = "no_pickup";
    private static final String SOULBOUND_LORE = ChatColor.GRAY + "Soulbound";

    private SlimefunUtils() {}

    /**
     * This method quickly returns whether an {@link Item} was marked as "no_pickup" by
     * a Slimefun device.
     * 
     * @param item
     *            The {@link Item} to query
     * @return Whether the {@link Item} is excluded from being picked up
     */
    public static boolean hasNoPickupFlag(@Nonnull Item item) {
        return item.hasMetadata(NO_PICKUP_METADATA);
    }

    /**
     * This will prevent the given {@link Item} from being picked up.
     * This is useful for display items which the {@link AncientPedestal} uses.
     * 
     * @param item
     *            The {@link Item} to prevent from being picked up
     * @param context
     *            The context in which this {@link Item} was flagged
     */
    public static void markAsNoPickup(@Nonnull Item item, @Nonnull String context) {
        item.setMetadata(NO_PICKUP_METADATA, new FixedMetadataValue(SlimefunPlugin.instance(), context));
    }

    /**
     * This method checks whether the given {@link ItemStack} is considered {@link Soulbound}.
     *
     * @param item
     *            The {@link ItemStack} to check for
     * @return Whether the given item is soulbound
     */
    public static boolean isSoulbound(@Nullable ItemStack item) {
        return isSoulbound(item, null);
    }

    /**
     * This method checks whether the given {@link ItemStack} is considered {@link Soulbound}.
     * If the provided item is a {@link SlimefunItem} then this method will also check that the item
     * is enabled in the provided {@link World}.
     *
     * @param item
     *            The {@link ItemStack} to check for
     * @param world
     *            The {@link World} to check if the {@link SlimefunItem} is enabled in if applicable.
     *            If {@code null} then this will not do a world check.
     * @return Whether the given item is soulbound
     */
    public static boolean isSoulbound(@Nullable ItemStack item, @Nullable World world) {
        if (item != null && item.getType() != Material.AIR) {
            ItemMeta meta = item.hasItemMeta() ? item.getItemMeta() : null;

            if (hasSoulboundFlag(meta)) {
                return true;
            }

            SlimefunItem sfItem = SlimefunItem.getByItem(item);

            if (sfItem instanceof Soulbound) {
                if (world != null) {
                    return !sfItem.isDisabledIn(world);
                } else {
                    return !sfItem.isDisabled();
                }
            } else if (meta != null) {
                return meta.hasLore() && meta.getLore().contains(SOULBOUND_LORE);
            }

        }
        return false;
    }

    private static boolean hasSoulboundFlag(@Nullable ItemMeta meta) {
        if (meta != null) {
            PersistentDataContainer container = meta.getPersistentDataContainer();
            NamespacedKey key = SlimefunPlugin.getRegistry().getSoulboundDataKey();

            return container.has(key, PersistentDataType.BYTE);
        }

        return false;
    }

    /**
     * Toggles an {@link ItemStack} to be Soulbound.<br>
     * If true is passed, this will add the {@link #SOULBOUND_LORE} and
     * add a {@link NamespacedKey} to the item so it can be quickly identified
     * by {@link #isSoulbound(ItemStack)}.<br>
     * If false is passed, this property will be removed.
     *
     * @param item
     *            The {@link ItemStack} you want to add/remove Soulbound from.
     * @param makeSoulbound
     *            If they item should be soulbound.
     * 
     * @see #isSoulbound(ItemStack)
     */
    public static void setSoulbound(@Nullable ItemStack item, boolean makeSoulbound) {
        if (item == null || item.getType() == Material.AIR) {
            throw new IllegalArgumentException("A soulbound item cannot be null or air!");
        }

        boolean isSoulbound = isSoulbound(item);
        ItemMeta meta = item.getItemMeta();

        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey key = SlimefunPlugin.getRegistry().getSoulboundDataKey();

        if (makeSoulbound && !isSoulbound) {
            container.set(key, PersistentDataType.BYTE, (byte) 1);
        }

        if (!makeSoulbound && isSoulbound) {
            container.remove(key);
        }

        List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();

        if (makeSoulbound && !isSoulbound) {
            lore.add(SOULBOUND_LORE);
        }

        if (!makeSoulbound && isSoulbound) {
            lore.remove(SOULBOUND_LORE);
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    /**
     * This method checks whether the given {@link ItemStack} is radioactive.
     * 
     * @param item
     *            The {@link ItemStack} to check
     * 
     * @return Whether this {@link ItemStack} is radioactive or not
     */
    public static boolean isRadioactive(@Nullable ItemStack item) {
        return SlimefunItem.getByItem(item) instanceof Radioactive;
    }

    /**
     * This method returns an {@link ItemStack} for the given texture.
     * The result will be a Player Head with this texture.
     * 
     * @param texture
     *            The texture for this head (base64 or hash)
     * 
     * @return An {@link ItemStack} with this Head texture
     */
    public static @Nonnull ItemStack getCustomHead(@Nonnull String texture) {
        Validate.notNull(texture, "The provided texture is null");

        if (SlimefunPlugin.instance() == null) {
            throw new PrematureCodeException("You cannot instantiate a custom head before Slimefun was loaded.");
        }

        if (SlimefunPlugin.getMinecraftVersion() == MinecraftVersion.UNIT_TEST) {
            // com.mojang.authlib.GameProfile does not exist in a Test Environment
            return new ItemStack(Material.PLAYER_HEAD);
        }

        String base64 = texture;

        if (PatternUtils.HEXADECIMAL.matcher(texture).matches()) {
            base64 = Base64.getEncoder().encodeToString(("{\"textures\":{\"SKIN\":{\"url\":\"http://textures.minecraft.net/texture/" + texture + "\"}}}").getBytes(StandardCharsets.UTF_8));
        }

        return SkullItem.fromBase64(base64);
    }

    public static boolean containsSimilarItem(Inventory inventory, ItemStack item, boolean checkLore) {
        if (inventory == null || item == null) {
            return false;
        }

        // Performance optimization
        if (!(item instanceof SlimefunItemStack)) {
            item = ItemStackWrapper.wrap(item);
        }

        for (ItemStack stack : inventory.getStorageContents()) {
            if (stack == null || stack.getType() == Material.AIR) {
                continue;
            }

            if (isItemSimilar(stack, item, checkLore, false)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isItemSimilar(@Nullable ItemStack item, @Nullable ItemStack sfitem, boolean checkLore) {
        return isItemSimilar(item, sfitem, checkLore, true);
    }

    public static boolean isItemSimilar(@Nullable ItemStack item, @Nullable ItemStack sfitem, boolean checkLore, boolean checkAmount) {
        if (item == null) {
            return sfitem == null;
        } else if (sfitem == null) {
            return false;
        } else if (item.getType() != sfitem.getType()) {
            return false;
        } else if (checkAmount && item.getAmount() < sfitem.getAmount()) {
            return false;
        } else if (sfitem instanceof SlimefunItemStack && item instanceof SlimefunItemStack) {
            return ((SlimefunItemStack) item).getItemId().equals(((SlimefunItemStack) sfitem).getItemId());
        } else if (item.hasItemMeta()) {
            ItemMeta itemMeta = item.getItemMeta();

            if (sfitem instanceof SlimefunItemStack) {
                Optional<String> id = SlimefunPlugin.getItemDataService().getItemData(itemMeta);

                if (id.isPresent()) {
                    return id.get().equals(((SlimefunItemStack) sfitem).getItemId());
                }

                ImmutableItemMeta meta = ((SlimefunItemStack) sfitem).getImmutableMeta();
                return equalsItemMeta(itemMeta, meta, checkLore);
            } else if (sfitem.hasItemMeta()) {
                return equalsItemMeta(itemMeta, sfitem.getItemMeta(), checkLore);
            } else {
                return false;
            }
        } else {
            return !sfitem.hasItemMeta();
        }
    }

    private static boolean equalsItemMeta(@Nonnull ItemMeta itemMeta, @Nonnull ImmutableItemMeta meta, boolean checkLore) {
        Optional<String> displayName = meta.getDisplayName();

        if (itemMeta.hasDisplayName() != displayName.isPresent()) {
            return false;
        } else if (itemMeta.hasDisplayName() && displayName.isPresent() && !itemMeta.getDisplayName().equals(displayName.get())) {
            return false;
        } else if (!checkLore) {
            return true;
        } else {
            Optional<List<String>> itemLore = meta.getLore();

            if (itemMeta.hasLore() && itemLore.isPresent()) {
                return equalsLore(itemMeta.getLore(), itemLore.get());
            } else {
                return !itemMeta.hasLore() && !itemLore.isPresent();
            }
        }
    }

    private static boolean equalsItemMeta(@Nonnull ItemMeta itemMeta, @Nonnull ItemMeta sfitemMeta, boolean checkLore) {
        if (itemMeta.hasDisplayName() != sfitemMeta.hasDisplayName()) {
            return false;
        } else if (itemMeta.hasDisplayName() && sfitemMeta.hasDisplayName() && !itemMeta.getDisplayName().equals(sfitemMeta.getDisplayName())) {
            return false;
        } else if (checkLore) {
            boolean hasItemMetaLore = itemMeta.hasLore();
            boolean hasSfItemMetaLore = sfitemMeta.hasLore();
            if (hasItemMetaLore && hasSfItemMetaLore && !equalsLore(itemMeta.getLore(), sfitemMeta.getLore())) {
                return false;
            } else if (hasItemMetaLore != hasSfItemMetaLore) {
                return false;
            }
        }

        if (itemMeta instanceof PotionMeta && sfitemMeta instanceof PotionMeta) {
            return ((PotionMeta) itemMeta).getBasePotionData().equals(((PotionMeta) sfitemMeta).getBasePotionData());
        }

        return true;
    }

    /**
     * This checks if the two provided lores are equal.
     * This method will ignore any lines such as the soulbound one.
     * 
     * @param lore1
     *            The first lore
     * @param lore2
     *            The second lore
     * 
     * @return Whether the two lores are equal
     */
    public static boolean equalsLore(@Nonnull List<String> lore1, @Nonnull List<String> lore2) {
        Validate.notNull(lore1, "Cannot compare lore that is null!");
        Validate.notNull(lore2, "Cannot compare lore that is null!");

        List<String> longerList = lore1.size() > lore2.size() ? lore1 : lore2;
        List<String> shorterList = lore1.size() > lore2.size() ? lore2 : lore1;

        int a = 0;
        int b = 0;

        for (; a < longerList.size(); a++) {
            if (isLineIgnored(longerList.get(a))) {
                continue;
            }

            while (shorterList.size() > b && isLineIgnored(shorterList.get(b))) {
                b++;
            }

            if (b >= shorterList.size()) {
                return false;
            } else if (longerList.get(a).equals(shorterList.get(b))) {
                b++;
            } else {
                return false;
            }
        }

        while (shorterList.size() > b && isLineIgnored(shorterList.get(b))) {
            b++;
        }

        return b == shorterList.size();
    }

    private static boolean isLineIgnored(@Nonnull String line) {
        return line.equals(SOULBOUND_LORE);
    }

    public static void updateCapacitorTexture(@Nonnull Location l, int charge, int capacity) {
        Validate.notNull(l, "Cannot update a texture for null");
        Validate.isTrue(capacity > 0, "Capacity must be greater than zero!");

        SlimefunPlugin.runSync(new CapacitorTextureUpdateTask(l, charge, capacity));
    }

    /**
     * This checks whether the {@link Player} is able to use the given {@link ItemStack}.
     * It will always return <code>true</code> for non-Slimefun items.
     * <p>
     * If you already have an instance of {@link SlimefunItem}, please use {@link SlimefunItem#canUse(Player, boolean)}.
     * 
     * @param p
     *            The {@link Player}
     * @param item
     *            The {@link ItemStack} to check
     * @param sendMessage
     *            Whether to send a message response to the {@link Player}
     * 
     * @return Whether the {@link Player} is able to use that item.
     */
    public static boolean canPlayerUseItem(@Nonnull Player p, @Nullable ItemStack item, boolean sendMessage) {
        Validate.notNull(p, "The player cannot be null");

        SlimefunItem sfItem = SlimefunItem.getByItem(item);

        if (sfItem != null) {
            return sfItem.canUse(p, sendMessage);
        } else {
            return true;
        }
    }

    /**
     * Helper method to spawn an {@link ItemStack}.
     * This method automatically calls a {@link SlimefunItemSpawnEvent} to allow
     * other plugins to catch the item being dropped.
     * 
     * @param loc
     *            The {@link Location} where to drop the item
     * @param item
     *            The {@link ItemStack} to drop
     * @param reason
     *            The {@link ItemSpawnReason} why the item is being dropped
     * @param addRandomOffset
     *            Whether a random offset should be added (see {@link World#dropItemNaturally(Location, ItemStack)})
     * 
     * @return The dropped {@link Item} (or null if the {@link SlimefunItemSpawnEvent} was cancelled)
     */
    @ParametersAreNonnullByDefault
    public static @Nullable Item spawnItem(Location loc, ItemStack item, ItemSpawnReason reason, boolean addRandomOffset) {
        SlimefunItemSpawnEvent event = new SlimefunItemSpawnEvent(loc, item, reason);
        SlimefunPlugin.instance().getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            World world = event.getLocation().getWorld();

            if (addRandomOffset) {
                return world.dropItemNaturally(event.getLocation(), event.getItemStack());
            } else {
                return world.dropItem(event.getLocation(), event.getItemStack());
            }
        } else {
            return null;
        }
    }

    /**
     * Helper method to spawn an {@link ItemStack}.
     * This method automatically calls a {@link SlimefunItemSpawnEvent} to allow
     * other plugins to catch the item being dropped.
     * 
     * @param loc
     *            The {@link Location} where to drop the item
     * @param item
     *            The {@link ItemStack} to drop
     * @param reason
     *            The {@link ItemSpawnReason} why the item is being dropped
     * 
     * @return The dropped {@link Item} (or null if the {@link SlimefunItemSpawnEvent} was cancelled)
     */
    @ParametersAreNonnullByDefault
    public static @Nullable Item spawnItem(Location loc, ItemStack item, ItemSpawnReason reason) {
        return spawnItem(loc, item, reason, false);
    }

}
