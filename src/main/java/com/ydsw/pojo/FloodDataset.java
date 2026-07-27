package com.ydsw.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("flood_dataset")
public class FloodDataset {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String name;
    
    private String sourceType;
    
    private Integer frameCount;
    
    private Integer frameIntervalMinutes;
    
    private Integer gridRows;
    
    private Integer gridCols;
    
    private Double cellsize;
    
    private Double demMin;
    
    private Double demMax;
    
    private Double maxWaterDepth;
    
    private String metadata;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}