//IGNORE THIS ITS FOR TESTING BUT ITS NO LONGER NEEDED, ONLY KEEPING FOR EMERGENCY

package g2m.g2m_backend.DAL;
import java.io.Console;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import g2m.g2m_backend.DAL.javaSQLobjects.QuestionData;
import g2m.g2m_backend.DAL.javaSQLobjects.Student;
import g2m.g2m_backend.business.BusinessLogic;

//all student ids will be automatically taken from their login info
//students must not have to enter their ids
public class PresentationLayer {
/* 
    /*
    Commands I still need to do:
    - Assign badge to student (after quiz completion, check if badge earned, if so assign badge)
    - View quizzes for a class (when student clicks on class, display quizzes associated with that class)
    - View Instructor Classes
    - Get all badges
    - Complete quiz process (machine learning is creating a slight block but im on it)
    

    // display main commands
    //these are commands available now
    public static void printCommands() {
        System.out.println("\n===== Available Commands =====");
        //working
        System.out.println("1. Register New User"); //login method
        //working
        System.out.println("2. Enroll Student in Class"); //enrolls student into class, called after instructor searches student by email when they click "Enroll Stduent"
        //working
        System.out.println("3. View Student Classes"); //for displaying a student's specific classes under the "Classes" section
        //NEEDS FIX
        System.out.println("4. Take a Quiz"); //for when a student clicks on a quiz to take it
        //needs testing
        System.out.println("5. View Student Badges"); //for displaying a student's specific badges under the "Badges" section
        //working
        System.out.println("6. Instructor: Create Class"); //instructor clicks "Create Class", when they click save all of the information they entered will be put through this procedure
        //needs testing
        System.out.println("7. Instructor: Create Quiz"); //same as above but for "Create Quiz"
        //needs testing
        System.out.println("8. Instructor: Add Question to Quiz"); //i might delete this...this should be integrated into Create Quiz
        //needs testing
        System.out.println("9. Search for a Student"); //for search results when enrolling students...instructors will search by email and the search query results should popup, then they can enroll the student
        //working
        System.out.println("10. Display All Badges"); //for displaying all available badges somewhere in the UI (so students know what they can work towards)
        //needs testing
        System.out.println("11. Get All Quizzes for a Class"); //when a student clicks on a class, display all quizzes associated with that class
        //needs testing
        System.out.println("12. Delete Enrollee From Class"); //instructor can delete a class enrollee
        //needs testing
        System.out.println("13. Instructor: Delete Class"); //instructor can remove a class
        System.out.println("Type 'exit' to quit.");
        System.out.println("==============================\n");
    }

    public static void main(String[] args) {

        //take username and password for db access
        Scanner in = new Scanner(System.in);
        System.out.print("Enter your username: ");
        String user = in.nextLine();

        //makes pw invisible
        Console console = System.console();
        String password;
        char[] passwordArray = console.readPassword("Enter your password: ");
        password = new String(passwordArray);
        //password = in.nextLine();
    

        QuizDal dal = new QuizDal();
        BusinessLogic logic = new BusinessLogic(dal);

        System.out.println("Welcome to MackAdapt!\n");

        printCommands();

        while (true) {
            System.out.print("Enter a command: ");
            String input = in.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Exiting program...");
                logic.closeConnection();
                break;
            }

            try {
                //fixed
                switch (input.toLowerCase()) {
                    case "1":
                    case "register new user":
                        System.out.print("Enter username: ");
                        String username = in.nextLine();
                        System.out.print("Enter email: ");
                        String email = in.nextLine();
                        System.out.print("Is this an instructor account? (Y/N): ");
                        boolean isInstructor = in.nextLine().equalsIgnoreCase("Y");
                        System.out.print(isInstructor);

                        System.out.print("Enter first name: ");
                        String firstName = in.nextLine();
                        System.out.print("Enter last name: ");
                        String lastName = in.nextLine();

                        System.out.print("Enter major (students) or subject (instructors): ");
                        String major = in.nextLine();
                        String subject = isInstructor ? major : null;
                        System.out.print(major);
                        System.out.print(subject);
                        System.out.print(firstName);
                        System.out.print(lastName);
                        System.out.print(email);
                        System.out.println(username);

                        boolean registered = logic.registerUser(username, email, isInstructor, major, subject, firstName, lastName);
                        System.out.println(registered ? "User successfully registered!" : "Registration failed.");
                        break;

                    case "2":
                    case "enroll student in class":
                        System.out.print("Enter class ID: ");
                        int classId = Integer.parseInt(in.nextLine());
                        System.out.print("Enter student email: ");
                        String studentEmail = in.nextLine();
                        boolean enrolled = logic.enrollStudentInClass(classId, studentEmail);
                        System.out.println(enrolled ? "Student enrolled successfully!" : "Enrollment failed.");
                        break;

                    case "3":
                        //for displaying student classes in UI (passes in studentid to get specific classes)
                    case "view student classes":
                        System.out.print("Enter student ID: ");
                        int studentId = Integer.parseInt(in.nextLine());
                        logic.viewStudentClasses(studentId);
                        break;

                    case "4": //there will be a display of quizzes, this case is for when the student clicks on a quiz to take
                    //quiz id will automatically get passed in, studentid will get passed in from login session
                    case "take a quiz":
                        System.out.print("Enter your student ID: ");
                        int sId = Integer.parseInt(in.nextLine());
                        System.out.print("Enter quiz ID: ");
                        int qId = Integer.parseInt(in.nextLine());
                        //logic.takeQuiz(sId, qId, in);
                        break;

                    case "5": //display student badges in UI (passes in studentid to get specific badges)
                    case "view student badges":
                        System.out.print("Enter student ID: ");
                        int id = Integer.parseInt(in.nextLine());
                        logic.displayStudentBadges(id);
                        break;

                    case "6":
                    case "instructor: create class":
                        System.out.print("Enter class ID: ");
                        int newClassId = Integer.parseInt(in.nextLine());
                        System.out.print("Enter class name: ");
                        String className = in.nextLine();
                        System.out.print("Enter instructor email: ");
                        String instrEmail = in.nextLine();
                        boolean created = logic.createClass(newClassId, className, instrEmail);
                        System.out.println(created ? "Class created successfully!" : "Class creation failed.");
                        break;

                    case "7":
                    case "instructor: create quiz":
                        System.out.print("Enter quiz name: ");
                        String quizName = in.nextLine();
                        System.out.print("Enter instructor ID: ");
                        int instructorId = Integer.parseInt(in.nextLine());
                        System.out.print("Enter class ID: ");
                        int classForQuiz = Integer.parseInt(in.nextLine());
                        List<QuestionData> questions = new ArrayList<>();
                        questions.add(new QuestionData(
                            "What is 2 + 2?", "easy",
                            "3", "4", "5", "6",
                            'B', 1, 0
                        ));
                        questions.add(new QuestionData(
                            "Which planet is closest to the Sun?", "easy",
                            "Earth", "Venus", "Mercury", "Mars",
                            'C', 1, 0
                        ));
                        List<Integer> readingIds = new ArrayList<>();
                        boolean quizCreated = logic.createQuiz(quizName, instructorId, classForQuiz, readingIds, questions);
                        System.out.println(quizCreated ? "Quiz created successfully!" : "Quiz creation failed.");
                        break;

                    case "8":
                        //integrate "Add another question" option into Create Quiz instead of having it separate
                    case "instructor: add question to quiz":
                        System.out.print("Enter quiz ID: ");
                        int quizId = Integer.parseInt(in.nextLine());
                        System.out.print("Enter question text: ");
                        String qText = in.nextLine();
                        System.out.print("Enter difficulty (easy, medium, hard): ");
                        String diff = in.nextLine();
                        System.out.print("Enter choice A: ");
                        String choiceA = in.nextLine();
                        System.out.print("Enter choice B: ");
                        String choiceB = in.nextLine();
                        System.out.print("Enter choice C: ");
                        String choiceC = in.nextLine();
                        System.out.print("Enter choice D: ");
                        String choiceD = in.nextLine();
                        System.out.print("Enter correct answer letter (A/B/C/D): ");
                        char correct = in.nextLine().toUpperCase().charAt(0);
                        System.out.print("Enter objective ID: ");
                        int objId = Integer.parseInt(in.nextLine());
                        //boolean added = logic.addQuestionToQuiz(qText, diff, choiceA, choiceB, choiceC, choiceD, correct, objId, quizId);
                        //System.out.println(added ? "Question added successfully!" : "Failed to add question.");
                        break;

                    case "9":
                    case "search for a student":
                        System.out.print("Search by (name/email/id): ");
                        String filterType = in.nextLine();
                        System.out.print("Enter search: ");
                        String query = in.nextLine();
                        ArrayList<Student> students = logic.searchStudent(filterType, query);
                        if (students.isEmpty()) {
                            System.out.println("No students found.");
                        } else {
                            System.out.println("Search results:");
                            BusinessLogic.printStudents(students);
                        }
                        break;

                    case "10":
                    case "display all badges":
                        logic.displayAllBadges();
                        break;

                    case "11":
                    case "get all quizzes for a class":
                        System.out.print("Enter class ID: ");
                        int ClassId = Integer.parseInt(in.nextLine());
                        logic.viewQuizzesByClass(ClassId);
                        break;

                    default:
                        System.out.println("Invalid command. Try again.");
                        printCommands();
                        break;
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        in.close();
    }
        */
}
