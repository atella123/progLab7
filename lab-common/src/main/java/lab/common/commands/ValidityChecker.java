package lab.common.commands;

public final class ValidityChecker {

    private ValidityChecker() {
        throw new UnsupportedOperationException();
    }

    public static CommandResponse checkValidity(BasicCommand command, Object[] args) {
        if (!command.isExecutableInstance()) {
            return new CommandResponse(CommandResult.ERROR, "Execute called on non executable instance");
        }
        if (!command.isValidArgument(args)) {
            return new CommandResponse(CommandResult.ERROR, "Illegal argument");
        }
        return new CommandResponse(CommandResult.SUCCESS);
    }

}
