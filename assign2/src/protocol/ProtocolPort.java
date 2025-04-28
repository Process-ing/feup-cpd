package protocol;

import java.io.IOException;

import protocol.unit.ProtocolUnit;

public interface ProtocolPort {
    void send(ProtocolUnit unit) throws IOException;
    ProtocolUnit receive() throws IOException;

    void close() throws IOException;
}
