package com.ydsw.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("flood_frame")
public class FloodFrame {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private Integer datasetId;
    
    private Integer frameIndex;
    
    private String frameName;
    
    private Integer minutes;
    
    private Double maxDepth;
    
    private Double meanDepth;
    
    private Double wetAreaPct;
    
    private Double volume;
    
    private String waterDepth;  // 改为 String 类型，存储 JSON 格式
    
    @TableField(exist = false)
    private String waterDepthJson;  // 用于传递 JSON 格式的水位数据

    // 转换为 JSON 存储
    public void setWaterDepthFromJson(String json) {
        this.waterDepthJson = json;
    }

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}