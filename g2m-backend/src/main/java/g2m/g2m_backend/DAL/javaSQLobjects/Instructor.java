package g2m.g2m_backend.DAL.javaSQLobjects;

public class Instructor {
    private int instructorId;
    private String schoolSubject;

    public Instructor(int instructorId, String schoolSubject)
    {
        this.instructorId = instructorId;
        this.schoolSubject = schoolSubject;
    }
    
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

