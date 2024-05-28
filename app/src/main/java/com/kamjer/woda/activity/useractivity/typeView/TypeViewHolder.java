package com.kamjer.woda.activity.useractivity.typeView;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.kamjer.woda.R;
import com.kamjer.woda.activity.useractivity.coloreditdialog.ColorSelectorAction;
import com.kamjer.woda.model.Type;

public class TypeViewHolder extends RecyclerView.ViewHolder{

    private final EditText editTextType;
    private final ImageButton buttonColorPicker;

    private final ColorSelectorAction colorSelectorAction;
    private final TypeNameChangedAction typeNameChangedAction;
    private final ColorSelectorAction removeTypeAction;

    private final ImageButton buttonRemoveType;

    public TypeViewHolder(@NonNull View itemView,
                          ColorSelectorAction colorSelectorAction,
                          TypeNameChangedAction typeNameChangedAction,
                          ColorSelectorAction removeTypeAction) {
        super(itemView);
        this.colorSelectorAction = colorSelectorAction;
        this.typeNameChangedAction = typeNameChangedAction;
        this.removeTypeAction = removeTypeAction;

        this.editTextType = itemView.findViewById(R.id.editTextType);
        this.buttonColorPicker = itemView.findViewById(R.id.buttonColorPicker);
        this.buttonRemoveType = itemView.findViewById(R.id.buttonRemoveType);
    }

    public void bind(@NonNull Type type, int position) {
        editTextType.setText(type.getType());
        Drawable buttonShape = ResourcesCompat.getDrawable(itemView.getResources(), R.drawable.color_type_button_shape, null);
        if (buttonShape != null) {
            buttonShape.setColorFilter(type.getColor(), PorterDuff.Mode.SRC_IN);
            buttonColorPicker.setBackground(buttonShape);
        } else {
            Toast.makeText(itemView.getContext(), itemView.getContext().getResources().getText(R.string.error_message_can_not_load), Toast.LENGTH_LONG).show();
        }
        buttonColorPicker.setOnClickListener(v -> {
//          because how android manges focus i need to switch focus by hand when opening
            notifyFocusedCleared();
            colorSelectorAction.action(type, position);
        });

        editTextType.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                typeNameChangedAction.action(type, editTextType.getText());
            }
        });
        buttonRemoveType.setOnClickListener(v -> removeTypeAction.action(type, position));
    }

    public void notifyFocusedCleared() {
        editTextType.clearFocus();
    }
}
