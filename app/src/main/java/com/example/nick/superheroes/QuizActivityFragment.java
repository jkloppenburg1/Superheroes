package com.example.nick.superheroes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
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
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
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
    private int guessRows = 2;
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
        //get file name of the next flag and remove it from the list
        String nextImage = quizQuestionList.remove(0);
        correctAnswer = nextImage; // update the correct answer
        answerTextView.setText(""); // clear answerTetView

        // display current question number
        questionNumberTextView.setText(getString(
                R.string.question, (correctAnswers + 1), NUMBER_OF_QUESTIONS));

        // extract the region from the next image's name
        String region = nextImage;

        AssetManager assets = getActivity().getAssets();

        // get an Input Stream to the asset representing the next flag
        // and try to use the InputStream
        try (InputStream stream =
                assets.open(region + "/" + nextImage + ".png"))
        {
            // load the asset as a Drawable and display on the flagImageView
            Drawable hero = Drawable.createFromStream(stream, nextImage);
            heroImageView.setImageDrawable(hero);
        }
        catch (IOException exception)
        {
            Log.e(TAG, "Error loading " + nextImage, exception);
        }

        Collections.shuffle(fileNameList); //shuffle file names

        // put the correct answer at the end of fileNameList
        int correct = fileNameList.indexOf(correctAnswer);
        fileNameList.add(fileNameList.remove(correct));

        // add guess Buttons based on value of guessRows
        for (int row = 0; row < guessRows; row++)
        {
            //place Buttons in currentTableRow
            for (int column = 0;
                    column < guessLinearLayouts[row].getChildCount();
                    column++)
            {
                // get reference to Button to configure
                Button newGuessButton =
                        (Button) guessLinearLayouts[row].getChildAt(column);
                newGuessButton.setEnabled(true);

                // get answer choice and set it to newGuessButton's text
                String filename = fileNameList.get((row * 2) + column);
                newGuessButton.setText(getAnswerChoice(filename));
            }
        }

        // randomly replace one Button with the correct answer
        int row = random.nextInt(guessRows); // pick random row
        int column = random.nextInt(2); // pick random column
        LinearLayout randomRow = guessLinearLayouts[row]; // get the row
        String answerChoice = getAnswerChoice(correctAnswer);
        ((Button) randomRow.getChildAt(column)).setText(answerChoice);
    }

    private View.OnClickListener guessButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button guessButton = ((Button) v);
            String guess = guessButton.getText().toString();
            String answer = getAnswerChoice(correctAnswer);
            totalGuesses++; //increment number of guesses user has made

            if (guess.equals(answer)) // if guess is correct
            {
                correctAnswers++; // increment the number of correct answers

                //display correct answer in green text
                answerTextView.setText(answer + "!");
                answerTextView.setTextColor(
                        getResources().getColor(R.color.correct_answer,
                                getContext().getTheme()));

                disableButtons(); // disable all guess Buttons

                // if the user has correctly identified NUMBER_OF_QUESTIONS answers
                if (correctAnswers == NUMBER_OF_QUESTIONS)
                {
                    // DialogFragment to displa quiz stats and start new quiz
                    DialogFragment quizResults =
                            new DialogFragment()
                            {
                                //create an AlertDialog and return it
                                @Override
                                public Dialog onCreateDialog(Bundle bundle)
                                {
                                    AlertDialog.Builder builder =
                                            new AlertDialog.Builder(getActivity());
                                    builder.setMessage(
                                            getString(R.string.results,
                                                    totalGuesses,
                                                    1000 / (double) totalGuesses));

                                    // "Reset Quiz" Button
                                    builder.setPositiveButton(R.string.reset_quiz,
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    resetQuiz();
                                                }
                                            });

                                    return builder.create(); // return the AlertDialog
                                }
                            };
                }
                else // answer was incorrect
                {
                    // display "Incorrect!" in red
                    answerTextView.setText(R.string.incorrect_answer);
                    answerTextView.setTextColor(getResources().getColor(
                            R.color.incorrect_answer, getContext().getTheme()));
                    guessButton.setEnabled(false); // disable incorrect answer
                }
            }
        }
    };

    private String getAnswerChoice(String name)
    {
        String answerChoice = name.substring(name.indexOf('-') + 1);
        return answerChoice.replace('_', ' ');
    }

    private void disableButtons()
    {
        for (int row = 0; row < guessRows; row++)
        {
            LinearLayout guessRow = guessLinearLayouts[row];
            for (int i = 0; i < guessRow.getChildCount(); i++)
                guessRow.getChildAt(i).setEnabled(false);
        }
    }

}
