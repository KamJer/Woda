package com.kamjer.woda.database;

import com.kamjer.woda.model.Type;
import com.kamjer.woda.repository.ResourcesRepository;

public class WaterDataValidation {

    /**
     * Validates if name of a type is proper
     * @param type to validate
     * @return true if name of a type is not empty
     */
    public static boolean validateTypeName(Type type){
        return !type.getType().isEmpty();
    }

    public static boolean validateTypeToRemove(Type type) {
        return ResourcesRepository.getInstance().getDefaultDrinksTypes().contains(type);
    }
}
