package cs.comp2100.edu.au.takeit.app.View;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;
import cs.comp2100.edu.au.takeit.app.R;

public class Home extends AppCompatActivity {
    ListView note_list;
    boolean search_mode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        note_list = (ListView) findViewById(R.id.note_list);
        registerForContextMenu(note_list);
        search_mode = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add_note) {
            startActivity(new Intent(getApplicationContext(), Note.class));
            return true;
        } else if (id == R.id.action_search) {
            search_mode = true;
            final LayoutInflater inflator = (LayoutInflater) this .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflator.inflate(R.layout.search_layout, null);

            EditText txt_search = (EditText) v.findViewById(R.id.txt_search);
            txt_search.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length()>0 && s.subSequence(s.length()-1, s.length()).toString().equalsIgnoreCase("\n")) {
                        //enter pressed
                    }
                }
            });

            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setCustomView(v);

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (search_mode) {
            getSupportActionBar().setDisplayShowCustomEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            search_mode = false;
        } else {
            finish();
        }
    }
}
