package g2m.DAL.javaSQLobjects;

public class StudentBadges {
    private int studentId;
    private int badgeId;

    //Constructor
    public StudentBadges(int studentId, int badgeId)
    {
        this.studentId = studentId;
        this.badgeId = badgeId;
    }

    //Setters & Getters
    
    public int getstudentId() {
        return studentId;
    }

    public void setstudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getbadgeId() {
        return badgeId;
    }

    public void setbadgeId(int badgeId) {
        this.badgeId = badgeId;
    }

    public String toString(){
        String toReturn = "";
        toReturn += "\nStudent ID: " + studentId;
        toReturn += "\nbadgeId: " + badgeId;

        return toReturn;
    }
}
