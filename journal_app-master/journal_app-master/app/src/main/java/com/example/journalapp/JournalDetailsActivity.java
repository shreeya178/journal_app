package com.example.journalapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class JournalDetailsActivity extends AppCompatActivity {

    EditText titleEditText, contentEditText;
    ImageButton saveJournalBtn;
    TextView pageTitleTextView;
    String title, content, docID;
    boolean isEditMode = false;
    TextView deleteJournalTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_details);

        titleEditText = findViewById(R.id.journal_title_text);
        contentEditText = findViewById(R.id.journal_content_text);
        saveJournalBtn = findViewById(R.id.save_journal_btn);
        pageTitleTextView = findViewById(R.id.page_title);
        deleteJournalTextView = findViewById(R.id.delete_journal_text_view_btn);

        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        docID = getIntent().getStringExtra("docId");

        if(docID!=null && !docID.isEmpty()){
            isEditMode = true;
        }

        if(isEditMode){
            pageTitleTextView.setText("Edit Journal");
            deleteJournalTextView.setVisibility(View.VISIBLE);
        }

        titleEditText.setText(title);
        contentEditText.setText(content);


        saveJournalBtn.setOnClickListener((v) -> saveJournal());
        deleteJournalTextView.setOnClickListener((v)->deleteJournalFromFirebase());
    }

    void saveJournal() {
        String JournalTitle = titleEditText.getText().toString();
        String JournalContent = contentEditText.getText().toString();

        if (JournalTitle == null || JournalTitle.isEmpty()) {
            titleEditText.setError("Title is required");
            return;
        }

        Journal journal = new Journal();
        journal.setTitle(JournalTitle);
        journal.setContent(JournalContent);
        journal.setTimestamp(Timestamp.now());

        saveJournalToFirebase(journal);

    }

    void saveJournalToFirebase(Journal journal){
        DocumentReference documentReference;

        if(isEditMode){
            documentReference = Utility.getCollectionReferenceForJournal().document(docID);
        } else {
            documentReference = Utility.getCollectionReferenceForJournal().document();
        }

        documentReference.set(journal).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // note is added
                    Utility.showToast(JournalDetailsActivity.this, "Note added successfully");
                    finish();
                } else {
                    Utility.showToast(JournalDetailsActivity.this, "Failed while adding note");
                }
            }
        });

    }

    void deleteJournalFromFirebase(){

        DocumentReference documentReference;
        documentReference = Utility.getCollectionReferenceForJournal().document(docID);

        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // note is added
                    Utility.showToast(JournalDetailsActivity.this, "Deleted successfully");
                    finish();
                } else {
                    Utility.showToast(JournalDetailsActivity.this, "Failed while deleting note");
                }
            }
        });

    }
}