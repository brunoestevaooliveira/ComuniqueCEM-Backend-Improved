package com.comuniquecem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Entidade representando um item de cronograma/agenda
 */
@Entity
@Table(name = "schedules", indexes = {
    @Index(name = "idx_schedule_user", columnList = "user_id"),
    @Index(name = "idx_schedule_date", columnList = "activity_date")
})
public class Schedule extends BaseEntity {

    @NotBlank(message = "Nome da atividade é obrigatório")
    @Size(min = 3, max = 200, message = "Nome da atividade deve ter entre 3 e 200 caracteres")
    @Column(name = "activity_name", nullable = false)
    private String activityName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Data da atividade é obrigatória")
    @Column(name = "activity_date", nullable = false)
    private LocalDate activityDate;

    @NotNull(message = "Prazo em dias é obrigatório")
    @Column(name = "deadline_days", nullable = false)
    private Integer deadlineDays;

    @NotBlank(message = "Cor é obrigatória")
    @Size(min = 4, max = 7, message = "Cor deve estar no formato hexadecimal (#RGB ou #RRGGBB)")
    @Column(name = "color", nullable = false)
    private String color;

    @Column(name = "completed", nullable = false)
    private Boolean completed = false;

    @Column(name = "priority")
    private Integer priority = 1; // 1-5 scale (1=baixa, 5=alta)

    @Column(name = "reminder_enabled", nullable = false)
    private Boolean reminderEnabled = false;

    @Column(name = "reminder_days_before")
    private Integer reminderDaysBefore = 1;

    // Relacionamentos
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Constructors
    public Schedule() {}

    public Schedule(String activityName, LocalDate activityDate, Integer deadlineDays, 
                   String color, User user) {
        this.activityName = activityName;
        this.activityDate = activityDate;
        this.deadlineDays = deadlineDays;
        this.color = color;
        this.user = user;
    }

    // Getters and Setters
    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(LocalDate activityDate) {
        this.activityDate = activityDate;
    }

    public Integer getDeadlineDays() {
        return deadlineDays;
    }

    public void setDeadlineDays(Integer deadlineDays) {
        this.deadlineDays = deadlineDays;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Boolean getReminderEnabled() {
        return reminderEnabled;
    }

    public void setReminderEnabled(Boolean reminderEnabled) {
        this.reminderEnabled = reminderEnabled;
    }

    public Integer getReminderDaysBefore() {
        return reminderDaysBefore;
    }

    public void setReminderDaysBefore(Integer reminderDaysBefore) {
        this.reminderDaysBefore = reminderDaysBefore;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // Helper methods
    public LocalDate getDeadlineDate() {
        return activityDate.plusDays(deadlineDays);
    }

    public boolean isOverdue() {
        return !completed && LocalDate.now().isAfter(getDeadlineDate());
    }

    public boolean isDueSoon() {
        LocalDate now = LocalDate.now();
        LocalDate deadline = getDeadlineDate();
        return !completed && 
               (now.isEqual(deadline) || 
                (now.isBefore(deadline) && now.plusDays(3).isAfter(deadline)));
    }

    public boolean canBeEditedBy(User user) {
        return this.user.equals(user);
    }

    public String getPriorityText() {
        return switch (priority) {
            case 1 -> "Muito Baixa";
            case 2 -> "Baixa";
            case 3 -> "Média";
            case 4 -> "Alta";
            case 5 -> "Muito Alta";
            default -> "Não definida";
        };
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "id=" + getId() +
                ", activityName='" + activityName + '\'' +
                ", activityDate=" + activityDate +
                ", completed=" + completed +
                ", priority=" + priority +
                '}';
    }
}
