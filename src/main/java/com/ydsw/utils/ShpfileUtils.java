package com.ydsw.utils;
import com.ydsw.domain.Sluice;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.locationtech.jts.geom.Geometry;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShpfileUtils {

    public static <T> List<T> parseMultipleShpGroups(MultipartFile[] fileGroups, Class<T> targetClass) throws IOException {
        List<T> allEntities = new ArrayList<>();
        for (MultipartFile shpFile : fileGroups) {
            // 仅处理 .shp 主文件
            if (!shpFile.getOriginalFilename().toLowerCase().endsWith(".shp")) {
                continue;
            }
            // 调用 parseShpFile 解析单个 SHP 文件组（含配套文件）
            List<T> entities = parseShpFile(shpFile,fileGroups, targetClass);
            allEntities.addAll(entities);
        }
        return allEntities;
    }
    /**
     * 解析 SHP 文件并返回实体列表
     * @param mainShpFile 上传的 SHP 文件（需包含 .shp, .dbf, .shx 等）
     * @param targetClass 目标实体类（需包含 Geometry 字段）
     * @return 解析后的实体列表
     */
    public static <T> List<T> parseShpFile(
            MultipartFile mainShpFile,
            MultipartFile[]  auxiliaryFiles,
            Class<T> targetClass
    ) throws IOException {

        // 1. 创建临时目录
        File tempDir = Files.createTempDirectory("shp_").toFile();
        String baseName = FilenameUtils.removeExtension(mainShpFile.getOriginalFilename());

        try {
            // 2. 保存主 .shp 文件
            File tempShpFile = new File(tempDir, baseName + ".shp");
            mainShpFile.transferTo(tempShpFile);

            // 3. 保存配套文件（.dbf, .shx 等）
            for (MultipartFile file : auxiliaryFiles) {
                String ext = FilenameUtils.getExtension(file.getOriginalFilename());
                if(Objects.equals(ext, "shp"))
                {
                    continue;
                }
                File tempAuxFile = new File(tempDir, baseName + "." + ext);
                file.transferTo(tempAuxFile);
            }

            // 4. 读取 SHP 文件组
            ShapefileDataStore dataStore = new ShapefileDataStore(tempShpFile.toURI().toURL());
            dataStore.setCharset(StandardCharsets.UTF_8);

            List<T> entities = new ArrayList<>();
            try (SimpleFeatureIterator iterator = dataStore.getFeatureSource().getFeatures().features()) {
                while (iterator.hasNext()) {
                    SimpleFeature feature = iterator.next();
                    T entity = convertFeatureToEntity(feature, targetClass);
                    entities.add(entity);
                }
            } finally {
                dataStore.dispose();
            }
            return entities;
        } finally {
            // 5. 清理临时目录
            FileUtils.deleteDirectory(tempDir);
        }
    }

    /**
     * 将 SimpleFeature 转换为目标实体对象
     */
    private static <T> T convertFeatureToEntity(SimpleFeature feature, Class<T> targetClass) {
        try {
            T entity = targetClass.getDeclaredConstructor().newInstance();
            // 根据实际字段映射调整此处逻辑
            // 使用反射或 BeanUtils 填充字段（此处简化示例）

            Geometry geometry = (Geometry) feature.getDefaultGeometry();

            try {
                targetClass.getMethod("setGeom", Geometry.class).invoke(entity, geometry);
            }catch (NoSuchMethodException ignored)
            {
                targetClass.getMethod("setGeog", Geometry.class).invoke(entity, geometry);
            }


            if (feature.getAttribute("OBJECTID")!=null) {
                Double Objectid=Double.parseDouble(feature.getAttribute("OBJECTID").toString());
                targetClass.getMethod("setObjectid", Double.class).invoke(entity, Objectid);
            }

            if (feature.getAttribute("PROVINCIAL")!=null) {
                String provincial=(String) feature.getAttribute("PROVINCIAL");
                targetClass.getMethod("setProvincial", String.class).invoke(entity, provincial);
            }

            if (feature.getAttribute("CITY")!=null) {
                String city=(String) feature.getAttribute("CITY");
                targetClass.getMethod("setCity", String.class).invoke(entity, city);
            }

            if (feature.getAttribute("COUNTRY")!=null) {
                String country=(String) feature.getAttribute("COUNTRY");
                targetClass.getMethod("setCountry", String.class).invoke(entity, country);
            }

            if (feature.getAttribute("IRRNAME")!=null) {
                String irrname=(String) feature.getAttribute("IRRNAME");
                targetClass.getMethod("setIrrname", String.class).invoke(entity, irrname);
            }

            if (feature.getAttribute("IRRTYPE")!=null) {
                String irrtype=(String) feature.getAttribute("IRRTYPE");
                targetClass.getMethod("setIrrtype", String.class).invoke(entity, irrtype);
            }

            if (feature.getAttribute("NAME")!=null) {
                String name = (String) feature.getAttribute("NAME");
                targetClass.getMethod("setName", String.class).invoke(entity, name);
            }

            if (feature.getAttribute("ANGLE")!=null) {
                BigDecimal angle = BigDecimal.valueOf((double)feature.getAttribute("ANGLE"));
                targetClass.getMethod("setAngle", BigDecimal.class).invoke(entity, angle);
            }

            if (feature.getAttribute("REMARK")!=null) {
                String remark = (String) feature.getAttribute("REMARK");
                targetClass.getMethod("setRemark", String.class).invoke(entity, remark);
            }
            if (feature.getAttribute("SHAPE_Leng")!=null) {
                BigDecimal shapeLeng=new BigDecimal(feature.getAttribute("SHAPE_Leng").toString());
                targetClass.getMethod("setShapeLeng", BigDecimal.class).invoke(entity, shapeLeng);
            }
            if (feature.getAttribute("SHAPE_Area")!=null) {
                BigDecimal shapeLeng=new BigDecimal(feature.getAttribute("SHAPE_Area").toString());
                targetClass.getMethod("setShapeArea", BigDecimal.class).invoke(entity, shapeLeng);
            }




            return entity;
        } catch (Exception e) {
            throw new RuntimeException("实体转换失败", e);
        }
    }
}