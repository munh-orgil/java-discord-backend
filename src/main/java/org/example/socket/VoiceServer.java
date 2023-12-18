package org.example.socket;

import org.example.modules.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoiceServer {
    public static Map<Long, List<User>> clientMap = new HashMap<>();
    public static ServerSocket server = null;
    public static void Init() {
        try {
            server = new ServerSocket(5555);
            server.setReuseAddress(true);
            System.out.println("Server started and waiting for clients");
            while (true) {
                Socket client = server.accept();
                System.out.println("New client from " + client.getInetAddress().getHostAddress());
                Server.ClientHandler clientSock = new Server.ClientHandler(client);
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
        private final Socket clientSocket;
        public User user;

        public ClientHandler(Socket socket)
        {
            this.clientSocket = socket;
        }

        public void run()
        {
            ObjectOutputStream out = null;
            ObjectInputStream in = null;
            Request req = null;
            Response res = null;
            try {
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                in = new ObjectInputStream(clientSocket.getInputStream());
                while(true) {

                }
            }
            catch (IOException  e) {
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
