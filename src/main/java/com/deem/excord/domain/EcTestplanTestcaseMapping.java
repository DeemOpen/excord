package com.deem.excord.domain;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "ec_testplan_testcase_mapping")
public class EcTestplanTestcaseMapping implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Size(max = 45)
    @Column(name = "assigned_to")
    private String assignedTo;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "testplanTestcaseLinkId")
    private List<EcTestresult> ecTestresultList;
    @JoinColumn(name = "testcase_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private EcTestcase testcaseId;
    @JoinColumn(name = "testplan_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private EcTestplan testplanId;

    public EcTestplanTestcaseMapping() {
    }

    public EcTestplanTestcaseMapping(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public List<EcTestresult> getEcTestresultList() {
        return ecTestresultList;
    }

    public void setEcTestresultList(List<EcTestresult> ecTestresultList) {
        this.ecTestresultList = ecTestresultList;
    }

    public EcTestcase getTestcaseId() {
        return testcaseId;
    }

    public void setTestcaseId(EcTestcase testcaseId) {
        this.testcaseId = testcaseId;
    }

    public EcTestplan getTestplanId() {
        return testplanId;
    }

    public void setTestplanId(EcTestplan testplanId) {
        this.testplanId = testplanId;
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
        if (!(object instanceof EcTestplanTestcaseMapping)) {
            return false;
        }
        EcTestplanTestcaseMapping other = (EcTestplanTestcaseMapping) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.deem.excord.domain.EcTestplanTestcaseMapping[ id=" + id + " ]";
    }

}
