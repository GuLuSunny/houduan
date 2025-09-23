package com.ydsw.controller;

import cn.hutool.json.JSONObject;
import com.fengwenyi.api.result.ResultTemplate;
import com.ydsw.domain.ModelFileStatus;
import com.ydsw.domain.ModelStatus;
import com.ydsw.domain.User;
import com.ydsw.service.ModelFileStatusService;
import com.ydsw.service.ModelListService;
import com.ydsw.service.UserService;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@Slf4j
public class ModelFileStatusController {
    @Autowired
    private ModelFileStatusService modelFileStatusService;
    final private String FileRootDirPath="D:\\heigankoumodel\\fileTemp\\";

    private final String ResultRootPath = "D:\\heigankoumodel\\code\\result\\";
    @Autowired
    private UserService userService;

    @Autowired
    private ModelListService modelListService;
    @PreAuthorize("hasAnyAuthority('api_modelFile_upload')")
    @PostMapping(value = "/api/modelFile/upload")
    public ResultTemplate<Object> uploadModelFile(@RequestParam("tiffile") MultipartFile file,
                                                  @RequestParam("createUserid") String userUid,
                                                  @RequestParam("userName") String userName,
                                                  @RequestParam("className") String className,
                                                  @RequestParam("observationTime")String observationTime) {

        List<String> classNameList = modelListService.getAllClassName();
        if (!classNameList.contains(className)) {
            return ResultTemplate.fail("非法的类型参数！");
        }
        if (!userAlive(userUid, userName, className)) {
            return ResultTemplate.fail("非法用户！");
        }

        String originalFilename = file.getOriginalFilename();
        String fileType = originalFilename.substring(originalFilename.lastIndexOf("."));
        if (!fileType.equalsIgnoreCase(".tif")) {
            return ResultTemplate.fail("文件类型错误！");
        }

        if (!couldUpload(className)) {
            return ResultTemplate.fail("服务器繁忙，请稍后重试");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        String formattedDate = sdf.format(date);
        String filename = userName + "-" + userUid+"_"+ observationTime +
                "_" + formattedDate + "_HH.tif";
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
        String fileName=modelName+"_prediction_preview";
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
                filepath=filepath.replace("\\\\","\\");
                //filename=className+"\\"+filepath.substring(filepath.lastIndexOf("\\")+1);
                String filename=filepath.replace("D:\\heigankoumodel\\fileTemp\\","");

                relativePath =year+File.separator+month+File.separator+className+File.separator
                        +filename.substring(filename.lastIndexOf('.'))+"_"+modelName+File.separator;
            }else {
                return ResponseEntity.status(400).build();
            }
        }
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
                filepath=filepath.replace("\\\\","\\");
                //filename=className+"\\"+filepath.substring(filepath.lastIndexOf("\\")+1);
                String filename=filepath.replace("D:\\heigankoumodel\\fileTemp\\","");

                relativePath =year+File.separator+month+File.separator+className+File.separator
                        +filename.substring(filename.lastIndexOf('.'))+"_"+modelName+File.separator;
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
                filepath=filepath.replace("\\\\","\\");
                //filename=className+"\\"+filepath.substring(filepath.lastIndexOf("\\")+1);
                String filename=filepath.replace("D:\\heigankoumodel\\fileTemp\\","");

                relativePath =year+File.separator+month+File.separator+className+File.separator
                        +filename.substring(filename.lastIndexOf('.'))+"_"+modelName+File.separator;
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
        String relativePath = "\\class_heatmaps_"+modelName;
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
                filepath=filepath.replace("\\\\","\\");
                //filename=className+"\\"+filepath.substring(filepath.lastIndexOf("\\")+1);
                String filename=filepath.replace("D:\\heigankoumodel\\fileTemp\\","");

                relativePath =year+File.separator+month+File.separator+className+File.separator
                        +filename.substring(filename.lastIndexOf('.'))+"_"+modelName+File.separator;
            }else {
                return ResponseEntity.status(400).build();
            }
        }
        Path filePath = Paths.get(ResultRootPath+relativePath, fileName);
        return getFileResponse(filePath, fileName, "image/tiff");
    }
    private final String plantResultPath = "D:\\heigankoumodel\\products";

    // 植被结果下载
    @PostMapping("/api/modelFile/PlantDownload")
    public ResponseEntity<Resource> downloadPlantFiles(@RequestBody JSONObject jsonObject) {
        String modelName = jsonObject.getStr("modelName");
        String type=  jsonObject.get("type").toString();
        if(Objects.equals(type, "tif"))
        {
            String fileName = "200502_"+modelName +"."+ type;
            Path filePath = Paths.get(plantResultPath, fileName);
            return getFileResponse(filePath, fileName, "image/tiff");
        } else if (Objects.equals(type, "png")) {
            String fileName = "200502_"+modelName +"."+ type;
            Path filePath = Paths.get(plantResultPath, fileName);
            return getImageResponse(filePath, fileName);
        }else{
            return ResponseEntity.status(500).body(null);
        }
    }
    // 通用方法：获取图片响应
    private ResponseEntity<Resource> getImageResponse(Path filePath, String fileName) {
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
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
