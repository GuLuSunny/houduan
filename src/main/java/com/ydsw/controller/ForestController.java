package com.ydsw.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.ydsw.domain.Forest;
import com.ydsw.service.ForestService;
import com.ydsw.utils.ResultTemplate;
import com.ydsw.utils.ShpUploadHelper;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.io.WKTReader;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@Slf4j
public class ForestController {

    @Autowired
    private ForestService forestService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${forest.tree-points-path:/data/ydsw/tree-points/}")
    private String treePointsPath;

    private static final int MAX_POINTS_PER_RANGE = 20000;

    // ==================== 上传 SHP ====================

    @PostMapping("/api/forest/uploadByShpfiles")
    @Transactional(rollbackFor = Exception.class)
    public ResultTemplate<Object> uploadByShpfiles(@RequestParam("shpfiles") MultipartFile[] fileGroup) {
        try {
            ResultTemplate<List<Forest>> parseResult = ShpUploadHelper.handleShpUpload(
                    fileGroup, Forest.class, "geom", null
            );
            if (!parseResult.isSuccess()) {
                return ResultTemplate.fail(parseResult.getMessage());
            }

            List<Forest> forestList = parseResult.getData();
            for (Forest forest : forestList) {
                if (forest.getGeom() != null) {
                    forest.getGeom().setSRID(4326);
                }
            }

            forestService.remove(null);

            forestService.saveBatch(forestList);

            try {
                generateAndSaveAllTreePoints(20);
            } catch (Exception e) {
                log.error("树点缓存生成失败，但数据已入库", e);
            }

            return ResultTemplate.success("提交成功！");

        } catch (Exception e) {
            log.error("SHP上传失败", e);
            return ResultTemplate.fail("文件处理失败: " + e.getMessage());
        }
    }

    // ==================== 获取林地范围 ====================

    @PostMapping("/api/forest/getAllForestAsGeoJson")
    public ResultTemplate<Object> getAllForestAsGeoJson() {
        List<Map<String, Object>> features = forestService.getAllForestAsGeoJson();

        for (Map<String, Object> feature : features) {
            Object geomObj = feature.get("geometry");
            if (geomObj instanceof PGobject) {
                String geomStr = geomObj.toString();
                feature.put("geometry", JSONUtil.parse(geomStr));
            } else if (geomObj instanceof String) {
                feature.put("geometry", JSONUtil.parse((String) geomObj));
            }
        }

        Map<String, Object> geoJson = new HashMap<>();
        geoJson.put("type", "FeatureCollection");
        geoJson.put("features", features);

        return ResultTemplate.success(geoJson);
    }

    // ==================== 获取树木点（缓存 + 保底生成） ====================

    @PostMapping("/api/forest/getTreePoints")
    public ResultTemplate<Object> getTreePoints() {
        try {
            File cacheFile = new File(treePointsPath, "forest_trees.geojson");
            if (cacheFile.exists()) {
                String content = FileUtil.readString(cacheFile, StandardCharsets.UTF_8);
                return ResultTemplate.success(JSONUtil.parse(content));
            }

            log.info("树点缓存文件不存在，开始生成...");
            generateAndSaveAllTreePoints(20);
            String content = FileUtil.readString(cacheFile, StandardCharsets.UTF_8);
            return ResultTemplate.success(JSONUtil.parse(content));

        } catch (Exception e) {
            log.error("获取树点失败", e);
            return ResultTemplate.fail("获取树点失败: " + e.getMessage());
        }
    }

    // ==================== 按 gid 生成单多边形树点（保留兼容旧前端） ====================

    @PostMapping("/api/forest/generateTreePointsGeoJson")
    public ResultTemplate<Object> generateTreePointsGeoJson(
            @RequestParam Integer rangeId,
            @RequestParam(required = false, defaultValue = "5") Integer density) {
        try {
            Map<String, Object> result = generatePointsForGid(rangeId, density);

            Map<String, Object> geoJson = new HashMap<>();
            geoJson.put("type", "FeatureCollection");
            geoJson.put("features", result.get("features"));

            Map<String, Object> wrap = new HashMap<>();
            wrap.put("rangeId", rangeId);
            wrap.put("totalPoints", ((List<?>) result.get("features")).size());
            wrap.put("geojson", geoJson);

            return ResultTemplate.success(wrap);

        } catch (Exception e) {
            log.error("生成树木点失败", e);
            return ResultTemplate.fail("生成失败: " + e.getMessage());
        }
    }

