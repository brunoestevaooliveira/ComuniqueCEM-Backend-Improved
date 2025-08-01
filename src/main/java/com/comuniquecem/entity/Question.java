package com.comuniquecem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Entidade representando uma questão de quiz
 */
@Entity
@Table(name = "questions", indexes = {
    @Index(name = "idx_question_institution", columnList = "institution_id")
})
public class Question extends BaseEntity {

    @NotBlank(message = "Título da questão é obrigatório")
    @Size(min = 10, max = 500, message = "Título deve ter entre 10 e 500 caracteres")
    @Column(name = "title", nullable = false, columnDefinition = "TEXT")
    private String title;

    @NotBlank(message = "Primeira opção é obrigatória")
    @Size(max = 200, message = "Opção deve ter no máximo 200 caracteres")
    @Column(name = "option_1", nullable = false)
    private String option1;

    @NotBlank(message = "Segunda opção é obrigatória")
    @Size(max = 200, message = "Opção deve ter no máximo 200 caracteres")
    @Column(name = "option_2", nullable = false)
    private String option2;

    @NotBlank(message = "Terceira opção é obrigatória")
    @Size(max = 200, message = "Opção deve ter no máximo 200 caracteres")
    @Column(name = "option_3", nullable = false)
    private String option3;

    @NotBlank(message = "Quarta opção é obrigatória")
    @Size(max = 200, message = "Opção deve ter no máximo 200 caracteres")
    @Column(name = "option_4", nullable = false)
    private String option4;

    @NotBlank(message = "Resposta correta é obrigatória")
    @Column(name = "correct_answer", nullable = false)
    private String correctAnswer;

    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "difficulty_level")
    private Integer difficultyLevel = 1; // 1-5 scale

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "answer_count", nullable = false)
    private Long answerCount = 0L;

    @Column(name = "correct_count", nullable = false)
    private Long correctCount = 0L;

    // Relacionamentos
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    // Constructors
    public Question() {}

    public Question(String title, String option1, String option2, String option3, 
                   String option4, String correctAnswer, Institution institution, User author) {
        this.title = title;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.correctAnswer = correctAnswer;
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

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public String getOption3() {
        return option3;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    public String getOption4() {
        return option4;
    }

    public void setOption4(String option4) {
        this.option4 = option4;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public Integer getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(Integer difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Long getAnswerCount() {
        return answerCount;
    }

    public void setAnswerCount(Long answerCount) {
        this.answerCount = answerCount;
    }

    public Long getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(Long correctCount) {
        this.correctCount = correctCount;
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
    public boolean isCorrectAnswer(String answer) {
        return this.correctAnswer.equals(answer);
    }

    public void recordAnswer(boolean correct) {
        this.answerCount++;
        if (correct) {
            this.correctCount++;
        }
    }

    public double getSuccessRate() {
        return answerCount > 0 ? (double) correctCount / answerCount * 100 : 0.0;
    }

    public boolean canBeEditedBy(User user) {
        return this.author.equals(user) || 
               user.getRole().name().contains("ADMIN");
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + getId() +
                ", title='" + title.substring(0, Math.min(title.length(), 50)) + "..." + '\'' +
                ", difficultyLevel=" + difficultyLevel +
                ", successRate=" + getSuccessRate() + "%" +
                '}';
    }
}
