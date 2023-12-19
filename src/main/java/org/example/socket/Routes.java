package org.example.socket;

import org.example.database.Database;
import org.example.modules.*;
import org.example.modules.Server;
import org.hibernate.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Routes {
    public static Response Handle(Request request, org.example.socket.Server.ClientHandler client) {
        Response res = new Response("request not found", 404, null);
        res = switch (request.module) {
            case "user" -> {
                User reqUser = (User) request.body;
                yield switch (request.method) {
                    case "login" -> reqUser.Login(client);
                    case "register" -> reqUser.Register();
                    case "edit" -> reqUser.Edit(client.user);
                    case "change_password" -> reqUser.ChangePassword(client.user);
                    default -> res;
                };
            }
            case "server" -> {
                Server reqServer = (Server) request.body;
                yield switch (request.method) {
                    case "list" -> Server.List();
                    case "find" -> reqServer.Find();
                    case "create" -> reqServer.Create(client.user);
                    case "edit" -> reqServer.Edit(client.user);
                    case "delete" -> reqServer.Delete(client.user);
                    default -> res;
                };
            }
            case "voice_channel" -> {
                VoiceChannel reqVoiceChannel = (VoiceChannel) request.body;
                yield switch (request.method) {
                    case "join" -> reqVoiceChannel.Join(client);
                    case "leave" -> reqVoiceChannel.Leave(client);
                    case "create" -> reqVoiceChannel.Create(client.user);
                    case "edit" -> reqVoiceChannel.Edit(client.user);
                    case "delete" -> reqVoiceChannel.Delete(client.user);
                    default -> res;
                };
            }
            case "text_channel" -> {
                TextChannel reqTextChannel = (TextChannel) request.body;
                yield switch (request.method) {
                    case "create" -> reqTextChannel.Create(client.user);
                    case "edit" -> reqTextChannel.Edit(client.user);
                    case "delete" -> reqTextChannel.Delete(client.user);
                    default -> res;
                };
            }
            case "message" -> {
                Message reqMessage = (Message) request.body;
                yield switch (request.method) {
                    case "list" -> reqMessage.List();
                    case "create" -> reqMessage.Create(client.user);
                    case "edit" -> reqMessage.Edit(client.user);
                    case "delete" -> reqMessage.Delete(client.user);
                    default -> res;
                };
            }
            default -> res;
        };
        res.func = request.func;

        return res;
    }
}
