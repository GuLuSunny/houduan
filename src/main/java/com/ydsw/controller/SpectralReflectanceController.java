package com.ydsw.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fengwenyi.api.result.ResultTemplate;
import com.ydsw.domain.Device;
import com.ydsw.domain.SpectralReflectance;
import com.ydsw.service.DeviceService;
import com.ydsw.service.SpectralReflectanceService;
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
public class SpectralReflectanceController {
    @Autowired
    private SpectralReflectanceService spectralReflectanceService;
    @Autowired
    private DeviceService deviceService;

    /**
     * 根据观测时间和设备号获取光谱数据
     *
     * @return 包含光谱数据的响应对象
     * @author Jayny-plus
     * //@param time  观测时间
     * //@param device 设备号
     */
    @PreAuthorize("hasAnyAuthority('api_SpectralReflectance')")
    @PostMapping("/api/SpectralReflectance")
    public ResultTemplate<Object> getSpectralReflectanceByTimeAndDevice(
            @RequestBody Map<String, String> requestBody) {
        // 记录开始时间
        long startTime = System.nanoTime();
        List<Device> deviceList = deviceService.fetchDeviceData(null, "", "01");
        Map deviceMap = new HashMap();
        for (Device device : deviceList) {
            deviceMap.put(device.getId(), device.getDeviceName());
        }
        String time = requestBody.get("time");
        String device = requestBody.get("device");
        List<Map<String, Object>> SpectralReflectanceList;
        if (device.equals("0") || device.isEmpty()) {
            SpectralReflectanceList = spectralReflectanceService.fetchDataByObservationTime(time);
        } else {
            SpectralReflectanceList = spectralReflectanceService.fetchDataByObservationTimeAndDevice(time, Integer.valueOf(device),null);
        }
        List<Map<String, Object>> ResultList = new ArrayList<>();
        if (SpectralReflectanceList.isEmpty()) {
            return ResultTemplate.fail("该数据不存在！");
        }
        for (int i = 0; i < SpectralReflectanceList.size(); i++) {
            String deviceId = SpectralReflectanceList.get(i).get("device_id").toString();
            int deviceIdInt = Integer.parseInt(deviceId);
            //站点名称和id转换
            String deviceName = (String) deviceMap.get(deviceIdInt);
            SpectralReflectanceList.get(i).put("deviceName",deviceName);
        }
        //统一格式，一个对象一条线
        Map<String, Object> res = new HashMap<>();
        res.put("wavelength", "");//每段数据的开头
        res.put("deviceId", SpectralReflectanceList.get(0).get("device_id").toString());
        res.put("deviceName", SpectralReflectanceList.get(0).get("deviceName").toString());
        res.put("data", "");
        ResultList.add(res);
        //同一设备的数据用’,‘分割，存放在同一Map中
        for (Map<String, Object> map : SpectralReflectanceList) {//wavelength,device_id,data
            boolean exchanged = false;//设备是否保存过的反
            for (Map<String, Object> res1 : ResultList) {
                if (res1.get("deviceId").equals(map.get("device_id").toString())) {
                    if (!res1.get("wavelength").toString().isEmpty()) {
                        res1.put("wavelength", res1.get("wavelength") + "," + map.get("wavelength").toString());
                        res1.put("data", res1.get("data") + "," + map.get("data"));
                    } else {
                        res1.put("wavelength", res1.get("wavelength") + (map.get("wavelength")).toString());
                        res1.put("data", res1.get("data").toString() + map.get("data").toString());
                    }
                    exchanged = true;
                    break;
                }
            }
            if (!exchanged) {
                Map<String, Object> resBegin = new HashMap<>();
                resBegin.put("deviceId", map.get("device_id").toString());
                resBegin.put("deviceName", map.get("deviceName").toString());
                resBegin.put("wavelength", map.get("wavelength").toString());
                resBegin.put("data", map.get("data"));
                ResultList.add(resBegin);
            }
        }
        // 记录结束时间
        long endTime = System.nanoTime();
        // 计算运行时间（纳秒）
        long duration = endTime - startTime;
        // 将纳秒转换为毫秒（1 毫秒 = 1,000,000 纳秒）
        double durationInMillis = duration / 1_000_000.0;
        durationInMillis=durationInMillis/1000;
        log.info("查询数据花费时间：" + durationInMillis + "s");
        return ResultTemplate.success(ResultList);
    }

