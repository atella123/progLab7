package lab.client;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

import lab.commands.ExecuteScript;
import lab.commands.Help;
import lab.commands.History;
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
import lab.common.commands.datacommands.RemoveByID;
import lab.common.commands.datacommands.RemoveGreater;
import lab.common.commands.datacommands.Show;
import lab.common.commands.datacommands.Update;
import lab.common.data.Color;
import lab.common.data.Coordinates;
import lab.common.data.Country;
import lab.common.data.Location;
import lab.common.data.Person;
import lab.common.io.IOManager;
import lab.common.io.Reader;
import lab.common.io.Writter;
import lab.common.util.ArgumentParser;
import lab.parsers.CoordinatesParser;
import lab.parsers.DataReader;
import lab.parsers.LocationParser;
import lab.parsers.PersonParser;
import lab.util.ClientCommandRunner;

public final class Client {

    private Client() {
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        IOManager<String, String> io = new IOManager<>(() -> {
            if (scanner.hasNextLine()) {
                return scanner.nextLine();
            }
            return null;
        }, System.out::println);
        InetSocketAddress serverAddress = getServerAdress(args, io::write);
        if (Objects.isNull(serverAddress)) {
            scanner.close();
            return;
        }
        try {
            createCommandRunner(serverAddress, io).run();
        } catch (SocketException e) {
            io.write("Socket couldn't be binded");
        }
        scanner.close();
    }

    public static ClientCommandRunner createCommandRunner(InetSocketAddress serverAdress,
            IOManager<String, ?> io)
            throws SocketException {
        Map<String, Command> clientCommandsMap = new HashMap<>();
        ArgumentParser<String> argumentParser = new ArgumentParser<>();
        IOManager<String, CommandResponse> commandRunnerIO = new IOManager<>(io::read,
                response -> {
                    if (response.hasPrintableResult()) {
                        System.out.println(response.getMessage());
                    }
                    if (response.hasCollectionToPrint()) {
                        for (Person p : response.getCollection()) {
                            System.out.println(p);
                        }
                    }
                });
        Map<String, DataCommand> serverCommandsMap = createServerCommandsMap();
        ClientCommandRunner runner = new ClientCommandRunner(clientCommandsMap, serverCommandsMap,
                argumentParser, serverAdress, commandRunnerIO);
        updateArgumentParser(argumentParser, serverCommandsMap, io::read);
        clientCommandsMap.putAll(createCommands(runner, serverCommandsMap));
        return runner;
    }

    public static void updateArgumentParser(ArgumentParser<String> argumentParser,
            Map<String, DataCommand> serverCommandsMap,
            Reader<String> reader) {
        argumentParser.add(Integer.class, x -> {
            try {
                return Integer.valueOf(convertToString(x));
            } catch (NumberFormatException e) {
                return null;
            }
        });
        argumentParser.add(String.class, Client::convertToString);
        argumentParser.add(Country.class,
                x -> DataReader.readEnumValue(convertToString(x), Country.class));
        argumentParser.add(Color.class,
                x -> DataReader.readEnumValue(convertToString(x), Color.class));
        argumentParser.add(File.class, x -> {
            if (Objects.nonNull(x)) {
                return new File(convertToString(x));
            }
            return null;
        });
        argumentParser.add(Person.class,
                x -> PersonParser.parsePerson(new IOManager<>(reader, System.out::println)));
        argumentParser.add(Coordinates.class,
                x -> CoordinatesParser.parseCoordinates(new IOManager<>(reader, System.out::println)));
        argumentParser.add(Location.class,
                x -> LocationParser.parseLocation(new IOManager<>(reader, System.out::println)));
        argumentParser.add(DataCommand.class, serverCommandsMap::get);
    }

    public static String convertToString(Object o) {
        if (Objects.isNull(o)) {
            return null;
        }
        return o.toString();
    }

    public static InetSocketAddress getServerAdress(String[] args, Writter<String> writter) {
        final int maxPort = 65535;
        final int minPort = 1;
        final int defaultPort = 1234;
        if (args.length < 1) {
            writter.write("Set default server adress (localhost:1234)");
            return new InetSocketAddress("localhost", defaultPort);
        }
        String[] ipAndPort = args[0].split(":");
        if (ipAndPort.length != 2) {
            writter.write("Invalid ip adress");
            return null;
        }
        int port = defaultPort;
        try {
            port = Integer.parseInt(ipAndPort[1]);
            if (port > maxPort || port < minPort) {
                writter.write("Port value must be between 0 and 65536");
                return null;
            }
        } catch (NumberFormatException e) {
            writter.write("Invalid port vaalue, set port to default (1234)");
        }
        String[] bytes = ipAndPort[0].split("\\.");
        final byte ipByteCount = 4;
        InetSocketAddress serverAdress = null;
        if (bytes.length == ipByteCount && Arrays.stream(bytes)
                .allMatch(x -> x.matches("((2((5[0-5])|([0-4]\\d))|[01](\\d){2})|(1\\d{2})|(\\d{1,2}))"))) {
            writter.write("Set server adress to " + args[0]);
            serverAdress = new InetSocketAddress(ipAndPort[0], port);
        } else {
            writter.write("Invalid ip adress " + ipAndPort[0]);
        }
        return serverAdress;
    }

    public static Map<String, Command> createCommands(ClientCommandRunner commandRunner,
            Map<String, DataCommand> serverCommands) {
        HashMap<String, Command> commands = new HashMap<>();
        Help help = new Help();
        help.addCommandMap(commands);
        help.addCommandMap(serverCommands);
        commands.put("help", help);
        commands.put("exit", new Exit());
        commands.put("history", new History(commandRunner));
        commands.put("execute_script", new ExecuteScript(commandRunner));
        return commands;
    }

    public static Map<String, DataCommand> createServerCommandsMap() {
        HashMap<String, DataCommand> commands = new HashMap<>();
        commands.put("info", new Info());
        commands.put("show", new Show());
        commands.put("add", new Add());
        commands.put("update", new Update());
        commands.put("remove_by_id", new RemoveByID());
        commands.put("clear", new Clear());
        commands.put("add_if_max", new AddIfMax());
        commands.put("remove_greater", new RemoveGreater());
        commands.put("min_by_coordinates", new MinByCoordinates());
        commands.put("group_counting_by_passport_id", new GroupCountingByPassportID());
        commands.put("filter_less_than_nationality", new FilterLessThanNationality());
        return commands;
    }
}
