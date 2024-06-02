package com.kamjer.woda.database;

import com.kamjer.woda.model.Type;
import com.kamjer.woda.repository.ResourcesRepository;

public class WaterDataValidation {

    public static boolean validateTypeToRemove(Type type) {
        return ResourcesRepository.getInstance().getDefaultDrinksTypes().contains(type);
    }
}
