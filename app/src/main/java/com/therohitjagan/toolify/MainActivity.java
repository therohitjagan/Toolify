package com.therohitjagan.toolify;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.therohitjagan.toolify.adapter.ToolAdapter;
import com.therohitjagan.toolify.alltools.calculators.CalculatorActivity;
import com.therohitjagan.toolify.alltools.PasswordGeneratorActivity;
import com.therohitjagan.toolify.alltools.barcodetool.BarcodeGeneratorActivity;
import com.therohitjagan.toolify.alltools.barcodetool.BarcodeHistory;
import com.therohitjagan.toolify.alltools.barcodetool.BarcodeScannerActivity;
import com.therohitjagan.toolify.alltools.calculators.LoanCalculatorActivity;
import com.therohitjagan.toolify.alltools.conversion.SpeechActivity;
import com.therohitjagan.toolify.alltools.conversion.TextSpeech;
import com.therohitjagan.toolify.alltools.notes.NoteListActivity;
import com.therohitjagan.toolify.alltools.systemtools.SystemInfoActivity;
import com.therohitjagan.toolify.alltools.texttools.CaseConverterActivity;
import com.therohitjagan.toolify.alltools.texttools.RegexTesterActivity;
import com.therohitjagan.toolify.alltools.texttools.TextSummarizationActivity;
import com.therohitjagan.toolify.alltools.texttools.WordCharacterCountActivity;
import com.therohitjagan.toolify.alltools.unitconvert.LengthConverterActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements ToolAdapter.OnToolClickListener {
    private RecyclerView recyclerView;
    private ToolAdapter toolAdapter;
    private List<Tool> toolList;
    private List<Tool> filteredList;
    private TabLayout categoryTabs;
    private TextInputEditText searchInput;
    private String currentCategory = "All";
    private ViewGroup bannerContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        searchInput = findViewById(R.id.searchInput);
        categoryTabs = findViewById(R.id.categoryTabs);

        setupRecyclerView();
        setupSearch();
        loadTools();
        setupCategoryTabs();




    }

    private void setupRecyclerView() {
        toolList = new ArrayList<>();
        filteredList = new ArrayList<>();
        toolAdapter = new ToolAdapter(this, filteredList, this);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(toolAdapter);
    }

    private void setupSearch() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTools(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupCategoryTabs() {
        Set<String> categories = new HashSet<>();
        categories.add("All");

        for (Tool tool : toolList) {
            categories.add(tool.getCategory());
        }

        categoryTabs.addTab(categoryTabs.newTab().setText("All"));
        for (String category : categories) {
            if (!category.equals("All")) {
                categoryTabs.addTab(categoryTabs.newTab().setText(category));
            }
        }

        categoryTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentCategory = tab.getText().toString();
                filterTools(searchInput.getText().toString());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void filterTools(String query) {
        filteredList.clear();
        for (Tool tool : toolList) {
            boolean matchesSearch = tool.getName().toLowerCase().contains(query.toLowerCase()) ||
                    tool.getCategory().toLowerCase().contains(query.toLowerCase());
            boolean matchesCategory = currentCategory.equals("All") ||
                    tool.getCategory().equals(currentCategory);

            if (matchesSearch && matchesCategory) {
                filteredList.add(tool);
            }
        }
        toolAdapter.notifyDataSetChanged();
    }

    private void loadTools() {
        // Calculator Category
        toolList.add(new Tool("Calculator", R.drawable.ic_calculator, "Calculator", CalculatorActivity.class));
        toolList.add(new Tool("Loan Calculator", R.drawable.ic_loan, "Calculator", LoanCalculatorActivity.class));


        // Utility Category
        toolList.add(new Tool("Notes", R.drawable.ic_notes, "Productivity", NoteListActivity.class));
       // toolList.add(new Tool("Timer", R.drawable.ic_timer, "Utility", TimerActivity.class));
        toolList.add(new Tool("Password Generator", R.drawable.ic_password, "Security", PasswordGeneratorActivity.class));


        // Converter Category
        toolList.add(new Tool("Length Converter", R.drawable.ic_length, "Conversion", LengthConverterActivity.class));



        // Barcode Tools Category
        toolList.add(new Tool("Barcode Scanner", R.drawable.ic_barcode_scanner, "Generation", BarcodeScannerActivity.class ));
        toolList.add(new Tool("Barcode Generator", R.drawable.ic_barcode_generator, "Generation", BarcodeGeneratorActivity.class ));
        toolList.add(new Tool("Barcode History", R.drawable.ic_barcode_history, "Generation", BarcodeHistory.class ));

        //Text Tools
        toolList.add(new Tool("Case Converter", R.drawable.ic_case, "Text Tools", CaseConverterActivity.class ));
        toolList.add(new Tool("Text Summarize", R.drawable.ic_summerize, "Text Tools", TextSummarizationActivity.class ));
        toolList.add(new Tool("Word Count", R.drawable.ic_word, "Text Tools", WordCharacterCountActivity.class ));
        toolList.add(new Tool("Text to Speech", R.drawable.ic_speech, "Text Tools", SpeechActivity.class ));
        toolList.add(new Tool("Speech to Text", R.drawable.ic_text, "Text Tools", TextSpeech.class ));
        toolList.add(new Tool("Regex Tester", R.drawable.ic_regex, "Text Tools", RegexTesterActivity.class ));

        //System Tools
        toolList.add(new Tool("System Info", R.drawable.ic_system, "Device Tools", SystemInfoActivity.class ));




        filteredList.addAll(toolList);
        toolAdapter.notifyDataSetChanged();
    }

    @Override
    public void onToolClick(Tool tool) {
            startActivity(new Intent(this, tool.getActivityClass()));



    }
}
