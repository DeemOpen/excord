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

    @RequestMapping(value = "/rest/pie-data", method = RequestMethod.GET)
    public List getPieData() {
        List pieData = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            List dataElement = new ArrayList();
            dataElement.add("Region_" + i);
            dataElement.add(Math.random() * 10);
            pieData.add(dataElement);
        }
        return pieData;
    }

    @RequestMapping(value = "/rest/column-data", method = RequestMethod.GET)
    public List<Map<String, Object>> getColumnData() {
        List<Map<String, Object>> columnData = new ArrayList<>();
        Map<String, Object> headerElement = new HashMap<>();
        headerElement.put("name", "Month");
        headerElement.put("data", new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"});
        columnData.add(headerElement);

        Map<String, Object> dataElement1 = new HashMap<>();
        dataElement1.put("name", Constants.STATUS_PASSED);
        dataElement1.put("data", new Integer[]{4, 5, 6, 2, 5, 7, 2, 1, 6, 7, 3, 4});
        columnData.add(dataElement1);

        Map<String, Object> dataElement2 = new HashMap<>();
        dataElement2.put("name", Constants.STATUS_FAILED);
        dataElement2.put("data", new Integer[]{7, 8, 9, 6, 7, 10, 9, 7, 6, 9, 8, 4});
        columnData.add(dataElement2);

        Map<String, Object> dataElement3 = new HashMap<>();
        dataElement3.put("name", Constants.STATUS_OTHER);
        dataElement3.put("data", new Integer[]{5, 2, 3, 6, 7, 1, 2, 6, 6, 4, 6, 3});
        columnData.add(dataElement3);

        return columnData;
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
