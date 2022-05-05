package lab.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;

import lab.common.commands.AbstractCommand;
import lab.common.commands.ValidityChecker;
import lab.common.commands.CommandResponse;
import lab.common.commands.CommandResult;
import lab.common.io.Reader;
import lab.common.util.IOSettableCommandRunner;

public final class ExecuteScript extends AbstractCommand {

    private final IOSettableCommandRunner<String, ?> runner;
    private final Stack<File> bannedFiles = new Stack<>();
    private final Stack<Reader<String>> oldIO = new Stack<>();

    public ExecuteScript() {
        super();
        runner = null;
    }

    public ExecuteScript(IOSettableCommandRunner<String, ?> runner) {
        super(true);
        this.runner = runner;
    }

    @Override
    public CommandResponse execute(Object... args) {
        CommandResponse validy = ValidityChecker.checkValidity(this, args);
        if (validy.getResult() != CommandResult.SUCCESS) {
            return validy;
        }
        File file = (File) args[0];
        if (!bannedFiles.contains(file)) {
            bannedFiles.push(file);
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                oldIO.push(runner.getIO().getReader());
                Reader<String> newReader = createReader(bufferedReader);
                runner.getIO().setReader(newReader);
                runner.run();
                runner.getIO().setReader(oldIO.pop());
            } catch (IOException e) {
                return new CommandResponse(CommandResult.ERROR, "Unable to read file");
            } finally {
                bannedFiles.pop();
            }
        }
        return new CommandResponse(CommandResult.SUCCESS);
    }

    private Reader<String> createReader(BufferedReader bufferedReader) {
        return () -> {
            try {
                return bufferedReader.readLine();
            } catch (IOException e) {
                return null;
            }
        };
    }

    @Override
    public String toString() {
        return "ExecuteScript";
    }

    @Override
    public String getMan() {
        return "execute_script file_name : считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + bannedFiles.hashCode();
        result = prime * result + oldIO.hashCode();
        result = prime * result + runner.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ExecuteScript other = (ExecuteScript) obj;
        if (!bannedFiles.equals(other.bannedFiles)) {
            return false;
        }
        if (!oldIO.equals(other.oldIO)) {
            return false;
        }
        if (runner == null) {
            return other.runner == null;
        }
        return runner.equals(other.runner);
    }

    @Override
    public boolean isVaildArgument(Object... args) {
        return args.length > 0 && args[0] instanceof File;
    }

    @Override
    public Class<?>[] getArgumentClasses() {
        return new Class<?>[] {
                File.class };
    }

}
