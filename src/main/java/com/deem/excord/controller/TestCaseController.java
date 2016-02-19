package com.deem.excord.controller;

import com.deem.excord.domain.EcTestcase;
import com.deem.excord.domain.EcTestcaseRequirementMapping;
import com.deem.excord.domain.EcTestfolder;
import com.deem.excord.domain.EcTestplan;
import com.deem.excord.domain.EcTestplanTestcaseMapping;
import com.deem.excord.domain.EcTestresult;
import com.deem.excord.domain.EcTeststep;
import com.deem.excord.repository.TestCaseRepository;
import com.deem.excord.repository.TestFolderRepository;
import com.deem.excord.repository.TestPlanRepository;
import com.deem.excord.repository.TestPlanTestCaseRepository;
import com.deem.excord.repository.TestResultRepository;
import com.deem.excord.repository.TestStepRepository;
import com.deem.excord.repository.TestcaseRequirementRepository;
import com.deem.excord.util.BizUtil;
import com.deem.excord.util.Constants;
import com.deem.excord.util.FlashMsgUtil;
import com.deem.excord.util.HistoryUtil;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class TestCaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestCaseController.class);

    @Autowired
    TestFolderRepository tfDao;

    @Autowired
    TestCaseRepository tcDao;

    @Autowired
    TestPlanTestCaseRepository tptcDao;

    @Autowired
    TestPlanRepository tpDao;

    @Autowired
    TestStepRepository tsDao;

    @Autowired
    TestcaseRequirementRepository tcrDao;

    @Autowired
    TestResultRepository trDao;

    @Autowired
    HistoryUtil historyUtil;

    @RequestMapping(value = {"/search"}, method = RequestMethod.GET)
    public String search(HttpSession session, Model model) {
        return "search";
    }

    @RequestMapping(value = {"/search"}, method = RequestMethod.POST)
    public String searchKey(HttpSession session, Model model, @RequestParam(value = "searchKey", required = true) String searchKey) {
        LOGGER.info("Search key: {}", searchKey);
        List<EcTestcase> testCaseLst = tcDao.searchTestcase(searchKey);
        model.addAttribute("searchKey", searchKey);
        model.addAttribute("testCaseLst", testCaseLst);
        return "search";
    }

    @RequestMapping(value = "/testcase", method = RequestMethod.GET)
    public String testCases(Model model, HttpSession session, @RequestParam(value = "nodeId", required = false, defaultValue = "1") Long nodeId) {

        FlashMsgUtil.INSTANCE.checkFlashMsg(session, model);
        EcTestfolder currenNode = tfDao.findOne(nodeId);
        EcTestfolder tempNode = currenNode;
        EcTestfolder parentNode = currenNode.getParentId();
        if (parentNode == null) {
            parentNode = currenNode;
        }

        Iterable<EcTestfolder> nodeLst = tfDao.findAllByParentIdOrderByNameAsc(tempNode);
        List<Long> childNodeLst = new ArrayList<Long>();
        for (EcTestfolder d : nodeLst) {
            if (tfDao.checkIfHasChildren(d.getId())) {
                childNodeLst.add(d.getId());
            }
        }

        List<EcTestfolder> parentNodeLst = new ArrayList<>();
        parentNodeLst.add(tempNode);
        while (tempNode.getParentId() != null) {
            parentNodeLst.add(tempNode.getParentId());
            tempNode = tempNode.getParentId();
        }
        Collections.reverse(parentNodeLst);

        //find all testcases in node.
        List<EcTestcase> testCaseLst = tcDao.findAllByFolderIdOrderByIdAsc(currenNode);
        if (testCaseLst == null) {
            testCaseLst = new ArrayList<EcTestcase>();
        }

        //find all test plans associated with testcases in this node.
        List<EcTestplanTestcaseMapping> tptcmapLst = tptcDao.findAllByTestFolderId(currenNode.getId());
        if (tptcmapLst == null) {
            tptcmapLst = new ArrayList<EcTestplanTestcaseMapping>();
        }

        //find all requirements associated with testcases in this node.
        List<EcTestcaseRequirementMapping> tcreqmapLst = tcrDao.findAllByTestFolderId(currenNode.getId());
        if (tcreqmapLst == null) {
            tcreqmapLst = new ArrayList<EcTestcaseRequirementMapping>();
        }

        //find all testplans which are active
        List<EcTestplan> testPlanLst = tpDao.findByEnabledOrderByIdDesc(Boolean.TRUE);
        if (testPlanLst == null) {
            testPlanLst = new ArrayList<EcTestplan>();
        }

        model.addAttribute("childNodeLst", childNodeLst);
        model.addAttribute("currentNode", currenNode);
        model.addAttribute("parentNode", parentNode);
        model.addAttribute("nodeLst", nodeLst);
        model.addAttribute("testCaseLst", testCaseLst);
        model.addAttribute("parentNodeLst", parentNodeLst);
        model.addAttribute("tptcmapLst", tptcmapLst);
        model.addAttribute("testPlanLst", testPlanLst);
        model.addAttribute("tcreqmapLst", tcreqmapLst);

        return "testcase";
    }

    @RequestMapping(value = "/testcase_save", method = RequestMethod.POST)
    public String testCaseSave(Model model, HttpSession session, HttpServletRequest request,
            @RequestParam(value = "tid", required = false) Long tid,
            @RequestParam(value = "tstepCount", required = true) Long tstepCount,
            @RequestParam(value = "tname", required = true) String tname,
            @RequestParam(value = "tdescription", required = true) String tdescription,
            @RequestParam(value = "tenabled", defaultValue = "false", required = false) Boolean tenabled,
            @RequestParam(value = "tautomated", defaultValue = "false", required = false) Boolean tautomated,
            @RequestParam(value = "tpriority", required = true) String tpriority,
            @RequestParam(value = "ttype", required = true) String ttype,
            @RequestParam(value = "tfolderId", required = true) Long tfolderId,
            @RequestParam(value = "tscriptfile", required = false) String tscriptfile,
            @RequestParam(value = "tmethod", required = false) String tmethod,
            @RequestParam(value = "tbugid", required = false) String tbugid,
            @RequestParam(value = "tlanguage", required = false) String tlanguage,
            @RequestParam(value = "tproduct", required = false) String tproduct,
            @RequestParam(value = "tfeature", required = false) String tfeature,
            @RequestParam(value = "taversion", required = false) String taversion,
            @RequestParam(value = "tdversion", required = false) String tdversion,
            @RequestParam(value = "ttrun", required = true) Integer timeToRun
    ) {

        EcTestfolder folder = tfDao.findOne(tfolderId);

        EcTestcase tcObj;
        if (tid != null) {
            tcObj = tcDao.findOne(tid);
            //Delete all existing steps of testcase.
            tsDao.deleteTeststepByTestcaseId(tid);
            //Mark all test runs as not run.
            updateTestcaseAsNotRun(tcObj);
        } else {
            //New testcase
            tcObj = new EcTestcase();
            tcObj.setSlug(BizUtil.INSTANCE.getSlug());
        }

        tcObj.setName(tname);
        tcObj.setDescription(tdescription);
        tcObj.setEnabled(tenabled);
        tcObj.setAutomated(tautomated);
        tcObj.setPriority(tpriority);
        tcObj.setCaseType(ttype);
        tcObj.setFolderId(folder);
        tcObj.setTestScriptFile(tscriptfile);
        tcObj.setMethodName(tmethod);
        tcObj.setBugId(tbugid);
        tcObj.setLanguage(tlanguage);
        tcObj.setProduct(tproduct);
        tcObj.setFeature(tfeature);
        tcObj.setAddedVersion(taversion);
        tcObj.setDeprecatedVersion(tdversion);
        tcObj.setTimeToRun(timeToRun);
        tcDao.save(tcObj);

        for (int i = 1; i <= tstepCount; i++) {
            EcTeststep tstep = new EcTeststep();
            tstep.setStepNumber(i);
            tstep.setDescription(request.getParameter("testStep_" + i));
            tstep.setExpected(request.getParameter("testExpected_" + i));
            tstep.setTestcaseId(tcObj);
            tsDao.save(tstep);

        }
        historyUtil.addHistory("Saved testcase: [" + tname + "] under [" + folder.getName() + "]", tcObj.getSlug(), request, session);
        session.setAttribute("flashMsg", "Successfully saved testcase: " + tcObj.getName());

        return "redirect:/testcase?nodeId=" + tfolderId;
    }

    @RequestMapping(value = "/testcase_addfolder", method = RequestMethod.POST)
    public String testCaseAddFolder(Model model, HttpSession session, HttpServletRequest request, @RequestParam(value = "folderName", required = true) String folderName, @RequestParam(value = "nodeId", required = true) Long nodeId) {

        EcTestfolder parentFolder = tfDao.findOne(nodeId);
        EcTestfolder childFolder = new EcTestfolder();
        childFolder.setSlug(BizUtil.INSTANCE.getSlug());
        childFolder.setName(folderName);
        childFolder.setParentId(parentFolder);
        tfDao.save(childFolder);
        historyUtil.addHistory("Added folder: [" + folderName + "] under [" + parentFolder.getName() + "]", parentFolder.getSlug(), request, session);
        return "redirect:/testcase?nodeId=" + parentFolder.getId();
    }

    @RequestMapping(value = "/testcase_add", method = RequestMethod.GET)
    public String testCaseAdd(Model model, @RequestParam(value = "nodeId", required = false, defaultValue = "1") Long nodeId) {
        EcTestfolder currenNode = tfDao.findOne(nodeId);
        model.addAttribute("currentNode", currenNode);
        return "testcase_form";
    }

    @RequestMapping(value = "/testcase_testplan_link", method = RequestMethod.POST)
    public String testCaseTestPlanLink(Model model, HttpServletRequest request, HttpSession session, @RequestParam(value = "testPlanId", required = true) Long testPlanId, @RequestParam(value = "nodeId", required = true) Long nodeId, @RequestParam(value = "testcaseChk") List<Long> testcaseChkLst) {
        Boolean disabledTcPresent = false;
        Boolean enabledTcPresent = false;
        for (Long testCaseId : testcaseChkLst) {
            EcTestplanTestcaseMapping tptcMap = new EcTestplanTestcaseMapping();
            EcTestcase tcObj = tcDao.findOne(testCaseId);
            EcTestplan tpObj = tpDao.findOne(testPlanId);
            if (tptcDao.findByTestplanIdAndTestcaseId(tpObj, tcObj) == null) {
                //If already a mapping exists then skip
                tptcMap.setTestcaseId(tcObj);
                tptcMap.setTestplanId(tpObj);
                if (tcObj.getEnabled()) {
                    //Dont map disabled test cases.
                    enabledTcPresent = true;
                    tptcDao.save(tptcMap);
                    historyUtil.addHistory("Linked TestPlan : [" + tpObj.getName() + "] with TestCase: [" + tcObj.getName() + "]", tpObj.getSlug(), request, session);
                } else {
                    disabledTcPresent = true;
                    LOGGER.info("Cant link disabled test case: [{}:{}] to test plan: [{}:{}]", tcObj.getId(), tcObj.getName(), tpObj.getId(), tpObj.getName());
                }
            } else {
                LOGGER.info("Link already exists for test case: [{}:{}] to test plan: [{}:{}]", tcObj.getId(), tcObj.getName(), tpObj.getId(), tpObj.getName());
            }
        }

        if (disabledTcPresent == true && enabledTcPresent == true) {
            session.setAttribute("flashMsg", "Successfully Linked Testcases, Disabled testcases cant be linked!");
        } else if (disabledTcPresent == true && enabledTcPresent == false) {
            session.setAttribute("flashMsg", "Disabled testcases cant be linked!");
        } else if (disabledTcPresent == false && enabledTcPresent == true) {
            session.setAttribute("flashMsg", "Successfully Linked Testcases!");
        } else {
            session.setAttribute("flashMsg", "Testcases already linked!");
        }

        return "redirect:/testcase?nodeId=" + nodeId;
    }

    @RequestMapping(value = "/testcase_enable", method = RequestMethod.POST)
    public String testcaseEnable(Model model, HttpServletRequest request, HttpSession session, @RequestParam(value = "nodeId", required = true) Long nodeId, @RequestParam(value = "testcaseChk") List<Long> testcaseChkLst) {

        for (Long testCaseId : testcaseChkLst) {
            EcTestcase tcObj = tcDao.findOne(testCaseId);
            tcObj.setEnabled(true);
            tcDao.save(tcObj);
            historyUtil.addHistory("Enabled testcase : [" + tcObj.getName() + "]", tcObj.getSlug(), request, session);
        }
        session.setAttribute("flashMsg", "Successfully enabled testcase!");
        return "redirect:/testcase?nodeId=" + nodeId;
    }

    @RequestMapping(value = "/testcase_disable", method = RequestMethod.POST)
    public String testcaseDisable(Model model, HttpServletRequest request, HttpSession session, @RequestParam(value = "nodeId", required = true) Long nodeId, @RequestParam(value = "testcaseChk") List<Long> testcaseChkLst) {

        for (Long testCaseId : testcaseChkLst) {
            EcTestcase tcObj = tcDao.findOne(testCaseId);
            tcObj.setEnabled(false);
            tcDao.save(tcObj);
            historyUtil.addHistory("Disabled testcase : [" + tcObj.getName() + "]", tcObj.getSlug(), request, session);
        }
        session.setAttribute("flashMsg", "Successfully disabled testcase!");
        return "redirect:/testcase?nodeId=" + nodeId;
    }

    @RequestMapping(value = "/testcase_delete", method = RequestMethod.POST)
    public String testcaseDelete(Model model, HttpServletRequest request, HttpSession session, @RequestParam(value = "nodeId", required = true) Long nodeId, @RequestParam(value = "testcaseChk") List<Long> testcaseChkLst) {

        for (Long testCaseId : testcaseChkLst) {
            EcTestcase tcObj = tcDao.findOne(testCaseId);
            String testcaseName = tcObj.getName();
            String testcaseSlug = tcObj.getSlug();
            tcDao.delete(tcObj);
            historyUtil.addHistory("Deleted testcase : [" + testcaseName + "]", testcaseSlug, request, session);
        }
        session.setAttribute("flashMsg", "Successfully deleted testcase!");
        return "redirect:/testcase?nodeId=" + nodeId;
    }

    @RequestMapping(value = "/testcase_deletefolder", method = RequestMethod.POST)
    public String testcaseDeleteFolder(Model model, HttpServletRequest request, HttpSession session, @RequestParam(value = "nodeId", required = true) Long nodeId) {
        EcTestfolder currentNode = tfDao.findOne(nodeId);

        //check if folder has nested
        //check if folder has tc.
        if (currentNode.getParentId() == null) {
            session.setAttribute("flashMsg", "Cant delete root node!");
            return "redirect:/testcase?nodeId=" + nodeId;
        }

//        if (!currentNode.getEcTestcaseList().isEmpty()) {
//            session.setAttribute("flashMsg", "Cant delete node with testcases. Delete the testcases or move them prior to delete!");
//            return "redirect:/testcase?nodeId=" + nodeId;
//        }
//        if (!currentNode.getEcTestfolderList().isEmpty()) {
//            session.setAttribute("flashMsg", "Cant delete node with nested nodes. Delete the nested nodes prior to delete!");
//            return "redirect:/testcase?nodeId=" + nodeId;
//        }
        if (currentNode.getParentId() != null) {
            Long parentId = currentNode.getParentId().getId();
            String folderName = currentNode.getName();
            String folderSlug = currentNode.getSlug();

            //Soft delete, node assigned to trash.
            currentNode.setParentId(tfDao.findOne(Constants.TRASH_NODE));
            tfDao.save(currentNode);

            historyUtil.addHistory("Folder Deleted : [" + folderName + "]", folderSlug, request, session);
            session.setAttribute("flashMsg", "Successfully deleted folder!");
            return "redirect:/testcase?nodeId=" + parentId;
        } else {
            return "redirect:/testcase?nodeId=" + nodeId;
        }
    }

    @RequestMapping(value = "/testcase_bulkedit", method = RequestMethod.POST)
    public String testcaseBulkEdit(Model model, HttpServletRequest request, HttpSession session, @RequestParam(value = "nodeId", required = true) Long nodeId, @RequestParam(value = "testcaseChk") List<Long> testcaseChkLst) {
        String bulkTc = StringUtils.arrayToCommaDelimitedString(testcaseChkLst.toArray());
        session.setAttribute("bulkTc", bulkTc);
        model.addAttribute("nodeId", nodeId);
        return "testcase_form_bulk";
    }

    @RequestMapping(value = "/testcase_bulksave", method = RequestMethod.POST)
    public String testcaseBulkSave(Model model, HttpServletRequest request, HttpSession session,
            @RequestParam(value = "nodeId", required = true) Long nodeId,
            @RequestParam(value = "bulkTc", required = true) String bulkTc,
            @RequestParam(value = "tenabled", required = false) Boolean tenabled,
            @RequestParam(value = "tautomated", required = false) Boolean tautomated,
            @RequestParam(value = "tpriority", required = true) String tpriority,
            @RequestParam(value = "ttype", required = true) String ttype,
            @RequestParam(value = "tlanguage", required = false) String tlanguage,
            @RequestParam(value = "tproduct", required = false) String tproduct,
            @RequestParam(value = "tfeature", required = false) String tfeature,
            @RequestParam(value = "taversion", required = false) String taversion,
            @RequestParam(value = "tdversion", required = false) String tdversion) {
        String[] tcLst = StringUtils.commaDelimitedListToStringArray(bulkTc);
        for (String tc : tcLst) {
            EcTestcase tcObj = tcDao.findOne(Long.parseLong(tc));
            if (taversion != null) {
                tcObj.setAddedVersion(taversion);
            }
            if (ttype != null) {
                tcObj.setCaseType(ttype);
            }
            if (tdversion != null) {
                tcObj.setDeprecatedVersion(tdversion);
            }
            if (tenabled != null) {
                tcObj.setEnabled(tenabled);
            }
            if (tautomated != null) {
                tcObj.setAutomated(tautomated);
            }
            if (tfeature != null) {
                tcObj.setFeature(tfeature);
            }
            if (tlanguage != null) {
                tcObj.setLanguage(tlanguage);
            }
            if (tpriority != null) {
                tcObj.setPriority(tpriority);
            }
            if (tproduct != null) {
                tcObj.setProduct(tproduct);
            }
            if (tcObj.getDescription() == null || tcObj.getDescription().equals("")) {
                tcObj.setDescription(" ");
            }
            tcDao.save(tcObj);
            historyUtil.addHistory("Testcase Bulk Updated : [" + tcObj.getName() + "]", tcObj.getSlug(), request, session);
        }
        return "redirect:/testcase?nodeId=" + nodeId;
    }

    @RequestMapping(value = "/testcase_cut", method = RequestMethod.POST)
    public String testcaseCut(Model model, HttpServletRequest request, HttpSession session, @RequestParam(value = "nodeId", required = true) Long nodeId, @RequestParam(value = "testcaseChk") List<Long> testcaseChkLst) {
        String clipboardTc = StringUtils.arrayToCommaDelimitedString(testcaseChkLst.toArray());
        session.setAttribute("clipboardTc", clipboardTc);
        session.setAttribute("flashMsg", "Testcases ready to move!");
        return "redirect:/testcase?nodeId=" + nodeId;
    }

    @RequestMapping(value = "/testcase_paste", method = RequestMethod.POST)
    public String testcasePaste(Model model, HttpServletRequest request, HttpSession session, @RequestParam(value = "nodeId", required = true) Long nodeId) {
        String clipboardTc = (String) session.getAttribute("clipboardTc");

        if (clipboardTc != null) {
            String[] clipboardTcLst = StringUtils.commaDelimitedListToStringArray(clipboardTc);
            for (String tc : clipboardTcLst) {
                EcTestcase tcObj = tcDao.findOne(Long.parseLong(tc));
                EcTestfolder newNode = tfDao.findOne(nodeId);
                tcObj.setFolderId(newNode);
                String oldFolder = tcObj.getFolderId().getName();
                String newFolder = newNode.getName();
                tcDao.save(tcObj);
                historyUtil.addHistory("Moved testcase : [" + tcObj.getName() + "] from [" + oldFolder + " ] to [" + newFolder + " ]", tcObj.getSlug(), request, session);
            }
            session.setAttribute("clipboardTc", null);
            session.setAttribute("flashMsg", "Testcases moved successfully!");
        }

        return "redirect:/testcase?nodeId=" + nodeId;
    }

    @RequestMapping(value = "/testcase_renamefolder", method = RequestMethod.POST)
    public String testcaseRenameFolder(Model model, HttpServletRequest request, HttpSession session, @RequestParam(value = "nodeId", required = true) Long nodeId, @RequestParam(value = "newNodeName", required = true) String newNodeName) {

        EcTestfolder currenNode = tfDao.findOne(nodeId);
        if (currenNode.getParentId() != null) {
            String currentName = currenNode.getName();
            String currentSlug = currenNode.getSlug();
            String newName = newNodeName;
            currenNode.setName(newNodeName);
            tfDao.save(currenNode);
            historyUtil.addHistory("Folder Renamed from : [" + currentName + "] to [" + newName + "]", currentSlug, request, session);
            session.setAttribute("flashMsg", "Successfully renamed folder!");
        } else {
            session.setAttribute("flashMsg", "Cant rename root folder!");
        }
        return "redirect:/testcase?nodeId=" + nodeId;
    }

    @RequestMapping(value = "/testcase_clone", method = RequestMethod.POST)
    public String testcaseClone(Model model, HttpServletRequest request, HttpSession session, @RequestParam(value = "nodeId", required = true) Long nodeId, @RequestParam(value = "testcaseChk") List<Long> testcaseChkLst) {

        for (Long testCaseId : testcaseChkLst) {
            EcTestcase tc = tcDao.findOne(testCaseId);
            EcTestcase newTc = new EcTestcase();
            newTc.setSlug(BizUtil.INSTANCE.getSlug());
            newTc.setAddedVersion(tc.getAddedVersion());
            newTc.setAutomated(tc.getAutomated());
            newTc.setBugId(tc.getBugId());
            newTc.setCaseType(tc.getCaseType());
            newTc.setDeprecatedVersion(tc.getDeprecatedVersion());
            newTc.setDescription(tc.getDescription());
            newTc.setEnabled(tc.getEnabled());
            newTc.setFeature(tc.getFeature());
            newTc.setFolderId(tc.getFolderId());
            newTc.setLanguage(tc.getLanguage());
            newTc.setMethodName(tc.getMethodName());

            String newTCName = tc.getName();
            if (newTCName.length() > 84) {
                newTCName = newTCName.substring(0, 84) + "_CLONE";
            }
            newTc.setName(newTCName);
            newTc.setPriority(tc.getPriority());
            newTc.setProduct(tc.getProduct());
            newTc.setTestScriptFile(tc.getTestScriptFile());
            newTc.setTimeToRun(tc.getTimeToRun());
            tcDao.save(newTc);
            historyUtil.addHistory("Cloned testcase : [" + tc.getName() + "]", tc.getSlug(), request, session);
            for (EcTeststep step : tc.getEcTeststepList()) {
                EcTeststep newstep = new EcTeststep();
                newstep.setDescription(step.getDescription());
                newstep.setExpected(step.getExpected());
                newstep.setStepNumber(step.getStepNumber());
                newstep.setTestcaseId(newTc);
                tsDao.save(newstep);
            }

        }
        session.setAttribute("flashMsg", "Successfully cloned!");
        return "redirect:/testcase?nodeId=" + nodeId;
    }

    @RequestMapping(value = "/testcase_export", method = RequestMethod.POST)
    public void testCaseExport(HttpServletResponse response, @RequestParam(value = "nodeId", required = true) Long nodeId, @RequestParam(value = "testcaseChk") List<Long> testcaseChkLst) {

        List<EcTestcase> testCaseLst = tcDao.findAllByFolderIdOrderByIdAsc(tfDao.findOne(nodeId));
        if (testCaseLst == null) {
            testCaseLst = new ArrayList<>();
        }

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("TestCases");

        Map<Integer, Object[]> data = new TreeMap<>();
        Integer idx = 0;
        data.put(idx, new Object[]{"ID", "NAME", "DESCRIPTION", "TIME_TO_RUN", "STEP", "PROCEDURE", "EXPECTED"});
        idx++;
        for (EcTestcase tc : testCaseLst) {
            if (testcaseChkLst.contains(tc.getId())) {
                String testcaseName = tc.getName();
                String testcaseDesc = tc.getDescription();
                String timetToRun = String.valueOf(tc.getTimeToRun());
                if (tc.getEcTeststepList().isEmpty()) {
                    data.put(idx, new Object[]{tc.getId().toString(), testcaseName, testcaseDesc, timetToRun, 1, "", ""});
                    idx++;
                } else {
                    for (EcTeststep step : tc.getEcTeststepList()) {
                        data.put(idx, new Object[]{tc.getId().toString(), testcaseName, testcaseDesc, timetToRun, step.getStepNumber(), step.getDescription(), step.getExpected()});
                        testcaseDesc = "";
                        testcaseName = "";
                        timetToRun = "";
                        idx++;
                    }
                }

            }
        }

        for (Map.Entry<Integer, Object[]> entry : data.entrySet()) {
            Integer key = entry.getKey();
            Object[] objArr = entry.getValue();
            Row row = sheet.createRow(key);
            int cellnum = 0;
            for (Object obj : objArr) {
                Cell cell = row.createCell(cellnum++);
                //All values will be Strings.
                if (obj instanceof String) {
                    cell.setCellValue((String) obj);
                } else {
                    cell.setCellValue(obj.toString());
                }
            }

        }

        try (ServletOutputStream outputStream = response.getOutputStream()) {
            response.setContentType("application/octet-stream");
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            String dateStr = sdf.format(cal.getTime());
            response.setHeader("Content-Disposition", "attachment; fileName=TestCases_Node_" + nodeId + "_" + dateStr + ".xlsx");
            workbook.write(outputStream);
        } catch (Exception ex) {
            LOGGER.error("Testcase Export Error!", ex);
        }
    }

    @RequestMapping(value = "/testcase_upload", method = RequestMethod.POST)
    public String testCaseUpload(HttpSession session, HttpServletRequest request, @RequestParam(value = "nodeId", required = true) Long nodeId, @RequestParam(value = "file") MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                String fileName = file.getOriginalFilename();
                LOGGER.info("Uploading testcase file: {}", fileName);
                ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                XSSFWorkbook workbook = new XSSFWorkbook(bis);
                XSSFSheet sheet = workbook.getSheetAt(0);
                Iterator<Row> rowIterator = sheet.iterator();
                EcTestfolder currentNode = tfDao.findOne(nodeId);
                EcTestcase tc = null;
                Integer stepNumber = 1;
                Boolean skipHeader = true;
                Long previousTestId = -1L;
                Long newTestId = -1L;
                final DataFormatter df = new DataFormatter();
                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    if (skipHeader) {
                        skipHeader = false;
                        continue;
                    }
                    if (row.getCell(0) != null) {
                        newTestId = Long.parseLong(df.formatCellValue(row.getCell(0)));
                        String testProcedure = BizUtil.INSTANCE.validateInput(df.formatCellValue(row.getCell(5)), -1);
                        String testExpected = BizUtil.INSTANCE.validateInput(df.formatCellValue(row.getCell(6)), -1);

                        if (newTestId.equals(previousTestId) && tc != null) {
                            //Just keep adding steps.
                            EcTeststep tp = new EcTeststep();
                            tp.setStepNumber(stepNumber);
                            tp.setDescription(testProcedure);
                            tp.setExpected(testExpected);
                            tp.setTestcaseId(tc);
                            stepNumber++;
                            tsDao.save(tp);
                        } else {
                            String testName = BizUtil.INSTANCE.validateInput(df.formatCellValue(row.getCell(1)), 90);
                            String testDescription = BizUtil.INSTANCE.validateInput(df.formatCellValue(row.getCell(2)), -1);
                            Integer timeToRun = Integer.parseInt(BizUtil.INSTANCE.validateInput(df.formatCellValue(row.getCell(3)), -1));

                            if (timeToRun != 1 & timeToRun != 5 && timeToRun != 10 && timeToRun != 15 && timeToRun != 20 && timeToRun != 30) {
                                timeToRun = 5;
                            }

                            previousTestId = newTestId;
                            stepNumber = 1;
                            tc = tcDao.findByIdAndFolderId(previousTestId, currentNode);
                            if (tc == null) {
                                tc = new EcTestcase();
                                tc.setSlug(BizUtil.INSTANCE.getSlug());
                                tc.setPriority(Constants.PRIORITY_P1);
                                tc.setCaseType(Constants.TYPE_MANUAL);
                                tc.setLanguage("");
                                tc.setTestScriptFile("");
                                tc.setMethodName("");
                                tc.setBugId("");
                                tc.setProduct("");
                                tc.setFeature("");
                                tc.setAddedVersion("");
                                tc.setDeprecatedVersion("");
                                historyUtil.addHistory("Testcase [" + testName + "] added by import, file: " + fileName, tc.getSlug(), request, session);
                            } else {
                                historyUtil.addHistory("Testcase [" + testName + "] updated by import, file: " + fileName, tc.getSlug(), request, session);
                                //Delete all existing teststeps.
                                tsDao.deleteTeststepByTestcaseId(tc.getId());
                                //Mark all test runs as not run.
                                updateTestcaseAsNotRun(tc);
                            }
                            tc.setName(testName);
                            tc.setDescription(testDescription);
                            tc.setEnabled(true);
                            tc.setFolderId(currentNode);
                            tc.setTimeToRun(timeToRun);
                            tcDao.save(tc);
                            EcTeststep tp = new EcTeststep();
                            tp.setStepNumber(stepNumber);
                            tp.setDescription(testProcedure);
                            tp.setExpected(testExpected);
                            tp.setTestcaseId(tc);
                            stepNumber++;
                            tsDao.save(tp);
                        }
                    }

                    //Just keep adding test steps.
                }
                session.setAttribute("flashMsg", "Successfully Imported :" + fileName);
            } catch (IOException | NumberFormatException ex) {
                session.setAttribute("flashMsg", "File upload failed! " + ex.getMessage());
                LOGGER.error("Testcase upload ERROR!", ex);
            }
        } else {
            session.setAttribute("flashMsg", "File is empty!");
        }
        return "redirect:/testcase?nodeId=" + nodeId;
    }

    @RequestMapping(value = "/testcase_edit", method = RequestMethod.GET)
    public String testcaseEdit(Model model, HttpServletRequest request, HttpSession session, @RequestParam(value = "testcaseId", required = true) Long testcaseId) {
        EcTestcase tc = tcDao.findOne(testcaseId);
        List<EcTeststep> tsLst = tsDao.findByTestcaseId(tc);

        Boolean reviewTc = tcrDao.checkIfNeedsReview(testcaseId);
        model.addAttribute("currentNode", tc.getFolderId());
        model.addAttribute("tc", tc);
        model.addAttribute("reviewTc", reviewTc);
        model.addAttribute("tsLst", tsLst);
        return "testcase_form";
    }

    @RequestMapping(value = "/testcase_view", method = RequestMethod.GET)
    public String testcaseView(Model model, HttpServletRequest request, HttpSession session, @RequestParam(value = "testcaseId", required = true) Long testcaseId) {
        EcTestcase tc = tcDao.findOne(testcaseId);
        List<EcTeststep> tsLst = tsDao.findByTestcaseId(tc);
        model.addAttribute("tc", tc);
        model.addAttribute("tsLst", tsLst);
        return "testcase_step";
    }

    @RequestMapping(value = "/testcase_req_link", method = RequestMethod.POST)
    public String testCaseRequirementLink(Model model, HttpServletRequest request, HttpSession session, @RequestParam(value = "nodeId", required = true) Long nodeId, @RequestParam(value = "testcaseChk") List<Long> testcaseChkLst) {
        String clipboardLinkTc = StringUtils.arrayToCommaDelimitedString(testcaseChkLst.toArray());
        session.setAttribute("clipboardLinkTc", clipboardLinkTc);
        session.setAttribute("clipboardNodeId", nodeId);
        return "redirect:/requirement";
    }

    public void updateTestcaseAsNotRun(EcTestcase tc) {
        tcrDao.updateAllLinkedTestcaseAsReviewed(tc.getId());
        //Mark all active testplan where status is run to not run for re-run of testcases.
        List<EcTestplanTestcaseMapping> tptcMappingLst = tptcDao.findByTestcaseId(tc);
        for (EcTestplanTestcaseMapping tptcLink : tptcMappingLst) {
            trDao.updateAllTestRunsLatestFlag(tptcLink.getId());
            EcTestresult tr = new EcTestresult();
            tr.setLatest(true);
            tr.setNote("");
            tr.setStatus(Constants.STATUS_NOT_RUN);
            tr.setTimestamp(new Date());
            tr.setTester(Constants.USER_SYSTEM);
            tr.setEnvironment("NA");
            tr.setBugTicket("");
            tr.setLatest(true);
            tr.setTestplanTestcaseLinkId(tptcLink);
            trDao.save(tr);
        }
    }
}
