package com.comuniquecem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Entidade representando uma mensagem no chat
 */
@Entity
@Table(name = "messages", indexes = {
    @Index(name = "idx_message_chat", columnList = "chat_id"),
    @Index(name = "idx_message_sender", columnList = "sender_id"),
    @Index(name = "idx_message_sent_at", columnList = "sent_at")
})
public class Message extends BaseEntity {

    @NotBlank(message = "Conteúdo da mensagem é obrigatório")
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @NotNull(message = "Data de envio é obrigatória")
    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    @Column(name = "delivered", nullable = false)
    private Boolean delivered = false;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "read", nullable = false)
    private Boolean read = false;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "is_file", nullable = false)
    private Boolean isFile = false;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "edited", nullable = false)
    private Boolean edited = false;

    @Column(name = "edited_at")
    private LocalDateTime editedAt;

    // Relacionamentos
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_to_id")
    private Message replyTo; // Para responder a uma mensagem específica

    // Constructors
    public Message() {}

    public Message(String content, User sender, Chat chat) {
        this.content = content;
        this.sender = sender;
        this.chat = chat;
        this.sentAt = LocalDateTime.now();
    }

    public Message(String fileName, String fileUrl, Long fileSize, String fileType, 
                  User sender, Chat chat) {
        this.content = fileName; // Para arquivos, o content será o nome do arquivo
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.isFile = true;
        this.sender = sender;
        this.chat = chat;
        this.sentAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public Boolean getDelivered() {
        return delivered;
    }

    public void setDelivered(Boolean delivered) {
        this.delivered = delivered;
        if (delivered && deliveredAt == null) {
            this.deliveredAt = LocalDateTime.now();
        }
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
        if (read && readAt == null) {
            this.readAt = LocalDateTime.now();
            // Auto-mark as delivered when read
            if (!delivered) {
                setDelivered(true);
            }
        }
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    public Boolean getIsFile() {
        return isFile;
    }

    public void setIsFile(Boolean isFile) {
        this.isFile = isFile;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
        if (deleted && deletedAt == null) {
            this.deletedAt = LocalDateTime.now();
        }
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Boolean getEdited() {
        return edited;
    }

    public void setEdited(Boolean edited) {
        this.edited = edited;
        if (edited && editedAt == null) {
            this.editedAt = LocalDateTime.now();
        }
    }

    public LocalDateTime getEditedAt() {
        return editedAt;
    }

    public void setEditedAt(LocalDateTime editedAt) {
        this.editedAt = editedAt;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public Message getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(Message replyTo) {
        this.replyTo = replyTo;
    }

    // Helper methods
    public boolean canBeEditedBy(User user) {
        return this.sender.equals(user) && !deleted && 
               sentAt.isAfter(LocalDateTime.now().minusMinutes(15)); // Só pode editar por 15 min
    }

    public boolean canBeDeletedBy(User user) {
        return this.sender.equals(user) || 
               user.getRole().name().contains("ADMIN");
    }

    public String getFormattedFileSize() {
        if (fileSize == null) return "";
        
        if (fileSize < 1024) return fileSize + " B";
        if (fileSize < 1024 * 1024) return String.format("%.1f KB", fileSize / 1024.0);
        if (fileSize < 1024 * 1024 * 1024) return String.format("%.1f MB", fileSize / (1024.0 * 1024));
        return String.format("%.1f GB", fileSize / (1024.0 * 1024 * 1024));
    }

    public boolean isImage() {
        return isFile && fileType != null && 
               (fileType.startsWith("image/") || 
                fileType.matches(".*\\.(jpg|jpeg|png|gif|bmp|webp)$"));
    }

    public boolean isDocument() {
        return isFile && fileType != null && 
               (fileType.contains("pdf") || 
                fileType.contains("document") || 
                fileType.contains("text") ||
                fileType.matches(".*\\.(pdf|doc|docx|txt|rtf)$"));
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + getId() +
                ", sender=" + sender.getName() +
                ", isFile=" + isFile +
                ", delivered=" + delivered +
                ", read=" + read +
                ", deleted=" + deleted +
                ", sentAt=" + sentAt +
                '}';
    }
}
