package lab.common.commands;

public final class ValidityChecker {

    private ValidityChecker() {
        throw new UnsupportedOperationException();
    }

    public static CommandResponse checkValidity(BasicCommand command, Object[] args) {
        if (!command.isExecutableInstance()) {
            return new CommandResponse(CommandResult.ERROR, "Execute called onunexecutable instance");
        }
        if (!command.isVaildArgument(args)) {
            return new CommandResponse(CommandResult.ERROR, "Illegal argument");
        }
        return new CommandResponse(CommandResult.SUCCESS);
    }

}
