package com.zes.device.models;

import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.exceptions.NotFoundException;
import com.zes.device.config.ZES_InfluxdbConfig;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.zes.device.ZES_SQLGenerator.convertTimestampToDateFormat;
import static com.zes.device.config.ZES_InfluxdbConfig.ZES_gv_org;

public abstract class ZES_TypeInfluxDB extends ZES_Type
{
    protected Point ZES_gv_point;
    private final String ZES_gv_bucket;
    private final String ZES_gv_measurement;
    private static final int MAX_RETRY_ATTEMPTS = 3;
    public ZES_TypeInfluxDB(long timestamp, byte[] bytes, String ictNumber)
    {
        super(timestamp, bytes, ictNumber);
        ZES_gv_bucket = convertTimestampToDateFormat(timestamp, "yyyy");
        ZES_gv_measurement =  convertTimestampToDateFormat(timestamp, "yyyyMM");
    }

    protected void ZES_initPoint(String type)
    {
        ZES_gv_point = new Point(ZES_gv_measurement)
                .addTag("ict_number", ZES_gv_ictNumber)
                .addTag("type", type)
                .time(ZES_gv_timestamp, WritePrecision.MS);
    }

    @Override
    public void ZES_saveLog()
    {
        if(ZES_gv_hasAnyNewValue == null || ZES_gv_hasAnyNewValue)
        {
            ZES_gv_hasAnyNewValue = null;
            int ZES_lv_retryCount = 0;
            boolean ZES_lv_savedSuccessfully = false;

            while((ZES_lv_retryCount < MAX_RETRY_ATTEMPTS) && !ZES_lv_savedSuccessfully)
            {
                try
                {
                    ZES_InfluxdbConfig.ZES_getInfluxDBClient().getWriteApiBlocking().writePoint(ZES_gv_bucket, ZES_gv_org, ZES_gv_point);
                    ZES_lv_savedSuccessfully = true;
                }
                catch (NotFoundException nfe)
                {
                    if(nfe.getMessage().contains("bucket \"" + ZES_gv_bucket + "\" not found"))
                    {
                        ZES_InfluxdbConfig.ZES_makeNewBucket(ZES_gv_bucket);
                    }
                    ZES_lv_retryCount++;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    @Override
    protected void ZES_parseData(ZES_Data data, ResultSet resultSet) throws SQLException
    {
        switch (data.ZES_gv_dataType)
        {
            case "long":
                data.setValue(ZES_getLong(ZES_gv_bytes, data.ZES_gv_offset, data.ZES_gv_size));
                ZES_gv_point.addField(data.ZES_gv_key, (long) data.ZES_gv_value);
                if (ZES_gv_hasPrevData)
                {
                    data.setPrevValue(resultSet.getLong(data.ZES_gv_key));
                }
                break;
            case "double":
                data.setValue(ZES_getDouble(ZES_gv_bytes, data.ZES_gv_offset, data.ZES_gv_delimit_size));
                ZES_gv_point.addField(data.ZES_gv_key, (double) data.ZES_gv_value);
                if (ZES_gv_hasPrevData)
                {
                    data.setPrevValue(resultSet.getDouble(data.ZES_gv_key));
                }
                break;
            case "time":
                data.setValue(ZES_getTime(ZES_gv_bytes, data.ZES_gv_offset));
                ZES_gv_point.addField(data.ZES_gv_key, data.ZES_gv_value.toString());
                if (ZES_gv_hasPrevData)
                {
                    data.setPrevValue(resultSet.getString(data.ZES_gv_key));
                }
                break;
        }
    }
}
