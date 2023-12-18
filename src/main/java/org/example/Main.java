package org.example;


import org.example.database.Database;
import org.example.socket.Server;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws Exception {
        Database.setUp();
//        Seeder.Init();
        Server.Init();
    }
}