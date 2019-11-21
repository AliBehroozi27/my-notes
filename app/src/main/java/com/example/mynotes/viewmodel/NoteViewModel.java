package com.example.mynotes.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.ObservableInt;

import com.example.mynotes.model.Note;
import com.example.mynotes.repository.NoteRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.MaybeSource;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.ReplaySubject;

public class NoteViewModel extends ViewModel {
    private static final String MY_PREFERENCES = "shared_preference";
    private static final String LANGUAGE = "language";
    private static final String ENGLISH = "en";
    private static NoteViewModel viewModel;
    private final SharedPreferences sharedPreferences;
    private ArrayList<Note> defaultNotes;
    private ArrayList<Note> defaultMarkNotes;
    private ArrayList<Note> defaultTodoNotes;
    private NoteRepository noteRepository;
    private CompositeDisposable compositeDisposable;
    private PublishSubject<String> querySubject;
    private ObservableInt doneJob;
    private ObservableInt deleteNote;
    private ReplaySubject<String> queryNotesDynamicSubject;
    private ReplaySubject<String> queryMarksDynamicSubject;
    private ReplaySubject<String> queryDoingsDynamicSubject;
    private MutableLiveData<String> langObservable;
    private MutableLiveData<List<Note>> queriedNotes;

    public static NoteViewModel getInstance(Context context, NoteRepository noteRepository) {
        if (viewModel == null) viewModel = new NoteViewModel(context, noteRepository);
        return viewModel;
    }


    private NoteViewModel(Context context, NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
        this.sharedPreferences = context.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        init();
    }

    private void init() {
        compositeDisposable = new CompositeDisposable();
        doneJob = new ObservableInt();
        deleteNote = new ObservableInt();
        defaultNotes = new ArrayList<Note>();
        defaultMarkNotes = new ArrayList<Note>();
        defaultTodoNotes = new ArrayList<Note>();

    }

    public void setLanguage(String lang) {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putString(LANGUAGE, lang);
        editor.apply();
    }

    public String getLanguage() {
        return sharedPreferences.getString(LANGUAGE, ENGLISH);
    }

    public LiveData<List<Note>> getAllNotes() {
        return noteRepository.getAllNotes();
    }

    public LiveData<List<Note>> getAllFavorites() {
        return noteRepository.getAllFavorites();
    }

    public LiveData<List<Note>> getAllDoings() {
        return noteRepository.getAllDoings();
    }

    public void insertNote(final Note note) {
        Single<Note> singleInsert = Single.just(note);
        singleInsert.observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<Note, SingleSource<Note>>() {
                    @Override
                    public SingleSource<Note> apply(Note note) throws Exception {
                        noteRepository.InsertNote(note);
                        return Single.just(note);
                    }
                })
                .subscribe(new SingleObserver<Note>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }


                    @Override
                    public void onSuccess(Note note) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    public void deleteNote(Note note) {
        Single<Note> singleDelete = Single.just(note);
        singleDelete.observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<Note, SingleSource<Note>>() {
                    @Override
                    public SingleSource<Note> apply(Note note) throws Exception {
                        noteRepository.DeleteNote(note);
                        return Single.just(note);
                    }
                })
                .subscribe(new SingleObserver<Note>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Note note) {
                        deleteNote.set(getRandomInt() + note.getId());
                    }

