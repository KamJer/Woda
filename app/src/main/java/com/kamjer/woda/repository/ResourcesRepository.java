package com.kamjer.woda.repository;

import android.content.Context;

import com.kamjer.woda.R;
import com.kamjer.woda.model.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ResourcesRepository {

    private static ResourcesRepository resourcesRepository;

    private String deleteTypeDefaultMessageException;

    private List<Type> defaultDrinksTypes;

    public static ResourcesRepository getInstance() {
        if (resourcesRepository == null) {
            resourcesRepository = new ResourcesRepository();
        }
        return resourcesRepository;
    }

    public void loadDefaultTypes(Context context) {
        String[] defaultDrinksTypesText = context.getResources().getStringArray(R.array.default_types);
        int[] defaultDrinksTypesColor = context.getResources().getIntArray(R.array.types_default_color);
        defaultDrinksTypes = new ArrayList<>();
        for (int i = 0; i < defaultDrinksTypesText.length; i++) {
            defaultDrinksTypes.add(new Type(defaultDrinksTypesText[i], defaultDrinksTypesColor[i]));
        }
    }

    public void loadDefaultTypeDeleteMessage(Context context) {
        deleteTypeDefaultMessageException = context.getResources().getString(R.string.delete_type_default_message_exception);
    }

    /**
     * Returns default types of water
     * @return default types loaded on start of an app, if there are no default types loaded, returns empty array of types
     */
    public List<Type> getDefaultDrinksTypes() {
        return Optional.ofNullable(defaultDrinksTypes).orElse(new ArrayList<>());
    }

    public String getDeleteTypeDefaultMessageException() {
        return deleteTypeDefaultMessageException;
    }
}
