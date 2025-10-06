package g2m.DAL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import g2m.DAL.javaSQLobjects.Student;

public class BusinessLogic {

    private QuizDal dal;

    private ArrayList<Student> students = new ArrayList<>();

    public BusinessLogic(QuizDal dal) {
        this.dal = dal;

        students = QuizDal.getAllStudents(); // load in all the books
    }

    /**
     * Used in searching for books based on filters by title, author, or genre
     * 
     * @param filterType title, author, or genre
     * @param query      the search query input by the user
     * @return an array list of books meeting the query's search criteria
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


    //Enroll student in a class
    public boolean enrollStudent(int classId, String email) {
        return dal.enrollStudent(classId, email);
    }

    //Returns all quizzes in DB
    public ArrayList<String> GetQuiz(int quizId) {
        return dal.getQuiz(quizId);
    }

    //returns all quizzes based on class code

    //returns all badges based on username
    public boolean getBadges(int username) {
        return dal.getBadges(username);
    }

    public ArrayList<String> sortList(ArrayList<String> listToSort){
         Collections.sort(listToSort);

         return listToSort;
    }

}