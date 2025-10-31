package g2m.DAL.javaSQLobjects;

public class classEnrollee {
    private int classId;
    private int studentId;

    //Constructor
    public classEnrollee(int classId, int studentId)
    {
        this.classId = classId;
        this.studentId = studentId;
    }

    //Setters & Getters
    
    public int getclassId() {
        return classId;
    }

    public void setclassId(int classId) {
        this.classId = classId;
    }

    public int getstudentId() {
        return studentId;
    }

    public void setstudentId(int studentId) {
        this.studentId = studentId;
    }

    public String toString(){
        String toReturn = "";
        toReturn += "Student studentId: " + studentId;
        toReturn += "\nClass ID: " + classId;

        return toReturn;
    }
    
}

