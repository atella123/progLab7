package lab.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lab.common.commands.CommandResponse;
import lab.common.commands.CommandResult;
import lab.common.io.IOManager;
import lab.common.util.DataCommandExecuteRequest;

public class DatagramChannelIOManager extends IOManager<ServerExecuteRequest, ServerResponse> {

    private static final Logger LOGGER = LogManager.getLogger(lab.io.DatagramChannelIOManager.class);
    private static final int MAX_PACKAGE_SIZE = 65507;
    private final DatagramChannel datagramChannel;

    public DatagramChannelIOManager(int port) throws IOException {
        datagramChannel = DatagramChannel.open();
        datagramChannel.bind(new InetSocketAddress(port));
        datagramChannel.configureBlocking(true);
        setReader(this::readCommandWithArgs);
        setWriter(this::writeResponse);
    }

    public DatagramChannelIOManager(int port, boolean blocking) throws IOException {
        datagramChannel = DatagramChannel.open();
        datagramChannel.bind(new InetSocketAddress(port));
        datagramChannel.configureBlocking(blocking);
        setReader(this::readCommandWithArgs);
        setWriter(this::writeResponse);
    }

    private ServerExecuteRequest readCommandWithArgs() {
        ByteBuffer inputPackages = ByteBuffer.wrap(new byte[MAX_PACKAGE_SIZE]);
        try {
            SocketAddress remoteAddress = datagramChannel.receive(inputPackages);
            if (Objects.isNull(remoteAddress)) {
                return null;
            }
            ObjectInputStream objectInputStream = new ObjectInputStream(
                    new ByteArrayInputStream(inputPackages.array()));
            DataCommandExecuteRequest input = (DataCommandExecuteRequest) objectInputStream.readObject();
            if (Objects.nonNull(input)) {
                LOGGER.info("New client request received from {} to execute {} command", remoteAddress,
                        input.getCommandClass().getSimpleName());
            }
            return new ServerExecuteRequest(input, remoteAddress);
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    private void writeResponse(ServerResponse response) {
        if (response.getResult().equals(CommandResult.COMMAND_NOT_FOUND)
                || response.getResult().equals(CommandResult.NO_INPUT)) {
            return;
        }
        ByteArrayOutputStream dataOutputStream = serealizeCommandResponse(response);
        writeNextDatagram(ByteBuffer.wrap(dataOutputStream.toByteArray()), response.getClientAddress());
        LOGGER.info("Send reply to client {}", response.getClientAddress());
    }

    private ByteArrayOutputStream serealizeCommandResponse(ServerResponse response) {
        try {
            ByteArrayOutputStream dataOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(dataOutputStream);
            objectOutputStream.writeObject(response.getAsCommandResponse());
            if (dataOutputStream.size() > MAX_PACKAGE_SIZE) {
                objectOutputStream
                        .writeObject(new CommandResponse(CommandResult.ERROR, "Original result couldn't be sent"));
            }
            return dataOutputStream;
        } catch (IOException e) {
            return new ByteArrayOutputStream();
        }
    }

    private void writeNextDatagram(ByteBuffer byteBuffer, SocketAddress remoteAddress) {
        while (byteBuffer.hasRemaining() && Objects.nonNull(remoteAddress)) {
            try {
                datagramChannel.send(byteBuffer, remoteAddress);
            } catch (IOException e) {
                LOGGER.error("An error occurred when sending response: {}", e.getMessage());
            }
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((datagramChannel == null) ? 0 : datagramChannel.hashCode());
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
        DatagramChannelIOManager other = (DatagramChannelIOManager) obj;
        if (datagramChannel == null) {
            return other.datagramChannel == null;
        }
        return datagramChannel.equals(other.datagramChannel);
    }

}
