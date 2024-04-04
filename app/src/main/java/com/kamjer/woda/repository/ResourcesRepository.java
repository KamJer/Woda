package com.kamjer.woda.repository;

import android.content.Context;

import com.kamjer.woda.R;
import com.kamjer.woda.model.Type;

import java.util.Optional;

public class ResourcesRepository {

    private static ResourcesRepository resourcesRepository;

    private Type[] defaultDrinksTypes;

    public static ResourcesRepository getInstance() {
        if (resourcesRepository == null) {
            resourcesRepository = new ResourcesRepository();
        }
        return resourcesRepository;
    }

    public void loadDefaultTypes(Context context) {
        String[] defaultDrinksTypesText = context.getResources().getStringArray(R.array.default_types);
        int[] defaultDrinksTypesColor = context.getResources().getIntArray(R.array.types_default_color);
        defaultDrinksTypes = new Type[defaultDrinksTypesText.length];
        for (int i = 0; i < defaultDrinksTypesText.length; i++) {
            defaultDrinksTypes[i] = new Type(defaultDrinksTypesText[i], defaultDrinksTypesColor[i]);
        }
    }

    /**
     * Returns default types of water
     * @return default types loaded on start of an app, if there are no default types loaded, returns empty array of types
     */
    public Type[] getDefaultDrinksTypes() {
        return Optional.ofNullable(defaultDrinksTypes).orElse(new Type[0]);
    }
}
