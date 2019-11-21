package com.example.mynotes.repository;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import com.example.mynotes.model.Note;
import com.example.mynotes.model.NoteDao;
import com.example.mynotes.model.NoteDatabase;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;

public class NoteRepository {
    private NoteDao noteDao;

    public NoteRepository(Context context) {
        this.noteDao = NoteDatabase.getInstance(context).getNoteDao();
    }

    public LiveData<List<Note>> getAllNotes() {
        return noteDao.getAllNotes();
    }

    public LiveData<List<Note>> getAllFavorites() {
        return noteDao.getAllFavorites();
    }

    public LiveData<List<Note>> getAllDoings() {
        return noteDao.getAllIsDoings();
    }

    public void InsertNote(Note note) {
        noteDao.insert(note);
    }

    public int GetSize() {
        return noteDao.getSize();
    }

    public void DeleteNote(Note note) {
        noteDao.delete(note);
    }

    public LiveData<List<Note>> QueryNotes(String text) {
        return noteDao.queryNotes("%"+text+"%");
    }

    public Maybe<List<Note>> QueryNotesDynamic(String text) {
        return noteDao.queryNotesDynamic("%"+text+"%");
    }

    public Maybe<List<Note>> QueryMarksDynamic(String text) {
        return noteDao.queryMarksDynamic("%"+text+"%");
    }

    public Maybe<List<Note>> QueryDoingsDynamic(String text) {
        return noteDao.queryDoingDynamic("%"+text+"%");
    }

    public LiveData<List<Note>> QueryMarks(String text) {
        return noteDao.queryMarks("%"+text+"%");
    }

    public LiveData<List<Note>> QueryDoing(String text) {
        return noteDao.queryDoing("%"+text+"%");
    }

    public void UpdateNote(Note note) {
        noteDao.update(note.getId(), note.getText(), note.getDate(), note.isMarked(), note.isDoing(), note.isDone());
    }

    public void DeleteAll() {
        noteDao.deleteAll();
    }

    public LiveData<Integer> getNotesCount(){
        return noteDao.getAllNotesCount();
    }
    public LiveData<Integer> getMarkedCount(){
        return noteDao.getAllFavoritesCount();
    }
    public LiveData<Integer> getDoingCount(){
        return noteDao.getAllIsDoingsCount();
    }
}
