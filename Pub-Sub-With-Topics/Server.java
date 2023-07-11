import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static final int PORT = 5000;
    private static List<ClientHandler> publisher = new ArrayList<>();
    private static List<ClientHandler> subscriber = new ArrayList<>();
    private static List<ClientHandler> clients = new ArrayList<>();
    private static int PublisherNumber = 1;
    private static int SubscriberNumber = 1;

    public static void main(String[] args){
        if(args.length > 0){
            try {
                int port = Integer.parseInt(args[0]);
                startServer(port);
            } catch (NumberFormatException e) {
                System.out.println("Invalid port number provided.");
            }
        } else {
            startServer(PORT);
        }
    }

    private static void startServer(int port) {
        try {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("Server started on port: "+port);

                while(true) {
                    Socket clientSocket = serverSocket.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String clientTypeWithTopic = in.readLine();
                    System.out.println(clientTypeWithTopic);
                    String clientType = clientTypeWithTopic.split(" ")[0];
                    String clientTopic = clientTypeWithTopic.split(" ")[1];

                    if(clientType.equalsIgnoreCase("publisher")){
                        System.out.println(clientType+" "+PublisherNumber+" connected (Topic:- "+clientTopic+"): " + clientSocket.getInetAddress().getHostAddress());
                    }else if(clientType.equalsIgnoreCase("subscriber")){
                        System.out.println(clientType+" "+SubscriberNumber+" connected (Topic:- "+clientTopic+"): " + clientSocket.getInetAddress().getHostAddress());
                    }else{
                        System.out.println("Unknown client type.");
                    }


                    ClientHandler clientHandler = null;

                    if(clientType.equalsIgnoreCase("publisher")){
                        clientHandler = new ClientHandler(clientSocket,clientType,++PublisherNumber,clientTopic);
                    }else if(clientType.equalsIgnoreCase("subscriber")){
                        clientHandler = new ClientHandler(clientSocket,clientType,++SubscriberNumber,clientTopic);
                    }
                    
                    
                    if(clientType.equalsIgnoreCase("publisher")){
                        publisher.add(clientHandler);
                    }else if(clientType.equalsIgnoreCase("subscriber")){
                        subscriber.add(clientHandler);
                    }else{
                        System.out.println("Unknown client type.");
                    }

                    clientHandler.start();
                }
            }
        } catch (Exception e) {
            System.out.println("Error occured while running the server.");
        }
    }

    static void broadcastMessage(String message, ClientHandler sender){
        for(ClientHandler client : subscriber){
            String clientType = client.getClientType();
            String senderType = sender.getClientType();
            String clientTopic = client.getClientTopic();
            String senderTopic = sender.getClientTopic();

            if(clientType.equalsIgnoreCase("subscriber") && senderType.equalsIgnoreCase("publisher")){
                if(clientTopic.equalsIgnoreCase(senderTopic)){
                    client.sendMessage(message);
                }
            }
        }
    }

    static void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    static class ClientHandler extends Thread {
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;
        private String ClientType;
        private int PublisherNumber;
        private int SubscriberNumber;
        private String Topic;

        public ClientHandler(Socket socket,String clientType,int number,String topic) {
            this.clientSocket = socket;
            this.ClientType = clientType;
            this.Topic = topic;

            if(clientType.equalsIgnoreCase("publisher")){
                this.PublisherNumber = number;
            }else if(clientType.equalsIgnoreCase("subscriber")){
                this.SubscriberNumber = number;
            }else{
                System.out.println("Unknown client type");
            }
        }

        public String getClientType(){
            return this.ClientType;
        }

        public String getClientTopic(){
            return this.Topic;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                String input;
                while ((input = in.readLine()) != null) {
                    Server.broadcastMessage(input, this);

                    System.out.println(this.ClientType+" "+this.PublisherNumber+" says: "+input);

                    if(input.equalsIgnoreCase("terminate")){
                        if(this.ClientType.equalsIgnoreCase("publisher")){
                            System.out.println(this.ClientType+" "+this.PublisherNumber+" disconnected: " + clientSocket.getInetAddress().getHostAddress());
                        } else if(this.ClientType.equalsIgnoreCase("subscriber")){
                            System.out.println(this.ClientType+" "+this.SubscriberNumber+" disconnected: " + clientSocket.getInetAddress().getHostAddress());
                        }
                    }
                }

                Server.removeClient(this);
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e){
                System.out.println("Error occured in client handler: "+e.getMessage());
            }
        }

        public void sendMessage(String message) {
            out.println(message);
        }
    }
}
