package cs.comp2100.edu.au.takeit.app.Model;

import android.database.Cursor;
import android.test.ActivityInstrumentationTestCase2;
import cs.comp2100.edu.au.takeit.app.View.Home;
import junit.framework.TestCase;

import java.util.Date;

/**
 * Created by Sina on 26/03/2016.
 */
public class NoteDBHelperTest extends ActivityInstrumentationTestCase2<Home> {
    NoteDBHelper dbHelper;
    int id;
    Cursor c;
    String title, note;

    public NoteDBHelperTest(Class<NoteDBHelper> activityClass) {
        super(activityClass);
    }
    public NoteDBHelperTest() {
        super(NoteDBHelper.class);
    }

    public void setUp() throws Exception {
        super.setUp();
        dbHelper = new NoteDBHelper(this.getActivity().getApplicationContext());
    }

    public void tearDown() throws Exception {

    }

    public void testInsertNote() throws Exception {
        //Testing note insertion
        id = dbHelper.insertNote("title", "note", new Date());
        c = dbHelper.getAllNotes();
        assertEquals(c.getCount(), 1);
    }

    public void testGetNote() throws Exception {
        //Testing getNote method
        c = dbHelper.getNote(id);
        title = c.getString(c.getColumnIndex(NoteDB.NoteEntry.COLUMN_NAME_NOTE_TITLE));
        note = c.getString(c.getColumnIndex(NoteDB.NoteEntry.COLUMN_NAME_NOTE));
        assertEquals(title, "title");
        assertEquals(note, "note");
    }

    public void testUpdateNote() throws Exception {
        //Testing updateNote method
        dbHelper.updateNote(id, "updatedTitle", "updatedNote");
        c = dbHelper.getNote(id);
        title = c.getString(c.getColumnIndex(NoteDB.NoteEntry.COLUMN_NAME_NOTE_TITLE));
        note = c.getString(c.getColumnIndex(NoteDB.NoteEntry.COLUMN_NAME_NOTE));
        assertEquals(title, "updatedTitle");
        assertEquals(note, "updatedNote");
    }

    public void testGetAllNotes() throws Exception {
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

    public void testSearch() throws Exception {
        id = dbHelper.insertNote("notetosearch","n",new Date());
        int id2 = dbHelper.insertNote("t","notetosearch",new Date());
        c = dbHelper.search("notetosearch");
        assertEquals(c.getCount(), 2);
    }

    public void testDeleteNote() throws Exception {
        //Testing deleteNote method
        dbHelper.deleteNote(id);
        c = dbHelper.getAllNotes();
        assertEquals(c.getCount(), 0);
    }
}