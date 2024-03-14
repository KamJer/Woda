package com.kamjer.woda.activity.useractivity.coloreditdialog;

import com.kamjer.woda.model.Type;

@FunctionalInterface
public interface ColorSelectorAction {
    void action(Type type, int position);
}
