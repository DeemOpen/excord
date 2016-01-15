package com.deem.excord.controller;

import com.deem.excord.domain.EcRequirement;
import com.deem.excord.domain.EcTestcase;
import com.deem.excord.domain.EcTestcaseRequirementMapping;
import com.deem.excord.repository.RequirementRepository;
import com.deem.excord.repository.TestCaseRepository;
import com.deem.excord.repository.TestcaseRequirementRepository;
import com.deem.excord.util.FlashMsgUtil;
import com.deem.excord.util.HistoryUtil;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.poi.ss.usermodel.Cell;
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

@Controller
public class RequirementController {

    private static final Logger logger = LoggerFactory.getLogger(RequirementController.class);

    @Autowired
    RequirementRepository reqDao;

    @Autowired
    TestcaseRequirementRepository tcrDao;

    @Autowired
    TestCaseRepository tcDao;

    @Autowired
    HistoryUtil historyUtil;

    @RequestMapping(value = "/requirement", method = RequestMethod.GET)
    public String requirement(Model model, HttpSession session, @RequestParam(value = "reqId", required = false, defaultValue = "1") Long reqId) {

        FlashMsgUtil.INSTANCE.checkFlashMsg(session, model);
        EcRequirement currReq = reqDao.findOne(reqId);
        EcRequirement parentReq = currReq.getParentId();
        if (parentReq == null) {
            parentReq = currReq;
        }
        EcRequirement tempReq = currReq;

        List<EcRequirement> parentReqLst = new ArrayList<>();
        parentReqLst.add(tempReq);
        while (tempReq.getParentId() != null) {
            parentReqLst.add(tempReq.getParentId());
            tempReq = tempReq.getParentId();
        }
        Collections.reverse(parentReqLst);

        Iterable<EcRequirement> nodeLst = reqDao.findAllByParentIdOrderByNameAsc(currReq);
        List<Long> childReqLst = new ArrayList<Long>();
        for (EcRequirement d : nodeLst) {
            if (reqDao.checkIfHasChildren(d.getId())) {
                childReqLst.add(d.getId());
            }
        }

        List<EcTestcaseRequirementMapping> tcrLst = tcrDao.findAllByRequirementId(currReq);

        model.addAttribute("currReq", currReq);
        model.addAttribute("parentReq", parentReq);
        model.addAttribute("parentReqLst", parentReqLst);
        model.addAttribute("nodeLst", nodeLst);
        model.addAttribute("childReqLst", childReqLst);
        model.addAttribute("tcrLst", tcrLst);

        return "requirement";
    }

    @RequestMapping(value = "/requirement_upload", method = RequestMethod.POST)
    public String requirementUpload(Model model, HttpServletRequest request, HttpSession session, @RequestParam(value = "reqId", required = true) Long reqId, @RequestParam(value = "action", required = true) String action) {
        return "redirect:/requirement";
    }

    @RequestMapping(value = "/testcase_requirement_link", method = RequestMethod.GET)
    public String testcaseRequirementLink(Model model, HttpSession session, HttpServletRequest request, @RequestParam(value = "reqId", required = false, defaultValue = "1") Long reqId) {
        Long nodeId = (Long) session.getAttribute("clipboardNodeId");
        String clipboardLinkTc = (String) session.getAttribute("clipboardLinkTc");
        if (clipboardLinkTc != null) {
            String[] clipboardLinkTcLst = StringUtils.commaDelimitedListToStringArray(clipboardLinkTc);
            for (String tcId : clipboardLinkTcLst) {

                EcTestcase tcObj = tcDao.findOne(Long.parseLong(tcId));
                EcRequirement reqObj = reqDao.findOne(reqId);
                if (tcrDao.findByTestcaseIdAndRequirementId(tcObj, reqObj) == null) {
                    EcTestcaseRequirementMapping tcrObj = new EcTestcaseRequirementMapping();
                    tcrObj.setRequirementId(reqObj);
                    tcrObj.setTestcaseId(tcObj);
                    tcrObj.setReview(Boolean.FALSE);
                    tcrDao.save(tcrObj);
                    historyUtil.addHistory("Linked testcase: [" + tcObj.getId() + ":" + tcObj.getName() + "] with requirement: [" + reqObj.getId() + ":" + reqObj.getName() + "]", session, request);
                }

            }
            session.setAttribute("flashMsg", "Successfully Linked testcases to requirement!");
            session.setAttribute("clipboardLinkTc", null);
            session.setAttribute("clipboardNodeId", null);
        }
        return "redirect:/testcase?nodeId=" + nodeId;
    }

    @RequestMapping(value = "/testcase_requirement_unlink", method = RequestMethod.GET)
    public String testcaseRequirementUnLink(Model model, HttpSession session, HttpServletRequest request, @RequestParam(value = "reqId", required = true) Long reqId, @RequestParam(value = "tcId", required = true) Long tcId) {

        EcTestcase tcObj = tcDao.findOne(tcId);
        EcRequirement reqObj = reqDao.findOne(reqId);
        if (tcObj != null && reqObj != null) {
            tcrDao.deleteByTestcaseIdAndRequirementId(tcObj, reqObj);
            session.setAttribute("flashMsg", "Successfully Unlinked testcases from requirement!");
            historyUtil.addHistory("Unlinked testcase: [" + tcObj.getId() + ":" + tcObj.getName() + "] from requirement: [" + reqObj.getId() + ":" + reqObj.getName() + "]", session, request);
        }
        return "redirect:/requirement?reqId=" + reqId;
    }

