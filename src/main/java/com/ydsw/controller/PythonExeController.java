package com.ydsw.controller;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fengwenyi.api.result.ResultTemplate;
import com.ydsw.domain.ModelFileStatus;
import com.ydsw.domain.ModelStatus;
import com.ydsw.domain.User;
import com.ydsw.service.ModelFileStatusService;
import com.ydsw.service.ModelStatusService;
import com.ydsw.service.UserService;
import com.ydsw.utils.ProcessBuilderUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class PythonExeController {
    // 添加日志记录器
    private static final Logger logger = LoggerFactory.getLogger(PythonExeController.class);
    @Autowired
    private UserService userService;

    @Autowired
    private ModelFileStatusService modelFileStatusService;
    @Autowired
    private ModelStatusService modelStatusService;
    
    private final String codeRootPath = "D:"+File.separator+"recognition"+File.separator+"code"+File.separator;
    final private String FileRootDirPath="D:"+File.separator+"recognition"+File.separator+"fileTemp"+File.separator;
    private final String waterChangePYPath="D:/recognition/code/waterchangeforuse.py";
    private final String condaPYPath="C:\\Users\\lenovo\\miniconda3\\envs\\version12\\python.exe";

    @PreAuthorize("hasAnyAuthority('api_groupType')")
    @PostMapping(value = "/api/groupType")
    public ResultTemplate<Object> buildGroupTypeProcess(@RequestBody JSONObject jsonObject) {
        String processName = jsonObject.getStr("processName");
        List<String> commons = jsonObject.getBeanList("commons", String.class);
        List<String> envValues = jsonObject.getBeanList("envValues", String.class);
        Map<String, String> values = new HashMap<>();
        String filepath = codeRootPath + processName + ".py";
        String fileRaletivePath = ".."+File.separator+"shuju"+File.separator;
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

    @PreAuthorize("hasAnyAuthority('api_groupType_all')")
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
        String baseDir = "D:/recognition/shuju/";
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
                String filepath = codeRootPath + pyFileName + ".py";
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

    @PreAuthorize("hasAnyAuthority('api_groupType_predict')")
    @PostMapping(value = "/api/groupType/predict")
    public ResultTemplate<Object> useModels(@RequestBody JSONObject jsonObject)
    {
        String modelName = jsonObject.getStr("modelName");
        String preview_png= Objects.equals(jsonObject.getStr("preview_png"), "False") ?"False" :"True";
        String confusion_matrix=Objects.equals(jsonObject.getStr("confusion_matrix"), "False") ?"False" :"True";
        String class_stats=Objects.equals(jsonObject.getStr("class_stats"), "False") ?"False" :"True";
        String heatmaps_summary=Objects.equals(jsonObject.getStr("heatmaps_summary"), "False") ?"False" :"True";
        String color_map=jsonObject.getStr("color_map");
        String userName=jsonObject.getStr("userName");
        String createUserId=jsonObject.getStr("createUserId");
        String input_dir=jsonObject.getStr("input_dir");
        String observationTime=jsonObject.getStr("observationTime")==null?"":jsonObject.getStr("observationTime");
        String funcitionSelected="null";
        String startTime=jsonObject.getStr("firstTime")==null?"":jsonObject.getStr("firstTime");
        String endTime=jsonObject.getStr("secondTime")==null?"":jsonObject.getStr("secondTime");
        if (observationTime==null&&(startTime==null&&endTime==null)) {
            return ResultTemplate.fail("未知的时间段！");
        }
        funcitionSelected=funcitionSelected.replace("null","");
        String className="land";
        if(preview_png.equals("True"))
        {
            funcitionSelected+="preview_png";
        }
        if(confusion_matrix.equals("True"))
        {
            funcitionSelected+=",confusion_matrix";
        }
        if(class_stats.equals("True"))
        {
            funcitionSelected+=",class_stats";
        }
        if(heatmaps_summary.equals("True"))
        {
            funcitionSelected+=",heatmaps_summary";
        }
        if(funcitionSelected.startsWith(","))
        {
            funcitionSelected=funcitionSelected.substring(1);
        }
        User user=new User();
        user.setUsername(userName);
        user.setStatus(0);
        List<Map<String,Object>> userList= userService.selectUserByCondition(user);
        boolean flag=false;
        for (Map<String,Object> map : userList) {
            if(Objects.equals(map.get("id").toString(), createUserId)){
                flag=true;
            }
        }
        if(!flag){
            return ResultTemplate.fail("非法用户！");
        }
        user.setId(Integer.valueOf(createUserId));
        user.setMemo(modelName);
        if(!couldVisit(modelName,observationTime))
        {
            return ResultTemplate.fail("服务器繁忙，请稍后重试。");
        }
        Map<String, String> values = new HashMap<>();
        String processname="";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String formattedDate = sdf.format(new Date());
        String filename=userName+"-"+createUserId+"_"+observationTime+"_"+formattedDate+"_HH";
        switch (modelName){
            case "mlp", "rf", "svm", "xgb" -> processname = "predict";
            case "XGB","CNN" -> processname = "predictV2";
            case "rfV2","CNNV2" -> processname = "predictV3";
            default -> {
                return ResultTemplate.fail("非法的参数名！");
            }
        }
        ModelFileStatus modelFileStatus=new ModelFileStatus();
        modelFileStatus.setDealStatus("success");
        modelFileStatus.setClassName(className);
        modelFileStatus.setUserName(userName);
        modelFileStatus.setCreateUserid(createUserId);
        modelFileStatus.setObservationTime(observationTime);
        if(processname.equals("predictV2"))
        {
            List<Map<String,Object>> mapList=modelFileStatusService.selectUserAndFileStatus(modelFileStatus);
            if(!mapList.isEmpty())
            {
                String filepath=mapList.get(0).get("filepath").toString();
                filepath=filepath.replace("\\\\","\\");
                filepath=filepath.replace("//","/");
                //filename=className+"/"+filepath.substring(filepath.lastIndexOf("/")+1);
                filename=filepath.replace(FileRootDirPath,"");
            }else {
                return ResultTemplate.fail("请先提交目标文件！");
            }
        } else if (processname.equals("predictV3")) {
            modelFileStatus.setType("multiple");
            modelFileStatus.setObservationTime(null);
            modelFileStatus.setStartTime(startTime);
            modelFileStatus.setEndTime(endTime);
            List<Map<String,Object>> mapList=modelFileStatusService.selectUserAndFileStatus(modelFileStatus);
            if(!mapList.isEmpty())
            {
                String filepath=mapList.get(0).get("filepath").toString();
                filepath=filepath.replace("\\\\","\\");
                filepath=filepath.replace("//","/");
                input_dir=filepath;
                mapFilesInDirectory(filepath,values);
                //filename=className+"/"+filepath.substring(filepath.lastIndexOf("/")+1);
                filename=filepath.replace(FileRootDirPath,"");
            }else {
                return ResultTemplate.fail("请先提交目标文件！");
            }
        }
        String filepath = codeRootPath + processname + ".py";
        try {
            switch (modelName) {
                case "mlp", "rf", "svm", "xgb","XGB","CNN","rfV2","CNNV2" -> values.put("modelSelected",modelName);
                default -> {
                    return ResultTemplate.fail("非法的参数名！");
                }
            }
            values.put("preview_png",preview_png);
            values.put("confusion_matrix",confusion_matrix);
            values.put("class_stats",class_stats);
            values.put("heatmaps_summary",heatmaps_summary);
            values.put("createUserid",createUserId);
            values.put("userName",userName);
            values.put("createTime",new Date().toString());
            values.put("filename",filename);
            if(color_map!=null) {
                values.put("color_map_str", color_map);
            }
            if(input_dir!=null) {
                values.put("input_dir", input_dir);
            }
            user.setAddress(funcitionSelected);
            user.setProductionCompany(className);
            user.setPassword(observationTime);
            user.setEmail(startTime);
            user.setTel(endTime);
            ProcessBuilderUtils.executeInBackground(filepath,null,values,user);
        }catch (RuntimeException e) {

            return ResultTemplate.fail("数据处理失败!");
        } catch (Exception e) {
            return ResultTemplate.fail("未知错误");
        }
        return ResultTemplate.success("预测已开始，请稍后查询。预计十分钟内完成。");
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


    private static void mapFilesInDirectory(String dirPath, Map<String, String> values) {
        File dir = new File(dirPath);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        String name = file.getName();
                        if (name.endsWith("pre_VV.tif")) values.put("t1_vv_path", file.getAbsolutePath());
                        else if (name.endsWith("pre_VH.tif")) values.put("t1_vh_path", file.getAbsolutePath());
                        else if (name.endsWith("next_VV.tif")) values.put("t2_vv_path", file.getAbsolutePath());
                        else if (name.endsWith("next_VH.tif")) values.put("t2_vh_path", file.getAbsolutePath());
                    }
                }
            }
        }

    }

    @PreAuthorize("hasAnyAuthority('api_plantCover')")
    @PostMapping(value = "/api/plantCover")
    public ResultTemplate<Object> buildplantCoverProcessWithoutResult(@RequestBody JSONObject jsonObject) {
        String modelName = jsonObject.getStr("modelName");
        String preview_png= Objects.equals(jsonObject.getStr("preview_png"), "False") ?"False" :"True";
        String class_stats= Objects.equals(jsonObject.getStr("class_stats"), "false") ?"false":"True";
        String userName=jsonObject.getStr("userName");
        String createUserId=jsonObject.getStr("createUserId");
        String className="plant";
        String observationTime=jsonObject.getStr("observationTime");
        String funcitionSelected = "";
        User user=new User();
        user.setUsername(userName);
        user.setStatus(0);
        List<Map<String,Object>> userList= userService.selectUserByCondition(user);
        boolean flag=false;
        for (Map<String,Object> map : userList) {
            if(Objects.equals(map.get("id").toString(), createUserId)){
                flag=true;
            }
        }
        if(!flag){
            return ResultTemplate.fail("非法用户！");
        }
        user.setId(Integer.valueOf(createUserId));
        user.setMemo(modelName);
        user.setPassword(observationTime);
        if(!couldVisit(modelName,observationTime))
        {
            return ResultTemplate.fail("服务器繁忙，请稍后重试。");
        }
        if(preview_png.equals("True"))
        {
            funcitionSelected+="preview_png";
        }
        if(class_stats.equals("True"))
        {
            funcitionSelected+=",class_stats";
        }
//        if(funcitionSelected.startsWith(","))
//        {
//            funcitionSelected=funcitionSelected.substring(1);
//        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String formattedDate = sdf.format(new Date());
        if(modelName==null || modelName.isEmpty()){
            return ResultTemplate.fail("非法参数！");
        }

        if(!modelName.startsWith("fanyan"))
        {
            return ResultTemplate.fail("未知操作！");
        }

        Map<String, String> values = new HashMap<>();
        String filePath= codeRootPath +modelName+".py";

        String filename="";

        ModelFileStatus modelFileStatus=new ModelFileStatus();
        modelFileStatus.setDealStatus("success");
        modelFileStatus.setClassName(className);
        modelFileStatus.setUserName(userName);
        modelFileStatus.setCreateUserid(createUserId);
        modelFileStatus.setObservationTime(observationTime);
        modelFileStatus.setModelName(modelName);
        List<Map<String,Object>> mapList=modelFileStatusService.selectUserAndFileStatus(modelFileStatus);
        String input_dir;
        if(!mapList.isEmpty())
        {
            String filepath=mapList.get(0).get("filepath").toString();
            filepath=filepath.replace("\\\\","\\");
            filepath=filepath.replace("//","/");
            input_dir=filepath;
            //filename=className+"/"+filepath.substring(filepath.lastIndexOf("/")+1);
            filename=filepath.replace(FileRootDirPath,"");
        }else {
            return ResultTemplate.fail("请先提交文件！");
        }
        values.put("preview_png",preview_png);
        values.put("class_stats",class_stats);
        values.put("input_dir", input_dir);
        values.put("createUserid",createUserId);
        values.put("userName",userName);
        values.put("createTime",new Date().toString());
        values.put("filename",filename);
        try {
            if (!modelName.equals("fanyanV2"))
            {
                user.setAddress(funcitionSelected);
                user.setProductionCompany(className);
                ProcessBuilderUtils.executeInBackground(filePath,null,values,user);
            }else  {
                ModelStatus modelStatus = new ModelStatus();
                modelStatus.setUserName(userName);
                modelStatus.setCreateUserid(createUserId);
                modelStatus.setObservationTime(observationTime);
                modelStatus.setModelName(modelName);
                modelStatus.setClassName(className);
                modelStatus.setUpdateTime(new Date());
                modelStatus.setCreateTime(new Date());
                modelStatus.setFunctionSelected(funcitionSelected);
                modelStatus.setType("lower");
                ProcessBuilderUtils.executeInBackground(filePath,null,values,modelStatus);
            }

        }catch (RuntimeException e){
            ResultTemplate.fail("程序执行失败！");
        }catch (Exception e){
            ResultTemplate.fail("未知错误!");
        }


        return ResultTemplate.success("程序已在后台运行!");
    }


    private  boolean couldVisit(User user)
    {
        List<Map<String,Object>> userList= userService.selectUserByCondition(user);
        boolean flag=false;
        for (Map<String,Object> map : userList) {
            if(Objects.equals(map.get("id").toString(), user.getId().toString())){
                flag=true;
            }
        }
        if(!flag){
            return false;
        }
        ModelStatus modelStatus = new ModelStatus();
        modelStatus.setModelName(user.getMemo());
        modelStatus.setUserName(user.getUsername());
        modelStatus.setCreateUserid(user.getId().toString());
        List<Map<String,Object>> usages= modelStatusService.selectModelStatusByConditions(modelStatus);
        for (Map<String,Object> map : usages) {
            Date date=new Date();
            if(Objects.equals(map.get("usageStatus").toString(), "executing"))
            {
                return false;
            }else if(Objects.equals(map.get("usageStatus").toString(), "success"))
            {
                if(Objects.equals(user.getId(), Integer.valueOf(map.get("createUserid").toString())) &&user.getUsername()==map.get("username"))
                {
                    return true;
                }

            }
        }
        return true;
    }
