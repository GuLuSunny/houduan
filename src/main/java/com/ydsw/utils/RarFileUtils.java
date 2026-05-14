package com.ydsw.utils;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import lombok.extern.slf4j.Slf4j;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.simple.ISimpleInArchive;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * RAR 工具类（基于 7-Zip-JBinding）
 * 支持：RAR5、RAR4、ZIP、7Z、中文、加密、跨平台
 */
@Slf4j
@Component
public class RarFileUtils {

    // ==================== 你们原有业务逻辑 ====================
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
        return Collections.emptyList();
    }

    // ==================== 检查 RAR 包含文件（支持 RAR5） ====================
    public static boolean containsRequiredTiffFiles(String rarFilePath, List<String> requiredFiles) {
        Set<String> fileNames = new HashSet<>();

        try (RandomAccessFile raf = new RandomAccessFile(rarFilePath, "r");
             IInArchive inArchive = SevenZip.openInArchive(null, new RandomAccessFileInStream(raf))) {

            ISimpleInArchive simple = inArchive.getSimpleInterface();
            for (ISimpleInArchiveItem item : simple.getArchiveItems()) {
                if (!item.isFolder()) {
                    String name = new File(item.getPath()).getName().toLowerCase();
                    fileNames.add(name);
                }
            }

            for (String req : requiredFiles) {
                if (!fileNames.contains(req.toLowerCase())) {
                    log.warn("缺少必需文件：{}", req);
                    return false;
                }
            }
            return true;

        } catch (Exception e) {
            log.error("检查RAR文件失败", e);
            return false;
        }
    }

    // ==================== 解压指定文件（完美支持 RAR5） ====================
    public static List<String> unzipRequiredFiles(String rarFilePath, String destDir, List<String> requiredFiles) {
        List<String> extracted = new ArrayList<>();
        new File(destDir).mkdirs();

        try (RandomAccessFile raf = new RandomAccessFile(rarFilePath, "r");
             IInArchive inArchive = SevenZip.openInArchive(null, new RandomAccessFileInStream(raf))) {

            ISimpleInArchive simple = inArchive.getSimpleInterface();

            for (ISimpleInArchiveItem item : simple.getArchiveItems()) {
                if (item.isFolder()) continue;

                String fileName = new File(item.getPath()).getName();
                if (requiredFiles.contains(fileName)) {
                    File outFile = new File(destDir, fileName);

                    try (FileOutputStream fos = new FileOutputStream(outFile)) {
                        item.extractSlow(new FileOutStreamAdapter(fos));
                        extracted.add(outFile.getAbsolutePath());
                        log.info("解压成功：{}", outFile.getAbsolutePath());
                    }
                }
            }
        } catch (Exception e) {
            log.error("解压RAR失败", e);
        }
        return extracted;
    }

    public static List<String> validateRarFilesBeforeSaving(MultipartFile[] files, String modelName) {
        List<String> errors = new ArrayList<>();
        if (files == null || files.length == 0) {
            errors.add("未选择任何文件");
            return errors;
        }

        boolean hasRar = false;
        List<String> tifNames = new ArrayList<>();
        List<String> otherFiles = new ArrayList<>();

        for (MultipartFile f : files) {
            String fname = f.getOriginalFilename();
            if (fname == null) {
                errors.add("存在无文件名文件");
                continue;
            }
            String ext = fname.substring(fname.lastIndexOf(".")).toLowerCase();
            if (ext.equals(".rar")) {
                if (hasRar) errors.add("只能上传一个RAR");
                hasRar = true;
            } else if (ext.equals(".tif") || ext.equals(".tiff")) {
                tifNames.add(fname);
            } else {
                otherFiles.add(fname);
            }
        }

        if (!otherFiles.isEmpty()) errors.add("不支持的文件：" + String.join(",", otherFiles));
        if (hasRar && !tifNames.isEmpty()) errors.add("RAR 与 TIF 不能同时上传");
        if (!tifNames.isEmpty()) {
            List<String> req = getRequiredFilesByModel(modelName);
            if (!req.isEmpty() && !hasAllRequiredTifFiles(tifNames, req)) {
                errors.add("缺少必需TIF：" + req);
            }
        }
        return errors;
    }

    public static List<String> validateRarFilesAfterSaving(List<String> paths, String model) {
        List<String> errors = new ArrayList<>();
        for (String p : paths) {
            if (p.toLowerCase().endsWith(".rar")) {
                List<String> req = getRequiredFilesByModel(model);
                if (!req.isEmpty() && !containsRequiredTiffFiles(p, req)) {
                    errors.add("RAR 缺少必需的 TIF 文件");
                }
            }
        }
        return errors;
    }

    public static boolean hasAllRequiredTifFiles(List<String> actual, List<String> req) {
        Set<String> set = actual.stream().map(String::toLowerCase).collect(Collectors.toSet());
        for (String s : req) if (!set.contains(s.toLowerCase())) return false;
        return true;
    }

    public static List<MultipartFile> filterRequiredFiles(MultipartFile[] files, String model) {
        List<MultipartFile> res = new ArrayList<>();
        List<String> req = getRequiredFilesByModel(model);
        boolean hasRar = false;
        for (MultipartFile f : files) {
            String name = f.getOriginalFilename();
            if (name == null) continue;
            String ext = name.substring(name.lastIndexOf(".")).toLowerCase();
            if (ext.equals(".rar")) {
                if (!hasRar) {
                    res.add(f);
                    hasRar = true;
                }
            } else if (ext.equals(".tif") || ext.equals(".tiff")) {
                String simple = new File(name).getName();
                if (req.contains(simple)) res.add(f);
            }
        }
        return res;
    }

    public static boolean deleteFile(String path) {
        File f = new File(path);
        if (f.exists()) {
            boolean del = f.delete();
            if (del) log.info("已删除：{}", path);
            else log.warn("删除失败：{}", path);
            return del;
        }
        return true;
    }

    public static boolean containsFilesWithExtensions(String rarPath, String... exts) {
        try (RandomAccessFile raf = new RandomAccessFile(rarPath, "r");
             IInArchive inArchive = SevenZip.openInArchive(null, new RandomAccessFileInStream(raf))) {

            Set<String> extSet = Arrays.stream(exts).map(String::toLowerCase).collect(Collectors.toSet());
            ISimpleInArchive simple = inArchive.getSimpleInterface();

            for (ISimpleInArchiveItem item : simple.getArchiveItems()) {
                if (!item.isFolder()) {
                    String name = item.getPath().toLowerCase();
                    for (String e : extSet) {
                        if (name.endsWith(e)) return true;
                    }
                }
            }
        } catch (Exception e) {
            log.error("检查文件后缀失败", e);
        }
        return false;
    }

    public static Set<String> getAllFileExtensions(String rarPath) {
        Set<String> exts = new HashSet<>();
        try (RandomAccessFile raf = new RandomAccessFile(rarPath, "r");
             IInArchive inArchive = SevenZip.openInArchive(null, new RandomAccessFileInStream(raf))) {

            ISimpleInArchive simple = inArchive.getSimpleInterface();
            for (ISimpleInArchiveItem item : simple.getArchiveItems()) {
                if (!item.isFolder()) {
                    String name = item.getPath();
                    int dot = name.lastIndexOf('.');
                    if (dot > 0) exts.add(name.substring(dot).toLowerCase());
                }
            }
        } catch (Exception e) {
            log.error("获取文件后缀失败", e);
        }
        return exts;
    }

    public static List<String> validateZipForShpFiles(String rarPath) {
        List<String> errors = new ArrayList<>();
        Map<String, Set<String>> groups = new HashMap<>();

        try (RandomAccessFile raf = new RandomAccessFile(rarPath, "r");
             IInArchive inArchive = SevenZip.openInArchive(null, new RandomAccessFileInStream(raf))) {

            ISimpleInArchive simple = inArchive.getSimpleInterface();
            for (ISimpleInArchiveItem item : simple.getArchiveItems()) {
                if (item.isFolder()) continue;
                String name = item.getPath();
                int dot = name.lastIndexOf('.');
                if (dot > 0) {
                    String base = name.substring(0, dot);
                    String ext = name.substring(dot).toLowerCase();
                    groups.computeIfAbsent(base, k -> new HashSet<>()).add(ext);
                }
            }

            boolean hasShp = false;
            for (Map.Entry<String, Set<String>> entry : groups.entrySet()) {
                Set<String> e = entry.getValue();
                if (e.contains(".shp")) {
                    hasShp = true;
                    if (!e.contains(".dbf")) errors.add(entry.getKey() + " 缺少 .dbf");
                    if (!e.contains(".shx")) errors.add(entry.getKey() + " 缺少 .shx");
                }
            }
            if (!hasShp) errors.add("未找到shp文件");
        } catch (Exception e) {
            errors.add("打开RAR失败");
        }
        return errors;
    }

    public static List<File> extractShpFilesFromZip(String rarPath, File destDir) {
        List<File> extracted = new ArrayList<>();
        Set<String> shpBases = new HashSet<>();
        destDir.mkdirs();

        try (RandomAccessFile raf = new RandomAccessFile(rarPath, "r");
             IInArchive inArchive = SevenZip.openInArchive(null, new RandomAccessFileInStream(raf))) {

            ISimpleInArchive simple = inArchive.getSimpleInterface();

            // 第一步：收集所有shp
            for (ISimpleInArchiveItem item : simple.getArchiveItems()) {
                if (!item.isFolder() && item.getPath().toLowerCase().endsWith(".shp")) {
                    String name = item.getPath();
                    shpBases.add(name.substring(0, name.lastIndexOf('.')).toLowerCase());
                }
            }

            // 第二步：解压
            for (ISimpleInArchiveItem item : simple.getArchiveItems()) {
                if (item.isFolder()) continue;
                String name = item.getPath();
                String base = name.substring(0, name.lastIndexOf('.')).toLowerCase();
                if (shpBases.contains(base)) {
                    File out = new File(destDir, name);
                    out.getParentFile().mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(out)) {
                        item.extractSlow(new FileOutStreamAdapter(fos));
                    }
                    extracted.add(out);
                }
            }
        } catch (Exception e) {
            log.error("SHP解压失败", e);
        }
        return extracted;
    }
    /**
     * 自定义适配器：将 FileOutputStream 转为 7-Zip 需要的 ISequentialOutStream
     */
    private static class FileOutStreamAdapter implements ISequentialOutStream {
        private final FileOutputStream fos;

        public FileOutStreamAdapter(FileOutputStream fos) {
            this.fos = fos;
        }

        @Override
        public int write(byte[] data) throws SevenZipException {
            try {
                fos.write(data);
                return data.length;
            } catch (IOException e) {
                throw new SevenZipException("写入文件失败", e);
            }
        }
    }
}