package com.zes.device.config;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.domain.BucketRetentionRules;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ZES_InfluxdbConfig
{
    private static InfluxDBClient ZES_gv_InfluxDBClient;
    public static String ZES_gv_org;
    public static String ZES_gv_orgId;

    static
    {
        Properties ZES_lv_properties = new Properties();
        try
        {
            FileInputStream fis = new FileInputStream("config/influxdb.properties");
            ZES_lv_properties.load(fis);
            String ZES_lv_url = ZES_lv_properties.getProperty("influxdb.url");
            String ZES_lv_token = ZES_lv_properties.getProperty("influxdb.token");
            ZES_gv_org = ZES_lv_properties.getProperty("influxdb.org");
            ZES_gv_orgId = ZES_lv_properties.getProperty("influxdb.orgId");
            String ZES_lv_bucket = ZES_lv_properties.getProperty("influxdb.bucket");
            ZES_gv_InfluxDBClient = InfluxDBClientFactory.create(ZES_lv_url, ZES_lv_token.toCharArray(), ZES_gv_org, ZES_lv_bucket);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    private ZES_InfluxdbConfig() {}
    public static InfluxDBClient ZES_getInfluxDBClient()
    {
        return ZES_gv_InfluxDBClient;
    }

    public static void ZES_makeNewBucket(String bucket)
    {
        BucketRetentionRules ZES_lv_retention = new BucketRetentionRules();
        ZES_lv_retention.setEverySeconds(0); //0 is infinite
        ZES_getInfluxDBClient().getBucketsApi().createBucket(bucket, ZES_lv_retention, ZES_gv_orgId);
    }
}
