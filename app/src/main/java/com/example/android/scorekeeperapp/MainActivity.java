package com.example.android.scorekeeperapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;
import static java.lang.System.err;
import static java.lang.System.out;
import java.io.IOException;
import java.io.PrintStream;
import com.example.android.scorekeeperapp.R;

public class MainActivity extends AppCompatActivity {

    final Context context = this;

    // set the constant variables
    int maxNumberRound = 2;
    int point3 = 3;
    int point2 = 2;
    int freeThrow = 1;

    // set score arrays
    int[] arrayScoreTeamA = new int[maxNumberRound];
    int[] arrayScoreTeamB = new int[maxNumberRound];

    // save the winner
    String[] winnerCollection = new String[maxNumberRound];

    // set the initial values
    int currentRound = 1;
    int scoreTeamA = 0;
    int scoreTeamB = 0;

    // set textView
    TextView scoreView4TeamA;
    TextView scoreView4TeamB;
    TextView currentRoundView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 0. set the content view
        setContentView(R.layout.activity_main);

        // 1. create two score text views
        createTextViews();
    }

    public void createTextViews() {

        //0. score display view for the team A
        scoreView4TeamA = (TextView) findViewById(R.id.team_a_score);
        scoreView4TeamA.setText(String.valueOf(scoreTeamA));

        //1. score display view for the team B
        scoreView4TeamB = (TextView) findViewById(R.id.team_b_score);
        scoreView4TeamB.setText(String.valueOf(scoreTeamB));

        //2. show the current round number
        currentRoundView = (TextView) findViewById(R.id.currentRoundTextView);
        currentRoundView.setText(getString(R.string.currentRound) + String.valueOf(currentRound));

    }

    // This function is called when clicking the point buttons and each button is recognized by ID
    public void onClickButton(View v) {

        // perform action on click
        switch (v.getId()) {

            case R.id.button1_teamA:
                updateScoreWithTeamNameAndScore(true, point3);
                break;
            case R.id.button2_teamA:
                updateScoreWithTeamNameAndScore(true, point2);
                break;
            case R.id.button3_teamA:
                updateScoreWithTeamNameAndScore(true, freeThrow);
                break;
            case R.id.button1_teamB:
                updateScoreWithTeamNameAndScore(false, point3);
                break;
            case R.id.button2_teamB:
                updateScoreWithTeamNameAndScore(false, point2);
                break;
            case R.id.button3_teamB:
                updateScoreWithTeamNameAndScore(false, freeThrow);
                break;
        }

    }

    // update the team score based its ID and points
    public void updateScoreWithTeamNameAndScore (boolean teamA, int plusPoint) {

        if (teamA) {
            scoreTeamA = scoreTeamA + plusPoint;
            setScoreAtScoreViewWithTeamandScore(true, scoreTeamA);
        } else { // teamB
            scoreTeamB = scoreTeamB + plusPoint;
            setScoreAtScoreViewWithTeamandScore(false, scoreTeamB);
        }

    }

    // when clicking the button "next round"
    public void onClickNextAround(View v) {

        if (v.getId() == R.id.nextRoundButton) {

            if (currentRound > maxNumberRound - 1) {

                // 0. show score result popup
                showGameResult();

            } else {

                // 0. save the current scores for both team
                arrayScoreTeamA[currentRound] = scoreTeamA;
                arrayScoreTeamB[currentRound] = scoreTeamB;

                // 0.1 update the winner name
                if (scoreTeamA > scoreTeamB) {
                    winnerCollection[currentRound] = getResources().getString(R.string.TeamA);
                } else if (scoreTeamB > scoreTeamA) {
                    winnerCollection[currentRound] = getResources().getString(R.string.TeamB);
                } else {
                    winnerCollection[currentRound] = getResources().getString(R.string.Tie);
                }

                // 1. increase the number of round
                currentRound = currentRound + 1;

                // 2. reset the score display for both team
                resetScores(2);

                // 3. show the current round number
                showCurrentRound();

            }

        }

    }

    public void showCurrentRound() {

        currentRoundView.setText(getString(R.string.currentRound) + String.valueOf(currentRound));

    }

    public int countNumberOfWin(String name) {

        int number = 0;
        for (int i = 0 ; i < maxNumberRound; i++) {

            if (name.equals(winnerCollection[i])) {
                number = number + 1;
            }

        }

        return number;
    }

    // It is shown when the current round is over than the maximum round
    public void showGameResult() {

        Log.d("ScoreKeep", "showGameResult: ");

        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.result_popup);


        // check who is winner
        String winnerName = "WHO?";
        int numberOfWin4TeamA = countNumberOfWin(getResources().getString(R.string.TeamA));
        int numberOfWin4TeamB = countNumberOfWin(getResources().getString(R.string.TeamB));

        if (numberOfWin4TeamA > numberOfWin4TeamB) {
            winnerName = getResources().getString(R.string.TeamA);
        } else if (numberOfWin4TeamB > numberOfWin4TeamA) {
            winnerName = getResources().getString(R.string.TeamB);
        } else {
            winnerName = getResources().getString(R.string.Tie);
        }

        // create the message
        String messageString = String.format("Winner is %s.\n\n Score is %d:%d", winnerName, numberOfWin4TeamA, numberOfWin4TeamB);

        // set the custom dialog components - text, image and button
        TextView text = (TextView) dialog.findViewById(R.id.popupMessage);
        text.setTextSize(25);
        text.setTextColor(getResources().getColor(R.color.darkGray));
        text.setText(messageString);

        // Add the close button
        Button dialogButton = (Button) dialog.findViewById(R.id.closePopupBtn);

        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }

        });

        dialog.show();

    }

    public String getResult(int scoreA, int scoreB) {

        String returnMessage = "";

        if (scoreA > scoreB) {
            returnMessage = getString(R.string.TeamAWon);
        } else if (scoreB > scoreA) {
            returnMessage = getString(R.string.TeamBWon);
        } else {
            returnMessage = getString(R.string.sameScore);
        }

        return returnMessage;
    }

    // call this function when clicking the button "Result"
    public void callScoreResultPopup() {

        StringBuilder concatString = new StringBuilder();

        for (int i = 0; i < maxNumberRound; i++) {

            if ((arrayScoreTeamA[i] > 0) ||(0 < arrayScoreTeamB[i])) {

                String resultString = getResult(arrayScoreTeamA[i], arrayScoreTeamB[i]);

                concatString.append(getString(R.string.round));
                concatString.append(i);
                concatString.append(": ");
                concatString.append(String.format("(%d : ", arrayScoreTeamA[i]));
                concatString.append(String.format("%d) ",arrayScoreTeamB[i]));
                concatString.append(resultString);

            }

        }

        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.popup);

        // set the custom dialog components - text, image and button
        TextView text = (TextView) dialog.findViewById(R.id.popupMessage);
        text.setTextColor(getResources().getColor(R.color.darkGray));
        text.setTextSize(22);
        text.setText(concatString.toString());

        // Add the close button
        Button dialogButton = (Button) dialog.findViewById(R.id.closePopupBtn);

        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }

        });

        dialog.show();
    }


    // When clicking the button "Result"
    public void onClickResult(View v) {
        callScoreResultPopup();
    }

    // When clicking the button "Reset"
    public void onClickReset(View v) {

        resetScores(2);
        currentRound = 1;
        showCurrentRound();

    }

    // update scoreDisplayTextView
    public void setScoreAtScoreViewWithTeamandScore (boolean isTeamA, int score) {

        if (isTeamA) {
            scoreView4TeamA.setText(String.valueOf(score));
        } else {
            scoreView4TeamB.setText(String.valueOf(score));
        }
    }

    // Reset all scores into 0
    public void resetScores(int teamIndex) {

        if (teamIndex == 0) { // Team A

            scoreTeamA = 0;
            setScoreAtScoreViewWithTeamandScore(true, scoreTeamA);

        } else if (teamIndex == 1) { // Team B

            scoreTeamB = 0;
            setScoreAtScoreViewWithTeamandScore(false, scoreTeamB);

        } else { // both

            scoreTeamA = 0;
            setScoreAtScoreViewWithTeamandScore(true, scoreTeamA);
            scoreTeamB = 0;
            setScoreAtScoreViewWithTeamandScore(false, scoreTeamB);
        }

    }


}