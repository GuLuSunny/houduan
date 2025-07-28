package com.ydsw.controller;

import cn.hutool.json.JSONObject;
import com.fengwenyi.api.result.ResultTemplate;
import com.ydsw.domain.ModelFileStatus;
import com.ydsw.domain.ModelStatus;
import com.ydsw.domain.User;
import com.ydsw.service.ModelFileStatusService;
import com.ydsw.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@Slf4j
public class ModelFileStatusController {
    @Autowired
    private ModelFileStatusService modelFileStatusService;
    String plantFileRootDirPath="D:\\heigankoumodel\\plantCover\\";
    String solidFileRootDirPath ="D:\\heigankoumodel\\tudifugaifenlei\\";
    String waterFileRootDirPath ="D:\\heigankoumodel\\waterRetrieval\\";

    @Autowired
    private UserService userService;

    @PreAuthorize("hasAnyAuthority('api_modelFile_upload')")
    @PostMapping(value = "/api/modelFile/upload")
    public ResultTemplate<Object> uploadModelFile(@RequestParam("tiffile") MultipartFile file
            ,@RequestParam("createUserid") String userUid, @RequestParam("userName") String userName,
            @RequestParam("modelName") String modelName) {

        if (userAlive(userUid, userName, modelName)) return ResultTemplate.fail("非法用户！");
        String fileType= Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf("."));
        if(!fileType.equals("tif"))
        {
            return ResultTemplate.fail("文件类型错误！");
        }

        return ResultTemplate.fail("未知错误!");
    }

    public boolean userAlive(String userUid, String userName, String modelName) {
        User user=new User();
        user.setUsername(userName);
        user.setStatus(0);
        List<Map<String,Object>> userList= userService.selectUserByCondition(user);
        boolean flag=false;
        for (Map<String,Object> map : userList) {
            if(Objects.equals(map.get("id"), userUid)){
                flag=true;
            }
        }
        if(!flag){
            return true;
        }
        user.setId(Integer.valueOf(userUid));
        user.setMemo(modelName);
        return false;
    }
    @PreAuthorize("hasAnyAuthority('api_modelFile_dropByConditions')")
    @PostMapping(value = "/api/modelFile/dropByConditions")
    public ResultTemplate<Object> dropByConditions(@RequestParam("Json") JSONObject jsonObject) {
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

    private boolean couldUpload(String modelName)
    {
        ModelFileStatus modelFileStatus = new ModelFileStatus();
        modelFileStatus.setModelName(modelName);
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


}
