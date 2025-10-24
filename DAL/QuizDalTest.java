package g2m.DAL;

import java.util.ArrayList;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import g2m.DAL.javaSQLobjects.Student;
import jdk.jfr.Timestamp;

public class QuizDalTest {

    private static QuizDal dal;
    private static String dbName;
    private static String user;
    private static String password;

    @BeforeClass
    public static void setup() {
        dbName = System.getenv("DB_NAME");
        user = System.getenv("DB_USER");
        password = System.getenv("DB_PASS");

        dal = new QuizDal(dbName, user, password);
    }

    //success
    @Test
    public void testGetAllStudents() {
        // Make sure that something is returned
        ArrayList<Student> students = dal.getAllStudents();
        assertNotNull(students);
        assertFalse(students.isEmpty());

    }

/*
    @Test
    public void searchForEnrolleesByClass() {
        // Make sure that a list is returned for a known class
        List<Map<String, Object>> students = dal.searchForEnrolleesByClass(101);
        assertNotNull(students);
        assertFalse(students.isEmpty());
    }
    */

   //success
    @Test
    public void searchForStudentByEmail() {
        // Make sure that a known email returns a student
        ArrayList<Student> student = dal.searchForStudentByEmail("olonaa@merrimack.edu");
        assertNotNull(student);
        assertFalse(student.isEmpty());
    }
}
    