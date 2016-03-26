package cs.comp2100.edu.au.takeit.app.View;

import android.app.Activity;
import android.database.Cursor;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import cs.comp2100.edu.au.takeit.app.Model.NoteDB;
import cs.comp2100.edu.au.takeit.app.Model.NoteDBHelper;
import cs.comp2100.edu.au.takeit.app.R;
import junit.framework.TestCase;

import java.util.Date;

/**
 * Created by Sina on 26/03/2016.
 */
public class NoteTest extends ActivityInstrumentationTestCase2<Home> {

    Activity mActivity;
    ListView notes;
    NoteDBHelper dbHelper;

    public NoteTest(Class<Home> activityClass) {
        super(activityClass);
    }

    public void setUp() throws Exception {
        super.setUp();
        mActivity = this.getActivity();
        notes = (ListView) mActivity.findViewById(R.id.note_list);
        dbHelper = new NoteDBHelper(this.getActivity().context);
    }

    public void testEmptyList() {
        assertNotNull(notes);
    }

    public void testList() {
        //Testing note insertion
        int id = dbHelper.insertNote("title", "note", new Date());
        Cursor c = dbHelper.getAllNotes();
        assertEquals(c.getCount(), 1);

        //Testing getNote method
        c = dbHelper.getNote(id);
        String title = c.getString(c.getColumnIndex(NoteDB.NoteEntry.COLUMN_NAME_NOTE_TITLE));
        String note = c.getString(c.getColumnIndex(NoteDB.NoteEntry.COLUMN_NAME_NOTE));
        assertEquals(title, "title");
        assertEquals(note, "note");

        //Testing updateNote method
        dbHelper.updateNote(id, "updatedTitle", "updatedNote");
        c = dbHelper.getNote(id);
        title = c.getString(c.getColumnIndex(NoteDB.NoteEntry.COLUMN_NAME_NOTE_TITLE));
        note = c.getString(c.getColumnIndex(NoteDB.NoteEntry.COLUMN_NAME_NOTE));
        assertEquals(title, "updatedTitle");
        assertEquals(note, "updatedNote");

        //Testing deleteNote method
        dbHelper.deleteNote(id);
        c = dbHelper.getAllNotes();
        assertEquals(c.getCount(), 0);

        //Testing getAllNotes method
        dbHelper.insertNote("title", "note", new Date());
        assertEquals(dbHelper.getAllNotes().getCount(), 1);
        dbHelper.insertNote("title", "note", new Date());
        assertEquals(dbHelper.getAllNotes().getCount(), 2);
        id = dbHelper.insertNote("title", "note", new Date());
        assertEquals(dbHelper.getAllNotes().getCount(), 3);
        dbHelper.deleteNote(id);
        assertEquals(dbHelper.getAllNotes().getCount(), 2);

    }

    public void testOnBackPressed() throws Exception {

    }
}