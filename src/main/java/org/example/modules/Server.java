package org.example.modules;

import jakarta.persistence.*;
import org.example.database.Database;
import org.example.socket.Response;
import org.hibernate.Session;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

@Entity
@Table(name="servers")
public class Server implements Serializable {
	@Serial
	private static final long serialVersionUID = 99808453L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	public Long id;
	public String name;
	@Lob
	public File logo;
	@ManyToOne
	@JoinColumn(name = "owner_id")
	public User owner;
	public Timestamp createdAt;

	@Transient
	public List<TextChannel> textChannels;
	@Transient
	public List<VoiceChannel> voiceChannels;

	public Server() {}

	@PrePersist
	public void onCreate() {
		createdAt = new Timestamp(System.currentTimeMillis());
	}
	@PostLoad
	public void afterFind() {
		Session session = Database.sessionFactory.openSession();
		session.beginTransaction();

		this.textChannels = session.createQuery("from TextChannel where serverId = ?1 order by sequence ASC ")
				.setParameter(1, this.id)
				.list();
		this.voiceChannels = session.createQuery("from VoiceChannel where serverId = ?1 order by sequence ASC ")
				.setParameter(1, this.id)
				.list();

		session.getTransaction().commit();
	}
	@PostPersist
	public void afterCreate() {
		Session session = Database.sessionFactory.openSession();
		session.beginTransaction();
		TextChannel textChannel = new TextChannel();
		textChannel.serverId = this.id;
		textChannel.name = "general";
		textChannel.sequence = 1L;

		VoiceChannel voiceChannel = new VoiceChannel();
		voiceChannel.serverId = this.id;
		voiceChannel.name = "general";
		voiceChannel.sequence = 1L;

		session.persist(textChannel);
		session.persist(voiceChannel);

		session.getTransaction().commit();
	}

	public static Response List() {
		Session session = Database.sessionFactory.openSession();
		session.beginTransaction();
        List<Server> servers = session.createQuery("from Server order by name").list();
		session.getTransaction().commit();
//		System.out.println(servers.getFirst().textChannels.getFirst().name);
		return new Response("Амжилттай", 200, servers);
	}
	public Response Find() {
		if (this.id == null || this.id == 0) {
			return new Response("Алдаа гарлаа", 400, null);
		}
		Session session = Database.sessionFactory.openSession();
		session.beginTransaction();
		Server server = (Server) session.createQuery("from Server where id = ?1").setParameter(1, this.id).getSingleResultOrNull();
		session.getTransaction().commit();
		return new Response("Амжилттай", 200, server);
	}
	public Response Create(User reqUser) {
		Server server = new Server();
		if (this.name == null || this.name.isBlank()) {
			return new Response("Серверийн нэр оруулах шаардлагатай", 400, null);
		}
		if (this.logo != null) {
			server.logo = this.logo;
		}
		server.name = this.name;
		server.owner = reqUser;

		Session session = Database.sessionFactory.openSession();
		session.beginTransaction();
		session.persist(server);
		session.getTransaction().commit();

		if (server.id == 0) {
			return new Response("Сервер үүсгэх үед алдаа гарлаа", 500, null);
		}

		return new Response("Амжилттай", 200, server);
	}

	public Response Edit(User reqUser) {
		if (this.id == 0) {
			return new Response("Алдаа гарлаа", 400, null);
		}
		Session session = Database.sessionFactory.openSession();
		session.beginTransaction();
		Server server = session.get(Server.class, this.id);
		session.getTransaction().commit();
		if (!server.owner.id.equals(reqUser.id)) {
			return new Response("Хандах эрхгүй", 403, null);
		}
		if (!this.name.isBlank()) {
			server.name = this.name;
		}
		if (this.logo != null) {
			server.logo = this.logo;
		}
		session.beginTransaction();
		session.update(server);
		session.getTransaction().commit();

		return new Response("Амжилттай", 200, server);
	}
	public Response Delete(User reqUser) {
		if (this.id == 0) {
			return new Response("Алдаа гарлаа", 400, null);
		}
		Session session = Database.sessionFactory.openSession();
		session.beginTransaction();
		Server server = session.get(Server.class, this.id);
		session.getTransaction().commit();
		if (!server.owner.id.equals(reqUser.id)) {
			return new Response("Хандах эрхгүй", 403, null);
		}
		session.beginTransaction();
		session.delete(server);
		session.getTransaction().commit();

		return new Response("Амжилттай", 200, null);
	}
}