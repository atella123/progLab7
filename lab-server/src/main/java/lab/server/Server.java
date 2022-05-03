package lab.server;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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

        Map<String, List<String>> argsMap = getArgsAsMap(args);

        int port = getServerPort(argsMap);
        String[] dbProperties = getDataBaseProperties(argsMap);

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
        CommandRunner<String> serverCommandRunner = new ServerCommandRunner(new ArgumentParser<>(),
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

    public static Map<String, List<String>> getArgsAsMap(String[] args) {

        Map<String, List<String>> argsMap = new HashMap<>();

        String argName = "";
        List<String> arguments = new ArrayList<>();

        argsMap.put(argName, arguments);

        for (int i = 0; i < args.length; i++) {
            if (args[i].matches("-.")) {
                argName = args[i];
                arguments = new ArrayList<>();
                argsMap.put(argName, arguments);
            } else {
                arguments.add(args[i]);
            }
        }

        return argsMap;
    }

    private static int getServerPort(Map<String, List<String>> args) {
        final int defaulPort = 1234;
        final int maxPort = 65535;
        final int minPort = 1;
        List<String> unordered = args.get("");
        List<String> ordered = args.get("-p");
        if (unordered.isEmpty() && Objects.isNull(ordered)) {
            LOGGER.info("Port value set as default (1234)");
            return defaulPort;
        }
        int port = -1;
        if (!ordered.isEmpty() && ordered.get(0).matches("\\d+")) {
            port = Integer.parseInt(ordered.get(0));
        } else if (!unordered.isEmpty() && unordered.get(0).matches("\\d+")) {
            port = Integer.parseInt(unordered.get(0));
            unordered = unordered.subList(1, unordered.size() - 2);
        }
        if (port <= maxPort && port >= minPort) {
            return port;
        }
        LOGGER.error(
                "First positional (or -p) argument must either be valid port value or not present (if so it will be set to default value)");
        return port;
    }

    private static String[] getDataBaseProperties(Map<String, List<String>> args) {
        final String protocol = "jdbc:postgresql://";
        List<String> unordered = args.get("");
        List<String> ordered = args.get("-d");
        if (unordered.size() < 3 || Objects.isNull(ordered) || ordered.size() < 3) {
            LOGGER.error(
                    "Unable to get DB properties: db properties (hostname, user, password) must be passed as positional arguments");
            return new String[0];
        }
        if (ordered.size() >= 3) {
            ordered.set(0, protocol + ordered.get(0));
            return Arrays.copyOf(ordered.toArray(new String[0]), 3);
        }
        unordered.set(0, protocol + unordered.get(0));
        return Arrays.copyOf(unordered.toArray(new String[0]), 3);
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
