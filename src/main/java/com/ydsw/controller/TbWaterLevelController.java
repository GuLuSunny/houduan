package com.ydsw.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fengwenyi.api.result.ResultTemplate;
import com.ydsw.domain.Device;
import com.ydsw.domain.TbWaterLevel;
import com.ydsw.pojo.excel.WaterExcel;
import com.ydsw.service.DeviceService;
import com.ydsw.service.TbWaterLevelService;
import com.ydsw.utils.ExcelParserUtils;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author zhangkaifei
 * //@description: TODO
 * //@date 2024/7/26  16:22
 * //@Version 1.0
 */
@Slf4j
@RestController
public class TbWaterLevelController {
    @Autowired
    private TbWaterLevelService tbWaterLevelService;

    @Autowired
    private DeviceService deviceService;
    /*
     * 根据具体时间或时间段返回水位信息
     * @param jsonArray
     * */
    @PreAuthorize("hasAnyAuthority('api_waterlevel')")
    @PostMapping(value = "/api/waterlevel")
    public ResultTemplate<Object> getWaterLevelByObservationTime(@RequestBody Map<String, String> requestBody) {
        String observationTime = requestBody.get("observationTime");
        String deviceName=requestBody.get("deviceName");
        List<Device> deviceList=deviceService.fetchDeviceData(null,deviceName,"06");
        if(deviceList.isEmpty())
        {
            return ResultTemplate.fail("非法的观测地点！");
        }
        int deviceId=deviceList.get(0).getId();
        if (observationTime == null || observationTime.isEmpty()) {
            String timeEarliest = requestBody.get("timeEarliest");
            String timeLatest = requestBody.get("timeLatest");
            if (timeEarliest == null || timeEarliest.isEmpty() || timeLatest == null || timeLatest.isEmpty()) {
                return ResultTemplate.fail("请求格式错误！errorType:无效参数！");
            }


            List<Map<String, Object>> tbWaterLevelList = tbWaterLevelService.fetchWaterLevelByTimePeriod(timeEarliest, timeLatest,deviceId);

            return ResultTemplate.success(tbWaterLevelList);
        }
        List<Map<String, Object>> tbWaterLevelList = tbWaterLevelService.findWaterLevelByObservationTime(observationTime,deviceId);
        return ResultTemplate.success(tbWaterLevelList);
    }

