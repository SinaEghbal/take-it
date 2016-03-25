package cs.comp2100.edu.au.takeit.app.View;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
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
import java.util.Date;

public class Note extends AppCompatActivity {

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
        noteDBHelper = new NoteDBHelper(this);

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
            txtTitle.setText(cursor.getString(cursor.getColumnIndex(NoteDB.NoteEntry.COLUMN_NAME_NOTE_TITLE)));
        }
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

        //noinspection SimplifiableIfStatement
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
        }


    return super.onOptionsItemSelected(item);
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
                // Image capture failed, advise user
            }
        }
    }


    private void addImageBetweenText(Uri data) throws IOException {
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

    private void save(View view) throws IOException {
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

}
