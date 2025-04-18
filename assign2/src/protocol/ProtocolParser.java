package protocol;

import protocol.unit.ProtocolUnit;

public interface ProtocolParser {
    ProtocolUnit parse(String string);
}
