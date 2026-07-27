package com.ydsw.controller;

import com.ydsw.service.FloodModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/flood")
public class FloodDataController {

    @Autowired
    private FloodModelService floodModelService;

    /**
     * 获取洪水模拟概览数据
     */
    @GetMapping("/overview")
    public ResponseEntity<?> getOverview() {
        try {
            Map<String, Object> data = floodModelService.getOverview();
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "success",
                "data", data
            ));
        } catch (Exception e) {
            return ResponseEntity.status(503).body(Map.of(
                "code", 503,
                "message", "Python 模型服务不可用: " + e.getMessage()
            ));
        }
    }

    /**
     * 获取地形数据（DEM）
     */
    @GetMapping("/terrain")
    public ResponseEntity<?> getTerrain() {
        try {
            byte[] terrainData = floodModelService.getTerrain();
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=terrain.bin")
                    .body(terrainData);
        } catch (Exception e) {
            return ResponseEntity.status(503).body(Map.of(
                "code", 503,
                "message", "获取地形数据失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 获取帧统计数据
     */
    @GetMapping("/frame-stats")
    public ResponseEntity<?> getFrameStats(
            @RequestParam(required = false) Integer limit) {
        try {
            List<Map<String, Object>> stats = floodModelService.getFrameStats();
            if (limit != null && limit > 0 && limit < stats.size()) {
                stats = stats.subList(0, limit);
            }
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", stats
            ));
        } catch (Exception e) {
            return ResponseEntity.status(503).body(Map.of(
                "code", 503,
                "message", "获取帧统计失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 获取指定帧的洪水深度数据
     */
    @GetMapping("/frame/{index}")
    public ResponseEntity<?> getFrame(@PathVariable int index) {
        try {
            byte[] frameData = floodModelService.getFrame(index);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=frame_" + index + ".bin")
                    .body(frameData);
        } catch (Exception e) {
            return ResponseEntity.status(503).body(Map.of(
                "code", 503,
                "message", "获取帧数据失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 获取数据集列表（支持限制数量）
     */
    @GetMapping("/runs")
    public ResponseEntity<?> getRuns(
            @RequestParam(required = false, defaultValue = "5") Integer limit) {
        try {
            List<Map<String, Object>> runs = floodModelService.getRuns();
            // 限制返回数量
            if (limit != null && limit > 0 && limit < runs.size()) {
                runs = runs.subList(0, limit);
            }
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", runs,
                "total", runs.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(503).body(Map.of(
                "code", 503,
                "message", "获取运行列表失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 测试 Python 模型服务连接
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        boolean connected = floodModelService.testConnection();
        if (connected) {
            return ResponseEntity.ok(Map.of(
                "status", "UP",
                "message", "Python 模型服务连接正常"
            ));
        } else {
            return ResponseEntity.status(503).body(Map.of(
                "status", "DOWN",
                "message", "Python 模型服务无法连接"
            ));
        }
    }

    /**
     * 清除缓存
     */
    @PostMapping("/cache/clear")
    public ResponseEntity<?> clearCache() {
        floodModelService.clearCache();
        return ResponseEntity.ok(Map.of(
            "code", 200,
            "message", "缓存已清除"
        ));
    }
}