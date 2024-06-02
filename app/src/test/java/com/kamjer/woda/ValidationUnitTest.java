package com.kamjer.woda;

import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.Assert.*;

import android.graphics.Color;

import com.kamjer.woda.database.WaterDataValidation;
import com.kamjer.woda.model.Type;
import com.kamjer.woda.repository.ResourcesRepository;

import java.util.ArrayList;
import java.util.List;

public class ValidationUnitTest {

    @Test
    public void typeValidationTypeToRemove_isTrue() {
//      mocking repository
        ResourcesRepository resourcesRepository = Mockito.mock(ResourcesRepository.class);
//      default types for mock
        List<Type> defaultTypes = new ArrayList<>();
        Type defaultType = new Type();
        defaultType.setId(1);
        defaultType.setType("Test");
        defaultType.setColor(Color.RED);
        defaultTypes.add(defaultType);

        try (MockedStatic<ResourcesRepository> resourcesRepositoryMockedStatic = Mockito.mockStatic(ResourcesRepository.class)) {
            resourcesRepositoryMockedStatic.when(ResourcesRepository::getInstance).thenReturn(resourcesRepository);
            Mockito.when(resourcesRepository.getDefaultDrinksTypes()).thenReturn(defaultTypes);

            Type typeToTest = new Type();
            typeToTest.setId(1);
            typeToTest.setType("Test");
            typeToTest.setColor(Color.RED);

            assertTrue(WaterDataValidation.validateTypeToRemove(typeToTest));
        }
    }

    @Test
    public void typeValidationTypeToRemove_isFalse() {
        ResourcesRepository resourcesRepository = Mockito.mock(ResourcesRepository.class);

        List<Type> defaultTypes = new ArrayList<>();
        Type defaultType = new Type();
        defaultType.setId(1);
        defaultType.setType("Test");
        defaultType.setColor(Color.RED);
        defaultTypes.add(defaultType);

        try (MockedStatic<ResourcesRepository> resourcesRepositoryMockedStatic = Mockito.mockStatic(ResourcesRepository.class)) {
            resourcesRepositoryMockedStatic.when(ResourcesRepository::getInstance).thenReturn(resourcesRepository);
            Mockito.when(resourcesRepository.getDefaultDrinksTypes()).thenReturn(defaultTypes);

            Type typeToTest = new Type();
            typeToTest.setId(0);
            typeToTest.setType("");
            typeToTest.setColor(Color.BLACK);

            assertFalse(WaterDataValidation.validateTypeToRemove(typeToTest));
        }
    }
}