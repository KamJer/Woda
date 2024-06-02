package com.kamjer.woda;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import android.content.Context;

import com.kamjer.woda.model.Water;
import com.kamjer.woda.repository.SqlRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

public class SqlRepositoryUnitTest {

    @Mock
    private Context mockContext;

    private SqlRepository sqlRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        sqlRepository = SqlRepository.getInstance();
    }

    @Test
    public void testSingletonInstance() {
        SqlRepository instance1 = SqlRepository.getInstance();
        SqlRepository instance2 = SqlRepository.getInstance();
        assertNotNull(instance1);
        assertEquals(instance1, instance2);
    }

    @Test
    public void testLoadSqlQuery() {
        when(mockContext.getString(R.string.sql_drop_table)).thenReturn("DROP TABLE IF EXISTS temp_table;");
        when(mockContext.getString(R.string.sql_create_temp_table)).thenReturn("CREATE TEMP TABLE temp_table AS SELECT * FROM water;");
        when(mockContext.getString(R.string.sql_delete_from_water)).thenReturn("DELETE FROM water;");
        when(mockContext.getString(R.string.sql_insert_into_water_from_temp)).thenReturn("INSERT INTO water SELECT * FROM temp_table;");
        when(mockContext.getString(R.string.sql_delete_type_where_id)).thenReturn("DELETE FROM type WHERE id=?;");
        when(mockContext.getString(R.string.sql_insert_into_water_values)).thenReturn("INSERT INTO water VALUES (?);");

        sqlRepository.loadSqlQuery(mockContext);

        assertEquals("DROP TABLE IF EXISTS temp_table;", sqlRepository.getSqlDropTempTable());
        assertEquals("CREATE TEMP TABLE temp_table AS SELECT * FROM water;", sqlRepository.getSqlCreateTempTable());
        assertEquals("DELETE FROM water;", sqlRepository.getSqlDeleteFromWater());
        assertEquals("INSERT INTO water SELECT * FROM temp_table;", sqlRepository.getSqlInsertIntoWaterFromTemp());
        assertEquals("DELETE FROM type WHERE id=1;", sqlRepository.getSqlDeleteTypeWhereId(1L));
        Water water = new Water();
        water.setId(1);
        water.setWaterDayId(LocalDate.now());
        assertEquals("INSERT INTO water VALUES (1, null, 0, '" + LocalDate.now().toString() + "');", sqlRepository.getSqlInsertIntoWaterValues(water));
    }

    @Test
    public void testGetSqlDropTempTable() {
        when(mockContext.getString(R.string.sql_drop_table)).thenReturn("DROP TABLE IF EXISTS temp_table;");
        sqlRepository.loadSqlQuery(mockContext);
        assertEquals("DROP TABLE IF EXISTS temp_table;", sqlRepository.getSqlDropTempTable());
    }

    @Test
    public void testGetSqlCreateTempTable() {
        when(mockContext.getString(R.string.sql_create_temp_table)).thenReturn("CREATE TEMP TABLE temp_table AS SELECT * FROM water;");
        sqlRepository.loadSqlQuery(mockContext);
        assertEquals("CREATE TEMP TABLE temp_table AS SELECT * FROM water;", sqlRepository.getSqlCreateTempTable());
    }

    @Test
    public void testGetSqlDeleteFromWater() {
        when(mockContext.getString(R.string.sql_delete_from_water)).thenReturn("DELETE FROM water;");
        sqlRepository.loadSqlQuery(mockContext);
        assertEquals("DELETE FROM water;", sqlRepository.getSqlDeleteFromWater());
    }

    @Test
    public void testGetSqlInsertIntoWaterFromTemp() {
        when(mockContext.getString(R.string.sql_insert_into_water_from_temp)).thenReturn("INSERT INTO water SELECT * FROM temp_table;");
        sqlRepository.loadSqlQuery(mockContext);
        assertEquals("INSERT INTO water SELECT * FROM temp_table;", sqlRepository.getSqlInsertIntoWaterFromTemp());
    }

    @Test
    public void testGetSqlDeleteTypeWhereId() {
        when(mockContext.getString(R.string.sql_delete_type_where_id)).thenReturn("DELETE FROM type WHERE id=?;");
        sqlRepository.loadSqlQuery(mockContext);
        assertEquals("DELETE FROM type WHERE id=1;", sqlRepository.getSqlDeleteTypeWhereId(1L));
    }

    @Test
    public void testGetSqlInsertIntoWaterValues() {
        Water water = new Water();
        water.setId(1);
        water.setWaterDrank(1500);
        water.setTypeId(1);
        water.setWaterDayId(LocalDate.now());
        String dateString = LocalDate.now().toString();
        when(mockContext.getString(R.string.sql_insert_into_water_values)).thenReturn("INSERT INTO water VALUES (?);");
        sqlRepository.loadSqlQuery(mockContext);
        assertEquals("INSERT INTO water VALUES (1, 1500, 1, '" + dateString + "');", sqlRepository.getSqlInsertIntoWaterValues(water));
    }
}

