package com.comuniquecem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Entidade representando uma notícia
 */
@Entity
@Table(name = "news", indexes = {
    @Index(name = "idx_news_institution", columnList = "institution_id"),
    @Index(name = "idx_news_created_at", columnList = "created_at")
})
public class News extends BaseEntity {

    @NotBlank(message = "Título é obrigatório")
    @Size(min = 5, max = 200, message = "Título deve ter entre 5 e 200 caracteres")
    @Column(name = "title", nullable = false)
    private String title;

    @NotBlank(message = "Conteúdo é obrigatório")
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "published", nullable = false)
    private Boolean published = false;

    @Column(name = "featured", nullable = false)
    private Boolean featured = false;

    @Column(name = "views", nullable = false)
    private Long views = 0L;

    // Relacionamentos
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    // Constructors
    public News() {}

    public News(String title, String content, Institution institution, User author) {
        this.title = title;
        this.content = content;
        this.institution = institution;
        this.author = author;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public Boolean getFeatured() {
        return featured;
    }

    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }

    public Long getViews() {
        return views;
    }

    public void setViews(Long views) {
        this.views = views;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    // Helper methods
    public void incrementViews() {
        this.views++;
    }

    public boolean canBeEditedBy(User user) {
        return this.author.equals(user) || 
               user.getRole().name().contains("ADMIN");
    }

    @Override
    public String toString() {
        return "News{" +
                "id=" + getId() +
                ", title='" + title + '\'' +
                ", published=" + published +
                ", views=" + views +
                '}';
    }
}
