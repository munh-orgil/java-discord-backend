package org.example.socket;

import org.example.modules.*;
import org.example.modules.Server;

public class Routes {
    public static Response Handle(Request request, org.example.socket.Server.ClientHandler client) {
        Response res = new Response("request not found", 404, null);
        switch (request.module) {
            case "user":
                User reqUser = (User) request.body;
                switch (request.method) {
                    case "login":
                        return reqUser.Login(client);
                    case "register":
                        return reqUser.Register();
                    case "edit":
                        return reqUser.Edit(client.user);
                    case "change_password":
                        return reqUser.ChangePassword(client.user);
                }
                break;
            case "server":
                Server reqServer = (Server) request.body;
                switch (request.method) {
                    case "list":
                        return Server.List();
                    case "find":
                        return reqServer.Find();
                    case "create":
                        return reqServer.Create(client.user);
                    case "edit":
                        return reqServer.Edit(client.user);
                    case "delete":
                        return reqServer.Delete(client.user);
                }
                break;
            case "voice_channel":
                VoiceChannel reqVoiceChannel = (VoiceChannel) request.body;
                switch (request.method) {
                    case "create":
                        return reqVoiceChannel.Create(client.user);
                    case "edit":
                        return reqVoiceChannel.Edit(client.user);
                    case "delete":
                        return reqVoiceChannel.Delete(client.user);
                }
                break;
            case "text_channel":
                TextChannel reqTextChannel = (TextChannel) request.body;
                switch (request.method) {
                    case "create":
                        return reqTextChannel.Create(client.user);
                    case "edit":
                        return reqTextChannel.Edit(client.user);
                    case "delete":
                        return reqTextChannel.Delete(client.user);
                }
                break;
            case "message":
                Message reqMessage = (Message) request.body;
                switch (request.method) {
                    case "list":
                        return reqMessage.List(request.pageNumber, request.pageSize);
                    case "create":
                        return reqMessage.Create(client.user);
                    case "edit":
                        return reqMessage.Edit(client.user);
                    case "delete":
                        return reqMessage.Delete(client.user);
                }
                break;
        }

        return res;
    }
}
