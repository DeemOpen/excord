package com.deem.excord.scheduler;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Component
public class Jobs {

    private static final Logger logger = LoggerFactory.getLogger(Jobs.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    //Every 90 mins
    //@Scheduled(cron = "*/90 * * * * *")
    public void periodicJobs() {
        logger.info("The time is now " + dateFormat.format(new Date()));
    }
}
