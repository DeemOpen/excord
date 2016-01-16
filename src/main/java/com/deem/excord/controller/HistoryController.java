package com.deem.excord.controller;

import com.deem.excord.domain.EcHistory;
import com.deem.excord.repository.HistoryRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HistoryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HistoryController.class);

    @Autowired
    HistoryRepository hDao;

    @RequestMapping(value = "/history", method = RequestMethod.GET)
    public String history(Model model) {
        List<EcHistory> historyLst = hDao.findTopChanges();
        model.addAttribute("historyLst", historyLst);
        return "history";
    }

    @RequestMapping(value = "/history", method = RequestMethod.POST)
    public String searchHistory(Model model, @RequestParam(value = "searchKey", required = true) String searchKey) {
        LOGGER.info("Search key: {}", searchKey);
        List<EcHistory> historyLst = hDao.findByChangeSummaryLikeOrderByIdDesc("%" + searchKey + "%");
        model.addAttribute("searchKey", searchKey);
        model.addAttribute("historyLst", historyLst);
        return "history";
    }

}
