package com.seekersoftvendingapp.database.table;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit.

/**
 * Entity mapped to table "TAKEOUT_RECORD".
 */
@Entity
public class TakeoutRecord implements Serializable {

    @Id
    private Long id;
    private Boolean isDel;
    private String passage;
    private String card;
    private String productId;
    private java.util.Date time;
    private Integer count;
    private String keepone;
    private String keeptwo;
    private String keepthree;

    @Generated
    public TakeoutRecord() {
    }

    public TakeoutRecord(Long id) {
        this.id = id;
    }

    @Generated
    public TakeoutRecord(Long id, Boolean isDel, String passage, String card, String productId, java.util.Date time, Integer count, String keepone, String keeptwo, String keepthree) {
        this.id = id;
        this.isDel = isDel;
        this.passage = passage;
        this.card = card;
        this.productId = productId;
        this.time = time;
        this.count = count;
        this.keepone = keepone;
        this.keeptwo = keeptwo;
        this.keepthree = keepthree;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getIsDel() {
        return isDel;
    }

    public void setIsDel(Boolean isDel) {
        this.isDel = isDel;
    }

    public String getPassage() {
        return passage;
    }

    public void setPassage(String passage) {
        this.passage = passage;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public java.util.Date getTime() {
        return time;
    }

    public void setTime(java.util.Date time) {
        this.time = time;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
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