    // ==================== 内部方法 ====================

    @SuppressWarnings("unchecked")
    private Map<String, Object> generatePointsForGid(Integer gid, int density) {
        String sql = "SELECT ST_AsText(geom) as wkt, shape_area FROM forest WHERE gid = ?";
        Map<String, Object> rangeData = jdbcTemplate.queryForMap(sql, gid);

        String wkt = (String) rangeData.get("wkt");
        Double areaHectare = ((Number) rangeData.get("shape_area")).doubleValue();

        WKTReader reader = new WKTReader();
        Geometry geometry;
        try {
            geometry = reader.read(wkt);
        } catch (Exception e) {
            throw new RuntimeException("解析几何失败: " + e.getMessage());
        }

        int pointCount = (int) Math.ceil(Math.abs(areaHectare) * density);
        if (pointCount > MAX_POINTS_PER_RANGE) {
            log.warn("gid={} 的 shape_area={}，density={} 算出来需要 {} 个点，已按上限 {} 截断",
                    gid, areaHectare, density, pointCount, MAX_POINTS_PER_RANGE);
            pointCount = MAX_POINTS_PER_RANGE;
        }
        Envelope envelope = geometry.getEnvelopeInternal();

        List<Map<String, Object>> features = new ArrayList<>();
        int validCount = 0;
        int maxAttempts = Math.max(pointCount * 3, 1000);

        for (int i = 0; i < maxAttempts && validCount < pointCount; i++) {
            double x = envelope.getMinX() + Math.random() * (envelope.getMaxX() - envelope.getMinX());
            double y = envelope.getMinY() + Math.random() * (envelope.getMaxY() - envelope.getMinY());

            Coordinate coord = new Coordinate(x, y);

            if (geometry.contains(geometry.getFactory().createPoint(coord))) {
                Map<String, Object> feature = new HashMap<>();
                feature.put("type", "Feature");

                Map<String, Object> geomJson = new HashMap<>();
                geomJson.put("type", "Point");
                geomJson.put("coordinates", Arrays.asList(x, y));
                feature.put("geometry", geomJson);

                Map<String, Object> properties = new HashMap<>();
                properties.put("gid", gid);
                properties.put("tree_type", "pine");
                properties.put("height", 5 + Math.random() * 10);
                feature.put("properties", properties);

                features.add(feature);
                validCount++;
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("features", features);
        result.put("totalPoints", validCount);
        return result;
    }

    private void generateAndSaveAllTreePoints(int density) throws IOException {
        List<Integer> gids = jdbcTemplate.queryForList("SELECT gid FROM forest ORDER BY gid", Integer.class);
        if (gids.isEmpty()) {
            log.warn("forest 表为空，跳过树点生成");
            return;
        }

        List<Map<String, Object>> allFeatures = new ArrayList<>();
        int globalId = 0;

        for (Integer gid : gids) {
            try {
                Map<String, Object> points = generatePointsForGid(gid, density);
                List<Map<String, Object>> features = (List<Map<String, Object>>) points.get("features");
                for (Map<String, Object> f : features) {
                    ((Map<String, Object>) f.get("properties")).put("id", globalId++);
                    allFeatures.add(f);
                }
                log.info("gid={} 生成了 {} 个树点", gid, features.size());
            } catch (Exception e) {
                log.error("gid={} 生成树点失败，跳过", gid, e);
            }
        }

        Map<String, Object> geoJson = new HashMap<>();
        geoJson.put("type", "FeatureCollection");
        geoJson.put("features", allFeatures);

        File dir = new File(treePointsPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File cacheFile = new File(dir, "forest_trees.geojson");
        FileUtil.writeString(JSONUtil.toJsonStr(geoJson), cacheFile, StandardCharsets.UTF_8);

        log.info("树点缓存文件已写入 {}，共 {} 个点", cacheFile.getAbsolutePath(), globalId);
    }
}
