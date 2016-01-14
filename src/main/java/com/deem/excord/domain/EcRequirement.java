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
    @Lob
    @Size(max = 65535)
    @Column(name = "story")
    private String story;
    @Size(max = 45)
    @Column(name = "priority")
    private String priority;
    @Size(max = 45)
    @Column(name = "state")
    private String state;
    @Column(name = "coverage")
    private Boolean coverage;
    @Size(max = 90)
    @Column(name = "release")
    private String release;
    @Size(max = 90)
    @Column(name = "product")
    private String product;
    @OneToMany(mappedBy = "parentId")
    private List<EcRequirement> ecRequirementCollection;
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    @ManyToOne
    private EcRequirement parentId;
    @OneToMany(mappedBy = "requirementId")
    private List<EcTestcaseRequirementMapping> ecTestcaseRequirementMappingCollection;

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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Boolean getCoverage() {
        return coverage;
    }

    public void setCoverage(Boolean coverage) {
        this.coverage = coverage;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public List<EcRequirement> getEcRequirementCollection() {
        return ecRequirementCollection;
    }

    public void setEcRequirementCollection(List<EcRequirement> ecRequirementCollection) {
        this.ecRequirementCollection = ecRequirementCollection;
    }

    public EcRequirement getParentId() {
        return parentId;
    }

    public void setParentId(EcRequirement parentId) {
        this.parentId = parentId;
    }

    public List<EcTestcaseRequirementMapping> getEcTestcaseRequirementMappingCollection() {
        return ecTestcaseRequirementMappingCollection;
    }

    public void setEcTestcaseRequirementMappingCollection(List<EcTestcaseRequirementMapping> ecTestcaseRequirementMappingCollection) {
        this.ecTestcaseRequirementMappingCollection = ecTestcaseRequirementMappingCollection;
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
