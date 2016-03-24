package cs.comp2100.edu.au.takeit.app.DBAdapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import cs.comp2100.edu.au.takeit.app.Model.NoteDB;
import cs.comp2100.edu.au.takeit.app.R;

/**
 * Created by Sina on 24/03/2016.
 */
public class CustomAdapter extends SimpleCursorAdapter {
    public CustomAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView title = (TextView) view.findViewById(R.id.note_title);
        String titleString = cursor.getString(cursor.getColumnIndex(NoteDB.NoteEntry.COLUMN_NAME_NOTE_TITLE));
        if (titleString.length() >= 15)
            titleString = titleString.substring(0,15) + "...";
        title.setText(titleString);

        TextView note = (TextView) view.findViewById((R.id.note_body));
        String noteString = cursor.getString(cursor.getColumnIndex(NoteDB.NoteEntry.COLUMN_NAME_NOTE));
        if (noteString.length() >= 25)
            noteString = noteString.substring(0,25) + "...";
        note.setText(noteString);
    }
}
