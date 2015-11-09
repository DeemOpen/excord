package com.deem.excord.domain;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "ec_testresult")
public class EcTestresult implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "status")
    private String status;
    @Size(max = 90)
    @Column(name = "environment")
    private String environment;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "tester")
    private String tester;
    @Lob
    @Size(max = 2147483647)
    @Column(name = "note")
    private String note;
    @Size(max = 45)
    @Column(name = "bug_ticket")
    private String bugTicket;
    @Basic(optional = false)
    @NotNull
    @Column(name = "latest")
    private boolean latest;
    @JoinColumn(name = "testplan_testcase_link_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private EcTestplanTestcaseMapping testplanTestcaseLinkId;

    public EcTestresult() {
    }

    public EcTestresult(Long id) {
        this.id = id;
    }

    public EcTestresult(Long id, Date timestamp, String status, String tester, boolean latest) {
        this.id = id;
        this.timestamp = timestamp;
        this.status = status;
        this.tester = tester;
        this.latest = latest;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getTester() {
        return tester;
    }

    public void setTester(String tester) {
        this.tester = tester;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getBugTicket() {
        return bugTicket;
    }

    public void setBugTicket(String bugTicket) {
        this.bugTicket = bugTicket;
    }

    public boolean getLatest() {
        return latest;
    }

    public void setLatest(boolean latest) {
        this.latest = latest;
    }

    public EcTestplanTestcaseMapping getTestplanTestcaseLinkId() {
        return testplanTestcaseLinkId;
    }

    public void setTestplanTestcaseLinkId(EcTestplanTestcaseMapping testplanTestcaseLinkId) {
        this.testplanTestcaseLinkId = testplanTestcaseLinkId;
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
        if (!(object instanceof EcTestresult)) {
            return false;
        }
        EcTestresult other = (EcTestresult) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.deem.excord.domain.EcTestresult[ id=" + id + " ]";
    }

}