    /*
     * @param year 年
     * 返回一年内的水位信息*/
    @PreAuthorize("hasAnyAuthority('api_waterlevelSelectByYear')")
    @PostMapping(value = "/api/waterlevelSelectByYear")
    public ResultTemplate<Object> getWaterLevelByObservationTimeEvertYear(@RequestBody Map<String, String> requestBody) {

        //数据准备部分
        String year = requestBody.get("year");
        if (year == null || year.isEmpty()) {
            return ResultTemplate.fail("请求格式错误！errorType:无效参数！");
        }
        String deviceName=requestBody.get("deviceName");
        List<Device> deviceList=deviceService.fetchDeviceData(null,deviceName,"06");
        if(deviceList.isEmpty())
        {
            return ResultTemplate.fail("非法的观测地点！");
        }
        int deviceId=deviceList.get(0).getId();
        String timeEarliest = year + "-01-01";
        String timeLatest = year + "-12-31";
        List<Map<String, Object>> tbWaterLevelList = tbWaterLevelService.fetchWaterLevelByTimePeriod(timeEarliest, timeLatest,deviceId);
        Map<String, String> averageWaterLevel = new HashMap<>();
        averageWaterLevel.put("month", "01,02,03,04,05,06,07,08,09,10,11,12");
        averageWaterLevel.put("data", "");
        averageWaterLevel.put("yearAvg", "");
        averageWaterLevel.put("waterdiff", "");
        averageWaterLevel.put("monthMax", "");
        averageWaterLevel.put("monthMin", "");
        Double Sum[] = new Double[13];
        Double waterLevelAverage[] = new Double[13];
        int count[] = new int[13];
        Double waterLevelMaxLevel[] = new Double[13];
        Double waterLevelMinLevel[] = new Double[13];

        //数据处理部分
        for (int i = 0; i <= 12; i++) {
            Sum[i] = 0.00;
            waterLevelMaxLevel[i] = 0.00;
            waterLevelMinLevel[i] = 100000.00;
        }
        for (Map<String, Object> t : tbWaterLevelList) {
            Double waterLevel = Double.parseDouble(t.get("waterlevel").toString());
            int Date = Integer.parseInt(t.get("observationTime").toString().substring(5, 7));
            if (waterLevel > waterLevelMaxLevel[0]) {
                waterLevelMaxLevel[0] = waterLevel;
            }
            if (waterLevel < waterLevelMinLevel[0]) {
                waterLevelMinLevel[0] = waterLevel;
            }
            if (waterLevel > waterLevelMaxLevel[Date]) {
                waterLevelMaxLevel[Date] = waterLevel;
            }
            if (waterLevel < waterLevelMinLevel[Date]) {
                waterLevelMinLevel[Date] = waterLevel;
            }
            count[0]++;
            Sum[0] += waterLevel;
            count[Date]++;
            Sum[Date] += waterLevel;
        }

        if (Sum[0] == 0.0) {
            return ResultTemplate.fail("该年份水位信息不存在！");
        }

        //录入部分
        waterLevelAverage[0] = Sum[0] / (count[0] * 1.0);
        averageWaterLevel.put("yearAvg", String.format("%.2f", waterLevelAverage[0]));
        averageWaterLevel.put("yearMax", String.format("%.2f", waterLevelMaxLevel[0]));
        averageWaterLevel.put("yearMin", String.format("%.2f", waterLevelMinLevel[0]));

        for (int i = 1; i <= 12; i++) {
            waterLevelAverage[i] = Sum[i] / (count[i] * 1.0);
            String str = String.format("%.2f", waterLevelAverage[i]);
            String str1 = String.format("%.2f", waterLevelAverage[0] - waterLevelAverage[i]);
            String str2 = String.format("%.2f", waterLevelMaxLevel[i]);
            String str3 = String.format("%.2f", waterLevelMinLevel[i]);
            if (str.equals("NaN") || str.isEmpty()) {
                str = "";
                str1 = "";
                str2 = "";
                str3 = "";
            }

            if (i != 1) {
                str = "," + str;
                str1 = "," + str1;
                str2 = "," + str2;
                str3 = "," + str3;
            }
            averageWaterLevel.put("data", averageWaterLevel.get("data") + str);
            averageWaterLevel.put("waterdiff", averageWaterLevel.get("waterdiff") + str1);
            averageWaterLevel.put("monthMax", averageWaterLevel.get("monthMax") + str2);
            averageWaterLevel.put("monthMin", averageWaterLevel.get("monthMin") + str3);
        }
        return ResultTemplate.success(averageWaterLevel);
    }

    /*
     * @param month 年-月
     * 月查询数据返回
     * */
    @PreAuthorize("hasAnyAuthority('api_waterlevelSelectBymonth')")
    @PostMapping(value = "api/waterlevelSelectBymonth")
    public ResultTemplate<Object> getWatelLevelByTimeEverMonth(@RequestBody Map<String, String> requestBody) {
        //数据获取
        String month = requestBody.get("month");
        String deviceName=requestBody.get("deviceName");
        List<Device> deviceList=deviceService.fetchDeviceData(null,deviceName,"06");
        if(deviceList.isEmpty())
        {
            return ResultTemplate.fail("非法的观测地点！");
        }
        int deviceId=deviceList.get(0).getId();
        if (month == null || month.isEmpty()) {
            return ResultTemplate.fail("请求参数错误！errorType：月参数为空！");
        }
        String timeEarliest = month + "-01";
        String timeLatest = month + "-31";
        List<Map<String, Object>> tbWaterLevelList = tbWaterLevelService.fetchWaterLevelByTimePeriod(timeEarliest, timeLatest,deviceId);
        Map<String, String> averageWaterLevel = new HashMap<>();
        String monStr = month.substring(month.lastIndexOf("-") + 1);
        String yearStr = month.substring(0, month.lastIndexOf("-"));

        int[] dayNums = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

        int tmp = Integer.parseInt(yearStr);

        if ((tmp % 100 != 0 && tmp % 4 == 0) || tmp % 400 == 0) {
            dayNums[2] = 29;
        }

        for (int i = 1; i <= dayNums[Integer.parseInt(monStr)]; i++) {
            if (i != 1) {
                averageWaterLevel.put("day", averageWaterLevel.get("day") + "," + String.format("%02d", i));
            } else {
                averageWaterLevel.put("day", String.format("%02d", i));
            }
        }

        averageWaterLevel.put("data", "");
        averageWaterLevel.put("waterdiff", "");
        Double Sum[] = new Double[32];
        int count = 0;
        Double monthMax = 0.00;
        Double monthMin = 100000.00;
        Double monthAvg = 0.0;
        //数据处理
        for (int i = 0; i < 32; i++) {
            Sum[i] = 0.0;
        }
        for (Map<String, Object> t : tbWaterLevelList) {
            Double waterLevel = Double.parseDouble(t.get("waterlevel").toString());
            int Date = Integer.parseInt(t.get("observationTime").toString().substring(8, 10));
            if (waterLevel > monthMax) {
                monthMax = waterLevel;
            }
            if (waterLevel < monthMin) {
                monthMin = waterLevel;
            }
            Sum[0] += waterLevel;
            count++;
            Sum[Date] += waterLevel;
        }
        if (Sum[0] == 0.0) {
            return ResultTemplate.fail("该月水位信息不存在！");
        }
        monthAvg = Sum[0] / (count * 1.0);
        //数据导入
        averageWaterLevel.put("monthAvg", String.format("%.2f", monthAvg));
        averageWaterLevel.put("monthMax", String.format("%.2f", monthMax));
        averageWaterLevel.put("monthMin", String.format("%.2f", monthMin));
        for (int i = 1; i <= 31; i++) {
            String str = String.format("%.2f", Sum[i]);
            String str1 = String.format("%.2f", monthAvg - Sum[i]);
            if (str.equals("NaN") || str.isEmpty() || str.equals("0.00")) {
                str = "";
                str1 = "";
            }
            if (i != 1) {
                str = "," + str;
                str1 = "," + str1;
            }
            averageWaterLevel.put("data", averageWaterLevel.get("data") + str);
            averageWaterLevel.put("waterdiff", averageWaterLevel.get("waterdiff") + str1);
        }
        return ResultTemplate.success(averageWaterLevel);
    }

