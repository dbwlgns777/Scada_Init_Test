package com.zes.device;

import com.zes.device.models.ZES_TypeInfluxDB;

import java.util.concurrent.BlockingQueue;

import static com.zes.device.ZES_DeviceApplication.ZES_gv_logger;

public class ZES_Consumer implements Runnable
{
    private final BlockingQueue<ZES_TypeInfluxDB> sharedQueue;
    private int threadNo;
    public ZES_Consumer(BlockingQueue<ZES_TypeInfluxDB> sharedQueue, int threadNo)
    {
        this.sharedQueue = sharedQueue;
        this.threadNo = threadNo;
    }

    @Override
    public void run()
    {
        while (!Thread.currentThread().isInterrupted())
        {
            try
            {
//              ZES_gv_logger.info("thread" + threadNo + " out queue : " + sharedQueue.size());
                ZES_TypeInfluxDB ZES_lv_typeBase = sharedQueue.take(); // Retrieve bytes from the corresponding blocking queue
                ZES_lv_typeBase.ZES_saveRealTime().ZES_saveLog();
            }
            catch (InterruptedException e)
            {
                ZES_gv_logger.info("Consumer thread " + threadNo + " interrupted, shutting down");
                Thread.currentThread().interrupt(); // Restore interrupted status
                break;
            }
            catch (Exception e)
            {
                ZES_gv_logger.severe("Unexpected error in Consumer thread " + threadNo + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
