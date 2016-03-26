package cs.comp2100.edu.au.takeit.app.View;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import cs.comp2100.edu.au.takeit.app.DBAdapter.CustomAdapter;
import cs.comp2100.edu.au.takeit.app.Model.NoteDB;
import cs.comp2100.edu.au.takeit.app.Model.NoteDBHelper;
import cs.comp2100.edu.au.takeit.app.R;

/*This class handles our home activity (main and first page of our app), and everything that is being done under that activity.*/
public class Home extends AppCompatActivity {
    ListView noteList;
    boolean searchMode;
    NoteDBHelper dbHelper;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        dbHelper = new NoteDBHelper(this);
        noteList = (ListView) findViewById(R.id.note_list);
        registerForContextMenu(noteList);
        searchMode = false;
        populateListView(dbHelper.getAllNotes());
        context = this;
    }

    @Override
    public void onResume() {
        super.onResume();
        populateListView(dbHelper.getAllNotes());
    }

    /*Inflates the actionbar into our layout.*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    /*Assigns the appropriate actions to context menu options.*/
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.note_list) {
            ListView lv = (ListView) v;
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
            Object obj =  lv.getItemAtPosition(acmi.position);
            final int _id = ((Cursor)obj).getInt(((Cursor)obj).getColumnIndex(NoteDB.NoteEntry.COLUMN_NAME_NOTE_ID));
            menu.add(0, 0, 2, "Share").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Cursor note = dbHelper.getNote(_id);
                    String title = note.getString(note.getColumnIndex(NoteDB.NoteEntry.COLUMN_NAME_NOTE_TITLE));
                    String body = note.getString(note.getColumnIndex(NoteDB.NoteEntry.COLUMN_NAME_NOTE));
                    startActivity(Intent.createChooser(Note.createShareIntent(title, body),"Share your note"));

                    return false;
                }
            });
            menu.add(0,0,0,"Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setMessage("Are you sure you want to delete the current note?");
                    alert.setTitle("Delete");
                    alert.setNegativeButton("Nope", null);
                    alert.setPositiveButton("Yes!!", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
//                            dbHelper.deleteNote(Integer.valueOf(((View) view.getParent()).getTag().toString()));
                            dbHelper.deleteNote(_id);
                            populateListView(dbHelper.getAllNotes());
                        }
                    });
                    alert.setCancelable(true);
                    alert.create().show();
                    return false;
                }
            });
            menu.add(0,0,1, "Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Intent goToNote = new Intent(getApplicationContext(), Note.class);
                    goToNote.putExtra("id", _id);
                    startActivity(goToNote);
                    return false;
                }
            });
        }
    }

    /*Manages the actions done through the menu in the action bar.*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add_note) {
            startActivity(new Intent(getApplicationContext(), Note.class));
            return true;
        } else if (id == R.id.action_search) {
            searchMode = true;
            final LayoutInflater inflator = (LayoutInflater) this .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflator.inflate(R.layout.search_layout, null);

            final EditText txtSearch = (EditText) v.findViewById(R.id.txt_search);
            txtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    Cursor res = dbHelper.search(txtSearch.getText().toString());
                    if (res.getCount() == 0) Toast.makeText(getApplicationContext(), "Nothing found!",
                            Toast.LENGTH_SHORT).show();
                    populateListView(dbHelper.search(txtSearch.getText().toString()));
                    return false;
                }
            });

            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setCustomView(v);
        }

        return super.onOptionsItemSelected(item);
    }

    /*Actions for the back button dialog.*/
    @Override
    public void onBackPressed() {
        if (searchMode) {
            getSupportActionBar().setDisplayShowCustomEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            searchMode = false;
        } else {
            finish();
        }
    }

    /*Populates the list of notes with the given query.
    * The query can be either querying all the notes or notes containing the given keyboard.*/
    public void populateListView(Cursor query) {
        String [] columns = new String[] {
                NoteDB.NoteEntry.COLUMN_NAME_NOTE_TITLE,
                NoteDB.NoteEntry.COLUMN_NAME_NOTE
        };
        int [] widgets = new int[] {
                R.id.note_title,
                R.id.note_body
        };

        CursorAdapter adapter = new CustomAdapter(
                getBaseContext(), R.layout.note_in_list, query, columns, widgets, 0);

        noteList = (ListView) findViewById(R.id.note_list);
        noteList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        noteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor noteCursor = (Cursor) Home.this.noteList.getItemAtPosition(position);
                int noteID = noteCursor.getInt(noteCursor.getColumnIndex(NoteDB.NoteEntry.COLUMN_NAME_NOTE_ID));
                Intent goToNote = new Intent(getApplicationContext(), Note.class);
                goToNote.putExtra("id", noteID);
                startActivity(goToNote);
            }
        });
    }
}
