import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.ServerSocket;

public class Server {
    private static final int PORT = 5000;

    public static void main(String[] args){
        if(args.length > 0){
            try {
                int port = Integer.parseInt(args[0]);
                startServer(port);
            } catch (NumberFormatException e) {
                System.out.println("Invalid port number provided.");
            }
        }else{
            startServer(PORT);
        }
    }

    private static void startServer(int port){
        try{
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server started on port "+ port +".");

            Socket clientSocket = serverSocket.accept();
            System.out.println("Client Connected: "+clientSocket.getInetAddress().getHostAddress());

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),true);

            String input;
            while((input = in.readLine()) != null) {
                System.out.println("Client: " + input);
                if(input.equalsIgnoreCase("terminate")){
                    System.out.println("Client disconnected.");
                    break;
                }
            }

            in.close();
            out.close();
            serverSocket.close();
            clientSocket.close();
        }catch (IOException e) {
            System.out.println("Error occured while running the server.");
        }
    }
}
