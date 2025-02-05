package unsafedodo.cobblemonexplock.mixin;

import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import unsafedodo.cobblemonexplock.util.IPokemonDataSaver;

@Mixin(Pokemon.class)
public abstract class SaveNbtMixin implements IPokemonDataSaver {
    @Unique
    private CompoundTag persistentData;

    @Override
    public CompoundTag cobblemon_explock$getPersistentData(){
        if(this.persistentData == null){
            this.persistentData = new CompoundTag();
        }
        return persistentData;
    }

    @Inject(method = "loadFromNBT", at = @At("TAIL"))
    protected void loadFromNbt(RegistryAccess registryAccess, CompoundTag nbt, CallbackInfoReturnable<Pokemon> cir){
        if(nbt.contains("explock")){
            persistentData = nbt.getCompound("explock");
        }
    }


    @Inject(method = "saveToNBT", at = @At("TAIL"))
    protected void saveToNbt(RegistryAccess registryAccess, CompoundTag nbt, CallbackInfoReturnable<CompoundTag> cir){
        if(persistentData != null){
            nbt.put("explock", persistentData);
        }
    }
}
