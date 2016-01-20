package com.deem.excord.domain;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "ec_requirement")
public class EcRequirement implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Size(max = 90)
    @Column(name = "name")
    private String name;
    @Column(name = "story_point")
    private Integer storyPoint;
    @Lob
    @Size(max = 65535)
    @Column(name = "story")
    private String story;
    @Size(max = 45)
    @Column(name = "priority")
    private String priority;
    @Size(max = 45)
    @Column(name = "status")
    private String status;
    @Column(name = "coverage")
    private Boolean coverage;
    @Size(max = 90)
    @Column(name = "release_name")
    private String releaseName;
    @Size(max = 90)
    @Column(name = "product")
    private String product;
    @Size(max = 45)
    @Column(name = "slug")
    private String slug;
    @OneToMany(mappedBy = "parentId")
    private List<EcRequirement> ecRequirementList;
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    @ManyToOne
    private EcRequirement parentId;
    @OneToMany(mappedBy = "requirementId")
    private List<EcTestcaseRequirementMapping> ecTestcaseRequirementMappingList;

    public EcRequirement() {
    }

    public EcRequirement(Long id) {
        this.id = id;
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

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Boolean getCoverage() {
        return coverage;
    }

    public void setCoverage(Boolean coverage) {
        this.coverage = coverage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReleaseName() {
        return releaseName;
    }

    public void setReleaseName(String releaseName) {
        this.releaseName = releaseName;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public EcRequirement getParentId() {
        return parentId;
    }

    public Integer getStoryPoint() {
        return storyPoint;
    }

    public void setStoryPoint(Integer storyPoint) {
        this.storyPoint = storyPoint;
    }

    public void setParentId(EcRequirement parentId) {
        this.parentId = parentId;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public List<EcRequirement> getEcRequirementList() {
        return ecRequirementList;
    }

    public void setEcRequirementList(List<EcRequirement> ecRequirementList) {
        this.ecRequirementList = ecRequirementList;
    }

    public List<EcTestcaseRequirementMapping> getEcTestcaseRequirementMappingList() {
        return ecTestcaseRequirementMappingList;
    }

    public void setEcTestcaseRequirementMappingList(List<EcTestcaseRequirementMapping> ecTestcaseRequirementMappingList) {
        this.ecTestcaseRequirementMappingList = ecTestcaseRequirementMappingList;
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
        if (!(object instanceof EcRequirement)) {
            return false;
        }
        EcRequirement other = (EcRequirement) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.deem.excord.domain.EcRequirement[ id=" + id + " ]";
    }

}
