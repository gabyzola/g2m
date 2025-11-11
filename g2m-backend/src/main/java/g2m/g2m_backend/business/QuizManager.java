package g2m.g2m_backend.business;

import java.util.*;
import java.util.stream.Collectors;

import g2m.g2m_backend.DAL.javaSQLobjects.QuizQuestion; 
import g2m.g2m_backend.DAL.QuizDal; 

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

    public QuizManager startQuiz(int quizId, int studentId) {

        QuizDal dal = new QuizDal();
        BusinessLogic bl = new BusinessLogic(dal);
        
        //gets entire list of quiz questions
        List<QuizQuestion> allQuestions = bl.getQuizQuestions(quizId);
        if (allQuestions == null || allQuestions.isEmpty()) {
            throw new IllegalStateException("No questions found for quiz " + quizId);
        }

        // gets students chosen objs
        List<Map<String, Object>> studentObjectives = bl.getStudentObjectives(quizId);
        if (studentObjectives == null || studentObjectives.isEmpty()) {
            throw new IllegalStateException("Student has not selected any objectives.");
        }

        //gets ids of objs to compare
        Set<Integer> selectedObjectiveIds = studentObjectives.stream()
            .map(obj -> (Integer) obj.get("objectiveId")) // assuming your map keys match DB column names
            .collect(Collectors.toSet());

        //filter questions by these objectives
        List<QuizQuestion> filteredQuestions = allQuestions.stream()
            .filter(q -> selectedObjectiveIds.contains(q.getObjectiveId()))
            .collect(Collectors.toList());

        //shuffle and pick only 5 questions
        Collections.shuffle(filteredQuestions);
        List<QuizQuestion> questionsForQuiz = filteredQuestions.stream()
            .limit(5)
            .collect(Collectors.toList());

        //return a QuizManager with the filtered questions
        return new QuizManager(questionsForQuiz);
    }


    public QuizQuestion getNextQuestion(boolean previousWasCorrect) {
        //adjusts difficulty based on the student's prev answers
        adjustDifficulty(previousWasCorrect);

        //filters through remaining questions and gets difficulty to evaluate
        List<QuizQuestion> filtered = remainingQuestions.stream()
            .filter(q -> q.getDifficulty() == currentDifficulty)
            .collect(Collectors.toList()); //had ro add this because it was causing errors 
            

        //get any remaining question (desired difficulty might not even be there)
        if (filtered.isEmpty()) {
            if (remainingQuestions.isEmpty()) {
                return null; //quiz done
            }
            filtered = remainingQuestions;
        }

        //pick a random question from that difficulty (if there's like a ton of one difficulty available)
        currentQuestion = filtered.get(new Random().nextInt(filtered.size()));

        //remove completed question from list
        remainingQuestions.remove(currentQuestion);

        return currentQuestion;
    }
       
    //called when checking correctness of previous answer
    private void adjustDifficulty(boolean wasCorrect) {
        switch (currentDifficulty) {
            case EASY -> currentDifficulty = wasCorrect ? DifficultyLevel.MEDIUM : DifficultyLevel.EASY;
            case MEDIUM -> currentDifficulty = wasCorrect ? DifficultyLevel.HARD : DifficultyLevel.EASY;
            case HARD -> currentDifficulty = wasCorrect ? DifficultyLevel.HARD : DifficultyLevel.MEDIUM;
        }
    }

    //no questions left
    public boolean isQuizOver() {
        return remainingQuestions.isEmpty();
    }
}

