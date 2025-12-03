package com.ydsw.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fengwenyi.api.result.ResultTemplate;
import com.ydsw.domain.Device;
import com.ydsw.domain.WaterPhysicochemistry;
import com.ydsw.service.DeviceService;
import com.ydsw.service.WaterPhysicochemistryService;
import com.ydsw.utils.ExcelParserUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RestController
public class WaterPhysicochemistryController {
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private WaterPhysicochemistryService waterPhysicochemistryService;

    /**
     * 获取水理化学数据
     * @author yanzhimeng
     * @param //time 观测时间
     * @return 水理化学数据列表
     */
    @PreAuthorize("hasAnyAuthority('api_water_physicochemistry')")
    @PostMapping("/api/water_physicochemistry")
    public ResultTemplate<Object> getWaterPhysicochemistry(@RequestBody Map<String, String> requestBody) {
        String time=requestBody.get("time");
        String device=requestBody.get("device");
        List<Device> deviceList = deviceService.fetchDeviceData(null, "", "01");
        Map deviceMap = new HashMap();
        for (Device de : deviceList) {
            deviceMap.put(de.getId(), de.getDeviceName());
        }
        List<WaterPhysicochemistry> waterPhysicochemistryList = waterPhysicochemistryService.fetchDataByObservationTimeAndDevice(time,device);
        for (int i = 0; i < waterPhysicochemistryList.size(); i++) {
            String deviceId = waterPhysicochemistryList.get(i).getDeviceId();
            int deviceIdInt = Integer.parseInt(deviceId);
            //站点名称和id转换
            String deviceName = (String) deviceMap.get(deviceIdInt);
            waterPhysicochemistryList.get(i).setDeviceId(deviceName);
        }
        return ResultTemplate.success(waterPhysicochemistryList);
    }
    /*
     * @param jsonArray
     * 根据日期分页模糊查询
     * 水体理化
     * */
    @PreAuthorize("hasAnyAuthority('api_water_physicochemistry_pageQurey')")
    @PostMapping(value = "/api/water_physicochemistry/pageQurey")
    public ResultTemplate<Object> pageQurey(@RequestBody JSONObject jsonObject) {
        int currentPage = jsonObject.getInt("currentPage");
        int pageSize = jsonObject.getInt("pageSize");
        if(currentPage < 1){
            return ResultTemplate.fail("无效页码！");
        }else if(pageSize < 1){
            return ResultTemplate.fail("无效单位！");
        }
        WaterPhysicochemistry waterPhysicochemistry = JSONUtil.toBean(jsonObject, com.ydsw.domain.WaterPhysicochemistry.class);

        IPage<Map<String, Object>> page =waterPhysicochemistryService.fetchDataByObservationTimeAndFilepath(currentPage,pageSize,waterPhysicochemistry);
        List<Map<String, Object>> records = page.getRecords();
        for (int i = 0; i < records.size(); i++) {
            Map<String, Object> record = records.get(i);            //deviceID转换
            String deviceId = record.get("deviceId").toString();
            if(deviceId.length()!=4)
            {
                List<Device> deviceList = deviceService.fetchDeviceData(Integer.parseInt(deviceId), "", "01");
                record.put("deviceId", deviceList.get(0).getDeviceName());
            }
            AtmosphereController.renameFilepath(record);

        }
        return ResultTemplate.success(page);
    }
    /**
     * 插入水理化学数据
     * @author yanzhimeng
     * @param jsonArray 包含多个水理化学数据的JSON数组
     * @return 操作结果
     */
    @PreAuthorize("hasAnyAuthority('api_water_physicochemistry_insert')")
    @PostMapping(value = "/api/water_physicochemistry/insert")
    @ResponseBody
    @Transactional
    public ResultTemplate<Object> waterPhysicochemistryInsert(@RequestBody JSONArray jsonArray) {
        log.info(JSONUtil.toJsonStr(jsonArray));
        List<WaterPhysicochemistry> WaterPhysicochemistryList = JSONUtil.toList(jsonArray, WaterPhysicochemistry.class);
        for (WaterPhysicochemistry waterPhysicochemistry:WaterPhysicochemistryList)
        {
            List<WaterPhysicochemistry> waterPhysicochemistryList=waterPhysicochemistryService.fetchDataByObservationTimeAndDevice(waterPhysicochemistry.getObservationTime(),waterPhysicochemistry.getDeviceId());
            for (WaterPhysicochemistry waterPhysicochemistry1:waterPhysicochemistryList) {
                if(waterPhysicochemistry1.getObservationTime().equals(waterPhysicochemistry.getObservationTime())&&waterPhysicochemistry1.getDeviceId().equals(waterPhysicochemistry.getDeviceId())){
                    List<Device> deviceList=deviceService.fetchDeviceData(Integer.parseInt(waterPhysicochemistry.getDeviceId()),null,"01");
                    if(deviceList==null)
                    {
                        return ResultTemplate.fail("不存在的站点！");
                    }else if(deviceList.isEmpty())
                    {
                        return ResultTemplate.fail("不存在的站点！");
                    }
                    return ResultTemplate.fail(deviceList.get(0).getDeviceName()+"站点，日期为"+waterPhysicochemistry1.getObservationTime()+"的数据已经存在！");
                }
            }
            waterPhysicochemistry.setStatus(0);
            waterPhysicochemistry.setCreateTime(new Date());
            waterPhysicochemistry.setType("form");
        }
        boolean flag = waterPhysicochemistryService.saveBatch(WaterPhysicochemistryList);
        if (flag) {
            return ResultTemplate.success();
        }
        return ResultTemplate.fail();
    }
    /*
     Excel文件上传
     * */
    @PreAuthorize("hasAnyAuthority('api_waterPhysicochemistry_insertByExcel')")
    @PostMapping(value = "/api/waterPhysicochemistry/insertByExcel")
    @Transactional
    public ResultTemplate<Object> waterPhysicochemistry_InsertByExcel(
            @RequestParam("createUserId") String userUid, @RequestParam("userName") String userName,
            @RequestParam("contactPhone") String contactPhone,@RequestParam("contactAddress") String contactAddress,
            @RequestParam("productionUnit") String productionUnit,@RequestParam("contactEmail") String contactEmail,
            @RequestParam("open") Integer open,@RequestParam("dataIntroduction") String dataIntroduction,
            @RequestParam MultipartFile fileMul) {
        if (userUid.isEmpty()) {
            userUid = UUID.randomUUID().toString();
        }
        //获取传入文件的本名
        List<WaterPhysicochemistry> waterPhysicochemistryList = new ArrayList<>();
        String fileName = fileMul.getOriginalFilename();
        String fileNameOutOfType;
        String fileType;
        fileNameOutOfType = fileName.substring(0, fileName.lastIndexOf("."));
        fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        if (!fileType.equals("xls") && !fileType.equals("xlsx")) {
            return ResultTemplate.fail("文件类型不匹配！请传入xls或xlsx文件！");
        }
        try {
            InputStream inputStream = fileMul.getInputStream();
            InputStream inputStream1 = fileMul.getInputStream();
            String newName = userName + "-" + userUid + "-" + fileNameOutOfType;
            Map<String, String> maps = ExcelParserUtils.getFileNameAndPath(newName, fileType, "shuitilihua");
            String filePathName = maps.get("savePath") + maps.get("fileName");
            List<Sheet> sheetList = ExcelParserUtils.RETSheetsByName(inputStream, fileType, "水体理化");
            if (sheetList == null || sheetList.isEmpty()) {
                //System.out.println("未找到符合条件的sheet");
                return ResultTemplate.fail("文件模板错误！请按正确的模板填写数据！");
            }

            JSONArray singleRCArray = new JSONArray();
            List list = new ArrayList();
            list.add(1);
            list.add(0);
            singleRCArray.add(list);

            JSONObject waterPhysicochemistryData = ExcelParserUtils.parseExcelFile(inputStream1, WaterPhysicochemistry.class, 0, 1, 3, singleRCArray, fileType);
            JSONArray rows = waterPhysicochemistryData.getJSONArray("classlist");
            waterPhysicochemistryList = JSONUtil.toList(rows,WaterPhysicochemistry.class);
            for (Sheet sheet : sheetList) {
                String observationTime = sheet.getSheetName().substring(4);
                String observationTimeOutofyear = observationTime.substring(5);
                String mouth = observationTimeOutofyear.substring(0,observationTimeOutofyear.indexOf("-"));
                String day = observationTimeOutofyear.substring(observationTimeOutofyear.indexOf("-")+1);
                if(mouth.isEmpty()||day.isEmpty()){
                    return ResultTemplate.fail("日期格式错误！");
                }
                if(mouth.length()==1){
                    mouth = "0"+mouth;
                }
                if(day.length()==1){
                    day = "0"+day;
                }
                observationTime = observationTime.substring(0,5)+mouth+"-"+day;
                String regex = "[0-9]{4}-[0-9]{2}-[0-9]{2}";
                Pattern pattern = Pattern.compile(regex);
                Matcher m = pattern.matcher(observationTime);
                boolean dateFlag = m.matches();
                if (!dateFlag) {
                    return ResultTemplate.fail("请检查日期是否按模板填写！");
                }
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                try{
                    Date date = formatter.parse(observationTime);
                }catch(Exception e){
                    return ResultTemplate.fail("请检查日期是否按模板填写！");
                }
                for (WaterPhysicochemistry waterPhysicochemistry : waterPhysicochemistryList) {
                    if (waterPhysicochemistry == null || waterPhysicochemistry.getDeviceId() == null || waterPhysicochemistry.getDeviceId().isEmpty()) {
                        waterPhysicochemistryList.remove(waterPhysicochemistry);
                        break;
                    }
                    if (waterPhysicochemistry.getWaterTemperature() == null || waterPhysicochemistry.getWaterTemperature().isEmpty()
                    && (waterPhysicochemistry.getChlorophyll() == null || waterPhysicochemistry.getChlorophyll().isEmpty())
                    && (waterPhysicochemistry.getCodmn() == null || waterPhysicochemistry.getCodmn().isEmpty())
                    && (waterPhysicochemistry.getDissolvedOxygen() == null  || waterPhysicochemistry.getDissolvedOxygen().isEmpty())
                    && (waterPhysicochemistry.getTn() == null || waterPhysicochemistry.getTn().isEmpty())
                    && (waterPhysicochemistry.getTp() == null || waterPhysicochemistry.getTp().isEmpty())
                    ){
                        waterPhysicochemistryList.remove(waterPhysicochemistry);
                        break;
                    }
                    String deviceId = waterPhysicochemistry.getDeviceId();
                    List<Device> deviceList= deviceService.fetchDeviceData(null,deviceId,"01");
                    deviceId = String.valueOf(deviceList.get(0).getId());
                    waterPhysicochemistry.setDeviceId(deviceId);
                    waterPhysicochemistry.waterPhysicochemistryAdd(observationTime,new Date(),fileType,userName,filePathName,fileNameOutOfType,userUid,0,contactPhone,contactAddress,productionUnit,contactEmail,open,dataIntroduction);
                    List<WaterPhysicochemistry> waterPhysicochemistryList1=waterPhysicochemistryService.fetchDataByObservationTimeAndDevice(observationTime,deviceId);
                    for (WaterPhysicochemistry waterPhysicochemistry1:waterPhysicochemistryList1) {
                        if(waterPhysicochemistry1.getObservationTime().equals(waterPhysicochemistry.getObservationTime())&&waterPhysicochemistry1.getDeviceId().equals(waterPhysicochemistry.getDeviceId())){
                            if(deviceList.isEmpty())
                            {
                                return ResultTemplate.fail("不存在的站点！");
                            }
                            return ResultTemplate.fail(deviceList.get(0).getDeviceName()+"站点，日期为"+waterPhysicochemistry1.getObservationTime()+"的数据已经存在！");
                        }
                    }
                }

            }

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
        //数据库录入部分
        boolean flag = waterPhysicochemistryService.saveBatch(waterPhysicochemistryList);

        if (flag) {
            return ResultTemplate.success("运行成功，文件已保存！");
        }
        return ResultTemplate.fail("数据入库时出错！");
    }


    /*
     * 根据id删除和查询条件删除，若id列表存在，只根据id列表删除，否则，根据条件删除，必须存在至少一个删除条件
     * */
    @PreAuthorize("hasAnyAuthority('api_water_physicochemistry_DelByIds')")
    @RequestMapping(value = "/api/water_physicochemistry/DelByIds")
    @Transactional
    public ResultTemplate<Object> SpeDelById(@RequestBody JSONObject jsonObject) {
        List<String> idArray = jsonObject.getBeanList("ids", String.class);//id列表
        String filepath = jsonObject.getStr("filepath");
        String dateSelected = jsonObject.getStr("dateSelected");
        String deviceId = jsonObject.getStr("deviceId");

        if (idArray.isEmpty()) {
            //根据条件删除
            try {
                waterPhysicochemistryService.delByIdList(new ArrayList<>(), dateSelected, filepath,deviceId);
            } catch (Exception e) {
                return ResultTemplate.fail("删除时出错！");
            }
            return ResultTemplate.success("删除成功！");
        }
        List<Integer> idList = new ArrayList<>();
        for (String s : idArray) {
            String[] ssplit = s.split("-");
            for (String s1 : ssplit) {
                idList.add(Integer.parseInt(s1));
            }
        }
        try {
            waterPhysicochemistryService.delByIdList(idList, dateSelected, filepath,deviceId);
        } catch (Exception e) {
            return ResultTemplate.fail("删除时出错！");
        }
        return ResultTemplate.success("删除成功！");
    }
}