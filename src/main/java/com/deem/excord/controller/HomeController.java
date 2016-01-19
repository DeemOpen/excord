package com.deem.excord.controller;

import com.deem.excord.domain.EcRequirement;
import com.deem.excord.repository.RequirementRepository;
import com.deem.excord.repository.TestCaseRepository;
import com.deem.excord.repository.TestPlanRepository;
import com.deem.excord.repository.TestResultRepository;
import com.deem.excord.util.BizUtil;
import com.deem.excord.util.FlashMsgUtil;
import java.util.ArrayList;
import java.util.List;
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

        EcRequirement currReq = rDao.findByParentIdIsNull();
        List<Long> listOfAllChildReqLst = BizUtil.INSTANCE.getListOfAllChildReq(currReq);
        if (listOfAllChildReqLst == null) {
            listOfAllChildReqLst = new ArrayList<>();
        }
        if (currReq.getCoverage()) {
            listOfAllChildReqLst.add(currReq.getId());
        }
        List<EcRequirement> reqMissingCoverageLst = rDao.findAllMissingCoverage();
        Integer missCnt = 0;
        for (EcRequirement coverageReq : reqMissingCoverageLst) {
            if (listOfAllChildReqLst.contains(coverageReq.getId())) {
                missCnt++;
            }
        }
        Integer totalReqCnt = listOfAllChildReqLst.size();
        Long coveragePercentage = Math.round(((totalReqCnt - missCnt) * 100.0) / totalReqCnt);

        Integer automationCnt = tcDao.automationCnt();
        Integer testcaseCnt = tcDao.getCountOfActiveTestcases();
        Long automationPercentage = Math.round((automationCnt * 100.0) / testcaseCnt);

        model.addAttribute("tcCnt", testcaseCnt);
        model.addAttribute("tpCnt", tpDao.getCountOfActiveTestplan());
        model.addAttribute("rCnt", rDao.getCountOfActiveRequirements());
        model.addAttribute("trCnt", trDao.getCountOfExecutionByYear());
        model.addAttribute("coveragePercentage", coveragePercentage);
        model.addAttribute("automationPercentage", automationPercentage);
        return "home";
    }

}
