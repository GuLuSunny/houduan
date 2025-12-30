package com.ydsw.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.io.File;
/**
 * ZIP文件工具类
 * 功能：检查ZIP文件是否包含特定的目录结构和文件
 *
 * @version 2.0
 */
@Slf4j
@Component
public class ZipFileUtils {

    /**
     * 根据模型名称获取所需的TIF文件列表
     */
    public static List<String> getRequiredFilesByModel(String modelName) {
        if ("fanyanV2".equalsIgnoreCase(modelName)) {
            return Arrays.asList(
                    "Alpha.tif", "Entropy.tif", "C11.tif", "C12_imag.tif",
                    "C22.tif", "HA.tif", "DpRVI.tif"
            );
        } else if ("fanyanRF".equalsIgnoreCase(modelName)) {
            return Arrays.asList(
                    "1mH1mA.tif", "Alpha.tif", "Entropy.tif", "Free_Vol.tif", "HV.tif",
                    "l1.tif", "Pauli_b.tif", "Span.tif", "VV.tif", "VZ_Dbl.tif",
                    "VZ_Vol.tif", "Yama_Surf.tif", "Yama_Vol.tif"
            );
        }
        return null; // 其他模型不需要验证
    }

    /**
     * 检查ZIP文件根目录是否包含所有必需的TIFF文件
     *
     * @param zipFilePath ZIP文件路径
     * @return boolean 是否包含所有必需的TIFF文件
     * @throws IOException 文件操作异常
     */
    // 在 ZipFileUtils 类中添加这个方法
    public static boolean containsRequiredTiffFiles(String zipFilePath, List<String> requiredFiles) {
        try (ZipFile zipFile = new ZipFile(zipFilePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            Set<String> fileNames = new HashSet<>();

            // 收集ZIP文件中的所有文件名（转换为小写）
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (!entry.isDirectory()) {
                    String fileName = new File(entry.getName()).getName();
                    fileNames.add(fileName.toLowerCase()); // 统一转换为小写
                }
            }

            // 检查是否包含所有必需的文件（也转换为小写比较）
            for (String requiredFile : requiredFiles) {
                if (!fileNames.contains(requiredFile.toLowerCase())) {
                    log.warn("缺少必需文件: {}", requiredFile);
                    return false;
                }
            }

            return true;
        } catch (IOException e) {
            log.error("检查ZIP文件失败", e);
            return false;
        }
    }

    /**
     * 在保存前验证文件
     */
    public static List<String> validateFilesBeforeSaving(MultipartFile[] files, String modelName) {
        List<String> errors = new ArrayList<>();

        if (files == null || files.length == 0) {
            errors.add("未选择任何文件");
            return errors;
        }

        boolean hasZipFile = false;
        List<String> tifFileNames = new ArrayList<>();
        List<String> otherFiles = new ArrayList<>();

        // 收集文件信息
        for (MultipartFile file : files) {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                errors.add("存在无文件名的文件");
                continue;
            }

            String fileExtension = "";
            if (originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            if (fileExtension.equalsIgnoreCase(".zip")) {
                if (hasZipFile) {
                    errors.add("只能上传一个ZIP文件");
                }
                hasZipFile = true;
            } else if (fileExtension.equalsIgnoreCase(".tif") || fileExtension.equalsIgnoreCase(".tiff")) {
                tifFileNames.add(originalFilename);
            } else {
                otherFiles.add(originalFilename);
            }
        }

        // 检查不支持的文件类型
        if (!otherFiles.isEmpty()) {
            errors.add("不支持的文件类型: " + String.join(", ", otherFiles));
        }

        // ZIP和TIF文件不能混合上传
        if (hasZipFile && !tifFileNames.isEmpty()) {
            errors.add("ZIP文件和TIF文件不能同时上传");
            return errors;
        }

        // TIF文件验证
        if (!tifFileNames.isEmpty()) {
            List<String> requiredFiles = getRequiredFilesByModel(modelName);
            if (requiredFiles != null && !requiredFiles.isEmpty()) {
                boolean hasAllRequiredFiles = hasAllRequiredTifFiles(tifFileNames, requiredFiles);
                if (!hasAllRequiredFiles) {
                    errors.add("缺少必要的TIF文件。所需文件: " + requiredFiles + "，实际上传文件: " + tifFileNames);
                }
            }
        }

        return errors;
    }

    /**
     * 保存后对ZIP文件进行详细验证
     */
    public static List<String> validateZipFilesAfterSaving(List<String> savedFilePaths, String modelName) {
        List<String> errors = new ArrayList<>();

        for (String filePath : savedFilePaths) {
            if (filePath.toLowerCase().endsWith(".zip")) {
                List<String> requiredFiles = getRequiredFilesByModel(modelName);
                if (requiredFiles != null && !requiredFiles.isEmpty()) {
                    boolean hasRequiredFiles = ZipFileUtils.containsRequiredTiffFiles(filePath, requiredFiles);
                    if (!hasRequiredFiles) {
                        errors.add("ZIP文件缺少必要的TIF文件。所需文件: " + requiredFiles);
                    }
                }
            }
        }

        return errors;
    }



