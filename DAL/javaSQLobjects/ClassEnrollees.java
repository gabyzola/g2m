package g2m.DAL.javaSQLobjects;

public class ClassEnrollees {
    private int classId;
    private int studentId;
    private String enrolled_at;

    //Constructor
    public ClassEnrollees(int classId, int studentId,  String enrolled_at)
    {
        this.classId = classId;
        this.studentId = studentId;
        this.enrolled_at = enrolled_at;
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

    public String getenrolled_at() {
        return enrolled_at;
    }

    public void setenrolled_at(String enrolled_at) {
        this.enrolled_at = enrolled_at;
    }

    public String toString(){
        String toReturn = "";
        toReturn += "Student studentId: " + studentId;
        toReturn += "\nClass ID: " + classId;
        toReturn += "\nenrolled_at: " + enrolled_at;

        return toReturn;
    }
    
}