    /*
     * //@param time  观测时间
     * //@return 返回当天有多少行数据。
     * */
    @PreAuthorize("hasAnyAuthority('api_GetRowNumOfDateData')")
    @PostMapping("/api/GetRowNumOfDateData")
    public ResultTemplate<Object> getRowNumOfDateData(@RequestBody Map<String, String> requestBody) {
        String time = requestBody.get("time");
        List<Map<String, Object>> SpectralReflectanceList;
        SpectralReflectanceList = spectralReflectanceService.fetchDataByObservationTime(time);
        if (SpectralReflectanceList.isEmpty()) {
            return ResultTemplate.fail("当天数据不存在！");
        }
        //统一格式，一个对象一条线
        //每段数据的开头
        Integer device = (Integer) SpectralReflectanceList.get(0).get("device_id");
        //同一设备的数据用’,‘分割，存放在同一Map中
        int wavelengthNum = 0;
        //判断逻辑:每个横坐标对应所有纵坐标，每个纵坐标对应所有横坐标。
        for (Map<String, Object> map : SpectralReflectanceList) {//wavelength,device_id,data
            if (map.get("device_id").equals(device)) {
                wavelengthNum++;
            }
        }
        Map<String, Object> num = new HashMap<>();
        num.put("wavelengthNum", wavelengthNum);
        return ResultTemplate.success(num);
    }

    /**
     * 插入光谱数据
     *
     * @param jsonArray 包含光谱数据的JSON数组
     * @return 操作结果
     * @author Jayny-Plus
     */
    @PreAuthorize("hasAnyAuthority('api_SpectralReflectance_insert')")
    @PostMapping(value = "/api/SpectralReflectance/insert")
    @ResponseBody
    @Transactional
    public ResultTemplate<Object> SpectralReflectanceInsert(@RequestBody JSONArray jsonArray) {
        // 记录开始时间
        long startTime = System.nanoTime();
        List<SpectralReflectance> SpectralReflectanceList = JSONUtil.toList(jsonArray, SpectralReflectance.class);
        for (SpectralReflectance spectralReflectance : SpectralReflectanceList)
        {
            List<Map<String,Object>> mapList = spectralReflectanceService.fetchDataByObservationTimeAndDevice(spectralReflectance.getObservationTime(), Integer.valueOf(spectralReflectance.getDeviceId()), Integer.valueOf(spectralReflectance.getWavelength()));
            for(Map<String,Object> map : mapList){
                if(Objects.equals(spectralReflectance.getDeviceId(), String.valueOf(map.get("device_id")))&&Objects.equals(spectralReflectance.getObservationTime(),map.get("observation_time").toString())&&(Objects.equals(spectralReflectance.getWavelength(),String.valueOf(map.get("wavelength"))))){
                    List<Device> deviceList=deviceService.fetchDeviceData(Integer.parseInt(spectralReflectance.getDeviceId()),null,"01");
                    if(deviceList==null)
                    {
                        return ResultTemplate.fail("不存在的站点！");
                    }else if(deviceList.isEmpty())
                    {
                        return ResultTemplate.fail("不存在的站点！");
                    }
                    return ResultTemplate.fail(deviceList.get(0).getDeviceName()+"站点，日期为"+spectralReflectance.getObservationTime()+",波长为"+spectralReflectance.getWavelength()+"的数据已经存在！");
                }
            }
            spectralReflectance.setStatus(0);
            spectralReflectance.setType("form");
            spectralReflectance.setCreateTime(new Date());
        }
        boolean flag = spectralReflectanceService.saveBatch(SpectralReflectanceList);
        // 记录结束时间
        long endTime = System.nanoTime();
        // 计算运行时间（纳秒）
        long duration = endTime - startTime;
        // 将纳秒转换为毫秒（1 毫秒 = 1,000,000 纳秒）
        double durationInMillis = duration / 1_000_000.0;
        durationInMillis=durationInMillis/1000;
        log.info("查询数据花费时间：" + durationInMillis + "s");
        if (flag) {
            return ResultTemplate.success();
        }
        return ResultTemplate.fail();
    }

