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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
	public List<Socket> currentClients;
	public VoiceChannel() {

	}

	@PrePersist
	public void onCreate() {
		createdAt = new Timestamp(System.currentTimeMillis());
	}

	public Response Join(org.example.socket.Server.ClientHandler client) {
		if (id == null || id == 0) {
			return new Response("Алдаа гарлаа", 400, null);
		}
		List<VoiceServer.ClientHandler> list = VoiceServer.voiceUsers.get(id);
		if (list == null) {
			list = new ArrayList<>();
		}
		VoiceServer.ClientHandler voiceClient = VoiceServer.userMap.get(client.user);
		voiceClient.currentVoiceChannel = this;
		if (!list.contains(voiceClient)) {
			list.add(voiceClient);
		}
		org.example.socket.VoiceServer.voiceUsers.put(id, list);
		return new Response("Амжилттай", 200, this);
	}
	public Response Leave(org.example.socket.Server.ClientHandler client) {
		if (id == null || id == 0) {
			return new Response("Алдаа гарлаа", 400, null);
		}
		List<VoiceServer.ClientHandler> list = VoiceServer.voiceUsers.get(id);
		if (list == null) {
			list = new ArrayList<>();
		}
		VoiceServer.ClientHandler voiceClient = VoiceServer.userMap.get(client.user);
		if (!list.contains(voiceClient)) {
			list.remove(voiceClient);
		}
		org.example.socket.VoiceServer.voiceUsers.put(id, list);
		return new Response("Амжилттай", 200, null);
	}

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