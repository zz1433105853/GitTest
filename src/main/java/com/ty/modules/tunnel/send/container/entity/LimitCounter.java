package com.ty.modules.tunnel.send.container.entity;

import com.ty.common.utils.StringUtils;
import org.apache.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Ysw on 2017/3/20.
 */
public class LimitCounter {
    private static Logger logger= Logger.getLogger(LimitCounter.class);
    private Timer timer = new Timer();
    private int sendLimit;
    private int counterNow;

    public LimitCounter(int sendLimit, String tdName) {
        int blockCount = 4;
        this.sendLimit = sendLimit/(blockCount+1);
        int period = 1000/blockCount;
        //init
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(counterNow != 0){

                    logger.debug(StringUtils.builderString("===========执行速度清零,当前通道:", tdName, "计数器：", String.valueOf(counterNow), "=============="));
                    modifyCounter(true, false);
                }
            }
       // },0 ,1000);
        },0 ,period);
    }

    public boolean isOverSpeed() {
          boolean result =true;
          if(counterNow>=sendLimit) {
              result = false;
          }
          return result;
    }

    public void doIncrement() {
        modifyCounter(false, true);
    }

    private synchronized void modifyCounter(boolean isReset,boolean isIncrement) {
        if(isReset) {
            this.counterNow = 0;
        }
        if(isIncrement) {
            this.counterNow++;
        }
    }
}