    /**
     * 检查是否包含所有必需的TIF文件
     */
    public static boolean hasAllRequiredTifFiles(List<String> actualFiles, List<String> requiredFiles) {
        Set<String> actualFileSet = actualFiles.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        for (String requiredFile : requiredFiles) {
            if (!actualFileSet.contains(requiredFile.toLowerCase())) {
                return false;
            }
        }
        return true;
    }
    /**
     * 过滤出需要的文件
     * 只保留：单个ZIP文件 或 所需的TIF文件
     */
    public static List<MultipartFile> filterRequiredFiles(MultipartFile[] files, String modelName) {
        List<MultipartFile> filesToSave = new ArrayList<>();
        List<String> requiredFiles = getRequiredFilesByModel(modelName);

        if (requiredFiles == null || requiredFiles.isEmpty()) {
            return filesToSave;
        }

        boolean hasZipFile = false;

        for (MultipartFile file : files) {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                continue;
            }

            String fileExtension = "";
            if (originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            if (fileExtension.equalsIgnoreCase(".zip")) {
                // 只保存第一个ZIP文件，忽略后续的ZIP文件
                if (!hasZipFile) {
                    filesToSave.add(file);
                    hasZipFile = true;
                }
            } else if (fileExtension.equalsIgnoreCase(".tif") || fileExtension.equalsIgnoreCase(".tiff")) {
                // 只保存所需的TIF文件
                String fileName = new File(originalFilename).getName();
                if (requiredFiles.contains(fileName)) {
                    filesToSave.add(file);
                }
            }
            // 其他类型文件直接忽略，不保存
        }

        return filesToSave;
    }







    /**
     * 判断条目是否在根目录下
     */
    private static boolean isInRootDirectory(String entryName) {
        return !entryName.contains("/") || entryName.lastIndexOf('/') == 0;
    }

    /**
     * 从路径中提取文件名
     */
    private static String getFileNameFromPath(String path) {
        if (path.contains("/")) {
            return path.substring(path.lastIndexOf('/') + 1);
        }
        return path;
    }

    /**
     * 检查ZIP条目是否匹配所需的目录结构
     */
    private static void checkEntryForRequiredDirs(String entryName, List<String> requiredDirs, Set<String> foundDirs) {
        for (String requiredDir : requiredDirs) {
            if (foundDirs.contains(requiredDir)) continue;

            String normalizedDir = requiredDir.endsWith("/") ? requiredDir : requiredDir + "/";

            if (entryName.equals(requiredDir) || entryName.equals(normalizedDir) ||
                    entryName.startsWith(normalizedDir)) {
                foundDirs.add(requiredDir);
                log.debug("找到目录 '{}', 匹配条目: '{}'", requiredDir, entryName);
            }
        }
    }

    /**
     * 获取ZIP文件中所有的目录列表
     */
    public static Set<String> getAllDirectories(String zipFilePath) throws IOException {
        Set<String> directories = new HashSet<>();

        try (ZipFile zipFile = new ZipFile(zipFilePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory()) {
                    directories.add(entry.getName());
                } else {
                    String name = entry.getName();
                    int lastSlash = name.lastIndexOf('/');
                    if (lastSlash > 0) {
                        String directory = name.substring(0, lastSlash + 1);
                        directories.add(directory);
                    }
                }
            }
        }

