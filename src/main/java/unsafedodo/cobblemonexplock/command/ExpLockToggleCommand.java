package unsafedodo.cobblemonexplock.command;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.NoPokemonStoreException;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import unsafedodo.cobblemonexplock.util.ExpData;
import unsafedodo.cobblemonexplock.util.IPokemonDataSaver;

public class ExpLockToggleCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandRegistryAccess, Commands.CommandSelection registrationEnvironment) {
        dispatcher.register(Commands.literal("explock")
                .then(Commands.argument("slotNumber", IntegerArgumentType.integer(1,6))
                        .requires(Permissions.require("explock.toggle", 0))
                        .executes(ExpLockToggleCommand::run)));
    }

    private static int run(CommandContext<CommandSourceStack> context) {
        try{
            PlayerPartyStore partyStore = Cobblemon.INSTANCE.getStorage().getParty(context.getSource().getPlayer());
            int slot = (IntegerArgumentType.getInteger(context, "slotNumber")-1);
            Pokemon pokemon = partyStore.get(slot);
            if(pokemon != null){
                boolean state = ExpData.setExpState((IPokemonDataSaver) pokemon);
                context.getSource().sendSuccess(() -> Component.literal("Exp lock state for "+pokemon.getDisplayName()+" changed to "+state).withStyle(ChatFormatting.GREEN), false);
            } else {
                context.getSource().sendSuccess(() -> Component.literal("Invalid slot").withStyle(ChatFormatting.RED), false);
                return -1;
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return 0;
    }
}
