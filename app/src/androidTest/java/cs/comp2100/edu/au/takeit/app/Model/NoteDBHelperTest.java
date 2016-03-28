package cs.comp2100.edu.au.takeit.app.Model;

import android.database.Cursor;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import java.util.Date;

/**
 * Created by Sina on 26/03/2016.
 */
public class NoteDBHelperTest extends AndroidTestCase {
    NoteDBHelper dbHelper;
    int id;
    Cursor c;
    String title, note;

    public void setUp() throws Exception {
        super.setUp();
        RenamingDelegatingContext context = new RenamingDelegatingContext(getContext(), "test_");
        dbHelper = new NoteDBHelper(context);
    }

    public void tearDown() throws Exception {
        //Gets rid of the traces of our test on the database.
        dbHelper.getWritableDatabase().rawQuery("DELETE FROM " + NoteDB.NoteEntry.TABLE_NAME,null);
    }

    public void testInsertNote() throws Exception {
        //Testing note insertion
        id = dbHelper.insertNote("title", "note", new Date());
        c = dbHelper.getAllNotes();
        assertEquals(c.getCount(), 1);
    }

    public void testGetNote() throws Exception {
        //Testing getNote method
        id = dbHelper.insertNote("title", "note", new Date());
        c = dbHelper.getNote(id);
        title = c.getString(c.getColumnIndex(NoteDB.NoteEntry.COLUMN_NAME_NOTE_TITLE));
        note = c.getString(c.getColumnIndex(NoteDB.NoteEntry.COLUMN_NAME_NOTE));
        assertEquals(title, "title");
        assertEquals(note, "note");
    }

    public void testUpdateNote() throws Exception {
        //Testing updateNote method
        id = dbHelper.insertNote("title", "note", new Date());
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
        id = dbHelper.insertNote("title", "note", new Date());
        c = dbHelper.getAllNotes();
        assertEquals(c.getCount(), 1);
        dbHelper.deleteNote(id);
        c = dbHelper.getAllNotes();
        assertEquals(c.getCount(), 0);
    }
}