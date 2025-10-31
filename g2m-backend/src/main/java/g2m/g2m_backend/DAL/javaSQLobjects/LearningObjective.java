package g2m.DAL.javaSQLobjects;

public class LearningObjective {
    private int objectiveId;
    private String objectiveName;
    private String objDescript;

    //Constructor
    public LearningObjective(int objectiveId, String objectiveName, String objDescript)
    {
        this.objectiveId = objectiveId;
        this.objectiveName = objectiveName;
        this.objDescript = objDescript;
    }

    //Setters & Getters
    
    public int getobjectiveId() {
        return objectiveId;
    }

    public void setobjectiveId(int objectiveId) {
        this.objectiveId = objectiveId;
    }

    public String getobjectiveName() {
        return objectiveName;
    }

    public void setobjectiveName(String objectiveName) {
        this.objectiveName = objectiveName;
    }

    public String getobjDescript() {
        return objDescript;
    }

    public void setobjDescript(String objDescript) {
        this.objDescript = objDescript;
    }

    public String toString(){
        String toReturn = "";
        toReturn += "Objective objectiveName: " + objectiveName;
        toReturn += "\nObjective ID: " + objectiveId;
        toReturn += "\nobjDescript: " + objDescript;

        return toReturn;
    }
}