    /*
     * @param createUserId 上传人ID；
     * @param userName 上传人昵称
     * */
    @PreAuthorize("hasAnyAuthority('api_SpectralReflectance_insertByExcel')")
    @PostMapping(value = "/api/SpectralReflectance/insertByExcel")
    @Transactional
    public ResultTemplate<Object> SpectralReflectanceInsertByExcel(
            @RequestParam("createUserId") String userUid, @RequestParam("userName") String userName,
            @RequestParam("contactPhone") String contactPhone,@RequestParam("contactAddress") String contactAddress,
            @RequestParam("productionUnit") String productionUnit,@RequestParam("contactEmail") String contactEmail,
            @RequestParam("open") Integer open,@RequestParam("dataIntroduction") String dataIntroduction,
            @RequestParam MultipartFile fileMul) {
        // 记录开始时间
        long startTime = System.nanoTime();

        //获取01所有设备code和设备名称-预获取
        List<Device> deviceList = deviceService.fetchDeviceData(null, "", "01");
        Map deviceMap = new HashMap();
        for (Device device : deviceList) {
            deviceMap.put(device.getDeviceName(),device.getId());
        }

        List<SpectralReflectance> spectralReflectanceList = new ArrayList<>();
        if (userUid.isEmpty()) {
            userUid = UUID.randomUUID().toString();
        }
        //获取传入文件的本名
        String fileName = fileMul.getOriginalFilename();
        String fileNameOutOfType;
        String fileType;
        fileNameOutOfType = fileName.substring(0, fileName.lastIndexOf("."));
        fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        if (!fileType.equals("xls") && !fileType.equals("xlsx")) {
            return ResultTemplate.fail("文件类型不匹配！请传入xls或xlsx文件！");
        }
        try {
            InputStream inputStream1 = fileMul.getInputStream();
            String newName = userName + "-" + userUid + "-" + fileNameOutOfType;
            Map<String, String> maps = ExcelParserUtils.getFileNameAndPath(newName, fileType, "guangpu");
            String filePathName = maps.get("savePath") + maps.get("fileName");
            List<Sheet> sheetList = ExcelParserUtils.RETSheetsByName(inputStream1, fileType, "光谱反射率");
            if (sheetList == null) {
                //System.out.println("未找到符合条件的sheet");
                return ResultTemplate.fail("文件模板错误！请按正确的模板填写数据！");
            }
            Map<String, String> oneOfResult = new HashMap<>();
            Date date = new Date();
            for (Sheet sheet : sheetList) {
                oneOfResult = ExcelParserUtils.parseExcelFileForSpectral(sheet, fileType);

                String[] wavelengths = oneOfResult.get("wavelength").split(",");
                String[] datas = oneOfResult.get("data").split(",");
                String[] deviceIds = oneOfResult.get("deviceId").split(",");
                String observationTime = sheet.getSheetName().substring(5);
                String observationTimeOutofyear = observationTime.substring(5);
                if(!observationTimeOutofyear.contains("-"))
                {
                    return ResultTemplate.fail("日期格式错误");
                }
                String mouth = observationTimeOutofyear.substring(0,observationTimeOutofyear.indexOf("-"));
                String day = observationTimeOutofyear.substring(observationTimeOutofyear.indexOf("-")+1);
                if(mouth.isEmpty()||day.isEmpty())
                {
                    return ResultTemplate.fail("日期格式错误");
                }
                if(mouth.length()==1){
                    mouth = "0"+mouth;
                }
                if(day.length()==1){
                    day = "0"+day;
                }
                observationTime=observationTime.substring(0,5)+mouth+"-"+day;
                String regex = "[0-9]{4}-[0-9]{2}-[0-9]{2}";
                Pattern pattern = Pattern.compile(regex);
                Matcher m = pattern.matcher(observationTime);
                boolean dateFlag = m.matches();
                if (!dateFlag) {
                    return ResultTemplate.fail("请检查日期是否按模板填写！");
                }
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                try{
                    Date date2 = formatter.parse(observationTime);
                }catch(Exception e){
                    return ResultTemplate.fail("请检查日期是否按模板填写！");
                }
                for (int i = 0; i < deviceIds.length; i++) {
                    String deviceIdName = deviceIds[i];
                    //站点名称和id转换
                    String ob=String.valueOf(deviceMap.get(deviceIdName));
                    if (ob== null||"".equals(ob)) {
                        return ResultTemplate.fail(deviceIdName + "站点转换失败！");
                    }
                    Integer deviceId = Integer.parseInt(ob);

                    List<Map<String,Object>> spectralReflectanceListExist = spectralReflectanceService.fetchDataByObservationTimeAndDevice(observationTime, deviceId, null);
                    for (int j = 0; j < wavelengths.length; j++) {
                        String wavelength = wavelengths[j];
                        String data = datas[i + j * deviceIds.length];
                        if(data.isEmpty())
                        {
                            return ResultTemplate.fail("存在空缺数据！");
                        }
                        SpectralReflectance spectralReflectance = new SpectralReflectance(wavelength,deviceId, data, observationTime, date, fileType,userName, filePathName, fileNameOutOfType, userUid, 0,contactPhone,contactAddress,productionUnit,contactEmail,open,dataIntroduction);
                        //查询数据库是否存在相同数据
                        Boolean bs = spectralReflectanceListExist.stream()
                                .anyMatch(device -> Objects.equals(device.get("device_id"),deviceId) &&
                                        Objects.equals(device.get("wavelength"),Integer.parseInt(wavelength))&& Objects.equals(device.get("observation_time"), spectralReflectance.getObservationTime()));
                        if(bs){
                            return ResultTemplate.fail(deviceIdName+"站点，日期为"+spectralReflectance.getObservationTime()+",波长为"+spectralReflectance.getWavelength()+"的数据已经存在！");
                        }
                        spectralReflectanceList.add(spectralReflectance);
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
        boolean flag = spectralReflectanceService.saveBatch(spectralReflectanceList);
// 记录结束时间
        long endTime = System.nanoTime();
        // 计算运行时间（纳秒）
        long duration = endTime - startTime;
        // 将纳秒转换为毫秒（1 毫秒 = 1,000,000 纳秒）
        double durationInMillis = duration / 1_000_000.0;
        durationInMillis=durationInMillis/1000;
        log.info("查询数据花费时间：" + durationInMillis + "s");
        if (flag) {
            return ResultTemplate.success("运行成功，文件已保存！");
        }
        return ResultTemplate.fail("数据入库时出错！");
    }

    /*
     * @param time  观测时间
     * 根据时间分页查询
     * */
    @PreAuthorize("hasAnyAuthority('api_SpectralReflectance_pageQuery')")
    @RequestMapping(value = "/api/SpectralReflectance/pageQuery")
    @ResponseBody
    public ResultTemplate<Object> SpectralReflectancePageQuery(@RequestBody JSONObject jsonObject) {
        int currentPage = jsonObject.getInt("currentPage");
        int pageSize = jsonObject.getInt("pageSize");
        if (currentPage < 1) {
            return ResultTemplate.fail("无效页码！");
        } else if (pageSize < 1) {
            return ResultTemplate.fail("无效单位！");
        }
        String time = (String) jsonObject.get("time");
        String filepath = (String) jsonObject.get("filepath");
        SpectralReflectance spectralReflectance = new SpectralReflectance();
        spectralReflectance.setObservationTime(time);
        spectralReflectance.setFilepath(filepath);
        IPage<Map<String, Object>> page = spectralReflectanceService.getSpectralReflectancePageListByCondition(currentPage, pageSize, spectralReflectance);
        // 使用传统的for循环遍历记录列表
        List<Map<String, Object>> records = page.getRecords();
        for (int i = 0; i < records.size(); i++) {
            Map<String, Object> record = records.get(i);

            String[] deviceIds = record.get("deviceId").toString().split(",");
            String deviceIdsStr = "";
            for (String deviceId : deviceIds) {
                if (deviceId.length() != 4) {
                    if(deviceId.length()==1) {
                        deviceId = "0"+ deviceId;
                    }
                    deviceId="LH"+deviceId;
                    deviceIdsStr+=deviceId+",";
                }
            }
            record.put("deviceId", deviceIdsStr.substring(0,deviceIdsStr.length()-1));
            // 从记录中获取filepath字段的值
            AtmosphereController.renameFilepath(record);
        }
        return ResultTemplate.success(page);
    }

    /*
     * 暂定
     * 根据时间和波长删除
     * */
    @PreAuthorize("hasAnyAuthority('api_delByDateAndWaveLength')")
    @DeleteMapping(value = "/api/delByDateAndWaveLength")
    @Transactional
    public ResultTemplate<Object> delByDateAndWaveLength(@RequestBody Map<String, String> map) {
        String time = map.get("time");
        String[] wavelength = map.get("wavelength").split(",");
        int num = 0;
        for (String s : wavelength) {
            num += spectralReflectanceService.delBytimeAndWavelength(time, s);
        }
        if (num > wavelength.length) {
            return ResultTemplate.success("共删除" + num + "条数据");
        } else {
            return ResultTemplate.fail("删除时出现异常" + "共删除" + num + "条数据");
        }
    }

    /*
     * 根据id删除和查询条件删除，若id列表存在，只根据id列表删除，否则，根据条件删除，必须存在至少一个删除条件
     * */
    @PreAuthorize("hasAnyAuthority('api_SpeDelById')")
    @RequestMapping(value = "/api/SpeDelById")
    @Transactional
    public ResultTemplate<Object> SpeDelById(@RequestBody JSONObject jsonObject) {
        List<String> idArray = jsonObject.getBeanList("ids", String.class);//id列表
        String filepath = jsonObject.getStr("filepath");
        String dateSelected = jsonObject.getStr("dateSelected");
        if ((idArray == null || idArray.isEmpty()) && (filepath == null || "".equals(filepath.trim()) && (dateSelected == null || "".equals(dateSelected.trim())))) {
            return ResultTemplate.fail("参数出错！");
        }
        if (idArray.size() == 0) {
            //根据条件删除
            spectralReflectanceService.delByIdList(new ArrayList<>(), dateSelected, filepath);
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
            spectralReflectanceService.delByIdList(idList, "", "");
        } catch (Exception e) {
            return ResultTemplate.fail("删除时出错！");
        }
        return ResultTemplate.success("删除成功！");
    }
}
