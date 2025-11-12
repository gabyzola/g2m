package g2m.g2m_backend.DAL.javaSQLobjects;

public class QuestionData {
    private String questionText;
    private String difficulty;
    private String choiceA, choiceB, choiceC, choiceD;
    private char correctAnswer;
    private int objectiveId;
    private int quizId; //

    public QuestionData(String questionText, String difficulty,
                        String choiceA, String choiceB, String choiceC, String choiceD,
                        char correctAnswer, int objectiveId, int quizId) {
        this.questionText = questionText;
        this.difficulty = difficulty;
        this.choiceA = choiceA;
        this.choiceB = choiceB;
        this.choiceC = choiceC;
        this.choiceD = choiceD;
        this.correctAnswer = correctAnswer;
        this.objectiveId = objectiveId;
        this.quizId = quizId;
    }

    //getters
    public String getQuestionText() { return questionText; }
    public String getDifficulty() { return difficulty; }
    public String getChoiceA() { return choiceA; }
    public String getChoiceB() { return choiceB; }
    public String getChoiceC() { return choiceC; }
    public String getChoiceD() { return choiceD; }
    public char getCorrectAnswer() { return correctAnswer; }
    public int getObjectiveId() { return objectiveId; }
    public int getQuizId() { return quizId; }
}
