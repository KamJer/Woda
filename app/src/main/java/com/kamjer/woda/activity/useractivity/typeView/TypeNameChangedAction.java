package com.kamjer.woda.activity.useractivity.typeView;

import com.kamjer.woda.model.Type;

@FunctionalInterface
public interface TypeNameChangedAction {

    void action(Type type, CharSequence newName);
}
