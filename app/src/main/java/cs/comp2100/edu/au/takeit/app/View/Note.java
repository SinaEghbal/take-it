/*
 * 2016.
 *  The content of this site is licensed under the Creative Commons Apache License 2.0.
 *  * Created by Sina Eghbal on 18/03/2016.
 */


package cs.comp2100.edu.au.takeit.app.View;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.net.Uri;
import android.widget.Toast;
import cs.comp2100.edu.au.takeit.app.Model.NoteDB;
import cs.comp2100.edu.au.takeit.app.Model.NoteDBHelper;
import cs.comp2100.edu.au.takeit.app.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/*This class handles whatever happens in the note class.*/
public class Note extends AppCompatActivity {

    private static final int SPEECH_RECOGNITION_REQ = 50;
    protected boolean saved;
    protected EditText txtTitle;
    protected EditText txtNote;
    protected int id;
    protected NoteDBHelper noteDBHelper;

    private static int RESULT_LOAD_IMAGE = 1;
    final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        saved = true;
        txtTitle = (EditText) findViewById(R.id.title_text);
        txtNote = (EditText) findViewById(R.id.note_text);
//        setBackgroundColor(getResources().getColor(R.color.rojo));
//        txtNote.setBackgroundResource(R.color.abc_primary_text_material_light);
//        txtNote.setBackgroundColor(R.color.secondary_text_default_material_dark);
        noteDBHelper = new NoteDBHelper(this);

