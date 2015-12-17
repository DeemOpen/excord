package com.deem.excord.controller;

import com.deem.excord.repository.TestPlanRepository;
import com.deem.excord.repository.TestResultRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ReportsController {

    @Autowired
    TestPlanRepository tpDao;

    @Autowired
    TestResultRepository trDao;

    @RequestMapping(value = "/reports", method = RequestMethod.GET)
    public String reportsHome(Model model) {
        List<Object[]> topTesterMonthLst = trDao.findByTopTesterByEnvByMonth();
        List<Object[]> topTesterYearLst = trDao.findByTopTesterByEnvByYear();
        List<Object[]> mostExeTestsLst = trDao.mostexecutedManualTests();

        model.addAttribute("topTesterMonthLst", topTesterMonthLst);
        model.addAttribute("topTesterYearLst", topTesterYearLst);
        model.addAttribute("mostExeTestsLst", mostExeTestsLst);
        return "reports";
    }

    @RequestMapping(value = "/testplan_metric", method = RequestMethod.GET)
    public String testplanMetric(Model model, @RequestParam(value = "testplanId", required = true) Long testplanId) {
        List<Object[]> metricLst = tpDao.findByPriorityByTester(testplanId);
        model.addAttribute("metricLst", metricLst);
        return "testplan_metric";
    }

    @RequestMapping(value = "/help", method = RequestMethod.GET)
    public String help(Model model) {
        return "help";
    }

}
