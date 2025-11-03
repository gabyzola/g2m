package g2m.g2m_backend.DAL.javaSQLobjects;

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
    
    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
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
