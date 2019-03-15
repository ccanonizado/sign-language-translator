package ccanonizado.signlanguagetranslator;

import android.content.Context;
import android.graphics.drawable.Drawable;
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

        translateButton = findViewById(R.id.translateButtonTS);
        backButton = findViewById(R.id.backButton);
        nextButton = findViewById(R.id.nextButton);
        resultReference = findViewById(R.id.resultReference);
        resultImage = findViewById(R.id.resultTS);
        inputText = findViewById(R.id.inputText);

        backButton.setVisibility(View.INVISIBLE);
        nextButton.setVisibility(View.INVISIBLE);

        // user has not translated yet
        resultCount = -1;

        // for getting the focus
        inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        // special characters regex
        regex = Pattern.compile("[^a-zA-Z0-9\\s]");

        // starting words / phrases from vocabulary
        vocabulary = Arrays.asList(
                "yes","no","you","lol","that","hi","hello","halt",
                "stop","equality","equal","okay","question","really",
                "later","gotcha","good","thank","i"
        );

        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // all lower case before processing
                input = inputText.getText().toString().toLowerCase();

                if (input.length() > 0){

                    // if not focused anymore - hide keyboard
                    if (getWindow().getCurrentFocus() != null)
                        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);

                    resultReference.setText(inputText.getText().toString());
                    inputText.setText(null);
                    resultSigns = textToSign(input);
                    resultCount = 0;

                    if (resultSigns.size() == 1) {
                        backButton.setVisibility(View.INVISIBLE);
                        nextButton.setVisibility(View.INVISIBLE);
                    }

                    else {
                        backButton.setVisibility(View.INVISIBLE);
                        nextButton.setVisibility(View.VISIBLE);
                    }

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
                if (resultCount == 0)
                    backButton.setVisibility(View.INVISIBLE);

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
                if (resultCount == resultSigns.size()-1)
                    nextButton.setVisibility(View.INVISIBLE);

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

        // special characters - invalid
        if (specialCharacters.find()){
            result.add("sign_message_special");
        }

        else {
            String[] line = text.split("\\s+");
            int remaining;

            for (int i=0; i<line.length; i++){
                remaining = line.length-1-i;

                // first layer - checks if part of words / phrases available
                if (vocabulary.contains(line[i])) {
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

                    else
                        result.add("sign_"+line[i]);
                }

                else
                    for (int j=0; j<line[i].length(); j++)
                        result.add("sign_"+Character.toString(line[i].charAt(j)));
            }
        }

        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) this.finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
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
