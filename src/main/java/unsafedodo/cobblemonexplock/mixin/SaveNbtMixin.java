package unsafedodo.cobblemonexplock.mixin;

import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import unsafedodo.cobblemonexplock.util.IPokemonDataSaver;

@Mixin(Pokemon.class)
public abstract class SaveNbtMixin implements IPokemonDataSaver {

    @Shadow private CompoundTag persistentData;

    @Override
    public CompoundTag cobblemon_explock$getPersistentData() {
        return this.persistentData;
    }
}