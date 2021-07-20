package io.github.thebusybiscuit.slimefun4.implementation.items.androids;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.block.Dispenser;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockDispenseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.handlers.VanillaInventoryDropHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;

/**
 * The {@link AndroidInterface} are inventories used to interact with a {@link ProgrammableAndroid}.
 * There are two variants of interfaces, fuel and items.
 * 
 * @author TheBusyBiscuit
 * 
 * @see ProgrammableAndroid
 *
 */
public class AndroidInterface extends SimpleSlimefunItem<BlockDispenseHandler> {

    @ParametersAreNonnullByDefault
    public AndroidInterface(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        addItemHandler(new VanillaInventoryDropHandler<>(Dispenser.class));
    }

    @Override
    public BlockDispenseHandler getItemHandler() {
        return (e, d, block, machine) -> e.setCancelled(true);
    }

}
