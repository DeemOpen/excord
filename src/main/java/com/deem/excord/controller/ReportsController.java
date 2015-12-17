package com.deem.excord.controller;

import com.deem.excord.repository.TestPlanRepository;
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

    @RequestMapping(value = "/reports", method = RequestMethod.GET)
    public String reportsHome(Model model) {
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
