package g2m.DAL.javaSQLobjects;

public class QuestionObjectives {
    private int questionId;
    private int objectiveId;

    //Constructor
    public QuestionObjectives(int questionId, int objectiveId)
    {
        this.questionId = questionId;
        this.objectiveId = objectiveId;
    }

    //Setters & Getters
    
    public int getquestionId() {
        return questionId;
    }

    public void setquestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getobjectiveId() {
        return objectiveId;
    }

    public void setobjectiveId(int objectiveId) {
        this.objectiveId = objectiveId;
    }

    public String toString(){
        String toReturn = "";
        toReturn += "\nQuestion ID: " + questionId;
        toReturn += "\nobjectiveId: " + objectiveId;

        return toReturn;
    }
    
}

