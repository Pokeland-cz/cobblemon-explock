package unsafedodo.cobblemonexplock.mixin;

import com.cobblemon.mod.common.api.pokemon.experience.StandardExperienceCalculator;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.pokemon.Pokemon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import unsafedodo.cobblemonexplock.util.IPokemonDataSaver;

@SuppressWarnings("ALL")
@Mixin(StandardExperienceCalculator.class)
public class ExpGainMixin {
	@ModifyVariable(method = "calculate", at = @At("STORE"), name = "term4", remap = false)
	private double injectedExpGainLock(double term4, BattlePokemon battlePokemon) {
		Pokemon pokemon = battlePokemon.getOriginalPokemon();
		boolean state = ((IPokemonDataSaver) pokemon).cobblemon_explock$getPersistentData().getBoolean("explock");
		if(!state)
			return term4;
		else
			return 0.0;
	}
}
