package com.seekersoftvendingapp.database.table;

import org.greenrobot.greendao.annotation.*;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit.

/**
 * Entity mapped to table "EMP_POWER".
 */
@Entity
public class EmpPower {

    @Id
    private Long id;
    private String unit;
    private java.util.Date begindate;
    private String productObjectId;
    private Integer count;
    private Integer period;
    private String objectId;
    private java.util.Date createdAt;
    private java.util.Date updatedAt;

    @Generated
    public EmpPower() {
    }

    public EmpPower(Long id) {
        this.id = id;
    }

    @Generated
    public EmpPower(Long id, String unit, java.util.Date begindate, String productObjectId, Integer count, Integer period, String objectId, java.util.Date createdAt, java.util.Date updatedAt) {
        this.id = id;
        this.unit = unit;
        this.begindate = begindate;
        this.productObjectId = productObjectId;
        this.count = count;
        this.period = period;
        this.objectId = objectId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public java.util.Date getBegindate() {
        return begindate;
    }

    public void setBegindate(java.util.Date begindate) {
        this.begindate = begindate;
    }

    public String getProductObjectId() {
        return productObjectId;
    }

    public void setProductObjectId(String productObjectId) {
        this.productObjectId = productObjectId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public java.util.Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.util.Date createdAt) {
        this.createdAt = createdAt;
    }

    public java.util.Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(java.util.Date updatedAt) {
        this.updatedAt = updatedAt;
    }

}
