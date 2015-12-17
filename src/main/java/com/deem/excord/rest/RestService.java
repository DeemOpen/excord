package com.deem.excord.rest;

import com.deem.excord.domain.EcTestcase;
import com.deem.excord.domain.EcTestplan;
import com.deem.excord.domain.EcTestplanTestcaseMapping;
import com.deem.excord.domain.EcTestresult;
import com.deem.excord.repository.TestCaseRepository;
import com.deem.excord.repository.TestPlanRepository;
import com.deem.excord.repository.TestPlanTestCaseRepository;
import com.deem.excord.repository.TestResultRepository;
import com.deem.excord.util.BizUtil;
import com.deem.excord.util.Constants;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestService {

    @Autowired
    TestPlanRepository tpDao;

    @Autowired
    TestCaseRepository tcDao;

    @Autowired
    TestResultRepository trDao;

    @Autowired
    TestPlanTestCaseRepository tptcDao;

    @Value("${test.env}")
    String testEnvArr;

    private static final Logger logger = LoggerFactory.getLogger(RestService.class);

    @RequestMapping(value = "/rest/run/{testplanId}/{testcaseId}/{tenv}/{tstatus}/{tcomments}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> updateTestRun(HttpServletRequest request,
            @PathVariable String tstatus,
            @PathVariable String tcomments,
            @PathVariable Long testplanId,
            @PathVariable Long testcaseId,
            @PathVariable String tenv) {
        try {
            //Check status + environment.
            if (!BizUtil.INSTANCE.checkStatus(tstatus)) {
                return new ResponseEntity<String>("Status code unknown!", HttpStatus.NOT_FOUND);
            }
            List<String> testEnvLst = Arrays.asList(testEnvArr.split(","));
            if (!testEnvLst.contains(tenv)) {
                return new ResponseEntity<String>("Environment unknown!", HttpStatus.NOT_FOUND);
            }

            EcTestcase tc = tcDao.findOne(testcaseId);
            EcTestplan tp = tpDao.findOne(testplanId);

            if (tc.getAutomated()) {
                //For automation to work the test case needs to have this flag enabled!
                EcTestplanTestcaseMapping tptcLinkId = tptcDao.findByTestplanIdAndTestcaseId(tp, tc);
                //Remove latest flag from all old runs.
                trDao.updateAllTestRunsLatestFlag(tptcLinkId.getId());
                EcTestresult tr = new EcTestresult();
                tr.setLatest(true);
                tr.setNote(tcomments);
                tr.setStatus(tstatus);
                tr.setTimestamp(new Date());
                tr.setTester(Constants.AUTOMATION_USER);
                tr.setEnvironment(tenv);
                tr.setTestplanTestcaseLinkId(tptcLinkId);
                trDao.save(tr);
            } else {
                return new ResponseEntity<String>("Test Case: " + tc.getId() + " is not marked as automated!", HttpStatus.NOT_FOUND);
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return new ResponseEntity<String>("ERROR!", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<String>("OK!", HttpStatus.OK);
    }

    @RequestMapping(value = "/rest/automated-data/{testplanId}", method = RequestMethod.GET)
    public List getAutomatedData(@PathVariable Long testplanId) {

        List<EcTestcase> tcLst = tcDao.findAllTestCasesByTestPlanId(testplanId);
        Integer autoCnt = 0;
        Integer manualCnt = 0;
        for (EcTestcase tc : tcLst) {
            if (tc.getAutomated()) {
                autoCnt++;
            } else {
                manualCnt++;
            }
        }

        List pieData = new ArrayList<>();
        List ele1 = new ArrayList();
        ele1.add("Automated Testcases");
        ele1.add(autoCnt);
        pieData.add(ele1);
        List ele2 = new ArrayList();
        ele2.add("Manual Testcases");
        ele2.add(manualCnt);
        pieData.add(ele2);

        return pieData;
    }

    @RequestMapping(value = "/rest/testtype-data/{testplanId}", method = RequestMethod.GET)
    public List getTesttypeData(@PathVariable Long testplanId) {

        List<EcTestcase> tcLst = tcDao.findAllTestCasesByTestPlanId(testplanId);
        Map<String, Integer> stats = new HashMap<String, Integer>();

        for (EcTestcase tc : tcLst) {
            if (stats.get(tc.getCaseType()) == null) {
                stats.put(tc.getCaseType(), 1);
            } else {
                stats.put(tc.getCaseType(), stats.get(tc.getCaseType()) + 1);
            }
        }
        List pieData = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : stats.entrySet()) {
            List ele = new ArrayList();
            ele.add(entry.getKey());
            ele.add(entry.getValue());
            pieData.add(ele);
        }

        return pieData;
    }

    @RequestMapping(value = "/rest/priority-data/{testplanId}", method = RequestMethod.GET)
    public List getPriorityData(@PathVariable Long testplanId) {

        List<EcTestcase> tcLst = tcDao.findAllTestCasesByTestPlanId(testplanId);
        Map<String, Integer> stats = new HashMap<String, Integer>();
        for (EcTestcase tc : tcLst) {
            if (stats.get(tc.getPriority()) == null) {
                stats.put(tc.getPriority(), 1);
            } else {
                stats.put(tc.getPriority(), stats.get(tc.getPriority()) + 1);
            }
        }
        List pieData = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : stats.entrySet()) {
            List ele = new ArrayList();
            ele.add(entry.getKey());
            ele.add(entry.getValue());
            pieData.add(ele);
        }

        return pieData;
    }

    @RequestMapping(value = "/rest/testplan-data", method = RequestMethod.GET)
    public List<Map<String, Object>> getTestPlanData(@RequestParam(value = "testplanId", required = true) Long testplanId) {

        List<Object[]> metricResults = tpDao.findRunMetricByTestPlanId(testplanId);
        Set<String> dtLst = new TreeSet<String>();
        List<Integer> passcntLst = new ArrayList<Integer>();
        List<Integer> failcntLst = new ArrayList<Integer>();
        List<Integer> othercntLst = new ArrayList<Integer>();

        Map<String, Integer> passMap = new HashMap<String, Integer>();
        Map<String, Integer> failMap = new HashMap<String, Integer>();
        Map<String, Integer> otherMap = new HashMap<String, Integer>();

        for (Object[] result : metricResults) {
            String dt = (String) result[1];
            dtLst.add(dt);
            String status = (String) result[0];
            Integer count = ((BigInteger) result[2]).intValue();
            if (status.equals(Constants.STATUS_PASSED)) {
                passMap.put(dt, count);
            } else if (status.equals(Constants.STATUS_FAILED)) {
                failMap.put(dt, count);
            } else {
                otherMap.put(dt, count);
            }
        }

        for (String dt : dtLst) {
            if (passMap.get(dt) != null) {
                passcntLst.add(passMap.get(dt));
            } else {
                passcntLst.add(0);
            }
            if (failMap.get(dt) != null) {
                failcntLst.add(failMap.get(dt));
            } else {
                failcntLst.add(0);
            }
            if (otherMap.get(dt) != null) {
                othercntLst.add(otherMap.get(dt));
            } else {
                othercntLst.add(0);
            }

        }

        List<Map<String, Object>> columnData = new ArrayList<>();
        Map<String, Object> headerElement = new HashMap<>();
        headerElement.put("name", "Day");
        headerElement.put("data", dtLst);
        columnData.add(headerElement);

        Map<String, Object> dataElement1 = new HashMap<>();
        dataElement1.put("name", Constants.STATUS_PASSED);
        dataElement1.put("data", passcntLst);
        columnData.add(dataElement1);

        Map<String, Object> dataElement2 = new HashMap<>();
        dataElement2.put("name", Constants.STATUS_FAILED);
        dataElement2.put("data", failcntLst);
        columnData.add(dataElement2);

        Map<String, Object> dataElement3 = new HashMap<>();
        dataElement3.put("name", Constants.STATUS_OTHER);
        dataElement3.put("data", othercntLst);
        columnData.add(dataElement3);

        return columnData;
    }
}
