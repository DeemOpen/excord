package com.deem.excord.controller;

import com.deem.excord.domain.EcTestcase;
import com.deem.excord.domain.EcTestplan;
import com.deem.excord.domain.EcTestplanTestcaseMapping;
import com.deem.excord.domain.EcTestresult;
import com.deem.excord.domain.EcUser;
import com.deem.excord.repository.TestCaseRepository;
import com.deem.excord.repository.TestPlanRepository;
import com.deem.excord.repository.TestPlanTestCaseRepository;
import com.deem.excord.repository.TestResultRepository;
import com.deem.excord.repository.UserRepository;
import com.deem.excord.util.BizUtil;
import com.deem.excord.util.FlashMsgUtil;
import com.deem.excord.util.HistoryUtil;
import com.deem.excord.vo.TestPlanMetricVo;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TestPlanController {

    private static final Logger logger = LoggerFactory.getLogger(TestPlanController.class);

    @Autowired
    TestPlanRepository tpDao;

    @Autowired
    TestCaseRepository tcDao;

    @Autowired
    TestPlanTestCaseRepository tptcDao;

    @Autowired
    TestResultRepository trDao;

    @Autowired
    UserRepository uDao;

    @Autowired
    HistoryUtil historyUtil;

    @RequestMapping(value = {"/"}, method = RequestMethod.GET)
    public String home(HttpSession session, Model model) {

        FlashMsgUtil.INSTANCE.checkFlashMsg(session, model);
        List<EcTestplan> testPlanLst = null;
        testPlanLst = tpDao.findByEnabledOrderByIdDesc(true);
        List<Object[]> metricsLst = tpDao.findMetrics();
        List<TestPlanMetricVo> testPlanMetricsLst = BizUtil.INSTANCE.gererateAllTestPlanMetrics(testPlanLst, metricsLst);
        model.addAttribute("home", "yes");
        model.addAttribute("testPlanMetricsLst", testPlanMetricsLst);
        model.addAttribute("testPlanLst", testPlanLst);
        return "testplan";
    }

    @RequestMapping(value = {"/testplan"}, method = RequestMethod.GET)
    public String testplanHome(HttpSession session, Model model) {

        FlashMsgUtil.INSTANCE.checkFlashMsg(session, model);
        List<EcTestplan> testPlanLst = null;
        testPlanLst = tpDao.findAllByOrderByIdDesc();
        List<Object[]> metricsLst = tpDao.findMetrics();
        List<TestPlanMetricVo> testPlanMetricsLst = BizUtil.INSTANCE.gererateAllTestPlanMetrics(testPlanLst, metricsLst);
        model.addAttribute("testPlanMetricsLst", testPlanMetricsLst);
        model.addAttribute("testPlanLst", testPlanLst);
        return "testplan";
    }

    @RequestMapping(value = "/testplan_add", method = RequestMethod.GET)
    public String addTestplan(Model model) {
        Iterable<EcUser> activeUsersLst = uDao.findByEnabledOrderByUsernameAsc(Boolean.TRUE);
        model.addAttribute("activeUsersLst", activeUsersLst);
        return "testplan_form";
    }

    @RequestMapping(value = "/testplan_clone", method = RequestMethod.GET)
    public String cloneTestplan(HttpSession session, HttpServletRequest request, Model model, @RequestParam(value = "testplanId", required = true) Long testplanId) {

        EcTestplan tp = tpDao.findOne(testplanId);
        EcTestplan newTp = new EcTestplan();
        newTp.setName(tp.getName() + "_CLONE");
        newTp.setCreator((String) session.getAttribute("authName"));
        newTp.setEnabled(tp.getEnabled());
        newTp.setEndDate(tp.getEndDate());
        newTp.setStartDate(tp.getStartDate());
        newTp.setReleaseName(tp.getReleaseName());
        newTp.setSchedule(tp.getSchedule());
        newTp.setLeader(tp.getLeader());
        tpDao.save(newTp);
        //Find all testcases associated with the testplan and copy.
        List<EcTestcase> tcLst = tcDao.findAllTestCasesByTestPlanId(testplanId);
        for (EcTestcase tc : tcLst) {
            EcTestplanTestcaseMapping tptcObj = new EcTestplanTestcaseMapping();
            tptcObj.setTestcaseId(tc);
            tptcObj.setTestplanId(newTp);
            tptcDao.save(tptcObj);

        }
        historyUtil.addHistory("Clone testplan: " + tp.getName(), session, request.getRemoteAddr());
        session.setAttribute("flashMsg", "Successfully Cloned TestPlan " + tp.getName());

        return "redirect:/";
    }

    @RequestMapping(value = "/testplan_history", method = RequestMethod.GET)
    public String historyTestPlan(HttpSession session, HttpServletRequest request, Model model, @RequestParam(value = "testplanId", required = true) Long testplanId) {

        List<EcTestresult> allTestRunsLst = trDao.findAllTestResultsByTestplanId(testplanId);
        model.addAttribute("allTestRunsLst", allTestRunsLst);
        return "testplan_history";
    }

    @RequestMapping(value = "/testplan_edit", method = RequestMethod.GET)
    public String editTestplan(Model model, @RequestParam(value = "testplanId", required = true) Long testplanId) {

        EcTestplan testPlan = tpDao.findOne(testplanId);
        Iterable<EcUser> activeUsersLst = uDao.findByEnabledOrderByUsernameAsc(Boolean.TRUE);
        model.addAttribute("activeUsersLst", activeUsersLst);
        model.addAttribute("testPlan", testPlan);
        return "testplan_form";
    }

    @RequestMapping(value = "/testplan_delete", method = RequestMethod.GET)
    public String deleteTestplan(HttpSession session, HttpServletRequest request, Model model, @RequestParam(value = "testplanId", required = true) Long testplanId) {

        EcTestplan testPlan = tpDao.findOne(testplanId);
        tpDao.delete(testPlan);
        historyUtil.addHistory("Deleted testplan: " + testPlan.getName(), session, request.getRemoteAddr());
        return "redirect:/";
    }

    @RequestMapping(value = "/testplan_run", method = RequestMethod.GET)
    public String runTestplan(HttpSession session, Model model, @RequestParam(value = "testplanId", required = true) Long testplanId) {

        EcTestplan testPlan = tpDao.findOne(testplanId);
        //Get all test cases associated with this test plan
        List<EcTestcase> testCaseLst = tcDao.findAllTestCasesByTestPlanId(testplanId);
        if (testCaseLst == null) {
            testCaseLst = new ArrayList<EcTestcase>();
        }
        Map<String, EcTestresult> trMap = new HashMap<String, EcTestresult>();
        for (EcTestcase tc : testCaseLst) {
            EcTestresult testresult = trDao.findLatestTestResultsByTestplanIdAndTestcaseId(testplanId, tc.getId());
            if (testresult != null) {
                trMap.put(tc.getId().toString(), testresult);
            }
        }
        List<EcTestplanTestcaseMapping> tptcLst = tptcDao.findByTestplanId(testPlan);
        model.addAttribute("tptcLst", tptcLst);
        model.addAttribute("testCaseLst", testCaseLst);
        model.addAttribute("trMap", trMap);
        model.addAttribute("testPlan", testPlan);
        return "testplan_run";
    }

    @RequestMapping(value = "/testplan_view", method = RequestMethod.GET)
    public String viewTestPlan(Model model, HttpSession session, @RequestParam(value = "testplanId", required = true) Long testplanId) {

        FlashMsgUtil.INSTANCE.checkFlashMsg(session, model);
        EcTestplan testPlan = tpDao.findOne(testplanId);
        List<EcTestplanTestcaseMapping> tptcLst = tptcDao.findByTestplanId(testPlan);
        List<Object[]> tmLst = tpDao.findMetricsByTestplanId(testplanId);
        List<TestPlanMetricVo> testPlanMetricLst = BizUtil.INSTANCE.flattenTestPlanMetrics(tmLst);
        Integer totalPassCount = 0;
        Integer totalCount = 0;
        Integer totalNotRunCount = 0;
        for (TestPlanMetricVo tpm : testPlanMetricLst) {
            totalPassCount = totalPassCount + tpm.getPassCount();
            totalCount = totalCount + tpm.getTotal();
            totalNotRunCount = totalNotRunCount + tpm.getNotrunCount();
        }
        //Get all test cases associated with this test plan
        List<EcTestcase> testCaseLst = tcDao.findAllTestCasesByTestPlanId(testplanId);
        if (testCaseLst == null) {
            testCaseLst = new ArrayList<EcTestcase>();
        }
        List<EcUser> activeUsersLst = uDao.findByEnabledOrderByUsernameAsc(Boolean.TRUE);
        model.addAttribute("activeUsersLst", activeUsersLst);
        model.addAttribute("testPlanMetricLst", testPlanMetricLst);
        model.addAttribute("testCaseLst", testCaseLst);
        model.addAttribute("testPlan", testPlan);
        model.addAttribute("tecaseCnt", totalCount);
        model.addAttribute("tptcLst", tptcLst);
        model.addAttribute("testPlanPassRate", Math.round((totalPassCount * 100.0) / totalCount));
        model.addAttribute("testPlanProgressRate", Math.round(((totalCount - totalNotRunCount) * 100.0) / totalCount));
        return "testplan_view";
    }

    @RequestMapping(value = "/testplan_save", method = RequestMethod.POST)
    public String saveTestPlan(HttpSession session, Model model, HttpServletRequest request,
            @RequestParam(value = "tid") String tid,
            @RequestParam(value = "tname") String tname,
            @RequestParam(value = "tcreator") String tcreator,
            @RequestParam(required = false, defaultValue = "false", value = "tenabled") Boolean tenabled,
            @RequestParam(value = "tstartdt") @DateTimeFormat(pattern = "MM-dd-yyyy") Date tstartdt,
            @RequestParam(value = "tenddt") @DateTimeFormat(pattern = "MM-dd-yyyy") Date tenddt,
            @RequestParam(value = "tleader") String tleader,
            @RequestParam(value = "trelease") String trelease,
            @RequestParam(value = "tschedule") String tschedule) {
        try {
            EcTestplan tp;
            if (!tid.isEmpty()) {
                tp = tpDao.findOne(Long.parseLong(tid));
            } else {
                tp = new EcTestplan();
            }
            tp.setName(tname);
            tp.setCreator(tcreator);
            tp.setEnabled(tenabled);
            tp.setStartDate(tstartdt);
            tp.setEndDate(tenddt);
            tp.setLeader(tleader);
            tp.setReleaseName(trelease);
            tp.setSchedule(tschedule);
            tpDao.save(tp);
            if (!tid.equals("")) {
                historyUtil.addHistory("Updated testplan: " + tname, session, request.getRemoteAddr());
                session.setAttribute("flashMsg", "Successfully Updated TestPlan " + tp.getName());
            } else {
                historyUtil.addHistory("Added testplan: " + tname, session, request.getRemoteAddr());
                session.setAttribute("flashMsg", "Successfully Added TestPlan " + tp.getName());
            }
            return "redirect:/";
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        return "redirect:/error";
    }

    @RequestMapping(value = "/testplan_run", method = RequestMethod.POST)
    public String executeTestplan(HttpSession session, Model model,
            HttpServletRequest request,
            @RequestParam(value = "status", required = true) String status,
            @RequestParam(value = "testplanId", required = true) Long testplanId,
            @RequestParam(value = "testcaseChk") List<Long> testcaseChkLst,
            @RequestParam(value = "testEnvironment", required = true) String testEnvironment) {

        for (Long testCaseId : testcaseChkLst) {
            EcTestplanTestcaseMapping tptcLinkId = tptcDao.findByTestplanIdAndTestcaseId(tpDao.findOne(testplanId), tcDao.findOne(testCaseId));
            //Remove latest flag from all old runs.
            trDao.updateAllTestRunsLatestFlag(tptcLinkId.getId());
            EcTestresult tr = new EcTestresult();
            tr.setLatest(true);
            tr.setNote(request.getParameter("tcomments_" + testCaseId));
            tr.setStatus(status);
            tr.setTimestamp(new Date());
            tr.setTester((String) session.getAttribute("authName"));
            tr.setEnvironment(testEnvironment);
            tr.setBugTicket(request.getParameter("tbug_" + testCaseId));
            tr.setTestplanTestcaseLinkId(tptcLinkId);
            trDao.save(tr);
        }
        return "redirect:/testplan_run?testplanId=" + testplanId;
    }

    @RequestMapping(value = "/testcase_testplan_map_remove", method = RequestMethod.POST)
    public String testCaseTestPlanMapRemove(Model model, HttpServletRequest request, HttpSession session, @RequestParam(value = "testPlanId", required = true) Long testPlanId, @RequestParam(value = "testcaseChk") List<Long> testcaseChkLst) {

        for (Long testCaseId : testcaseChkLst) {
            EcTestcase tc = tcDao.findOne(testCaseId);
            EcTestplan tp = tpDao.findOne(testPlanId);
            EcTestplanTestcaseMapping tptcMap = tptcDao.findByTestplanIdAndTestcaseId(tp, tc);
            historyUtil.addHistory("UnLinked TestPlan : [" + tp.getName() + "] with TestCase: [" + tc.getName() + "] ", session, request.getRemoteAddr());
            tptcDao.delete(tptcMap);
        }
        session.setAttribute("flashMsg", "Successfully Unlinked!");
        return "redirect:/testplan_view?testplanId=" + testPlanId;
    }

    @RequestMapping(value = "/testplan_testcase_assign", method = RequestMethod.POST)
    public String testCaseTestPlanAssign(Model model, HttpServletRequest request, HttpSession session,
            @RequestParam(value = "testPlanId", required = true) Long testPlanId,
            @RequestParam(value = "testcaseAssignedTo", required = true) String testcaseAssignedTo,
            @RequestParam(value = "testcaseChk") List<Long> testcaseChkLst) {

        for (Long testCaseId : testcaseChkLst) {
            EcTestcase tc = tcDao.findOne(testCaseId);
            EcTestplan tp = tpDao.findOne(testPlanId);
            EcTestplanTestcaseMapping tptcMap = tptcDao.findByTestplanIdAndTestcaseId(tp, tc);
            tptcMap.setAssignedTo(testcaseAssignedTo);
            tptcDao.save(tptcMap);
            logger.info("Assigned testcase: {} to user:{}", tc.getName(), testcaseAssignedTo);

        }
        session.setAttribute("flashMsg", "Successfully Assigned Testcases!");
        return "redirect:/testplan_view?testplanId=" + testPlanId;
    }

}
