package com.therohitjagan.toolify.alltools.barcodetool;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.therohitjagan.toolify.R;
import com.therohitjagan.toolify.alltools.barcodetool.models.DatabaseHelper;
import com.therohitjagan.toolify.alltools.barcodetool.models.HistoryAdapter;
import com.therohitjagan.toolify.alltools.barcodetool.models.HistoryItem;
import java.util.ArrayList;

public class BarcodeHistory extends AppCompatActivity implements HistoryAdapter.OnHistoryItemActionListener {

    private RecyclerView rvHistory;
    private HistoryAdapter adapter;
    private ArrayList<HistoryItem> historyList;
    private DatabaseHelper dbHelper;
    private ViewGroup bannerContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_history);

        rvHistory = findViewById(R.id.rv_history);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        historyList = new ArrayList<>();
        adapter = new HistoryAdapter(historyList, this); // Pass 'this' as the callback listener.
        rvHistory.setAdapter(adapter);

        dbHelper = new DatabaseHelper(this);
        loadHistory();


    }

    private void loadHistory() {
        historyList.clear();
        Cursor cursor = dbHelper.getAllHistory();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String data = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATA));
                String format = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_FORMAT));
                String timestamp = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TIMESTAMP));
                historyList.add(new HistoryItem(data, format, timestamp));
            } while (cursor.moveToNext());
            cursor.close();
        }
        adapter.notifyDataSetChanged();
    }

    // Callback for deletion: remove item from database and adapter.
    @Override
    public void onDelete(HistoryItem item, int position) {
        // Delete the record from the database. Here we use a combination of data and timestamp as identifiers.


            dbHelper.deleteHistory(item);
            adapter.removeItem(position);
            Toast.makeText(this, "History record deleted", Toast.LENGTH_SHORT).show();

    }

    // Callback for sharing: create a share intent with the record's details.
    @Override
    public void onShare(HistoryItem item) {
        String shareText = "Data: " + item.getData() + "\nFormat: " + item.getFormat() + "\nTimestamp: " + item.getTimestamp();
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent, "Share History Record"));
    }
}
