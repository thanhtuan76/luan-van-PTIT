/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.yenthanh;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Admin
 */
@Entity
@Table(name = "thanhresults")
//@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Thanhresults.findAll", query = "SELECT t FROM Thanhresults t")
    , @NamedQuery(name = "Thanhresults.findById", query = "SELECT t FROM Thanhresults t WHERE t.id = :id")
    , @NamedQuery(name = "Thanhresults.findByAlgoName", query = "SELECT t FROM Thanhresults t WHERE t.algoName = :algoName")
    , @NamedQuery(name = "Thanhresults.findByNoCloudlets", query = "SELECT t FROM Thanhresults t WHERE t.noCloudlets = :noCloudlets")
    , @NamedQuery(name = "Thanhresults.findByAllocationType", query = "SELECT t FROM Thanhresults t WHERE t.allocationType = :allocationType")
    , @NamedQuery(name = "Thanhresults.findByAvgExecTime", query = "SELECT t FROM Thanhresults t WHERE t.avgExecTime = :avgExecTime")
    , @NamedQuery(name = "Thanhresults.findByAvgRespTime", query = "SELECT t FROM Thanhresults t WHERE t.avgRespTime = :avgRespTime")
    , @NamedQuery(name = "Thanhresults.findByRunTime", query = "SELECT t FROM Thanhresults t WHERE t.runTime = :runTime")})
public class Thanhresults implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "AlgoName")
    private String algoName;
    @Column(name = "noCloudlets")
    private BigInteger noCloudlets;
    @Column(name = "AllocationType")
    private String allocationType;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "AvgExecTime")
    private BigDecimal avgExecTime;
    @Column(name = "AvgRespTime")
    private BigDecimal avgRespTime;
    @Column(name = "RunTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date runTime;

    public Thanhresults() {
    }

    public Thanhresults(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAlgoName() {
        return algoName;
    }

    public void setAlgoName(String algoName) {
        this.algoName = algoName;
    }

    public BigInteger getNoCloudlets() {
        return noCloudlets;
    }

    public void setNoCloudlets(BigInteger noCloudlets) {
        this.noCloudlets = noCloudlets;
    }

    public String getAllocationType() {
        return allocationType;
    }

    public void setAllocationType(String allocationType) {
        this.allocationType = allocationType;
    }

    public BigDecimal getAvgExecTime() {
        return avgExecTime;
    }

    public void setAvgExecTime(BigDecimal avgExecTime) {
        this.avgExecTime = avgExecTime;
    }

    public BigDecimal getAvgRespTime() {
        return avgRespTime;
    }

    public void setAvgRespTime(BigDecimal avgRespTime) {
        this.avgRespTime = avgRespTime;
    }

    public Date getRunTime() {
        return runTime;
    }

    public void setRunTime(Date runTime) {
        this.runTime = runTime;
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
        if (!(object instanceof Thanhresults)) {
            return false;
        }
        Thanhresults other = (Thanhresults) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.yenthanh.Thanhresults[ id=" + id + " ]";
    }
    
}
