package lab.common.commands;

public abstract class AbstractCommand implements Command {

    protected final boolean isExecutableInstance;

    public AbstractCommand() {
        isExecutableInstance = false;
    }

    protected AbstractCommand(boolean isExecutableInstance) {
        this.isExecutableInstance = isExecutableInstance;
    }

    @Override
    public String toString() {
        return "Command";
    }

    public boolean isExecutableInstance() {
        return isExecutableInstance;
    }
}
