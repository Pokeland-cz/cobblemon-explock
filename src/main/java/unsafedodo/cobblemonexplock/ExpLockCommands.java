package unsafedodo.cobblemonexplock;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class ExpLockCommands {
    private static final String SLOT = "slotNumber";
    private static final String LEVEL = "level";

    private static final DynamicCommandExceptionType EMPTY_SLOT =
            new DynamicCommandExceptionType(slot -> Component.literal("No Pokemon in slot " + slot));

    private ExpLockCommands() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        int maxLevel = Cobblemon.INSTANCE.getConfig().getMaxPokemonLevel();

        // /explock <slot> [level]
        dispatcher.register(Commands.literal("explock")
                .requires(Permissions.require("explock.toggle", 2))
                .then(Commands.argument(SLOT, IntegerArgumentType.integer(1, 6))
                        .executes(ExpLockCommands::toggle)
                        .then(Commands.argument(LEVEL, IntegerArgumentType.integer(1, maxLevel))
                                .executes(ExpLockCommands::setLevel))));

        // /explockstatus <slot>
        dispatcher.register(Commands.literal("explockstatus")
                .requires(Permissions.require("explock.status", 2))
                .then(Commands.argument(SLOT, IntegerArgumentType.integer(1, 6))
                        .executes(ExpLockCommands::status)));
    }

    private static int toggle(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Pokemon pokemon = getPokemon(ctx);
        boolean locked = ExpLockData.toggleLock(pokemon);
        reply(ctx, prefix(pokemon, " is now ").append(state(locked ? "LOCKED" : "UNLOCKED", locked ? ChatFormatting.RED : ChatFormatting.GREEN)));
        return Command.SINGLE_SUCCESS;
    }

    private static int setLevel(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Pokemon pokemon = getPokemon(ctx);
        int level = IntegerArgumentType.getInteger(ctx, LEVEL);
        ExpLockData.setLevelCap(pokemon, level);
        reply(ctx, prefix(pokemon, " is now locked at ").append(state("Level " + level, ChatFormatting.YELLOW)));
        return Command.SINGLE_SUCCESS;
    }

    private static int status(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Pokemon pokemon = getPokemon(ctx);
        int cap = ExpLockData.getLevelCap(pokemon);
        MutableComponent message;
        if (ExpLockData.isLocked(pokemon)) {
            message = prefix(pokemon, " is currently ").append(state("LOCKED", ChatFormatting.RED));
        } else if (cap != ExpLockData.NO_CAP) {
            message = prefix(pokemon, " is currently locked at ").append(state("Level " + cap, ChatFormatting.YELLOW));
        } else {
            message = prefix(pokemon, " is currently ").append(state("UNLOCKED", ChatFormatting.GREEN));
        }
        reply(ctx, message);
        return Command.SINGLE_SUCCESS;
    }

    private static Pokemon getPokemon(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        int slot = IntegerArgumentType.getInteger(ctx, SLOT);
        Pokemon pokemon = Cobblemon.INSTANCE.getStorage()
                .getParty(ctx.getSource().getPlayerOrException())
                .get(slot - 1);
        if (pokemon == null) {
            throw EMPTY_SLOT.create(slot);
        }
        return pokemon;
    }

    private static MutableComponent prefix(Pokemon pokemon, String suffix) {
        return Component.literal("Exp for ").append(pokemon.getDisplayName(false)).append(suffix);
    }

    private static MutableComponent state(String text, ChatFormatting color) {
        return Component.literal(text).withStyle(color);
    }

    private static void reply(CommandContext<CommandSourceStack> ctx, Component message) {
        ctx.getSource().sendSuccess(() -> message, false);
    }
}
