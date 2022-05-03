package lab.io;

import java.net.SocketAddress;

import lab.common.commands.CommandResponse;
import lab.common.commands.CommandResult;
import lab.common.data.Person;

public final class ServerResponse extends CommandResponse {

    private final SocketAddress clientAddress;

    public ServerResponse(CommandResponse response, SocketAddress clientAddress) {
        super(response.getResult(), response.getMessage(), response.getCollection());
        this.clientAddress = clientAddress;
    }

    public ServerResponse(CommandResult result, SocketAddress clientAddress) {
        super(result);
        this.clientAddress = clientAddress;

    }

    public ServerResponse(CommandResult result, Person[] collectionResult, SocketAddress clientAddress) {
        super(result, collectionResult);
        this.clientAddress = clientAddress;

    }

    public ServerResponse(CommandResult result, String message, SocketAddress clientAddress) {
        super(result, message);
        this.clientAddress = clientAddress;

    }

    public ServerResponse(CommandResult result, String message, Person[] collectionResult,
            SocketAddress clientAddress) {
        super(result, message, collectionResult);
        this.clientAddress = clientAddress;
    }

    public CommandResponse getAsCommandResponse() {
        return new CommandResponse(this.getResult(), this.getMessage(), this.getCollection());
    }

    public SocketAddress getClientAddress() {
        return clientAddress;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((clientAddress == null) ? 0 : clientAddress.hashCode());
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
        ServerResponse other = (ServerResponse) obj;
        if (clientAddress == null) {
            return other.clientAddress == null;
        }
        return clientAddress.equals(other.clientAddress);
    }

}
