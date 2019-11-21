package com.example.mynotes.view.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.example.mynotes.R;
import com.example.mynotes.adapter.TabAdapter;
import com.example.mynotes.model.Note;
import com.example.mynotes.repository.NoteRepository;
import com.example.mynotes.view.fragment.MarkFragment;
import com.example.mynotes.view.fragment.NoteFragment;
import com.example.mynotes.view.fragment.ToDoFragment;
import com.example.mynotes.viewmodel.NoteViewModel;
import com.example.mynotes.viewmodel.ViewModelFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.main_appbar)
    AppBarLayout mainBar;
    @BindView(R.id.search_appbar)
    AppBarLayout searchBar;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.ivBackArrow)
    ImageView backArrow;
    @BindView(R.id.searchView)
    public SearchView searchView;
    @BindString(R.string.todo_frag_title)
    String todoFragTitle;
    @BindString(R.string.mark_frag_title)
    String markFragTitle;
    @BindString(R.string.notes_frag_title)
    String notesFragTitle;

    private static final String FARSI = "fa";
    private static final String ENGLISH = "en";
    private static final String ACTION_TYPE = "action_type";
    private static final int ADD = 2;
    private static final String ADD_NOTE = "add_note";
    private static final int STANDARD_APPBAR = 0;
    private static final int SEARCH_APPBAR = 1;
    private static final int ADD_REQUEST_CODE = 19;
    private static final int EDIT_REQUEST_CODE = 20;
    private static final int MARK_FRAGMENT = 2;
    private static final int NOTE_FRAGMENT = 1;
    private static final int TODO_FRAGMENT = 0;
    private int mAppBarState;
    private TabAdapter adapter;
    private NoteViewModel noteViewModel;
    private NoteRepository repository;
    private ToDoFragment todoFragment;
    private MarkFragment markFragment;
    private NoteFragment noteFragment;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //vm
        initViewModel();

        //setContentWithSetting
        setContent();

        //toolbar
        initToolbar();

        //initTabs
        initTabs();

        //searchListener
        initTextListener();

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setContent() {
        String lang = noteViewModel.getLanguage();
        if (lang.equals(FARSI)) setLocale(FARSI);
        if (lang.equals(ENGLISH)) setLocale(ENGLISH);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Note myNote;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ADD_REQUEST_CODE:
                    myNote = (Note) data.getExtras().get(ADD_NOTE);
                    if (!checkEmptyNote(myNote.getText())) noteViewModel.insertNote(myNote);
                    break;
                case EDIT_REQUEST_CODE:
                    myNote = (Note) data.getExtras().get(ADD_NOTE);
                    if (!checkEmptyNote(myNote.getText())) noteViewModel.updateNote(myNote);
                default:
                    break;
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        setAppBaeState(STANDARD_APPBAR);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.m_search:
                toggleToolBarState();
                break;
            case R.id.m_deleteAll:
                noteViewModel.deleteAll();
                break;
            case R.id.m_lang_farsi:
                setLocale(FARSI);
                noteViewModel.setLanguage(FARSI);
                item.setChecked(true);
                refresh();
                break;
            case R.id.m_lang_english:
                setLocale(ENGLISH);
                noteViewModel.setLanguage(ENGLISH);
                item.setChecked(true);
                refresh();
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        String lang = noteViewModel.getLanguage();
        if (lang.equals(FARSI)) menu.findItem(R.id.m_lang_farsi).setChecked(true);
        if (lang.equals(ENGLISH)) menu.findItem(R.id.m_lang_english).setChecked(true);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mAppBarState == SEARCH_APPBAR) {
            toggleToolBarState();
        } else
            super.onBackPressed();
    }

    @OnClick(R.id.ivBackArrow)
    public void onBackArrowClick() {
        toggleToolBarState();
    }

    @OnClick(R.id.fab)
    public void onFabClicked() {
        Intent intent = new Intent(this, AddNoteActivity.class);
        intent.putExtra(ACTION_TYPE, ADD);
        startActivityForResult(intent, ADD_REQUEST_CODE);
    }

    @Override
    protected void onDestroy() {
        //noteViewModel.dispose();
        super.onDestroy();
    }

    private void initTextListener() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                switch (tabLayout.getSelectedTabPosition()) {
                    case MARK_FRAGMENT:
                        noteViewModel.queryMarks(s).observe(MainActivity.this, new Observer<List<Note>>() {
                            @Override
                            public void onChanged(@Nullable List<Note> notes) {
                                markFragment.setQueryResult((ArrayList<Note>) notes);
                            }
                        });
                        break;
                    case NOTE_FRAGMENT:
                        noteViewModel.queryNotes(s).observe(MainActivity.this, new Observer<List<Note>>() {
                            @Override
                            public void onChanged(@Nullable List<Note> notes) {
                                noteFragment.setQueryResult((ArrayList<Note>) notes);
                            }
                        });
                        break;
                    case TODO_FRAGMENT:
                        noteViewModel.queryDoings(s).observe(MainActivity.this, new Observer<List<Note>>() {
                            @Override
                            public void onChanged(@Nullable List<Note> notes) {
                                todoFragment.setQueryResult((ArrayList<Note>) notes);
                            }
                        });
                        break;
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                switch (tabLayout.getSelectedTabPosition()) {
                    case MARK_FRAGMENT:
                        noteViewModel.queryMarksDynamic(s).observe(MainActivity.this, new Observer<List<Note>>() {
                            @Override
                            public void onChanged(@Nullable List<Note> notes) {
                                markFragment.setQueryResult((ArrayList<Note>) notes);
                                if (notes.size() == 0) markFragment.showNoResultMsg(true);
                                else markFragment.showNoResultMsg(false);
                            }
                        });
                        break;
                    case NOTE_FRAGMENT:
                        noteViewModel.queryNotesDynamic(s).observe(MainActivity.this, new Observer<List<Note>>() {
                            @Override
                            public void onChanged(@Nullable List<Note> notes) {
                                noteFragment.setQueryResult((ArrayList<Note>) notes);
                                if (notes.size() == 0) noteFragment.showNoResultMsg(true);
                                else noteFragment.showNoResultMsg(false);
                            }
                        });
                        break;
                    case TODO_FRAGMENT:
                        noteViewModel.queryDoingsDynamic(s).observe(MainActivity.this, new Observer<List<Note>>() {
                            @Override
                            public void onChanged(@Nullable List<Note> notes) {
                                todoFragment.setQueryResult((ArrayList<Note>) notes);
                                if (notes.size() == 0) todoFragment.showNoResultMsg(true);
                                else todoFragment.showNoResultMsg(false);
                            }
                        });
                        break;
                }
                return true;
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void setLocale(String lang) {
        Resources res = getApplicationContext().getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(lang));
        res.updateConfiguration(conf, dm);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    public void refresh() {
        finish();
        Intent refresh = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(refresh);
    }

    private void initViewModel() {
        repository = new NoteRepository(this);
        ViewModelFactory factory = ViewModelFactory.getInstance(this, repository);
        noteViewModel = ViewModelProviders.of(this, factory).get(NoteViewModel.class);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        setAppBaeState(STANDARD_APPBAR);
    }


    private void initTabs() {
        adapter = new TabAdapter(this.getSupportFragmentManager());
        todoFragment = new ToDoFragment();
        markFragment = new MarkFragment();
        noteFragment = new NoteFragment();
        adapter.addFragment(todoFragment, todoFragTitle);
        adapter.addFragment(noteFragment, notesFragTitle);
        adapter.addFragment(markFragment, markFragTitle);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(1).select();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (mAppBarState == SEARCH_APPBAR)
                    toggleToolBarState();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                setDefaultNotes(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mAppBarState == SEARCH_APPBAR)
                    toggleToolBarState();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private boolean checkEmptyNote(String text) {
        return text.trim().isEmpty();
    }

    private void toggleToolBarState() {
        if (mAppBarState == STANDARD_APPBAR) {
            setAppBaeState(SEARCH_APPBAR);
        } else {
            setAppBaeState(STANDARD_APPBAR);
            setDefaultNotes(-1);
            searchView.setQuery("", true);
        }
    }


    private void setDefaultNotes(int position) {
        if (position == -1) {
            switch (tabLayout.getSelectedTabPosition()) {
                case NOTE_FRAGMENT:
                    noteFragment.setDefaultNotes();
                case TODO_FRAGMENT:
                    todoFragment.setDefaultNotes();
                case MARK_FRAGMENT:
                    markFragment.setDefaultNotes();
            }
        } else {
            switch (position) {
                case NOTE_FRAGMENT:
                    noteFragment.setDefaultNotes();
                case TODO_FRAGMENT:
                    todoFragment.setDefaultNotes();
                case MARK_FRAGMENT:
                    markFragment.setDefaultNotes();
            }
        }
    }

    private void setAppBaeState(int state) {
        mAppBarState = state;
        if (mAppBarState == STANDARD_APPBAR) {
            searchBar.setVisibility(View.GONE);
            mainBar.setVisibility(View.VISIBLE);
            setSupportActionBar(toolbar);

            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            try {
                im.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken()
                        , 0); // make keyboard hide
            } catch (NullPointerException e) {
            }
        } else if (mAppBarState == SEARCH_APPBAR) {
            mainBar.setVisibility(View.GONE);
            searchBar.setVisibility(View.VISIBLE);
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0); // make keyboard popup
        }
    }


}

