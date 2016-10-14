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

import static android.R.attr.type;
import static com.example.nick.superheroes.R.string.question;

/**
 * A placeholder fragment containing a simple view.
 */
public class QuizActivityFragment extends Fragment {
    // String for logging errors
    private static final String TAG = "FlagQuiz Activity";

    private static final int NUMBER_OF_QUESTIONS = 10;

//    private List<String> fileNameList; // image file names
    private List<String> quizHeroesList; // Heroes in current quiz
//    private Set<String> imageSet; // worldRegions in current quiz
    private int correctAnswer; //position of correct answer for current question
    private int totalGuesses; // number of guesses made
    private int correctAnswers; // number of correct guesses
    private int guessRows = 2;
    private SecureRandom random; // used to randomize quiz
    private Handler handler; // used to delay loading next flag

    private TextView questionNumberTextView;
    private ImageView heroImageView;
    private LinearLayout[] guessLinearLayouts; // rows of answer buttons
    private TextView answerTextView; //displays correct answer

    private String typeOfQuestion; //filter which question which is asked
    private boolean[] answeredArray; // Array that keeps track of previous answers in current quiz
    private String[] answers; //Array that holds correct answers, copied from SuperHero
    private int pos; //Integer that keeps track of correct answer


 //   public QuizActivityFragment() {
 //   }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);

        //fileNameList = new ArrayList<>();
        quizHeroesList = new ArrayList<>();
        random = new SecureRandom();
        handler = new Handler();

        //Set typeOfQuestion to array of answers


        //Set questions that have been answered
        answeredArray = new boolean[SuperHero.usernames.length];

        for (int i = 0; i < answeredArray.length; i++)
        {
            answeredArray[i] = false;
        }



