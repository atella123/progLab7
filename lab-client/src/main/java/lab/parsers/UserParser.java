package lab.parsers;

import java.util.Objects;

import lab.common.commands.CommandResponse;
import lab.common.commands.CommandResult;
import lab.common.commands.datacommands.RegisterUserConnection;
import lab.common.commands.datacommands.User;
import lab.common.io.IOManager;
import lab.common.io.Writter;
import lab.common.util.DataCommandExecuteRequest;

public final class UserParser {

    private static final int MAX_USERNAME_LEN = 30;
    private static final int MAX_PASSWORD_LEN = 30;

    private UserParser() {
        throw new UnsupportedOperationException();
    }

    public static User readUser(Writter<String> writter, IOManager<String, CommandResponse> clientIO,
            IOManager<CommandResponse, DataCommandExecuteRequest> toServerIO) {

        CommandResponse response = null;
        User user;
        IOManager<String, String> stringIO = new IOManager<>(clientIO::readLine, writter);

        do {

            if (Objects.nonNull(response) && response.hasPrintableResult()) {
                writter.write(response.getMessage());
            }

            writter.write("Please enter L(ogin) to continue singing in or R(egister) to continue registration");
            String loginOrRegister = DataReader.readValidString(new IOManager<>(clientIO::readLine, writter),
                    (x -> x.matches("([Ll](ogin)?)|([Rr](egister)?)")),
                    "Please enter [Ll](ogin) to continue singing in or R(egister) to continue registration");
            loginOrRegister = loginOrRegister.substring(0, 1).toUpperCase();

            writter.write("Enter username:");
            String username = DataReader.readValidString(stringIO, x -> x.length() <= MAX_USERNAME_LEN,
                    String.format("Username lenght must be less than or equal to %d", MAX_USERNAME_LEN));
            writter.write("Enter password:");
            String password = DataReader.readValidString(stringIO, x -> x.length() <= MAX_PASSWORD_LEN,
                    String.format("Password lenght must be less than or equal to %d", MAX_PASSWORD_LEN));

            user = new User(username, password);
            DataCommandExecuteRequest request = new DataCommandExecuteRequest(user, RegisterUserConnection.class,
                    loginOrRegister);

            toServerIO.write(request);
            response = toServerIO.readLine();

        } while (response.getResult() != CommandResult.SUCCESS);
        writter.write("Logged in successfully");
        return user;
    }

}
