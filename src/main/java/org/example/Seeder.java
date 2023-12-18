package org.example;

import org.example.database.Database;
import org.example.modules.Server;
import org.example.modules.TextChannel;
import org.example.modules.User;
import org.hibernate.Session;

import java.io.File;

public class Seeder {
    public static void Init() {
        Session session = Database.sessionFactory.openSession();
        session.beginTransaction();
        User user = new User();
        user.email = "m_orgilmn@yahoo.com";
        user.nickname = "muug";
        user.password = "123";
        user.avatar = new File("src/main/resources/logo.png");
        session.persist(user);
        User user1 = new User();
        user1.email = "se21d03@nmit.edu.mn";
        user1.nickname = "a";
        user1.password = "123";
        session.persist(user1);

        Server server = new Server();
        server.owner = user;
        server.name = "test1";
        session.persist(server);
        Server server1 = new Server();
        server1.owner = user1;
        server1.name = "test2";
        session.persist(server1);

        session.getTransaction().commit();
    }
}
