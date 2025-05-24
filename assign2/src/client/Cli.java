package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

public class Cli {
    private static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static String getInput() {
        try {
            String input = reader.ready() ? reader.readLine() : null;
            if (input != null)
                System.out.print("\033[F\r\033[2K");

            return input;

        } catch (IOException e) {
            printError("Error reading input: " + e.getMessage());
            return "";
        }
    }

    public static void printMessage(String username, String message, boolean isSelf) {
        if (isSelf) {
            System.out.printf("\r\033[2K\033[32m%s (You)\033[0m: %s\n", username, message);
        } else {
            System.out.printf("\r\033[2K\033[33m%s\033[0m: %s\n", username, message);
        }
    }

    public static void printResponse(String response) {
        System.out.printf("\r\033[2K\033[36m%s\033[0m\n", response);
    }

    public static void printError(String error) {
        System.out.printf("\r\033[2K\033[31m%s\033[0m\n", error);
    }

    public static void printWarning(String error) {
        System.out.printf("\r\033[2K\033[33m%s\033[0m\n", error);
    }

    public static void printConnection(String info) {
        System.out.printf("\r\033[2K\033[34m%s\033[0m\n", info);
    }

    public static void printInfo(String info) {
        System.out.println("\r\033[2K\033[36mSession Information\033[0m");
        System.out.println(info);
    }

    public static void printHelp(Map<String, String> availableCommands) {
        System.out.println("\r\033[2K\033[36mAvailable Commands\033[0m");
        for (String description: availableCommands.values()) {
            System.out.printf("%s\n", description);
        }
    }

    public static void printRooms(List<String> rooms, List<String> aiRooms){
        System.out.println("\r\033[2K\033[36mAvailable Rooms\033[0m");
        for (String room: rooms) {
            System.out.printf(" ◇ %s\n", room);
        }
        for (String aiRoom: aiRooms) {
            System.out.printf(" ◈ %s \033[35m(AI)\033[0m\n", aiRoom);
        }
    }
}