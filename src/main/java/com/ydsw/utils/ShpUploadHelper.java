package com.ydsw.utils;



import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Geometry;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

@Slf4j
public class ShpUploadHelper {

    /**
     * 统一的SHP文件上传处理
     * @param fileGroup 上传的文件数组
     * @param entityClass 目标实体类
     * @param geometryType 几何类型："geom" 或 "geog"
     * @param customProcessor 自定义处理器
     * @return 解析后的实体列表
     */
    public static <T> ResultTemplate<List<T>> handleShpUpload(
            MultipartFile[] fileGroup,
            Class<T> entityClass,
            String geometryType,
            CustomProcessor<T> customProcessor) throws IOException {

        if (fileGroup == null || fileGroup.length == 0) {
            return ResultTemplate.fail("请提交文件！");
        }

        try {
            List<T> entityList;

            // 检查是否是单个ZIP文件
            if (fileGroup.length == 1 &&
                    fileGroup[0].getOriginalFilename().toLowerCase().endsWith(".zip")) {

                // 处理ZIP文件
                File tempZipFile = File.createTempFile("shp_zip_", ".zip");
                try {
                    fileGroup[0].transferTo(tempZipFile);
                    entityList = ShpfileUtils.parseShpFromZipFile(tempZipFile, entityClass);
                } finally {
                    if (tempZipFile.exists()) {
                        tempZipFile.delete();
                    }
                }

            } else {
                // 验证SHP文件组
                String fileName = fileGroup[0].getOriginalFilename();
                for (int i = 1; i < fileGroup.length; i++) {
                    String finename = fileGroup[i].getOriginalFilename();
                    if (!Objects.equals(
                            getBaseName(finename),
                            getBaseName(fileName))) {
                        return ResultTemplate.fail("文件格式错误！");
                    }
                }

                // 解析SHP文件组
                entityList = ShpfileUtils.parseMultipleShpGroups(fileGroup, entityClass);
            }

            // 预处理：检查实体类是否有必要的方法
            validateEntityClass(entityClass, geometryType);

            // 应用通用处理
            for (T entity : entityList) {
                processEntity(entity, entityClass, geometryType, customProcessor);
            }

            return ResultTemplate.success(entityList);

        } catch (Exception e) {
            log.error("文件处理失败", e);
            throw new IOException("文件处理失败: " + e.getMessage(), e);
        }
    }

    /**
     * 验证实体类是否包含必要的方法
     */
    private static <T> void validateEntityClass(Class<T> entityClass, String geometryType) throws NoSuchMethodException {
        // 检查几何字段方法
        if ("geom".equals(geometryType)) {
            entityClass.getMethod("getGeom");
            // 检查是否有setGeom方法（可选）
            try {
                entityClass.getMethod("setGeom", Geometry.class);
            } catch (NoSuchMethodException e) {
                log.warn("实体类 {} 没有setGeom方法，这可能影响某些操作", entityClass.getName());
            }
        } else if ("geog".equals(geometryType)) {
            entityClass.getMethod("getGeog");
            try {
                entityClass.getMethod("setGeog", Geometry.class);
            } catch (NoSuchMethodException e) {
                log.warn("实体类 {} 没有setGeog方法，这可能影响某些操作", entityClass.getName());
            }
        } else {
            throw new IllegalArgumentException("不支持的几何类型: " + geometryType);
        }

        // 检查其他必要方法
        try {
            entityClass.getMethod("setCreateTime", Date.class);
        }catch (NoSuchMethodException e) {
            log.warn("实体类 {} 没有setCreateTime方法，这可能影响某些操作", entityClass.getName());
        }
        try {
            entityClass.getMethod("setStatus", Integer.class);
        }catch (NoSuchMethodException e) {
            log.warn("实体类 {} 没有setStatus方法，这可能影响某些操作", entityClass.getName());
        }


    }

    /**
     * 处理单个实体
     */
    private static <T> void processEntity(T entity, Class<T> entityClass,
                                          String geometryType,
                                          CustomProcessor<T> customProcessor) throws Exception {

        // 1. 设置SRID
        Object geometry = null;
        if ("geom".equals(geometryType)) {
            Method getGeomMethod = entityClass.getMethod("getGeom");
            geometry = getGeomMethod.invoke(entity);
        } else if ("geog".equals(geometryType)) {
            Method getGeogMethod = entityClass.getMethod("getGeog");
            geometry = getGeogMethod.invoke(entity);
        }

        if (geometry instanceof Geometry geom) {
            geom.setSRID(4326);
        } else if (geometry != null) {
            // 如果geometry不是JTS Geometry类型，尝试通过反射调用setSRID
            try {
                Method setSridMethod = geometry.getClass().getMethod("setSRID", int.class);
                setSridMethod.invoke(geometry, 4326);
            } catch (NoSuchMethodException e) {
                log.warn("几何对象类型 {} 没有setSRID方法", geometry.getClass().getName());
            }
        }

        // 2. 设置创建时间
        Method setCreateTimeMethod = entityClass.getMethod("setCreateTime", Date.class);
        setCreateTimeMethod.invoke(entity, new Date());

        // 3. 设置状态
        Method setStatusMethod = entityClass.getMethod("setStatus", Integer.class);
        setStatusMethod.invoke(entity, 0);

        // 4. 调用自定义处理器
        if (customProcessor != null) {
            customProcessor.process(entity);
        }
    }


    /**
     * 获取文件基本名
     */
    private static String getBaseName(String fileName) {
        if (fileName == null) return "";
        int dotIndex = fileName.lastIndexOf('.');
        return dotIndex > 0 ? fileName.substring(0, dotIndex) : fileName;
    }

    /**
     * 自定义处理器接口
     */
    @FunctionalInterface
    public interface CustomProcessor<T> {
        void process(T entity) throws Exception;
    }
}