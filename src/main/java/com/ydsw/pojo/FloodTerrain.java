package com.ydsw.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("flood_terrain")
public class FloodTerrain {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private Integer datasetId;
    
    private Integer rows;
    
    private Integer cols;
    
    private String elevation;  // 改为 String 类型，存储 JSON 格式
    
    private Double xllcorner;
    
    private Double yllcorner;
    
    private Double cellsizeX;
    
    private Double cellsizeY;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}