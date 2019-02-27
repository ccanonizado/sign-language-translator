package ccanonizado.signlanguagetranslator;

import android.content.Intent;
import android.media.Image;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class HomeScreen extends AppCompatActivity {
    private Button signToTextButton;
    private Button textToSignButton;
    private Button vocabularyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        getSupportActionBar().hide();

        signToTextButton = findViewById(R.id.signToTextButton);
        textToSignButton = findViewById(R.id.textToSignButton);
        vocabularyButton = findViewById(R.id.vocabularyButton);

        signToTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSignToText();
            }
        });

        textToSignButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTextToSign();
            }
        });

        vocabularyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openVocabulary();
            }
        });
    }

    public void openSignToText() {
        Intent intent = new Intent(this, SignToText.class);
        startActivity(intent);
    }

    public void openTextToSign() {
        Intent intent = new Intent(this, TextToSign.class);
        startActivity(intent);
    }

    public void openVocabulary() {
        Intent intent = new Intent(this, Vocabulary.class);
        startActivity(intent);
    }
}