//    private  boolean couldVisit(String modelName)
//    {
//        ModelStatus modelStatus = new ModelStatus();
//        modelStatus.setModelName(modelName);
//        List<Map<String,Object>> usages= modelStatusService.selectModelStatusByConditions(modelStatus);
//        for (Map<String,Object> map : usages) {
//            Date date1=new Date();
//            if(Objects.equals(map.get("usageStatus").toString(), "executing"))
//            {
//                return false;
//            }else if(Objects.equals(map.get("usageStatus").toString(), "success"))
//            {
//                SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
//                if(fmt.format(date1).equals(fmt.format((Date) map.get("createTime")))) {
//                    return false;
//                }
//                if(Objects.equals(map.get("modelName").toString(), modelStatus.getModelName()))
//                {
//                    ModelStatus modelStatus1 = new ModelStatus(map);
//                    modelStatusService.dropModelLogs(new ArrayList<>(), modelStatus1);
//                }
//            }
//        }
//        return true;
//    }

    private  boolean couldVisit(String modelName,String observationTime)
    {
        ModelStatus modelStatus = new ModelStatus();
        modelStatus.setModelName(modelName);
        List<Map<String,Object>> usages= modelStatusService.selectModelStatusByConditions(modelStatus);
        for (Map<String,Object> map : usages) {
            if(Objects.equals(map.get("usageStatus").toString(), "executing"))
            {
                return false;
            }else if(Objects.equals(map.get("usageStatus").toString(), "success"))
            {
                if(observationTime.equals(map.get("observationTime"))) {
                    return false;
                }
            }
        }
        return true;
    }

    // 1. 获取文件URL的接口
    @PostMapping(value = "/api/modelFile/getLandResult")
    public ResultTemplate<Object> getLandResult(@RequestBody JSONObject jsonObject) {
        Map<String, Object> response = new HashMap<>();
        String modelName = jsonObject.getStr("modelName");
        String userName = jsonObject.getStr("userName");
        Integer createUserId = jsonObject.getInt("createUserId");

        // 验证modelName合法性
        if (!Arrays.asList("mlp", "rf", "xgb", "svm","XGB","CNN","rfV2","CNNV2").contains(modelName)) {
            return ResultTemplate.fail("非法的模型名");
        }
        User user = new User();
        user.setUsername(userName);
        user.setId(createUserId);
        user.setStatus(0);
        user.setMemo(modelName);
        if(!couldVisit(user))
        {
            return ResultTemplate.fail("请先提交申请！");
        }
        Map<String, String> urls = new HashMap<>();
        urls.put("preview_png", "/api/modelFile/preview");
        urls.put("confusion_matrix", "/api/modelFile/download/confusion_matrix");
        urls.put("class_stats", "/api/modelFile/download/class_stats");

        Map<String, Object> data = new HashMap<>();
        data.put("urls", urls);
        data.put("modelName", modelName);

        return ResultTemplate.success(data);
    }

    //分析水域面积变化的接口
    //@PreAuthorize("hasAnyAuthority('api_groupType_all')")
    @PostMapping("/api/waterChange")
    public ResultTemplate<Object> buildWaterChangeProcess(
            @RequestParam("earlyFile") MultipartFile earlyFile,
            @RequestParam("lateFile") MultipartFile lateFile,
            @RequestParam("waterTagValue") String waterTagValue) {

        // 1. 校验文件是否为空
        if (earlyFile == null || earlyFile.isEmpty()) {
            logger.error("earlyFile 为空");
            return ResultTemplate.fail("earlyFile 不能为空");
        }
        if (lateFile == null || lateFile.isEmpty()) {
            logger.error("lateFile 为空");
            return ResultTemplate.fail("lateFile 不能为空");
        }

        // 2. 创建保存目录
        String baseDir = "D:/recognition/tudifugaifenlei/shuju/";
        File dir = new File(baseDir);
        if (!dir.exists() && !dir.mkdirs()) {
            logger.error("临时目录创建失败: {}", baseDir);
            return ResultTemplate.fail("临时目录创建失败，请联系管理员");
        }

        // 使用UUID生成唯一文件名，避免并发冲突
        String uuid = UUID.randomUUID().toString();
        File earlySave = new File(baseDir, "early_" + uuid + ".tif");
        File lateSave = new File(baseDir, "late_" + uuid + ".tif");


        try {
            earlyFile.transferTo(earlySave);
            lateFile.transferTo(lateSave);
            logger.info("earlyFile 保存成功: {}", earlySave.getAbsolutePath());
            logger.info("lateFile 保存成功: {}", lateSave.getAbsolutePath());
        } catch (IOException e) {
            logger.error("文件保存失败: {}", e.getMessage(), e);
            return ResultTemplate.fail("文件上传失败，请稍后重试");
        }

        // 3. 调用 Python 脚本
        StringBuilder output = new StringBuilder();
        int exitCode = -1;
        Process process = null;
        BufferedReader reader = null;
        // 添加环境变量

        try {
            // 修改 ProcessBuilder 设置
            ProcessBuilder pb = new ProcessBuilder(
                    condaPYPath,
                    waterChangePYPath,
                    earlySave.getAbsolutePath(),
                    lateSave.getAbsolutePath(),
                    waterTagValue
            );
            pb.redirectErrorStream(true);

            // 设置工作目录为脚本所在目录
            File scriptFile = new File(waterChangePYPath);
            File scriptDir = scriptFile.getParentFile();
            if (scriptDir != null && scriptDir.exists()) {
                pb.directory(scriptDir);
            }

            pb.environment().put("EARLY_PATH", earlySave.getAbsolutePath());
            pb.environment().put("LATE_PATH", lateSave.getAbsolutePath());

            // 添加环境变量来指定结果目录（可选，也可以让Python脚本自动使用工作目录）
            pb.environment().put("RESULT_DIR", scriptDir != null ?
                    new File(scriptDir, "result").getAbsolutePath() : "./result");

            process = pb.start();

            reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
                logger.info("Python 输出: {}", line);
            }

            exitCode = process.waitFor();
            logger.info("Python 进程退出码: {}", exitCode);

            if (exitCode != 0) {
                return ResultTemplate.fail("Python 脚本执行失败:\n" + output.toString());
            }

            // 解析Python输出的JSON
            String jsonOutput = output.toString().trim();
            logger.info("原始JSON输出: {}", jsonOutput);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode resultJson;

            try {
                resultJson = objectMapper.readTree(jsonOutput);

                // 检查是否有错误字段
                if (resultJson.has("error")) {
                    logger.error("Python脚本返回错误: {}", resultJson.get("error").asText());
                    return ResultTemplate.fail("Python脚本执行错误: " + resultJson.get("error").asText());
                }

                // 创建格式化的响应对象
                Map<String, Object> formattedResponse = new LinkedHashMap<>();

                // 添加文件路径信息
                formattedResponse.put("stats_file", resultJson.get("stats_file").asText());
                formattedResponse.put("image_file", resultJson.get("image_file").asText());
                formattedResponse.put("tif_file", resultJson.get("tif_file").asText());

                // 添加统计信息
                JsonNode statsNode = resultJson.get("stats");
                Map<String, Object> statsMap = new LinkedHashMap<>();

                if (statsNode != null && statsNode.isObject()) {
                    statsNode.fields().forEachRemaining(entry -> {
                        JsonNode statItem = entry.getValue();
                        Map<String, Object> statMap = new LinkedHashMap<>();
                        statMap.put("pixels", statItem.get("pixels").asInt());
                        statMap.put("area", statItem.get("area").asDouble());
                        statMap.put("ratio", statItem.get("ratio").asDouble());
                        statsMap.put(entry.getKey(), statMap);
                    });
                }

                formattedResponse.put("stats", statsMap);

                return ResultTemplate.success(formattedResponse);

            } catch (JsonProcessingException e) {
                logger.warn("Python输出不是标准JSON格式，返回原始文本: {}", e.getMessage());
                // 如果不是JSON，返回原始文本
                return ResultTemplate.success(jsonOutput);
            }

        } catch (IOException e) {
            logger.error("执行 Python 脚本IO错误: {}", e.getMessage(), e);
            return ResultTemplate.fail("执行 Python 脚本IO错误:\n" + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("执行 Python 脚本被中断: {}", e.getMessage(), e);
            return ResultTemplate.fail("执行 Python 脚本被中断:\n" + e.getMessage());
        } catch (Exception e) {
            logger.error("执行 Python 脚本出错: {}", e.getMessage(), e);
            return ResultTemplate.fail("执行 Python 脚本出错:\n" + e.getMessage());
        } finally {
            // 关闭资源
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    logger.warn("关闭读取器失败: {}", e.getMessage());
                }
            }
            if (process != null) {
                process.destroy();
            }

            // 删除临时文件（确保Python脚本执行完成后再删除）
            boolean earlyDeleted = earlySave.exists() && earlySave.delete();
            boolean lateDeleted = lateSave.exists() && lateSave.delete();

            if (!earlyDeleted || !lateDeleted) {
                logger.warn("临时文件删除失败: early={}, late={}", earlyDeleted, lateDeleted);
            } else {
                logger.info("临时文件已清理");
            }
        }
    }

    // 水域面积预测
    @PostMapping("/api/waterExtract")
    public ResultTemplate<Object> waterExtract(
            @RequestParam(value = "sarFile", required = false) MultipartFile sarFile,
            @RequestParam(value = "optFile", required = false) MultipartFile optFile,
            @RequestParam(value = "model", defaultValue = "UNet") String model) {

        // 1) 参数校验
        if ((sarFile == null || sarFile.isEmpty()) && (optFile == null || optFile.isEmpty())) {
            return ResultTemplate.fail("必须至少上传 SAR 或 Optical 影像");
        }

        // 针对模型的输入要求进一步校验
        if ((model.equalsIgnoreCase("DeepLabV3") ||
                model.equalsIgnoreCase("HRNet") ||
                model.equalsIgnoreCase("SFNet")) &&
                (sarFile == null || sarFile.isEmpty() || optFile == null || optFile.isEmpty())) {
            return ResultTemplate.fail(model + " 模型必须同时上传 SAR 和 Optical 影像");
        }

        // 2) 临时文件目录
        String baseDir = "D://recognition/code/output_results/";
        File dir = new File(baseDir);
        if (!dir.exists() && !dir.mkdirs()) {
            return ResultTemplate.fail("临时目录创建失败");
        }
        // 创建 sar 和 optical 子目录
        File sarDir = new File(baseDir + "sar/");
        File optDir = new File(baseDir + "optical/");
        if (!sarDir.exists() && !sarDir.mkdirs()) {
            return ResultTemplate.fail("SAR 子目录创建失败");
        }
        if (!optDir.exists() && !optDir.mkdirs()) {
            return ResultTemplate.fail("Optical 子目录创建失败");
        }

        String uuid = UUID.randomUUID().toString();
        File sarSave = null, optSave = null;
        String commonFileName = uuid + ".tif"; // 使用相同文件名
        try {
            if (sarFile != null && !sarFile.isEmpty()) {
                sarSave = new File(sarDir, commonFileName);
                sarFile.transferTo(sarSave);
                System.out.println("保存 SAR 文件: " + sarSave.getAbsolutePath());
            }
            if (optFile != null && !optFile.isEmpty()) {
                optSave = new File(optDir, commonFileName);
                optFile.transferTo(optSave);
                System.out.println("保存 Optical 文件: " + optSave.getAbsolutePath());
            }
        } catch (IOException e) {
            return ResultTemplate.fail("文件保存失败: " + e.getMessage());
        }

        File outDir = new File(baseDir, "results_" + uuid);
        if (!outDir.exists() && !outDir.mkdirs()) {
            return ResultTemplate.fail("结果目录创建失败");
        }

        // 3) 构建 Python 命令
        StringBuilder output = new StringBuilder();
        int exitCode;
        Process process = null;
        try {
            File pythonWorkDir = new File(codeRootPath);

            List<String> command = new ArrayList<>();
            command.add(condaPYPath);
            command.add(codeRootPath+"core.py");
            command.add("--model");
            command.add(model);
            if (sarSave != null) {
                command.add("--sar");
                command.add(sarSave.getAbsolutePath());
            }
            if (optSave != null) {
                command.add("--opt");
                command.add(optSave.getAbsolutePath());
            }
            command.add("--output");
            command.add(outDir.getAbsolutePath());

            System.out.println("执行命令: " + String.join(" ", command));

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            pb.directory(pythonWorkDir);  // 设置为脚本目录


            Map<String, String> env = pb.environment();
            env.put("PYTHONUNBUFFERED", "1");
            env.put("PYTHONPATH", pythonWorkDir.getAbsolutePath());
            env.put("PYTHONIOENCODING", "utf-8"); // 强制 Python 使用 UTF-8 编码输出

            process = pb.start();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            exitCode = process.waitFor();
            if (exitCode != 0) {
                return ResultTemplate.fail("Python 脚本执行失败:\n" + output);
            }

        } catch (Exception e) {
            return ResultTemplate.fail("执行 Python 出错: " + e.getMessage());
        } finally {
            if (sarSave != null && sarSave.exists()) sarSave.delete();
            if (optSave != null && optSave.exists()) optSave.delete();
        }

        // 4) 解析 Python 输出（JSON）
        try {
            ObjectMapper mapper = new ObjectMapper();
            String outputStr = output.toString().trim();
            System.out.println("原始 Python 输出: " + outputStr);
            // 提取 JSON 部分
            // 方法1: 查找第一个 [ 和最后一个 ]
            int start = outputStr.lastIndexOf('[');
            int end = outputStr.lastIndexOf(']');

            if (start == -1 || end == -1 || start >= end) {
                // 方法2: 查找第一个 { 和最后一个 }
                start = outputStr.indexOf('{');
                end = outputStr.lastIndexOf('}');
                if (start == -1 || end == -1 || start >= end) {
                    return ResultTemplate.fail("无法从 Python 输出中提取 JSON: \n" + outputStr);
                }
            }

            String jsonStr = outputStr.substring(start, end + 1);
            System.out.println("提取的 JSON: " + jsonStr);

            JsonNode root = mapper.readTree(jsonStr);

            if (root.isObject() && root.has("error")) {
                return ResultTemplate.fail("Python 错误: " + root.get("error").asText());
            }
            return ResultTemplate.success(root);
        } catch (Exception e) {
            return ResultTemplate.fail("解析 Python 输出失败: " + e.getMessage() + "\n原始输出: " + output.toString());
        }
    }
    /**
     * 获取预测结果预览图片代理接口
     * 按照 /api/modelFile/preview 的格式设计
     */
    @PostMapping(value = "/api/proxy/preview")
    public ResponseEntity<byte[]> getPreviewImageProxy(@RequestBody JSONObject jsonObject) {
        try {
            String imagePath = jsonObject.getStr("imagePath");

            if (imagePath == null || imagePath.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            Path filePath = Paths.get(imagePath);
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            // 使用字节数组确保完整传输
            byte[] imageBytes = Files.readAllBytes(filePath);
            String fileName = filePath.getFileName().toString();
            String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

            MediaType mediaType;
            switch (extension) {
                case "png":
                    mediaType = MediaType.IMAGE_PNG;
                    break;
                case "jpg":
                case "jpeg":
                    mediaType = MediaType.IMAGE_JPEG;
                    break;
                case "gif":
                    mediaType = MediaType.IMAGE_GIF;
                    break;
                default:
                    mediaType = MediaType.APPLICATION_OCTET_STREAM;
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(mediaType);
            headers.setContentLength(imageBytes.length);
            headers.setCacheControl("no-cache");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(imageBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping(value = "/api/proxy/download")
    public ResponseEntity<byte[]> downloadTifFileProxy(@RequestBody JSONObject jsonObject) {
        try {
            String filePath = jsonObject.getStr("filePath");
            String customFileName = jsonObject.getStr("fileName");

            if (filePath == null || filePath.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                return ResponseEntity.notFound().build();
            }

            // 使用字节数组确保完整传输
            byte[] fileBytes = Files.readAllBytes(path);
            String fileName = customFileName != null ? customFileName : path.getFileName().toString();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("image/tiff"));
            headers.setContentDispositionFormData("attachment", fileName);
            headers.setContentLength(fileBytes.length); // 重要：设置正确的文件大小

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping(value = "/api/model/landChange")
    public ResultTemplate<Object> getLandChange(@RequestParam("DISPLAY_MODE") String displayMode,
                                                @RequestParam("earlyFile") MultipartFile earlyFile,
                                                @RequestParam("lateFile") MultipartFile lateFile,
                                                @RequestParam(value = "configFile", required = false) MultipartFile configFile,
                                                @RequestParam(value = "change_stats", required = false) String changeStats,
                                                @RequestParam(value = "change_image", required = false) String changeImage,
                                                @RequestParam(value = "change_tif", required = false) String changeTif) {

        // 1. 参数校验
        displayMode = displayMode.equalsIgnoreCase("all") ? "all" : "changes_only";

        if (earlyFile == null || earlyFile.isEmpty()) {
            return ResultTemplate.fail("早期文件不能为空");
        }
        if (lateFile == null || lateFile.isEmpty()) {
            return ResultTemplate.fail("后期文件不能为空");
        }

        // 2. 创建临时目录
        String baseDir = codeRootPath+"shuju/";
        String tempDir = baseDir + "land_change_temp_" + System.currentTimeMillis() + "/";
        File dir = new File(tempDir);
        if (!dir.exists() && !dir.mkdirs()) {
            return ResultTemplate.fail("临时目录创建失败");
        }

        // 3. 保存上传的文件
        File earlySave = null;
        File lateSave = null;
        File configSave = null;

        try {
            // 保存早期文件
            String earlyFileName = "early_" + System.currentTimeMillis() + ".tif";
            earlySave = new File(tempDir, earlyFileName);
            earlyFile.transferTo(earlySave);
            logger.info("早期文件保存成功: {}", earlySave.getAbsolutePath());

            // 保存后期文件
            String lateFileName = "late_" + System.currentTimeMillis() + ".tif";
            lateSave = new File(tempDir, lateFileName);
            lateFile.transferTo(lateSave);
            logger.info("后期文件保存成功: {}", lateSave.getAbsolutePath());

            // 保存配置文件（如果有）
            if (configFile != null && !configFile.isEmpty()) {
                String configFileName = "config_" + System.currentTimeMillis() + ".json";
                configSave = new File(tempDir, configFileName);
                configFile.transferTo(configSave);
                logger.info("配置文件保存成功: {}", configSave.getAbsolutePath());
            }

            // 设置环境变量
            Map<String, String> envVars = new HashMap<>();
            envVars.put("TIF1_PATH", earlySave.getAbsolutePath());
            envVars.put("TIF2_PATH", lateSave.getAbsolutePath());
            envVars.put("OUTPUT_DIR", tempDir + "results");
            envVars.put("DISPLAY_MODE", displayMode);
            envVars.put("OUTPUT_JSON", "true");

            if (configSave != null) {
                envVars.put("CONFIG_PATH", configSave.getAbsolutePath());
            }

            //  执行Python脚本
            String pythonOutput = ProcessBuilderUtils.executeAndReturnStd(
                    codeRootPath+"land_change_detection_visualizer.py",
                    null,
                    envVars,
                    "UTF-8"
            );

            logger.info("Python脚本输出: {}", pythonOutput);

            //  解析Python输出（JSON格式）
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode resultJson = objectMapper.readTree(pythonOutput);

            if (!resultJson.has("success") || !resultJson.get("success").asBoolean()) {
                String errorMsg = resultJson.has("error") ? resultJson.get("error").asText() : "Python脚本执行失败";
                return ResultTemplate.fail(errorMsg);
            }

            // 7. 构建返回结果
            Map<String, Object> response = new LinkedHashMap<>();

            // 文件路径
            response.put("stats_file", resultJson.path("json_path").asText());
            response.put("image_file", resultJson.path("png_path").asText());
            response.put("tif_file", resultJson.path("tif_path").asText());

            // 统计信息
            JsonNode statsNode = resultJson.path("stats");
            if (!statsNode.isMissingNode()) {
                // 将整个stats对象放入response，这样前端可以访问所有统计信息
                response.put("stats", statsNode);

                // 同时提取一些关键统计数据到顶层，便于前端直接使用
                JsonNode pixelStats = statsNode.path("pixel_statistics");
                if (!pixelStats.isMissingNode()) {
                    int totalValid = pixelStats.path("valid_pixels").asInt();
                    int changed = pixelStats.path("changed_pixels").asInt();
                    int unchanged = pixelStats.path("unchanged_pixels").asInt();

                    response.put("total_pixels", pixelStats.path("total_pixels").asInt());
                    response.put("valid_pixels", totalValid);
                    response.put("changed_pixels", changed);
                    response.put("unchanged_pixels", unchanged);
                    response.put("change_percentage", totalValid > 0 ? (changed * 100.0 / totalValid) : 0);
                    response.put("unchanged_percentage", totalValid > 0 ? (unchanged * 100.0 / totalValid) : 0);
                }

                // 变化类型统计
                JsonNode changeTypesNode = statsNode.path("change_types");
                if (!changeTypesNode.isMissingNode() && changeTypesNode.isArray()) {
                    List<Map<String, Object>> changeTypes = new ArrayList<>();
                    for (JsonNode changeNode : changeTypesNode) {
                        Map<String, Object> changeType = new HashMap<>();
                        changeType.put("from", changeNode.path("from").asInt());
                        changeType.put("to", changeNode.path("to").asInt());
                        changeType.put("from_name", changeNode.path("from_name").asText());
                        changeType.put("to_name", changeNode.path("to_name").asText());
                        changeType.put("count", changeNode.path("count").asInt());
                        changeType.put("percentage", changeNode.path("percentage").asDouble());
                        changeTypes.add(changeType);
                    }
                    response.put("change_types", changeTypes);
                }

                // 未变化类别统计
                JsonNode unchangedClasses = statsNode.path("unchanged_classes");
                if (!unchangedClasses.isMissingNode()) {
                    Map<String, Object> unchangedStats = new HashMap<>();
                    unchangedClasses.fields().forEachRemaining(entry -> {
                        Map<String, Object> statDetail = new HashMap<>();
                        statDetail.put("count", entry.getValue().asInt());
                        // 计算百分比
                        int totalValidPixels = pixelStats.path("valid_pixels").asInt();
                        double percentage = totalValidPixels > 0 ? (entry.getValue().asInt() * 100.0 / totalValidPixels) : 0;
                        statDetail.put("percentage", percentage);
                        unchangedStats.put(entry.getKey(), statDetail);
                    });
                    response.put("unchanged_classes", unchangedStats);
                }
            }

            return ResultTemplate.success(response);

        } catch (Exception e) {
            logger.error("地类变化检测失败: {}", e.getMessage(), e);
            return ResultTemplate.fail("地类变化检测失败: " + e.getMessage());
        } finally {
            // 8. 清理临时文件（可以延迟清理或保留一段时间供用户下载）
            try {
                // 可以设置一个定时任务来清理旧文件，这里先简单删除
                if (earlySave != null && earlySave.exists()) earlySave.delete();
                if (lateSave != null && lateSave.exists()) lateSave.delete();
                if (configSave != null && configSave.exists()) configSave.delete();

                // 保留结果文件，可以在24小时后清理
                File resultsDir = new File(tempDir + "results");
                if (resultsDir.exists()) {
                    // 这里可以记录结果文件的路径，后续通过定时任务清理
                    logger.info("结果文件保存在: {}", resultsDir.getAbsolutePath());
                }
            } catch (Exception e) {
                logger.warn("清理临时文件失败: {}", e.getMessage());
            }
        }
    }

    @PostMapping(value = "/api/model/getPlantChange")
    public ResultTemplate<Object> getPlantChange(
            @RequestParam("DISPLAY_MODE") String displayMode,
            @RequestParam("earlyFile") MultipartFile earlyFile,
            @RequestParam("lateFile") MultipartFile lateFile,
            @RequestParam(value = "change_stats", required = false) String changeStats,
            @RequestParam(value = "change_image", required = false) String changeImage,
            @RequestParam(value = "classified_tif", required = false) String classifiedTif,
            @RequestParam(value = "raw_tif", required = false) String rawTif,
            @RequestParam(value = "CHANGE_THRESHOLDS", required = false) String changeThresholds) {

        // 1. 参数校验
        displayMode = displayMode.equalsIgnoreCase("all") ? "all" : "changes_only";

        if (earlyFile == null || earlyFile.isEmpty()) {
            return ResultTemplate.fail("早期FVC文件不能为空");
        }
        if (lateFile == null || lateFile.isEmpty()) {
            return ResultTemplate.fail("后期FVC文件不能为空");
        }

        // 2. 创建临时目录
        String baseDir = condaPYPath+"shuju/";
        String tempDir = baseDir + "plant_change_temp_" + System.currentTimeMillis() + "/";
        File dir = new File(tempDir);
        if (!dir.exists() && !dir.mkdirs()) {
            return ResultTemplate.fail("临时目录创建失败");
        }

        // 3. 保存上传的文件
        File earlySave = null;
        File lateSave = null;

        try {
            // 保存早期文件
            String earlyFileName = "early_" + System.currentTimeMillis() + ".tif";
            earlySave = new File(tempDir, earlyFileName);
            earlyFile.transferTo(earlySave);
            logger.info("早期FVC文件保存成功: {}", earlySave.getAbsolutePath());

            // 保存后期文件
            String lateFileName = "late_" + System.currentTimeMillis() + ".tif";
            lateSave = new File(tempDir, lateFileName);
            lateFile.transferTo(lateSave);
            logger.info("后期FVC文件保存成功: {}", lateSave.getAbsolutePath());

            // 设置环境变量
            Map<String, String> envVars = new HashMap<>();
            envVars.put("TIF1_PATH", earlySave.getAbsolutePath());
            envVars.put("TIF2_PATH", lateSave.getAbsolutePath());
            envVars.put("OUTPUT_DIR", tempDir + "results");
            envVars.put("DISPLAY_MODE", displayMode);
            envVars.put("OUTPUT_JSON", "true");

            // 添加阈值配置
            if (changeThresholds != null && !changeThresholds.trim().isEmpty()) {
                envVars.put("CHANGE_THRESHOLDS", changeThresholds);
            }

            // 4. 执行Python脚本
            String pythonScriptPath = codeRootPath + "plantchange.py";
            String pythonOutput = ProcessBuilderUtils.executeAndReturnStd(
                    pythonScriptPath,
                    null,
                    envVars,
                    "UTF-8"
            );

            logger.info("Python脚本输出: {}", pythonOutput);

            // 5. 解析Python输出（JSON格式）
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode resultJson = objectMapper.readTree(pythonOutput);

            if (!resultJson.has("success") || !resultJson.get("success").asBoolean()) {
                String errorMsg = resultJson.has("error") ? resultJson.get("error").asText() : "Python脚本执行失败";
                return ResultTemplate.fail(errorMsg);
            }

            // 6. 构建返回结果
            Map<String, Object> response = new LinkedHashMap<>();

            // 文件路径
            response.put("stats_file", resultJson.path("json_path").asText());
            response.put("image_file", resultJson.path("png_path").asText());
            response.put("classified_tif_file", resultJson.path("classified_tif_path").asText());
            response.put("raw_tif_file", resultJson.path("raw_tif_path").asText());

            // 统计信息
            JsonNode statsNode = resultJson.path("stats");
            if (!statsNode.isMissingNode()) {
                // 将整个stats对象放入response
                response.put("stats", statsNode);

                // 提取关键统计数据
                JsonNode pixelStats = statsNode.path("pixel_statistics");
                if (!pixelStats.isMissingNode()) {
                    int totalValid = pixelStats.path("valid_pixels").asInt();
                    int changed = pixelStats.path("changed_pixels").asInt();
                    int unchanged = pixelStats.path("unchanged_pixels").asInt();

                    response.put("total_pixels", pixelStats.path("total_pixels").asInt());
                    response.put("valid_pixels", totalValid);
                    response.put("changed_pixels", changed);
                    response.put("unchanged_pixels", unchanged);
                    response.put("change_percentage", totalValid > 0 ? (changed * 100.0 / totalValid) : 0);
                    response.put("unchanged_percentage", totalValid > 0 ? (unchanged * 100.0 / totalValid) : 0);
                }

                // 类别统计
                JsonNode classStatsNode = statsNode.path("class_statistics");
                JsonNode classPercentagesNode = statsNode.path("class_percentages");

                if (!classStatsNode.isMissingNode()) {
                    response.put("class_statistics", classStatsNode);
                }
                if (!classPercentagesNode.isMissingNode()) {
                    response.put("class_percentages", classPercentagesNode);
                }

                // 变化统计信息
                JsonNode changeStatsNode = statsNode.path("change_statistics");
                if (!changeStatsNode.isMissingNode()) {
                    response.put("change_statistics", changeStatsNode);
                }
            }

            return ResultTemplate.success(response);

        } catch (Exception e) {
            logger.error("植被覆盖度变化检测失败: {}", e.getMessage(), e);
            return ResultTemplate.fail("植被覆盖度变化检测失败: " + e.getMessage());
        } finally {
            // 7. 清理临时文件
            try {
                if (earlySave != null && earlySave.exists()) earlySave.delete();
                if (lateSave != null && lateSave.exists()) lateSave.delete();

                // 保留结果文件一段时间
                File resultsDir = new File(tempDir + "results");
                if (resultsDir.exists()) {
                    logger.info("结果文件保存在: {}", resultsDir.getAbsolutePath());
                }
            } catch (Exception e) {
                logger.warn("清理临时文件失败: {}", e.getMessage());
            }
        }
    }
}

