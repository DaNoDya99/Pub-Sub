import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public static final String SERVER_IP = "localhost";
    public static final int SERVER_PORT = 5000;

    public static void main(String[] args){
        if(args.length == 3){
            try {
                String serverIP = args[0];
                int serverPort = Integer.parseInt(args[1]);
                String clientType = args[2];

                Socket socket = new Socket(serverIP, serverPort);
                System.out.println("Connected to server: "+serverIP+":"+serverPort);

                BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
                out.println(clientType);

                Thread readThread = new Thread(() -> {
                    try {
                        BufferedReader serverIn =  new BufferedReader(new InputStreamReader(socket.getInputStream()));

                        String serverMsg;
                        while((serverMsg = serverIn.readLine()) != null){
                            System.out.println("Server says: "+serverMsg);
                        }
                    } catch (IOException e) {
                        System.out.println("Error occurred while reading from server: " + e.getMessage());
                    }
                });

                readThread.start();

                String userInput;
                while((userInput = in.readLine()) != null){
                    out.println(userInput);
                    if(userInput.equalsIgnoreCase("terminate")){
                        break;
                    }
                }

                socket.close();
                System.out.println("Disconnected from server.");
            } catch (IOException e) {
                System.out.println("Error occurred while running the client: " + e.getMessage());
            }
        }
    }
}