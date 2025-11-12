package g2m.g2m_backend.DAL.javaSQLobjects;

public class Attempts {
    private int attemptId;
    private int studentId;
    private int quizId;
    private int questionId;
    private int chosenChoiceId;
    private boolean isCorrect;
    private int pointsEarned;

    //Constructor
    public Attempts(int attemptId, int studentId, int quizId, int questionId, int chosenChoiceId, boolean isCorrect, int pointsEarned)
    {
        this.attemptId = attemptId;
        this.studentId = studentId;
        this.quizId = quizId;
        this.questionId  = questionId;
        this.chosenChoiceId=chosenChoiceId;
        this.isCorrect=isCorrect;
        this.pointsEarned=pointsEarned;
    }

    //Setters & Getters
    
    public int getattemptId() {
        return attemptId;
    }

    public void setattemptId(int attemptId) {
        this.attemptId = attemptId;
    }

    public int getstudentId() {
        return studentId;
    }

    public void setstudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getquizId() {
        return quizId;
    }

    public void setquizId(int quizId) {
        this.quizId = quizId;
    }

    public int getquestionId () {
        return questionId ;
    }

    public void setquestionId (int questionId ) {
        this.questionId  = questionId ;
    }

    public int getChosenChoiceId () {
        return questionId ;
    }

    public void setChosenChoiceId (int chosenChoiceId) {
        this.chosenChoiceId  = chosenChoiceId;
    }

    public boolean getIsCorrect () {
        return isCorrect;
    }

    public void setIsCorrect (boolean isCorrect) {
        this.isCorrect  = isCorrect;
    }

    public int getPointsEarned () {
        return pointsEarned;
    }

    public void setPointsEarned (int pointsEarned) {
        this.pointsEarned  = pointsEarned;
    }

    public String toString(){
        String toReturn = "";
        toReturn += "Student studentId: " + studentId;
        toReturn += "\nAttempt ID: " + attemptId;
        toReturn += "\nquizId: " + quizId;
        toReturn += "\nquestionId : " + questionId ;
        toReturn += "\nChosen Choice Id : " + chosenChoiceId ;
        toReturn += "\nCorrect? : " + isCorrect ;
        toReturn += "\npoints earned : " + pointsEarned ;

        return toReturn;
    }
}

