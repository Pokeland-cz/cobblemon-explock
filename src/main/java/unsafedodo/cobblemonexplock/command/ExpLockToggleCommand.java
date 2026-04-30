package unsafedodo.cobblemonexplock.command;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import unsafedodo.cobblemonexplock.util.ExpData;
import unsafedodo.cobblemonexplock.util.IPokemonDataSaver;

public class ExpLockToggleCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandRegistryAccess, Commands.CommandSelection registrationEnvironment) {
        dispatcher.register(Commands.literal("explock")
                .requires(Permissions.require("explock.toggle", 2))
                .then(Commands.argument("slotNumber", IntegerArgumentType.integer(1, 6))
                        .executes(ExpLockToggleCommand::run)
                        .then(Commands.argument("level", IntegerArgumentType.integer(1, 100))
                                .executes(ExpLockToggleCommand::runWithLevel))));
    }

    // Handles: /explock <slot>
    private static int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();

        PlayerPartyStore partyStore = Cobblemon.INSTANCE.getStorage().getParty(player);

        int slot = IntegerArgumentType.getInteger(context, "slotNumber") - 1;
        Pokemon pokemon = partyStore.get(slot);

        if (pokemon != null) {
            boolean newState = false;

            // Route through ExpData
            if (pokemon instanceof IPokemonDataSaver dataSaver) {
                newState = ExpData.setExpState(dataSaver);
            }

            Component message = Component.literal("Exp lock for ")
                    .append(pokemon.getDisplayName(false))
                    .append(Component.literal(" set to " + newState))
                    .withStyle(newState ? ChatFormatting.GREEN : ChatFormatting.RED);

            context.getSource().sendSuccess(() -> message, false);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.literal("No Pokemon in slot " + (slot + 1)));
            return 0;
        }
    }

    // Handles: /explock <slot> <level>
    private static int runWithLevel(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();

        PlayerPartyStore partyStore = Cobblemon.INSTANCE.getStorage().getParty(player);

        int slot = IntegerArgumentType.getInteger(context, "slotNumber") - 1;
        int level = IntegerArgumentType.getInteger(context, "level");
        Pokemon pokemon = partyStore.get(slot);

        if (pokemon != null) {

            // Route through ExpData
            if (pokemon instanceof IPokemonDataSaver dataSaver) {
                ExpData.setExpLevel(dataSaver, level);
            }

            Component message = Component.literal("Exp lock for ")
                    .append(pokemon.getDisplayName(false))
                    .append(Component.literal(" set to level " + level))
                    .withStyle(ChatFormatting.GREEN);

            context.getSource().sendSuccess(() -> message, false);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.literal("No Pokemon in slot " + (slot + 1)));
            return 0;
        }
    }
}