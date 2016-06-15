package com.deem.excord.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "ec_testfolder")
public class EcTestfolder implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "name")
    private String name;
    @Size(max = 45)
    @Column(name = "slug")
    private String slug;
    @OneToMany(mappedBy = "parentId")
    private List<EcTestfolder> ecTestfolderList;
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    @ManyToOne
    private EcTestfolder parentId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "folderId")
    private List<EcTestcase> ecTestcaseList;

    public EcTestfolder() {
    }

    public EcTestfolder(Long id) {
        this.id = id;
    }

    public EcTestfolder(Long id, String name) {
        this.id = id;
        this.name = name;
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

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public List<EcTestfolder> getEcTestfolderList() {
        return ecTestfolderList;
    }

    public void setEcTestfolderList(List<EcTestfolder> ecTestfolderList) {
        this.ecTestfolderList = ecTestfolderList;
    }

    public EcTestfolder getParentId() {
        return parentId;
    }

    public void setParentId(EcTestfolder parentId) {
        this.parentId = parentId;
    }

    public List<EcTestcase> getEcTestcaseList() {
        return ecTestcaseList;
    }

    public void setEcTestcaseList(List<EcTestcase> ecTestcaseList) {
        this.ecTestcaseList = ecTestcaseList;
    }
      public List<EcTestfolder> getAllParentFolderList() {
        List<EcTestfolder> parentFolderList = new ArrayList<>();
        if (parentId != null) {
            parentFolderList.add(parentId);
            parentFolderList.addAll(parentId.getAllParentFolderList());
        } 
        return parentFolderList;
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
        if (!(object instanceof EcTestfolder)) {
            return false;
        }
        EcTestfolder other = (EcTestfolder) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.deem.excord.domain.EcTestfolder[ id=" + id + " ]";
    }

}
