package com.kamjer.woda.activity.useractivity.typeView;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.kamjer.woda.model.Type;

public class TypeRecyclerView extends RecyclerView {
    public TypeRecyclerView(@NonNull Context context) {
        super(context);
    }

    public TypeRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TypeRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void notifyFocusCleared() {
        TypeListViewAdapter adapter = (TypeListViewAdapter) getAdapter();
        if (adapter != null) {
            for (int i = 0; i < adapter.getItemCount(); i++) {
                TypeViewHolder holder = (TypeViewHolder) findViewHolderForAdapterPosition(i);
                if (holder != null) {
                    holder.notifyFocusedCleared();
                }
            }
        }
    }
}
