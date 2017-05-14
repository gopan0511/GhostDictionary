/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.ghost;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.InputStream;
import java.util.Random;


public class GhostActivity extends AppCompatActivity {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private static final String statuskey_fragment = "currentWordFragment";
    private static final String statuskey_Turn = "currentTurn";
    private static final String statuskey_GameStatus = "currentGameStatus";
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private Random random = new Random();

    Button challengeButton;
    TextView statusTextView, wordFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        wordFragment = (TextView) findViewById(R.id.ghostText);

        wordFragment.setText("Ghost");
        statusTextView = (TextView) findViewById(R.id.gameStatus);
        challengeButton = (Button) findViewById(R.id.challenge);
        challengeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String current = wordFragment.getText().toString();
                String possibleword = dictionary.getAnyWordStartingWith(current);
                if(dictionary.isWord(current) || possibleword == null)
                {
                    statusTextView.setText("User Wins!");

                }
                else
                {
                    wordFragment.setText(possibleword);
                    statusTextView.setText("Computer wins! Wrong Challenge!");

                }
                userTurn= false;
                challengeButton.setEnabled(false);
            }
        });
        AssetManager assetManager = getAssets();
        try{
            InputStream is = assetManager.open("words.txt");
            dictionary = new FastDictionary(is);
        }catch (Exception e){
            e.printStackTrace();
            Log.v("GHOST","words.txt not found");
        }

        onStart(null);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(statuskey_fragment,wordFragment.getText().toString());
        savedInstanceState.putString(statuskey_GameStatus,statusTextView.getText().toString());
        savedInstanceState.putBoolean(statuskey_Turn,userTurn);
        super.onSaveInstanceState(savedInstanceState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        wordFragment.setText(savedInstanceState.getString(statuskey_fragment));
        statusTextView.setText(savedInstanceState.getString(statuskey_GameStatus));
        userTurn = savedInstanceState.getBoolean(statuskey_Turn);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     * @param view
     * @return true
     */
    public boolean onStart(View view) {

        userTurn = random.nextBoolean();
        TextView text = (TextView) findViewById(R.id.ghostText);
        text.setText("");
        TextView label = (TextView) findViewById(R.id.gameStatus);
        if (userTurn) {
            label.setText(USER_TURN);

        } else {
            label.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }


    private void computerTurn() {
        TextView label = (TextView) findViewById(R.id.gameStatus);
        // Do computer turn stuff then make it the user's turn again
        /*
        Check if the fragment is a word with at least 4 characters. If so declare victory by updating the game status

         Use the dictionary's getAnyWordStartingWith method to get a possible longer word
                If such a word doesn't exist (method returns null), challenge the user's fragment and declare victory (you can't bluff this computer!)
                If such a word does exist, add the next letter of it to the fragment (remember the substring method in the Java string library)
         */

        String currentword = wordFragment.getText().toString();

        if(dictionary.isWord(currentword))
        {
            //Declare Victory
            statusTextView.setText("Valid word! Computer Wins!");

        }
        else
        {
            String possibleword = dictionary.getAnyWordStartingWith(currentword);
            if(possibleword == null)
            {
                //declare victory
                statusTextView.setText("Computer Wins by Challenge as no word is possible!");

            }
            else
            {
                //Add letter from possible word
                currentword+= possibleword.charAt(currentword.length());
                wordFragment.setText(currentword);
                label.setText(USER_TURN);
                challengeButton.setEnabled(true);
                userTurn = true;

            }
        }

    }

    /**
     * Handler for user key presses.
     * @param keyCode
     * @param event
     * @return whether the key stroke was handled.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        TextView label = (TextView) findViewById(R.id.gameStatus);
        char keyPressed = (char) event.getUnicodeChar(event.getMetaState());
        if(Character.isLetter(keyPressed) && userTurn)
        {

            String update = wordFragment.getText().toString()+Character.toString(keyPressed);
            wordFragment.setText(update);
            userTurn= false;
            challengeButton.setEnabled(false);
            label.setText(COMPUTER_TURN);
            computerTurn();

        }
        return super.onKeyUp(keyCode, event);
    }


}





