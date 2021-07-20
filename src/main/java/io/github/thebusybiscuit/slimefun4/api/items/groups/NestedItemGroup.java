package io.github.thebusybiscuit.slimefun4.api.items.groups;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.guide.SurvivalSlimefunGuide;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;

import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;

public class NestedItemGroup extends FlexItemGroup {

    private static final int GROUP_SIZE = 36;
    private final List<SubItemGroup> subGroups = new ArrayList<>();

    @ParametersAreNonnullByDefault
    public NestedItemGroup(NamespacedKey key, ItemStack item) {
        this(key, item, 3);
    }

    @ParametersAreNonnullByDefault
    public NestedItemGroup(NamespacedKey key, ItemStack item, int tier) {
        super(key, item, tier);
    }

    /**
     * This will add the given {@link SubItemGroup} to this {@link NestedItemGroup}.
     * 
     * @param category
     *            The {@link SubItemGroup} to add.
     */
    public void addSubGroup(@Nonnull SubItemGroup category) {
        Validate.notNull(category, "The sub item group cannot be null!");

        subGroups.add(category);
    }

    /**
     * This will remove the given {@link SubItemGroup} from this {@link NestedItemGroup} (if present).
     * 
     * @param category
     *            The {@link SubItemGroup} to remove.
     */
    public void removeSubGroup(@Nonnull SubItemGroup category) {
        Validate.notNull(category, "The sub item group cannot be null!");

        subGroups.remove(category);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isVisible(Player p, PlayerProfile profile, SlimefunGuideMode mode) {
        return true;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void open(Player p, PlayerProfile profile, SlimefunGuideMode mode) {
        openGuide(p, profile, mode, 1);
    }

    @ParametersAreNonnullByDefault
    private void openGuide(Player p, PlayerProfile profile, SlimefunGuideMode mode, int page) {
        GuideHistory history = profile.getGuideHistory();

        if (mode == SlimefunGuideMode.SURVIVAL_MODE) {
            history.add(this, page);
        }

        ChestMenu menu = new ChestMenu(Slimefun.getLocalization().getMessage(p, "guide.title.main"));
        SurvivalSlimefunGuide guide = (SurvivalSlimefunGuide) Slimefun.getRegistry().getSlimefunGuide(mode);

        menu.setEmptySlotsClickable(false);
        menu.addMenuOpeningHandler(pl -> pl.playSound(pl.getLocation(), guide.getSound(), 1, 1));
        guide.createHeader(p, profile, menu);

        menu.addItem(1, new CustomItemStack(ChestMenuUtils.getBackButton(p, "", ChatColor.GRAY + Slimefun.getLocalization().getMessage(p, "guide.back.guide"))));
        menu.addMenuClickHandler(1, (pl, s, is, action) -> {
            SlimefunGuide.openMainMenu(profile, mode, history.getMainMenuPage());
            return false;
        });

        int index = 9;

        int target = (GROUP_SIZE * (page - 1)) - 1;

        while (target < (subGroups.size() - 1) && index < GROUP_SIZE + 9) {
            target++;

            SubItemGroup category = subGroups.get(target);
            menu.addItem(index, category.getItem(p));
            menu.addMenuClickHandler(index, (pl, slot, item, action) -> {
                SlimefunGuide.openCategory(profile, category, mode, 1);
                return false;
            });

            index++;
        }

        int pages = target == subGroups.size() - 1 ? page : (subGroups.size() - 1) / GROUP_SIZE + 1;

        menu.addItem(46, ChestMenuUtils.getPreviousButton(p, page, pages));
        menu.addMenuClickHandler(46, (pl, slot, item, action) -> {
            int next = page - 1;

            if (next != page && next > 0) {
                openGuide(p, profile, mode, next);
            }

            return false;
        });

        menu.addItem(52, ChestMenuUtils.getNextButton(p, page, pages));
        menu.addMenuClickHandler(52, (pl, slot, item, action) -> {
            int next = page + 1;

            if (next != page && next <= pages) {
                openGuide(p, profile, mode, next);
            }

            return false;
        });

        menu.open(p);
    }

}
