package com.kamjer.woda.activity.useractivity.deletetypeconflictdialog;

import android.app.Activity;
import android.app.Notification;
import android.graphics.Path;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kamjer.woda.R;
import com.kamjer.woda.model.Type;
import com.kamjer.woda.model.Water;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class DeleteTypeConflictDialog extends AppCompatActivity {

    public static final String WATER_LIST_NAME = "waters";
    public static final String TYPE_TO_DELETE_NAME = "typeToDelete";
    public static final String TYPE_SELECTED_NAME = "typeSelected";
    public static final String ACTION_SELECTED_NAME = "selected";
    public static final String TYPE_LIST_NAME = "typylist";


    private ArrayList<Type> typeList;
    private ArrayList<Water> waterList;
    private Type typeToDelete;

    private Button deleteButton;
    private Button chageButton;
    private Button nothingButton;

    private Spinner spinner;

    public enum Selected {
        DELETE,
        CHANGE,
        NOTHING
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        typeList = Optional.ofNullable((ArrayList<Type>) getIntent().getSerializableExtra(TYPE_LIST_NAME))
                .orElse(new ArrayList<>());
        waterList =  Optional.ofNullable((ArrayList<Water>) getIntent().getSerializableExtra(WATER_LIST_NAME))
                .orElse(new ArrayList<>());
        typeToDelete =  Optional.ofNullable((Type) getIntent().getSerializableExtra(TYPE_TO_DELETE_NAME)).orElse(new Type());
    }

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.delete_type_conflict_dialog);
        deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setText(getResources().getText(R.string.text_delete_button_delete_type_conflict_dialog));

        chageButton = findViewById(R.id.changeButton);
        chageButton.setText(getResources().getText(R.string.text_change_button_delete_type_conflict_dialog));

        nothingButton = findViewById(R.id.nothingButton);
        nothingButton.setText(getResources().getText(R.string.text_nothing_button_delete_type_conflict_dialog));

        ArrayAdapter<Type> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, typeList);
        spinner = findViewById(R.id.typeSpinner);
        spinner.setAdapter(adapter);

        deleteButton.setOnClickListener(v -> setActionSelectedAction(Selected.DELETE));
        chageButton.setOnClickListener(v -> setActionSelectedAction(Selected.CHANGE));
        nothingButton.setOnClickListener(v -> setActionSelectedAction(Selected.NOTHING));
    }

    private void setActionSelectedAction(Selected selected) {
        getIntent().putExtra(ACTION_SELECTED_NAME, selected);
        if (Objects.requireNonNull(selected) == Selected.CHANGE) {
            getIntent().putExtra(TYPE_SELECTED_NAME, (Type) spinner.getSelectedItem());
        }
        setResult(Activity.RESULT_OK, getIntent());
        this.finish();
    }
}
