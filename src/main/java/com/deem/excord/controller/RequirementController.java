package com.deem.excord.controller;

import com.deem.excord.domain.EcRequirement;
import com.deem.excord.domain.EcTestcase;
import com.deem.excord.domain.EcTestcaseRequirementMapping;
import com.deem.excord.domain.EcTestfolder;
import com.deem.excord.domain.EcTeststep;
import com.deem.excord.repository.RequirementRepository;
import com.deem.excord.repository.TestCaseRepository;
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
        //if (!req.getEcRequirementList().isEmpty()) {
        //    session.setAttribute("flashMsg", "Cant delete requirements with child requirements. Delete the child requirements prior to delete!");
        //    return "redirect:/requirement?reqId=" + reqId;
        //}

        Long parentId = req.getParentId().getId();
        String reqName = req.getName();
        reqDao.delete(req);
        historyUtil.addHistory("Deleted requirement: [" + reqId + ":" + reqName + "]", session, request);
        session.setAttribute("flashMsg", "Successfully deleted requirement!");
        return "redirect:/requirement?reqId=" + parentId;

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

        if (req.getStory() != null) {
            if (!req.getStory().equals(rstory)) {
                //Story changed then mark all testcases for review.
                tcrDao.updateAllLinkedTestcaseForReview(reqId);
            }
        }
        req.setStory(rstory);
        reqDao.save(req);
        if (!rParentId.equals(-1L)) {
            historyUtil.addHistory("Saved requirement: [" + rname + "] under [" + parentReq.getId() + ":" + parentReq.getName() + "]", session, request);
        } else {
            historyUtil.addHistory("Saved requirement: [" + rname + "] under [ - ]", session, request);
        }
        session.setAttribute("flashMsg", "Successfully saved requirement: " + rname);

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

    @RequestMapping(value = "/req_upload", method = RequestMethod.POST)
    public String requirementUpload(Model model, HttpServletRequest request, HttpSession session, @RequestParam(value = "reqId", required = true) Long reqId, @RequestParam(value = "file") MultipartFile file) {
        Boolean statusError = false;
        Boolean priorityError = false;
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                String fileName = file.getOriginalFilename();
                logger.info("Uploading requirements file: {}", fileName);
                ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                XSSFWorkbook workbook = new XSSFWorkbook(bis);
                XSSFSheet sheet = workbook.getSheetAt(0);
                Iterator<Row> rowIterator = sheet.iterator();
                EcRequirement reqObj = reqDao.findOne(reqId);
                Boolean skipHeader = true;
                final DataFormatter df = new DataFormatter();
                Long childReqId = -1L;
                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    if (skipHeader) {
                        skipHeader = false;
                        continue;
                    }
                    EcRequirement childReq = null;
                    if (row.getCell(0) != null) {
                        childReqId = Long.parseLong(df.formatCellValue(row.getCell(0)));
                        childReq = reqDao.findOne(childReqId);
                    } else {
                        childReq = new EcRequirement();
                        childReq.setParentId(reqObj);
                    }
                    String rName = validateInput(df.formatCellValue(row.getCell(1)), 90);
                    String rPriority = validateInput(df.formatCellValue(row.getCell(2)), 45);
                    if (!BizUtil.INSTANCE.checkReqPriority(rPriority)) {
                        priorityError = true;
                        continue;
                    }
                    String rStatus = validateInput(df.formatCellValue(row.getCell(3)), 45);
                    if (!BizUtil.INSTANCE.checkReqStatus(rStatus)) {
                        statusError = true;
                        continue;
                    }

                    String rRelease = validateInput(df.formatCellValue(row.getCell(4)), 45);
                    String rProduct = validateInput(df.formatCellValue(row.getCell(5)), 45);
                    String rCoverage = validateInput(df.formatCellValue(row.getCell(6)), 5);
                    String rStory = validateInput(df.formatCellValue(row.getCell(4)), -1);

                    childReq.setName(rName);
                    childReq.setPriority(rPriority);
                    childReq.setStatus(rStatus);
                    childReq.setReleaseName(rRelease);
                    childReq.setProduct(rProduct);
                    childReq.setCoverage(Boolean.valueOf(rCoverage));
                    if (childReq.getStory() != null) {
                        if (!childReq.getStory().equals(rStory)) {
                            //Story changed then mark all testcases for review.
                            tcrDao.updateAllLinkedTestcaseForReview(reqId);
                        }
                    }
                    childReq.setStory(rStory);

                    reqDao.save(childReq);
                    historyUtil.addHistory("Requirement [" + childReq.getId() + ":" + childReq.getName() + "] added/updated by import, file: " + fileName, session, request);
                }
                historyUtil.addHistory("Uploaded requirements file: " + fileName + " to requirement [" + reqObj.getId() + ":" + reqObj.getName() + "]", session, request);

                if (statusError) {
                    session.setAttribute("flashMsg", "Invalid Status codes found in :" + fileName);
                } else if (priorityError) {
                    session.setAttribute("flashMsg", "Invalid Priority codes found in :" + fileName);
                } else {
                    session.setAttribute("flashMsg", "Successfully Imported :" + fileName);
                }

            } catch (Exception ex) {
                session.setAttribute("flashMsg", "File upload failed! " + ex.getMessage());
                ex.printStackTrace();
            }
        } else {
            session.setAttribute("flashMsg", "File is empty!");
        }
        return "redirect:/requirement?reqId=" + reqId;
    }

    public String validateInput(String value, Integer length) {
        if (value == null || value.isEmpty()) {
            return " ";
        }
        if (length > 0 && value.length() > length) {
            value = value.substring(0, length);
        }
        return value;
    }
}
