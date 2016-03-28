package cs.comp2100.edu.au.takeit.app.View;

import android.app.Activity;
import android.database.Cursor;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.widget.EditText;
import android.widget.ListView;
import cs.comp2100.edu.au.takeit.app.Model.NoteDB;
import cs.comp2100.edu.au.takeit.app.Model.NoteDBHelper;
import cs.comp2100.edu.au.takeit.app.R;

import java.util.Date;
import java.util.List;

/**
 * Created by Sina on 26/03/2016.
 */
public class NoteTest extends ActivityInstrumentationTestCase2<Note> {

    Activity mActivity;
    ListView notes;
    NoteDBHelper dbHelper;
    EditText title;
    EditText body;

//    public NoteTest(Class<Home> activityClass) {
//        super(Home.class);
//    }
    public NoteTest() {
        super(Note.class);
    }

    public void setUp() throws Exception {
        super.setUp();
        mActivity = this.getActivity();
        notes = (ListView) mActivity.findViewById(R.id.note_list);
        dbHelper = new NoteDBHelper(this.getActivity().getApplicationContext());
        title = (EditText) mActivity.findViewById(R.id.title_text);
        body = (EditText) mActivity.findViewById(R.id.note_text);
    }

    public void testEmptyList() {
        //Test whether the list is empty or not
        assertNull(notes);
    }

    @UiThreadTest
    public void testOnBackPressed() throws Exception {
        //Check if something happens without user notification.
        Cursor c = dbHelper.getAllNotes();
        title.append("a");
        mActivity.onBackPressed();
        Cursor c2 = dbHelper.getAllNotes();
        assertEquals(c.getCount(), c2.getCount());
        while (c.moveToNext()) {
            c2.moveToNext();
            assertEquals(c.getString(c.getColumnIndex(NoteDB.NoteEntry.COLUMN_NAME_NOTE_TITLE)),
                    c2.getString(c2.getColumnIndex(NoteDB.NoteEntry.COLUMN_NAME_NOTE_TITLE)));
            assertEquals(c.getString(c.getColumnIndex(NoteDB.NoteEntry.COLUMN_NAME_NOTE)),
                    c2.getString(c2.getColumnIndex(NoteDB.NoteEntry.COLUMN_NAME_NOTE)));
        }
    }
}