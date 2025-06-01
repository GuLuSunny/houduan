package com.ydsw.controller;

import cn.hutool.json.JSONObject;
import com.fengwenyi.api.result.ResultTemplate;
import com.ydsw.utils.ProcessBuilderUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.processing.FilerException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.logging.ErrorManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class PythonExeController {
    // 添加日志记录器
    private static final Logger logger = LoggerFactory.getLogger(PythonExeController.class);

    @PostMapping(value = "/api/groupType")
    public ResultTemplate<Object> buildGroupTypeProcess(@RequestBody JSONObject jsonObject) {
        String processName = jsonObject.getStr("processName");
        List<String> commons = jsonObject.getBeanList("commons", String.class);
        List<String> envValues = jsonObject.getBeanList("envValues", String.class);
        Map<String, String> values = new HashMap<>();
        String filepath = "D:\\heigankoumodel\\tudifugaifenlei\\code\\" + processName + ".py";
        String fileRaletivePath = "..\\shuju\\";
        try {
            switch (processName) {
                case "cluster" -> {
                    if (envValues.size() == 2) {
                        values.put("input_tif_path", fileRaletivePath + envValues.get(0));
                        values.put("output_tif_path", fileRaletivePath + envValues.get(1));
                        ProcessBuilderUtils.executePythonScript(filepath, null, values);
                    } else {
                        ProcessBuilderUtils.executePythonScript(filepath);
                    }
                }
                case "特征生成" -> {
                    if (envValues.size() == 4) {
                        values.put("INPUT_HH", fileRaletivePath + envValues.get(0));
                        values.put("INPUT_HV", fileRaletivePath + envValues.get(1));
                        values.put("OUT_GABOR", fileRaletivePath + envValues.get(2));
                        values.put("OUT_POL", fileRaletivePath + envValues.get(3));
                        ProcessBuilderUtils.executeWithLogMonitoring(filepath, null, values);
                    } else {
                        ProcessBuilderUtils.executeWithLogMonitoring(filepath);
                    }
                }
                case "分离波段" -> {
                    if (envValues.size() == 2) {
                        values.put("input_path", fileRaletivePath + envValues.get(0));
                        values.put("output_path", fileRaletivePath + envValues.get(1));
                        ProcessBuilderUtils.executePythonScript(filepath, null, values);
                    } else {
                        ProcessBuilderUtils.executePythonScript(filepath);
                    }
                }
                case "特征筛选" -> {
                    if (envValues.size() == 3) {
                        values.put("LABEL_PATH", fileRaletivePath + envValues.get(0));
                        values.put("FEATURE_DIR", fileRaletivePath + envValues.get(1));
                        values.put("OUTPUT_DIR", fileRaletivePath + envValues.get(2));
                        ProcessBuilderUtils.executePythonScript(filepath, null, values);
                    } else {
                        ProcessBuilderUtils.executePythonScript(filepath);
                    }
                }
                case "样本生成", "特征聚合", "标签聚合" -> {
                    ProcessBuilderUtils.executePythonScript(filepath, null, values);
                }
                case "RF", "XG", "SVM" -> ProcessBuilderUtils.executeWithRealTimeOutput(filepath);
                case "MLP" -> ProcessBuilderUtils.executeWithRealTimeOutput(filepath, null, "UTF-8");
                default -> {
                    return ResultTemplate.fail("非法操作！");
                }
            }
        } catch (RuntimeException e) {

            return ResultTemplate.fail("数据处理失败!");
        } catch (Exception e) {
            return ResultTemplate.fail("未知错误");
        }
        return ResultTemplate.success();
    }

    @PostMapping(value = "/api/groupType/all")
    public ResultTemplate<Object> buildGroupTypeProcess(@Param("tiffile") MultipartFile tiffile) {
        String filename = tiffile.getOriginalFilename();
        if (filename == null) {
            return ResultTemplate.fail("非法访问!");
        }

        // 验证文件类型 (支持.tif和.TIF)
        String fileType = filename.substring(filename.lastIndexOf("."));
        if (!fileType.equalsIgnoreCase(".tif")) {
            return ResultTemplate.fail("非法文件类型，仅支持TIFF格式");
        }

        // 创建保存目录
        String baseDir = "D:\\heigankoumodel\\tudifugaifenlei\\shuju\\";
        File dir = new File(baseDir);
        if (!dir.exists() && !dir.mkdirs()) {
            return ResultTemplate.fail("目录创建失败！请联系管理员");
        }

        // 构建完整保存路径 - 使用固定文件名
        String savePath = baseDir + "yanmo_25_snic_tem.tif";
        File saveFile = new File(savePath);


        // 保存文件
        try {
            tiffile.transferTo(saveFile);
        } catch (IOException e) {
            // 记录详细日志但返回通用错误信息
            logger.error("文件保存失败: {}", e.getMessage(), e);
            return ResultTemplate.fail("文件上传失败，请稍后重试");
        }

        // 准备执行Python脚本
        Map<String, String> values = new HashMap<>();
        values.put("input_tif_path", savePath);
        List<String> filenames = Arrays.asList("cluster", "特征筛选", "分离波段", "特征生成",
                "样本生成", "特征聚合", "标签聚合", "RF", "XG", "SVM", "MLP");

        try {
            for (String pyFileName : filenames) {
                String filepath = "D:\\heigankoumodel\\tudifugaifenlei\\code\\" + pyFileName + ".py";
                switch (pyFileName) {
                    case "cluster", "特征筛选", "分离波段" -> ProcessBuilderUtils.executePythonScript(filepath);
                    case "特征生成" -> ProcessBuilderUtils.executeWithLogMonitoring(filepath);
                    case "样本生成", "特征聚合", "标签聚合" ->
                            ProcessBuilderUtils.executePythonScript(filepath, null, values);
                    case "RF", "XG", "SVM" -> ProcessBuilderUtils.executeWithRealTimeOutput(filepath);
                    case "MLP" -> ProcessBuilderUtils.executeWithRealTimeOutput(filepath, null, "UTF-8");
                    default -> throw new IllegalArgumentException("非法的Python脚本名称: " + pyFileName);
                }
            }
        } catch (Exception e) {
            // 记录详细日志但返回通用错误信息
            logger.error("处理过程中出错: {}", e.getMessage(), e);

            // 出错时删除已保存文件
            deleteFileWithRetry(saveFile);
            return ResultTemplate.fail("处理失败，请联系管理员");
        } finally {
            // 确保文件最终被删除
            deleteFileWithRetry(saveFile);
        }
        return ResultTemplate.success();
    }

    // 重试删除文件的方法
    private void deleteFileWithRetry(File file) {
        if (file != null && file.exists()) {
            int maxAttempts = 3;
            for (int i = 0; i < maxAttempts; i++) {
                if (file.delete()) {
                    return; // 删除成功
                }
                // 删除失败时等待并重试
                System.gc(); // 触发垃圾回收释放文件句柄
                try {
                    Thread.sleep(1000); // 等待1秒
                } catch (InterruptedException ignored) {
                }
            }
            logger.warn("文件删除失败: {}", file.getAbsolutePath());
        }
    }
}