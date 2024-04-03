package com.kamjer.woda.database;

import com.kamjer.woda.model.Type;

public class WaterDataValidation {

    /**
     * Validates if name of a type is proper
     * @param type to validate
     * @return true if name of a type is not empty
     */
    public static boolean validateTypeName(Type type){
        return !type.getType().isEmpty();
    }
}
