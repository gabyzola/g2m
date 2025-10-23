package g2m.DAL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import g2m.DAL.javaSQLobjects.Student;
import g2m.DAL.javaSQLobjects.Badge;

public class BusinessLogic {

    private QuizDal dal;

    private ArrayList<Student> students = new ArrayList<>();
    private ArrayList<Badge> badges = new ArrayList<>();

    public BusinessLogic(QuizDal dal) {
        this.dal = dal;

        students = dal.getAllStudents(); 
    }

    /**
     * Used in searching for students based on filters by email, name, or studentId
     * 
     * @param filterType 
     * @param query      the search query input by the user
     * @return array list of objects meeting the query's search criteria
     */
    public ArrayList<Student> getStudentByQuery(String filterType, String query) {

        if (filterType.equalsIgnoreCase("email")) {
        return dal.searchForStudentByEmail(query);
    } else if (filterType.equalsIgnoreCase("name")) {
        return dal.searchForStudentByName(query);
    } else if (filterType.equalsIgnoreCase("studentId")) {
        return dal.searchForStudentById(query);
    } else {
        return null;
    }
    }

    public static String printStudents(ArrayList<Student> students) {
        String toReturn = "";
        for (int i = 0; i < students.size(); i++) {
            toReturn += "\n" + students.get(i);
            toReturn += "\n";
        }

        return toReturn;
    }

    //get all students
    public ArrayList<Student> getAllStudents() {
        return dal.getAllStudents();
    }


    //Enroll student in a class
    public boolean enrollStudent(int classId, String email) {
        return dal.enrollStudent(classId, email);
    }

    //Returns quiz by id
    public ArrayList<String> GetQuiz(int quizId) {
        return dal.getQuiz(quizId);
    }

    //returns all quizzes in a class

    //returns all badges based on username
    public boolean getBadges(int username) {
        return dal.getBadges(username);
    }

    //returns all badges in the system
    public static String printBadges(ArrayList<Badge> badges) {
        String toReturn = "";
        for (int i = 0; i < badges.size(); i++) {
            toReturn += "\n" + badges.get(i);
            toReturn += "\n";
        }

        return toReturn;
    }

    //returns attempt by studentid

    //returns all students in a class by classId
    //print enrollees by class
    public void printEnrollees(int myClassId) {

        List<Map<String, Object>> results =  dal.searchForEnrolleesByClass(myClassId);

        for (Map<String, Object> row : results) {
        System.out.println(row.get("firstName"));
        }

    }

    //returns all classes under a professor

    //returns all classes in the database

    //returns all instructors in the database

    //returns specific instructor by query

    //returns questions under a quizid

    //returns all questions

    //get next question (machine learning)

    //choose learning objective (called in takeQuiz)

    //takeQuiz

    //return score

    //submit quiz (calls return score)

    //returns learning objective based on reading (readingId)

    //add difficulty (machine learning)

    //Login

    //Assign Badge

    //create quiz

    //create class

    
    //sort all lists
    public ArrayList<String> sortList(ArrayList<String> listToSort){
         Collections.sort(listToSort);

         return listToSort;
    }

}