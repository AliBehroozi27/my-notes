package com.example.mynotes.view.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.Observable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mynotes.R;
import com.example.mynotes.adapter.OnItemClickListener;
import com.example.mynotes.adapter.RecyclerAdapter;
import com.example.mynotes.helper.OnStartDragListener;
import com.example.mynotes.helper.OnSwipeListener;
import com.example.mynotes.helper.SimpleItemTouchHelperCallback;
import com.example.mynotes.model.Note;
import com.example.mynotes.repository.NoteRepository;
import com.example.mynotes.view.activity.AddNoteActivity;
import com.example.mynotes.viewmodel.NoteViewModel;
import com.example.mynotes.viewmodel.ViewModelFactory;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class NoteFragment extends Fragment implements OnItemClickListener, OnStartDragListener, OnSwipeListener {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_noItem)
    TextView tvNoItem;
    @BindView(R.id.tv_noResult)
    TextView tvNoResult;
    @BindString(R.string.delete_failed_msg)
    String deleteFailedMsg;
    @BindString(R.string.delete_success_msg)
    String deleteSuccessMsg;
    @BindString(R.string.do_failed_msg)
    String doFailedMsg;
    @BindString(R.string.job_done_msg)
    String jobDoneMsg;
    @BindString(R.string.job_undone_msg)
    String jobUndoneMsg;
    @BindString(R.string.undo_msg)
    String undoMsg;

    private static final int EDIT_REQUEST_CODE = 20;
    private static final int EDIT = 1;
    private static final String MY_NOTE = "my_note";
    private static final String ACTION_TYPE = "action_type";
    private ArrayList<Note> notes;
    private RecyclerAdapter adapter;
    private ItemTouchHelper mItemTouchHelper;
    private NoteViewModel viewModel;
    private boolean getDelete;
    private Snackbar snackbar;
    private static NoteFragment noteFragment;
    private View viewLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note, container, false);
        ButterKnife.bind(this, view);
        viewLayout = getActivity().findViewById(R.id.coorLay);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListData();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        NoteRepository repository = new NoteRepository(getActivity());
        ViewModelFactory factory = ViewModelFactory.getInstance(getActivity(),repository);
        viewModel = ViewModelProviders.of(this, factory).get(NoteViewModel.class);
        viewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(@Nullable List<Note> newNotes) {
                notes = (ArrayList<Note>) newNotes;
                viewModel.setDefaultNotes(notes);
                adapter.setNotes(notes);
                adapter.notifyDataSetChanged();

            }
        });
        viewModel.getNotesCount().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer size) {
                if (size == 0) {
                    tvNoItem.setVisibility(View.VISIBLE);
                } else {
                    tvNoItem.setVisibility(View.GONE);
                    tvNoResult.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getActivity(), AddNoteActivity.class);
        intent.putExtra(MY_NOTE, notes.get(position));
        intent.putExtra(ACTION_TYPE, EDIT);
        getActivity().startActivityForResult(intent, EDIT_REQUEST_CODE);
    }

    @Override
    public void onItemLongClick(int position) {
        Note clickedNote = notes.get(position);
        if (clickedNote.isDoing()) {
            if (!clickedNote.isDone()) {
                clickedNote.setDone(true);
                viewModel.DoJob(notes.get(position));
                viewModel.getDoneJob().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                    @Override
                    public void onPropertyChanged(Observable sender, int propertyId) {
                        if (viewModel.getDoneJob().get() == -1) {
                            makeSnackBar(false, doFailedMsg, null);
                        } else {
                            makeSnackBar(false, jobDoneMsg, null);
                        }
                    }
                });
            } else {
                clickedNote.setDone(false);
                viewModel.DoJob(notes.get(position));
                viewModel.getDoneJob().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                    @Override
                    public void onPropertyChanged(Observable sender, int propertyId) {
                        if (viewModel.getDoneJob().get() == -1) {
                            makeSnackBar(false, doFailedMsg, null);
                        } else {
                            makeSnackBar(false, jobUndoneMsg, null);
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onItemBookMarkClick(int position) {
        Note clickedNote = notes.get(position);
        if (clickedNote.isMarked()) clickedNote.setMarked(false);
        else clickedNote.setMarked(true);
        viewModel.updateNote(clickedNote);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onSwipe(final int position) {
        Note deletingNote = notes.get(position);
        if (snackbar != null) snackbar.dismiss();
        makeSnackBar(true, deleteSuccessMsg, deletingNote);
        viewModel.getDeleteNote().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (viewModel.getDeleteNote().get() == -1) makeSnackBar(false, deleteFailedMsg, null);
            }
        });
    }

    public void setQueryResult(ArrayList<Note> notes) {
        if (adapter == null) setListData();
        this.notes = notes;
        adapter.setNotes(notes);
        adapter.notifyDataSetChanged();
    }

    public void setDefaultNotes() {
        this.notes = viewModel.getDefaultNotes();
        adapter.setNotes(notes);
        adapter.notifyDataSetChanged();
        tvNoResult.setVisibility(View.GONE);
    }

    public void setListData() {
        if (notes == null) notes = new ArrayList<Note>();
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapter(notes, this.getActivity(), this, this);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

    }

    public void showNoResultMsg(Boolean visible) {
        if (visible) tvNoResult.setVisibility(View.VISIBLE);
        else tvNoResult.setVisibility(View.GONE);
    }

    private void makeSnackBar(boolean deleteAction, String msg, final Note deletingNote) {
        snackbar = Snackbar.make(viewLayout, msg, Snackbar.LENGTH_SHORT);
        if (deleteAction) {
            getDelete = true;
            snackbar.setAction(undoMsg, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapter.notifyDataSetChanged();
                    getDelete = false;
                }
            });
            snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);
                    if (getDelete) {
                        viewModel.deleteNote(deletingNote);
                    }
                }

            });
        }
        snackbar.show();
    }
}