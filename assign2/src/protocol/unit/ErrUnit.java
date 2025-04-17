package protocol.unit;

import protocol.ProtocolErrorIdentifier;

public record ErrUnit(ProtocolErrorIdentifier id) implements ProtocolUnit {}
