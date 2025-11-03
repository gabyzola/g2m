package g2m.g2m_backend.controller;

import g2m.g2m_backend.DAL.QuizDal;
import g2m.g2m_backend.business.BusinessLogic;
import g2m.g2m_backend.DAL.javaSQLobjects.Student;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Allow frontend JS calls (like from React or plain HTML)
public class QuizController {

    private final BusinessLogic bl;

    public QuizController() {
        QuizDal dal = new QuizDal();
        this.bl = new BusinessLogic(dal);
    }

    // Example: Register a new user
    @PostMapping("/register")
    public boolean registerUser(@RequestBody Map<String, Object> data) {
        String username = (String) data.get("username");
        String email = (String) data.get("email");
        boolean isInstructor = (boolean) data.get("isInstructor");
        String major = (String) data.get("major");
        String subject = (String) data.get("schoolSubject");
        String firstName = (String) data.get("firstName");
        String lastName = (String) data.get("lastName");

        return bl.registerUser(username, email, isInstructor, major, subject, firstName, lastName);
    }

    // Example: Search students
    @GetMapping("/students/search")
    public ArrayList<Student> searchStudent(@RequestParam String type, @RequestParam String query) {
        return bl.searchStudent(type, query);
    }

    // Example: Enroll student
    @PostMapping("/enroll")
    public boolean enrollStudent(@RequestBody Map<String, Object> data) {
        int classId = (int) data.get("classId");
        String email = (String) data.get("email");
        return bl.enrollStudentInClass(classId, email);
    }

    //Not done yet
}
