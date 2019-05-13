package ccanonizado.signlanguagetranslator;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wonderkiln.camerakit.CameraListener;
import com.wonderkiln.camerakit.CameraView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SignToText extends AppCompatActivity {

    // widget variables
    private Button translateButton;
    private TextView hint;
    private TextView mainResult;
    private TextView otherResults;
    private CameraView cameraView;

    // translation variables
    private Boolean translated;
    static Classifier classifier;
    static final int INPUT_SIZE = 300;
    private static final int IMAGE_MEAN = 128;
    private static final float IMAGE_STD = 128;
    private static final String INPUT_NAME = "Mul";
    private static final String OUTPUT_NAME = "final_result";
    private static final String MODEL_FILE = "file:///android_asset/graph.pb";
    private static final String LABEL_FILE = "file:///android_asset/labels.txt";
    static List<Classifier.Recognition> results;
    static Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_to_text);

        // show back button
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get references of widgets
        translateButton = findViewById(R.id.translateButtonST);
        hint = findViewById(R.id.stHint);
        mainResult = findViewById(R.id.stMainResult);
        otherResults = findViewById(R.id.stOtherResults);
        cameraView = findViewById(R.id.camera);

        // hide result first
        mainResult.setVisibility(View.INVISIBLE);
        otherResults.setVisibility(View.INVISIBLE);

        // initialize variables and model
        translated = false;
        results = new ArrayList<>();
        initializeModel();

        cameraView.setCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] picture) {

                bitmap = BitmapFactory.decodeByteArray(
                        picture,
                        0,
                        picture.length);
                bitmap = Bitmap.createScaledBitmap(
                        bitmap,
                        INPUT_SIZE,
                        INPUT_SIZE,
                        false);

                // call tensorflow image recognition
                results = classifier.recognizeImage(bitmap);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        translateButton.setText("Translate");
                        translateButton.setEnabled(true);
                        translateButton.setClickable(true);
                    }
                });

                // use UI thread to make changes
                if (results.size() > 0)
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setResultsText(results);
                        }
                    });
            }
        });

        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // hide and show other widgets
                hint.setVisibility(View.INVISIBLE);
                mainResult.setVisibility(View.VISIBLE);
                otherResults.setVisibility(View.VISIBLE);
                
                // user has translated then take picture
                translated = true;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        translateButton.setText("Translating");
                        translateButton.setEnabled(false);
                        translateButton.setClickable(false);
                    }
                });

                mainResult.setText(null);
                otherResults.setText(null);
                cameraView.captureImage();
            }
        });
    }

    private void setResultsText(List<Classifier.Recognition> results) {
        String mainResultString = "";
        String otherResultsString = "";
        String confidence;

        switch (results.get(0).getTitle()) {
            case "hellohi":
                mainResultString += "hello / hi";
                break;
            case "okaygoodjob":
                mainResultString += "okay / good job";
                break;
            default:
                mainResultString += results.get(0).getTitle();
                break;
        }

        confidence = String.format(Locale.US, "%.2f",
                results.get(0).getConfidence() * 100);
        mainResultString += ": " + confidence + "%";
        mainResult.setText(mainResultString);

        for(int i=1; i<results.size(); i++) {
            // format other labels
            switch (results.get(i).getTitle()) {
                case "hellohi":
                    otherResultsString += "hello / hi";
                    break;
                case "okaygoodjob":
                    otherResultsString += "okay / good job";
                    break;
                default:
                    otherResultsString += results.get(i).getTitle();
                    break;
            }

            if (i != results.size()) {
                confidence = String.format(Locale.US, "%.2f",
                        results.get(i).getConfidence() * 100);
                otherResultsString += ": " + confidence + "%\n";
            }

            else {
                confidence = String.format(Locale.US, "%.2f",
                        results.get(i).getConfidence() * 100);
                otherResultsString += ": " + confidence + "%";
            }
        }

        otherResults.setText(otherResultsString);
    }

    private void initializeModel() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    classifier = TensorFlowImageClassifier.create(
                            getAssets(),
                            MODEL_FILE,
                            LABEL_FILE,
                            INPUT_SIZE,
                            INPUT_SIZE,
                            IMAGE_MEAN,
                            IMAGE_STD,
                            INPUT_NAME,
                            OUTPUT_NAME);
                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing TensorFlow Model!", e);
                }
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        cameraView.stop();
        super.onPause();
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
        if (translated) {
            mainResult.setText(null);
            otherResults.setText(null);
            hint.setVisibility(View.VISIBLE);
            mainResult.setVisibility(View.INVISIBLE);
            otherResults.setVisibility(View.INVISIBLE);
            translated = false;
        }
        else
            super.onBackPressed();
    }
}
