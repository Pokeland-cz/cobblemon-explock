package unsafedodo.cobblemonexplock.util;

import net.minecraft.nbt.CompoundTag;

public class ExpData {

    public static boolean setExpState(IPokemonDataSaver pokemon){
        CompoundTag nbt = pokemon.cobblemon_explock$getPersistentData();
        boolean state = nbt.getBoolean("explock");
        state = !state;

        nbt.putBoolean("explock", state);
        return state;
    }
}
