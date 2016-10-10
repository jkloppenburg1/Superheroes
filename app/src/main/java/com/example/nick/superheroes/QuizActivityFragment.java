package com.example.nick.superheroes;

import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A placeholder fragment containing a simple view.
 */
public class QuizActivityFragment extends Fragment {
    // String for logging errors
    private static final String TAG = "FlagQuiz Activity";

    private static final int NUMBER_OF_QUESTIONS = 10;

    private List<String> fileNameList; // image file names
    private List<String> quizQuestionList; // question being asked
    private Set<String> questionsSet; // question to be asked
    private String correctAnswer; //correct answer for current question
    private int totalGuesses; // number of guesses made
    private int correctAnswers; // number of correct guesses

    private SecureRandom random; // used to randomize quiz
    private Handler handler; // used to delay loading next flag

    private TextView questionNumberTextView;
    private ImageView heroImageView;
    private LinearLayout[] guessLinearLayouts; // rows of answer buttons
    private TextView answerTextView; //displays correct answer


    public QuizActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);

        fileNameList = new ArrayList<>();
        quizQuestionList = new ArrayList<>();
        random = new SecureRandom();
        handler = new Handler();

        // get references to GUI
        questionNumberTextView =
                (TextView) view.findViewById(R.id.questionNumberTextView);
        heroImageView = (ImageView) view.findViewById(R.id.heroImageView);
        guessLinearLayouts = new LinearLayout[2];
        guessLinearLayouts[0] =
                (LinearLayout) view.findViewById(R.id.row1LinearLayout);
        guessLinearLayouts[1] =
                (LinearLayout) view.findViewById(R.id.row2LinearLayout);

        // configure listeners for the guess Buttons
        for (LinearLayout row : guessLinearLayouts) {
            for (int column = 0; column < row.getChildCount(); column++) {
                Button button = (Button) row.getChildAt(column);
                button.setOnClickListener(guessButtonListener);
            }
        }

        // set questionNumberTextView's text
        questionNumberTextView.setText(
                getString(R.string.question, 1, NUMBER_OF_QUESTIONS));
        return view;
    }

    public void updateQuestion(SharedPreferences sharedPreferences) {
        questionsSet =
                sharedPreferences.getStringSet(QuizActivity.QUESTIONS, null);
    }

    public void resetQuiz() {
        //use AssetManager to get image file names for enabled regions
        AssetManager assets = getActivity().getAssets();
        fileNameList.clear(); // empty list of image file names

        for (String question : questionsSet) {
            fileNameList.add(question.replace(".png", ""));
        }
/*
        try
        {
            for (String question : questionsSet)
            {
                fileNameList.add(question.replace(".png", ""));
            }
        }
        catch (IOException exception)
        {
            Log.e(TAG, "Error loading image file names", exception);
        }
    }
    */
        correctAnswers = 0; // reset number of correct answers made
        totalGuesses = 0;
        quizQuestionList.clear();

        int questionCounter = 1;
        int numberOfQuestions = fileNameList.size();

        // add NUMBER_OF_QUESTIONS random file names to the quizQuestionsList
        while (questionCounter <= NUMBER_OF_QUESTIONS)
        {
            int randomIndex = random.nextInt(numberOfQuestions);

            // get the random file name
            String filename = fileNameList.get(randomIndex);

            // if the region is enabled and it hasn't already been chosen
            if (!quizQuestionList.contains(filename))
            {
                quizQuestionList.add(filename);
                questionCounter++;
            }
        }
        loadNextQuestion(); //start quiz by loading first question
    }

    private void loadNextQuestion() //// Start working here
    {

    }
}
