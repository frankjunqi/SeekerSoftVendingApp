package com.seekersoftvendingapp.database.table;

import org.greenrobot.greendao.annotation.*;
import java.io.Serializable;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit.

/**
 * Entity mapped to table "PASSAGE".
 */
@Entity
public class Passage implements Serializable {
    private String flag;
    private Boolean isDel;
    private Integer capacity;
    private String product;
    private String seqNo;
    private Boolean borrowState;
    private String borrowUser;
    private Integer stock;
    private Integer whorlSize;
    private Boolean isSend;

    @Id
    private String objectId;
    private java.util.Date createdAt;
    private java.util.Date updatedAt;
    private String keepone;
    private String keeptwo;
    private String keepthree;

    @Generated
    public Passage() {
    }

    public Passage(String objectId) {
        this.objectId = objectId;
    }

    @Generated
    public Passage(String flag, Boolean isDel, Integer capacity, String product, String seqNo, Boolean borrowState, String borrowUser, Integer stock, Integer whorlSize, Boolean isSend, String objectId, java.util.Date createdAt, java.util.Date updatedAt, String keepone, String keeptwo, String keepthree) {
        this.flag = flag;
        this.isDel = isDel;
        this.capacity = capacity;
        this.product = product;
        this.seqNo = seqNo;
        this.borrowState = borrowState;
        this.borrowUser = borrowUser;
        this.stock = stock;
        this.whorlSize = whorlSize;
        this.isSend = isSend;
        this.objectId = objectId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.keepone = keepone;
        this.keeptwo = keeptwo;
        this.keepthree = keepthree;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public Boolean getIsDel() {
        return isDel;
    }

    public void setIsDel(Boolean isDel) {
        this.isDel = isDel;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(String seqNo) {
        this.seqNo = seqNo;
    }

    public Boolean getBorrowState() {
        return borrowState;
    }

    public void setBorrowState(Boolean borrowState) {
        this.borrowState = borrowState;
    }

    public String getBorrowUser() {
        return borrowUser;
    }

    public void setBorrowUser(String borrowUser) {
        this.borrowUser = borrowUser;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getWhorlSize() {
        return whorlSize;
    }

    public void setWhorlSize(Integer whorlSize) {
        this.whorlSize = whorlSize;
    }

    public Boolean getIsSend() {
        return isSend;
    }

    public void setIsSend(Boolean isSend) {
        this.isSend = isSend;
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
