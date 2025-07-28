package com.ydsw.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fengwenyi.api.result.ResultTemplate;
import com.ydsw.domain.DroneImage;
import com.ydsw.service.DeviceService;
import com.ydsw.service.DroneImageService;
import com.ydsw.utils.ExcelParserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RestController
public class DroneImageController {
    @Autowired
    private DroneImageService droneImageService;

    @Autowired
    private DeviceService deviceService;
/*
* 无人机影像表单提交
* */
    @PreAuthorize("hasAnyAuthority('api_DroneImage_insert')")
    @PostMapping(value = "/api/DroneImage/insert")
    @Transactional
    public ResultTemplate<Object> DroneImageInsert(
            @RequestParam("deviceName")String deviceName,@RequestParam("typeDetail") String typeDetail,
      @RequestParam("observationTime") String observationTime,
     @RequestParam("createUserId") String userUid, @RequestParam("userName") String userName,
     @RequestParam("contactPhone") String contactPhone,@RequestParam("contactAddress") String contactAddress,
     @RequestParam("productionUnit") String productionUnit,@RequestParam("contactEmail") String contactEmail,
     @RequestParam("open") Integer open,@RequestParam("dataIntroduction") String dataIntroduction,
     @RequestParam MultipartFile fileMul) throws IOException {
        if (userUid.isEmpty()) {
            return ResultTemplate.fail("获取用户id失败！");
        }else if (userName.isEmpty()) {
            return ResultTemplate.fail("用户名为空！");
        } else if (deviceName.isEmpty()) {
            return ResultTemplate.fail("观测地点不能为空！");
        } else if (typeDetail.isEmpty()) {
            return ResultTemplate.fail("观测类型不能为空！");
        }
        String regex = "[0-9]{4}-[0-9]{2}-[0-9]{2}";
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(observationTime);
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        boolean dateFlag = m.matches();
        if (!dateFlag) {
            return ResultTemplate.fail("请检查日期是否按模板填写！");
        }
        try{
            Date date = formatter.parse(observationTime);
        }catch(Exception e){
            return ResultTemplate.fail("请检查日期是否按模板填写！");
        }
        //获取传入文件的本名
        String fileName = fileMul.getOriginalFilename();
        String fileNameOutOfType;
        String fileType;
        fileNameOutOfType = fileName.substring(0, fileName.lastIndexOf("."));
        fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        if(fileType.isEmpty())
        {
            return ResultTemplate.fail("文件拓展名为空!");
        }
        DroneImage droneImage;

        try {
            String newName = userName + "-" + userUid + "-" + fileNameOutOfType;
            Map<String, String> maps = ExcelParserUtils.getFileNameAndPath(newName, fileType, "DroneImage");
            String filePathName = maps.get("savePath") + maps.get("fileName");
            droneImage = new DroneImage(deviceName, observationTime,new Date(),fileType,typeDetail,
                    filePathName,fileNameOutOfType,userUid,userName,0,contactPhone,contactAddress,productionUnit,contactEmail,open,dataIntroduction);

            //保存文件
            try {
                ExcelParserUtils.saveFile(maps.get("savePath"), maps.get("fileName"), fileMul);
            } catch (IOException e) {
    //                throw new RuntimeException(e);
                return ResultTemplate.fail(e.getMessage());
            }
        } catch (IOException e) {
            return ResultTemplate.fail(e.getMessage());
        }

        boolean flag=droneImageService.save(droneImage);
        if (flag) {
            return ResultTemplate.success("运行成功，文件已保存！");
        }
        return ResultTemplate.fail("数据入库时出错！");
    }
    /*
     * @param jsonArray
     * 根据日期分页模糊查询
     * 无人机
     * */
    @PreAuthorize("hasAnyAuthority('api_Drone_pageQurey')")
    @PostMapping(value = "/api/Drone/pageQurey")
    public ResultTemplate<Object> pageQurey(@RequestBody JSONObject jsonObject) {
        int currentPage = jsonObject.getInt("currentPage");
        int pageSize = jsonObject.getInt("pageSize");
        if(currentPage < 1){
            return ResultTemplate.fail("无效页码！");
        }else if(pageSize < 1){
            return ResultTemplate.fail("无效单位！");
        }
        DroneImage droneImageClass = JSONUtil.toBean(jsonObject, com.ydsw.domain.DroneImage.class);

        IPage<Map<String, Object>> page =droneImageService.fetchDataByObservationTimeAndFilepath(currentPage,pageSize,droneImageClass);
        for (Map<String,Object> map:page.getRecords()) {

            AtmosphereController.renameFilepath(map);
        }
        return ResultTemplate.success(page);
    }

    /*
     * 根据id删除和查询条件删除，若id列表存在，只根据id列表删除，否则，根据条件删除，必须存在至少一个删除条件
     * */
    @PreAuthorize("hasAnyAuthority('api_Drone_DelByIds')")
    @RequestMapping(value = "/api/Drone/DelByIds")
    @Transactional
    public ResultTemplate<Object> SpeDelById(@RequestBody JSONObject jsonObject) {
        List<String> idArray = jsonObject.getBeanList("ids", String.class);//id列表
        String filepath = jsonObject.getStr("filepath");
        String dateSelected = jsonObject.getStr("observationTime");
        String deviceName = jsonObject.getStr("deviceId");
        String type=jsonObject.getStr("type");
        if (idArray != null && idArray.isEmpty()) {
            if (deviceName==null&&(filepath == null || filepath.trim().isEmpty() )&& (dateSelected == null || "".equals(dateSelected.trim()))) {
                return ResultTemplate.fail("无效参数！");
            }//根据条件删除
            droneImageService.delByIdList(new ArrayList<>(), dateSelected, filepath,deviceName,type);
            return ResultTemplate.success("删除成功！");
        }
        List<Integer> idList = new ArrayList<>();
        ModelFileStatusController.ArrayStrToInt(idArray,idList);
        try {
            droneImageService.delByIdList(idList, dateSelected, filepath,deviceName,type);
        } catch (Exception e) {
            return ResultTemplate.fail("删除时出错！");
        }
        return ResultTemplate.success("删除成功！");
    }
}
