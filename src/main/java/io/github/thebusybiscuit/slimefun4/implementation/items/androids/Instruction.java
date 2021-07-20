package io.github.thebusybiscuit.slimefun4.implementation.items.androids;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Animals;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.utils.HeadTexture;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;

import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;

/**
 * This enum holds every {@link Instruction} for the {@link ProgrammableAndroid}
 * added by Slimefun itself.
 * 
 * @author TheBusyBiscuit
 *
 */
public enum Instruction {

    /**
     * This {@link Instruction} is the starting point of a {@link Script}.
     */
    START(AndroidType.NONE, HeadTexture.SCRIPT_START),

    /**
     * This {@link Instruction} is the end token of a {@link Script}.
     * Once this {@link Instruction} is reached, the {@link Script} will start again.
     */
    REPEAT(AndroidType.NONE, HeadTexture.SCRIPT_REPEAT),

    /**
     * This {@link Instruction} will make the {@link ProgrammableAndroid} wait
     * for one Slimefun tick.
     */
    WAIT(AndroidType.NONE, HeadTexture.SCRIPT_WAIT),

    /**
     * This will make the {@link ProgrammableAndroid} go forward.
     */
    GO_FORWARD(AndroidType.NON_FIGHTER, HeadTexture.SCRIPT_FORWARD, (android, b, inv, face) -> {
        Block target = b.getRelative(face);
        android.move(b, face, target);
    }),

    /**
     * This will make the {@link ProgrammableAndroid} go up.
     */
    GO_UP(AndroidType.NON_FIGHTER, HeadTexture.SCRIPT_UP, (android, b, inv, face) -> {
        Block target = b.getRelative(BlockFace.UP);
        android.move(b, face, target);
    }),

    /**
     * This will make the {@link ProgrammableAndroid} go down.
     */
    GO_DOWN(AndroidType.NON_FIGHTER, HeadTexture.SCRIPT_DOWN, (android, b, inv, face) -> {
        Block target = b.getRelative(BlockFace.DOWN);
        android.move(b, face, target);
    }),

    /**
     * This will make the {@link ProgrammableAndroid} rotate to the left side.
     */
    TURN_LEFT(AndroidType.NONE, HeadTexture.SCRIPT_LEFT, (android, b, inv, face) -> {
        int mod = -1;
        android.rotate(b, face, mod);
    }),

    /**
     * This will make the {@link ProgrammableAndroid} rotate to the right side.
     */
    TURN_RIGHT(AndroidType.NONE, HeadTexture.SCRIPT_RIGHT, (android, b, inv, face) -> {
        int mod = 1;
        android.rotate(b, face, mod);
    }),

    /**
     * This will make a {@link MinerAndroid} dig the {@link Block} above.
     */
    DIG_UP(AndroidType.MINER, HeadTexture.SCRIPT_DIG_UP, (android, b, inv, face) -> {
        Block target = b.getRelative(BlockFace.UP);
        android.dig(b, inv, target);
    }),

    /**
     * This will make a {@link MinerAndroid} dig the {@link Block} ahead.
     */
    DIG_FORWARD(AndroidType.MINER, HeadTexture.SCRIPT_DIG_FORWARD, (android, b, inv, face) -> {
        Block target = b.getRelative(face);
        android.dig(b, inv, target);
    }),

    /**
     * This will make a {@link MinerAndroid} dig the {@link Block} below.
     */
    DIG_DOWN(AndroidType.MINER, HeadTexture.SCRIPT_DIG_DOWN, (android, b, inv, face) -> {
        Block target = b.getRelative(BlockFace.DOWN);
        android.dig(b, inv, target);
    }),

    /**
     * This will make a {@link MinerAndroid} dig the {@link Block} above
     * and then move itself to that new {@link Location}.
     */
    MOVE_AND_DIG_UP(AndroidType.MINER, HeadTexture.SCRIPT_DIG_UP, (android, b, inv, face) -> {
        Block target = b.getRelative(BlockFace.UP);
        android.moveAndDig(b, inv, face, target);
    }),

    /**
     * This will make a {@link MinerAndroid} dig the {@link Block} ahead
     * and then move itself to that new {@link Location}.
     */
    MOVE_AND_DIG_FORWARD(AndroidType.MINER, HeadTexture.SCRIPT_DIG_FORWARD, (android, b, inv, face) -> {
        Block target = b.getRelative(face);
        android.moveAndDig(b, inv, face, target);
    }),

    /**
     * This will make a {@link MinerAndroid} dig the {@link Block} below
     * and then move itself to that new {@link Location}.
     */
    MOVE_AND_DIG_DOWN(AndroidType.MINER, HeadTexture.SCRIPT_DIG_DOWN, (android, b, inv, face) -> {
        Block target = b.getRelative(BlockFace.DOWN);
        android.moveAndDig(b, inv, face, target);
    }),

    /**
     * This will make a {@link ButcherAndroid} attack any {@link LivingEntity}
     * ahead of them.
     */
    ATTACK_MOBS_ANIMALS(AndroidType.FIGHTER, HeadTexture.SCRIPT_ATTACK, (android, b, inv, face) -> {
        Predicate<LivingEntity> predicate = e -> true;
        android.attack(b, face, predicate);
    }),

    /**
     * This will make a {@link ButcherAndroid} attack any {@link Monster}
     * ahead of them.
     */
    ATTACK_MOBS(AndroidType.FIGHTER, HeadTexture.SCRIPT_ATTACK, (android, b, inv, face) -> {
        Predicate<LivingEntity> predicate = Monster.class::isInstance;
        android.attack(b, face, predicate);
    }),

