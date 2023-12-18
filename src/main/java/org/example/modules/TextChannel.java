package org.example.modules;

import jakarta.persistence.*;
import org.example.database.Database;
import org.example.socket.Response;
import org.hibernate.Session;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name="text_channels")
public class TextChannel implements Serializable {
	@Serial
	private static final long serialVersionUID = 99808453L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;
	public String name;
	public Long serverId;
	public Long sequence;
	public Timestamp createdAt;
	public TextChannel() {}

	@PrePersist
	public void onCreate() {
		createdAt = new Timestamp(System.currentTimeMillis());
	}

	public Response Create(User reqUser) {
		TextChannel textChannel = new TextChannel();
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
		Long maxSequence = (Long) session.createQuery("select sequence from TextChannel where serverId = ?1")
				.setParameter(1, this.serverId)
				.uniqueResult();

		textChannel.serverId = this.serverId;
		textChannel.name = this.name;
		textChannel.sequence = maxSequence + 1;
		session.persist(textChannel);

		session.getTransaction().commit();

		if (textChannel.id == 0) {
			return new Response("Өрөө үүсгэхэд алдаа гарлаа", 500, textChannel);
		}
		return new Response("Амжилттай", 200, textChannel);
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

		TextChannel textChannel = session.get(TextChannel.class, this.id);
		Server server = session.get(Server.class, textChannel.serverId);
		if (server.owner.id.equals(reqUser.id)) {
			return new Response("Хандах эрхгүй", 403, null);
		}
		textChannel.name = this.name;
		session.update(textChannel);

		session.getTransaction().commit();

		return new Response("Амжилттай", 200, textChannel);
	}

	public Response Delete(User reqUser) {
		if (this.id == 0) {
			return new Response("Алдаа гарлаа", 400, null);
		}
		Session session = Database.sessionFactory.openSession();
		session.beginTransaction();

		TextChannel textChannel = session.get(TextChannel.class, this.id);
		Server server = session.get(Server.class, textChannel.serverId);
		if (server.owner.id.equals(reqUser.id)) {
			return new Response("Хандах эрхгүй", 403, null);
		}
		session.delete(textChannel);

		session.getTransaction().commit();

		return new Response("Амжилттай", 200, textChannel);
	}
}