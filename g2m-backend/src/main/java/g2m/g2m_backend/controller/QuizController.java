package g2m.g2m_backend.controller;

import g2m.g2m_backend.DAL.QuizDal;
import g2m.g2m_backend.business.BusinessLogic;
import g2m.g2m_backend.DAL.javaSQLobjects.QuestionData;
import g2m.g2m_backend.DAL.javaSQLobjects.Student;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Allow frontend JS calls (like from React or plain HTML)
public class QuizController {

    private final BusinessLogic bl;
    Scanner in = new Scanner(System.in); //not sure if i need this but it gets rid of errors

    public QuizController() {
        QuizDal dal = new QuizDal();
        this.bl = new BusinessLogic(dal);
    }

    // Register a new user: Ready to connect
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

    //create class: Ready to connect
    @PostMapping("/class")
    public boolean createClass(@RequestBody Map<String, Object>data) {
        int classId = (int) data.get("classId");
        String className = (String) data.get("className");
        String instructorEmail = (String) data.get("instructorEmail");
        return bl.createClass(classId, className, instructorEmail);
    }

    //display a list of instructor classes: Ready to connect
    @GetMapping("/instructors/{instructorId}/classes")
    public void viewInstructorClasses(@PathVariable int instructorId) {
        bl.viewInstructorClasses(instructorId);
    }

    // Enroll student: NOT ready
    @PostMapping("/enroll")
    public boolean enrollStudent(@RequestBody Map<String, Object> data) {
        int classId = (int) data.get("classId");
        String email = (String) data.get("email");
        return bl.enrollStudentInClass(classId, email);
    }

    //List enrollees in a class: NOT ready 
    @GetMapping("/classes/{classId}/enrollees")
        public void viewClassEnrollees(@PathVariable int classId) {
        bl.viewClassEnrollees(classId);
    }

    //display student classes: Ready to connect
    @GetMapping("/students/{studentId}/classes")
    public void viewStudentClasses(@PathVariable int studentId) {
        bl.viewStudentClasses(studentId);
    }

    //upload reading: NOT ready
    @PostMapping("/classes/{classId}/readings")
    public boolean uploadReading(@PathVariable int classId, @RequestBody Map<String, Object> data) {
        int instructorId = (int) data.get("instructorId");
        String readingName = (String) data.get("readingName");
        String filePath = (String) data.get("filePath");
        return bl.uploadReading(instructorId, classId, readingName, filePath);
    }

    //create quiz: NOT ready
    @PostMapping("/classes/{classId}/quiz")
    public boolean createQuiz(@PathVariable int classId, @RequestBody Map<String, Object> data) {
        String quizName = (String) data.get("quizName");
        int instructorId = (int) data.get("instructorId");
        List<Integer> readingIds = (List<Integer>) data.get("readingIds");
        List<QuestionData> questions = (List<QuestionData>) data.get("questions");
        return bl.createQuiz(quizName, instructorId, classId, readingIds, questions);
    }

    //view quizzes for a class: Ready to connect
    @GetMapping("/classes/{classId}/quizzes")
    public void viewQuizzesByClass(@PathVariable int classId) {
        bl.viewQuizzesByClass(classId);
    }

    // Search students: Ready to connect
    @GetMapping("/students/search")
    public ArrayList<Student> searchStudent(@RequestParam String type, @RequestParam String query) {
        return bl.searchStudent(type, query);
    }

    //display relavant learning objectives: NOT ready
    @GetMapping("/quizzes/{quizId}/objectives")
    public void viewObjectivesByQuiz(@PathVariable int quizId) {
        bl.viewObjectivesByQuiz(quizId);
    }

    //take quiz: NOT ready
    @PostMapping("/quizzes/{quizId}/take/{studentId}")
    public void takeQuiz(@PathVariable int studentId, @PathVariable int quizId) {
        Scanner in = new Scanner(System.in);
        bl.takeQuiz(studentId, quizId, in);
    }

    //display student badges: NOT ready
    @GetMapping("/students/{studentId}/badges")
    public void viewStudentBadges(@PathVariable int studentId) {
        bl.displayStudentBadges(studentId);
    }

    //display all badges: Ready to connect
    public void viewAllBadges() {
        bl.displayAllBadges();
    }

   
}
