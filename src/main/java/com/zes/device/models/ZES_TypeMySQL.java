package com.zes.device.models;

import com.zes.device.ZES_SQLGenerator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public abstract class ZES_TypeMySQL extends ZES_Type
{
    protected String ZES_gv_logTableName;

    public ZES_TypeMySQL(long timestamp, byte[] bytes, String ictNumber)
    {
        super(timestamp, bytes, ictNumber);
    }

    protected void ZES_initLogTableName(String type)
    {
        ZES_gv_logTableName = "pms_real_data_" + type + "_log";
    }

    protected void ZES_addInsertLogQuery(List<String> ZES_lv_queries)
    {
        if(ZES_gv_hasAnyNewValue == null || ZES_gv_hasAnyNewValue)
        {
            String ZES_lv_insertLogQuery = ZES_SQLGenerator.getInsertQuery(ZES_getDataMap(), ZES_gv_ictNumber, ZES_gv_logTableName, ZES_gv_timestamp);
            ZES_lv_queries.add(ZES_lv_insertLogQuery);
            ZES_gv_hasAnyNewValue = null;
        }
    }

    @Override
    protected void ZES_parseData(ZES_Data data, ResultSet resultSet) throws SQLException
    {
        switch (data.ZES_gv_dataType)
        {
            case "long":
                data.setValue(ZES_getLong(ZES_gv_bytes, data.ZES_gv_offset, data.ZES_gv_size));
                if (ZES_gv_hasPrevData)
                {
                    data.setPrevValue(resultSet.getLong(data.ZES_gv_key));
                }
                break;
            case "double":
                data.setValue(ZES_getDouble(ZES_gv_bytes, data.ZES_gv_offset, data.ZES_gv_delimit_size));
                if (ZES_gv_hasPrevData)
                {
                    data.setPrevValue(resultSet.getDouble(data.ZES_gv_key));
                }
                break;
            case "time":
                data.setValue(ZES_getTime(ZES_gv_bytes, data.ZES_gv_offset));
                if (ZES_gv_hasPrevData)
                {
                    data.setPrevValue(resultSet.getString(data.ZES_gv_key));
                }
                break;
        }
    }

    protected abstract ZES_Data[] ZES_getDataMap();
}
