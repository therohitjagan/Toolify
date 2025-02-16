package com.therohitjagan.toolify.alltools.notes;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notes")
public class Note {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String content; // HTML content from the RichEditor
    public String tags;
    public String color;   // Hex color string (e.g., "#FFFFFF")

    public Note(String content, String tags, String color) {
        this.content = content;
        this.tags = tags;
        this.color = color;
    }
}
