package cs.comp2100.edu.au.takeit.app.View;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import cs.comp2100.edu.au.takeit.app.Model.NoteDBHelper;
import cs.comp2100.edu.au.takeit.app.R;
import junit.framework.TestCase;

/**
 * Created by Sina on 26/03/2016.
 */
public class HomeTest extends TestCase {

    Home home;
    Cursor cursor;
    NoteDBHelper db;
    public void setUp() throws Exception {
        super.setUp();
        home = new Home();
        db = new NoteDBHelper(this);
    }

    public void tearDown() throws Exception {

    }

    public void testPopulateListView() throws Exception {
        ListView notes = (ListView) home.findViewById(R.id.note_list);
        assertNotNull("unloaded list", notes);
        cursor = db.getReadableDatabase().rawQuery("",null);
        home.populateListView(cursor);

//        home.populateListView();

    }
}