package edu.ncsu.dialogueclassfall2013.androidspeechdemo;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends ActionBarActivity implements TextToSpeech.OnInitListener{

    public final static String EXTRA_MESSAGE = "edu.ncsu.dialogueclassfall2013.androidspeechdemo.MESSAGE";

    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;
    private TextToSpeech tts;

    public void checkVoiceRecognition() {
        // Check if voice recognition is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0) {
            ((Button) findViewById(R.id.listen_button)).setEnabled(false);
            Toast.makeText(this, "Voice recognizer not present",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /** Called when the user clicks the Send button */
    public void listen(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                ArrayList<String> textMatchList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (!textMatchList.isEmpty()) {
                    String firstMatch = textMatchList.get(0);
                    EditText editText = (EditText) findViewById(R.id.edit_message);
                    editText.setText(firstMatch);
                }
            }
        } else if(resultCode == RecognizerIntent.RESULT_AUDIO_ERROR){
            showToastMessage("Audio Error");
        } else if(resultCode == RecognizerIntent.RESULT_CLIENT_ERROR){
            showToastMessage("Client Error");
        } else if(resultCode == RecognizerIntent.RESULT_NETWORK_ERROR){
            showToastMessage("Network Error");
        } else if(resultCode == RecognizerIntent.RESULT_NO_MATCH){
            showToastMessage("No Match");
        } else if(resultCode == RecognizerIntent.RESULT_SERVER_ERROR){
            showToastMessage("Server Error");
        }
    }

    public void speak(View view) {
        if (tts!=null) {
            EditText editText = (EditText) findViewById(R.id.edit_message);
            String text = editText.getText().toString();
            if (text!=null) {
                if (!tts.isSpeaking()) {
                    tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        }
    }

    public void onInit(int code) {
        if (code==TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.getDefault());
        } else {
            tts = null;
            showToastMessage("Failed to initialize TTS engine.");
        }
    }

    /**
     * Helper method to show the toast message
     **/
    void showToastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        checkVoiceRecognition();
        tts = new TextToSpeech(this, this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

}
