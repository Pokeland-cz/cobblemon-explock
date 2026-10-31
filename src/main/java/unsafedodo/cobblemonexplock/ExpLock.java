package unsafedodo.cobblemonexplock;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.pokemon.ExperienceGainedEvent;
import com.cobblemon.mod.common.api.events.pokemon.interaction.ExperienceCandyUseEvent;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.fabricmc.api.ModInitializer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExpLock implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("cobblemon-explock");

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                ExpLockCommands.register(dispatcher));

        // Every exp source (battles, candies, commands, other mods) goes through Pokemon.addExperience,
        // which fires this event. LOWEST so the cap applies after other mods adjust the amount.
        CobblemonEvents.EXPERIENCE_GAINED_EVENT_PRE.subscribe(Priority.LOWEST, ExpLock::onExperienceGained);

        // Refuse candies up front so they aren't consumed on a locked or capped Pokémon.
        CobblemonEvents.EXPERIENCE_CANDY_USE_PRE.subscribe(Priority.NORMAL, ExpLock::onCandyUse);

        LOGGER.info("cobblemon-explock loaded!");
    }

    private static void onExperienceGained(ExperienceGainedEvent.Pre event) {
        int allowed = ExpLockData.allowedExperience(event.getPokemon(), event.getExperience());
        if (allowed <= 0) {
            event.cancel();
        } else if (allowed < event.getExperience()) {
            event.setExperience(allowed);
        }
    }

    private static void onCandyUse(ExperienceCandyUseEvent.Pre event) {
        Pokemon pokemon = event.getPokemon();
        if (ExpLockData.allowedExperience(pokemon, 1) > 0) {
            return;
        }
        event.cancel();

        Component reason = ExpLockData.isLocked(pokemon)
                ? Component.literal(" has its exp locked")
                : Component.literal(" has reached its exp lock level (" + ExpLockData.getLevelCap(pokemon) + ")");
        event.getPlayer().displayClientMessage(
                pokemon.getDisplayName(false).copy().append(reason).withStyle(ChatFormatting.RED), true);
    }
}
