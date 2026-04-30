package unsafedodo.cobblemonexplock.util;

import net.minecraft.nbt.CompoundTag;

public class ExpData {

    public static boolean setExpState(IPokemonDataSaver pokemon){
        CompoundTag nbt = pokemon.cobblemon_explock$getPersistentData();
        boolean state = nbt.getBoolean("explock");
        state = !state;

        nbt.putBoolean("explock", state);

        // Remove the level lock if the player is toggling the standard lock
        // to prevent unexpected overlaps.
        if (nbt.contains("explock_level")) {
            nbt.remove("explock_level");
        }

        return state;
    }

    // Optional: You can also move the set level logic here to keep your NBT handling in one utility class!
    public static void setExpLevel(IPokemonDataSaver pokemon, int level) {
        CompoundTag nbt = pokemon.cobblemon_explock$getPersistentData();
        nbt.putBoolean("explock", false); // Disable hard lock
        nbt.putInt("explock_level", level);
    }
}