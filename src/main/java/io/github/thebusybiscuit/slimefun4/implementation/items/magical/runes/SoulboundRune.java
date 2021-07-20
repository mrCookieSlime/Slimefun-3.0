package io.github.thebusybiscuit.slimefun4.implementation.items.magical.runes;

import java.util.Collection;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.core.attributes.Soulbound;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemDropHandler;
import io.github.thebusybiscuit.slimefun4.core.services.sounds.SoundEffect;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.items.magical.SoulboundItem;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;

/**
 * This {@link SlimefunItem} allows you to convert any {@link ItemStack} into a
 * {@link SoulboundItem}. It is also one of the very few utilisations of {@link ItemDropHandler}.
 * 
 * @author Linox
 * @author Walshy
 * @author TheBusyBiscuit
 * 
 * @see ItemDropHandler
 * @see Soulbound
 *
 */
public class SoulboundRune extends SimpleSlimefunItem<ItemDropHandler> {

    private static final double RANGE = 1.5;

    @ParametersAreNonnullByDefault
    public SoulboundRune(Category category, SlimefunItemStack item, RecipeType type, ItemStack[] recipe) {
        super(category, item, type, recipe);
    }

    @Override
    public ItemDropHandler getItemHandler() {
        return (e, p, item) -> {
            if (isItem(item.getItemStack())) {

                if (!canUse(p, true)) {
                    return true;
                }

                SlimefunPlugin.runSync(() -> activate(p, item), 20L);

                return true;
            }
            return false;
        };
    }

    private void activate(@Nonnull Player p, @Nonnull Item rune) {
        // Being sure the entity is still valid and not picked up or whatsoever.
        if (!rune.isValid()) {
            return;
        }

        Location l = rune.getLocation();
        Collection<Entity> entites = l.getWorld().getNearbyEntities(l, RANGE, RANGE, RANGE, this::findCompatibleItem);
        Optional<Entity> optional = entites.stream().findFirst();

        if (optional.isPresent()) {
            Item item = (Item) optional.get();
            ItemStack itemStack = item.getItemStack();

            if (itemStack.getAmount() == 1) {
                // This lightning is just an effect, it deals no damage.
                l.getWorld().strikeLightningEffect(l);

                SlimefunPlugin.runSync(() -> {
                    // Being sure entities are still valid and not picked up or whatsoever.
                    if (rune.isValid() && item.isValid() && itemStack.getAmount() == 1) {

                        l.getWorld().createExplosion(l, 0);
                        SoundEffect.SOULBOUND_RUNE_RITUAL_SOUND.playAt(l, SoundCategory.PLAYERS);

                        item.remove();
                        rune.remove();

                        SlimefunUtils.setSoulbound(itemStack, true);
                        l.getWorld().dropItemNaturally(l, itemStack);

                        SlimefunPlugin.getLocalization().sendMessage(p, "messages.soulbound-rune.success", true);
                    } else {
                        SlimefunPlugin.getLocalization().sendMessage(p, "messages.soulbound-rune.fail", true);
                    }
                }, 10L);
            } else {
                SlimefunPlugin.getLocalization().sendMessage(p, "messages.soulbound-rune.fail", true);
            }
        }
    }

    /**
     * This method checks whether a given {@link Entity} is an {@link Item} which can
     * be bound to a soul. We exclude the {@link SoulboundRune} itself and any already
     * {@link Soulbound} {@link Item}.
     * 
     * @param entity
     *            The {@link Entity} to check
     * 
     * @return Whether this {@link Entity} is compatible
     */
    private boolean findCompatibleItem(@Nonnull Entity entity) {
        if (entity instanceof Item) {
            Item item = (Item) entity;

            return !SlimefunUtils.isSoulbound(item.getItemStack()) && !isItem(item.getItemStack());
        }

        return false;
    }

}
