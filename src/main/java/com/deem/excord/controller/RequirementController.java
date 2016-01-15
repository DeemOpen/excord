package com.deem.excord.controller;

import com.deem.excord.domain.EcRequirement;
import com.deem.excord.domain.EcTestcase;
import com.deem.excord.domain.EcTestcaseRequirementMapping;
import com.deem.excord.repository.RequirementRepository;
import com.deem.excord.repository.TestCaseRepository;
import com.deem.excord.repository.TestcaseRequirementRepository;
import com.deem.excord.util.FlashMsgUtil;
import com.deem.excord.util.HistoryUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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

    @RequestMapping(value = "/requirement", method = RequestMethod.POST)
    public String requirementTestCaseLink(Model model, HttpServletRequest request, HttpSession session, @RequestParam(value = "reqId", required = true) Long reqId, @RequestParam(value = "action", required = true) String action) {

        if (action.equals("testcase_requirement_link")) {
            return linkTestcaseRequirement(model, request, session, reqId, action);
        } else if (action.equals("testcase_requirement_link_cancel")) {
            return unLinkTestcaseRequirement(model, request, session, reqId, action);
        } else if (action.equals("req_create")) {
            return createRequirement(model, request, session, reqId, action);
        } else if (action.equals("req_edit")) {
            return editRequirement(model, request, session, reqId, action);
        } else if (action.equals("req_delete")) {
            return deleteRequirement(model, request, session, reqId, action);
        } else {
            return "redirect:/requirement";
        }

    }

    public String linkTestcaseRequirement(Model model, HttpServletRequest request, HttpSession session, Long reqId, String action) {
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

    public String unLinkTestcaseRequirement(Model model, HttpServletRequest request, HttpSession session, Long reqId, String action) {
        Long nodeId = (Long) session.getAttribute("clipboardNodeId");
        session.setAttribute("clipboardLinkTc", null);
        session.setAttribute("clipboardNodeId", null);
        session.setAttribute("flashMsg", "Linking cancelled!");
        return "redirect:/testcase?nodeId=" + nodeId;
    }

    public String createRequirement(Model model, HttpServletRequest request, HttpSession session, Long reqId, String action) {
        EcRequirement parentReq = reqDao.findOne(reqId);
        model.addAttribute("parentReq", parentReq);
        return "requirement_form";
    }

    public String editRequirement(Model model, HttpServletRequest request, HttpSession session, Long reqId, String action) {
        EcRequirement req = reqDao.findOne(reqId);
        model.addAttribute("req", req);
        model.addAttribute("parentReq", req.getParentId());
        return "requirement_form";
    }

    public String deleteRequirement(Model model, HttpServletRequest request, HttpSession session, Long reqId, String action) {
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
            reqDao.delete(req);
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

        EcRequirement parentReq = reqDao.findOne(rParentId);
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

        historyUtil.addHistory("Saved requirement: [" + rname + "] under [" + parentReq.getId() + ":" + parentReq.getName() + "]", session, request);
        session.setAttribute("flashMsg", "Successfully saved requirement: " + rname);

        return "redirect:/requirement?reqId=" + req.getId();
    }

}
