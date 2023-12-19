package org.example.socket;

import org.example.modules.User;
import org.example.modules.VoiceChannel;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoiceServer {
    public static Map<Long, List<ClientHandler>> voiceUsers = new HashMap<>();
    public static Map<User, ClientHandler> userMap = new HashMap<>();
    public static ServerSocket server = null;
    public static void Init() {
        try {
            server = new ServerSocket(5555);
            server.setReuseAddress(true);
            while (true) {
                try {
                    Socket client = server.accept();
                    ClientHandler clientSock = new ClientHandler(client);
                    User user = Server.ipUser.get(client.getInetAddress().getHostAddress());
                    clientSock.user = user;
                    userMap.put(user, clientSock);
                    new Thread(clientSock).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
        public VoiceChannel currentVoiceChannel = null;
        public InputStream inStream;
        public OutputStream outStream;

        public ClientHandler(Socket socket)
        {
            this.clientSocket = socket;
        }

        public void run()
        {
            try {
                outStream = clientSocket.getOutputStream();
                inStream = clientSocket.getInputStream();
                byte[] receiveData = new byte[2200];
                while (inStream.read(receiveData) != -1) {
                    List<ClientHandler> clients = VoiceServer.voiceUsers.get(currentVoiceChannel.id);
                    for (ClientHandler client: clients) {
                        if (client == this) {
                            continue;
                        }
                        client.outStream.write(receiveData);
                    }
                }
//                while () {
//                    Long id = Long.parseLong(request.method);
//                    List<org.example.socket.Server.ClientHandler> list = org.example.socket.VoiceServer.voiceUsers.get(id);
//                    if (list == null) {
//                        list = new ArrayList<>();
//                    }
//                    byte[] bytes = (byte[]) request.body;
//                    for (org.example.socket.Server.ClientHandler handler: list) {
//        //                if (client.equals(handler)) {
//        //                    continue;
//        //                }
//        //                if (handler.user.isDeaf()) {
//        //                    continue;
//        //                }
//                        try {
//                            handler.out.writeObject(new Response("", 200, bytes, "voice"));
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
            }
            catch (IOException  e) {
                e.printStackTrace();
            }
            finally {
                try {
                    if (outStream != null) {
                        outStream.close();
                    }
                    if (inStream != null) {
                        inStream.close();
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
