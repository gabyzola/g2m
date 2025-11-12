package g2m.g2m_backend.DAL.javaSQLobjects;

import java.time.LocalDateTime;

public class Classroom {
    private int classId;
    private String className;
    private int instructorId;
    private LocalDateTime created_at;

    //Constructor
    public Classroom(int classId, String className, int instructorId, LocalDateTime created_at)
    {
        this.classId = classId;
        this.className = className;
        this.instructorId = instructorId;
        this.created_at = created_at;
    }

    //Setters & Getters
    
    public int getclassId() {
        return classId;
    }

    public void setclassId(int classId) {
        this.classId = classId;
    }

    public String getclassName() {
        return className;
    }

    public void setclassName(String className) {
        this.className = className;
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

    public String toString(){
        String toReturn = "";
        toReturn += "\nclass ID: " + classId;
        toReturn += "\nclassName: " + className;
        toReturn += "\ninstructorId: " + instructorId;
        toReturn += "\ncreated_at: $" + created_at;

        return toReturn;
    }
}

