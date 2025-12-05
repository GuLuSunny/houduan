package com.ydsw.controller;

import cn.hutool.json.JSONObject;
import com.fengwenyi.api.result.ResultTemplate;
import com.ydsw.domain.ModelFileStatus;
import com.ydsw.domain.ModelStatus;
import com.ydsw.domain.User;
import com.ydsw.service.ModelFileStatusService;
import com.ydsw.service.ModelListService;
import com.ydsw.service.UserService;
import com.ydsw.utils.ZipFileUtils;
import lombok.extern.slf4j.Slf4j;
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class ModelFileStatusController {
    @Autowired
    private ModelFileStatusService modelFileStatusService;
    final private String FileRootDirPath="D:"+File.separator+"recognition"+File.separator+"fileTemp"+File.separator;

    private final String ResultRootPath = "D://recognition/code/result/";
    @Autowired
    private UserService userService;

    @Autowired
    private ModelListService modelListService;
    @PreAuthorize("hasAnyAuthority('api_modelFile_upload')")
    @PostMapping(value = "/api/modelFile/upload")
    public ResultTemplate<Object> uploadModelFile(@RequestParam("tiffile") MultipartFile file,
                                                  @RequestParam("createUserId") String userUid,
                                                  @RequestParam("userName") String userName,
                                                  @RequestParam("className") String className,
                                                  @RequestParam("observationTime")String observationTime,
                                                  @RequestParam("dataIntroduction") String dataIntroduction) {

        if (file == null || file.isEmpty()) {
            return ResultTemplate.fail("文件不能为空");
        }
        List<String> classNameList = modelListService.getAllClassName();
        if (!classNameList.contains(className)) {
            return ResultTemplate.fail("非法的类型参数！");
        }
        if (!userAlive(userUid, userName, className)) {
            return ResultTemplate.fail("非法用户！");
        }

        String originalFilename = file.getOriginalFilename();
        String fileType = originalFilename.substring(originalFilename.lastIndexOf("."));
        if (!fileType.equalsIgnoreCase(".tif")&&!fileType.equalsIgnoreCase(".zip")) {
            return ResultTemplate.fail("文件类型错误！");
        }

        if (!couldUpload(className,observationTime)) {
            return ResultTemplate.fail("服务器繁忙，请稍后重试");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        String formattedDate = sdf.format(date);

        String filename ="";
        if(fileType.equalsIgnoreCase(".tif")) {
            filename = userName + "-" + userUid + "_" + observationTime +
                    "_" + formattedDate + "_" +  "HH"  + ".tif";
        } else if (fileType.equalsIgnoreCase(".zip")) {
            filename = userName + "-" + userUid + "_" + formattedDate + ".zip";
        }
        String directoryPath = FileRootDirPath + File.separator + className;
        String filepath = directoryPath + File.separator + filename;
        ModelFileStatus modelFileStatus = new ModelFileStatus();
        modelFileStatus.setCreateUserid(userUid);
        modelFileStatus.setUserName(userName);
        modelFileStatus.setClassName(className);
        modelFileStatus.setFilepath(filepath);
        modelFileStatus.setCreateTime(date);
        modelFileStatus.setUpdateTime(date);
        modelFileStatus.setObservationTime(observationTime);
        modelFileStatus.setDataIntroduction(dataIntroduction);
        List<Map<String,Object>> list = modelFileStatusService.selectUserAndFileStatus(modelFileStatus);
        if(!list.isEmpty()){
            return ResultTemplate.fail("当天数据已经存在！");
        }
        try {
            // 创建目录（如果不存在）
            modelFileStatus.setDealStatus("executing");
            modelFileStatusService.save(modelFileStatus);
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 保存文件
            File dest = new File(filepath);
            file.transferTo(dest);

            // 保存文件记录到数据库

            modelFileStatus.setType("only");
            modelFileStatus.setDealStatus("success");
            modelFileStatusService.updateDealStatusViod(modelFileStatus);

            return ResultTemplate.success("文件上传成功");

        } catch (IOException e) {
            log.error("文件保存失败", e);
            modelFileStatus.setDealStatus("failed");
            modelFileStatusService.save(modelFileStatus);
            return ResultTemplate.fail("文件保存失败: " + e.getMessage());
        }
    }


    @PostMapping(value = "/api/modelFile/uploadMul")
    public ResultTemplate<Object> uploadModelFile(@RequestParam("tiffiles") MultipartFile[] files,
                                                  @RequestParam("createUserId") String userUid,
                                                  @RequestParam("userName") String userName,
                                                  @RequestParam("className") String className,
                                                  @RequestParam("observationTime")String observationTime,
                                                  @RequestParam("modelName") String modelName,
                                                  @RequestParam("dataIntroduction") String dataIntroduction) {

        if (files == null || files.length == 0) {
            return ResultTemplate.fail("文件不能为空");
        }
        List<String> classNameList = modelListService.getAllClassName();
        if (!classNameList.contains(className)) {
            return ResultTemplate.fail("非法的类型参数！");
        }
        if (!userAlive(userUid, userName, className)) {
            return ResultTemplate.fail("非法用户！");
        }
        String fileType="tif";
        for (MultipartFile file : files) {
            String originalFilename = file.getOriginalFilename();
            fileType = originalFilename.substring(originalFilename.lastIndexOf("."));
            if (!fileType.equalsIgnoreCase(".tif")&&!fileType.equalsIgnoreCase(".zip")) {
                return ResultTemplate.fail("文件类型错误！");
            }

        }

        if (!couldUpload(className,observationTime)) {
            return ResultTemplate.fail("服务器繁忙，请稍后重试");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        String formattedDate = sdf.format(date);
        String filename = userName + "-" + userUid + "_" + observationTime +
                "_" + formattedDate;
        String directoryPath = FileRootDirPath + File.separator + className+File.separator+filename+File.separator;
        ModelFileStatus modelFileStatus = new ModelFileStatus();
        modelFileStatus.setCreateUserid(userUid);
        modelFileStatus.setUserName(userName);
        modelFileStatus.setClassName(className);
        modelFileStatus.setFilepath(directoryPath);
        modelFileStatus.setCreateTime(date);
        modelFileStatus.setUpdateTime(date);
        modelFileStatus.setObservationTime(observationTime);
        modelFileStatus.setDataIntroduction(dataIntroduction);
        List<Map<String,Object>> list = modelFileStatusService.selectUserAndFileStatus(modelFileStatus);
        if(!list.isEmpty()){
            return ResultTemplate.fail("当天数据已经存在！");
        }
        try {
            // 创建目录（如果不存在）
            modelFileStatus.setDealStatus("executing");
            modelFileStatusService.save(modelFileStatus);
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 保存文件
            for(MultipartFile file : files) {
                File dest = new File(directoryPath+File.separator+file.getOriginalFilename());
                file.transferTo(dest);
            }

            // 保存文件记录到数据库

            modelFileStatus.setType("multiple");
            modelFileStatus.setDealStatus("success");
            modelFileStatusService.updateDealStatusViod(modelFileStatus);

            return ResultTemplate.success("文件上传成功");

        } catch (IOException e) {
            log.error("文件保存失败", e);
            modelFileStatus.setDealStatus("failed");
            modelFileStatusService.save(modelFileStatus);
            return ResultTemplate.fail("文件保存失败: " + e.getMessage());
        }
    }

    @PostMapping(value = "/api/model/getTrainData")
    public ResultTemplate<Object> getTrainData(@RequestParam("dataset") MultipartFile dataset,
                                               @RequestParam("createUserid") String userUid,
                                               @RequestParam("userName") String userName,
                                               @RequestParam("modelName") String modelMame,
                                               @RequestParam("className") String className,
                                               @RequestParam("dataIntroduction") String dataIntroduction) {
        String filename = userName + "-" + userUid+"_"+modelMame+ "_train.tif";
        String directoryPath = FileRootDirPath + File.separator + className;
        String filepath = directoryPath + File.separator + filename;
        ModelFileStatus modelFileStatus=new ModelFileStatus();
        modelFileStatus.setCreateUserid(userUid);
        modelFileStatus.setUserName(userName);
        modelFileStatus.setClassName(className);
        modelFileStatus.setFilepath(filepath);
        modelFileStatus.setCreateTime(new Date());
        modelFileStatus.setUpdateTime(new Date());
        modelFileStatus.setObservationTime(dataIntroduction);
        modelFileStatus.setDataIntroduction(dataIntroduction);
        try {
            // 创建目录（如果不存在）
            modelFileStatus.setDealStatus("executing");
            modelFileStatusService.save(modelFileStatus);
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 保存文件
            File dest = new File(filepath);
            dataset.transferTo(dest);

            // 保存文件记录到数据库

            modelFileStatus.setDealStatus("success");
            modelFileStatusService.updateDealStatusViod(modelFileStatus);

            return ResultTemplate.success("文件上传成功");

        } catch (IOException e) {
            log.error("文件保存失败", e);
            modelFileStatus.setDealStatus("failed");
            modelFileStatusService.save(modelFileStatus);
            return ResultTemplate.fail("文件保存失败: " + e.getMessage());
        }
    }

    @PostMapping(value = "/api/model/plantFilesUpload")
    public ResultTemplate<Object> plantFilesUpload(@RequestParam("files") MultipartFile[] files,
                                                   @RequestParam("createUserid") String userUid,
                                                   @RequestParam("userName") String userName,
                                                   @RequestParam("modelName") String modelMame,
                                                   @RequestParam("className") String className,
                                                   @RequestParam("observationTime") String observationTime,
                                                   @RequestParam("dataIntroduction") String dataIntroduction) {

        String directoryPath = FileRootDirPath + className + File.separator;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        String formattedDate = sdf.format(date);
        String filename = userName + "-" + userUid + "_" + modelMame + "_" +
                observationTime + "-" + formattedDate + File.separator;
        directoryPath += filename;

        // 创建文件状态记录
        ModelFileStatus modelFileStatus = new ModelFileStatus();
        modelFileStatus.setCreateUserid(userUid);
        modelFileStatus.setUserName(userName);
        modelFileStatus.setClassName(className);
        modelFileStatus.setModelName(modelMame);
        modelFileStatus.setFilepath(directoryPath);
        modelFileStatus.setDealStatus("executing");
        modelFileStatus.setType("multiple");
        modelFileStatus.setObservationTime(observationTime);
        modelFileStatus.setCreateTime(new Date());
        modelFileStatus.setUpdateTime(new Date());
        modelFileStatus.setDataIntroduction(dataIntroduction);
        try {
            // 创建目录（如果不存在）
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 步骤1: 先进行基本文件验证
            List<String> validationErrors = ZipFileUtils.validateFilesBeforeSaving(files, modelMame);
            if (!validationErrors.isEmpty()) {
                modelFileStatus.setDealStatus("failed");
                modelFileStatusService.save(modelFileStatus);
                return ResultTemplate.fail("文件验证失败: " + String.join(", ", validationErrors));
            }

            // 步骤2: 验证通过后才保存记录到数据库
            modelFileStatusService.save(modelFileStatus);

            // 步骤3: 过滤并只保存需要的文件
            List<String> savedFilePaths = new ArrayList<>();
            List<MultipartFile> filesToSave = ZipFileUtils.filterRequiredFiles(files, modelMame);

            for (MultipartFile file : filesToSave) {
                String filepath = directoryPath + File.separator + file.getOriginalFilename();

                try {
                    File dest = new File(filepath);
                    file.transferTo(dest);
                    savedFilePaths.add(filepath);
                } catch (IOException e) {
                    log.error("文件保存失败: {}", filepath, e);
                    // 删除已保存的文件
                    for (String savedPath : savedFilePaths) {
                        new File(savedPath).delete();
                    }
                    modelFileStatus.setDealStatus("failed");
                    modelFileStatusService.updateDealStatusViod(modelFileStatus);
                    return ResultTemplate.fail("文件保存失败: " + e.getMessage());
                }
            }

            // 步骤4: 对ZIP文件进行详细验证
            List<String> zipValidationErrors = ZipFileUtils.validateZipFilesAfterSaving(savedFilePaths, modelMame);
            if (!zipValidationErrors.isEmpty()) {
                // 删除所有已保存的文件
                for (String savedPath : savedFilePaths) {
                    new File(savedPath).delete();
                }
                modelFileStatus.setDealStatus("failed");
                modelFileStatusService.updateDealStatusViod(modelFileStatus);
                return ResultTemplate.fail("ZIP文件内容验证失败: " + String.join(", ", zipValidationErrors));
            }

            // 步骤5: 处理ZIP文件解压
            List<String> finalFilePaths = new ArrayList<>();
            List<String> requiredFiles = ZipFileUtils.getRequiredFilesByModel(modelMame);

            for (String savedPath : savedFilePaths) {
                if (savedPath.toLowerCase().endsWith(".zip")) {
                    modelFileStatus.setType("zip");
                    try {
                        // 解压ZIP文件中的必需文件
                        List<String> extractedFiles = ZipFileUtils.unzipRequiredFiles(savedPath, directoryPath, requiredFiles);
                        finalFilePaths.addAll(extractedFiles);

                        // 删除ZIP文件
                        boolean deleted = ZipFileUtils.deleteFile(savedPath);
                        if (!deleted) {
                            log.warn("ZIP文件删除失败: {}", savedPath);
                        }
                    } catch (IOException e) {
                        log.error("ZIP文件解压失败: {}", savedPath, e);
                        // 删除所有已保存的文件
                        for (String path : savedFilePaths) {
                            new File(path).delete();
                        }
                        modelFileStatus.setDealStatus("failed");
                        modelFileStatusService.updateDealStatusViod(modelFileStatus);
                        return ResultTemplate.fail("ZIP文件解压失败: " + e.getMessage());
                    }
                } else {
                    // 非ZIP文件直接保留
                    finalFilePaths.add(savedPath);
                }
            }

            // 步骤6: 清理目录中不需要的文件（确保只保留必需的文件）
            cleanUnnecessaryFiles(directoryPath, requiredFiles);

            // 步骤7: 更新状态为成功
            modelFileStatus.setDealStatus("success");
            modelFileStatusService.updateDealStatusViod(modelFileStatus);

            return ResultTemplate.success("文件上传并解压成功");

        } catch (Exception e) {
            log.error("文件上传处理失败", e);
            modelFileStatus.setDealStatus("failed");
            try {
                modelFileStatusService.updateDealStatusViod(modelFileStatus);
            } catch (Exception ex) {
                modelFileStatusService.save(modelFileStatus);
            }
            return ResultTemplate.fail("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 清理目录中不需要的文件，只保留必需的文件
     */
    private void cleanUnnecessaryFiles(String directoryPath, List<String> requiredFiles) {
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            return;
        }

        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        Set<String> requiredFileSet = requiredFiles.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        for (File file : files) {
            if (file.isFile()) {
                String fileName = file.getName().toLowerCase();
                // 如果不是必需的文件，且不是ZIP文件（ZIP文件已经在解压后删除），则删除
                if (!requiredFileSet.contains(fileName) && !fileName.endsWith(".zip")) {
                    boolean deleted = file.delete();
                    if (deleted) {
                        log.info("删除不需要的文件: {}", file.getAbsolutePath());
                    } else {
                        log.warn("删除不需要的文件失败: {}", file.getAbsolutePath());
                    }
                }
            }
        }
    }


    public boolean userAlive(String userUid, String userName, String className) {
        User user=new User();
        user.setUsername(userName);
        user.setStatus(0);
        List<Map<String,Object>> userList= userService.selectUserByCondition(user);
        boolean flag=false;
        for (Map<String,Object> map : userList) {
            if(Objects.equals(map.get("id").toString(), userUid)){
                flag=true;
            }
        }
        if(flag){
            return true;
        }
        user.setId(Integer.valueOf(userUid));
        user.setMemo(className);

        return false;
    }

    @PreAuthorize("hasAnyAuthority('api_modelFile_dropByConditions')")
    @PostMapping(value = "/api/modelFile/dropByConditions")
    public ResultTemplate<Object> dropByConditions(@RequestBody JSONObject jsonObject) {
        ModelFileStatus modelFileStatus=jsonObject.toBean(ModelFileStatus.class);
        List<String> idArray = jsonObject.getBeanList("ids", String.class);
        if (idArray != null && idArray.isEmpty())
        {
            try {
                modelFileStatusService.dropModelFileStatus(new ArrayList<>(), modelFileStatus);
            }catch (Exception e){
                ResultTemplate.fail("未知错误");
            }
        }
        List<Integer> idList = new ArrayList<>();
        ArrayStrToInt(idArray,idList);
       
        try {
            modelFileStatusService.dropModelFileStatus(idList, modelFileStatus);
        }catch (Exception e){
            ResultTemplate.fail("未知错误");
        }
        return ResultTemplate.success("删除成功！");
    }

    public static void ArrayStrToInt(List<String> idArray,List<Integer> idList) {
        if (idArray != null) {
            for (String s : idArray) {
                String[] ssplit = s.split("-");
                for (String s1 : ssplit) {
                    idList.add(Integer.parseInt(s1));
                }
            }
        }
    }

    private boolean couldUpload(String className)
    {
        ModelFileStatus modelFileStatus = new ModelFileStatus();
        modelFileStatus.setClassName(className);
        List<Map<String,Object>> usages = modelFileStatusService.selectUserAndFileStatus(modelFileStatus);
        for (Map<String,Object> map : usages) {
            Date date1=new Date();
            if(Objects.equals(map.get("dealStatus").toString(), "executing"))
            {
                return false;
            }else if(Objects.equals(map.get("dealStatus").toString(), "success"))
            {
                SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
                if(fmt.format(date1).equals(fmt.format((Date) map.get("createTime")))) {
                    return false;
                }
                ModelFileStatus modelFileStatus1 = new ModelFileStatus(map);
                modelFileStatusService.dropModelFileStatus(new ArrayList<>(), modelFileStatus1);
            }
        }
        return true;
    }

    private boolean couldUpload(String className,String observationTime)
    {
        ModelFileStatus modelFileStatus = new ModelFileStatus();
        modelFileStatus.setClassName(className);
        List<Map<String,Object>> usages = modelFileStatusService.selectUserAndFileStatus(modelFileStatus);
        for (Map<String,Object> map : usages) {
            if(Objects.equals(map.get("dealStatus").toString(), "executing"))
            {
                return false;
            }else if(Objects.equals(map.get("dealStatus").toString(), "success"))
            {
                if(observationTime.equals(map.get("observationTime"))) {
                    return false;
                }
//                ModelFileStatus modelFileStatus1 = new ModelFileStatus(map);
//                modelFileStatusService.dropModelFileStatus(new ArrayList<>(), modelFileStatus1);
            }
        }
        return true;
    }

    @PostMapping(value = "/api/modelFile/getModelFileStatus")
    public ResultTemplate<Object> getModelFileStatus(@RequestBody JSONObject jsonObject) {
        ModelFileStatus modelFileStatus=jsonObject.toBean(ModelFileStatus.class);
        List<Map<String,Object>> usages = modelFileStatusService.selectUserAndFileStatus(modelFileStatus);
        return ResultTemplate.success(usages);
    }

    // 2. 直接展示图片的接口
    @PostMapping(value = "/api/modelFile/preview")
    public ResponseEntity<Resource> getPreviewImage(@RequestBody JSONObject jsonObject) {
        String modelName = jsonObject.getStr("modelName");
        String userName=jsonObject.getStr("userName");
        String createUserId = jsonObject.getStr("createUserId");
        String relativePath="";
        String observationTime= jsonObject.getStr("observationTime");
        String className = jsonObject.getStr("className");
        if(observationTime.isEmpty() || className.isEmpty())
        {
            return ResponseEntity.status(500).build();
        }
        String year=observationTime.substring(0,4);
        String month=observationTime.substring(5,7);
        ModelFileStatus modelFileStatus=new ModelFileStatus();
        modelFileStatus.setDealStatus("success");
        modelFileStatus.setClassName(className);
        modelFileStatus.setUserName(userName);
        modelFileStatus.setCreateUserid(createUserId);
        modelFileStatus.setObservationTime(observationTime);

        if(modelName.equals("XGB")||modelName.equals("CNN"))
        {
            List<Map<String,Object>> mapList=modelFileStatusService.selectUserAndFileStatus(modelFileStatus);
            if(!mapList.isEmpty())
            {
                String filepath=mapList.get(0).get("filepath").toString();
                filepath=filepath.replace(File.separator+File.separator, File.separator);
                //filename=className+""+File.separator+""+filepath.substring(filepath.lastIndexOf(""+File.separator+"")+1);
                String filename=filepath.replace(FileRootDirPath,"");

                relativePath =year+File.separator+month+File.separator+
                        filename.substring(0,filename.lastIndexOf('.'))+"_"+modelName+File.separator;
            }else {
                return ResponseEntity.status(400).build();
            }
        }
        else if(modelName.equals("rfV2")||modelName.equals("CNNV2"))
        {
            modelFileStatus.setType("multiple");
            List<Map<String,Object>> mapList=modelFileStatusService.selectUserAndFileStatus(modelFileStatus);
            if(!mapList.isEmpty())
            {
                String filepath=mapList.get(0).get("filepath").toString();
                filepath=filepath.replace(File.separator+File.separator, File.separator);
                //filename=className+""+File.separator+""+filepath.substring(filepath.lastIndexOf(""+File.separator+"")+1);
                String filename=filepath.replace(FileRootDirPath+"land"+File.separator,"");
                filename = filename.substring(0,filename.length()-1)+"_SAR";
                relativePath =year+File.separator+month+File.separator+
                        filename+File.separator;
                modelName=filename;
            }else {
                return ResponseEntity.status(400).build();
            }
        }
        String fileName=modelName+"_prediction_preview.png";
        Path filePath = Paths.get(ResultRootPath+relativePath, fileName);

        return getImageResponse(filePath, fileName);
    }

    // 3. 下载混淆矩阵文件的接口
    @PostMapping("/api/modelFile/download/confusion_matrix")
    public ResponseEntity<Resource> downloadConfusionMatrix(@RequestBody JSONObject jsonObject) {
        String modelName = jsonObject.getStr("modelName");
        String fileName = modelName + "_confusion_matrix.png";
        String userName = jsonObject.getStr("userName");
        String createUserId = jsonObject.getStr("createUserId");
        String relativePath="";
        if(modelName.equals("XGB")||modelName.equals("CNN"))
        {
            String observationTime= jsonObject.getStr("observationTime");
            String className = jsonObject.getStr("className");
            String year=observationTime.substring(0,4);
            String month=observationTime.substring(5,7);
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
                filepath=filepath.replace(""+File.separator+""+File.separator+"",""+File.separator+"");
                //filename=className+""+File.separator+""+filepath.substring(filepath.lastIndexOf(""+File.separator+"")+1);
                String filename=filepath.replace(FileRootDirPath,"");

                relativePath =year+File.separator+month+File.separator+
                        filename.substring(0,filename.lastIndexOf('.'))+"_"+modelName+File.separator;
            }else {
                return ResponseEntity.status(400).build();
            }
        }
        Path filePath = Paths.get(ResultRootPath+relativePath, fileName);
        return getFileResponse(filePath, fileName, "image/png");
    }

    // 4. 下载分类统计文件的接口
    @PostMapping("/api/modelFile/download/class_stats")
    public ResponseEntity<Resource> downloadClassStats(@RequestBody JSONObject jsonObject) {
        String modelName = jsonObject.getStr("modelName");
        String fileName=modelName + "_class_stats.txt";;

        String userName = jsonObject.getStr("userName");
        String createUserId = jsonObject.getStr("createUserId");
        String relativePath="";
        if(modelName.equals("XGB")||modelName.equals("CNN"))
        {
            String observationTime= jsonObject.getStr("observationTime");
            String className = jsonObject.getStr("className");
            String year=observationTime.substring(0,4);
            String month=observationTime.substring(5,7);
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
                filepath=filepath.replace(""+File.separator+""+File.separator+"",""+File.separator+"");
                //filename=className+""+File.separator+""+filepath.substring(filepath.lastIndexOf(""+File.separator+"")+1);
                String filename=filepath.replace(FileRootDirPath,"");

                relativePath =year+File.separator+month+File.separator+
                        filename.substring(0,filename.lastIndexOf('.'))+"_"+modelName+File.separator;
            }else {
                return ResponseEntity.status(400).build();
            }
        }
        Path filePath = Paths.get(ResultRootPath+relativePath, fileName);
        return getFileResponse(filePath, fileName, "text/plain");
    }

    // 5. 下载单分类热力图的接口
    @PostMapping("/api/modelFile/download/heatmaps_summary")
    public ResponseEntity<Resource> downloadHeatmapsSummary(@RequestBody JSONObject jsonObject) {
        String modelName = jsonObject.getStr("modelName");
        String relativePath = ""+File.separator+"class_heatmaps_"+modelName;
        String fileName="class_heatmaps_summary.png";
        Path filePath = Paths.get(ResultRootPath+relativePath, fileName);
        return getFileResponse(filePath, fileName, "image/png");
    }

    // 6. 栅格文件下载
    @PostMapping("/api/modelFile/download/tif")
    public ResponseEntity<Resource> downloadTifFile(@RequestBody JSONObject jsonObject) {
        String modelName = jsonObject.getStr("modelName");
        String fileName = modelName + "_prediction.tif";
        String userName = jsonObject.getStr("userName");
        String createUserId = jsonObject.getStr("createUserId");
        String relativePath="";
        if(modelName.equals("XGB")||modelName.equals("CNN"))
        {
            String observationTime= jsonObject.getStr("observationTime");
            String className = jsonObject.getStr("className");
            String year=observationTime.substring(0,4);
            String month=observationTime.substring(5,7);
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
                filepath=filepath.replace(File.separator+File.separator,File.separator);
                //filename=className+""+File.separator+""+filepath.substring(filepath.lastIndexOf(""+File.separator+"")+1);
                String filename=filepath.replace(FileRootDirPath,"");

                relativePath =year+File.separator+month+File.separator+
                        filename.substring(0,filename.lastIndexOf('.'))+"_"+modelName+File.separator;
            }else {
                return ResponseEntity.status(400).build();
            }
        }
        Path filePath = Paths.get(ResultRootPath+relativePath, fileName);
        return getFileResponse(filePath, fileName, "image/tiff");
    }
    private final String plantResultPath = ResultRootPath;

    // 植被结果下载
    @PostMapping("/api/modelFile/PlantDownload")
    public ResponseEntity<Resource> downloadPlantFiles(@RequestBody JSONObject jsonObject) {
        String modelName = jsonObject.getStr("modelName");
        String userName=jsonObject.getStr("userName");
        String createUserId = jsonObject.getStr("createUserId");
        String relativePath="";
        String observationTime= jsonObject.getStr("observationTime");
        String className = jsonObject.getStr("className");
        String fileName = "200502_"+modelName ;
        String type=  jsonObject.get("type").toString();
        String pngType = (jsonObject.get("pngType")==null?"simple":jsonObject.get("pngType").toString());

        if(modelName.equals("fanyan"))
        {
            modelName="RF";
            fileName = "200502_"+modelName ;
        } else if (modelName.equals("fanyanNN")) {
            modelName="GA_1DResNet";
            fileName = "200502_"+modelName ;
        }else {
            String year=observationTime.substring(0,4);
            String month=observationTime.substring(5,7);
            ModelFileStatus modelFileStatus=new ModelFileStatus();
            modelFileStatus.setDealStatus("success");
            modelFileStatus.setClassName(className);
            modelFileStatus.setUserName(userName);
            modelFileStatus.setCreateUserid(createUserId);
            modelFileStatus.setObservationTime(observationTime);
            modelFileStatus.setModelName(modelName);
            if (modelName.equals("fanyanV2")) {
                modelName = "Ada-XGB";
                fileName=modelName;
                pngType = "prediction_preview";
            } else if (modelName.equals("fanyanRF")) {
                modelName = "RF";
                fileName=modelName;
            }
            modelFileStatus.setType("multiple");
            List<Map<String,Object>> mapList=modelFileStatusService.selectUserAndFileStatus(modelFileStatus);
            if(!mapList.isEmpty())
            {
                String filepath=mapList.get(0).get("filepath").toString();
                filepath=filepath.replace(File.separator+File.separator, File.separator);
                //filename=className+""+File.separator+""+filepath.substring(filepath.lastIndexOf(""+File.separator+"")+1);
                String filename=filepath.replace(FileRootDirPath+"plant"+File.separator,"");

                relativePath =year+File.separator+month+File.separator+
                        filename.substring(0,filename.length()-1)+"_"+modelName+File.separator;
            }else {
                return ResponseEntity.status(400).build();
            }
        }


        Path filePath;
        if(Objects.equals(type, "tif"))
        {
            fileName+="_prediction."+type;
            filePath =Paths.get(plantResultPath+relativePath, fileName);
            return getFileResponse(filePath, fileName, "image/tiff");
        } else if (Objects.equals(type, "png")) {
            fileName+="_"+pngType+"."+type;
            filePath =  Paths.get(plantResultPath+relativePath, fileName);
            return getImageResponse(filePath, fileName);
        }else{
            return ResponseEntity.status(500).body(null);
        }
    }

    //获取植被模型文件
    @PostMapping("/api/model/plantTrainResult")
    public ResponseEntity<Resource> downloadPlantTrainResult(@RequestBody JSONObject jsonObject) {
        String modelName = jsonObject.getStr("modelName");
        String userName = jsonObject.getStr("userName");
        String createUserId = jsonObject.getStr("createUserId");
        String observationTime = jsonObject.getStr("observationTime");
        String type = jsonObject.getStr("type");
        String fileName = "";
        String filePath = ResultRootPath + "model" + File.separator;
        int step = jsonObject.getInt("DownloadStep");

        // 验证类型
        if (!type.equals("train")) {
            return ResponseEntity.status(400).build();
        }

        // 根据模型类型和步骤确定文件名
        if (modelName.equals("1DResnet")) {
            if (step == 0) {
                fileName = observationTime + "_1dresnet.h5";
            } else {
                fileName = observationTime + "_1dresnet_scaler.pkl";
            }
        } else if (modelName.equals("RFV2")) {
            fileName = observationTime + "_RF.pkl";
        } else {
            return ResponseEntity.status(500).build();
        }

        Path path = Paths.get(filePath).resolve(fileName).normalize();
        // 使用封装的通用方法返回文件
        return getFileResponse(path, fileName,"application/octet-stream");
    }

    // 通用方法：获取图片响应
    public ResponseEntity<Resource> getImageResponse(Path filePath, String fileName) {
        try {
            File file = filePath.toFile();
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }
            Resource resource = new FileSystemResource(file);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                    .contentType(MediaType.IMAGE_PNG)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // 通用方法：获取文件下载响应
    private ResponseEntity<Resource> getFileResponse(Path filePath, String fileName, String contentType) {
        try {
            File file = filePath.toFile();
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }
            Resource resource = new FileSystemResource(file);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + fileName + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
