package ccanonizado.signlanguagetranslator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SignToText extends AppCompatActivity {
    private Button translateButton;
    private Button resetButton;
    private TextView hint;
    private TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_to_text);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        translateButton = findViewById(R.id.translateButtonST);
        resetButton = findViewById(R.id.resetButton);

        hint = findViewById(R.id.stHint);
        result = findViewById(R.id.stResult);

        result.setVisibility(View.INVISIBLE);
        resetButton.setVisibility(View.INVISIBLE);

        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translateButton.setVisibility(View.GONE);
                hint.setVisibility(View.GONE);
                resetButton.setVisibility(View.VISIBLE);
                result.setVisibility(View.VISIBLE);
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.setText("''");
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
