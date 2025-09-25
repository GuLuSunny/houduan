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
import com.ydsw.service.impl.ModelFileStatusServiceImpl;
import com.ydsw.utils.ProcessBuilderUtils;
import org.apache.ibatis.annotations.Param;
import org.geolatte.geom.M;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.processing.FilerException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.ErrorManager;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    
    private final String codeRootPath = "D:\\heigankoumodel\\code\\";
    private final String ResultRootPath = "D:\\heigankoumodel\\code\\result\\";
    @Autowired
    private ModelFileStatusServiceImpl modelFileStatusServiceImpl;

    @PreAuthorize("hasAnyAuthority('api_groupType')")
    @PostMapping(value = "/api/groupType")
    public ResultTemplate<Object> buildGroupTypeProcess(@RequestBody JSONObject jsonObject) {
        String processName = jsonObject.getStr("processName");
        List<String> commons = jsonObject.getBeanList("commons", String.class);
        List<String> envValues = jsonObject.getBeanList("envValues", String.class);
        Map<String, String> values = new HashMap<>();
        String filepath = "D:\\heigankoumodel\\code\\" + processName + ".py";
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
        String baseDir = "D:\\heigankoumodel\\shuju\\";
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
        String observationTime=jsonObject.getStr("observationTime");
        String funcitionSelected="null";
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
        if(!couldVisit(modelName))
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
            default -> {
                return ResultTemplate.fail("非法的参数名！");
            }
        }
        if(processname.equals("predictV2"))
        {
            ModelFileStatus modelFileStatus=new ModelFileStatus();
            modelFileStatus.setDealStatus("success");
            modelFileStatus.setClassName(className);
            modelFileStatus.setUserName(userName);
            modelFileStatus.setCreateUserid(createUserId);
            modelFileStatus.setObservationTime(observationTime);
            List<Map<String,Object>> mapList=modelFileStatusService.selectUserAndFileStatus(modelFileStatus);
            if(!mapList.isEmpty())
            {
                String filepath=mapList.get(0).get("filepath").toString();
                filepath=filepath.replace("\\\\","\\");
                //filename=className+"\\"+filepath.substring(filepath.lastIndexOf("\\")+1);
                filename=filepath.replace("D:\\heigankoumodel\\fileTemp\\","");
            }else {
                return ResultTemplate.fail("请先提交目标文件！");
            }
        }
        String filepath = codeRootPath + processname + ".py";
        try {
            switch (modelName) {
                case "mlp", "rf", "svm", "xgb","XGB","CNN" -> values.put("modelSelected",modelName);
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

    @PreAuthorize("hasAnyAuthority('api_plantCover')")
    @PostMapping(value = "/api/plantCover")
    public ResultTemplate<Object> buildplantCoverProcessWithoutResult(@RequestBody JSONObject jsonObject) {
        String modelName = jsonObject.getStr("modelName");
        String preview_png= Objects.equals(jsonObject.getStr("preview_png"), "False") ?"False" :"True";
        String filepathroot = "D:\\heigankoumodel\\code"+File.separator;
        String color_map=jsonObject.getStr("color_map");
        String userName=jsonObject.getStr("userName");
        String createUserId=jsonObject.getStr("createUserId");
        String input_dir=jsonObject.getStr("input_dir");
        String funcitionSelected="null";
        String className="plant";
        String observationTime=jsonObject.getStr("observationTime");
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
        if(!couldVisit(modelName))
        {
            return ResultTemplate.fail("服务器繁忙，请稍后重试。");
        }
        if(preview_png.equals("True"))
        {
            funcitionSelected+="preview_png";
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
        if(!modelName.equals("RF") && !modelName.equals("1DResnet"))
        {
            if(!modelName.equals("fanyan") && !modelName.equals("fanyanNN"))
            {
                return ResultTemplate.fail("未知操作！");
            }
        }
        Map<String, String> values = new HashMap<>();
        String filePath=filepathroot+modelName+".py";

        String filename="";

        ModelFileStatus modelFileStatus=new ModelFileStatus();
        modelFileStatus.setDealStatus("success");
        modelFileStatus.setClassName(className);
        modelFileStatus.setUserName(userName);
        modelFileStatus.setCreateUserid(createUserId);
        List<Map<String,Object>> mapList=modelFileStatusService.selectUserAndFileStatus(modelFileStatus);
        if(!mapList.isEmpty())
        {
            String filepath=mapList.get(0).get("filepath").toString();
            filepath=filepath.replace("\\\\","\\");
            //filename=className+"\\"+filepath.substring(filepath.lastIndexOf("\\")+1);
            filename=filepath.replace("D:\\heigankoumodel\\fileTemp\\","");
        }else {
            return ResultTemplate.fail("请先提交文件！");
        }
        values.put("preview_png",preview_png);
        if(input_dir!=null) {
            values.put("input_dir", input_dir);
        }
        values.put("createUserid",createUserId);
        values.put("userName",userName);
        values.put("createTime",new Date().toString());
        try {
            ProcessBuilderUtils.executeInBackground(filePath,null,values);
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
                SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
                if(fmt.format(date).equals(fmt.format((Date) map.get("createTime")))) {

                    return false;
                }
                if(map.get("modelName")==modelStatus.getModelName())
                {
                    ModelStatus modelStatus1 = new ModelStatus(map);
                    modelStatusService.dropModelLogs(new ArrayList<>(), modelStatus1);
                }

            }
        }
        return true;
    }
    private  boolean couldVisit(String modelName)
    {
        ModelStatus modelStatus = new ModelStatus();
        modelStatus.setModelName(modelName);
        List<Map<String,Object>> usages= modelStatusService.selectModelStatusByConditions(modelStatus);
        for (Map<String,Object> map : usages) {
            Date date1=new Date();
            if(Objects.equals(map.get("usageStatus").toString(), "executing"))
            {
                return false;
            }else if(Objects.equals(map.get("usageStatus").toString(), "success"))
            {
                SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
                if(fmt.format(date1).equals(fmt.format((Date) map.get("createTime")))) {
                    return false;
                }
                if(Objects.equals(map.get("modelName").toString(), modelStatus.getModelName()))
                {
                    ModelStatus modelStatus1 = new ModelStatus(map);
                    modelStatusService.dropModelLogs(new ArrayList<>(), modelStatus1);
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
        String createUserId = jsonObject.getStr("createUserId");

        // 验证modelName合法性
        if (!Arrays.asList("mlp", "rf", "xgb", "svm","XGB","CNN").contains(modelName)) {
            return ResultTemplate.fail("非法的模型名");
        }
        User user = new User();
        user.setUsername(userName);
        user.setId(Integer.parseInt(createUserId));
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


//    @PostMapping(value = "/api/model/getResultV2_preview")
//    public ResponseEntity<Resource> getResultV2_preview(@RequestBody JSONObject jsonObject) {
//        String modelName = jsonObject.getStr("modelName");
//        String fileName = modelName + "_predictAndgetResult.txt";
//        Path filePath = Paths.get(ResultRootPath, fileName);
//        return getFileResponse(filePath, fileName, "image/png");
//    }
//    @PostMapping(value = "/api/model/getResultV2_tif")
//    public ResponseEntity<Resource> getResultV2_tif(@RequestBody JSONObject jsonObject) {
//        String modelName = jsonObject.getStr("modelName");
//        String fileName = modelName + "_predictAndgetResult.txt";
//        Path filePath = Paths.get(ResultRootPath, fileName);
//        return getFileResponse(filePath, fileName, "image/tiff");
//    }
    @PostMapping(value = "/api/model/predictV2")
    public ResultTemplate<Object> predictV2(@RequestBody JSONObject jsonObject) {
        String modelName = jsonObject.getStr("modelName");
        List<String> commons = jsonObject.getBeanList("commons", String.class);
        List<String> envValues = jsonObject.getBeanList("envValues", String.class);
        String preview_png= Objects.equals(jsonObject.getStr("preview_png"), "False") ?"False" :"True";
        String userName=jsonObject.getStr("userName");
        String createUserId=jsonObject.getStr("createUserId");
        String funcitionSelected=jsonObject.getStr("funcitionSelected");
        funcitionSelected=funcitionSelected.replace("null","");
        String className="land";
        if(preview_png.equals("True"))
        {
            funcitionSelected+="preview_png";
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
        if(!couldVisit(modelName))
        {
            return ResultTemplate.fail("服务器繁忙，请稍后重试。");
        }
        Map<String, String> values = new HashMap<>();
        String processname="";
        switch (modelName){
            case "XGB" -> processname = "XGB_predict";
            case "CNN" -> processname = "CNN_predict";
            default -> {
                return ResultTemplate.fail("非法的参数名！");
            }
        }
        String filepath = codeRootPath + processname + ".py";
        try {
            switch (modelName) {
                case "XGB" -> values.put("modelSelected",modelName);
                case "CNN" -> values.put("modelSelected",modelName);
                default -> {
                    return ResultTemplate.fail("非法的参数名！");
                }
            }
            values.put("preview_png",preview_png);
            user.setAddress(funcitionSelected);
            user.setProductionCompany(className);
            ProcessBuilderUtils.executeInBackground(filepath,null,values,user);
        }catch (RuntimeException e) {

            return ResultTemplate.fail("数据处理失败!");
        } catch (Exception e) {
            return ResultTemplate.fail("未知错误");
        }
        return ResultTemplate.success("预测已开始，请稍后查询。预计十分钟内完成。");
    }
    //分析水域面积变化的接口
    //@PreAuthorize("hasAnyAuthority('api_groupType_all')")
    @PostMapping("/api/waterChange")
    public ResultTemplate<Object> buildWaterChangeProcess(
            @RequestParam("earlyFile") MultipartFile earlyFile,
            @RequestParam("lateFile") MultipartFile lateFile) {

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
        String baseDir = "D:\\heigankoumodel\\tudifugaifenlei\\shuju\\";
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

        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "D:\\Pycharm\\condaInstall\\envs\\yzm38\\python.exe",
                    "E:\\陆浑湖\\waterchangeforuse\\waterchangeforuse.py",
                    earlySave.getAbsolutePath(),
                    lateSave.getAbsolutePath(),
                    "3"
            );
            pb.redirectErrorStream(true);

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
        String baseDir = "C:\\Users\\俞笙\\Desktop\\水资源监测\\output_results\\";
        File dir = new File(baseDir);
        if (!dir.exists() && !dir.mkdirs()) {
            return ResultTemplate.fail("临时目录创建失败");
        }
        // 创建 sar 和 optical 子目录
        File sarDir = new File(baseDir + "sar\\");
        File optDir = new File(baseDir + "optical\\");
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
            File pythonWorkDir = new File("C:\\Users\\俞笙\\Desktop\\水资源监测");

            List<String> command = new ArrayList<>();
            command.add("D:\\Pycharm\\condaInstall\\envs\\yzm38\\python.exe");
            command.add("C:\\Users\\俞笙\\Desktop\\水资源监测\\core.py");
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
            Pattern jsonPattern = Pattern.compile("\\[\\{.*\\}\\]");
            Matcher matcher = jsonPattern.matcher(outputStr);
            if (!matcher.find()) {
                return ResultTemplate.fail("无法从 Python 输出中提取 JSON: \n" + outputStr);
            }
            String jsonStr = matcher.group(0);
            JsonNode root = mapper.readTree(jsonStr);

            if (root.isObject() && root.has("error")) {
                return ResultTemplate.fail("Python 错误: " + root.get("error").asText());
            }
            return ResultTemplate.success(root);
        } catch (Exception e) {
            return ResultTemplate.fail("解析 Python 输出失败: " + e.getMessage() + "\n原始输出: " + output.toString());
        }
    }

}

