package g2m.DAL.javaSQLobjects;

public class Badge {
    private int badgeId;
    private String badgeName;
    private String description;
    private int pointThreshold;

    //Constructor
    public Badge(int badgeId, String badgeName, String description, int pointThreshold)
    {
        this.badgeId = badgeId;
        this.badgeName = badgeName;
        this.description = description;
        this.pointThreshold = pointThreshold;
    }

    //Setters & Getters
    
    public int getBadgeId() {
        return badgeId;
    }

    public void setbadgeId(int badgeId) {
        this.badgeId = badgeId;
    }

    public String getbadgeName() {
        return badgeName;
    }

    public void setbadgeName(String badgeName) {
        this.badgeName = badgeName;
    }

    public String getdescription() {
        return description;
    }

    public void setdescription(String description) {
        this.description = description;
    }

    public int getpointThreshold() {
        return pointThreshold;
    }

    public void setpointThreshold(int pointThreshold) {
        this.pointThreshold = pointThreshold;
    }


    public String toString(){
        String toReturn = "";
        toReturn += "Badge badgeName: " + badgeName;
        toReturn += "\nBadge ID: " + badgeId;
        toReturn += "\ndescription: " + description;
        toReturn += "\npointThreshold: " + pointThreshold;

        return toReturn;
    }
}
