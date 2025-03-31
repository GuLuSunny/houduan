package com.ydsw.utils;
import org.locationtech.jts.geom.Geometry;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ShpfileUtils {

    /**
     * 解析 SHP 文件并返回实体列表
     * @param file 上传的 SHP 文件（需包含 .shp, .dbf, .shx 等）
     * @param targetClass 目标实体类（需包含 Geometry 字段）
     * @return 解析后的实体列表
     */
    public static <T> List<T> parseShpFile(MultipartFile file, Class<T> targetClass) throws IOException {
        // 1. 创建临时文件
        File tempFile = File.createTempFile("shapefile", ".shp");
        file.transferTo(tempFile);

        // 2. 读取 SHP 文件
        ShapefileDataStore dataStore = new ShapefileDataStore(tempFile.toURI().toURL());
        dataStore.setCharset(StandardCharsets.UTF_8);

        List<T> entities = new ArrayList<>();
        try (SimpleFeatureIterator iterator = dataStore.getFeatureSource().getFeatures().features()) {
            while (iterator.hasNext()) {
                SimpleFeature feature = iterator.next();
                // 3. 转换为目标实体对象
                T entity = convertFeatureToEntity(feature, targetClass);
                entities.add(entity);
            }
        } finally {
            // 4. 清理资源
            dataStore.dispose();
            tempFile.delete();
        }
        return entities;
    }

    /**
     * 将 SimpleFeature 转换为目标实体对象
     */
    private static <T> T convertFeatureToEntity(SimpleFeature feature, Class<T> targetClass) {
        try {
            T entity = targetClass.getDeclaredConstructor().newInstance();
            // 示例：假设实体类有 setName() 和 setGeometry() 方法
            // 根据实际字段映射调整此处逻辑
            String name = (String) feature.getAttribute("name");
            Geometry geometry = (Geometry) feature.getDefaultGeometry();

            // 使用反射或 BeanUtils 填充字段（此处简化示例）
            targetClass.getMethod("setName", String.class).invoke(entity, name);
            targetClass.getMethod("setGeometry", Geometry.class).invoke(entity, geometry);

            return entity;
        } catch (Exception e) {
            throw new RuntimeException("实体转换失败", e);
        }
    }
}