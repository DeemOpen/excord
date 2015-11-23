package com.deem.excord.domain;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "ec_teststep")
public class EcTeststep implements Serializable, Comparable<EcTeststep> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "step_number")
    private Integer stepNumber;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 2147483647)
    @Column(name = "description")
    private String description;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 2147483647)
    @Column(name = "expected")
    private String expected;
    @JoinColumn(name = "testcase_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private EcTestcase testcaseId;

    public EcTeststep() {
    }

    public EcTeststep(Long id) {
        this.id = id;
    }

    public EcTeststep(Long id, Integer stepNumber, String description, String expected) {
        this.id = id;
        this.stepNumber = stepNumber;
        this.description = description;
        this.expected = expected;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(Integer stepNumber) {
        this.stepNumber = stepNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExpected() {
        return expected;
    }

    public void setExpected(String expected) {
        this.expected = expected;
    }

    public EcTestcase getTestcaseId() {
        return testcaseId;
    }

    public void setTestcaseId(EcTestcase testcaseId) {
        this.testcaseId = testcaseId;
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
        if (!(object instanceof EcTeststep)) {
            return false;
        }
        EcTeststep other = (EcTeststep) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.deem.excord.domain.EcTeststep[ id=" + id + " ]";
    }

    @Override
    public int compareTo(EcTeststep obj) {
        return this.stepNumber.compareTo(obj.stepNumber);
    }

}
