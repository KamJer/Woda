package com.kamjer.woda.activity.useractivity.typeView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kamjer.woda.R;
import com.kamjer.woda.activity.useractivity.coloreditdialog.ColorSelectorAction;
import com.kamjer.woda.model.Type;

import java.util.ArrayList;

public class TypeListViewAdapter extends RecyclerView.Adapter<TypeViewHolder> {

    private ArrayList<Type> typeList;

    private final ColorSelectorAction colorSelectorAction;
    private final ColorSelectorAction buttonRemoveTypeAction;
    private final TypeNameChangedAction typeNameChangedAction;
    public TypeListViewAdapter(ArrayList<Type> typeList,
                               ColorSelectorAction colorSelectorAction,
                               ColorSelectorAction buttonRemoveType,
                               TypeNameChangedAction typeNameChangedAction) {
        this.typeList = typeList;
        this.colorSelectorAction = colorSelectorAction;
        this.buttonRemoveTypeAction = buttonRemoveType;
        this.typeNameChangedAction = typeNameChangedAction;
    }

    @NonNull
    @Override
    public TypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//      Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_type_cell, parent, false);
        return new TypeViewHolder(view, colorSelectorAction, typeNameChangedAction, buttonRemoveTypeAction);
    }

    @Override
    public void onBindViewHolder(@NonNull TypeViewHolder holder, int position) {
        holder.bind(typeList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return typeList.size();
    }

    public void addType(Type type) {
        typeList.add(type);
        notifyDataSetChanged();
    }

    public void removeType(Type type) {
        typeList.remove(type);
        notifyDataSetChanged();
    }

    public void setTypeList(ArrayList<Type> typeList) {
        this.typeList = typeList;
        this.notifyDataSetChanged();
    }

    public ArrayList<Type> getTypeList() {
        return typeList;
    }
}
