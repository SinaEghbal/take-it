package cs.comp2100.edu.au.takeit.app.View;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.net.Uri;
import cs.comp2100.edu.au.takeit.app.DBAdapter.Serialize;
import cs.comp2100.edu.au.takeit.app.Model.NoteDB;
import cs.comp2100.edu.au.takeit.app.Model.NoteDBHelper;
import cs.comp2100.edu.au.takeit.app.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Note extends AppCompatActivity {
//    private static final int MEDIA_TYPE_IMAGE = 1;
    protected boolean saved;
    protected EditText txtTitle;
    protected EditText txtNote;
    protected int id;
    protected int img_id = 0;
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
            if (id == -1) {
                noteDBHelper.insertNote(txtTitle.getText().toString(), txtNote.getText().toString(), new Date());
            } else {
                noteDBHelper.updateNote(id,txtTitle.getText().toString(),txtNote.getText().toString());
            }
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

//    private static File getOutputMediaFile(int type){
//        // To be safe, you should check that the SDCard is mounted
//        // using Environment.getExternalStorageState() before doing this.
//
//        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES), "Picture Note");
//        // This location works best if you want the created images to be shared
//        // between applications and persist after your app has been uninstalled.
//
//        // Create the storage directory if it does not exist
//        if (! mediaStorageDir.exists()){
//            if (! mediaStorageDir.mkdirs()){
//                Log.d("Picture Note", "failed to create directory");
//                return null;
//            }
//        }
//
//        // Create a media file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        File mediaFile;
//        if (type == 1){
//            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
//                    "IMG_"+ timeStamp + ".jpg");
//        } else {
//            return null;
//        }
//
//        return mediaFile;
//    }

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
                    try {
                        save(getWindow().getDecorView().getRootView());
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
//            Uri selectedImage = data.getData();
//            String[] filePathColumn = {MediaStore.Images.Media.DATA};
//
//            Cursor cursor = getContentResolver().query(selectedImage,
//                    filePathColumn, null, null, null);
//            cursor.moveToFirst();
//
//            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//            String picturePath = cursor.getString(columnIndex);
//            cursor.close();
//
//            Drawable image = new BitmapDrawable(getResources(), picturePath);
            try {
                addImageBetweenText(data.getData());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
//            txtNote.setCompoundDrawablesWithIntrinsicBounds(0, 0, image, 0);


//            ImageView imageView = (ImageView) findViewById(R.id.imgView);
//            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        } else if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
//                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
//                    Drawable image = Drawable.createFromStream(inputStream, "camera");
//                    addImageBetweenText(image);
                    addImageBetweenText(data.getData());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
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


    private void addImageBetweenText(Uri data) throws IOException {

//        String[] filePathColumn = {MediaStore.Images.Media.DATA};

//        Cursor cursor = getContentResolver().query(data,
//                filePathColumn, null, null, null);
//        cursor.moveToFirst();
//
//        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//        String picturePath = cursor.getString(columnIndex);
//        cursor.close();

//
//        String a = data.toString();
//        Drawable image = Drawable.createFromPath(data.toString());//new BitmapDrawable(getResources(), picturePath);
//        a = data.getScheme();

//        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data);

//        Uri a = Uri.parse(data.toString());
//        String a = data.toString();
        InputStream inputStream = getContentResolver().openInputStream(data);
        Drawable drawable = Drawable.createFromStream(inputStream, "camera");

//        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
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

//    private void addImageBetweenText(Drawable drawable, Uri data) {
////        editText = (EditText)mRoot.findViewById(R.id.content);
//        drawable .setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
//        ImageSpan imageSpan = new ImageSpan(drawable);
//
//        SpannableStringBuilder builder = new SpannableStringBuilder();
//        builder.append(txtNote.getText());
//
//// this is a string that will let you find a place, where the ImageSpan is.
//        String imgId = "[img="+img_id++ +"]";
//
//        int selStart = txtNote.getSelectionStart();
//
//// current selection is replaceв with imageId
////        String a = drawable.toString();
//        builder.replace(txtNote.getSelectionStart(), txtNote.getSelectionEnd(), data.toString());
////        builder.replace(txtNote.getSelectionStart(), txtNote.getSelectionEnd(), (CharSequence) drawable);
//
//
//// This adds a span to display image where the imageId is. If you do builder.toString() - the string will contain imageId where the imageSpan is.
//// you can yse this later - if you want to location of imageSpan in text;
//        builder.setSpan(imageSpan, selStart, selStart + imgId.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        txtNote.setText(builder);
//        txtNote.setSelection(selStart);
////        drawable .setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
////
////        int selectionCursor = txtNote.getSelectionStart();
////        txtNote.getText().insert(selectionCursor, "build/intermediates/exploded-aar/com.android.support/support-vector-drawable/24.0.0-alpha1/res");
////        selectionCursor = txtNote.getSelectionStart();
////
////        SpannableStringBuilder builder = new SpannableStringBuilder(txtNote.getText());
////        builder.setSpan(new ImageSpan(drawable), selectionCursor - "build/intermediates/exploded-aar/com.android.support/support-vector-drawable/24.0.0-alpha1/res".length(), selectionCursor,                                                   Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
////        txtNote.setText(builder);
////        txtNote.setSelection(selectionCursor);
//    }

    private void save(View view) throws IOException {
        if (id == -1) {
            noteDBHelper.insertNote(txtTitle.getText().toString(), txtNote.getText().toString(), new Date());
        } else {
            noteDBHelper.updateNote(id,txtTitle.getText().toString(), txtNote.getText().toString());
        }
        saved = true;
        InputMethodManager inputManager = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

}
