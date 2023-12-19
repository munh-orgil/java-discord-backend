package org.example.modules;

import jakarta.persistence.*;
import org.example.database.Database;
import org.example.socket.Response;
import org.example.socket.Server;
import org.hibernate.Session;
import org.hibernate.engine.jdbc.env.internal.LobTypes;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.Serial;
import java.io.Serializable;
import java.net.Socket;
import java.sql.Timestamp;

@Entity
@Table(name="users")
public class User implements Serializable {
	@Serial
	private static final long serialVersionUID = 99808453L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;
	@Column(unique = true)
	public String email;
	public String nickname;
	@Lob
	public File avatar;
	public String password;
	@Transient
	public String newPassword;
	public Long isMuted;
	public Long isDeafened;
	public Timestamp createdAt;
	@Transient
	public Socket voiceSocket;

	public User() {}

	@PrePersist
	public void onCreate() {
		createdAt = new Timestamp(System.currentTimeMillis());
	}

	public Response Login(Server.ClientHandler client) {
		String email = this.email;
		String password = this.password;
		Response res = null;
		Session session = Database.sessionFactory.openSession();
		session.beginTransaction();
		User user = (User) session.createQuery("from User where email = ?1").setParameter(1, email).getSingleResultOrNull();
		session.getTransaction().commit();
		if (user == null) {
			res = new Response("Хэрэглэгч олдсонгүй", 400, null);
			return res;
		}
		if (!user.password.equals(password)) {
			res = new Response("Нууц үг буруу", 400, null);
			return res;
		}
		res = new Response("Амжилттай", 200, user);
		client.user = user;
		Server.ipUser.put(client.clientSocket.getInetAddress().getHostAddress(), user);
		return res;
    }

	public Response Register() {
		User user = new User();
		Response res = null;
		if (this.email.isBlank()) {
			res = new Response("И-мэйл оруулсан байх шаардлагатай", 400, null);
			return res;
		}
		if (this.password.isBlank()) {
			res = new Response("Нууц үг оруулсан байх шаардлагатай", 400, null);
			return res;
		}
		user.email = this.email;
		user.password = this.password;
		if (this.avatar != null) {
			user.avatar = this.avatar;
		}
		if (this.nickname.isBlank()) {
			this.nickname = email;
		}
		user.nickname = this.nickname;
		Session session = Database.sessionFactory.openSession();
		session.beginTransaction();

		session.persist(user);

		session.getTransaction().commit();

		if (user.id == null) {
			res = new Response("Хэрэглэгч бүртгэх үед алдаа гарлаа", 500, null);
		} else {
			res = new Response("Хэрэглэгч амжилттай бүртгэгдлээ", 200, user);
		}
		return res;
	}

	public Response Edit(User reqUser) {
		Session session = Database.sessionFactory.openSession();
		session.beginTransaction();
		User user = session.get(User.class, reqUser.id);
		if (this.nickname != null && !this.nickname.isBlank()) {
			user.nickname = this.nickname;
		}
		if (this.avatar != null) {
			user.avatar = this.avatar;
		}
		if (this.isMuted != null) {
			user.isMuted = this.isMuted;
		}
		if (this.isDeafened != null) {
			user.isDeafened = this.isDeafened;
		}
		session.update(user);
		session.getTransaction().commit();

		return new Response("Амжилттай", 200, user);
	}

	public Response ChangePassword(User reqUser) {
		Response res = null;
		if (this.newPassword == null || this.newPassword.isBlank()) {
			res = new Response("Нууц үг оруулсан байх шаардлагатай", 400, null);
			return res;
		}

		Session session = Database.sessionFactory.openSession();
		session.beginTransaction();
		User user = session.get(User.class, reqUser.id);
		session.getTransaction().commit();
		if (!this.password.equals(user.password)) {
			res = new Response("Нууц үг буруу байна", 400, null);
			return res;
		}
		user.password = newPassword;
		session.beginTransaction();
		session.update(user);
		session.getTransaction().commit();

		res = new Response("Амжилттай", 200, user);
		return res;
	}

	public boolean isDeaf() {
		Session session = Database.sessionFactory.openSession();
		session.beginTransaction();
		Long isDeaf = (Long) session.createQuery("select isDeafened from User where id = ?1").setParameter(1, id).getSingleResultOrNull();
		session.getTransaction().commit();
		return isDeaf.equals(1L);
	}

	public boolean isMute() {
		Session session = Database.sessionFactory.openSession();
		session.beginTransaction();
		Long isMute = (Long) session.createQuery("select isMuted from User where id = ?1").setParameter(1, id).getSingleResultOrNull();
		session.getTransaction().commit();
		return isMute.equals(1L);
	}
	@Override
	public String toString() {
		return "User:\n" +
				"id: " + id.toString() + "\n" +
				"email: " + email + "\n" +
				"nickname: " + nickname + "\n";
	}
}