package cs.comp2100.edu.au.takeit.app.View;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import cs.comp2100.edu.au.takeit.app.Model.NoteDBHelper;
import cs.comp2100.edu.au.takeit.app.R;
import junit.framework.TestCase;

import java.util.Date;

/**
 * Created by Sina on 26/03/2016.
 */
public class HomeTest extends ActivityInstrumentationTestCase2<Home> {

    Home home;
    Cursor cursor;
    NoteDBHelper db;
    ListView notes;

    public HomeTest(Class activityClass) {
        super(activityClass);
    }

    public void setUp() throws Exception {
        super.setUp();
        home = new Home();
        notes = (ListView) getActivity().findViewById(R.id.note_list);
    }

    public void tearDown() throws Exception {

    }

    public void testPopulateListView() throws Exception {
        ListView notes = (ListView) home.findViewById(R.id.note_list);
        assertNotNull("unloaded list", notes);
//        cursor = db.getReadableDatabase().rawQuery("",null);
        db.insertNote("t","b", new Date());
        home.populateListView(cursor);
        assertEquals(notes.getCount(),1);
        int id = db.insertNote("t","b", new Date());
        home.populateListView(cursor);
        assertEquals(notes.getCount(),2);
        db.deleteNote(id);
        home.populateListView(cursor);
        assertEquals(notes.getCount(),1);
    }
}