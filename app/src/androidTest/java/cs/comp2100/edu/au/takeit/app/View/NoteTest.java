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

    public void testOnBackPressed() throws Exception {
        
    }
}