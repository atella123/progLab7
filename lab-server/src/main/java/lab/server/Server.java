package lab.server;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lab.common.commands.Command;
import lab.common.commands.CommandResponse;
import lab.common.commands.Exit;
import lab.common.commands.datacommands.Add;
import lab.common.commands.datacommands.AddIfMax;
import lab.common.commands.datacommands.Clear;
import lab.common.commands.datacommands.DataCommand;
import lab.common.commands.datacommands.FilterLessThanNationality;
import lab.common.commands.datacommands.GroupCountingByPassportID;
import lab.common.commands.datacommands.Info;
import lab.common.commands.datacommands.MinByCoordinates;
import lab.common.commands.datacommands.RegisterUserConnection;
import lab.common.commands.datacommands.RemoveByID;
import lab.common.commands.datacommands.RemoveGreater;
import lab.common.commands.datacommands.Show;
import lab.common.commands.datacommands.Update;
import lab.common.io.IOManager;
import lab.common.util.ArgumentParser;
import lab.common.util.CommandRunner;
import lab.data.PersonDBManager;
import lab.io.DatagramChannelIOManager;
import lab.util.ServerCommandRunner;
import lab.util.ServerToClientCommandRunner;

public final class Server {

    private static final Logger LOGGER = LogManager.getLogger(lab.server.Server.class);

    private Server() {
        throw new UnsupportedOperationException("This is an utility class and can not be instantiated");
    }

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        int port = getServerPort(args);
        if (port == -1) {
            scanner.close();
            return;
        }

        PersonDBManager manager = createDBManager("jdbc:postgresql://localhost:5432/lab7",
                "postgres", "postgres", "MD2");
        DatagramChannelIOManager clientIOManager = createDatagramChannelIOManager(port);

        if (Objects.isNull(manager) || Objects.isNull(clientIOManager)) {
            scanner.close();
            return;
        }

        ServerToClientCommandRunner serverToClientCommandRunner;
        Map<Class<? extends DataCommand>, DataCommand> clientCommandManager = createClientCommandsMap(manager);

        serverToClientCommandRunner = new ServerToClientCommandRunner(clientCommandManager, clientIOManager);

        Map<String, Command> serverCommandsMap = createServerCommandsMap();
        CommandRunner<String> serverCommandRunner = new ServerCommandRunner(new ArgumentParser<>(),
                serverCommandsMap, createDefaultIOManager(scanner));
        LOGGER.info("Starting server at port {}", port);
        serverToClientCommandRunner.run();
        // PersonCollectionServer personCollectionServer = new
        // PersonCollectionServer(serverToClientCommandRunner);
        // serverCommandRunner.run();
        LOGGER.info("Server stopped");
        scanner.close();
    }

    public static PersonDBManager createDBManager(String url, String user, String password, String messageDigest) {
        try {
            return new PersonDBManager(url, user, password, MessageDigest.getInstance(messageDigest));
        } catch (SQLException e) {
            LOGGER.error("Couldn't connect to DB: {}", e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error(e);
        }
        return null;
    }

    public static DatagramChannelIOManager createDatagramChannelIOManager(int port) {
        try {
            return new DatagramChannelIOManager(port);
        } catch (IOException e) {
            LOGGER.error("Starting server failed: {}", e.getMessage());
        }
        return null;
    }

    public static Map<Class<? extends DataCommand>, DataCommand> createClientCommandsMap(
            PersonDBManager manager) {
        HashMap<Class<? extends DataCommand>, DataCommand> commands = new HashMap<>();
        commands.put(Info.class, new Info(manager));
        commands.put(Show.class, new Show(manager));
        commands.put(Add.class, new Add(manager));
        commands.put(Update.class, new Update(manager));
        commands.put(RemoveByID.class, new RemoveByID(manager));
        commands.put(Clear.class, new Clear(manager));
        commands.put(AddIfMax.class, new AddIfMax(manager));
        commands.put(RemoveGreater.class, new RemoveGreater(manager));
        commands.put(MinByCoordinates.class, new MinByCoordinates(manager));
        commands.put(GroupCountingByPassportID.class, new GroupCountingByPassportID(manager));
        commands.put(FilterLessThanNationality.class, new FilterLessThanNationality(manager));
        commands.put(RegisterUserConnection.class, new RegisterUserConnection(manager.getUserManager()));
        return commands;
    }

    public static Map<String, Command> createServerCommandsMap() {
        HashMap<String, Command> commands = new HashMap<>();
        commands.put("exit", new Exit());
        return commands;
    }

    public static int getServerPort(String[] args) {
        final int defaultPort = 1234;
        final int maxPort = 65535;
        final int minPort = 1;
        if (args.length < 2) {
            LOGGER.info("Port value set as default (1234)");
            return defaultPort;
        }
        if (args[1].matches("\\d{1,5}")) {
            int port = Integer.parseInt(args[1]);
            if (port <= maxPort && port >= minPort) {
                return port;
            }
        }
        LOGGER.error(
                "Second argument must either be valid port value or not present (if so it will be set to default value)");
        return -1;
    }

    public static IOManager<String, CommandResponse> createDefaultIOManager(Scanner scanner) {
        return new IOManager<>(
                () -> {
                    if (scanner.hasNextLine()) {
                        return scanner.nextLine();
                    }
                    return null;
                },
                (CommandResponse response) -> {
                    if (response.hasPrintableResult()) {
                        System.out.println(response.getMessage());
                    }
                });
    }
}
