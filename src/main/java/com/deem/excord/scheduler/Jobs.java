package com.deem.excord.scheduler;

import com.deem.excord.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Component
public class Jobs {

    private static final Logger logger = LoggerFactory.getLogger(Jobs.class);

    @Autowired
    UserRepository uDao;

    //Everyday at 6:00 AM
    @Scheduled(cron = "0 0 6 * * ?")
    public void dailyJob() {
        logger.info("Running daily job!");
        uDao.disableOldUsers();

    }
}
