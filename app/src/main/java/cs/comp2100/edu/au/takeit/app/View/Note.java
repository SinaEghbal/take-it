package cs.comp2100.edu.au.takeit.app.View;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cs.comp2100.edu.au.takeit.app.Model.NoteDB;
import cs.comp2100.edu.au.takeit.app.Model.NoteDBHelper;
import cs.comp2100.edu.au.takeit.app.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Note extends AppCompatActivity {
    private static final int MEDIA_TYPE_IMAGE = 1;
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
//        if (savedInstanceState == null) {
            id = -1;
//            Toast.makeText(getApplicationContext(), "new", Toast.LENGTH_LONG).show();
        } else {
            id = getIntent().getExtras().getInt("id");
            Cursor cursor = noteDBHelper.getNote(id);
            cursor.moveToFirst();
            txtNote.setText(cursor.getString(cursor.getColumnIndex(NoteDB.NoteEntry.COLUMN_NAME_NOTE)));
            txtTitle.setText(cursor.getString(cursor.getColumnIndex(NoteDB.NoteEntry.COLUMN_NAME_NOTE_TITLE)));
//            Toast.makeText(getApplicationContext(), "update", Toast.LENGTH_LONG).show();
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
//            if (id == -1) {
//                noteDBHelper.insertNote(txtTitle.getText().toString(), txtNote.getText().toString(), new Date());
//            } else {
//                noteDBHelper.updateNote(id,txtTitle.getText().toString(),txtNote.getText().toString());
//            }
            save(getCurrentFocus().getRootView());
            saved = true;
            return true;
        } else if (id == R.id.action_discard) {
            finish();
            return true;
        } else if(id == R.id.action_attach_image) {
            Intent browseImage = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(browseImage, RESULT_LOAD_IMAGE);
            return true;
        } else if(id == R.id.action_take_photo) {
            Intent takePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

//            Uri fileUri = Uri.fromFile(getOutputMediaFile(MEDIA_TYPE_IMAGE));
//            takePhoto.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(takePhoto, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            return true;
        }


    return super.onOptionsItemSelected(item);
    }

    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == 1){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
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
//                    Toast.makeText(getApplicationContext(), "saveee", Toast.LENGTH_LONG).show();
                    save(getWindow().getDecorView().getRootView());
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
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Drawable image = new BitmapDrawable(getResources(), picturePath);
            addImageBetweenText(image);
//            txtNote.setCompoundDrawablesWithIntrinsicBounds(0, 0, image, 0);


//            ImageView imageView = (ImageView) findViewById(R.id.imgView);
//            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        } else if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
//                Toast.makeText(this, "Image saved to:\n" +
                try {
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    Drawable image = Drawable.createFromStream(inputStream, "camera");
                    addImageBetweenText(image);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
//                Drawable image = new BitmapDrawable(getResources(), data.getData());
//                        data.getData(), Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }
    }

    private void addImageBetweenText(Drawable drawable) {
        drawable .setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

        int selectionCursor = txtNote.getSelectionStart();
        txtNote.getText().insert(selectionCursor, "build/intermediates/exploded-aar/com.android.support/support-vector-drawable/24.0.0-alpha1/res");
        selectionCursor = txtNote.getSelectionStart();

        SpannableStringBuilder builder = new SpannableStringBuilder(txtNote.getText());
        builder.setSpan(new ImageSpan(drawable), selectionCursor - "build/intermediates/exploded-aar/com.android.support/support-vector-drawable/24.0.0-alpha1/res".length(), selectionCursor,                                                   Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtNote.setText(builder);
        txtNote.setSelection(selectionCursor);
    }

    private void save(View view) {
        if (id == -1) {
            noteDBHelper.insertNote(txtTitle.getText().toString(), txtNote.getText().toString(), new Date());
        } else {
            noteDBHelper.updateNote(id,txtTitle.getText().toString(),txtNote.getText().toString());
        }
        saved = true;
        InputMethodManager inputManager = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

}
