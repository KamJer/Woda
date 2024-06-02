package com.kamjer.woda;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.res.Resources;

import com.kamjer.woda.model.Type;
import com.kamjer.woda.repository.ResourcesRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

public class ResourcesRepositoryUnitTest {
    @Mock
    private Context mockContext;
    @Mock
    private Resources mockResources;

    private ResourcesRepository resourcesRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mockContext.getResources()).thenReturn(mockResources);
        when(mockResources.getString(R.string.delete_type_default_message_exception)).thenReturn("Default delete message");

        resourcesRepository = new ResourcesRepository();
    }

    @Test
    public void testLoadDefaultTypes() {
        String[] defaultTypesText = {"Water", "Juice", "Soda"};
        int[] defaultTypesColor = {0xFF0000, 0x00FF00, 0x0000FF};

        when(mockResources.getStringArray(R.array.default_types)).thenReturn(defaultTypesText);
        when(mockResources.getIntArray(R.array.types_default_color)).thenReturn(defaultTypesColor);

        resourcesRepository.loadDefaultTypes(mockContext);

        List<Type> defaultTypes = resourcesRepository.getDefaultDrinksTypes();

        assertEquals(3, defaultTypes.size());
        assertEquals("Water", defaultTypes.get(0).getType());
        assertEquals(0xFF0000, defaultTypes.get(0).getColor().intValue());
        assertEquals("Juice", defaultTypes.get(1).getType());
        assertEquals(0x00FF00, defaultTypes.get(1).getColor().intValue());
        assertEquals("Soda", defaultTypes.get(2).getType());
        assertEquals(0x0000FF, defaultTypes.get(2).getColor().intValue());
    }

    @Test
    public void testLoadDefaultTypeDeleteMessage() {
        resourcesRepository.loadDefaultTypeDeleteMessage(mockContext);

        String deleteMessage = resourcesRepository.getDeleteTypeDefaultMessageException();

        assertEquals("Default delete message", deleteMessage);
    }

    @Test
    public void testGetDefaultDrinksTypesWhenNotLoaded() {
        List<Type> defaultTypes = resourcesRepository.getDefaultDrinksTypes();

        assertTrue(defaultTypes.isEmpty());
    }

    @Test
    public void testGetDefaultDrinksTypesWhenLoaded() {
        String[] defaultTypesText = {"Water", "Juice"};
        int[] defaultTypesColor = {0xFF0000, 0x00FF00};

        when(mockResources.getStringArray(R.array.default_types)).thenReturn(defaultTypesText);
        when(mockResources.getIntArray(R.array.types_default_color)).thenReturn(defaultTypesColor);

        resourcesRepository.loadDefaultTypes(mockContext);

        List<Type> defaultTypes = resourcesRepository.getDefaultDrinksTypes();

        assertEquals(2, defaultTypes.size());
        assertEquals("Water", defaultTypes.get(0).getType());
        assertEquals(0xFF0000, defaultTypes.get(0).getColor().intValue());
        assertEquals("Juice", defaultTypes.get(1).getType());
        assertEquals(0x00FF00, defaultTypes.get(1).getColor().intValue());
    }

    @Test
    public void testGetDeleteTypeDefaultMessageExceptionWhenNotLoaded() {
        String deleteMessage = resourcesRepository.getDeleteTypeDefaultMessageException();

        assertNull(deleteMessage);
    }
}
