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

    public static void printMessage(String username, String message) {
        System.out.printf("\r\033[2K%s# %s\n", username, message);    
    }
}