    /*
     * 单条插入
     * */
    @PreAuthorize("hasAnyAuthority('api_tbWaterLevel_insert')")
    @PostMapping(value = "/api/tbWaterLevel/insert")
    @Transactional
    public ResultTemplate<Object> tbWaterLevel_insert(@RequestBody JSONArray jsonArray) {
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        List<TbWaterLevel> tbWaterLevelList = new ArrayList<>();
        TbWaterLevel tbWaterLevel = JSONUtil.toBean(jsonObject.toString(), TbWaterLevel.class);
        if(tbWaterLevel.getWaterLevel()==null|| tbWaterLevel.getWaterLevel().equals("") ||tbWaterLevel.getDeviceId()==null)
        {
            return ResultTemplate.fail("水位数据或站点数据缺失！");
        }
        List<Map<String,Object>> mapList=tbWaterLevelService.findWaterLevelByObservationTime(tbWaterLevel.getObservationTime(),tbWaterLevel.getDeviceId());
        if(!mapList.isEmpty())
        {
            for (Map<String,Object> map : mapList) {
                if(Objects.equals(tbWaterLevel.getObservationTime(), map.get("observationTime").toString()) && Objects.equals(tbWaterLevel.getDeviceId(), (Integer) map.get("deviceId")))
                {
                    return ResultTemplate.fail("当天:"+ tbWaterLevel.getObservationTime()+"该地水位数据已经存在");
                }
            }

        }
//        String deviceName=jsonObject.getStr("deviceName");
        tbWaterLevel.setCreateTime(new Date());
        tbWaterLevel.setStatus(0);
        tbWaterLevel.setType("form");
//        List<Device> deviceList = deviceService.fetchDeviceData(null, deviceName, "06");
//        tbWaterLevel.setDeviceId(deviceList.get(0).getId());
        tbWaterLevelList.add(tbWaterLevel);
        boolean flag = tbWaterLevelService.saveBatch(tbWaterLevelList);
        if (flag) {
            return ResultTemplate.success();
        }
        return ResultTemplate.fail();
    }

