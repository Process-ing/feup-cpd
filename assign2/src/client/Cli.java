package client;

import client.state.AuthenticatedState;
import client.state.ClientState;
import client.state.GuestState;
import client.state.RoomState;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Cli {
    private static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static String getInput() {
        try {
            String input = reader.readLine();
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

    public static void printConnection(String info) {
        System.out.printf("\r\033[2K\033[34m%s\033[0m\n", info);    
    }

    public static void printInfo(ClientState state) {
        System.out.println("\r\033[2K    \033[36mSession information\033[0m");
        switch (state) {
            case GuestState guestState -> {
                System.out.println("Not logged in.");
                break;
            }
            case AuthenticatedState authenticatedState -> {
                System.out.printf("Logged in as: %s.\n", authenticatedState.getUsername());
                break;
            }
            case RoomState roomState -> {
                System.out.printf("Logged in as: %s. In room: %s\n", roomState.getUsername(), roomState.getRoomName());
                break;
            }
            default -> {
                System.out.println("Unknown state.");
            }
        }
    }

    public static void printHelp(ClientState state) {
        System.out.println("\r\033[2K    \033[36mAvailable commands\033[0m");
        state.getAvailableCommands().forEach((_command, description) -> {
            System.out.printf("%s\n", description);
        });
        if (state instanceof RoomState) {
            System.out.printf("<message> : Send a message to the room\n");
        }
    }
}