package cs.comp2100.edu.au.takeit.app.View;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.ActivityInstrumentationTestCase2;
import android.test.RenamingDelegatingContext;
import android.test.UiThreadTest;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import cs.comp2100.edu.au.takeit.app.Model.NoteDB;
import cs.comp2100.edu.au.takeit.app.Model.NoteDBHelper;
import cs.comp2100.edu.au.takeit.app.R;
import junit.framework.TestCase;

import java.util.Date;

/**
 * Created by Sina on 26/03/2016.
 */
public class HomeTest extends ActivityInstrumentationTestCase2<Home> {

    Activity nActivity;
//    Home home;
    Cursor cursor;
    NoteDBHelper db;
    ListView notes;

//    public HomeTest(Class activityClass) {
//        super(activityClass);
//    }
    public HomeTest() {
        super(Home.class);
    }

    public void setUp() throws Exception {
        super.setUp();
        nActivity = this.getActivity();
        notes = (ListView) getActivity().findViewById(R.id.note_list);
        db = new NoteDBHelper(this.getActivity().getApplicationContext());
    }

    public void tearDown() throws Exception {

    }

    @UiThreadTest
    public void testPopulateListView() throws Exception {
//        ListView notes = (ListView) nActivity.findViewById(R.id.note_list);
        assertNotNull("unloaded list", notes);
        cursor = db.getAllNotes();
        do {
            db.deleteNote(cursor.getInt(cursor.getColumnIndex(NoteDB.NoteEntry.COLUMN_NAME_NOTE_ID)));
        } while (cursor.moveToNext());
        db.insertNote("t","b", new Date());
        getActivity().populateListView(cursor);
        assertEquals(notes.getCount(),1);
        int id = db.insertNote("t","b", new Date());
        getActivity().populateListView(cursor);
        assertEquals(notes.getCount(),2);
        db.deleteNote(id);
        getActivity().populateListView(cursor);
        assertEquals(notes.getCount(),1);
    }
}