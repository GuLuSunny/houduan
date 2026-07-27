package com.ydsw.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ydsw.config.FloodModelConfig;
import com.ydsw.mapper.FloodDatasetMapper;
import com.ydsw.mapper.FloodFrameMapper;
import com.ydsw.mapper.FloodTerrainMapper;
import com.ydsw.pojo.FloodDataset;
import com.ydsw.pojo.FloodFrame;
import com.ydsw.pojo.FloodTerrain;
import com.ydsw.service.FloodModelService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class FloodModelServiceImpl implements FloodModelService {

    @Autowired
    private FloodModelConfig config;

    @Autowired
    private FloodDatasetMapper floodDatasetMapper;

    @Autowired
    private FloodFrameMapper floodFrameMapper;

    @Autowired
    private FloodTerrainMapper floodTerrainMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final Map<String, Object> cache = new HashMap<>();

    @PostConstruct
    public void init() {
        log.info("洪水模型 API 服务实现初始化完成");
        log.info("API 地址: {}", config.getApiUrl());
    }

    /**
     * 发送 GET 请求到 Python 模型服务（返回字符串）
     */
    private String sendGet(String endpoint) throws Exception {
        String url = config.getApiUrl() + endpoint;
        log.debug("请求: {}", url);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("API 请求失败, 状态码: " + response.statusCode()
                    + ", 响应: " + response.body());
        }

        return response.body();
    }

    /**
     * 发送 GET 请求到 Python 模型服务（返回字节数组）
     */
    private byte[] sendGetBytes(String endpoint) throws Exception {
        String url = config.getApiUrl() + endpoint;
        log.debug("请求二进制数据: {}", url);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<byte[]> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() != 200) {
            throw new RuntimeException("API 请求失败, 状态码: " + response.statusCode());
        }

        return response.body();
    }

    @Override
    public Map<String, Object> getOverview() throws Exception {
        String cacheKey = "overview";
        if (cache.containsKey(cacheKey)) {
            return (Map<String, Object>) cache.get(cacheKey);
        }

        String response = sendGet("/api/overview?dataset_id=" + config.getDefaultDatasetId());
        Map<String, Object> data = objectMapper.readValue(response,
                new TypeReference<Map<String, Object>>() {});

        // 保存到数据库
        saveOverviewToDatabase(data);

        cache.put(cacheKey, data);
        return data;
    }

    @Override
    public byte[] getTerrain() throws Exception {
        return sendGetBytes("/api/terrain?dataset_id=" + config.getDefaultDatasetId());
    }

    @Override
    public List<Map<String, Object>> getFrameStats() throws Exception {
        String cacheKey = "frame_stats";
        if (cache.containsKey(cacheKey)) {
            return (List<Map<String, Object>>) cache.get(cacheKey);
        }

        String response = sendGet("/api/frame-stats?dataset_id=" + config.getDefaultDatasetId());
        List<Map<String, Object>> stats = objectMapper.readValue(response,
                new TypeReference<List<Map<String, Object>>>() {});

        cache.put(cacheKey, stats);
        return stats;
    }

    @Override
    public byte[] getFrame(int frameIndex) throws Exception {
        int runId = getAvailableRunId();
        String url = "/api/runs/" + runId + "/frames/" + frameIndex;
        log.info("请求帧数据: {}", url);
        try {
            byte[] data = sendGetBytes(url);
            log.info("帧 {} 返回数据大小: {} bytes", frameIndex, data.length);
            if (data.length > 0) {
                // 打印前20个字节用于调试
                StringBuilder hex = new StringBuilder();
                for (int i = 0; i < Math.min(20, data.length); i++) {
                    hex.append(String.format("%02X ", data[i]));
                }
                log.info("前20字节: {}", hex.toString());
            }
            return data;
        } catch (Exception e) {
            log.error("获取帧数据失败: frameIndex={}, runId={}", frameIndex, runId, e);
            throw e;
        }
    }

    /**
     * 自动查找可用的 run_id（与 dataset_id=1 关联的第一个 run）
     */
    private int getAvailableRunId() throws Exception {
        // 尝试从数据库获取已存储的 run_id
        // 若没有，则调用 Python API 获取 runs 列表
        String overviewStr = sendGet("/api/overview?dataset_id=1");
        Map<String, Object> overview = objectMapper.readValue(overviewStr, new TypeReference<>() {});
        List<Map<String, Object>> runs = (List<Map<String, Object>>) overview.get("runs");
        if (runs != null && !runs.isEmpty()) {
            int runId = toInt(runs.get(0).get("id"));
            log.info("使用 run_id: {}", runId);
            return runId;
        }
        // 如果没有 runs，使用配置的默认值
        log.warn("无法获取 run_id，使用默认值: {}", config.getDefaultRunId());
        return config.getDefaultRunId();
    }

    @Override
    public List<Map<String, Object>> getRuns() throws Exception {
        String response = sendGet("/api/datasets");
        Map<String, Object> data = objectMapper.readValue(response,
                new TypeReference<Map<String, Object>>() {});
        Object runsObj = data.get("datasets");
        if (runsObj instanceof List) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> runs = (List<Map<String, Object>>) runsObj;
            return runs;
        }
        return new ArrayList<>();
    }

    @Override
    public List<Map<String, Object>> getEvaluationRuns() throws Exception {
        String response = sendGet("/api/evaluation/runs");
        Map<String, Object> data = objectMapper.readValue(response,
                new TypeReference<Map<String, Object>>() {});
        Object runsObj = data.get("runs");
        if (runsObj instanceof List) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> runs = (List<Map<String, Object>>) runsObj;
            return runs;
        }
        return new ArrayList<>();
    }

    @Override
    public boolean testConnection() {
        try {
            sendGet("/api/overview");
            return true;
        } catch (Exception e) {
            log.warn("API 连接测试失败: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public void clearCache() {
        cache.clear();
        log.info("缓存已清除");
    }

    /**
     * 将概览数据保存到数据库（同时保存地形和帧数据）
     */
    private void saveOverviewToDatabase(Map<String, Object> overviewData) {
        try {
            // 1. 保存数据集信息
            FloodDataset dataset = new FloodDataset();
            dataset.setName("Python模型数据_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));
            dataset.setSourceType("python_model");

            @SuppressWarnings("unchecked")
            Map<String, Object> datasetInfo = (Map<String, Object>) overviewData.get("dataset");
            Integer datasetId = null;
            if (datasetInfo != null) {
                dataset.setFrameCount(toInt(datasetInfo.get("frame_count")));
                dataset.setFrameIntervalMinutes(toInt(datasetInfo.get("frame_interval_minutes")));
                dataset.setGridRows(toInt(datasetInfo.get("grid_rows")));
                dataset.setGridCols(toInt(datasetInfo.get("grid_cols")));
                dataset.setCellsize(toDouble(datasetInfo.get("cellsize")));
                dataset.setDemMin(toDouble(datasetInfo.get("dem_min")));
                dataset.setDemMax(toDouble(datasetInfo.get("dem_max")));
                dataset.setMaxWaterDepth(toDouble(datasetInfo.get("max_water_depth")));
                datasetId = toInt(datasetInfo.get("id"));
            }
            dataset.setMetadata(objectMapper.writeValueAsString(overviewData));
            floodDatasetMapper.insert(dataset);

            Integer savedDatasetId = dataset.getId();
            log.info("数据集已保存到数据库, ID: {}", savedDatasetId);

            // 2. 保存地形数据（从 Python 获取）
            saveTerrainToDatabase(savedDatasetId);

            // 3. 保存帧统计数据（从 Python 获取）
            saveFrameStatsToDatabase(savedDatasetId);

            log.info("数据集 {} 的所有数据已完整保存", savedDatasetId);

        } catch (Exception e) {
            log.error("保存数据集到数据库失败", e);
        }
    }

    /**
     * 保存地形数据到数据库
     */
    private void saveTerrainToDatabase(Integer datasetId) {
        try {
            byte[] terrainBytes = getTerrain();
            if (terrainBytes == null || terrainBytes.length == 0) {
                log.warn("地形数据为空");
                return;
            }

            // 获取概览数据中的地形信息
            String overviewStr = sendGet("/api/overview?dataset_id=" + config.getDefaultDatasetId());
            Map<String, Object> overviewData = objectMapper.readValue(overviewStr,
                    new TypeReference<Map<String, Object>>() {});

            @SuppressWarnings("unchecked")
            Map<String, Object> datasetInfo = (Map<String, Object>) overviewData.get("dataset");

            Integer rows = toInt(datasetInfo.get("grid_rows"));
            Integer cols = toInt(datasetInfo.get("grid_cols"));

            // 将二进制地形数据转为 float 数组，然后转为 JSON 字符串
            int floatCount = terrainBytes.length / 4;
            if (floatCount == 0) {
                log.warn("地形二进制数据为空，跳过");
                return;
            }
            float[] floats = new float[floatCount];
            ByteBuffer.wrap(terrainBytes)
                .order(ByteOrder.LITTLE_ENDIAN)
                .asFloatBuffer()
                .get(floats);

            // 校验数据
            float maxVal = Float.MIN_VALUE;
            float minVal = Float.MAX_VALUE;
            float sum = 0;
            for (float f : floats) {
                if (f > maxVal) maxVal = f;
                if (f < minVal) minVal = f;
                sum += f;
            }
            float avgVal = floatCount > 0 ? sum / floatCount : 0;
            log.info("地形数据范围: min={}, max={}, avg={}, count={}", minVal, maxVal, avgVal, floatCount);

            if (maxVal > 1000 || minVal < -100) {
                log.warn("地形数据可能异常！min={}, max={}", minVal, maxVal);
            }

            String elevationJson = objectMapper.writeValueAsString(floats);

            FloodTerrain terrain = new FloodTerrain();
            terrain.setDatasetId(datasetId);
            terrain.setRows(rows);
            terrain.setCols(cols);
            terrain.setElevation(elevationJson);
            terrain.setXllcorner(toDouble(datasetInfo.get("xllcorner")));
            terrain.setYllcorner(toDouble(datasetInfo.get("yllcorner")));
            terrain.setCellsizeX(toDouble(datasetInfo.get("cellsize_x")));
            terrain.setCellsizeY(toDouble(datasetInfo.get("cellsize_y")));

            floodTerrainMapper.insert(terrain);
            log.info("地形数据已保存, ID: {}", terrain.getId());

        } catch (Exception e) {
            log.error("保存地形数据失败", e);
        }
    }

    /**
     * 保存帧统计数据到数据库
     */
    private void saveFrameStatsToDatabase(Integer datasetId) {
        try {
            List<Map<String, Object>> frameStats = getFrameStats();
            if (frameStats == null || frameStats.isEmpty()) {
                log.warn("帧统计为空");
                return;
            }

            int savedCount = 0;
            for (Map<String, Object> stat : frameStats) {
                FloodFrame frame = new FloodFrame();
                frame.setDatasetId(datasetId);
                frame.setFrameIndex(toInt(stat.get("frame")));
                frame.setFrameName((String) stat.get("name"));
                frame.setMinutes(toInt(stat.get("minutes")));
                frame.setMaxDepth(toDouble(stat.get("max_depth_m")));
                frame.setMeanDepth(toDouble(stat.get("mean_depth_m")));
                frame.setWetAreaPct(null); // 原始数据中没有这个字段
                frame.setVolume(toDouble(stat.get("water_volume_m3")));

                // 获取完整帧数据并保存为 JSON（获取每个帧的二进制数据）
                byte[] frameBytes = null;
                try {
                    Integer frameIndex = toInt(stat.get("frame"));
                    if (frameIndex != null) {
                        frameBytes = getFrame(frameIndex);
                    }
                } catch (Exception e) {
                    log.warn("获取帧 {} 数据失败: {}", stat.get("frame"), e.getMessage());
                    frame.setWaterDepth(null);
                    floodFrameMapper.insert(frame);
                    continue; // 跳过此帧，继续下一帧
                }

                if (frameBytes != null && frameBytes.length > 0) {
                    // 解析二进制数据
                    int floatCount = frameBytes.length / 4;
                    if (floatCount > 0) {
                        float[] floats = new float[floatCount];
                        ByteBuffer.wrap(frameBytes)
                            .order(ByteOrder.LITTLE_ENDIAN)
                            .asFloatBuffer()
                            .get(floats);

                        // 计算统计值
                        float maxVal = Float.MIN_VALUE;
                        float minVal = Float.MAX_VALUE;
                        float sum = 0;
                        for (float f : floats) {
                            if (f > maxVal) maxVal = f;
                            if (f < minVal) minVal = f;
                            sum += f;
                        }
                        float avgVal = floatCount > 0 ? sum / floatCount : 0;
                        log.info("帧 {} 水位数据统计: min={}, max={}, avg={}, count={}",
                                 stat.get("frame"), minVal, maxVal, avgVal, floatCount);

                        // 如果数据明显异常（如全0但期望非0），记录警告
                        double expectedMaxDepth = toDouble(stat.get("max_depth_m"));
                        if (expectedMaxDepth > 0.1 && Math.abs(maxVal - expectedMaxDepth) > 10) {
                            log.warn("帧 {} 水位数据与统计值不一致！解析max={}, 期望max={}",
                                     stat.get("frame"), maxVal, expectedMaxDepth);
                        }

                        // 转为 JSON 存储
                        String waterDepthJson = objectMapper.writeValueAsString(floats);
                        frame.setWaterDepth(waterDepthJson);
                    } else {
                        log.warn("帧 {} 二进制数据长度为0", stat.get("frame"));
                        frame.setWaterDepth(null);
                    }
                } else {
                    log.warn("帧 {} 未获取到二进制数据", stat.get("frame"));
                    frame.setWaterDepth(null);
                }

                floodFrameMapper.insert(frame);
                savedCount++;
            }

            log.info("帧统计数据已保存, 共 {} 帧", savedCount);

        } catch (Exception e) {
            log.error("保存帧统计数据失败", e);
        }
    }

    private Integer toInt(Object value) {
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).intValue();
        return null;
    }

    private Double toDouble(Object value) {
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).doubleValue();
        return null;
    }
}