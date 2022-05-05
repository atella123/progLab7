package lab.io;

import java.net.SocketAddress;

import lab.common.util.DataCommandExecuteRequest;

public class ServerExecuteRequest extends DataCommandExecuteRequest {

    private final SocketAddress clientAddress;

    public ServerExecuteRequest(DataCommandExecuteRequest request,
            SocketAddress clientAddress) {
        super(request.getUser(), request.getCommandClass(), request.getArguments());
        this.clientAddress = clientAddress;
    }

    public SocketAddress getClientAddress() {
        return clientAddress;
    }

}
