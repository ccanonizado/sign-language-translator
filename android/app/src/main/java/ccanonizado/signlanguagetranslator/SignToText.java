package ccanonizado.signlanguagetranslator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.camerakit.CameraKitView;

public class SignToText extends AppCompatActivity {
    private Button translateButton;
    private Button resetButton;
    private TextView hint;
    private TextView result;
    private CameraKitView cameraKitView;

    // translation variables
    private Boolean translated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_to_text);

        // show back button
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        translateButton = findViewById(R.id.translateButtonST);
        resetButton = findViewById(R.id.resetButton);
        hint = findViewById(R.id.stHint);
        result = findViewById(R.id.stResult);
        cameraKitView = findViewById(R.id.camera);

        result.setVisibility(View.INVISIBLE);
        resetButton.setVisibility(View.INVISIBLE);

        translated = false;

        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translateButton.setVisibility(View.INVISIBLE);
                hint.setVisibility(View.INVISIBLE);
                resetButton.setVisibility(View.VISIBLE);
                result.setVisibility(View.VISIBLE);
                translated = true;
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

    @Override
    public void onBackPressed() {
        if (translated) {
            translateButton.setVisibility(View.VISIBLE);
            hint.setVisibility(View.VISIBLE);
            resetButton.setVisibility(View.INVISIBLE);
            result.setVisibility(View.INVISIBLE);
            translated = false;
        }
        else
            super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        cameraKitView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraKitView.onResume();
    }

    @Override
    protected void onPause() {
        cameraKitView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        cameraKitView.onStop();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        cameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
