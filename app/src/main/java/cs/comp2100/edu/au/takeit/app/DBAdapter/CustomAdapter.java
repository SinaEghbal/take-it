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

    /*Puts different fields (namely title and note) of each record into the appropriate TextView. Cuts the long
    * Strings and changes the uris with a single <image> tag for the list_item layout.*/
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView title = (TextView) view.findViewById(R.id.note_title);
        String titleString = cursor.getString(cursor.getColumnIndex(NoteDB.NoteEntry.COLUMN_NAME_NOTE_TITLE));
        if (titleString.length() >= 15)
            titleString = titleString.substring(0,15) + "...";
        title.setText(titleString);

        TextView note = (TextView) view.findViewById((R.id.note_body));
        StringBuilder noteString = new StringBuilder();
        noteString.append(cursor.getString(cursor.getColumnIndex(NoteDB.NoteEntry.COLUMN_NAME_NOTE)));
        while (noteString.toString().contains("[image]")) {
            noteString.replace(noteString.indexOf("[image]"),noteString.indexOf("[\\image]") + 8, "<image>");
        }
        if (noteString.length() >= 25)
            noteString.replace(25, noteString.length(), "...");
        note.setText(noteString.toString().replace("\n", " "));
    }
}
