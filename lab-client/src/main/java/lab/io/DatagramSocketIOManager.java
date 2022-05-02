package lab.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import lab.common.commands.CommandResponse;
import lab.common.commands.CommandResult;
import lab.common.io.IOManager;
import lab.common.util.DataCommandExecuteRequest;

public class DatagramSocketIOManager extends IOManager<CommandResponse, DataCommandExecuteRequest> {

    private static final int MAX_PACKAGE_SIZE = 65507;
    private static final int TIMEOUT = 50000;
    private final InetSocketAddress serverAddress;
    private DatagramSocket socket;

    public DatagramSocketIOManager(InetSocketAddress serverAddress) throws SocketException {
        this.serverAddress = serverAddress;
        setupNewSocket();
        setReader(this::readResponse);
        setWritter(this::writeCommandWithArgs);
    }

    private void setupNewSocket() throws SocketException {
        InetSocketAddress address;
        address = new InetSocketAddress(0);
        socket = new DatagramSocket(address);
        socket.connect(serverAddress);
        socket.setSoTimeout(TIMEOUT);
    }

    private CommandResponse setupNewSocketSuppresed() {
        try {
            setupNewSocket();
            return new CommandResponse(CommandResult.ERROR, "Couldn't get response from server");
        } catch (SocketException e) {
            return new CommandResponse(CommandResult.END, "Can't connect to server");
        }
    }

    private CommandResponse readResponse() {
        try {
            DatagramPacket packet = new DatagramPacket(new byte[MAX_PACKAGE_SIZE], MAX_PACKAGE_SIZE);
            socket.receive(packet);
            ByteArrayInputStream byteInputStream = new ByteArrayInputStream(packet.getData());
            ObjectInputStream objectInputStream = new ObjectInputStream(byteInputStream);
            return (CommandResponse) objectInputStream.readObject();
        } catch (SocketTimeoutException e) {
            return setupNewSocketSuppresed();
        } catch (IOException | ClassNotFoundException e) {
            return new CommandResponse(CommandResult.ERROR, "Couldn't get response from server");
        }
    }

    private void writeCommandWithArgs(DataCommandExecuteRequest commandWithArgs) {
        try {
            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);
            objectOutputStream.writeObject(commandWithArgs);
            DatagramPacket packet = new DatagramPacket(byteOutputStream.toByteArray(),
                    byteOutputStream.toByteArray().length);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + MAX_PACKAGE_SIZE;
        result = prime * result + ((socket == null) ? 0 : socket.hashCode());
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
        DatagramSocketIOManager other = (DatagramSocketIOManager) obj;
        if (socket == null) {
            return other.socket == null;
        }
        return socket.equals(other.socket);
    }
}