        return directories;
    }

    /**
     * 解压ZIP文件中的必需文件到目标目录
     *
     * @param zipFilePath ZIP文件路径
     * @param destDir 目标目录
     * @param requiredFiles 必需文件列表
     * @return 解压成功的文件列表
     * @throws IOException 文件操作异常
     */
    public static List<String> unzipRequiredFiles(String zipFilePath, String destDir, List<String> requiredFiles) throws IOException {
        List<String> extractedFiles = new ArrayList<>();

        try (ZipFile zipFile = new ZipFile(zipFilePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (!entry.isDirectory()) {
                    String fileName = new File(entry.getName()).getName(); // 只取文件名，忽略路径

                    // 只解压必需的文件
                    if (requiredFiles.contains(fileName)) {
                        File destFile = new File(destDir, fileName);

                        // 确保目标目录存在
                        File parentDir = destFile.getParentFile();
                        if (!parentDir.exists()) {
                            parentDir.mkdirs();
                        }

                        // 解压文件
                        try (InputStream is = zipFile.getInputStream(entry);
                             FileOutputStream fos = new FileOutputStream(destFile)) {

                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = is.read(buffer)) > 0) {
                                fos.write(buffer, 0, length);
                            }
                        }

                        extractedFiles.add(destFile.getAbsolutePath());
                        log.info("解压文件: {} -> {}", entry.getName(), destFile.getAbsolutePath());
                    }
                }
            }
        }

        return extractedFiles;
    }

    /**
     * 删除指定的文件
     *
     * @param filePath 文件路径
     * @return 是否删除成功
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                log.info("删除文件: {}", filePath);
            } else {
                log.warn("删除文件失败: {}", filePath);
            }
            return deleted;
        }
        return true; // 文件不存在视为删除成功
    }

    /**
     * 检查ZIP文件是否包含指定扩展名的文件
     */
    public static boolean containsFilesWithExtensions(String zipFilePath, String... extensions) throws IOException {
        try (ZipFile zipFile = new ZipFile(zipFilePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            Set<String> extensionSet = Arrays.stream(extensions)
                    .map(ext -> ext.toLowerCase())
                    .collect(Collectors.toSet());

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (!entry.isDirectory()) {
                    String fileName = new File(entry.getName()).getName().toLowerCase();
                    for (String ext : extensionSet) {
                        if (fileName.endsWith(ext)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * 获取ZIP文件中所有文件的扩展名集合
     */
    public static Set<String> getAllFileExtensions(String zipFilePath) throws IOException {
        Set<String> extensions = new HashSet<>();

        try (ZipFile zipFile = new ZipFile(zipFilePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (!entry.isDirectory()) {
                    String fileName = new File(entry.getName()).getName();
                    int dotIndex = fileName.lastIndexOf('.');
                    if (dotIndex > 0) {
                        extensions.add(fileName.substring(dotIndex).toLowerCase());
                    }
                }
            }
        }

        return extensions;
    }
    /**
     * 验证ZIP文件是否包含有效的SHP文件组
     */
    static List<String> validateZipForShpFiles(String zipFilePath) throws IOException {
        List<String> errors = new ArrayList<>();

        try (ZipFile zipFile = new ZipFile(zipFilePath)) {
            // 按基本名分组收集文件
            Map<String, Set<String>> baseNameGroups = new HashMap<>();

            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (!entry.isDirectory()) {
                    String entryName = entry.getName();
                    String fileName = new File(entryName).getName();

                    // 提取基本名（不带扩展名）
                    int dotIndex = fileName.lastIndexOf('.');
                    if (dotIndex > 0) {
                        String baseName = fileName.substring(0, dotIndex);
                        String extension = fileName.substring(dotIndex).toLowerCase();

                        baseNameGroups.computeIfAbsent(baseName, k -> new HashSet<>())
                                .add(extension);
                    }
                }
            }

            // 检查每个分组是否包含必要的文件
            for (Map.Entry<String, Set<String>> entry : baseNameGroups.entrySet()) {
                String baseName = entry.getKey();
                Set<String> extensions = entry.getValue();

                // 检查是否包含.shp文件
                if (!extensions.contains(".shp")) {
                    continue; // 不是SHP文件组，跳过
                }

                // 检查必要的配套文件
                if (!extensions.contains(".dbf")) {
                    errors.add("SHP文件组 '" + baseName + "' 缺少 .dbf 文件");
                }
                if (!extensions.contains(".shx")) {
                    errors.add("SHP文件组 '" + baseName + "' 缺少 .shx 文件");
                }
            }

            // 如果没有任何.shp文件
            boolean hasShpFile = baseNameGroups.values().stream()
                    .anyMatch(extensions -> extensions.contains(".shp"));
            if (!hasShpFile) {
                errors.add("ZIP文件中未找到.shp文件");
            }
        }

        return errors;
    }

    /**
     * 从ZIP文件中提取SHP文件及其配套文件
     */
    static List<File> extractShpFilesFromZip(String zipFilePath, File destDir) throws IOException {
        List<File> extractedFiles = new ArrayList<>();

        try (ZipFile zipFile = new ZipFile(zipFilePath)) {
            // 先收集所有.shp文件的基本名
            Set<String> shpBaseNames = new HashSet<>();
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (!entry.isDirectory()) {
                    String fileName = new File(entry.getName()).getName().toLowerCase();
                    if (fileName.endsWith(".shp")) {
                        String baseName = fileName.substring(0, fileName.lastIndexOf('.'));
                        shpBaseNames.add(baseName);
                    }
                }
            }

            // 重新遍历，提取所有匹配基本名的文件
            entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (!entry.isDirectory()) {
                    String entryName = entry.getName();
                    String fileName = new File(entryName).getName();
                    String baseName = fileName.substring(0, fileName.lastIndexOf('.'));

                    if (shpBaseNames.contains(baseName.toLowerCase())) {
                        // 提取文件
                        File destFile = new File(destDir, fileName);

                        // 确保父目录存在
                        File parentDir = destFile.getParentFile();
                        if (!parentDir.exists()) {
                            parentDir.mkdirs();
                        }

                        try (java.io.InputStream is = zipFile.getInputStream(entry);
                             java.io.FileOutputStream fos = new java.io.FileOutputStream(destFile)) {

                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = is.read(buffer)) > 0) {
                                fos.write(buffer, 0, length);
                            }
                        }

                        extractedFiles.add(destFile);
                    }
                }
            }
        }

        return extractedFiles;
    }
}