    /*
     * Excel文件插入（带传参）
     * */
    @PreAuthorize("hasAnyAuthority('api_tbWaterLevel_insertByExcel')")
    @PostMapping(value = "api/tbWaterLevel/insertByExcel")
    @ResponseBody
    @Transactional
    public ResultTemplate<Object> tbWaterLevel_insertByExcel(
            @RequestParam("createUserId") String userUid, @RequestParam("userName") String userName,
            @RequestParam("contactPhone") String contactPhone,@RequestParam("contactAddress") String contactAddress,
            @RequestParam("productionUnit") String productionUnit,@RequestParam("contactEmail") String contactEmail,
            @RequestParam("open") Integer open,@RequestParam("dataIntroduction") String dataIntroduction,@RequestParam MultipartFile fileMul) {
        if (userUid.isEmpty()) {
            return ResultTemplate.fail("获取用户id失败！");
        }else if (userName.isEmpty()) {
            return ResultTemplate.fail("用户名为空！");
        }


        //准备工作
        List<TbWaterLevel> tbWaterLevelList = new ArrayList<>();
        String year = "";
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
            InputStream inputStream = fileMul.getInputStream();
            InputStream inputStream1 = fileMul.getInputStream();
            //fileMul=new MockMultipartFile(file.getName(),inputStreamChange);
            String newName = userName + "-" + userUid + "-" + fileNameOutOfType;
            Map<String, String> maps = ExcelParserUtils.getFileNameAndPath(newName, fileType, "shuiwei");
            String filePathName = maps.get("savePath") + maps.get("fileName");
            JSONArray singleRCArray = new JSONArray();
            List list = new ArrayList();
            list.add(1);
            list.add(0);
            singleRCArray.add(list);
            JSONObject waterObj = ExcelParserUtils.parseExcelFile(inputStream, WaterExcel.class, 0, 3, 4, singleRCArray, fileType);
            //log.info(waterObj.toString());
            JSONArray peoples = (JSONArray) waterObj.get("classlist");
            List<WaterExcel> t = JSONUtil.toList(peoples, WaterExcel.class);
            Map<String, String> SAC = ExcelParserUtils.getSheetNamesAndFirstCellOfExcel(inputStream1, fileType);
            year = ExcelParserUtils.getOnlyNumsFromStr(SAC.get("firstCell"));
            if(year.length()!=4)
            {
                return ResultTemplate.fail("日期格式错误");
            }
            String deviceName=SAC.get("firstCell").substring(0,SAC.get("firstCell").indexOf("年")-4);
            List<Device> deviceList=deviceService.fetchDeviceData(null,deviceName,"06");
            if(deviceList.isEmpty())
            {
                return ResultTemplate.fail("非法地段！");
            }
            Integer deviceId=deviceList.get(0).getId();
            //实时简化对应 //1-"01" 2-"02"
            String[] numsString = new String[12];
            for (int i = 0; i < 9; i++) {
                numsString[i] = "0" + (i + 1);
            }
            numsString[9] = "10";
            numsString[10] = "11";
            numsString[11] = "12";
            //对一年十二个月的同一天进行操作
            for (WaterExcel p : t) {
                //转换插入
                //天数为空，代表本年数据录入完毕,其余数据自行计算
                if (p.getDay().isEmpty()) {
                    break;
                }
                //观测时间整合
                String ResultTime = "";
                //关键水位数据
                String[] Result = new String[12];
//                System.out.println(p);//对象--未转换
                String day = p.getDay().substring(0, p.getDay().length() - 2);
                if (day.length() == 1) {
                    day = "0" + day;
                }
                p.putInArray(Result);//重写实体类方法
                for (int i = 0; i < 12; i++) {
                    if (Result[i].isEmpty() || Result[i] == null) {

                    } else {
                        //观测时间
                        ResultTime = "";
                        ResultTime += year + "-"; //获取方式未确定
                        ResultTime += numsString[i] + "-";
                        ResultTime += day;

                        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        try{
                            Date date = formatter.parse(ResultTime);
                        }catch(Exception e){
                            return ResultTemplate.fail("请检查日期是否按模板填写！");
                        }
                        //入类、重写实体类方法
                        TbWaterLevel tbWaterLevel = new TbWaterLevel(deviceId,Result[i], ResultTime, new Date(), fileType, userName, filePathName,fileNameOutOfType, userUid, 0,contactPhone,contactAddress,productionUnit,contactEmail,open,dataIntroduction);
                        List<Map<String,Object>> mapList=tbWaterLevelService.findWaterLevelByObservationTime(tbWaterLevel.getObservationTime(),tbWaterLevel.getDeviceId());
                        if(!mapList.isEmpty())
                        {
                            for (Map<String,Object> map : mapList) {
                                if(Objects.equals(tbWaterLevel.getObservationTime(), map.get("observationTime").toString()) && Objects.equals(tbWaterLevel.getDeviceId(), (Integer) map.get("deviceId")))
                                {
                                    return ResultTemplate.fail("数据库中该年:"+year+" 水位数据已经存在,请勿重复提交！");
                                }
                            }

                        }
                        tbWaterLevelList.add(tbWaterLevel);
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
        boolean flag = tbWaterLevelService.saveBatch(tbWaterLevelList);
        if (flag) {
            return ResultTemplate.success("运行成功，文件已保存！");
        }
        return ResultTemplate.fail("数据入库时出错！");
    }

    //后端模板下载方式（自定义）
    @PreAuthorize("hasAnyAuthority('api_waterlevelExcelModelDownload')")
    @PostMapping(value = "api/waterlevelExcelModelDownload")
    public void modelDownload(@RequestBody Map<String, String> requestBody, HttpServletResponse response) throws IOException {
        String fileUploadPath = "C:\\Users\\Administrator\\Desktop\\项目资源\\";//附6.陆浑水库水位—上传模板.xls\\";
        String relativePath = requestBody.get("relativePath");
        String[] split = relativePath.split("/");
        String fileName = split[split.length - 1];
        File uploadFile = new File(fileUploadPath + relativePath);

        ServletOutputStream outputStream = response.getOutputStream();
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        response.setContentType("application/octet-stream");
        outputStream.write(FileUtil.readBytes(uploadFile));
        outputStream.flush();
        outputStream.close();
    }

    /*
     * 根据id删除和查询条件删除，若id列表存在，只根据id列表删除，否则，根据条件删除，必须存在至少一个删除条件
     * */
    @PreAuthorize("hasAnyAuthority('api_waterlevel_delByIds')")
    @RequestMapping(value = "/api/waterlevel/delByIds")
    @Transactional
    public ResultTemplate<Object> delByIds(@RequestBody JSONObject jsonObject) {
        List<Integer> idArray = jsonObject.getBeanList("ids", Integer.class);//id列表
        String filepath = jsonObject.getStr("filepath");
        String observationTime = jsonObject.getStr("observationTime");
        if ((idArray == null || idArray.isEmpty()) && (filepath == null || "".equals(filepath.trim()) && (observationTime == null || "".equals(observationTime.trim())))) {
            return ResultTemplate.fail("参数出错！");
        }
        if (idArray.size() == 0) {
            //根据条件删除
            tbWaterLevelService.delByIdList(new ArrayList<>(), observationTime, filepath);
            return ResultTemplate.success("删除成功！");
        }
        try {
            tbWaterLevelService.delByIdList(idArray, "", "");
        } catch (Exception e) {
            return ResultTemplate.fail("删除时出错！");
        }
        return ResultTemplate.success("删除成功！");
    }

    /*
     * @param jsonArray
     * 根据日期分页模糊查询
     * 水位分页查询
     * */
    @PreAuthorize("hasAnyAuthority('api_waterlevel_fetchBytimeandPage')")
    @RequestMapping(value = "/api/waterlevel/fetchBytimeandPage")
    @ResponseBody
    public ResultTemplate<Object> fetchBytimeAndPage(@RequestBody JSONObject jsonObject) {
        try {
            int currentPage = jsonObject.get("currentPage") == null ? 1 : jsonObject.getInt("currentPage");
            int pageSize = jsonObject.get("pageSize") == null ? 10 : jsonObject.getInt("pageSize");
            String deviceName=jsonObject.getStr("deviceName");
            List<Device> deviceList=deviceService.fetchDeviceData(null,deviceName,"06");
            if(deviceList.isEmpty())
            {
                return ResultTemplate.fail("非法的观测地点！");
            }
            int deviceId=deviceList.get(0).getId();
            jsonObject.set("deviceId",deviceId);
            TbWaterLevel tbWaterLevel = JSONUtil.toBean(jsonObject, TbWaterLevel.class);
            IPage<Map<String, Object>> page = tbWaterLevelService.getWaterLevelByPage(currentPage, pageSize, tbWaterLevel);
            List<Map<String, Object>> records = page.getRecords();
            for (int i = 0; i < records.size(); i++) {
                Map<String, Object> record = records.get(i);
                AtmosphereController.renameFilepath(record);
            }
            return ResultTemplate.success(page);
        } catch (NumberFormatException e) {
            return ResultTemplate.fail("数据查询失败" + e.getMessage());
        }

    }
}
