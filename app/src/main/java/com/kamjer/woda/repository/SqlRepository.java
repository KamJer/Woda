package com.kamjer.woda.repository;

import android.content.Context;

import com.kamjer.woda.R;
import com.kamjer.woda.model.Water;

public class SqlRepository {

    private static SqlRepository sqlRepository;

    private String sqlDropTable;
    private String sqlCreateTempTable;
    private String sqlDeleteFromWater;
    private String sqlInsertIntoWaterFromTemp;
    private String sqlInsertIntoWaterValues;
    private String sqlDeleteTypeWhereId;

    public static SqlRepository getInstance() {
        if (sqlRepository == null) {
            sqlRepository = new SqlRepository();
        }
        return sqlRepository;
    }

    public void loadSqlQuery(Context applicationContext) {
        sqlDropTable = applicationContext.getString(R.string.sql_drop_table);
        sqlCreateTempTable = applicationContext.getString(R.string.sql_create_temp_table);
        sqlDeleteFromWater = applicationContext.getString(R.string.sql_delete_from_water);
        sqlInsertIntoWaterFromTemp = applicationContext.getString(R.string.sql_insert_into_water_from_temp);
        sqlDeleteTypeWhereId = applicationContext.getString(R.string.sql_delete_type_where_id);
        sqlInsertIntoWaterValues = applicationContext.getString(R.string.sql_insert_into_water_values);
    }

    public String getSqlDropTable() {
        return sqlDropTable;
    }

    public String getSqlCreateTempTable() {
        return sqlCreateTempTable;
    }

    public String getSqlDeleteFromWater() {
        return sqlDeleteFromWater;
    }

    public String getSqlInsertIntoWaterFromTemp() {
        return sqlInsertIntoWaterFromTemp;
    }

    public String getSqlDeleteTypeWhereId(long id) {
        return sqlDeleteTypeWhereId = sqlDeleteTypeWhereId.replace("?", String.valueOf(id));
    }

    public String getSqlInsertIntoWaterValues(Water water) {
        return sqlInsertIntoWaterValues = sqlInsertIntoWaterValues.replace("?", water.toString());
    }
}
