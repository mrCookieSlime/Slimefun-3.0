package io.github.thebusybiscuit.slimefun4.core.guide.options;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import io.github.bakedlibs.dough.common.ChatColors;
import io.github.bakedlibs.dough.common.CommonPatterns;
import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.core.services.github.Contributor;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;

import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;

/**
 * This menu shows a list of every {@link Contributor} to this project.
 * 
 * @author TheBusyBiscuit
 *
 */
final class ContributorsMenu {

    private ContributorsMenu() {}

    public static void open(Player p, int page) {
        ChestMenu menu = new ChestMenu(Slimefun.getLocalization().getMessage(p, "guide.title.credits"));

        menu.setEmptySlotsClickable(false);
        menu.addMenuOpeningHandler(pl -> pl.playSound(pl.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 0.7F, 0.7F));

        ChestMenuUtils.drawBackground(menu, 0, 2, 3, 4, 5, 6, 7, 8, 45, 47, 48, 49, 50, 51, 53);

        menu.addItem(1, new CustomItemStack(ChestMenuUtils.getBackButton(p, "", "&7" + Slimefun.getLocalization().getMessage(p, "guide.back.settings"))));
        menu.addMenuClickHandler(1, (pl, slot, item, action) -> {
            SlimefunGuideSettings.openSettings(pl, p.getInventory().getItemInMainHand());
            return false;
        });

        List<Contributor> contributors = new ArrayList<>(Slimefun.getGitHubService().getContributors().values());
        contributors.sort(Comparator.comparingInt(Contributor::getPosition));

        for (int i = page * 36; i < contributors.size() && i < (page + 1) * 36; i++) {
            Contributor contributor = contributors.get(i);
            ItemStack skull = getContributorHead(p, contributor);

            menu.addItem(i - page * 36 + 9, skull);
            menu.addMenuClickHandler(i - page * 36 + 9, (pl, slot, item, action) -> {
                if (contributor.getProfile() != null) {
                    pl.closeInventory();
                    ChatUtils.sendURL(pl, contributor.getProfile());
                }
                return false;
            });
        }

        int pages = (contributors.size() - 1) / 36 + 1;

        menu.addItem(46, ChestMenuUtils.getPreviousButton(p, page + 1, pages));
        menu.addMenuClickHandler(46, (pl, slot, item, action) -> {
            if (page > 0) {
                open(pl, page - 1);
            }

            return false;
        });

        menu.addItem(52, ChestMenuUtils.getNextButton(p, page + 1, pages));
        menu.addMenuClickHandler(52, (pl, slot, item, action) -> {
            if (page + 1 < pages) {
                open(pl, page + 1);
            }

            return false;
        });

        menu.open(p);
    }

    private static ItemStack getContributorHead(Player p, Contributor contributor) {
        ItemStack skull = SlimefunUtils.getCustomHead(contributor.getTexture());

        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setDisplayName(contributor.getDisplayName());

        List<String> lore = new LinkedList<>();
        lore.add("");

        for (Map.Entry<String, Integer> entry : contributor.getContributions()) {
            String info = entry.getKey();

            if (!info.startsWith("&")) {
                String[] segments = CommonPatterns.COMMA.split(info);
                info = Slimefun.getLocalization().getMessage(p, "guide.credits.roles." + segments[0]);

                if (segments.length == 2) {
                    info += " &7(" + Slimefun.getLocalization().getMessage(p, "languages." + segments[1]) + ')';
                }
            }

            if (entry.getValue() > 0) {
                String commits = Slimefun.getLocalization().getMessage(p, "guide.credits." + (entry.getValue() > 1 ? "commits" : "commit"));

                info += " &7(" + entry.getValue() + ' ' + commits + ')';
            }

            lore.add(ChatColors.color(info));
        }

        if (contributor.getProfile() != null) {
            lore.add("");
            lore.add(ChatColors.color("&7\u21E8 &e") + Slimefun.getLocalization().getMessage(p, "guide.credits.profile-link"));
        }

        meta.setLore(lore);
        skull.setItemMeta(meta);
        return skull;
    }

}
