package com.kamjer.woda.activity.useractivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kamjer.woda.R;
import com.kamjer.woda.activity.useractivity.coloreditdialog.ColorPickerDialog;
import com.kamjer.woda.activity.useractivity.coloreditdialog.ColorSelectorAction;
import com.kamjer.woda.activity.useractivity.deletetypeconflictdialog.DeleteTypeConflictDialog;
import com.kamjer.woda.activity.useractivity.typeView.TypeListViewAdapter;
import com.kamjer.woda.activity.useractivity.typeView.TypeNameChangedAction;
import com.kamjer.woda.activity.useractivity.typeView.TypeRecyclerView;
import com.kamjer.woda.model.Type;
import com.kamjer.woda.model.Water;
import com.kamjer.woda.repository.ResourcesRepository;
import com.kamjer.woda.utils.exception.TypeToRemoveCanNotBeDefaultException;
import com.kamjer.woda.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserActivity extends AppCompatActivity {

    private EditText textViewWaterAmount;
    private UserViewModel userViewModel;

    private TypeListViewAdapter adapter;

    private ColorSelectorAction colorSelectorAction;
    private ColorSelectorAction buttonRemoveTypeAction;

    private TypeNameChangedAction typeNameChangedAction;

    private TypeRecyclerView typeListRecycler;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userViewModel = new ViewModelProvider(this, ViewModelProvider.Factory.from(UserViewModel.initializer)).get(UserViewModel.class);

        ActivityResultLauncher<Intent> removeTypeConflictDialogLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
//                            getting data from dialog
                            DeleteTypeConflictDialog.Selected selected = Optional
                                    .ofNullable((DeleteTypeConflictDialog.Selected) data.getSerializableExtra(DeleteTypeConflictDialog.ACTION_SELECTED_NAME))
                                    .orElse(DeleteTypeConflictDialog.Selected.NOTHING);
                            Type typeToDelete = (Type) Optional.ofNullable(data.getSerializableExtra(DeleteTypeConflictDialog.TYPE_TO_DELETE_NAME)).orElse(new Type());
                            List<Water> waterList =  Optional.ofNullable((ArrayList<Water>) data.getSerializableExtra(DeleteTypeConflictDialog.WATER_LIST_NAME))
                                    .orElse(new ArrayList<>());
//                            handling selection
                            switch (selected) {
                                case CHANGE:
                                    Type typeSelected = Optional.ofNullable((Type) data.getSerializableExtra(DeleteTypeConflictDialog.TYPE_SELECTED_NAME)).orElse(new Type());
                                    changeTypesDialogAction(typeSelected, waterList, typeToDelete);
                                    break;
                                case DELETE:
                                    deleteTypesDialogAction(waterList, typeToDelete);
                                    break;
                            }
                        }
                    }
                });

        buttonRemoveTypeAction = (type, position) -> userViewModel.loadWatersByType(type, this, waters -> {
            ArrayList<Water> waterList = new ArrayList<>(waters);

//                if there are no waters with this type delete that type
            if (waters.isEmpty()) {
                try {
                    userViewModel.removeType(type);
                } catch (TypeToRemoveCanNotBeDefaultException e) {
                    Toast.makeText(this, ResourcesRepository.getInstance().getDeleteTypeDefaultMessageException(), Toast.LENGTH_LONG).show();
                }
//              if there are waters with this type ask user what to do
            } else {
                Intent deleteTypeConflictDialogIntent = new Intent(this, DeleteTypeConflictDialog.class);
                deleteTypeConflictDialogIntent.putExtra(DeleteTypeConflictDialog.TYPE_LIST_NAME, new ArrayList<>(userViewModel.getTypes().values()));
                deleteTypeConflictDialogIntent.putExtra(DeleteTypeConflictDialog.WATER_LIST_NAME, waterList);
                deleteTypeConflictDialogIntent.putExtra(DeleteTypeConflictDialog.TYPE_TO_DELETE_NAME, type);
                removeTypeConflictDialogLauncher.launch(deleteTypeConflictDialogIntent);
            }
        });

//        dialog for choosing new color
        ActivityResultLauncher<Intent> colorEditStartYourDialogLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Type typeToUpdate = Optional.ofNullable((Type) data.getSerializableExtra("type"))
                                    .orElse(new Type());
                            userViewModel.updateType(typeToUpdate);
                        }
                    }
                });

        colorSelectorAction = (type, position) -> {
            Intent colorPickerDialog = new Intent(this, ColorPickerDialog.class);
            colorPickerDialog.putExtra("type", type);
            colorEditStartYourDialogLauncher.launch(colorPickerDialog);
        };

        typeNameChangedAction = (type, newName) -> {
            type.setType(newName.toString());
            userViewModel.updateType(type);
        };

        userViewModel.setTypesObserver(this, longTypeHashMap ->
                adapter.setTypeList(new ArrayList<>(userViewModel.getTypes().values())));

        userViewModel.setWaterAmountToDrinkObserver(this, integer -> {
            textViewWaterAmount.setText(String.valueOf(userViewModel.getWaterAmountToDrink()));
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.user_activity_layout);

        ImageButton addWaterTypeButton = findViewById(R.id.imageButtonAddWaterType);
        addWaterTypeButton.setOnClickListener(v -> addNewTypeAction());

        textViewWaterAmount = findViewById(R.id.editTextNumber);
        textViewWaterAmount.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                setWaterAmountAction(v);
            }
        });
        adapter = createNewTypeAdapter(new ArrayList<>(userViewModel.getTypes().values()));
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        typeListRecycler = findViewById(R.id.recyclerViewTypeList);
        typeListRecycler.setLayoutManager(layoutManager);
        typeListRecycler.setAdapter(adapter);
    }

    private void changeTypesDialogAction(Type typeSelected, List<Water> waterList, Type typeToDelete) {
        long typeIdSelected = typeSelected.getId();
        waterList.forEach(water -> water.setTypeId(typeIdSelected));
        userViewModel.insertWatersSumWatersDeleteType(waterList, typeToDelete);
    }

    private void deleteTypesDialogAction(List<Water> waterList, Type typeToDelete) {
        userViewModel.removeWaters(waterList);
        try {
            userViewModel.removeType(typeToDelete);
        } catch (TypeToRemoveCanNotBeDefaultException e) {
            Toast.makeText(this, ResourcesRepository.getInstance().getDeleteTypeDefaultMessageException(), Toast.LENGTH_LONG).show();
        }
    }

    private void setWaterAmountAction(View view) {
        int waterAmountToDrink = Integer.parseInt(textViewWaterAmount.getText().toString());
        userViewModel.setWaterAmountToDrink(getApplicationContext(), waterAmountToDrink);
    }

    private void addNewTypeAction() {
        Type newType = new Type("", Color.BLACK);
        userViewModel.insertType(newType);
    }

    private TypeListViewAdapter createNewTypeAdapter(ArrayList<Type> typeList) {
        return adapter = new TypeListViewAdapter(
                typeList,
                colorSelectorAction,
                buttonRemoveTypeAction,
                typeNameChangedAction);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        clears focus on all elements in a recyclerView and textView, necessary to fire focus listener on ViewHolder
        typeListRecycler.notifyFocusCleared();
        textViewWaterAmount.clearFocus();
        userViewModel.clearDisposable();
    }
}
