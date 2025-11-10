package g2m.g2m_backend.DAL.javaSQLobjects;
import g2m.g2m_backend.business.DifficultyLevel;

import java.util.List;

public class QuizQuestion {
    private int questionId;
    private int questionNumber;
    private String questionText;
    private String learningObjective;
    private DifficultyLevel difficulty;
    private List<Choice> choices;

    public QuizQuestion() {}

    public QuizQuestion(int questionId, int questionNumber, String questionText, String learningObjective, DifficultyLevel difficulty, List<Choice> choices) {
        this.questionId = questionId;
        this.questionNumber = questionNumber;
        this.questionText = questionText;
        this.learningObjective = learningObjective;
        this.difficulty = difficulty;
        this.choices = choices;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getLearningObjective() {
        return learningObjective;
    }

    public void setLearningObjective(String learningObjective) {
        this.learningObjective = learningObjective;
    }

    public DifficultyLevel getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(DifficultyLevel difficulty) {
        this.difficulty = difficulty;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    //created a new class for choices
    public static class Choice {
        private int choiceId;
        private String choiceLabel;
        private String choiceText;

        public Choice() {}

        public Choice(int choiceId, String choiceLabel, String choiceText) {
            this.choiceId = choiceId;
            this.choiceLabel = choiceLabel;
            this.choiceText = choiceText;
        }

        public int getChoiceId() {
            return choiceId;
        }

        public void setChoiceId(int choiceId) {
            this.choiceId = choiceId;
        }

        public String getChoiceLabel() {
            return choiceLabel;
        }

        public void setChoiceLabel(String choiceLabel) {
            this.choiceLabel = choiceLabel;
        }

        public String getChoiceText() {
            return choiceText;
        }

        public void setChoiceText(String choiceText) {
            this.choiceText = choiceText;
        }
    }
}
