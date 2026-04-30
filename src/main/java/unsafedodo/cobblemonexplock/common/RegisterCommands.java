package unsafedodo.cobblemonexplock.common;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import unsafedodo.cobblemonexplock.command.ExpLockStatusCommand;
import unsafedodo.cobblemonexplock.command.ExpLockToggleCommand;

public class RegisterCommands {
    public static void register(){
        CommandRegistrationCallback.EVENT.register(ExpLockToggleCommand::register);
        CommandRegistrationCallback.EVENT.register(ExpLockStatusCommand::register);
    }
}