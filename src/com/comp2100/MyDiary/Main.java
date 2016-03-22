package com.comp2100.MyDiary;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.comp2100.MyDiary.db.NoteDB;
import com.comp2100.MyDiary.db.NoteDBHelper;

import java.util.Date;

public class Main extends Activity {

    /**
     * Called when the activity is first created.
     */

    public class CustomAdapter extends SimpleCursorAdapter {
        public CustomAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
        }
        //        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            String a = this.getCursor().getString(this.getCursor().getColumnIndex(NoteDB.NoteEntry.COLUMN_NAME_NOTE_ID));
            v.setTag(a);
//            Log.v("tab", a);
//            ByteBuffer buffer = ByteBuffer.wrap(this.getCursor().getBlob(1));
//            short a = buffer.getShort();
//            int a = this.getCursor().getBlob(1);
//            String b = new String(this.getCursor().getBlob(1));
//            Integer c = new Integer (String.valueOf(this.getCursor().getBlob(1)));
//            byte[] d = this.getCursor().getBlob(1);
//            v.sestTag("a");
//            String a = dumpCursorToString(this.getCursor());
            return v;
//            LayoutInflater inflater = getLayoutInflater();
//            View row = inflater.inflate(R.layout.notes, parent, false);
////            convertView.setTag();
//            Button deleteButton = (Button) row.findViewById(R.id.delete_btn);
//            deleteButton.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//                    Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG).show();
//                }
//            });
//            TextView titleText = (TextView) row.findViewById(R.id.textTitle);
//
//            TextView noteText = (TextView) row.findViewById(R.id.textNote);
//            return row;
        }
    }

    NoteDBHelper dbHelper;
//    Button btn_submit = (Button) findViewById(R.id.btnSubmit);
    ListView notes;
    EditText _title;
    EditText _note;
    int update = -1;
    Button cancel;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().hide();
        setContentView(R.layout.main);
        dbHelper = new NoteDBHelper(this);
        _title = (EditText)findViewById(R.id.txt_note_title);
        _note = (EditText)findViewById(R.id.txt_note);
        cancel = (Button) findViewById(R.id.btnCancel);
        cancel.setEnabled(false);
        populateListView();
    }

    @Override
    public void onBackPressed() {

    }

    public void populateListView() {
        final Cursor cursor = dbHelper.getAllNotes();
        String [] columns = new String[] {
                NoteDB.NoteEntry.COLUMN_NAME_NOTE_TITLE,
                NoteDB.NoteEntry.COLUMN_NAME_NOTE,
//                NoteDB.NoteEntry.COLUMN_NAME_TIMESTAMP,
                NoteDB.NoteEntry.COLUMN_NAME_NOTE_ID,
        };
        int [] widgets = new int[] {
                R.id.textTitle,
                R.id.textNote
        };

        CursorAdapter adapter = new CustomAdapter(
                getBaseContext()/*this*/, R.layout.notes, cursor, columns, widgets, 0);
        notes = (ListView)findViewById(R.id.note_list);
        notes.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        notes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor noteCursor = (Cursor) Main.this.notes.getItemAtPosition(position);
                int noteID = noteCursor.getInt(noteCursor.getColumnIndex(NoteDB.NoteEntry.COLUMN_NAME_NOTE_ID));
                retrieveNote(noteID);
            }
            {

        }});
    }

    private void retrieveNote(int noteID) {
        final Cursor cursor = dbHelper.getNote(noteID);
        String note = cursor.getString(cursor.getColumnIndex(NoteDB.NoteEntry.COLUMN_NAME_NOTE));
        String title = cursor.getString(cursor.getColumnIndex(NoteDB.NoteEntry.COLUMN_NAME_NOTE_TITLE));
//        Toast.makeText(getApplicationContext(), title, Toast.LENGTH_LONG).show();
        _title.setText(title);
        _note.setText(note);
        update = noteID;
    }

    public void add(View view) {
        try {
            String titleText = _title.getText().toString();
            String noteText = _note.getText().toString();
            if (update != -1) {
                dbHelper.updateNote(update, titleText, noteText);
                update = -1;
            } else {
                dbHelper.insertNote(titleText, noteText, new Date());
            }
            _title.setText("");
            _note.setText("");
            populateListView();
            InputMethodManager inputManager = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.RESULT_UNCHANGED_SHOWN);

        } catch (Exception e) {

        }
    }

    public void delete(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Are you sure you want to delete x");
        alert.setTitle("Delete");
        alert.setNegativeButton("Nope", null);
        alert.setPositiveButton("Yes!!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dbHelper.deleteNote(Integer.valueOf(((View) view.getParent()).getTag().toString()));
                populateListView();
            }
        });
        alert.setCancelable(true);
        alert.create().show();
    }
}
