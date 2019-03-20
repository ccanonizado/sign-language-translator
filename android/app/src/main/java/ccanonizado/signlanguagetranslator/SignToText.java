package ccanonizado.signlanguagetranslator;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wonderkiln.camerakit.CameraListener;
import com.wonderkiln.camerakit.CameraView;

import java.util.ArrayList;
import java.util.List;

public class SignToText extends AppCompatActivity {
    private Button translateButton;
    private Button resetButton;
    private TextView hint;
    private TextView result;
    private CameraView cameraView;

    // translation variables
    private Boolean translated;
    static Classifier classifier;
    static final int INPUT_SIZE = 300;
    private static final int IMAGE_MEAN = 128;
    private static final float IMAGE_STD = 128f;
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

        translateButton = findViewById(R.id.translateButtonST);
        resetButton = findViewById(R.id.resetButton);
        hint = findViewById(R.id.stHint);
        result = findViewById(R.id.stResult);
        cameraView = findViewById(R.id.camera);

        result.setVisibility(View.INVISIBLE);
        resetButton.setVisibility(View.INVISIBLE);

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
//                bitmap = ImageHelper.getRotatedImage(bitmap, 90);
                results = classifier.recognizeImage(bitmap);
                Log.i("Results",Integer.toString(results.size()));
                new Handler(Looper.getMainLooper()).post(new Runnable(){
                    @Override
                    public void run() {
                        result.setText("'"+results.get(0).getTitle()+"'");
                    }
                });
            }
        });

        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translateButton.setVisibility(View.INVISIBLE);
                hint.setVisibility(View.INVISIBLE);
                resetButton.setVisibility(View.VISIBLE);
                result.setVisibility(View.VISIBLE);
                translated = true;
                cameraView.captureImage();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.setText("''");
            }
        });
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
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        cameraView.stop();
        super.onPause();
    }
}
