package com.seekersoftvendingapp.database.table;

import org.greenrobot.greendao.annotation.*;
import java.io.Serializable;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit.

/**
 * Entity mapped to table "EMP_CARD".
 */
@Entity
public class EmpCard implements Serializable {

    @NotNull
    private String card;
    private String emp;
    private Boolean isDel;
    private String createdAt;
    private String updatedAt;
    private String objectId;
    private String keepone;
    private String keeptwo;
    private String keepthree;

    @Generated
    public EmpCard() {
    }

    @Generated
    public EmpCard(String card, String emp, Boolean isDel, String createdAt, String updatedAt, String objectId, String keepone, String keeptwo, String keepthree) {
        this.card = card;
        this.emp = emp;
        this.isDel = isDel;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.objectId = objectId;
        this.keepone = keepone;
        this.keeptwo = keeptwo;
        this.keepthree = keepthree;
    }

    @NotNull
    public String getCard() {
        return card;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setCard(@NotNull String card) {
        this.card = card;
    }

    public String getEmp() {
        return emp;
    }

    public void setEmp(String emp) {
        this.emp = emp;
    }

    public Boolean getIsDel() {
        return isDel;
    }

    public void setIsDel(Boolean isDel) {
        this.isDel = isDel;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getKeepone() {
        return keepone;
    }

    public void setKeepone(String keepone) {
        this.keepone = keepone;
    }

    public String getKeeptwo() {
        return keeptwo;
    }

    public void setKeeptwo(String keeptwo) {
        this.keeptwo = keeptwo;
    }

    public String getKeepthree() {
        return keepthree;
    }

    public void setKeepthree(String keepthree) {
        this.keepthree = keepthree;
    }

}
