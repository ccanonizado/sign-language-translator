package ccanonizado.signlanguagetranslator;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextToSign extends AppCompatActivity {
    
    // widget variables
    private Button translateButton;
    private ImageButton backButton;
    private ImageButton nextButton;
    private TextView resultReference;
    private ImageView resultImage;
    private EditText inputText;

    // translation variables
    private String input;
    private Pattern regex;
    private Matcher specialCharacters;
    private List<String> vocabulary;
    private List<String> resultSigns;
    private Integer resultCount;
    private Integer imageId;
    private InputMethodManager inputManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_to_sign);

        // show back button
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get references of widgets
        translateButton = findViewById(R.id.translateButtonTS);
        backButton = findViewById(R.id.backButton);
        nextButton = findViewById(R.id.nextButton);
        resultReference = findViewById(R.id.resultReference);
        resultImage = findViewById(R.id.resultTS);
        inputText = findViewById(R.id.inputText);

        // hide other widgets
        backButton.setVisibility(View.INVISIBLE);
        nextButton.setVisibility(View.INVISIBLE);

        // initialize to -1 (user not translated yet)
        resultCount = -1;

        // get keyboard focus
        inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        // store special characters regex
        regex = Pattern.compile("[^a-zA-Z0-9\\s]");

        // list words / phrases from vocabulary
        vocabulary = Arrays.asList(
                "yes","no","you","lol","that","hi","hello","halt",
                "stop","equality","equal","okay","question","really",
                "later","gotcha","good","thank","i"
        );

        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // convert input to lower case
                input = inputText.getText().toString().toLowerCase();

                if (input.length() > 0){

                    // if not focused anymore - hide keyboard
                    if (getWindow().getCurrentFocus() != null)
                        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);

                    // show letters / words being translated and reset input field
                    resultReference.setText(inputText.getText().toString());
                    inputText.setText(null);

                    // get result of translation
                    resultSigns = textToSign(input);
                    resultCount = 0;

                    // hide both left and right buttons
                    if (resultSigns.size() == 1) {
                        backButton.setVisibility(View.INVISIBLE);
                        nextButton.setVisibility(View.INVISIBLE);
                    }

                    // show right button first
                    else {
                        backButton.setVisibility(View.INVISIBLE);
                        nextButton.setVisibility(View.VISIBLE);
                    }

                    // show current image
                    imageId = getResources().getIdentifier(
                            resultSigns.get(resultCount), "drawable", getPackageName()
                    );
                    resultImage.setImageResource(imageId);
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultCount--;
                nextButton.setVisibility(View.VISIBLE);

                // check bounds
                if (resultCount == 0)
                    backButton.setVisibility(View.INVISIBLE);

                // show current image
                imageId = getResources().getIdentifier(
                        resultSigns.get(resultCount), "drawable", getPackageName()
                );
                resultImage.setImageResource(imageId);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultCount++;
                backButton.setVisibility(View.VISIBLE);

                // check bounds
                if (resultCount == resultSigns.size()-1)
                    nextButton.setVisibility(View.INVISIBLE);

                // show current image
                imageId = getResources().getIdentifier(
                        resultSigns.get(resultCount), "drawable", getPackageName()
                );
                resultImage.setImageResource(imageId);
            }
        });
    }

    public List<String> textToSign(String text){
        specialCharacters = regex.matcher(text);

        List<String> result = new ArrayList<>();

        // if there is a special character - invalid
        if (specialCharacters.find()){
            result.add("sign_message_special");
        }

        else {
            String[] line = text.split("\\s+");
            int remaining;

            for (int i=0; i<line.length; i++){
                remaining = line.length-1-i;

                // if current word is part of vocabulary
                if (vocabulary.contains(line[i])) {

                    /*

                        This block checks if the phrases are satisfied
                        if not - just spell out the words given

                        Phrases available:
                        I love you
                        I hate you
                        thank you
                        good job

                    */

                    if (line[i].equals("i")) {
                        if (remaining < 2)
                            result.add("sign_"+line[i]);
                        else {
                            if (line[i+1].equals("love")) {
                                if (line[i+2].equals("you"))
                                    result.add("sign_i_love_you");
                            }
                            else if (line[i+1].equals("hate")) {
                                if (line[i+2].equals("you"))
                                    result.add("sign_i_hate_you");
                            }
                            i += 2;
                        }
                    }

                    else if (line[i].equals("thank")) {
                        if (remaining < 1) {
                            for (int j=0; j<line[i].length(); j++)
                                result.add("sign_"+Character.toString(line[i].charAt(j)));
                        }

                        else {
                            if (line[i+1].equals("you"))
                                result.add("sign_thank_you");
                            else {
                                for (int j=0; j<line[i].length(); j++)
                                    result.add("sign_"+Character.toString(line[i].charAt(j)));
                            }
                        }
                        i += 1;
                    }

                    else if (line[i].equals("good")) {
                        if (remaining < 1) {
                            for (int j=0; j<line[i].length(); j++)
                                result.add("sign_"+Character.toString(line[i].charAt(j)));
                        }

                        else {
                            if (line[i+1].equals("job"))
                                result.add("sign_good_job");
                            else {
                                for (int j=0; j<line[i].length(); j++)
                                    result.add("sign_"+Character.toString(line[i].charAt(j)));
                            }
                        }
                        i += 1;
                    }

                    // add word to list of results
                    else
                        result.add("sign_"+line[i]);
                }

                /*

                    Either it is a word that does not exist from the vocabulary
                    or it is a singular letter / number - in this case then
                    simply add every character to the results

                */
                else
                    for (int j=0; j<line[i].length(); j++)
                        result.add("sign_"+Character.toString(line[i].charAt(j)));
            }
        }

        return result;
    }

    // home button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) this.finish();
        return super.onOptionsItemSelected(item);
    }

    // back button
    @Override
    public void onBackPressed() {

        // if user has translated once - revert to default view
        if (resultCount != -1) {
            backButton.setVisibility(View.INVISIBLE);
            nextButton.setVisibility(View.INVISIBLE);
            resultImage.setImageResource(R.drawable.sign_message_default);
            resultReference.setText(null);
            inputText.setText(null);
            resultCount = -1;
        }
        else
            super.onBackPressed();
    }
}
