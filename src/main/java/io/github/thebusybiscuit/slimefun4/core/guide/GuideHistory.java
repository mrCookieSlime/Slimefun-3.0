package io.github.thebusybiscuit.slimefun4.core.guide;

import java.util.Deque;
import java.util.LinkedList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;

/**
 * {@link GuideHistory} represents the browsing history of a {@link Player} through the
 * {@link SlimefunGuide}.
 * 
 * @author TheBusyBiscuit
 * 
 * @see SlimefunGuide
 * @see PlayerProfile
 *
 */
public class GuideHistory {

    private final PlayerProfile profile;
    private final Deque<GuideEntry<?>> queue = new LinkedList<>();
    private int mainMenuPage = 1;

    /**
     * This creates a new {@link GuideHistory} for the given {@link PlayerProfile}
     * 
     * @param profile
     *            The {@link PlayerProfile} this {@link GuideHistory} was made for
     */
    public GuideHistory(@Nonnull PlayerProfile profile) {
        Validate.notNull(profile, "Cannot create a GuideHistory without a PlayerProfile!");
        this.profile = profile;
    }

    /**
     * This method will clear this {@link GuideHistory} and remove all entries.
     */
    public void clear() {
        queue.clear();
    }

    /**
     * This method sets the page of the main menu of this {@link GuideHistory}
     * 
     * @param page
     *            The current page of the main menu that should be stored
     */
    public void setMainMenuPage(int page) {
        Validate.isTrue(page >= 1, "page must be greater than 0!");

        mainMenuPage = page;
    }

    /**
     * This returns the current main menu page of this {@link GuideHistory}
     * 
     * @return The main menu page of this {@link GuideHistory}
     */
    public int getMainMenuPage() {
        return mainMenuPage;
    }

    /**
     * This method adds a {@link ItemGroup} to this {@link GuideHistory}.
     * Should the {@link ItemGroup} already be the last element in this {@link GuideHistory},
     * then the entry will be overridden with the new page.
     * 
     * @param category
     *            The {@link ItemGroup} that should be added to this {@link GuideHistory}
     * @param page
     *            The current page of the {@link ItemGroup} that should be stored
     */
    public void add(@Nonnull ItemGroup category, int page) {
        refresh(category, page);
    }

    /**
     * This method adds a {@link ItemStack} to this {@link GuideHistory}.
     * Should the {@link ItemStack} already be the last element in this {@link GuideHistory},
     * then the entry will be overridden with the new page.
     * 
     * @param item
     *            The {@link ItemStack} that should be added to this {@link GuideHistory}
     * @param page
     *            The current page of the recipes of this {@link ItemStack}
     */
    public void add(@Nonnull ItemStack item, int page) {
        refresh(item, page);
    }

    /**
     * This method stores the given {@link SlimefunItem} in this {@link GuideHistory}.
     * 
     * @param item
     *            The {@link SlimefunItem} that should be added to this {@link GuideHistory}
     */
    public void add(@Nonnull SlimefunItem item) {
        Validate.notNull(item, "Cannot add a non-existing SlimefunItem to the GuideHistory!");
        queue.add(new GuideEntry<>(item, 0));
    }

    /**
     * This method stores the given search term in this {@link GuideHistory}.
     * 
     * @param searchTerm
     *            The term that the {@link Player} searched for
     */
    public void add(@Nonnull String searchTerm) {
        Validate.notNull(searchTerm, "Cannot add an empty Search Term to the GuideHistory!");
        queue.add(new GuideEntry<>(searchTerm, 0));
    }

    private <T> void refresh(@Nonnull T object, int page) {
        Validate.notNull(object, "Cannot add a null Entry to the GuideHistory!");
        Validate.isTrue(page >= 0, "page must not be negative!");

        GuideEntry<?> lastEntry = getLastEntry(false);

        if (lastEntry != null && lastEntry.getIndexedObject().equals(object)) {
            lastEntry.setPage(page);
        } else {
            queue.add(new GuideEntry<>(object, page));
        }
    }

    /**
     * This returns the amount of elements in this {@link GuideHistory}.
     * 
     * @return The size of this {@link GuideHistory}
     */
    public int size() {
        return queue.size();
    }

    /**
     * Retrieves the last page in the {@link SlimefunGuide} that was visited by a {@link Player}.
     * Optionally also rewinds the history back to that entry.
     * 
     * @param remove
     *            Whether to remove the current entry so it moves back to the entry returned.
     * @return The last Guide Entry that was saved to the given Players guide history.
     */
    @Nullable
    private GuideEntry<?> getLastEntry(boolean remove) {
        if (remove && !queue.isEmpty()) {
            queue.removeLast();
        }

        return queue.isEmpty() ? null : queue.getLast();
    }

    /**
     * This method opens the last opened entry to the associated {@link PlayerProfile}
     * of this {@link GuideHistory}.
     * 
     * @param guide
     *            The {@link SlimefunGuideImplementation} to use
     */
    public void openLastEntry(@Nonnull SlimefunGuideImplementation guide) {
        GuideEntry<?> entry = getLastEntry(false);
        open(guide, entry);
    }

    /**
     * This method opens the previous entry to the associated {@link PlayerProfile}.
     * More precisely, it will remove the last entry and open the second-last entry
     * to the {@link Player}.
     * 
     * It can be thought of as a "back" button. Since that is what this is used for.
     * 
     * @param guide
     *            The {@link SlimefunGuideImplementation} to use
     */
    public void goBack(@Nonnull SlimefunGuideImplementation guide) {
        GuideEntry<?> entry = getLastEntry(true);
        open(guide, entry);
    }

    private <T> void open(@Nonnull SlimefunGuideImplementation guide, @Nullable GuideEntry<T> entry) {
        if (entry == null) {
            guide.openMainMenu(profile, mainMenuPage);
        } else if (entry.getIndexedObject() instanceof ItemGroup) {
            guide.openCategory(profile, (ItemGroup) entry.getIndexedObject(), entry.getPage());
        } else if (entry.getIndexedObject() instanceof SlimefunItem) {
            guide.displayItem(profile, (SlimefunItem) entry.getIndexedObject(), false);
        } else if (entry.getIndexedObject() instanceof ItemStack) {
            guide.displayItem(profile, (ItemStack) entry.getIndexedObject(), entry.getPage(), false);
        } else if (entry.getIndexedObject() instanceof String) {
            guide.openSearch(profile, (String) entry.getIndexedObject(), false);
        } else {
            throw new IllegalStateException("Unknown GuideHistory entry: " + entry.getIndexedObject());
        }
    }

}