    @RequestMapping(value = "/testcase_requirement_link_cancel", method = RequestMethod.GET)
    public String testcaseRequirementLinkCancel(Model model, HttpSession session, @RequestParam(value = "reqId", required = false, defaultValue = "1") Long reqId) {
        Long nodeId = (Long) session.getAttribute("clipboardNodeId");
        session.setAttribute("clipboardLinkTc", null);
        session.setAttribute("clipboardNodeId", null);
        session.setAttribute("flashMsg", "Linking cancelled!");
        return "redirect:/testcase?nodeId=" + nodeId;
    }

    @RequestMapping(value = "/req_create", method = RequestMethod.GET)
    public String requirementCreate(Model model, HttpSession session, @RequestParam(value = "reqId", required = false, defaultValue = "1") Long reqId) {
        EcRequirement parentReq = reqDao.findOne(reqId);
        model.addAttribute("parentReq", parentReq);
        return "requirement_form";
    }

    @RequestMapping(value = "/req_edit", method = RequestMethod.GET)
    public String requirementEdit(Model model, HttpSession session, @RequestParam(value = "reqId", required = false, defaultValue = "1") Long reqId) {
        EcRequirement req = reqDao.findOne(reqId);
        model.addAttribute("req", req);
        model.addAttribute("parentReq", req.getParentId());
        return "requirement_form";
    }

    @RequestMapping(value = "/req_delete", method = RequestMethod.POST)
    public String requirementDelete(Model model, HttpSession session, HttpServletRequest request, @RequestParam(value = "reqId", required = false, defaultValue = "1") Long reqId) {

        EcRequirement req = reqDao.findOne(reqId);
        if (req.getParentId() == null) {
            session.setAttribute("flashMsg", "Cant delete root requirement!");
            return "redirect:/requirement?reqId=" + reqId;
        }
        if (!req.getEcRequirementList().isEmpty()) {
            session.setAttribute("flashMsg", "Cant delete requirements with child requirements. Delete the child requirements prior to delete!");
            return "redirect:/requirement?reqId=" + reqId;
        } else {
            Long parentId = req.getParentId().getId();
            String reqName = req.getName();
            reqDao.delete(req);
            historyUtil.addHistory("Deleted requirement: [" + reqName + "]", session, request);
            session.setAttribute("flashMsg", "Successfully deleted requirement!");
            return "redirect:/requirement?reqId=" + parentId;
        }
    }

    @RequestMapping(value = "/requirement_save", method = RequestMethod.POST)
    public String requirementSave(Model model, HttpSession session, HttpServletRequest request,
            @RequestParam(value = "reqId", required = false) Long reqId,
            @RequestParam(value = "rParentId", required = true) Long rParentId,
            @RequestParam(value = "rname", required = true) String rname,
            @RequestParam(value = "rstatus", required = true) String rstatus,
            @RequestParam(value = "rcoverage", defaultValue = "false", required = true) Boolean rcoverage,
            @RequestParam(value = "rpriority", required = true) String rpriority,
            @RequestParam(value = "rrelease", required = false) String rrelease,
            @RequestParam(value = "rproduct", required = false) String rproduct,
            @RequestParam(value = "rstory", required = true) String rstory
    ) {

        EcRequirement parentReq = null;
        if (!rParentId.equals(-1L)) {
            parentReq = reqDao.findOne(rParentId);
        }
        EcRequirement req = null;
        if (reqId != null) {
            req = reqDao.findOne(reqId);
        } else {
            req = new EcRequirement();
        }
        req.setParentId(parentReq);
        req.setName(rname);
        req.setStatus(rstatus);
        req.setCoverage(rcoverage);
        req.setPriority(rpriority);
        req.setReleaseName(rrelease);
        req.setProduct(rproduct);
        req.setStory(rstory);
        reqDao.save(req);
        if (!rParentId.equals(-1L)) {
            historyUtil.addHistory("Saved requirement: [" + rname + "] under [" + parentReq.getId() + ":" + parentReq.getName() + "]", session, request);
        } else {
            historyUtil.addHistory("Saved requirement: [" + rname + "] under [ - ]", session, request);
        }

        session.setAttribute("flashMsg", "Successfully saved requirement: " + rname);
        tcrDao.updateAllLinkedTestcaseForReview(reqId);

        return "redirect:/requirement?reqId=" + req.getId();
    }

    @RequestMapping(value = "/req_export", method = RequestMethod.GET)
    public void requirementExport(HttpServletResponse response, @RequestParam(value = "reqId", required = true, defaultValue = "1") Long reqId) {

        ServletOutputStream outputStream = null;

        EcRequirement currenReq = reqDao.findOne(reqId);
        List<EcRequirement> requirementLst = reqDao.findAllByParentIdOrderByNameAsc(currenReq);
        if (requirementLst == null) {
            requirementLst = new ArrayList<EcRequirement>();
        }

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Requirements");

        Map<Integer, Object[]> data = new TreeMap<Integer, Object[]>();
        Integer idx = 0;
        data.put(idx, new Object[]{"ID", "NAME", "PRIORITY", "STATUS", "RELEASE_NAME", "PRODUCT", "COVERAGE", "STORY"});
        idx++;
        for (EcRequirement req : requirementLst) {
            data.put(idx, new Object[]{req.getId().toString(), req.getName(), req.getPriority(), req.getStatus(), req.getReleaseName(), req.getProduct(), req.getCoverage(), req.getStory()});
            idx++;
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

        try {
            response.setContentType("application/octet-stream");
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            String dateStr = sdf.format(cal.getTime());
            response.setHeader("Content-Disposition", "attachment; fileName=Requirements_" + currenReq.getId() + "_" + dateStr + ".xlsx");
            outputStream = response.getOutputStream();
            workbook.write(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.flush();
                outputStream.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
