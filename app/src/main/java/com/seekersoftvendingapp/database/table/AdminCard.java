package com.seekersoftvendingapp.database.table;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

import java.io.Serializable;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit.

/**
 * Entity mapped to table "ADMIN_CARD".
 */
@Entity
public class AdminCard implements Serializable {
    private Boolean isDel;

    @NotNull
    private String card;

    @Id
    private String objectId;
    private java.util.Date createdAt;
    private java.util.Date updatedAt;
    private String keepone;
    private String keeptwo;
    private String keepthree;

    @Generated
    public AdminCard() {
    }

    public AdminCard(String objectId) {
        this.objectId = objectId;
    }

    @Generated
    public AdminCard(Boolean isDel, String card, String objectId, java.util.Date createdAt, java.util.Date updatedAt, String keepone, String keeptwo, String keepthree) {
        this.isDel = isDel;
        this.card = card;
        this.objectId = objectId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.keepone = keepone;
        this.keeptwo = keeptwo;
        this.keepthree = keepthree;
    }

    public Boolean getIsDel() {
        return isDel;
    }

    public void setIsDel(Boolean isDel) {
        this.isDel = isDel;
    }

    @NotNull
    public String getCard() {
        return card;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setCard(@NotNull String card) {
        this.card = card;
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