        /*Checks whether we have a new note or we're calling an existing note.*/
        if (getIntent().getExtras() == null) {
            id = -1;
        } else {
            id = getIntent().getExtras().getInt("id");
            Cursor cursor = noteDBHelper.getNote(id);
            cursor.moveToFirst();
            StringBuilder note = new StringBuilder();
            note.append(cursor.getString(cursor.getColumnIndex(NoteDB.NoteEntry.COLUMN_NAME_NOTE)));
            while (note.toString().contains("[image]")) {
                int start = note.indexOf("[image]");
                int end = note.indexOf("[\\image]");

                txtNote.append(note.substring(0, start));
                String data = note.substring(start + 7, end);
                Uri d = Uri.parse(data);
                try {
                    addImageBetweenText(d);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                note.replace(0,end+8, "");
            }
            txtNote.append(note.toString());
//            txtNote.setBackgroundColor(Color.RED);
            txtTitle.setText(cursor.getString(cursor.getColumnIndex(NoteDB.NoteEntry.COLUMN_NAME_NOTE_TITLE)));
        }

        /*Change the saved flag to false if the note or its title are changed.*/
        txtNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                saved = false;
            }
        });

        txtTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                saved = false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();

        if (item.getItemId() == R.id.action_save) {
            if (!saved)
                try {
                    save(getCurrentFocus().getRootView());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            return true;
        } else if (item.getItemId() == R.id.action_discard) {
            finish();
            return true;
        } else if(item.getItemId() == R.id.action_attach_image) {
            Intent browseImage = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(browseImage, RESULT_LOAD_IMAGE);
            return true;
        } else if(item.getItemId() == R.id.action_take_photo) {
            Intent takePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePhoto, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            return true;
        } else if (item.getItemId() == R.id.action_share) {
            try {
                share(txtTitle.getText().toString(), txtNote.getText().toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (item.getItemId() == R.id.action_voice_recognition) {
            startVoiceRecognition();
        } else if (item.getItemId() == R.id.action_get_and_change_colour) {
            String note = txtNote.getText().toString();
            String[] colour = {"red", "green", "blue"};
            for (String current_colour: colour) {
                if (note.contains(current_colour)) {
                    if (current_colour == "blue") {
                        txtNote.setBackgroundColor(Color.BLUE);
                    } else if (current_colour == "green") {
                        txtNote.setBackgroundColor(Color.GREEN);

                    } else if (current_colour == "red") {
                        txtNote.setBackgroundColor(Color.RED);
                    } else {

                    }
                }
            }

        }
    return super.onOptionsItemSelected(item);
    }

    /*Implementation for the voice recognition feature which transcribes the human voice.*/
    private void startVoiceRecognition() {
        Intent voiceRecognitionIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        voiceRecognitionIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        voiceRecognitionIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        voiceRecognitionIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt));
        startActivityForResult(voiceRecognitionIntent, SPEECH_RECOGNITION_REQ);
    }

    @Override
    public void onBackPressed() {
        if (saved)
            super.onBackPressed();
        else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("Save your changes or discard them?");
            dialog.setNeutralButton("Cancel", null);
            dialog.setPositiveButton("Discard", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            dialog.setNegativeButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        save(getCurrentFocus().getRootView());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    finish();
                }
            });
            dialog.setCancelable(true);
            dialog.create().show();
        }
    }

    /*The method that is being called after capturing a photo or loading it from the memory*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            try {
                addImageBetweenText(data.getData());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    addImageBetweenText(data.getData());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed!!!
            }
        } else if (requestCode == SPEECH_RECOGNITION_REQ) {
            if (resultCode == RESULT_OK) {
                ArrayList<String> recognizedSpeech = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                StringBuilder sb = new StringBuilder().append(" ");
                for (int i = 0; i < recognizedSpeech.size(); i++) {
                    sb.append(recognizedSpeech.get(i) + " ");
                }
                txtNote.append(sb.toString());
            } else {
                //Well, results is not ok!!!
            }
        }
    }

    /*Gets a URI and insert the image at its location to the EditText.*/
    public void addImageBetweenText(Uri data) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(data);
        Drawable drawable = Drawable.createFromStream(inputStream, "camera");

        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        ImageSpan imageSpan = new ImageSpan(drawable);

        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(txtNote.getText());

        int selStart = txtNote.getSelectionStart();

        builder.replace(txtNote.getSelectionStart(), txtNote.getSelectionEnd(), "[image]" + data.toString() + "[\\image]");

        builder.setSpan(imageSpan, selStart, selStart + data.toString().length() + 15, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtNote.setText(builder);
        txtNote.setSelection(selStart);
  }

    /*Saves the note. This can be either saving a new note or updating an existing note.*/
    public void save(View view) throws IOException {
        if (id == -1) {
            noteDBHelper.insertNote(txtTitle.getText().toString(), txtNote.getText().toString(), new Date());
        } else {
            noteDBHelper.updateNote(id,txtTitle.getText().toString(), txtNote.getText().toString());
        }
        saved = true;
        Toast.makeText(getApplicationContext(), "saved", Toast.LENGTH_SHORT).show();
        InputMethodManager inputManager = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    /*Share the notes on your favorite social media or email.*/
    public void share(String title, String note) throws IOException {
        if (!saved) {
            save(getCurrentFocus().getRootView());
            Toast.makeText(getApplicationContext(), "Note saved", Toast.LENGTH_SHORT);
        }
        startActivity(Intent.createChooser(createShareIntent(title, note),"Share your note"));
    }

    /*Creates a share intent which is being used by the share function when we are sharing a note.
    * Also parses the body of our note to look for image tags and pass them as images if found.*/
    public static Intent createShareIntent(String title, String note) {
        Intent share = new Intent(Intent.ACTION_SEND_MULTIPLE);
        share.setType("*/*");
        share.putExtra(Intent.EXTRA_SUBJECT, title);
        StringBuilder body = new StringBuilder().append(note);
        ArrayList<Uri> images = new ArrayList<Uri>();
        while (body.toString().contains("[image]")) {
            Uri uri = Uri.parse(body.substring(body.indexOf("[image]")+7, body.indexOf("[\\image]")));
            images.add(uri);
            body.replace(body.indexOf("[image]"), body.indexOf("[\\image]")+8, "");
        }
        share.putParcelableArrayListExtra(Intent.EXTRA_STREAM, images);
        share.putExtra(Intent.EXTRA_TEXT, body.toString());
        return share;
    }

}
