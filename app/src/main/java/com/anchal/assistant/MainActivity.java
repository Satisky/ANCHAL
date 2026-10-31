
package com.anchal.assistant;

import android.app.Activity;
import android.os.Bundle;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.RecognitionListener;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.graphics.Color;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {

    private TextView response;
    private EditText input;
    private SpeechRecognizer recognizer;
    private TextToSpeech tts;

    private static final int MIC_PERMISSION = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createInterface();

        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(new Locale("hi", "IN"));
            }
        });

        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            recognizer = SpeechRecognizer.createSpeechRecognizer(this);
            recognizer.setRecognitionListener(new RecognitionListener() {

                @Override
                public void onReadyForSpeech(Bundle params) {
                    response.setText("Listening...");
                }

                @Override
                public void onBeginningOfSpeech() {}

                @Override
                public void onRmsChanged(float rmsdB) {}

                @Override
                public void onBufferReceived(byte[] buffer) {}

                @Override
                public void onEndOfSpeech() {
                    response.setText("Processing...");
                }

                @Override
                public void onError(int error) {
                    response.setText("Voice not understood. Please try again.");
                }

                @Override
                public void onResults(Bundle results) {
                    ArrayList<String> matches =
                        results.getStringArrayList(
                            SpeechRecognizer.RESULTS_RECOGNITION
                        );

                    if (matches != null && !matches.isEmpty()) {
                        input.setText(matches.get(0));
                        processCommand(matches.get(0));
                    }
                }

                @Override
                public void onPartialResults(Bundle results) {}

                @Override
                public void onEvent(int eventType, Bundle params) {}
            });
        }
    }

    private void createInterface() {

        LinearLayout main = new LinearLayout(this);
        main.setOrientation(LinearLayout.VERTICAL);
        main.setPadding(28, 35, 28, 25);
        main.setGravity(Gravity.CENTER_HORIZONTAL);
        main.setBackgroundColor(Color.rgb(5, 12, 28));

        TextView title = new TextView(this);
        title.setText("A N C H A L");
        title.setTextSize(32);
        title.setTextColor(Color.rgb(0, 220, 255));
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 30, 0, 10);

        TextView subtitle = new TextView(this);
        subtitle.setText("YOUR PERSONAL AI ASSISTANT");
        subtitle.setTextColor(Color.LTGRAY);
        subtitle.setTextSize(13);
        subtitle.setGravity(Gravity.CENTER);

        response = new TextView(this);
        response.setText("Hello! I am ANCHAL.\nHow can I help you?");
        response.setTextColor(Color.WHITE);
        response.setTextSize(19);
        response.setGravity(Gravity.CENTER);
        response.setPadding(15, 35, 15, 35);

        input = new EditText(this);
        input.setHint("Type your command...");
        input.setTextColor(Color.WHITE);
        input.setHintTextColor(Color.GRAY);
        input.setSingleLine(true);

        Button sendButton = new Button(this);
        sendButton.setText("SEND");
        sendButton.setOnClickListener(v -> {
            String command = input.getText().toString().trim();
            if (!command.isEmpty()) {
                processCommand(command);
            }
        });

        Button micButton = new Button(this);
        micButton.setText("🎤 SPEAK");
        micButton.setTextSize(18);
        micButton.setOnClickListener(v -> startListening());

        main.addView(title);
        main.addView(subtitle);
        main.addView(response, new LinearLayout.LayoutParams(
            -1, 0, 1
        ));
        main.addView(input, new LinearLayout.LayoutParams(
            -1, -2
        ));
        main.addView(sendButton);
        main.addView(micButton);

        setContentView(main);
    }

    private void startListening() {

        if (recognizer == null) {
            response.setText("Speech recognition is not available.");
            return;
        }

        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                new String[]{Manifest.permission.RECORD_AUDIO},
                MIC_PERMISSION
            );
            return;
        }

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        );
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            "hi-IN"
        );
        intent.putExtra(
            RecognizerIntent.EXTRA_MAX_RESULTS,
            3
        );

        recognizer.startListening(intent);
    }

    private void processCommand(String command) {

        String text = command.toLowerCase(Locale.ROOT).trim();
        String answer;

        if (text.contains("hello") ||
            text.contains("hi") ||
            text.contains("namaste") ||
            text.contains("नमस्ते")) {

            answer = "Hello! Main ANCHAL hoon. Bataiye, kya help chahiye?";

        } else if (text.contains("time") ||
                   text.contains("समय") ||
                   text.contains("kitne baje")) {

            answer = "Abhi time hai " +
                new SimpleDateFormat("hh:mm a", Locale.getDefault())
                .format(new Date());

        } else if (text.contains("date") ||
                   text.contains("tarikh") ||
                   text.contains("तारीख")) {

            answer = "Aaj ki date hai " +
                new SimpleDateFormat("dd MMMM yyyy",
                new Locale("hi", "IN")).format(new Date());

        } else if (text.contains("youtube")) {

            answer = "Opening YouTube.";
            speak(answer);

            Intent launch = getPackageManager()
                .getLaunchIntentForPackage("com.google.android.youtube");

            if (launch != null) {
                startActivity(launch);
            } else {
                Intent web = new Intent(
                    Intent.ACTION_VIEW,
                    android.net.Uri.parse("https://www.youtube.com")
                );
                startActivity(web);
            }
            return;

        } else if (text.contains("how are you") ||
                   text.contains("kaise ho")) {

            answer = "Main bilkul ready hoon! Aap bataiye?";

        } else if (text.contains("your name") ||
                   text.contains("tumhara naam")) {

            answer = "Mera naam ANCHAL hai. Main aapka personal assistant hoon.";

        } else {

            answer = "Abhi main ye command nahi samajh paayi. Main dheere dheere aur smart banungi.";
        }

        response.setText(answer);
        speak(answer);
    }

    private void speak(String message) {
        if (tts != null) {
            tts.speak(
                message,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "ANCHAL_RESPONSE"
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults) {

        super.onRequestPermissionsResult(
            requestCode, permissions, grantResults
        );

        if (requestCode == MIC_PERMISSION &&
            grantResults.length > 0 &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            startListening();

        } else if (requestCode == MIC_PERMISSION) {
            response.setText("Microphone permission is required for voice commands.");
        }
    }

    @Override
    protected void onDestroy() {
        if (recognizer != null) {
            recognizer.destroy();
        }

        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }

        super.onDestroy();
    }
}
