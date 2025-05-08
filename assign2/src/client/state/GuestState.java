package client.state;

import client.Client;
import java.util.Optional;
import protocol.unit.ErrUnit;
import protocol.unit.LoginUnit;
import protocol.unit.OkUnit;
import protocol.unit.ProtocolUnit;
import protocol.unit.RegisterUnit;

public class GuestState extends ClientState {
    public GuestState(Client client) {
        super(client);
    }

    @Override
    public Optional<ProtocolUnit> visit(OkUnit unit) {
        Client client = this.getClient();
        ProtocolUnit previousUnit = client.getPreviousUnit();

        switch (previousUnit) {
            case LoginUnit loginUnit -> {
                System.out.println("Login successful: " + loginUnit.user());
                client.setState(new AuthenticatedState(client, loginUnit.user()));
            }
            case RegisterUnit registerUnit -> {
                System.out.println("Registration successful: " + registerUnit.user());
                client.setState(new AuthenticatedState(client, registerUnit.user()));
            }
            default -> {
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<ProtocolUnit> visit(ErrUnit unit) {
        System.out.println("Error occurred during authentication: " + unit.id());
        return Optional.empty();
    }
}
