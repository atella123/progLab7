package lab.parsers;

import java.util.Objects;

import lab.common.commands.CommandResponse;
import lab.common.commands.CommandResult;
import lab.common.commands.datacommands.RegisterCommandFlags;
import lab.common.commands.datacommands.RegisterUserConnection;
import lab.common.commands.datacommands.User;
import lab.common.io.IOManager;
import lab.common.io.Writer;
import lab.common.util.DataCommandExecuteRequest;

public final class UserParser {

    private static final int MAX_USERNAME_LEN = 30;
    private static final int MAX_PASSWORD_LEN = 30;

    private UserParser() {
        throw new UnsupportedOperationException();
    }

    public static User readUser(Writer<String> writer, IOManager<String, CommandResponse> clientIO,
                                IOManager<CommandResponse, DataCommandExecuteRequest> toServerIO) {

        CommandResponse response = null;
        User user;
        IOManager<String, String> stringIO = new IOManager<>(clientIO::read, writer);

        do {

            if (Objects.nonNull(response) && response.hasPrintableResult()) {
                writer.write(response.getMessage());
            }

            writer.write("Please enter L(ogin) to continue singing in or R(egister) to continue registration");
            String loginOrRegister = DataReader.readValidString(new IOManager<>(clientIO::read, writer),
                    (x -> x.matches("([Ll](ogin)?)|([Rr](egister)?)")),
                    "Please enter L(ogin) to continue singing in or R(egister) to continue registration");

            writer.write("Enter username:");
            String username = DataReader.readValidString(stringIO, x -> x.length() <= MAX_USERNAME_LEN,
                    String.format("Username length must be less than or equal to %d", MAX_USERNAME_LEN));
            writer.write("Enter password:");
            String password = DataReader.readValidString(stringIO, x -> x.length() <= MAX_PASSWORD_LEN,
                    String.format("Password length must be less than or equal to %d", MAX_PASSWORD_LEN));

            user = new User(username, password);
            DataCommandExecuteRequest request = new DataCommandExecuteRequest(user, RegisterUserConnection.class,
                    RegisterCommandFlags.getFlag(loginOrRegister));

            toServerIO.write(request);
            response = toServerIO.read();

        } while (response.getResult() != CommandResult.SUCCESS);
        writer.write("Logged in successfully");
        return user;
    }

}
