package g2m.DAL.javaSQLobjects;

public class Question {
    private int questionId;
    private String questionText;
    private int quizId;
    private String difficulty;
    private int correctChoiceId;

    //Constructor
    public Question(int questionId, String questionText, int quizId, String difficulty, int correctChoiceId)
    {
        this.questionId = questionId;
        this.questionText = questionText;
        this.quizId = quizId;
        this.difficulty = difficulty;
        this.correctChoiceId = correctChoiceId;
    }

    //Setters & Getters
    
    public int getquestionId() {
        return questionId;
    }

    public void setquestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getquestionText() {
        return questionText;
    }

    public void setquestionText(String questionText) {
        this.questionText = questionText;
    }

    public int getquizId() {
        return quizId;
    }

    public void setquizId(int quizId) {
        this.quizId = quizId;
    }

    public String getdifficulty() {
        return difficulty;
    }

    public void setdifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public int getcorrectChoiceId() {
        return correctChoiceId;
    }

    public void setcorrectChoiceId(int correctChoiceId) {
        this.correctChoiceId = correctChoiceId;
    } 

    public String toString(){
        String toReturn = "";
        toReturn += "Question questionText: " + questionText;
        toReturn += "\nQuestion ID: " + questionId;
        toReturn += "\nquizId: " + quizId;
        toReturn += "\ndifficulty: " + difficulty;
        toReturn += "\ncorrectChoiceId: " + correctChoiceId;

        return toReturn;
    }
}

