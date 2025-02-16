package com.therohitjagan.toolify.alltools.notes;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.therohitjagan.toolify.R;

import java.util.List;

public class NoteListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    private FloatingActionButton fabAddNote;

    // Room database instance.
    private NoteDatabase noteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);

        Toolbar toolbar = findViewById(R.id.toolbarList);
        setSupportActionBar(toolbar);

        // Initialize Room database.
        noteDatabase = NoteDatabase.getDatabase(this);

        recyclerView = findViewById(R.id.notesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteAdapter = new NoteAdapter(noteDatabase.noteDao().getAllNotes());
        recyclerView.setAdapter(noteAdapter);

        fabAddNote = findViewById(R.id.fabAddNote);
        fabAddNote.setOnClickListener(v -> {
            // Launch NoteEditorActivity to create a new note.
            Intent intent = new Intent(NoteListActivity.this, NoteEditorActivity.class);
            startActivity(intent);
        });

        // Enable swipe-to-delete using ItemTouchHelper.
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int pos = viewHolder.getAdapterPosition();
                Note note = noteAdapter.getNoteAt(pos);
                noteDatabase.noteDao().deleteNote(note);
                noteAdapter.removeNoteAt(pos);
                Snackbar.make(recyclerView, "Note deleted", Snackbar.LENGTH_SHORT).show();
            }
        };

        new ItemTouchHelper(simpleItemTouchCallback).attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list when returning from the editor.
        noteAdapter.updateNotes(noteDatabase.noteDao().getAllNotes());
    }

    // RecyclerView Adapter for notes.
    private class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
        private List<Note> noteList;

        NoteAdapter(List<Note> noteList) {
            this.noteList = noteList;
        }

        @NonNull
        @Override
        public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
            return new NoteViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
            Note note = noteList.get(position);
            holder.tvTags.setText(note.tags);
            // Strip HTML tags from content for preview.
            String plainText = note.content != null ? Html.fromHtml(note.content).toString() : "";
            holder.tvContent.setText(plainText);

            // Set card background to the note's selected color.
            holder.itemView.setBackgroundColor(android.graphics.Color.parseColor(note.color));

            holder.itemView.setOnClickListener(v -> {
                // Open note for editing. Pass note id via Intent.
                Intent intent = new Intent(NoteListActivity.this, NoteEditorActivity.class);
                intent.putExtra("noteId", note.id);
                startActivity(intent);
            });

            holder.btnDelete.setOnClickListener(v -> {
                int pos = holder.getAdapterPosition();
                noteDatabase.noteDao().deleteNote(note);
                removeNoteAt(pos);
                Snackbar.make(recyclerView, "Note deleted", Snackbar.LENGTH_SHORT).show();
            });
        }

        @Override
        public int getItemCount() {
            return noteList.size();
        }

        Note getNoteAt(int position) {
            return noteList.get(position);
        }

        void removeNoteAt(int position) {
            noteList.remove(position);
            notifyItemRemoved(position);
        }

        void updateNotes(List<Note> newNotes) {
            noteList = newNotes;
            notifyDataSetChanged();
        }

        class NoteViewHolder extends RecyclerView.ViewHolder {
            TextView tvTags, tvContent;
            ImageButton btnDelete;

            public NoteViewHolder(View itemView) {
                super(itemView);
                tvTags = itemView.findViewById(R.id.tvTags);
                tvContent = itemView.findViewById(R.id.tvContent);
                btnDelete = itemView.findViewById(R.id.btnDeleteNote);
            }
        }
    }
}
