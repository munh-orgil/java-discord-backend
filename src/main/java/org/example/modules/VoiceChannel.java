package org.example.modules;

import jakarta.persistence.*;
import org.example.database.Database;
import org.example.socket.Response;
import org.example.socket.VoiceServer;
import org.hibernate.Session;

import java.io.Serial;
import java.io.Serializable;
import java.net.Socket;
import java.sql.Timestamp;

@Entity
@Table(name="voice_channels")
public class VoiceChannel implements Serializable {
	@Serial
	private static final long serialVersionUID = 99808453L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;
	public String name;
	public Long serverId;
	public Long sequence;
	public Timestamp createdAt;
	@Transient
	public Socket clientSocket;

	public VoiceChannel() {

	}

	@PrePersist
	public void onCreate() {
		createdAt = new Timestamp(System.currentTimeMillis());
	}

	public Response Join(User reqUser) {
		if (id == null || id == 0) {
			return new Response("Алдаа гарлаа", 400, null);
		}
		VoiceServer.clientMap.get(id).add(reqUser);
		return new Response("Амжилттай", 200, null);
	}
//	public Response Leave() {
//
//	}

	public Response Create(User reqUser) {
		VoiceChannel voiceChannel = new VoiceChannel();
		if (this.name == null || this.name.isBlank()) {
			return new Response("Өрөөний нэр оруулах шаардлагатай", 400, null);
		}
		if (this.serverId == 0) {
			return new Response("Алдаа гарлаа", 400, null);
		}
		Session session = Database.sessionFactory.openSession();
		session.beginTransaction();

		Server server = session.get(Server.class, this.serverId);
		if (server.owner.id.equals(reqUser.id)) {
			return new Response("Хандах эрхгүй", 403, null);
		}

		Long maxSequence = (Long) session.createQuery("select sequence from VoiceChannel where serverId = ?1")
				.setParameter(1, this.serverId)
				.uniqueResult();

		voiceChannel.serverId = this.serverId;
		voiceChannel.name = this.name;
		voiceChannel.sequence = maxSequence + 1;
		session.persist(voiceChannel);

		session.getTransaction().commit();

		if (voiceChannel.id == 0) {
			return new Response("Өрөө үүсгэхэд алдаа гарлаа", 500, voiceChannel);
		}
		return new Response("Амжилттай", 200, voiceChannel);
	}

	public Response Edit(User reqUser) {
		if (this.id == 0) {
			return new Response("Алдаа гарлаа", 400, null);
		}
		if (this.name == null || this.name.isBlank()) {
			return new Response("Өрөөний нэр оруулах шаардлагатай", 400, null);
		}
		Session session = Database.sessionFactory.openSession();
		session.beginTransaction();

		VoiceChannel voiceChannel = session.get(VoiceChannel.class, this.id);
		Server server = session.get(Server.class, voiceChannel.serverId);
		if (server.owner.id.equals(reqUser.id)) {
			return new Response("Хандах эрхгүй", 403, null);
		}
		voiceChannel.name = this.name;
		session.update(voiceChannel);

		session.getTransaction().commit();

		return new Response("Амжилттай", 200, voiceChannel);
	}

	public Response Delete(User reqUser) {
		if (this.id == 0) {
			return new Response("Алдаа гарлаа", 400, null);
		}
		Session session = Database.sessionFactory.openSession();
		session.beginTransaction();

		VoiceChannel voiceChannel = session.get(VoiceChannel.class, this.id);
		Server server = session.get(Server.class, voiceChannel.serverId);
		if (server.owner.id.equals(reqUser.id)) {
			return new Response("Хандах эрхгүй", 403, null);
		}
		session.delete(voiceChannel);

		session.getTransaction().commit();

		return new Response("Амжилттай", 200, voiceChannel);
	}
}