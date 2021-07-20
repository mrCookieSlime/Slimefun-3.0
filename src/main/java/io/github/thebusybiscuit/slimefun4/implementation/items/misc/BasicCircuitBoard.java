package io.github.thebusybiscuit.slimefun4.implementation.items.misc;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.entity.IronGolem;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.settings.IntRangeSetting;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotPlaceable;
import io.github.thebusybiscuit.slimefun4.core.attributes.RandomMobDrop;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;

/**
 * The {@link BasicCircuitBoard} is a basic crafting component which can be
 * obtained by killing an {@link IronGolem}.
 * 
 * @author TheBusyBiscuit
 * @author dniym
 *
 */
public class BasicCircuitBoard extends SimpleSlimefunItem<ItemUseHandler> implements NotPlaceable, RandomMobDrop {

    private final ItemSetting<Boolean> dropSetting = new ItemSetting<>(this, "drop-from-golems", true);
    private final ItemSetting<Integer> chance = new IntRangeSetting(this, "golem-drop-chance", 0, 75, 100);

    @ParametersAreNonnullByDefault
    public BasicCircuitBoard(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        addItemSetting(dropSetting);
        addItemSetting(chance);
    }

    @Override
    public int getMobDropChance() {
        return chance.getValue();
    }

    public boolean isDroppedFromGolems() {
        return dropSetting.getValue();
    }

    @Override
    public ItemUseHandler getItemHandler() {
        return PlayerRightClickEvent::cancel;
    }

}