package g2m.g2m_backend.DAL.javaSQLobjects;

import java.time.LocalDateTime;

public class Quiz {
    private int quizId;
    private String quizName;
    private int instructorId;
    private LocalDateTime created_at;
    private int classId;

    public Quiz(int quizId, String quizName, int instructorId, LocalDateTime created_at, int classId)
    {
        this.quizId = quizId;
        this.quizName = quizName;
        this.instructorId = instructorId;
        this.created_at = created_at;
        this.classId = classId;
    }
    
    public int getquizId() {
        return quizId;
    }

    public void setquizId(int quizId) {
        this.quizId = quizId;
    }

    public String getquizName() {
        return quizName;
    }

    public void setquizName(String quizName) {
        this.quizName = quizName;
    }

    public int getinstructorId() {
        return instructorId;
    }

    public void setinstructorId(int instructorId) {
        this.instructorId = instructorId;
    }

    public LocalDateTime getcreated_at() {
        return created_at;
    }

    public void setcreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public int getclassId() {
        return classId;
    }

    public void setclassId(int classId) {
        this.classId = classId;
    } 

    public String toString(){
        String toReturn = "";
        toReturn += "Quiz Name: " + quizName;
        toReturn += "\nQuiz ID: " + quizId;
        toReturn += "\ninstructorId: " + instructorId;
        toReturn += "\ncreated at: " + created_at;
        toReturn += "\nclassId: $" + classId;

        return toReturn;
    }
}