        //imageSet = SuperHero.usernames;

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
                getString(question, 1, NUMBER_OF_QUESTIONS));
        return view;
    }

    //Updates the array 'answers' to match the question type
    public void updateQuestion(SharedPreferences sharedPreferences) {
        typeOfQuestion =
                sharedPreferences.getString(QuizActivity.QUESTIONS, null);
        if (typeOfQuestion == "Names")
        {
            answers = SuperHero.names;
        }
        else if (typeOfQuestion == "Superpower")
        {
            answers = SuperHero.superpower;
        }
        else
        {
            answers = SuperHero.oneThing;
        }
    }

    public void resetQuiz() {


        //use AssetManager to get image file names for enabled regions
        AssetManager assets = getActivity().getAssets();
//        fileNameList.clear(); // empty list of image file names

        /*
        for (String question : questionsSet) {
            fileNameList.add(question.replace(".png", ""));
        }



        try
        {
            for (String image : imageSet)
            {
                fileNameList.add(image.replace(".png", ""));
            }
        }
        catch (IOException exception)
        {
            Log.e(TAG, "Error loading image file names", exception);
        }
        */


        correctAnswers = 0; // reset number of correct answers made
        totalGuesses = 0;
        quizHeroesList.clear();
        // Reset check for previous answers
        for (int i = 0; i < answeredArray.length; i++)
        {
            answeredArray[i] = false;
        }

        int questionCounter = 1;
        int numberOfHeroes = SuperHero.usernames.length;
/*
        // add NUMBER_OF_QUESTIONS random file names to the quizQuestionsList
        while (questionCounter <= NUMBER_OF_QUESTIONS)
        {
            int randomIndex = random.nextInt(numberOfHeroes);

            // get the random file name
      //      String filename = fileNameList.get(randomIndex);

            // if the region is enabled and it hasn't already been chosen
            if (!quizHeroesList.contains(filename))
            {
                quizHeroesList.add(filename);
                questionCounter++;
            }

        }
        */
        loadNextQuestion(); //start quiz by loading first question
    }

    private void loadNextQuestion()
    {
        //get position of next flag
        pos = random.nextInt(SuperHero.usernames.length);
        // check if position has been used
        while (answeredArray[pos])
        {
            pos = random.nextInt(SuperHero.usernames.length);
        }
        //current position used
        answeredArray[pos] = true;

        //get file name of the next flag and remove it from the list
        String nextImage = SuperHero.usernames[pos] + ".png";
        correctAnswer = pos; // update the correct answer
        answerTextView.setText(""); // clear answerTextView

        // display current question number
        questionNumberTextView.setText(getString(
                question, (correctAnswers + 1), NUMBER_OF_QUESTIONS));

        // extract the answer from the next image's name
        //String questionType = nextImage;

 //       AssetManager assets = getActivity().getAssets();

        // get an Input Stream to the asset representing the next hero
        // and try to use the InputStream
 /*       try (InputStream stream =
                assets.open(questionType) + "/" + nextImage + ".png"))
        {
            // load the asset as a Drawable and display on the flagImageView
            Drawable hero = Drawable.createFromStream(stream, nextImage);
            heroImageView.setImageDrawable(hero);
        }
        catch (IOException exception)
        {
            Log.e(TAG, "Error loading " + nextImage, exception);
        }
  */

        //Collections.shuffle(SuperHero.usernames); //shuffle file names

        // put the correct answer at the end of fileNameList
        //int correct = fileNameList.indexOf(correctAnswer);
        //fileNameList.add(fileNameList.remove(correct));

        // add guess Buttons based on value of guessRows
        int choice1 = random.nextInt(SuperHero.usernames.length);
        int choice2 = random.nextInt(SuperHero.usernames.length);
        int choice3 = random.nextInt(SuperHero.usernames.length);
        int choice4 = random.nextInt(SuperHero.usernames.length);


        //verify none of the answers overlap, // will become infinite if not enough choices
        while (choice1 == pos)
        {
            choice1++;
            if (choice1 >= SuperHero.usernames.length)
                choice1 = 0;
        }
        while (choice2 == pos || choice2 == choice1)
        {
            choice2++;
            if (choice2 >= SuperHero.usernames.length)
                choice2 = 0;
        }
        while (choice3 == pos || choice3 == choice2 || choice3 == choice1)
        {
            choice3++;
            if (choice3 >= SuperHero.usernames.length)
                choice3 = 0;
        }
        while (choice4 == pos || choice4 == choice3 || choice4 == choice2 || choice4 == choice1)
        {
            choice4++;
            if (choice4 >= SuperHero.usernames.length)
                choice4 = 0;
        }

        //Button 1
        Button newGuessButton = (Button) guessLinearLayouts[0].getChildAt(0);
        newGuessButton.setText(answers[choice1]);

        //Button 2
        newGuessButton = (Button) guessLinearLayouts[0].getChildAt(1);
        newGuessButton.setText(answers[choice1]);

        //Button 3
        newGuessButton = (Button) guessLinearLayouts[1].getChildAt(0);
        newGuessButton.setText(answers[choice1]);

        //Button 4
        newGuessButton = (Button) guessLinearLayouts[1].getChildAt(1);
        newGuessButton.setText(answers[choice1]);

        /*
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
                newGuessButton.setText(SuperHero.names[choice1]);
            }
        }
        */

        // randomly replace one Button with the correct answer
        int row = random.nextInt(guessRows); // pick random row
        int column = random.nextInt(2); // pick random column
        LinearLayout randomRow = guessLinearLayouts[row]; // get the row
        //String answerChoice = getAnswerChoice(correctAnswer);
        ((Button) randomRow.getChildAt(column)).setText(answers[pos]);
    }

    private View.OnClickListener guessButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button guessButton = ((Button) v);
            String guess = guessButton.getText().toString();
            String answer = answers[pos];
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
                    // DialogFragment to display quiz stats and start new quiz
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

    /*
    private String getAnswerChoice(String name)
    {
        String answerChoice = name.substring(name.indexOf('-') + 1);
        return answerChoice.replace('_', ' ');
    }
    */

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
