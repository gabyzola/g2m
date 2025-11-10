package g2m.g2m_backend.business;

import java.util.*;
import g2m.g2m_backend.DAL.javaSQLobjects.Question; 

public class QuizManager {
    private List<Question> allQuestions;       // All questions for this quiz
    private List<Question> remainingQuestions; // Unanswered questions
    private DifficultyLevel currentDifficulty; // Current difficulty state
    private Question currentQuestion;

    public QuizManager(List<Question> quizQuestions) {
        this.allQuestions = new ArrayList<>(quizQuestions);
        this.remainingQuestions = new ArrayList<>(quizQuestions);
        this.currentDifficulty = DifficultyLevel.MEDIUM; // Start medium by default
    }

    /* 
    public Question getNextQuestion(boolean previousWasCorrect) {
        // Adjust difficulty based on answer
        adjustDifficulty(previousWasCorrect);

        // Filter remaining questions by difficulty
        List<Question> filtered = remainingQuestions.stream()
            .filter(q -> q.getDifficulty() == currentDifficulty)
            .toList();
            

        // If no questions at that difficulty remain, just grab any remaining one
        if (filtered.isEmpty()) {
            if (remainingQuestions.isEmpty()) {
                return null; // quiz complete
            }
            filtered = remainingQuestions;
        }

        // Pick a random question from that difficulty
        currentQuestion = filtered.get(new Random().nextInt(filtered.size()));

        // Remove it so we don't repeat
        remainingQuestions.remove(currentQuestion);

        return currentQuestion;
    }
        */

    private void adjustDifficulty(boolean wasCorrect) {
        switch (currentDifficulty) {
            case EASY -> currentDifficulty = wasCorrect ? DifficultyLevel.MEDIUM : DifficultyLevel.EASY;
            case MEDIUM -> currentDifficulty = wasCorrect ? DifficultyLevel.HARD : DifficultyLevel.EASY;
            case HARD -> currentDifficulty = wasCorrect ? DifficultyLevel.HARD : DifficultyLevel.MEDIUM;
        }
    }

    public boolean isQuizOver() {
        return remainingQuestions.isEmpty();
    }
}

