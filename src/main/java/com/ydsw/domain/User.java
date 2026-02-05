package com.ydsw.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * 用户表
 * @TableName user
 */
@TableName(value ="\"user\"")
public class User implements Serializable {
    /**
     * 用户id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 联系电话
     */
    private String tel;

    /**
     * 联系地址
     */
    private String address;

    /**
     * 联系邮箱
     */
    private String email;

    /**
     * 制作单位
     */
    private String productionCompany;

    /**
     * 是否已删除，0：未删除，1：已删除
     */
    private Integer status;

    /**
     * 数据入库时间
     */
    private Date createTime;

    /**
     * 备注
     */
    private String memo;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 用户id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 联系电话
     */
    public String getTel() {
        return tel;
    }

    /**
     * 联系电话
     */
    public void setTel(String tel) {
        this.tel = tel;
    }

    /**
     * 联系地址
     */
    public String getAddress() {
        return address;
    }

    /**
     * 联系地址
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * 联系邮箱
     */
    public String getEmail() {
        return email;
    }

    /**
     * 联系邮箱
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 制作单位
     */
    public String getProductionCompany() {
        return productionCompany;
    }

    /**
     * 制作单位
     */
    public void setProductionCompany(String productionCompany) {
        this.productionCompany = productionCompany;
    }

    /**
     * 是否已删除，0：未删除，1：已删除
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 是否已删除，0：未删除，1：已删除
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 数据入库时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 数据入库时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 备注
     */
    public String getMemo() {
        return memo;
    }

    /**
     * 备注
     */
    public void setMemo(String memo) {
        this.memo = memo;
    }

    public User(){};
    public  User(Map<String,Object> map){
        id=(Integer)map.get("id");
        username=(String)map.get("username");
        password=(String)map.get("password");
        tel=(String)map.get("tel");
        address=(String)map.get("address");
        email=(String)map.get("email");
        productionCompany=(String)map.get("productionCompany");
        status=(Integer)map.get("status");
        createTime=(Date)map.get("createTime");
        memo=(String)map.get("memo");
    }
    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        User other = (User) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getUsername() == null ? other.getUsername() == null : this.getUsername().equals(other.getUsername()))
                && (this.getPassword() == null ? other.getPassword() == null : this.getPassword().equals(other.getPassword()))
                && (this.getTel() == null ? other.getTel() == null : this.getTel().equals(other.getTel()))
                && (this.getAddress() == null ? other.getAddress() == null : this.getAddress().equals(other.getAddress()))
                && (this.getEmail() == null ? other.getEmail() == null : this.getEmail().equals(other.getEmail()))
                && (this.getProductionCompany() == null ? other.getProductionCompany() == null : this.getProductionCompany().equals(other.getProductionCompany()))
                && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
                && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
                && (this.getMemo() == null ? other.getMemo() == null : this.getMemo().equals(other.getMemo()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUsername() == null) ? 0 : getUsername().hashCode());
        result = prime * result + ((getPassword() == null) ? 0 : getPassword().hashCode());
        result = prime * result + ((getTel() == null) ? 0 : getTel().hashCode());
        result = prime * result + ((getAddress() == null) ? 0 : getAddress().hashCode());
        result = prime * result + ((getEmail() == null) ? 0 : getEmail().hashCode());
        result = prime * result + ((getProductionCompany() == null) ? 0 : getProductionCompany().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getMemo() == null) ? 0 : getMemo().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", username=").append(username);
        sb.append(", password=").append(password);
        sb.append(", tel=").append(tel);
        sb.append(", address=").append(address);
        sb.append(", email=").append(email);
        sb.append(", productionCompany=").append(productionCompany);
        sb.append(", status=").append(status);
        sb.append(", createTime=").append(createTime);
        sb.append(", memo=").append(memo);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}