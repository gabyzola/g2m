package g2m.DAL.javaSQLobjects;

public class Student {
    private int studentId;
    private String badge;
    private int totalPoints;
    private String major;

    //Constructor
    public Student(int studentId, String badge, int totalPoints, String major)
    {
        this.studentId = studentId;
        this.badge = badge;
        this.totalPoints = totalPoints;
        this.major = major;
    }

    //Setters & Getters
    
    public int getstudentId() {
        return studentId;
    }

    public void setstudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getbadge() {
        return badge;
    }

    public void setbadge(String badge) {
        this.badge = badge;
    }

    public int gettotalPoints() {
        return totalPoints;
    }

    public void settotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public String getmajor() {
        return major;
    }

    public void setmajor(String major) {
        this.major = major;
    }

    public String toString(){
        String toReturn = "";
        toReturn += "Badge: " + badge;
        toReturn += "\nStudent ID: " + studentId;
        toReturn += "\ntotalPoints: " + totalPoints;
        toReturn += "\nmajor: " + major;

        return toReturn;
    }
    

}
