package unsafedodo.cobblemonexplock.mixin;

import com.cobblemon.mod.common.item.interactive.CandyItem;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import unsafedodo.cobblemonexplock.util.IPokemonDataSaver;

@Mixin(CandyItem.class)
public class CandyItemMixin {

    @Inject(method = "canUseOnPokemon", at = @At("HEAD"), cancellable = true, remap = false)
    private void blockCandyOnLockedPokemon(ItemStack stack, Pokemon pokemon, CallbackInfoReturnable<Boolean> cir) {

        if (pokemon instanceof IPokemonDataSaver dataSaver) {
            CompoundTag data = dataSaver.cobblemon_explock$getPersistentData();

            if (data != null) {
                // 1. Check if completely locked
                if (data.getBoolean("explock")) {
                    cir.setReturnValue(false); // Deny candy
                }
                // 2. Check if a level lock is configured
                else if (data.contains("explock_level")) {
                    int maxLevel = data.getInt("explock_level");

                    // If the Pokemon is already at or above the target level, deny the candy
                    if (pokemon.getLevel() >= maxLevel) {
                        cir.setReturnValue(false);
                    }
                }
            }
        }
    }
}