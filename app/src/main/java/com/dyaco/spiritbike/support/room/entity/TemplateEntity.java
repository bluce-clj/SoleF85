package com.dyaco.spiritbike.support.room.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import com.dyaco.spiritbike.support.room.UserProfileEntity;

import java.io.Serializable;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = TemplateEntity.TEMPLATE,
        primaryKeys = {"templateParentUid", "templateName"},
        indices = {@Index("templateName")},
        foreignKeys = @ForeignKey(entity = UserProfileEntity.class,
        parentColumns = "uid",
        childColumns = "templateParentUid",
        onDelete = CASCADE))

public class TemplateEntity implements Serializable {
    public static final String TEMPLATE = "template";
    @NonNull
    public String templateName;
    public int templateParentUid;
    public int templateType;
    private int level;
    private int time;
    private String diagramLevel;
    private String diagramIncline;
    private int someData;
    private int baseProgramId;
    private int useProgramId;

    public int getUseProgramId() {
        return useProgramId;
    }

    public void setUseProgramId(int useProgramId) {
        this.useProgramId = useProgramId;
    }

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private byte[] levelDiagram;
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private byte[] inclineDiagram;

    public byte[] getLevelDiagram() {
        return levelDiagram;
    }

    public void setLevelDiagram(byte[] levelDiagram) {
        this.levelDiagram = levelDiagram;
    }

    public byte[] getInclineDiagram() {
        return inclineDiagram;
    }

    public void setInclineDiagram(byte[] inclineDiagram) {
        this.inclineDiagram = inclineDiagram;
    }

    public TemplateEntity() {
        templateName = "DEFAULT_NAME";
    }

    @NonNull
    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(@NonNull String templateName) {
        this.templateName = templateName;
    }

    public int getTemplateParentUid() {
        return templateParentUid;
    }

    public void setTemplateParentUid(int templateParentUid) {
        this.templateParentUid = templateParentUid;
    }

    public int getTemplateType() {
        return templateType;
    }

    public void setTemplateType(int templateType) {
        this.templateType = templateType;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getDiagramLevel() {
        return diagramLevel;
    }

    public void setDiagramLevel(String diagramLevel) {
        this.diagramLevel = diagramLevel;
    }

    public String getDiagramIncline() {
        return diagramIncline;
    }

    public void setDiagramIncline(String diagramIncline) {
        this.diagramIncline = diagramIncline;
    }

    public int getSomeData() {
        return someData;
    }

    public void setSomeData(int someData) {
        this.someData = someData;
    }

    public static String getTEMPLATE() {
        return TEMPLATE;
    }

    public int getBaseProgramId() {
        return baseProgramId;
    }

    public void setBaseProgramId(int baseProgramId) {
        this.baseProgramId = baseProgramId;
    }
}
