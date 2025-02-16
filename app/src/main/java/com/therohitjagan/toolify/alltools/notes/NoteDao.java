package com.therohitjagan.toolify.alltools.notes;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NoteDao {

    @Query("SELECT * FROM notes")
    List<Note> getAllNotes();

    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    Note getNoteById(int id);

    @Insert
    long insertNote(Note note);

    @Update
    int updateNote(Note note);

    @Delete
    int deleteNote(Note note);
}
