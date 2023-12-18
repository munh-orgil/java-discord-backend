package org.example.modules;

import jakarta.persistence.*;
import org.example.database.Database;
import org.example.socket.Request;
import org.example.socket.Response;
import org.example.socket.Server;
import org.hibernate.Session;

import java.io.*;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="messages")
public class Message implements Serializable {
	@Serial
	private static final long serialVersionUID = 99808453L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;
	public String content;
	@ManyToOne
	@JoinColumn(name = "author_id")
	public User author;
	@ManyToOne
	public TextChannel textChannel;
	@Lob
	public File attachment;
	public Timestamp createdAt;
	public Message() {}

	@PrePersist
	public void onCreate() {
		createdAt = new Timestamp(System.currentTimeMillis());
	}

	public Response List(int pageNumber, int pageSize) {
		if (this.textChannel == null) {
			return new Response("Алдаа гарлаа", 400, new ArrayList<Message>());
		}

		Session session = Database.sessionFactory.openSession();
		session.beginTransaction();

		List<Message> messages = session.createQuery("from Message where textChannel = ?1")
				.setParameter(1, this.textChannel)
				.setFirstResult((pageNumber - 1) * pageSize)
				.setMaxResults(pageSize).list();

		session.getTransaction().commit();

		return new Response("Амжилттай", 200, messages);
	}

	public Response Create(User user) {
		if ((this.content == null || this.content.isBlank()) && this.attachment == null) {
			return new Response("Алдаа", 400, null);
		}
		Message message = new Message();
		if (this.content != null && !this.content.isBlank()) {
			message.content = this.content;
		}
		if (this.attachment != null) {
			message.attachment = this.attachment;
		}
		message.author = user;
		message.textChannel = this.textChannel;

		Session session = Database.sessionFactory.openSession();
		session.beginTransaction();

		session.persist(message);
		if (message.id == null) {
			return new Response("Алдаа гарлаа", 500, null);
		}

		session.getTransaction().commit();

		ObjectOutputStream out = null;
		for (Socket client: Server.clients) {
            try {
                out = new ObjectOutputStream(client.getOutputStream());
				out.writeObject(new Response("New msg", 200, message));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		return new Response("Амжилттай", 200, message);
	}

	public Response Edit(User user) {
		Session session = Database.sessionFactory.openSession();
		session.beginTransaction();
		Message message = session.get(Message.class, this.id);
		if (!message.author.id.equals(user.id)) {
			return new Response("Хандах эрхгүй", 403, null);
		}
		if (this.content != null) {
			message.content = this.content;
		}
		session.update(message);
		session.getTransaction().commit();
		return new Response("Амжилттай", 200, message);
	}

	public Response Delete(User user) {
		Session session = Database.sessionFactory.openSession();
		session.beginTransaction();
		Message message = session.get(Message.class, this.id);
		if (!message.author.id.equals(user.id)) {
			return new Response("Хандах эрхгүй", 403, null);
		}
		session.delete(message);
		session.getTransaction().commit();
		return new Response("Амжилттай", 200, message);
	}
}