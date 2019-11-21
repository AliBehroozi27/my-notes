package com.example.mynotes.model;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Maybe;


@Dao
public interface NoteDao {

    @Query("SELECT * FROM notes")
    LiveData<List<Note>> getAllNotes();

    @Query("SELECT COUNT(*) FROM notes")
    LiveData<Integer> getAllNotesCount();

    @Query("SELECT * FROM notes WHERE text LIKE :queryText ")
    LiveData<List<Note>> queryNotes(String queryText);

    @Query("SELECT * FROM notes WHERE text LIKE :queryText AND isMarked = 1")
    LiveData<List<Note>> queryMarks(String queryText);

    @Query("SELECT * FROM notes WHERE text LIKE :queryText AND isDoing = 1")
    LiveData<List<Note>> queryDoing(String queryText);

    @Query("SELECT * FROM notes WHERE text LIKE :queryText ")
    Maybe<List<Note>> queryNotesDynamic(String queryText);

    @Query("SELECT * FROM notes WHERE text LIKE :queryText AND isMarked = 1")
    Maybe<List<Note>> queryMarksDynamic(String queryText);

    @Query("SELECT * FROM notes WHERE text LIKE :queryText AND isDoing = 1")
    Maybe<List<Note>> queryDoingDynamic(String queryText);

    @Query("SELECT * FROM notes WHERE isDoing = 1")
    LiveData<List<Note>> getAllIsDoings();

    @Query("SELECT * FROM notes WHERE isMarked = 1")
    LiveData<List<Note>> getAllFavorites();

    @Query("SELECT COUNT(*) FROM notes WHERE isDoing = 1")
    LiveData<Integer> getAllIsDoingsCount();

    @Query("SELECT COUNT(*) FROM notes WHERE isMarked = 1")
    LiveData<Integer> getAllFavoritesCount();

    @Query("SELECT COUNT(*) FROM notes")
    int getSize();

    @Insert
    void insert(Note note);

    @Delete
    void delete(Note note);

    @Query("UPDATE notes SET text =:uText, date =:uDate, isMarked=:uIsMarked ,isDoing =:uIsDoing , isDone =:uIsDone WHERE id =:id")
    void update(int id, String uText, String uDate, boolean uIsMarked, boolean uIsDoing, boolean uIsDone);

    @Query("DELETE FROM notes")
    void deleteAll();

}

