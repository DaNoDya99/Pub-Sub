import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client{
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 5000;

    public static void main(String[] args) {
        if(args.length > 2) {
            try {
                String serverIP = args[0];
                int serverPort = Integer.parseInt(args[1]);
                startClient(serverPort, serverIP);
            }catch (NumberFormatException e) {
                System.out.println("Invalid server port number provided.");
            }
        } else {
            startClient(SERVER_PORT, SERVER_IP);
        }
    }

    private static void startClient(int serverPort, String serverIP) {
        try {
            Socket socket = new Socket(serverIP,serverPort);
            System.out.println("Connected to server: "+serverIP + ":" + serverPort );

            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            String input;
            while ((input = in.readLine()) != null){
                out.println(input);
                if(input.equalsIgnoreCase("terminate")){
                    System.out.println("Disconnected from server.");
                    break;
                }
            }

            in.close();
            out.close();
            socket.close();
        }catch(IOException e) {
            System.out.println("Error occured while running the clinet.");
        }
    }
}