                    @Override
                    public void onError(Throwable e) {
                        deleteNote.set(-1);
                    }
                });
    }

    public void updateNote(Note note) {
        Single<Note> singleUpdate = Single.just(note);
        singleUpdate.observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<Note, SingleSource<Note>>() {
                    @Override
                    public SingleSource<Note> apply(Note note) throws Exception {
                        noteRepository.UpdateNote(note);
                        return Single.just(note);
                    }
                })
                .subscribe(new SingleObserver<Note>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Note note) {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                });
    }

    public void DoJob(Note note) {
        Single<Note> singleUpdate = Single.just(note);
        singleUpdate.observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<Note, SingleSource<Note>>() {
                    @Override
                    public SingleSource<Note> apply(Note note) throws Exception {
                        noteRepository.UpdateNote(note);
                        return Single.just(note);
                    }
                })
                .subscribe(new SingleObserver<Note>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Note note) {
                        doneJob.set(note.getId() + getRandomInt());
                    }

                    @Override
                    public void onError(Throwable e) {
                        doneJob.set(-1);
                    }
                });
    }

    public LiveData<List<Note>> queryNotes(String s) {
        return noteRepository.QueryNotes(s);
    }

    public LiveData<List<Note>> queryMarks(String s) {
        return noteRepository.QueryMarks(s);
    }

    public LiveData<List<Note>> queryDoings(String s) {
        return noteRepository.QueryDoing(s);
    }

    public MutableLiveData<List<Note>> queryNotesDynamic(final String query) {
        if (queriedNotes == null) queriedNotes = new MutableLiveData<List<Note>>();
        getDynamicQueryNotesSubject(queriedNotes).onNext(query);
        return queriedNotes;
    }

    public ReplaySubject<String> getDynamicQueryNotesSubject(final MutableLiveData<List<Note>> queriedNotes) {
        if (this.queryNotesDynamicSubject == null) {
            this.queryNotesDynamicSubject = ReplaySubject.create();
            this.queryNotesDynamicSubject
                    .debounce(300, TimeUnit.MILLISECONDS)
                    .flatMapMaybe(new Function<String, MaybeSource<List<Note>>>() {
                        @Override
                        public MaybeSource<List<Note>> apply(String s) throws Exception {
                            return noteRepository.QueryNotesDynamic(s);
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<Note>>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            compositeDisposable.add(d);
                        }

                        @Override
                        public void onNext(List<Note> notes) {
                            queriedNotes.postValue(notes);
                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
        return queryNotesDynamicSubject;
    }

    public MutableLiveData<List<Note>> queryMarksDynamic(final String query) {
        MutableLiveData<List<Note>> queriedNotes = new MutableLiveData<List<Note>>();
        getDynamicQueryMarksSubject(queriedNotes).onNext(query);
        return queriedNotes;
    }

    public ReplaySubject<String> getDynamicQueryMarksSubject(final MutableLiveData<List<Note>> queriedNotes) {
        if (this.queryMarksDynamicSubject == null) {
            this.queryMarksDynamicSubject = ReplaySubject.create();
            this.queryMarksDynamicSubject
                    .debounce(300, TimeUnit.MILLISECONDS)
                    .flatMapMaybe(new Function<String, MaybeSource<List<Note>>>() {
                        @Override
                        public MaybeSource<List<Note>> apply(String s) throws Exception {
                            return noteRepository.QueryMarksDynamic(s);
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<Note>>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            compositeDisposable.add(d);

                        }

                        @Override
                        public void onNext(List<Note> notes) {
                            queriedNotes.postValue(notes);
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
        return queryMarksDynamicSubject;
    }

    public MutableLiveData<List<Note>> queryDoingsDynamic(final String query) {
        MutableLiveData<List<Note>> queriedNotes = new MutableLiveData<List<Note>>();
        getDynamicQueryDoingsSubject(queriedNotes).onNext(query);
        return queriedNotes;
    }

    public ReplaySubject<String> getDynamicQueryDoingsSubject(final MutableLiveData<List<Note>> queriedNotes) {
        if (this.queryDoingsDynamicSubject == null) {
            this.queryDoingsDynamicSubject = ReplaySubject.create();
            this.queryDoingsDynamicSubject
                    .debounce(300, TimeUnit.MILLISECONDS)
                    .flatMapMaybe(new Function<String, MaybeSource<List<Note>>>() {
                        @Override
                        public MaybeSource<List<Note>> apply(String s) throws Exception {
                            return noteRepository.QueryDoingsDynamic(s);
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<Note>>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            compositeDisposable.add(d);

                        }

                        @Override
                        public void onNext(List<Note> notes) {
                            queriedNotes.postValue(notes);
                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
        return queryDoingsDynamicSubject;
    }

    public ArrayList<Note> getDefaultNotes() {
        return defaultNotes;
    }

    public void setDefaultNotes(ArrayList<Note> defaultNotes) {
        this.defaultNotes = defaultNotes;
    }

    public ArrayList<Note> getDefaultMarkNotes() {
        return defaultMarkNotes;
    }

    public void setDefaultMarkNotes(ArrayList<Note> defaultMarkNotes) {
        this.defaultMarkNotes = defaultMarkNotes;
    }

    public ArrayList<Note> getDefaultTodoNotes() {
        return defaultTodoNotes;
    }

    public void setDefaultTodoNotes(ArrayList<Note> defaultTodoNotes) {
        this.defaultTodoNotes = defaultTodoNotes;
    }

    public LiveData<Integer> getNotesCount() {
        return noteRepository.getNotesCount();
    }

    public LiveData<Integer> getMarkedCount() {
        return noteRepository.getMarkedCount();
    }

    public LiveData<Integer> getTodoCount() {
        return noteRepository.getDoingCount();
    }


    public ObservableInt getDoneJob() {
        return doneJob;
    }

    public ObservableInt getDeleteNote() {
        return deleteNote;
    }

    public int getRandomInt() {
        Random random = new Random();
        return random.nextInt(9999999);
    }


    public void deleteAll() {
        Single<String> singleDelete = Single.just("");
        singleDelete.observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<String, SingleSource<String>>() {
                    @Override
                    public SingleSource<String> apply(String note) throws Exception {
                        noteRepository.DeleteAll();
                        return Single.just(note);
                    }
                })
                .subscribe(new SingleObserver<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(String note) {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                });

    }

    public void dispose() {
        compositeDisposable.dispose();
    }


}
