package com.example.mynotes.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.NonNull;

import com.example.mynotes.repository.NoteRepository;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private final Context context;
    private NoteRepository noteRepository;
    private static ViewModelFactory viewModelInstance;

    public static ViewModelFactory getInstance(Context context, NoteRepository noteRepository){
        if (viewModelInstance == null) viewModelInstance = new ViewModelFactory(context , noteRepository);
        return viewModelInstance;
    }

    private ViewModelFactory(Context context ,NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
        this.context = context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(NoteViewModel.class))
            return (T) NoteViewModel.getInstance(context ,noteRepository);

        return null;
    }
}
