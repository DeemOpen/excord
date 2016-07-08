package com.deem.excord.util;

import com.deem.excord.domain.EcRequirement;
import com.deem.excord.domain.EcTestcase;
import com.deem.excord.domain.EcTestfolder;
import com.deem.excord.domain.EcTestplan;
import com.deem.excord.domain.EcTestplanTestcaseMapping;
import com.deem.excord.vo.TestPlanMetricVo;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum BizUtil {

    INSTANCE;
    private static final Logger LOGGER = LoggerFactory.getLogger(BizUtil.class);

    public String getSlug() {
        return UUID.randomUUID().toString();
    }

    public List<TestPlanMetricVo> flattenTestPlanMetricsByFolder(List<Object[]> tmLst) {
        Map<Long, TestPlanMetricVo> tmMap = new HashMap<Long, TestPlanMetricVo>();
        for (Object[] result : tmLst) {
            Long folderId = ((BigInteger) result[0]).longValue();
            String status = (String) result[2];
            if (status == null) {
                status = Constants.STATUS_NOT_RUN;
            }
            TestPlanMetricVo tpm = tmMap.get(folderId);
            if (tpm == null) {
                tpm = new TestPlanMetricVo();
                tpm.setFolderId(folderId);
                tpm.setFolderName((String) result[1]);
                tpm.setPassCount(0);
                tpm.setFailCount(0);
                tpm.setNaCount(0);
                tpm.setBlockedCount(0);
                tpm.setFutureCount(0);
                tpm.setNotcompleteCount(0);
                tpm.setNotrunCount(0);
                tpm.setTotal(0);
            }

            Integer count = 0;
            if (result[3] != null) {
                count = ((BigInteger) result[3]).intValue();
            }
            tpm.setTotal(tpm.getTotal() + count);
            switch (status) {
                case Constants.STATUS_PASSED:
                    tpm.setPassCount(count);
                    break;
                case Constants.STATUS_FAILED:
                    tpm.setFailCount(count);
                    break;
                case Constants.STATUS_BLOCKED:
                    tpm.setBlockedCount(count);
                    break;
                case Constants.STATUS_NOT_COMPLETED:
                    tpm.setNotcompleteCount(count);
                    break;
                default:
                    tpm.setNotrunCount(tpm.getNotrunCount() + count);
                    break;
            }
            tmMap.put(folderId, tpm);

        }

        List<TestPlanMetricVo> finalMetricLst = new ArrayList<TestPlanMetricVo>();
        for (Map.Entry<Long, TestPlanMetricVo> entry : tmMap.entrySet()) {
            TestPlanMetricVo value = entry.getValue();
            value.setPassRate(Math.round((value.getPassCount() * 100.0) / value.getTotal()));
            value.setProgressRate(Math.round(((value.getTotal() - value.getNotrunCount()) * 100.0) / value.getTotal()));
            finalMetricLst.add(value);
        }
        return finalMetricLst;
    }

    public List<TestPlanMetricVo> flattenTestPlanMetricsByProduct(List<Object[]> tmLst) {
        Map<String, TestPlanMetricVo> tmMap = new HashMap<String, TestPlanMetricVo>();
        for (Object[] result : tmLst) {
            String product = (String) result[0];
            String status = (String) result[1];

            if (status == null) {
                status = Constants.STATUS_NOT_RUN;
            }
            TestPlanMetricVo tpm = tmMap.get(product);
            if (tpm == null) {
                tpm = new TestPlanMetricVo();
                tpm.setProduct(product);
                tpm.setPassCount(0);
                tpm.setFailCount(0);
                tpm.setNaCount(0);
                tpm.setBlockedCount(0);
                tpm.setFutureCount(0);
                tpm.setNotcompleteCount(0);
                tpm.setNotrunCount(0);
                tpm.setTotal(0);
            }

            Integer count = 0;
            if (result[2] != null) {
                count = ((BigInteger) result[2]).intValue();
            }
            tpm.setTotal(tpm.getTotal() + count);
            switch (status) {
                case Constants.STATUS_PASSED:
                    tpm.setPassCount(count);
                    break;
                case Constants.STATUS_FAILED:
                    tpm.setFailCount(count);
                    break;
                case Constants.STATUS_BLOCKED:
                    tpm.setBlockedCount(count);
                    break;
                case Constants.STATUS_NOT_COMPLETED:
                    tpm.setNotcompleteCount(count);
                    break;
                default:
                    tpm.setNotrunCount(tpm.getNotrunCount() + count);
                    break;
            }
            tmMap.put(product, tpm);

        }

        List<TestPlanMetricVo> finalMetricLst = new ArrayList<TestPlanMetricVo>();
        for (Map.Entry<String, TestPlanMetricVo> entry : tmMap.entrySet()) {
            TestPlanMetricVo value = entry.getValue();
            value.setPassRate(Math.round((value.getPassCount() * 100.0) / value.getTotal()));
            value.setProgressRate(Math.round(((value.getTotal() - value.getNotrunCount()) * 100.0) / value.getTotal()));
            finalMetricLst.add(value);
        }
        return finalMetricLst;
    }

    public List<TestPlanMetricVo> gererateTestplanMetrics(EcTestplan tp, List<Object[]> metricsLst) {
        List<TestPlanMetricVo> resultLst = new ArrayList<TestPlanMetricVo>();
        TestPlanMetricVo tpm = new TestPlanMetricVo();
        tpm.setTestPlanId(tp.getId());
        tpm.setPassCount(0);
        tpm.setProgressCount(0);
        tpm.setTotal(0);
        for (Object[] result : metricsLst) {
            Long tpId = ((BigInteger) result[0]).longValue();
            String tpStatus = (String) (result[1]);
            Integer tpCount = ((BigInteger) (result[2])).intValue();
            if (tpId.equals(tp.getId())) {
                switch (tpStatus) {
                    case Constants.STATUS_PASSED:
                        tpm.setPassCount(tpCount);
                        break;
                    case "TOTAL":
                        tpm.setTotal(tpCount);
                        break;
                    case "RUN":
                        tpm.setProgressCount(tpCount);
                        break;
                }
            }
        }
        LOGGER.debug("PlanId:{},Pass:{},Total:{},Progress:{}", tp.getId(), tpm.getPassCount(), tpm.getTotal(), tpm.getProgressCount());
        tpm.setPassRate(Math.round((tpm.getPassCount() * 100.0) / tpm.getTotal()));
        tpm.setProgressRate(Math.round((tpm.getProgressCount() * 100.0) / tpm.getTotal()));
        resultLst.add(tpm);
        return resultLst;
    }

    public List<TestPlanMetricVo> gererateAllTestPlanMetrics(List<EcTestplan> testPlanLst, List<Object[]> metricsLst) {
        List<TestPlanMetricVo> resultLst = new ArrayList<TestPlanMetricVo>();
        Map<Long, TestPlanMetricVo> mapObj = new HashMap<Long, TestPlanMetricVo>();
        for (EcTestplan tp : testPlanLst) {
            TestPlanMetricVo tpm = new TestPlanMetricVo();
            tpm.setTestPlanId(tp.getId());
            tpm.setPassCount(0);
            tpm.setProgressCount(0);
            tpm.setTotal(0);
            mapObj.put(tp.getId(), tpm);
        }

        for (Object[] result : metricsLst) {
            Long tpId = ((BigInteger) result[0]).longValue();
            String tpStatus = (String) (result[1]);
            Integer tpCount = ((BigInteger) (result[2])).intValue();
            TestPlanMetricVo tpm = mapObj.get(tpId);
            if (tpm != null) {
                switch (tpStatus) {
                    case Constants.STATUS_PASSED:
                        tpm.setPassCount(tpCount);
                        break;
                    case Constants.STATUS_NOT_RUN:
                        break;
                    case "TOTAL":
                        tpm.setTotal(tpCount);
                        break;
                    case "RUN":
                        tpm.setProgressCount(tpCount);
                        break;
                }
            }
        }
        for (Map.Entry<Long, TestPlanMetricVo> entry : mapObj.entrySet()) {
            TestPlanMetricVo tpm = entry.getValue();
            tpm.setPassRate(Math.round((tpm.getPassCount() * 100.0) / tpm.getTotal()));
            tpm.setProgressRate(Math.round((tpm.getProgressCount() * 100.0) / tpm.getTotal()));
            resultLst.add(tpm);
        }
        return resultLst;
    }

    public Boolean checkStatus(String tstatus) {
        switch (tstatus) {
            case Constants.STATUS_PASSED:
            case Constants.STATUS_BLOCKED:
            case Constants.STATUS_FAILED:
            case Constants.STATUS_NOT_COMPLETED:
            case Constants.STATUS_NOT_RUN:
                return true;
            default:
                return false;
        }

    }

    public Boolean checkReqPriority(String rPriority) {
        switch (rPriority) {
            case Constants.PRIORITY_P1:
            case Constants.PRIORITY_P2:
            case Constants.PRIORITY_P3:
            case Constants.PRIORITY_P4:
                return true;
            default:
                return false;
        }
    }

    public Boolean checkReqStatus(String rStatus) {
        switch (rStatus) {
            case Constants.REQ_STATUS_ACTIVE:
            case Constants.REQ_STATUS_DEPRECATED:
            case Constants.REQ_STATUS_PROPOSED:
                return true;
            default:
                return false;
        }

    }

    public Boolean checkEnv(String tenv) {
        switch (tenv) {

            case Constants.STATUS_PASSED:
            case Constants.STATUS_BLOCKED:
            case Constants.STATUS_FAILED:
            case Constants.STATUS_NOT_COMPLETED:
            case Constants.STATUS_NOT_RUN:
                return true;
            default:
                return false;
        }

    }

    public List<Long> getListOfAllChildReq(EcRequirement currReq) {
        if (currReq.getEcRequirementList().isEmpty()) {
            return null;
        } else {
            List<Long> allReqIdLst = new ArrayList<Long>();
            for (EcRequirement req : currReq.getEcRequirementList()) {
                if (req.getCoverage()) {
                    allReqIdLst.add(req.getId());
                }
                List<Long> nestedChildReqLst = getListOfAllChildReq(req);
                if (nestedChildReqLst != null) {
                    allReqIdLst.addAll(nestedChildReqLst);
                }
            }
            return allReqIdLst;
        }
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

    public Map<String, String> getFolderHierarchyMap(EcTestplan testPlan) {

        Map<String, String> map = new TreeMap<>();
        List<EcTestplanTestcaseMapping> testCaseMappings = testPlan.getEcTestplanTestcaseMappingList();
        for (EcTestplanTestcaseMapping testCaseMapping : testCaseMappings) {
            EcTestfolder folder = testCaseMapping.getTestcaseId().getFolderId();

            StringBuilder pathBuilder = new StringBuilder();
            pathBuilder.append(Constants.PATH_DELIMITER);
            List<EcTestfolder> pFolders = folder.getAllParentFolderList();
            Collections.reverse(pFolders);
            for (EcTestfolder pfolder : pFolders) {
                pathBuilder.append(pfolder.getName());
                pathBuilder.append(Constants.PATH_DELIMITER);
            }
            pathBuilder.append(folder.getName());
            map.put(folder.getId().toString(), pathBuilder.toString());
        }
        return map;

    }
}
