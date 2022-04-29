package lab.commands;

import lab.common.commands.AbstractCommand;
import lab.common.commands.CommandResponse;
import lab.common.commands.CommandResult;
import lab.common.commands.SaveToFile;
import lab.common.users.User;

public class SaveAndExit extends AbstractCommand {

    private final SaveToFile saveCommand;

    public SaveAndExit() {
        super();
        this.saveCommand = null;
    }

    public SaveAndExit(SaveToFile saveCommand) {
        super(true);
        this.saveCommand = saveCommand;
    }

    @Override
    public CommandResponse execute(User user, Object... args) {
        saveCommand.execute(user, args);
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
