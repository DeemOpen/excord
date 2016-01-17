package com.deem.excord.domain;

import java.io.Serializable;
import java.util.List;
import java.util.SortedSet;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "ec_testcase")
public class EcTestcase implements Serializable {

    @OneToMany(mappedBy = "testcaseId")
    private List<EcTestcaseRequirementMapping> ecTestcaseRequirementMappingCollection;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 90)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 2147483647)
    @Column(name = "description")
    private String description;
    @Basic(optional = false)
    @NotNull
    @Column(name = "enabled")
    private boolean enabled;
    @Basic(optional = false)
    @NotNull
    @Column(name = "automated")
    private boolean automated;
    @Size(max = 45)
    @Column(name = "added_version")
    private String addedVersion;
    @Size(max = 45)
    @Column(name = "deprecated_version")
    private String deprecatedVersion;
    @Size(max = 45)
    @Column(name = "bug_id")
    private String bugId;
    @Size(max = 45)
    @Column(name = "language")
    private String language;
    @Size(max = 90)
    @Column(name = "test_script_file")
    private String testScriptFile;
    @Size(max = 45)
    @Column(name = "method_name")
    private String methodName;
    @Basic(optional = false)
    @Size(min = 1, max = 10)
    @Column(name = "priority")
    private String priority;
    @Size(max = 45)
    @Column(name = "product")
    private String product;
    @Size(max = 45)
    @Column(name = "feature")
    private String feature;
    @Basic(optional = false)
    @Size(min = 1, max = 45)
    @Column(name = "case_type")
    private String caseType;
    @Column(name = "time_to_run")
    private Integer timeToRun;
    @JoinColumn(name = "folder_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private EcTestfolder folderId;
    @OneToMany(mappedBy = "testcaseId")
    private List<EcTestupload> ecTestuploadList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "testcaseId")
    @OrderBy("stepNumber ASC")
    private SortedSet<EcTeststep> ecTeststepList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "testcaseId")
    private List<EcTestplanTestcaseMapping> ecTestplanTestcaseMappingList;

    public EcTestcase() {
    }

    public EcTestcase(Long id) {
        this.id = id;
    }

    public EcTestcase(Long id, String name, String description, boolean enabled, boolean automated) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.enabled = enabled;
        this.automated = automated;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean getAutomated() {
        return automated;
    }

    public void setAutomated(boolean automated) {
        this.automated = automated;
    }

    public String getAddedVersion() {
        return addedVersion;
    }

    public void setAddedVersion(String addedVersion) {
        this.addedVersion = addedVersion;
    }

    public String getDeprecatedVersion() {
        return deprecatedVersion;
    }

    public void setDeprecatedVersion(String deprecatedVersion) {
        this.deprecatedVersion = deprecatedVersion;
    }

    public String getBugId() {
        return bugId;
    }

    public void setBugId(String bugId) {
        this.bugId = bugId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTestScriptFile() {
        return testScriptFile;
    }

    public void setTestScriptFile(String testScriptFile) {
        this.testScriptFile = testScriptFile;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getCaseType() {
        return caseType;
    }

    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }

    public Integer getTimeToRun() {
        return timeToRun;
    }

    public void setTimeToRun(Integer timeToRun) {
        this.timeToRun = timeToRun;
    }

    public EcTestfolder getFolderId() {
        return folderId;
    }

    public void setFolderId(EcTestfolder folderId) {
        this.folderId = folderId;
    }

    public List<EcTestupload> getEcTestuploadList() {
        return ecTestuploadList;
    }

    public void setEcTestuploadList(List<EcTestupload> ecTestuploadList) {
        this.ecTestuploadList = ecTestuploadList;
    }

    public SortedSet<EcTeststep> getEcTeststepList() {
        return ecTeststepList;
    }

    public void setEcTeststepList(SortedSet<EcTeststep> ecTeststepList) {
        this.ecTeststepList = ecTeststepList;
    }

    public List<EcTestplanTestcaseMapping> getEcTestplanTestcaseMappingList() {
        return ecTestplanTestcaseMappingList;
    }

    public void setEcTestplanTestcaseMappingList(List<EcTestplanTestcaseMapping> ecTestplanTestcaseMappingList) {
        this.ecTestplanTestcaseMappingList = ecTestplanTestcaseMappingList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EcTestcase)) {
            return false;
        }
        EcTestcase other = (EcTestcase) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.deem.excord.domain.EcTestcase[ id=" + id + " ]";
    }

    public List<EcTestcaseRequirementMapping> getEcTestcaseRequirementMappingCollection() {
        return ecTestcaseRequirementMappingCollection;
    }

    public void setEcTestcaseRequirementMappingCollection(List<EcTestcaseRequirementMapping> ecTestcaseRequirementMappingCollection) {
        this.ecTestcaseRequirementMappingCollection = ecTestcaseRequirementMappingCollection;
    }

}
