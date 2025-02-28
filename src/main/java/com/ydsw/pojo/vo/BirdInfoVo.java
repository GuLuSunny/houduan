package com.ydsw.pojo.vo;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhangkaifei
 * @description: TODO
 * @date 2024/11/21  17:06
 * @Version 1.0
 */
public class BirdInfoVo implements Serializable {

    private String id;
    private String label;
    private String nameLatin;
    private String leafBool;
    private List<BirdInfoVo> children;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getNameLatin() {
        return nameLatin;
    }

    public void setNameLatin(String nameLatin) {
        this.nameLatin = nameLatin;
    }

    public List<BirdInfoVo> getChildren() {
        return children;
    }

    public void setChildren(List<BirdInfoVo> children) {
        this.children = children;
    }

    public String getLeafBool() {
        return leafBool;
    }

    public void setLeafBool(String leafBool) {
        this.leafBool = leafBool;
    }

    @Override
    public String toString() {
        return "BirdInfoVo{" +
                "id='" + id + '\'' +
                ", label='" + label + '\'' +
                ", nameLatin='" + nameLatin + '\'' +
                ", leafBool='" + leafBool + '\'' +
                ", children=" + children +
                '}';
    }
}
