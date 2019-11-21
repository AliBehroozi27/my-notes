package com.example.mynotes.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mynotes.R;
import com.example.mynotes.model.Note;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddNoteActivity extends AppCompatActivity {
    private static final String NOTE = "note";
    private static final String ACTION_TYPE = "action_type";
    private static final int EDIT = 1;
    private static final int ADD = 2;
    private static final String MY_NOTE = "my_note";
    private static final String ADD_NOTE = "add_note";

    @BindView(R.id.note)
    EditText etNote;
    @BindView(R.id.date)
    TextView date;
    @BindView(R.id.bookMark)
    ImageView bookMark;
    @BindView(R.id.todo)
    ImageView todo;
    @BindView(R.id.addNote_appbar)
    AppBarLayout addNoteBar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private int actionType;
    private Note myNote;
    private boolean todoCheck = false;
    private boolean bookMarkCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        ButterKnife.bind(this);
        actionType = Integer.parseInt(getIntent().getExtras().get(ACTION_TYPE).toString());
        initToolbar();
        initViews(actionType);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.m_save:
                Intent intent = new Intent(this, MainActivity.class);
                myNote.setText(etNote.getText().toString());
                if (actionType == EDIT)
                    myNote.setDate(date.getText().toString());
                else
                    myNote.setDate(date.getText().toString());
                myNote.setDoing(todoCheck);
                myNote.setMarked(bookMarkCheck);
                intent.putExtra(ADD_NOTE, myNote);
                setResult(Activity.RESULT_OK, intent);
                finish();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return true;
    }

    @OnClick(R.id.ivBackArrow)
    public void onBackArrow() {
        finish();
    }

    @OnClick(R.id.todo)
    public void onTodoClick() {
        if (todoCheck) {
            todoCheck = false;
            todo.setImageResource(R.drawable.ic_not_todo);
        } else {
            todoCheck = true;
            todo.setImageResource(R.drawable.ic_todo);
        }

    }

    @OnClick(R.id.bookMark)
    public void onBookMarkClick() {
        if (bookMarkCheck) {
            bookMarkCheck = false;
            bookMark.setImageResource(R.drawable.ic_not_bookmark);
        } else {
            bookMarkCheck = true;
            bookMark.setImageResource(R.drawable.ic_bookmark);
        }
    }

    public String getCurrentDate() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy hh:mm");
        return df.format(c);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
    }

    private void initViews(int actionType) {
        myNote = new Note(null, null, false, false, false);
        switch (actionType) {
            case EDIT:
                myNote = (Note) getIntent().getExtras().get(MY_NOTE);
                etNote.setText(myNote.getText());
                date.setText(myNote.getDate());
                if (myNote.isMarked()) {
                    bookMark.setImageResource(R.drawable.ic_bookmark);
                    bookMarkCheck = true;
                }
                if (myNote.isDoing()) {
                    todo.setImageResource(R.drawable.ic_todo);
                    todoCheck = true;
                }
                break;
            case ADD:
                date.setText(getCurrentDate());


        }
    }


}
