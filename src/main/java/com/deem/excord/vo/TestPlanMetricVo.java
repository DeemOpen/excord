package com.deem.excord.vo;

public class TestPlanMetricVo {

    private Long testPlanId;
    private String testPlan;
    private Long folderId;
    private String folderName;
    private Integer passCount;
    private Integer failCount;
    private Integer naCount;
    private Integer blockedCount;
    private Integer futureCount;
    private Integer notcompleteCount;
    private Integer notrunCount;
    private Integer total;
    private Long passRate;
    private Integer progressCount;
    private Long progressRate;
    private String product;

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }
    
    public Long getTestPlanId() {
        return testPlanId;
    }

    public void setTestPlanId(Long testPlanId) {
        this.testPlanId = testPlanId;
    }

    public String getTestPlan() {
        return testPlan;
    }

    public void setTestPlan(String testPlan) {
        this.testPlan = testPlan;
    }

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public Integer getPassCount() {
        return passCount;
    }

    public void setPassCount(Integer passCount) {
        this.passCount = passCount;
    }

    public Integer getFailCount() {
        return failCount;
    }

    public void setFailCount(Integer failCount) {
        this.failCount = failCount;
    }

    public Integer getNaCount() {
        return naCount;
    }

    public void setNaCount(Integer naCount) {
        this.naCount = naCount;
    }

    public Integer getBlockedCount() {
        return blockedCount;
    }

    public void setBlockedCount(Integer blockedCount) {
        this.blockedCount = blockedCount;
    }

    public Integer getFutureCount() {
        return futureCount;
    }

    public void setFutureCount(Integer futureCount) {
        this.futureCount = futureCount;
    }

    public Integer getNotcompleteCount() {
        return notcompleteCount;
    }

    public void setNotcompleteCount(Integer notcompleteCount) {
        this.notcompleteCount = notcompleteCount;
    }

    public Integer getNotrunCount() {
        return notrunCount;
    }

    public void setNotrunCount(Integer notrunCount) {
        this.notrunCount = notrunCount;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Long getPassRate() {
        return passRate;
    }

    public void setPassRate(Long passRate) {
        this.passRate = passRate;
    }

    public Integer getProgressCount() {
        return progressCount;
    }

    public void setProgressCount(Integer progressCount) {
        this.progressCount = progressCount;
    }

    public Long getProgressRate() {
        return progressRate;
    }

    public void setProgressRate(Long progressRate) {
        this.progressRate = progressRate;
    }

}
