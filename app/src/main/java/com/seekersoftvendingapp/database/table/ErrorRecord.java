package com.seekersoftvendingapp.database.table;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit.

/**
 * Entity mapped to table "ERROR_RECORD".
 */
@Entity
public class ErrorRecord {

    @Id
    private Long id;
    private Boolean isFlag;
    private String passage;
    private String card;
    private String node;
    private String info;
    private String time;

    @Generated
    public ErrorRecord() {
    }

    public ErrorRecord(Long id) {
        this.id = id;
    }

    @Generated
    public ErrorRecord(Long id, Boolean isFlag, String passage, String card, String node, String info, String time) {
        this.id = id;
        this.isFlag = isFlag;
        this.passage = passage;
        this.card = card;
        this.node = node;
        this.info = info;
        this.time = time;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getIsFlag() {
        return isFlag;
    }

    public void setIsFlag(Boolean isFlag) {
        this.isFlag = isFlag;
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

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}