package com.therohitjagan.toolify.alltools.notes;


import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import com.google.android.material.button.MaterialButton;
import com.therohitjagan.toolify.R;

import jp.wasabeef.richeditor.RichEditor;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class NoteEditorActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_IMAGE = 101;
    private static final int REQUEST_PERMISSION_READ_STORAGE = 102;

    private RichEditor mEditor;
    private ImageButton btnBold, btnItalic, btnUnderline, btnInsertImage, btnAddChecklist, btnChangeColor;
    private MaterialButton btnShareNote;
    private LinearLayout checklistContainer;
    private EditText tagsEditText;

    // Track the selected background color for this note.
    private String selectedColor = "#FFFFFF";

    // When editing an existing note, this holds its id.
    private int editingNoteId = -1;

    // Database instance and DAO.
    private NoteDatabase noteDatabase;

    private ViewGroup bannerContainer;

    private static final int MAX_IMAGE_SIZE = 1024 * 1024; // 1MB limit

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tagsEditText = findViewById(R.id.tagsEditText);
        mEditor = findViewById(R.id.richeditor);
        mEditor.setEditorHeight(200);
        mEditor.setEditorFontSize(16);
        mEditor.setPadding(10, 10, 10, 10);
        mEditor.setPlaceholder("Start writing your note...");

        btnBold = findViewById(R.id.action_bold);
        btnItalic = findViewById(R.id.action_italic);
        btnUnderline = findViewById(R.id.action_underline);
        btnInsertImage = findViewById(R.id.action_insert_image);
        btnAddChecklist = findViewById(R.id.action_add_checklist);
        btnChangeColor = findViewById(R.id.action_change_color);
        btnShareNote = findViewById(R.id.btnShareNote);
        checklistContainer = findViewById(R.id.checklistContainer);

        // Initialize Room database.
        noteDatabase = NoteDatabase.getDatabase(this);

        // Formatting actions.
        btnBold.setOnClickListener(v -> mEditor.setBold());
        btnItalic.setOnClickListener(v -> mEditor.setItalic());
        btnUnderline.setOnClickListener(v -> mEditor.setUnderline());

        btnInsertImage.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(NoteEditorActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(NoteEditorActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_READ_STORAGE);
            } else {
                openImagePicker();
            }
        });

        btnAddChecklist.setOnClickListener(v -> addChecklistItem("New Item"));

        btnChangeColor.setOnClickListener(v -> showColorPickerDialog());

        btnShareNote.setOnClickListener(v -> {
            saveOrUpdateNote();
            shareNote();
        });

        // Check if we’re editing an existing note.
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("noteId")) {
            editingNoteId = intent.getIntExtra("noteId", -1);
            loadNote(editingNoteId);
        }

    }

    /**
     * Loads the note details for editing from the database.
     */
    private void loadNote(int noteId) {
        Note note = noteDatabase.noteDao().getNoteById(noteId);
        if (note != null) {
            tagsEditText.setText(note.tags);
            mEditor.setHtml(note.content);
            selectedColor = note.color;
            mEditor.setBackgroundColor(Color.parseColor(selectedColor));
        }
    }

    /**
     * Saves the current note (either as new or updates an existing one) to the database.
     */
    private void saveOrUpdateNote() {
        String content = mEditor.getHtml();
        String tags = tagsEditText.getText().toString();
        if (TextUtils.isEmpty(content)) {
            Snackbar.make(findViewById(R.id.noteCoordinatorLayout),
                    "Cannot save an empty note", Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (editingNoteId == -1) {
            // Save new note.
            Note newNote = new Note(content, tags, selectedColor);
            long newId = noteDatabase.noteDao().insertNote(newNote);
            editingNoteId = (int) newId;
        } else {
            // Update existing note.
            Note updatedNote = new Note(content, tags, selectedColor);
            updatedNote.id = editingNoteId;
            noteDatabase.noteDao().updateNote(updatedNote);
        }
    }

    /**
     * Opens the image picker.
     */
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    /**
     * Dynamically adds a checklist item.
     */
    private void addChecklistItem(String text) {
        CheckBox checkBox = new CheckBox(this);
        checkBox.setText(text);
        checkBox.setPadding(8, 8, 8, 8);
        checklistContainer.addView(checkBox);
    }

    /**
     * Displays a simple color picker dialog.
     */
    private void showColorPickerDialog() {
        // Predefined color options.
        final String[] colors = {"#FFFFFF", "#FFCDD2", "#F8BBD0", "#E1BEE7", "#D1C4E9",
                "#C5CAE9", "#BBDEFB", "#B3E5FC", "#B2EBF2", "#B2DFDB"};
        final String[] colorNames = {"White", "Red", "Pink", "Purple", "Deep Purple",
                "Indigo", "Blue", "Light Blue", "Cyan", "Teal"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Note Color");
        builder.setItems(colorNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedColor = colors[which];
                // Update the editor background with the chosen color.
                mEditor.setBackgroundColor(Color.parseColor(selectedColor));
            }
        });
        builder.show();
    }

    /**
     * Shares the note content.
     */
    private void shareNote() {
        String noteContent = mEditor.getHtml();
        String tags = tagsEditText.getText().toString();
        String shareText = "";
        if (!TextUtils.isEmpty(tags)) {
            shareText += "Tags: " + tags + "\n\n";
        }
        shareText += "Note:\n" + (noteContent != null ? noteContent.replaceAll("\\<.*?\\>", "") : "");
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent, "Share Note via"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Auto-save the note when leaving the activity.
        saveOrUpdateNote();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            handleImageInsertion(imageUri);
        }
    }

    /**
     * Converts the image at the given URI into a Base64 encoded string.
     */
    private String encodeImageToBase64(Uri imageUri) throws IOException {
        ContentResolver resolver = getContentResolver();
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(resolver, imageUri);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.NO_WRAP);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_READ_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Snackbar.make(findViewById(R.id.noteCoordinatorLayout),
                        "Permission denied to access images", Snackbar.LENGTH_LONG).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void handleImageInsertion(Uri imageUri) {
        if (imageUri == null) return;

        try {
            // Add error handling and size checking
            ContentResolver resolver = getContentResolver();
            Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(resolver, imageUri);

            // Calculate dimensions for resizing while maintaining aspect ratio
            int maxDimension = 1024; // Maximum width or height
            float scale = Math.min(
                    (float) maxDimension / originalBitmap.getWidth(),
                    (float) maxDimension / originalBitmap.getHeight()
            );

            int newWidth = Math.round(originalBitmap.getWidth() * scale);
            int newHeight = Math.round(originalBitmap.getHeight() * scale);

            // Resize bitmap
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(
                    originalBitmap, newWidth, newHeight, true);

            // Convert to base64 with compression
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            byte[] imageBytes = outputStream.toByteArray();

            if (imageBytes.length > MAX_IMAGE_SIZE) {
                throw new IOException("Image size too large after compression");
            }

            String base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

            // Insert with proper formatting and error checking
            if (!TextUtils.isEmpty(base64Image)) {
                runOnUiThread(() -> {
                    try {
                        mEditor.insertImage("data:image/jpeg;base64," + base64Image,
                                "Inserted Image " + System.currentTimeMillis());
                    } catch (Exception e) {
                        showError("Failed to insert image into editor");
                    }
                });
            } else {
                throw new IOException("Failed to encode image");
            }

            // Clean up
            resizedBitmap.recycle();
            originalBitmap.recycle();
            outputStream.close();

        } catch (OutOfMemoryError e) {
            showError("Image is too large to process");
        } catch (IOException e) {
            showError("Failed to process image: " + e.getMessage());
        } catch (Exception e) {
            showError("Unexpected error while processing image");
        }
    }

    private void showError(String message) {
        runOnUiThread(() -> {
            Snackbar.make(findViewById(R.id.noteCoordinatorLayout),
                    message, Snackbar.LENGTH_LONG).show();
        });
    }


}

