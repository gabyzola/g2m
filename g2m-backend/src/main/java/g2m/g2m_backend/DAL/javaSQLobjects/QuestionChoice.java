package g2m.g2m_backend.DAL.javaSQLobjects;

public class QuestionChoice {
    private int choiceId;
    private int questionId;
    private char choiceLabel;
    private String choiceText;

    public QuestionChoice(int choiceId, int questionId, char choiceLabel, String choiceText)
    {
        this.choiceId = choiceId;
        this.questionId = questionId;
        this.choiceLabel=choiceLabel;
        this.choiceText=choiceText;
    }
    
    public int getchoiceId() {
        return choiceId;
    }

    public void setchoiceId(int choiceId) {
        this.choiceId = choiceId;
    }

    public int getquestionId() {
        return questionId;
    }

    public void setquestionId(int questionId) {
        this.questionId = questionId;
    }

    public char getChoiceLabel() {
        return choiceLabel;
    }

    public void setChoiceLabel(char choiceLabel) {
        this.choiceLabel = choiceLabel;
    }

    public String getChoiceText() {
        return choiceText;
    }

    public void setChoiceText(String choiceText) {
        this.choiceText = choiceText;
    }

    public String toString(){
        String toReturn = "";
        toReturn += "\nChoice ID: " + choiceId;
        toReturn += "\nquestionId: " + questionId;
        toReturn += "\nChoice label: " + choiceLabel;
        toReturn += "\nChoice text: " + choiceText;

        return toReturn;
    }
}

