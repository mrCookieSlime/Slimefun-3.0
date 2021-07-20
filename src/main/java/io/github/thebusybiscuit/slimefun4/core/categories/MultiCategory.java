package io.github.thebusybiscuit.slimefun4.core.categories;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.cscorelib2.item.CustomItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.implementation.guide.SurvivalSlimefunGuide;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;

public class MultiCategory extends FlexCategory {

    private static final int CATEGORY_SIZE = 36;
    private final List<SubCategory> subCategories = new ArrayList<>();

    @ParametersAreNonnullByDefault
    public MultiCategory(NamespacedKey key, ItemStack item) {
        this(key, item, 3);
    }

    @ParametersAreNonnullByDefault
    public MultiCategory(NamespacedKey key, ItemStack item, int tier) {
        super(key, item, tier);
    }

    /**
     * This will add the given {@link SubCategory} to this {@link MultiCategory}.
     * 
     * @param category
     *            The {@link SubCategory} to add.
     */
    public void addSubCategory(@Nonnull SubCategory category) {
        Validate.notNull(category, "The Category cannot be null!");

        subCategories.add(category);
    }

    /**
     * This will remove the given {@link SubCategory} from this {@link MultiCategory} (if present).
     * 
     * @param category
     *            The {@link SubCategory} to remove.
     */
    public void removeSubCategory(@Nonnull SubCategory category) {
        Validate.notNull(category, "The Category cannot be null!");

        subCategories.remove(category);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isVisible(Player p, PlayerProfile profile, SlimefunGuideMode mode) {
        return mode == SlimefunGuideMode.SURVIVAL_MODE;
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

        ChestMenu menu = new ChestMenu(SlimefunPlugin.getLocalization().getMessage(p, "guide.title.main"));
        SurvivalSlimefunGuide guide = (SurvivalSlimefunGuide) SlimefunPlugin.getRegistry().getSlimefunGuide(mode);

        menu.setEmptySlotsClickable(false);
        menu.addMenuOpeningHandler(pl -> pl.playSound(pl.getLocation(), guide.getSound(), 1, 1));
        guide.createHeader(p, profile, menu);

        menu.addItem(1, new CustomItem(ChestMenuUtils.getBackButton(p, "", ChatColor.GRAY + SlimefunPlugin.getLocalization().getMessage(p, "guide.back.guide"))));
        menu.addMenuClickHandler(1, (pl, s, is, action) -> {
            SlimefunGuide.openMainMenu(profile, mode, history.getMainMenuPage());
            return false;
        });

        int index = 9;

        int target = (CATEGORY_SIZE * (page - 1)) - 1;

        while (target < (subCategories.size() - 1) && index < CATEGORY_SIZE + 9) {
            target++;

            SubCategory category = subCategories.get(target);
            menu.addItem(index, category.getItem(p));
            menu.addMenuClickHandler(index, (pl, slot, item, action) -> {
                SlimefunGuide.openCategory(profile, category, mode, 1);
                return false;
            });

            index++;
        }

        int pages = target == subCategories.size() - 1 ? page : (subCategories.size() - 1) / CATEGORY_SIZE + 1;

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
