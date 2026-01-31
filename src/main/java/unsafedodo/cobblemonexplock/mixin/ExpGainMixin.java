package unsafedodo.cobblemonexplock.mixin;

import com.cobblemon.mod.common.api.pokemon.experience.StandardExperienceCalculator;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import unsafedodo.cobblemonexplock.util.IPokemonDataSaver;

@Mixin(StandardExperienceCalculator.class)
public class ExpGainMixin {

    /**
     * Intercepts the experience calculation at the very beginning.
     * If the Pokemon has "explock" set to true in its persistent data, returns 0.
     */
    @Inject(method = "calculate", at = @At("HEAD"), cancellable = true, remap = false)
    private void injectedExpGainLock(BattlePokemon battlePokemon, BattlePokemon opponentPokemon, double participationMultiplier, CallbackInfoReturnable<Integer> cir) {

        Pokemon pokemon = battlePokemon.getOriginalPokemon();

        if (pokemon instanceof IPokemonDataSaver dataSaver) {
            CompoundTag data = dataSaver.cobblemon_explock$getPersistentData();

            if (data != null && data.getBoolean("explock")) {
                cir.setReturnValue(0);
            }
        }
    }
}