package g2m.g2m_backend.business;

import java.util.*;
import g2m.g2m_backend.DAL.javaSQLobjects.QuizQuestion; 
import org.springframework.stereotype.Service;

@Service
public class QuizManager {
    private List<QuizQuestion> allQuizQuestions;  //all of the quiz questions
    private List<QuizQuestion> remainingQuestions; //unanswered questions
    private DifficultyLevel currentDifficulty; //current difficulty state
    private QuizQuestion currentQuestion;

    public QuizManager(List<QuizQuestion> quizQuestions) {
        this.allQuizQuestions = new ArrayList<>(quizQuestions);
        this.remainingQuestions = new ArrayList<>(quizQuestions);
        this.currentDifficulty = DifficultyLevel.MEDIUM; // Start medium by default
    }

    //not in api yet !

    //get question 9one by one)
    public QuizQuestion getNextQuestion() {
        if (remainingQuestions.isEmpty()) {
            currentQuestion = null;
            return null; 
        }
        currentQuestion = remainingQuestions.remove(0);
        return currentQuestion;
    }

    //student submits answer one by one
    public boolean submitAnswer(int choiceId) {
        if (currentQuestion == null) {
            throw new IllegalStateException("No question is currently active.");
        }

        boolean wasCorrect = (choiceId == currentQuestion.getCorrectChoiceId());
        //adjustDifficulty(wasCorrect); //for later
        return wasCorrect;
    }
}

