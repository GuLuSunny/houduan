package com.ydsw.service;

import java.util.List;
import java.util.Map;

/**
 * 洪水模型服务接口
 * 负责调用 Python 洪水模型的 FastAPI 服务
 */
public interface FloodModelService {

    /**
     * 获取数据集概览
     */
    Map<String, Object> getOverview() throws Exception;

    /**
     * 获取地形数据（DEM）
     */
    byte[] getTerrain() throws Exception;

    /**
     * 获取帧统计数据
     */
    List<Map<String, Object>> getFrameStats() throws Exception;

    /**
     * 获取指定帧的洪水深度数据
     */
    byte[] getFrame(int frameIndex) throws Exception;

    /**
     * 获取所有数据集列表
     */
    List<Map<String, Object>> getRuns() throws Exception;

    /**
     * 获取评测运行列表
     */
    List<Map<String, Object>> getEvaluationRuns() throws Exception;

    /**
     * 测试 API 连接
     */
    boolean testConnection();

    /**
     * 清除缓存
     */
    void clearCache();
}