package g2m.DAL.javaSQLobjects;

public class Instructor {
    private int instructorId;
    private String schoolSubject;

    //Constructor
    public Instructor(int instructorId, String schoolSubject)
    {
        this.instructorId = instructorId;
        this.schoolSubject = schoolSubject;
    }

    //Setters & Getters
    
    public int getinstructorId() {
        return instructorId;
    }

    public void setinstructorId(int instructorId) {
        this.instructorId = instructorId;
    }

    public String getschoolSubject() {
        return schoolSubject;
    }

    public void setschoolSubject(String schoolSubject) {
        this.schoolSubject = schoolSubject;
    }


    public String toString(){
        String toReturn = "";
        toReturn += "School Subject: " + schoolSubject;
        toReturn += "\nInstructor ID: " + instructorId;

        return toReturn;
    }
}