    /**
     * This will make a {@link ButcherAndroid} attack any {@link Animals Animal}
     * ahead of them.
     */
    ATTACK_ANIMALS(AndroidType.FIGHTER, HeadTexture.SCRIPT_ATTACK, (android, b, inv, face) -> {
        Predicate<LivingEntity> predicate = Animals.class::isInstance;
        android.attack(b, face, predicate);
    }),

    /**
     * This will make a {@link ButcherAndroid} attack any <strong>adult</strong>
     * {@link Animals Animal} ahead of them.
     */
    ATTACK_ANIMALS_ADULT(AndroidType.FIGHTER, HeadTexture.SCRIPT_ATTACK, (android, b, inv, face) -> {
        Predicate<LivingEntity> predicate = e -> e instanceof Animals && ((Ageable) e).isAdult();
        android.attack(b, face, predicate);
    }),

    /**
     * This will make a {@link WoodcutterAndroid} chop down the tree in front of them.
     */
    CHOP_TREE(AndroidType.WOODCUTTER, HeadTexture.SCRIPT_CHOP_TREE),

    /**
     * This {@link Instruction} makes a {@link FisherAndroid} try to catch fish from
     * the water below.
     */
    CATCH_FISH(AndroidType.FISHERMAN, HeadTexture.SCRIPT_FISH, (android, b, inv, face) -> android.fish(b, inv)),

    /**
     * This {@link Instruction} will make a {@link FarmerAndroid} try to harvest
     * the {@link Block} in front of them.
     */
    FARM_FORWARD(AndroidType.FARMER, HeadTexture.SCRIPT_FARM_FORWARD, (android, b, inv, face) -> {
        Block target = b.getRelative(face);
        android.farm(b, inv, target, false);
    }),

    /**
     * This {@link Instruction} will make a {@link FarmerAndroid} try to harvest
     * the {@link Block} below.
     */
    FARM_DOWN(AndroidType.FARMER, HeadTexture.SCRIPT_FARM_DOWN, (android, b, inv, face) -> {
        Block target = b.getRelative(BlockFace.DOWN);
        android.farm(b, inv, target, false);
    }),

    /**
     * This {@link Instruction} will make a {@link FarmerAndroid} try to harvest
     * the {@link Block} in front of them.
     * 
     * <strong>This includes plants from ExoticGarden.</strong>
     */
    FARM_EXOTIC_FORWARD(AndroidType.ADVANCED_FARMER, HeadTexture.SCRIPT_FARM_FORWARD, (android, b, inv, face) -> {
        Block target = b.getRelative(face);
        android.farm(b, inv, target, true);
    }),

    /**
     * This {@link Instruction} will make a {@link FarmerAndroid} try to harvest
     * the {@link Block} below.
     * 
     * <strong>This includes plants from ExoticGarden.</strong>
     */
    FARM_EXOTIC_DOWN(AndroidType.ADVANCED_FARMER, HeadTexture.SCRIPT_FARM_DOWN, (android, b, inv, face) -> {
        Block target = b.getRelative(BlockFace.DOWN);
        android.farm(b, inv, target, true);
    }),

    /**
     * This {@link Instruction} will force the {@link ProgrammableAndroid} to push their
     * items into an {@link AndroidInterface} ahead of them.
     */
    INTERFACE_ITEMS(AndroidType.NONE, HeadTexture.SCRIPT_PUSH_ITEMS, (android, b, inv, face) -> {
        Block target = b.getRelative(face);
        android.depositItems(inv, target);
    }),

    /**
     * This {@link Instruction} will force the {@link ProgrammableAndroid} to pull
     * fuel from an {@link AndroidInterface} ahead of them.
     */
    INTERFACE_FUEL(AndroidType.NONE, HeadTexture.SCRIPT_PULL_FUEL, (android, b, inv, face) -> {
        Block target = b.getRelative(face);
        android.refuel(inv, target);
    });

    private static final Map<String, Instruction> nameLookup = new HashMap<>();
    public static final Instruction[] valuesCache = values();

    static {
        for (Instruction instruction : valuesCache) {
            nameLookup.put(instruction.name(), instruction);
        }
    }

    private final ItemStack item;
    private final AndroidType type;
    private final AndroidAction method;

    @ParametersAreNonnullByDefault
    Instruction(AndroidType type, HeadTexture head, @Nullable AndroidAction method) {
        this.type = type;
        this.item = SlimefunUtils.getCustomHead(head.getTexture());
        this.method = method;
    }

    @ParametersAreNonnullByDefault
    Instruction(AndroidType type, HeadTexture head) {
        this(type, head, null);
    }

    @Nonnull
    public ItemStack getItem() {
        return item;
    }

    @Nonnull
    public AndroidType getRequiredType() {
        return type;
    }

    @ParametersAreNonnullByDefault
    public void execute(ProgrammableAndroid android, Block b, BlockMenu inventory, BlockFace face) {
        Validate.notNull(method, "Instruction '" + name() + "' must be executed manually!");
        method.perform(android, b, inventory, face);
    }

    /**
     * Get a value from the cache map rather than calling {@link Enum#valueOf(Class, String)}.
     * This is 25-40% quicker than the standard {@link Enum#valueOf(Class, String)} depending on
     * your Java version. It also means that you can avoid an IllegalArgumentException which let's
     * face it is always good.
     *
     * @param value
     *            The value which you would like to look up.
     * 
     * @return The {@link Instruction} or null if it does not exist.
     */
    @Nullable
    public static Instruction getInstruction(@Nonnull String value) {
        Validate.notNull(value, "An Instruction cannot be null!");
        return nameLookup.get(value);
    }
}
