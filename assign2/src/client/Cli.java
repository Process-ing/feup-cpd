package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Cli {
    private static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static String getInput() {
        String input = "";        
        try {            
            input = reader.readLine();        
        } catch (IOException e) {            
            e.printStackTrace();        
        }
        System.out.print("\033[F\r\033[2K");
        return input;    
    }

    public static void printMessage(String username, String message, boolean isSelf) {
        if (isSelf) {
            System.out.printf("\r\033[2K\033[32m%s (You)\033[0m: %s\n", username, message);    
        } else {
            System.out.printf("\r\033[2K\033[33m%s\033[0m: %s\n", username, message);    
        }
    }

    public static void printResponse(String response) {
        System.out.printf("\r\033[2K\033[34m%s\033[0m\n", response);    
    }

    public static void printError(String error) {
        System.out.printf("\r\033[2K\033[31m%s\033[0m\n", error);    
    }
}