package lab.common.commands;

import lab.common.users.User;

public final class Exit extends AbstractCommand {

    public Exit() {
        super(true);
    }

    @Override
    public CommandResponse execute(User user, Object... args) {
        return new CommandResponse(CommandResult.END);
    }

    @Override
    public String toString() {
        return "Exit";
    }

    public String getMan() {
        return "exit : завершить программу (без сохранения в файл)";
    }

    @Override
    public boolean isVaildArgument(Object... args) {
        return true;
    }

    @Override
    public Class<?>[] getArgumentClasses() {
        return new Class<?>[0];
    }

}
