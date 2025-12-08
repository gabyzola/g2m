package g2m.g2m_backend.DAL.javaSQLobjects;

public class User {

    private String googleSub;
    private int userId;
    private String email;
    private boolean isInstructor;
    private String firstName;
    private String lastName;

    // constructor
    public User(String googleSub, int userId, String email, boolean isInstructor,
                String firstName, String lastName) {
        this.googleSub=googleSub;
        this.userId = userId;
        this.email = email;
        this.isInstructor = isInstructor;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // getters
    public String getGoogleSub() { return googleSub; }
    public int getUserId() { return userId; }
    public String getEmail() { return email; }
    public boolean getIsInstructor() { return isInstructor; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }

    // setters (optional but useful)
    public void setGoogleSub(String googleSub) { this.googleSub = googleSub; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setEmail(String email) { this.email = email; }
    public void setIsInstructor(boolean isInstructor) { this.isInstructor = isInstructor; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
}
