package g2m.g2m_backend.DAL.javaSQLobjects;

public class Badge {
    private int badgeId;
    private String badgeName;
    private int pointThreshold;

    //Constructor
    public Badge(int badgeId, String badgeName, int pointThreshold)
    {
        this.badgeId = badgeId;
        this.badgeName = badgeName;
        this.pointThreshold = pointThreshold;
    }

    //Setters & Getters
    
    public int getBadgeId() {
        return badgeId;
    }

    public void setBadgeId(int badgeId) {
        this.badgeId = badgeId;
    }

    public String getBadgeName() {
        return badgeName;
    }

    public void setBadgeName(String badgeName) {
        this.badgeName = badgeName;
    }

    public int getPointThreshold() {
        return pointThreshold;
    }

    public void setPointThreshold(int pointThreshold) {
        this.pointThreshold = pointThreshold;
    }


    public String toString(){
        String toReturn = "";
        toReturn += "Badge badgeName: " + badgeName;
        toReturn += "\nBadge ID: " + badgeId;
        toReturn += "\npointThreshold: " + pointThreshold;

        return toReturn;
    }
}

