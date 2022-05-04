package lab.server;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Arrays;
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
import lab.util.PersonCollectionServer;
import lab.util.ServerCommandRunner;
import lab.util.ServerToClientCommandRunner;

public final class Server {

    private static final Logger LOGGER = LogManager.getLogger(lab.server.Server.class);

    private Server() {
        throw new UnsupportedOperationException("This is an utility class and can not be instantiated");
    }

    public static void main(String[] args) {

        int port = getServerPort(args);
        String[] dbProperties = getDataBaseProperties(args);

        if (port == -1 || dbProperties.length == 0) {
            return;
        }

        PersonDBManager manager = createDBManager(dbProperties, "MD2");
        DatagramChannelIOManager clientIOManager = createDatagramChannelIOManager(port);

        if (Objects.isNull(manager) || Objects.isNull(clientIOManager)) {
            return;
        }

        Scanner scanner = new Scanner(System.in);
        Map<String, Command> serverCommandsMap = createServerCommandsMap();
        CommandRunner serverCommandRunner = new ServerCommandRunner(new ArgumentParser<>(),
                serverCommandsMap, createDefaultIOManager(scanner));

        Map<Class<? extends DataCommand>, DataCommand> clientCommandManager = createClientCommandsMap(manager);
        ServerToClientCommandRunner serverToClientCommandRunner = new ServerToClientCommandRunner(clientCommandManager,
                clientIOManager);

        LOGGER.info("Starting server at port {}", port);

        PersonCollectionServer personCollectionServer = new PersonCollectionServer(serverCommandRunner,
                serverToClientCommandRunner);
        personCollectionServer.run();

        LOGGER.info("Server stopped");
        scanner.close();
    }

    public static PersonDBManager createDBManager(String[] properties, String messageDigest) {
        try {
            return new PersonDBManager(properties, MessageDigest.getInstance(messageDigest));
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
        final int maxPort = 65535;
        final int minPort = 1;
        if (args.length > 0 && args[0].matches("\\d{1,5}")) {
            int port = Integer.parseInt(args[0]);
            if (port <= maxPort && port >= minPort) {
                return port;
            }
        }
        LOGGER.error(
                "First argument must either be valid port value");
        return -1;
    }

    private static String[] getDataBaseProperties(String[] args) {
        final String protocol = "jdbc:postgresql://";
        final int propertiesCount = 3;
        if (args.length < propertiesCount + 1) {
            LOGGER.error("Arguments from position 2 to 4 must match db url, username and password");
            return new String[0];
        }
        String[] properties = Arrays.copyOfRange(args, 1, 1 + propertiesCount);
        properties[0] = protocol + properties[0];
        return properties;
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
