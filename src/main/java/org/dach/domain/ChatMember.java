package org.dach.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "chat_members")
public class ChatMember {

    @EmbeddedId
    private ChatMemberId id;

    @Column(name = "first_name", length = 150)
    private String firstName;

    @Column(name = "last_name", length = 150)
    private String lastName;

    @Column(name = "username", length = 150)
    private String username;

    @Column(name = "registered_at", nullable = false)
    private LocalDateTime registeredAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public ChatMember() {
    }

    public ChatMember(long chatId, long userId, String firstName, String lastName, String username) {
        this.id = new ChatMemberId(chatId, userId);
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.registeredAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public ChatMemberId getId() {
        return id;
    }

    public long getChatId() {
        return id.chatId;
    }

    public long getUserId() {
        return id.userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String toMentionHtml() {
        if (username != null && !username.isBlank()) {
            return "@" + escapeHtml(username);
        }
        String name = firstName != null ? firstName : "user";
        if (lastName != null && !lastName.isBlank()) {
            name += " " + lastName;
        }
        return "<a href=\"tg://user?id=" + getUserId() + "\">" + escapeHtml(name) + "</a>";
    }

    private static String escapeHtml(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    @Embeddable
    public static class ChatMemberId implements Serializable {

        @Column(name = "chat_id")
        public long chatId;

        @Column(name = "user_id")
        public long userId;

        public ChatMemberId() {
        }

        public ChatMemberId(long chatId, long userId) {
            this.chatId = chatId;
            this.userId = userId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ChatMemberId other)) return false;
            return chatId == other.chatId && userId == other.userId;
        }

        @Override
        public int hashCode() {
            return Long.hashCode(chatId) * 31 + Long.hashCode(userId);
        }
    }
}