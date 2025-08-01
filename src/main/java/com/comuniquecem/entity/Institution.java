package com.comuniquecem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

/**
 * Entidade representando uma instituição de ensino
 */
@Entity
@Table(name = "institutions")
public class Institution extends BaseEntity {

    @NotBlank(message = "Nome da instituição é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @NotBlank(message = "Código da instituição é obrigatório")
    @Size(min = 3, max = 20, message = "Código deve ter entre 3 e 20 caracteres")
    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "address")
    private String address;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @NotBlank(message = "Senha da instituição é obrigatória")
    @Size(min = 6, message = "Senha deve ter pelo menos 6 caracteres")
    @Column(name = "password", nullable = false)
    private String password;

    @NotBlank(message = "Senha dos professores é obrigatória")
    @Size(min = 6, message = "Senha dos professores deve ter pelo menos 6 caracteres")
    @Column(name = "teacher_password", nullable = false)
    private String teacherPassword;

    @Column(name = "description")
    private String description;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    // Relacionamentos
    @OneToMany(mappedBy = "institution", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();

    @OneToMany(mappedBy = "institution", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<News> news = new HashSet<>();

    @OneToMany(mappedBy = "institution", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Question> questions = new HashSet<>();

    // Constructors
    public Institution() {}

    public Institution(String name, String password, String teacherPassword) {
        this.name = name;
        this.password = password;
        this.teacherPassword = teacherPassword;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTeacherPassword() {
        return teacherPassword;
    }

    public void setTeacherPassword(String teacherPassword) {
        this.teacherPassword = teacherPassword;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public Set<News> getNews() {
        return news;
    }

    public void setNews(Set<News> news) {
        this.news = news;
    }

    public Set<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(Set<Question> questions) {
        this.questions = questions;
    }

    // Helper methods
    public void addUser(User user) {
        this.users.add(user);
        user.setInstitution(this);
    }

    public void removeUser(User user) {
        this.users.remove(user);
        user.setInstitution(null);
    }

    @Override
    public String toString() {
        return "Institution{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", active=" + active +
                '}';
    }
}
