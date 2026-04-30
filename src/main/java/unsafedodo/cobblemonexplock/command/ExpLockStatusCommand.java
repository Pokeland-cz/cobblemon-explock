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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import unsafedodo.cobblemonexplock.util.IPokemonDataSaver;

public class ExpLockStatusCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandRegistryAccess, Commands.CommandSelection registrationEnvironment) {
        // Registers the command: /explockstatus <slotNumber>
        dispatcher.register(Commands.literal("explockstatus")
                .requires(Permissions.require("explock.status", 2))
                .then(Commands.argument("slotNumber", IntegerArgumentType.integer(1, 6))
                        .executes(ExpLockStatusCommand::run)));
    }

    private static int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        PlayerPartyStore partyStore = Cobblemon.INSTANCE.getStorage().getParty(player);

        int slot = IntegerArgumentType.getInteger(context, "slotNumber") - 1;
        Pokemon pokemon = partyStore.get(slot);

        if (pokemon != null) {
            Component message = getStatusMessage(pokemon);
            context.getSource().sendSuccess(() -> message, false);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.literal("No Pokemon in slot " + (slot + 1)));
            return 0;
        }
    }

    private static Component getStatusMessage(Pokemon pokemon) {
        if (pokemon instanceof IPokemonDataSaver dataSaver) {
            CompoundTag data = dataSaver.cobblemon_explock$getPersistentData();

            if (data != null) {
                // Check if completely locked
                if (data.getBoolean("explock")) {
                    return Component.literal("Exp for ")
                            .append(pokemon.getDisplayName(false))
                            .append(Component.literal(" is currently "))
                            .append(Component.literal("LOCKED").withStyle(ChatFormatting.RED));
                }
                // Check if level locked
                else if (data.contains("explock_level")) {
                    int level = data.getInt("explock_level");
                    return Component.literal("Exp for ")
                            .append(pokemon.getDisplayName(false))
                            .append(Component.literal(" is currently locked at "))
                            .append(Component.literal("Level " + level).withStyle(ChatFormatting.YELLOW));
                }
            }
        }

        // Default state
        return Component.literal("Exp for ")
                .append(pokemon.getDisplayName(false))
                .append(Component.literal(" is currently "))
                .append(Component.literal("UNLOCKED").withStyle(ChatFormatting.GREEN));
    }
}