package lab.common.commands.datacommands;

import lab.common.commands.CommandResponse;
import lab.common.commands.CommandResult;
import lab.common.util.UserManager;

public final class RegisterUserConnection extends AbstractDataCommand {

    private final UserManager userManager;

    public RegisterUserConnection() {
        super();
        this.userManager = null;
    }

    public RegisterUserConnection(UserManager userManager) {
        super(true);
        this.userManager = userManager;
    }

    @Override
    public CommandResponse execute(User user, Object... args) {
        if (!isExecutableInstance) {
            return new CommandResponse(CommandResult.ERROR, "Execute called on unexecutable instance");
        }
        if (!isVaildArgument(args)) {
            return new CommandResponse(CommandResult.ERROR, "Illegal argument");
        }
        if (args[0] == RegisterCommandFlags.REGISTER) {
            if (userManager.isUsernameTaken(user)) {
                return new CommandResponse(CommandResult.ERROR, "Username is already taken");
            }
            userManager.addUser(user);
        } else if (!userManager.isRegisteredUser(user)) {
            return new CommandResponse(CommandResult.ERROR, "Invalid username or password");
        }
        return new CommandResponse(CommandResult.SUCCESS);
    }

    @Override
    public boolean isVaildArgument(Object... args) {
        return args.length > 0 && args[0] instanceof RegisterCommandFlags;
    }

    @Override
    public Class<?>[] getArgumentClasses() {
        return new Class[] {
                RegisterCommandFlags.class };
    }

    @Override
    public String getMan() {
        return "register_user : вывести последние 11 команд (без их аргументов)";
    }

}
