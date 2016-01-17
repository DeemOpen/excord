package com.deem.excord.controller;

import com.deem.excord.repository.RequirementRepository;
import com.deem.excord.repository.TestCaseRepository;
import com.deem.excord.repository.TestPlanRepository;
import com.deem.excord.repository.TestResultRepository;
import com.deem.excord.util.FlashMsgUtil;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    TestCaseRepository tcDao;

    @Autowired
    RequirementRepository rDao;

    @Autowired
    TestPlanRepository tpDao;

    @Autowired
    TestResultRepository trDao;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(Model model, HttpSession session) {
        FlashMsgUtil.INSTANCE.checkFlashMsg(session, model);
        model.addAttribute("tcCnt", tcDao.count());
        model.addAttribute("tpCnt", tpDao.count());
        model.addAttribute("rCnt", rDao.count());
        model.addAttribute("trCnt", trDao.count());
        return "home";
    }

}
