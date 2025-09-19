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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@Slf4j
public class ModelFileStatusController {
    @Autowired
    private ModelFileStatusService modelFileStatusService;
    final private String FileRootDirPath="D:\\heigankoumodel\\fileTemp\\";


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
        String filename = userName + "-" + userUid + "_" + formattedDate + "_HH.tif";
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
        try {
            // 创建目录（如果不存在）
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 保存文件
            File dest = new File(filepath);
            file.transferTo(dest);

            // 保存文件记录到数据库

            modelFileStatus.setDealStatus("success");
            modelFileStatusService.save(modelFileStatus);

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
            if(Objects.equals(map.get("usageStatus").toString(), "executing"))
            {
                return false;
            }else if(Objects.equals(map.get("usageStatus").toString(), "success"))
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


}
