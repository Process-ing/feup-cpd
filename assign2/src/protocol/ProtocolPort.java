package protocol;

import java.io.IOException;

import exception.EndpointUnreachableException;
import protocol.unit.ProtocolUnit;

public interface ProtocolPort {
    void send(ProtocolUnit unit) throws IOException;

    ProtocolUnit receive() throws IOException;

    void connect() throws EndpointUnreachableException, IOException;
    boolean isConnected();

    void close() throws IOException;
}
