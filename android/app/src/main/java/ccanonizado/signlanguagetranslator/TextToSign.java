package ccanonizado.signlanguagetranslator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class TextToSign extends AppCompatActivity {
    private Button translateButton;
    private EditText inputText;
    private String input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_to_sign);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        translateButton = findViewById(R.id.translateButtonTS);
        inputText = findViewById(R.id.inputText);

        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input = inputText.getText().toString();
                if (input.length() > 0){
                    inputText.setText(null);
                    // do translation
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) this.finish();
        return super.onOptionsItemSelected(item);
    }
}
