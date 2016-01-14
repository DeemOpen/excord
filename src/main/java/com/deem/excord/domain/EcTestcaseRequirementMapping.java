package com.deem.excord.domain;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "ec_testcase_requirement_mapping")
public class EcTestcaseRequirementMapping implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @JoinColumn(name = "requirement_id", referencedColumnName = "id")
    @ManyToOne
    private EcRequirement requirementId;
    @JoinColumn(name = "testcase_id", referencedColumnName = "id")
    @ManyToOne
    private EcTestcase testcaseId;
    @Column(name = "review")
    private Boolean review;

    public EcTestcaseRequirementMapping() {
    }

    public EcTestcaseRequirementMapping(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EcRequirement getRequirementId() {
        return requirementId;
    }

    public void setRequirementId(EcRequirement requirementId) {
        this.requirementId = requirementId;
    }

    public EcTestcase getTestcaseId() {
        return testcaseId;
    }

    public void setTestcaseId(EcTestcase testcaseId) {
        this.testcaseId = testcaseId;
    }

    public Boolean getReview() {
        return review;
    }

    public void setReview(Boolean review) {
        this.review = review;
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
        if (!(object instanceof EcTestcaseRequirementMapping)) {
            return false;
        }
        EcTestcaseRequirementMapping other = (EcTestcaseRequirementMapping) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.deem.excord.domain.EcTestcaseRequirementMapping[ id=" + id + " ]";
    }

}
