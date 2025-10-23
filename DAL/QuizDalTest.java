package g2m.DAL;

import java.util.ArrayList;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import g2m.DAL.javaSQLobjects.Student;

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

    @Test
    public void testGetAllStudents() {
        // Make sure that something is returned
        ArrayList<Student> students = dal.getAllStudents();
        assertNotNull(students);
        assertFalse(students.isEmpty());

    }
}

/*
    @Test
    public void testGetReviewsValid() {
        // use a known bookid so make sure something is returned
        ArrayList<String> reviews = dal.getReviews(1);
        assertNotNull(reviews);
    }

    @Test
    public void testGetReviewsInvalid() {
        // use an invalid bookid so make sure nothing is returned
        ArrayList<String> reviews = dal.getReviews(-1);
        assertTrue(reviews.isEmpty());

    }

    @Test
    public void testLeaveReviewValid() {
        try {
            dal.leaveReview(1, "Bob", 5, "Loved the writing");
        } catch (Exception e) {
            fail("leaveReview should not throw for valid input");
        }
    }

    @Test
    public void testRegisterLoyaltyCard() {
        // for unique input result should be true
        String email = "test" + System.currentTimeMillis() + "@test.com";
        boolean result = dal.registerLoyaltyCard("test", email);
        assertTrue(result);
    }

    @Test
    public void testRegisterLoyaltyCardDuplicate() {
        // should fail for duplicate email
        String email = "copy@test.com";
        dal.registerLoyaltyCard("Test1", email);
        boolean result = dal.registerLoyaltyCard("Test2", email);
        assertFalse(result);
    }

    @Test
    public void testSearchForBookByAuthor() {
        // for known author, something should be returned
        ArrayList<Book> books = dal.searchForBookByAuthor("Suzanne Collins");
        assertNotNull(books);
        assertFalse(books.isEmpty());
    }

    @Test
    public void testSearchForBookByNonsenseAuthor() {
        // for unknown author, nothing should be returned
        ArrayList<Book> books = dal.searchForBookByAuthor("woiuyhndoiufb3uwyglxiknh");
        assertNotNull(books);
        assertTrue(books.isEmpty());
    }

    @Test
    public void testSearchForBookByGenre() {
        // valid genre should return
        ArrayList<Book> books = dal.searchForBookByGenre("Fantasy");
        assertNotNull(books);
        assertFalse("Should find fantasy books", books.isEmpty());

    }

    @Test
    public void testSearchForBookByNonsenseGenre() {
        // invalid genre should not return
        ArrayList<Book> books = dal.searchForBookByGenre("&^%*&Y)&(*ef)");
        assertTrue(books.isEmpty());
    }

    @Test
    public void testSearchForBookByTitle() {
        // Something should return for valid title
        ArrayList<Book> books = dal.searchForBookByTitle("Catching Fire");
        assertNotNull(books);
        assertFalse(books.isEmpty());

    }

    @Test
    public void testSearchForBookByNonsenseTitle() {
        // Nothing should return for invalid title
        ArrayList<Book> books = dal.searchForBookByTitle("*&^$&%$#@$#&*&*))))))))))&*&%^%$%$&YJYUTG^E#$W$%RDY%E%$");
        assertNotNull(books);
        assertFalse(books.isEmpty());

    }
 */