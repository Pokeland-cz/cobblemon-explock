package unsafedodo.cobblemonexplock;

import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.nbt.CompoundTag;

/**
 * Reads and writes the exp lock state stored in a Pokémon's persistent data.
 * Key names are unchanged from earlier versions so existing locks keep working.
 */
public final class ExpLockData {
    private static final String LOCKED_KEY = "explock";
    private static final String LEVEL_KEY = "explock_level";

    /** Returned by {@link #getLevelCap} when no level cap is set. */
    public static final int NO_CAP = -1;

    private ExpLockData() {}

    public static boolean isLocked(Pokemon pokemon) {
        return pokemon.getPersistentData().getBoolean(LOCKED_KEY);
    }

    public static int getLevelCap(Pokemon pokemon) {
        CompoundTag data = pokemon.getPersistentData();
        return data.contains(LEVEL_KEY) ? data.getInt(LEVEL_KEY) : NO_CAP;
    }

    /**
     * Returns how much of {@code requested} exp the Pokémon may receive:
     * 0 when fully locked, otherwise at most enough to reach its level cap.
     */
    public static int allowedExperience(Pokemon pokemon, int requested) {
        if (isLocked(pokemon)) {
            return 0;
        }
        int cap = getLevelCap(pokemon);
        if (cap == NO_CAP) {
            return requested;
        }
        return Math.min(requested, pokemon.getExperienceToLevel(cap));
    }

    /**
     * Unlocks the Pokémon if it has any lock (full or level cap); otherwise fully locks it.
     *
     * @return true if the Pokémon is now locked
     */
    public static boolean toggleLock(Pokemon pokemon) {
        CompoundTag data = pokemon.getPersistentData();
        boolean lock = !isLocked(pokemon) && !data.contains(LEVEL_KEY);
        data.remove(LEVEL_KEY);
        if (lock) {
            data.putBoolean(LOCKED_KEY, true);
        } else {
            data.remove(LOCKED_KEY);
        }
        markDirty(pokemon);
        return lock;
    }

    public static void setLevelCap(Pokemon pokemon, int level) {
        CompoundTag data = pokemon.getPersistentData();
        data.remove(LOCKED_KEY);
        data.putInt(LEVEL_KEY, level);
        markDirty(pokemon);
    }

    /** Persistent data edits aren't tracked by Cobblemon, so tell the store to save the Pokémon. */
    private static void markDirty(Pokemon pokemon) {
        pokemon.onChange(null);
    }
}
