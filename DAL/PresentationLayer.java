package g2m.DAL;
import java.io.Console;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import g2m.DAL.javaSQLobjects.QuestionData;
import g2m.DAL.javaSQLobjects.Student;


//COMMENTS ARE FOR HOW TO USE THESE IN THE UI
//all student ids will be automatically taken from their login info
//students must not have to enter their ids
public class PresentationLayer {

    public static void printCommands() {
        System.out.println("\n===== Available Commands =====");
        System.out.println("1. Register New User");
        System.out.println("2. Enroll Student in Class");

        //Working
        System.out.println("3. View Student Classes");
        System.out.println("4. Take a Quiz");
        System.out.println("5. View Student Badges");
        System.out.println("6. Instructor: Create Class");

        //ERROR
        System.out.println("7. Instructor: Create Quiz");
        System.out.println("8. Instructor: Add Question to Quiz");

        //Working
        System.out.println("9. Search for a Student");

        //Working
        System.out.println("10. Display All Badges");

        //Working
        System.out.println("11. Get All Quizzes for a Class");

        //Working
        System.out.println("12. Instructor: View Instructor Classes");

        //ERROR
        System.out.println("13. View Class Enrollees");

        //ERROR: invalid reading file
        System.out.println("14. Upload Reading");
        System.out.println("15. View Objectives By Quiz");
        System.out.println("Type 'exit' to quit.");
        System.out.println("==============================\n");
    }

    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);
        System.out.print("Enter your database username: ");
        String user = in.nextLine();

        Console console = System.console();
        String password;

        if (console != null) {
            char[] passwordArray = console.readPassword("Enter your password: ");
            password = new String(passwordArray);
        } else {
            System.out.print("Enter your password: ");
            password = in.nextLine();
        }

        QuizDal dal = new QuizDal("QuizzingDb", user, password);
        BusinessLogic logic = new BusinessLogic(dal);

        System.out.println("Welcome to MackAdapt CLI Tester!\n");
        printCommands();

        while (true) {
            System.out.print("\nEnter a command: ");
            String input = in.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Exiting program...");
                logic.closeConnection();
                break;
            }

            try {
                switch (input.toLowerCase()) {
                    case "1":
                    case "register new user":
                        System.out.print("Enter username: ");
                        String username = in.nextLine();
                        System.out.print("Enter email: ");
                        String email = in.nextLine();
                        System.out.print("Is this an instructor account? (Y/N): ");
                        boolean isInstructor = in.nextLine().equalsIgnoreCase("Y");
                        System.out.print("Enter first name: ");
                        String firstName = in.nextLine();
                        System.out.print("Enter last name: ");
                        String lastName = in.nextLine();
                        System.out.print("Enter major (students) or subject (instructors): ");
                        String major = in.nextLine();
                        String subject = isInstructor ? major : null;

                        boolean registered = logic.registerUser(username, email, isInstructor, major, subject, firstName, lastName);
                        System.out.println(registered ? "✅ User successfully registered!" : "❌ Registration failed.");
                        break;

                    case "2":
                    case "enroll student in class":
                        System.out.print("Enter class ID: ");
                        int classId = Integer.parseInt(in.nextLine());
                        System.out.print("Enter student email: ");
                        String studentEmail = in.nextLine();
                        boolean enrolled = logic.enrollStudentInClass(classId, studentEmail);
                        System.out.println(enrolled ? "✅ Student enrolled successfully!" : "❌ Enrollment failed.");
                        break;

                    case "3":
                    case "view student classes":
                        System.out.print("Enter student ID: ");
                        int studentId = Integer.parseInt(in.nextLine());
                        logic.viewStudentClasses(studentId);
                        break;

                    case "4":
                    case "take a quiz":
                        System.out.print("Enter your student ID: ");
                        int sId = Integer.parseInt(in.nextLine());
                        System.out.print("Enter quiz ID: ");
                        int qId = Integer.parseInt(in.nextLine());
                        logic.takeQuiz(sId, qId, in);
                        break;

                    case "5":
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
                        System.out.println(created ? "✅ Class created successfully!" : "❌ Class creation failed.");
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
                        questions.add(new QuestionData("What is 2 + 2?", "easy", "3", "4", "5", "6", 'B', 1, 0));
                        questions.add(new QuestionData("Which planet is closest to the Sun?", "easy", "Earth", "Venus", "Mercury", "Mars", 'C', 1, 0));
                        List<Integer> readingIds = new ArrayList<>();

                        boolean quizCreated = logic.createQuiz(quizName, instructorId, classForQuiz, readingIds, questions);
                        System.out.println(quizCreated ? "✅ Quiz created successfully!" : "❌ Quiz creation failed.");
                        break;

                    case "8":
                    case "instructor: add question to quiz":
                        System.out.println("This is now part of Create Quiz. No action needed.");
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

                    case "12":
                    case "instructor: view instructor classes":
                        System.out.print("Enter instructor ID: ");
                        int instId = Integer.parseInt(in.nextLine());
                        logic.viewInstructorClasses(instId);
                        break;

                    case "13":
                    case "view class enrollees":
                        System.out.print("Enter class ID: ");
                        int cId = Integer.parseInt(in.nextLine());
                        logic.viewClassEnrollees(cId);
                        break;

                    case "14":
                    case "upload reading":
                        System.out.print("Enter instructor ID: ");
                        int iId = Integer.parseInt(in.nextLine());
                        System.out.print("Enter class ID: ");
                        int clId = Integer.parseInt(in.nextLine());
                        System.out.print("Enter reading name: ");
                        String readingName = in.nextLine();
                        System.out.print("Enter file path: ");
                        String filePath = in.nextLine();
                        logic.uploadReading(iId, clId, readingName, filePath);
                        break;

                    case "15":
                    case "view objectives by quiz":
                        System.out.print("Enter quiz ID: ");
                        int quizId = Integer.parseInt(in.nextLine());
                        logic.viewObjectivesByQuiz(quizId);
                        break;

                    default:
                        System.out.println("Invalid command. Try again.");
                        printCommands();
                        break;
                }

            } catch (Exception e) {
                System.out.println("❌ Error: " + e.getMessage());
                e.printStackTrace();
            }
        }

        in.close();
    }
}