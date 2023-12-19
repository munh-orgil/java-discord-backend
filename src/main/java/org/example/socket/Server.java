package org.example.socket;

import com.mysql.cj.xdevapi.Client;
import org.example.modules.User;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {
    public static ServerSocket server = null;
    public static List<ClientHandler> clients = new ArrayList<ClientHandler>();
    public static Map<String, User> ipUser = new HashMap<>();
    public static void Init() {
        try {
            server = new ServerSocket(5000);
            server.setReuseAddress(true);
            System.out.println("Server started and waiting for clients");
            while (true) {
                Socket client = server.accept();
                System.out.println("New client from " + client.getInetAddress().getHostAddress());
                ClientHandler clientSock = new ClientHandler(client);
                clients.add(clientSock);
                new Thread(clientSock).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (server != null) {
                try {
                    server.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static class ClientHandler implements Runnable {
        public final Socket clientSocket;
        public User user;
        public ObjectInputStream in;
        public ObjectOutputStream out;

        // Constructor
        public ClientHandler(Socket socket)
        {
            this.clientSocket = socket;
        }

        public void run()
        {
            Request req = null;
            Response res = null;
            try {
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                in = new ObjectInputStream(clientSocket.getInputStream());
                while(true) {
                    req = (Request) in.readObject();
                    res = Routes.Handle(req, this);
                    out.writeObject(res);
                }
            }
            catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                    if (in != null) {
                        in.close();
                        clientSocket.close();
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
