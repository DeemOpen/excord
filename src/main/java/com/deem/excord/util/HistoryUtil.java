package com.deem.excord.util;

import com.deem.excord.domain.EcHistory;
import com.deem.excord.repository.HistoryRepository;
import java.util.Date;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HistoryUtil {

    private static final Logger logger = LoggerFactory.getLogger(HistoryUtil.class);

    @Autowired
    HistoryRepository hDao;

    public void addHistory(String logMsg, HttpSession session, String ip) {
        String user = (String) session.getAttribute("authName");
        logger.info("{} by {} from {}", logMsg, user, ip);
        EcHistory history = new EcHistory();
        history.setChangeDate(new Date());
        history.setChangeUser(user);
        history.setChangeSummary(logMsg);
        history.setChangeIp(ip);
        hDao.save(history);

    }

}
