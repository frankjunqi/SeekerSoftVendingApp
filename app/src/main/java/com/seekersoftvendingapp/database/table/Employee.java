package com.seekersoftvendingapp.database.table;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit.

/**
 * Entity mapped to table "EMPLOYEE".
 */
@Entity
public class Employee {
    private Boolean isDel;

    @NotNull
    private String empNo;
    private String card;
    private String power;

    @Id
    private String objectId;
    private java.util.Date createdAt;
    private java.util.Date updatedAt;

    @Generated
    public Employee() {
    }

    public Employee(String objectId) {
        this.objectId = objectId;
    }

    @Generated
    public Employee(Boolean isDel, String empNo, String card, String power, String objectId, java.util.Date createdAt, java.util.Date updatedAt) {
        this.isDel = isDel;
        this.empNo = empNo;
        this.card = card;
        this.power = power;
        this.objectId = objectId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Boolean getIsDel() {
        return isDel;
    }

    public void setIsDel(Boolean isDel) {
        this.isDel = isDel;
    }

    @NotNull
    public String getEmpNo() {
        return empNo;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setEmpNo(@NotNull String empNo) {
        this.empNo = empNo;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
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