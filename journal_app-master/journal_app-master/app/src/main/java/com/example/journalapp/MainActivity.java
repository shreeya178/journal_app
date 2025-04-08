package com.example.journalapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton addJournalBtn;
    RecyclerView recyclerView;
    ImageButton menuBtn;
    JournalAdapter journalAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addJournalBtn = findViewById(R.id.add_journal_btn);
        recyclerView = findViewById(R.id.recyler_view);
        menuBtn = findViewById(R.id.menu_btn);

        addJournalBtn.setOnClickListener((v) -> startActivity(new Intent(MainActivity.this, JournalDetailsActivity.class)));
        menuBtn.setOnClickListener((v) -> showMenu());

        setupRecyclerView();
    }

    void showMenu() {
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, menuBtn);
        popupMenu.getMenu().add("Logout");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getTitle().equals("Logout")) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                    return true;
                }
                return false;
            }
        });
    }

    void setupRecyclerView() {
        Query query = Utility.getCollectionReferenceForJournal().orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Journal> options = new FirestoreRecyclerOptions.Builder<Journal>()
                .setQuery(query, Journal.class)
                .build();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        journalAdapter = new JournalAdapter(options, this);
        recyclerView.setAdapter(journalAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (journalAdapter != null) {
            journalAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (journalAdapter != null) {
            journalAdapter.stopListening();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        journalAdapter.notifyDataSetChanged();
    }
}
