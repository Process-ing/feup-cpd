package client.state;

import java.util.Map;
import java.util.Optional;

import client.Cli;
import client.BaseClient;
import protocol.unit.ProtocolUnit;

public abstract class InteractiveState extends ClientState {
    public InteractiveState(BaseClient client) {
        super(client);
    }

    public abstract Map<String, String> getAvailableCommands();

    public abstract String getInfo();

    public abstract ProtocolUnit buildResponse(String input);

    private String getCommand(String input) {
        if (!input.startsWith("/"))
            return "";

        int firstSpace = input.indexOf(" ");
        if (firstSpace == -1)
            firstSpace = input.length();

        return input.substring(0, firstSpace);
    }

    public Optional<ProtocolUnit> buildNextUnit(String input) {
        Map<String, String> availableCommands = getAvailableCommands();

        String command = getCommand(input);

        if (!availableCommands.containsKey(command) || command.equals("/help")) {
            Cli.printHelp(availableCommands);
            return Optional.empty();
        }

        if (command.equals("/info")) {
            Cli.printInfo(getInfo());
            return Optional.empty();
        }

        return Optional.of(buildResponse(input));
    }
}
