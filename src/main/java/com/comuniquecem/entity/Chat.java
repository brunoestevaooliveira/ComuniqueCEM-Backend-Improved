package com.comuniquecem.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Entidade representando uma conversa entre dois usuários
 */
@Entity
@Table(name = "chats", indexes = {
    @Index(name = "idx_chat_users", columnList = "user1_id, user2_id"),
    @Index(name = "idx_chat_user1", columnList = "user1_id"),
    @Index(name = "idx_chat_user2", columnList = "user2_id")
})
public class Chat extends BaseEntity {

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "last_message_at")
    private java.time.LocalDateTime lastMessageAt;

    // Relacionamentos
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1_id", nullable = false)
    private User user1; // Usuário que iniciou a conversa

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2_id", nullable = false)
    private User user2; // Usuário que recebeu o convite

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Message> messages = new HashSet<>();

    // Constructors
    public Chat() {}

    public Chat(User user1, User user2) {
        this.user1 = user1;
        this.user2 = user2;
        this.lastMessageAt = java.time.LocalDateTime.now();
    }

    // Getters and Setters
    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public java.time.LocalDateTime getLastMessageAt() {
        return lastMessageAt;
    }

    public void setLastMessageAt(java.time.LocalDateTime lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    public Set<Message> getMessages() {
        return messages;
    }

    public void setMessages(Set<Message> messages) {
        this.messages = messages;
    }

    // Helper methods
    public boolean includesUser(User user) {
        return user1.equals(user) || user2.equals(user);
    }

    public User getOtherUser(User currentUser) {
        return user1.equals(currentUser) ? user2 : user1;
    }

    public void addMessage(Message message) {
        this.messages.add(message);
        message.setChat(this);
        this.lastMessageAt = java.time.LocalDateTime.now();
    }

    public boolean canBeAccessedBy(User user) {
        return includesUser(user);
    }

    public long getUnreadCount(User user) {
        return messages.stream()
                .filter(message -> !message.getSender().equals(user))
                .filter(message -> !message.getRead())
                .count();
    }

    @Override
    public String toString() {
        return "Chat{" +
                "id=" + getId() +
                ", user1=" + user1.getName() +
                ", user2=" + user2.getName() +
                ", active=" + active +
                ", lastMessageAt=" + lastMessageAt +
                '}';
    }
}
