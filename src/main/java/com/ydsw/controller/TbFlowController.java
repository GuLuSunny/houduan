package com.ydsw.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fengwenyi.api.result.ResultTemplate;
import com.ydsw.domain.Device;
import com.ydsw.domain.TbFlow;
import com.ydsw.pojo.excel.FlowExcel;
import com.ydsw.service.DeviceService;
import com.ydsw.service.TbFlowService;
import com.ydsw.utils.ExcelParserUtils;
import lombok.extern.slf4j.Slf4j;
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
public class TbFlowController {
    @Autowired
    private TbFlowService tbFlowService;
    @Autowired
    private DeviceService deviceService;

    /**
     * 返回一年内的径流信息
     */
    @PreAuthorize("hasAnyAuthority('api_tbFlowSelectByYear')")
    @PostMapping(value = "/api/tbFlowSelectByYear")
    public ResultTemplate<Object> getByYearAndDevice(@RequestBody Map<String, String> requestBody) {
        // 数据准备部分
        String year = requestBody.get("year");
        String device = requestBody.get("device");
        if (year == null || year.isEmpty()) {
            return ResultTemplate.fail("请求格式错误！errorType:无效参数！");
        }
        List<Device> deviceList = new ArrayList<>();
        if (device != null && !"".equals(device.trim())) {//有设备号
            Device d = new Device();
            d.setId(Integer.parseInt(device));
            deviceList.add(d);
        } else {//无设备号
            deviceList = deviceService.fetchDeviceData(null, "", "02");//所有径流观测点信息
        }
        Map<Object, Object> flowSummary = new HashMap<>();
        flowSummary.put("year", year);
        List dataList = new ArrayList<>();//数据对象数组
        for (int i = 0; i < deviceList.size(); i++) {//设备号遍利
            Integer deviceId = deviceList.get(i).getId();
            List<Map<String, Object>> tbFlowList = tbFlowService.findFlowByYearAndDevice(year, String.valueOf(deviceId));
            Map<Integer, List<Double>> mapFlowMonth = new TreeMap<>();//每个月的天流量
            Map<Integer, Integer> mapFlowMonthDay = new TreeMap<>();//每个月的天数
            Map<Integer, Double> mapFlowMonthCount = new TreeMap<>();//每个月的总流量
            Map<Integer, Double> mapFlowMonthMax = new TreeMap<>();//每个月的最高流量
            Map<Integer, Double> mapFlowMonthMin = new TreeMap<>();//每个月的最低流量
            Map<Integer, Double> mapFlowMonthAvg = new TreeMap<>();//每个月的平均流量
            Map<Integer, Double> mapFlowMonthDiff = new TreeMap<>();//每个月的流量差，月平均-年平均
            Double yearFlowAvg = 0.00;//年平均流量
            Double yearFlowMax = null;//年最高流量
            Double yearFlowMin = null;//年最低流量
            Double yearFlowSum = 0.00;//年总流量
            Integer yearDayCount = 0;//一年总天数
            //按月份分组
            for (int j = 0; j < tbFlowList.size(); j++) {
                Map<String, Object> objectMap = tbFlowList.get(j);//获取当前对象year , id,deviceId,flow,observationTime
                String observationTime = (String) objectMap.get("observationTime");//时间
                Double flow = Double.parseDouble((String) objectMap.get("flow"));//流量
                Integer month = Integer.parseInt(observationTime.substring(5, 7));//月份
                List<Double> flowList = mapFlowMonth.get(month);//获取当前月的天流量
                flowList = flowList != null ? flowList : new ArrayList<>();
                flowList.add(flow);
                mapFlowMonth.put(month, flowList);
                Integer monthDayCount = mapFlowMonthDay.get(month);//每个月的天数
                monthDayCount = monthDayCount != null ? monthDayCount : 0;
                monthDayCount++;
                mapFlowMonthDay.put(month, monthDayCount);
                Double monthFlowCount = mapFlowMonthCount.get(month);//每个月的总流量
                monthFlowCount = monthFlowCount != null ? monthFlowCount : 0.00;
                monthFlowCount += flow;
                mapFlowMonthCount.put(month, monthFlowCount);
                Double monthFlowMax = mapFlowMonthMax.get(month);//每个月的最高流量
                monthFlowMax = monthFlowMax != null ? monthFlowMax : flow;
                monthFlowMax = monthFlowMax < flow ? flow : monthFlowMax;
                mapFlowMonthMax.put(month, monthFlowMax);
                Double monthFlowMin = mapFlowMonthMin.get(month);//每个月的最低流量
                monthFlowMin = monthFlowMin != null ? monthFlowMin : flow;
                monthFlowMin = monthFlowMin > flow ? flow : monthFlowMin;
                mapFlowMonthMin.put(month, monthFlowMin);
                yearFlowSum = yearFlowSum + flow;//年总流量
                yearFlowMax = yearFlowMax != null ? yearFlowMax : flow;//年最高流量
                yearFlowMax = yearFlowMax < flow ? flow : yearFlowMax;
                yearFlowMin = yearFlowMin != null ? yearFlowMin : flow;//年最低流量
                yearFlowMin = yearFlowMin > flow ? flow : yearFlowMin;
                yearDayCount = yearDayCount + 1;//一年总天数
            }
            yearFlowAvg = Math.round((yearFlowSum / yearDayCount) * 100.0) / 100.0;//年平均流量
            // 使用for-each循环遍历HashMap
            for (Map.Entry<Integer, Double> entry : mapFlowMonthCount.entrySet()) {
                Integer month = entry.getKey();//月
                Double flow = entry.getValue();//月总流量
                Integer monthDayCount = mapFlowMonthDay.get(month);//月总日数
                Double mAvg = Math.round((flow / monthDayCount) * 100.0) / 100.0;
                mapFlowMonthAvg.put(month, mAvg);//每个月的平均流量
                mapFlowMonthDiff.put(month, Math.round((mAvg - yearFlowAvg) * 100.0) / 100.0);
            }
            Map<String, Object> map = new HashMap<>();//数据对象
            map.put("month", mapFlowMonth.keySet());//月份数组
            map.put("yearMax", yearFlowMax);//年最高径流量
            map.put("yearAvg", yearFlowAvg);//年平均径流量
            map.put("yearMin", yearFlowMin);//年最低径流量
            map.put("monthMax", mapFlowMonthMax.values());//月最高径流量数组
            map.put("monthMin", mapFlowMonthMin.values());//月最低径流量数组
            map.put("monthAvg", mapFlowMonthAvg.values());// 月平均径流量数组
            map.put("monthDiff", mapFlowMonthDiff.values());// 月平均-年平均流量数组
            map.put("device", deviceId);
            dataList.add(map);
        }
        flowSummary.put("data", dataList);
//        log.info(JSONUtil.toJsonStr(flowSummary));
        return ResultTemplate.success(flowSummary);
    }

    /*
     * 月查询径流数据返回
     */
    @PreAuthorize("hasAnyAuthority('api_tbFlowSelectByMonth')")
    @PostMapping(value = "api/tbFlowSelectByMonth")
    public ResultTemplate<Object> getFlowByMonthAndDevice(@RequestBody Map<String, String> requestBody) {
        // 数据获取
        String yearMonth = requestBody.get("month");
        String device = requestBody.get("device");
        if (yearMonth == null || yearMonth.isEmpty()) {
            return ResultTemplate.fail("请求格式错误！errorType:无效参数！");
        }
        List<Device> deviceList = new ArrayList<>();
        if (device != null && !"".equals(device.trim())) {//有设备号
            Device d = new Device();
            d.setId(Integer.parseInt(device));
            deviceList.add(d);
        } else {//无设备号
            deviceList = deviceService.fetchDeviceData(null, "", "02");//所有径流观测点信息
        }
        Map<Object, Object> flowSummary = new HashMap<>();
        flowSummary.put("yearMonth", yearMonth);
        List dataList = new ArrayList<>();//数据对象数组
        for (int i = 0; i < deviceList.size(); i++) {//设备号遍利
            Integer deviceId = deviceList.get(i).getId();
            List<Map<String, Object>> tbFlowList = tbFlowService.findFlowByYearMonthAndDevice(yearMonth, String.valueOf(deviceId));
            Map<Integer, Double> mapFlowMonth = new TreeMap<>();//每天径流量
            Map<Integer, Double> mapFlowMonthDiff = new TreeMap<>();//每日的流量差，日径流-月平均
            Double monthFlowAvg = 0.00;//月平均流量
            Double monthFlowMax = null;//月最高流量
            Double monthFlowMin = null;//月最低流量
            Double monthFlowSum = 0.00;//月总流量
            Integer monthDayCount = 0;//月总天数
            //按月份分组
            for (int j = 0; j < tbFlowList.size(); j++) {
                Map<String, Object> objectMap = tbFlowList.get(j);//获取当前对象year , id,deviceId,flow,observationTime
                String observationTime = (String) objectMap.get("observationTime");//时间
                Double flow = Double.parseDouble((String) objectMap.get("flow"));//流量
                Integer day= Integer.parseInt(observationTime.substring(8, 10));//日
                mapFlowMonth.put(day, flow);//设置当前日的天流量
                monthFlowSum = monthFlowSum + flow;//月总流量
                monthFlowMax = monthFlowMax != null ? monthFlowMax : flow;//月最高流量
                monthFlowMax = monthFlowMax < flow ? flow : monthFlowMax;
                monthFlowMin = monthFlowMin != null ? monthFlowMin : flow;//月最低流量
                monthFlowMin = monthFlowMin > flow ? flow : monthFlowMin;
                monthDayCount = monthDayCount + 1;//月中总天数
            }
            monthFlowAvg = Math.round((monthFlowSum / monthDayCount) * 100.0) / 100.0;//月平均流量
            // 使用for-each循环遍历HashMap
            for (Map.Entry<Integer, Double> entry : mapFlowMonth.entrySet()) {
                Integer monthDay = entry.getKey();//日
                Double flowDay = entry.getValue();//日流量
                mapFlowMonthDiff.put(monthDay, Math.round((flowDay - monthFlowAvg) * 100.0) / 100.0);
            }
            Map<String, Object> map = new HashMap<>();//数据对象
            map.put("month", mapFlowMonth.values());//每天径流量数组
            map.put("monthAvg", monthFlowAvg);//  月平均径流量
            map.put("monthMax", monthFlowMax);//月最高径流量
            map.put("monthMin", monthFlowMin);// 月最小径流量
            map.put("day", mapFlowMonth.keySet());//天数组
            map.put("flowdiff", mapFlowMonthDiff.values());//日径流-月平均
            map.put("device", deviceId);
            dataList.add(map);
        }
        flowSummary.put("data", dataList);
        log.info(JSONUtil.toJsonStr(flowSummary));
        return ResultTemplate.success(flowSummary);
    }

    /*
     * 表单插入
     */
    @PreAuthorize("hasAnyAuthority('api_tbFlow_insert')")
    @PostMapping(value = "/api/tbFlow/insert")
    @Transactional
    public ResultTemplate<Object> tbFlow_insert(@RequestBody JSONArray jsonArray) {

        JSONObject jsonObject = jsonArray.getJSONObject(0);
        List<TbFlow> tbFlowList = new ArrayList<>();
        TbFlow tbFlow = JSONUtil.toBean(jsonObject.toString(), TbFlow.class);
        if(tbFlow.getObservationTime()==null||tbFlow.getObservationTime().isEmpty()||tbFlow.getDeviceId()==null) {
            return ResultTemplate.fail("时间地点不明确！"+tbFlow.getObservationTime()+","+tbFlow.getDeviceId());
        }
        List<Map<String,Object>> mapList=tbFlowService.selectFlowByObservationTimeAndDeviceId(tbFlow.getObservationTime(),tbFlow.getDeviceId());
        for (Map<String,Object> map : mapList) {
            if(Objects.equals(tbFlow.getObservationTime(),map.get("observationTime"))&&Objects.equals(tbFlow.getDeviceId(),map.get("deviceId"))){
                List<Device> deviceList = deviceService.fetchDeviceData(tbFlow.getDeviceId(), null, "02");
                if(deviceList==null)
                {
                    return ResultTemplate.fail("不存在的站点！");
                }else if(deviceList.isEmpty())
                {
                    return ResultTemplate.fail("不存在的站点！");
                }
                return ResultTemplate.fail("该日："+tbFlow.getObservationTime()+",此地："+deviceList.get(0).getDeviceName()+"已经存在！");
            }
        }
        //String deviceName=jsonObject.getStr("deviceName");
        tbFlow.setCreateTime(new Date());
        tbFlow.setStatus(0);
        tbFlow.setType("form");
        //List<Device> deviceList = deviceService.fetchDeviceData(null, deviceName, "02");
        //tbFlow.setDeviceId(deviceList.get(0).getId());


        tbFlowList.add(tbFlow);
        boolean flag = tbFlowService.saveBatch(tbFlowList);
        if (flag) {
            return ResultTemplate.success();
        }
        return ResultTemplate.fail();
    }


    /*
     * Excel文件提交
     */
    @PreAuthorize("hasAnyAuthority('api_tbFlow_insertByExcel')")
    @PostMapping(value = "api/tbFlow/insertByExcel")
    @ResponseBody
    @Transactional
    public ResultTemplate<Object> tbFlow_insertByExcel(
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
        List<TbFlow> tbFlowList = new ArrayList<>();
        String year = "";  // Year will be extracted from the sheet header
        String month = ""; // Month will also be extracted from the sheet header

        if (userUid.isEmpty()) {
            userUid = UUID.randomUUID().toString();
        }

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
            String newName = userName + "-" + userUid + "-" + fileNameOutOfType;
            Map<String, String> maps = ExcelParserUtils.getFileNameAndPath(newName, fileType, "jingliu");
            String filePathName = maps.get("savePath") + maps.get("fileName");

            // Extract sheet names and first cell content
            Map<String, String> sheetNames = ExcelParserUtils.getSheetNamesAndFirstCellOfExcel(inputStream, fileType);

            String firstCell = sheetNames.get("firstCell");
            year = ExcelParserUtils.getOnlyNumsFromStr(firstCell.substring(0, 4));  // Extract 4-digit year
            month = ExcelParserUtils.getOnlyNumsFromStr(firstCell.replaceAll(".*年", "").replaceAll("月.*", "")); // Extract month

            month = (month.length() == 1) ? "0" + month : month;

            JSONArray singleRCArray = new JSONArray();
            List list = new ArrayList();
            list.add(1);
            list.add(0);
            singleRCArray.add(list);

            // Make sure to reset the InputStream for the new sheet
            inputStream = fileMul.getInputStream();  // Reset the stream
            JSONObject flowData = ExcelParserUtils.parseExcelFile(inputStream, FlowExcel.class, 0, 3, 4, singleRCArray, fileType);
            JSONArray rows = flowData.getJSONArray("classlist");

            List<FlowExcel> flowExcelList = JSONUtil.toList(rows, FlowExcel.class);

            Integer deviceId1;
            Integer deviceId2;
            String deviceName1="";
            String deviceName2="";
            inputStream = fileMul.getInputStream();
            Map<String,String> AllCellInRow=ExcelParserUtils.getAllCellByRowNum(inputStream,fileType,2);

            if (AllCellInRow != null) {
                String[] Cells = AllCellInRow.get("Cells").split("##");
                String[] Cell = Cells[0].split(",");
                deviceName1 = Cell[1];
                deviceName2 = Cell[2];

            }else {
                return ResultTemplate.fail("未知的站点信息");
            }
            List<Device> deviceList = deviceService.fetchDeviceData(null, deviceName1, "02");
            deviceId1 = deviceList.get(0).getId();
            deviceList = deviceService.fetchDeviceData(null, deviceName2, "02");
            deviceId2 = deviceList.get(0).getId();
              for (FlowExcel flowExcel : flowExcelList) {
                // Ensure 'row' is not null and has necessary data
                if (flowExcel == null || flowExcel.getDay() == null || flowExcel.getDay().isEmpty()) {
                    continue; // Skip if the row is null or day is empty
                }

                String day = flowExcel.getDay();
                day=day.substring(0,day.length()-2);
                day = (day.length() == 1) ? "0" + day : day;

                String observationTime = year + "-" + month + "-" + day;
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
                      System.out.println(date);
                  }catch(Exception e){
                        return ResultTemplate.fail("请检查日期是否按模板填写！");
                  }
                // Add each location's flow data for this date
                String data1=flowExcel.getData1();
                String data2=flowExcel.getData2();// Convert List<String> to String[]
                if(data1==null||data2==null)
                {
                    return ResultTemplate.fail("数据不在允许范围内!");
                }
                if(!data1.isEmpty())
                {
                    TbFlow tbFlow1=new TbFlow(deviceId1,data1,observationTime,new Date(),fileType,userName,filePathName,fileNameOutOfType,userUid,0,contactPhone,contactAddress,productionUnit,contactEmail,open,dataIntroduction);
                    List<Map<String,Object>> mapList=tbFlowService.selectFlowByObservationTimeAndDeviceId(observationTime,deviceId1);
                    for (Map<String,Object> map : mapList) {
                        if(Objects.equals(observationTime,map.get("observationTime"))&&Objects.equals(deviceId1,map.get("deviceId"))){
                            deviceList=deviceService.fetchDeviceData(deviceId1,null,"02");
                            return ResultTemplate.fail("该日："+observationTime+",此地："+deviceList.get(0).getDeviceName()+"已经存在！");
                        }
                    }
                    tbFlowList.add(tbFlow1);
                }
                if(!data2.isEmpty())
                {
                    TbFlow tbFlow2=new TbFlow(deviceId2,data2,observationTime,new Date(),fileType,userName,filePathName,fileNameOutOfType,userUid,0,contactPhone,contactAddress,productionUnit,contactEmail,open,dataIntroduction);
                    List<Map<String,Object>> mapList=tbFlowService.selectFlowByObservationTimeAndDeviceId(observationTime,deviceId2);
                    for (Map<String,Object> map : mapList) {
                        if(Objects.equals(observationTime,map.get("observationTime"))&&Objects.equals(deviceId2,map.get("deviceId"))){
                            deviceList=deviceService.fetchDeviceData(deviceId2,null,"02");
                            return ResultTemplate.fail("该日："+observationTime+",此地："+deviceList.get(0).getDeviceName()+"已经存在！");
                        }
                    }
                    tbFlowList.add(tbFlow2);
                }

            }
            // Save the file
            ExcelParserUtils.saveFile(maps.get("savePath"), maps.get("fileName"), fileMul);
        } catch (IOException e) {
            return ResultTemplate.fail(e.getMessage());
        }

        // Database insertion
        boolean flag = tbFlowService.saveBatch(tbFlowList);
        if (flag) {
            return ResultTemplate.success("运行成功，文件已保存！");
        }
        return ResultTemplate.fail("数据入库时出错！");
    }
    /*
     * @param jsonArray
     * 根据日期分页模糊查询
     * 径流
     * */
    @PreAuthorize("hasAnyAuthority('api_tbFlow_pageQurey')")
    @PostMapping(value = "/api/tbFlow/pageQurey")
    public ResultTemplate<Object> pageQurey(@RequestBody JSONObject jsonObject) {
        int currentPage = jsonObject.getInt("currentPage");
        int pageSize = jsonObject.getInt("pageSize");
        if(currentPage < 1){
            return ResultTemplate.fail("无效页码！");
        }else if(pageSize < 1){
            return ResultTemplate.fail("无效单位！");
        }
        TbFlow tbFlowClass = JSONUtil.toBean(jsonObject, com.ydsw.domain.TbFlow.class);

        IPage<Map<String, Object>> page =tbFlowService.fetchDataByObservationTimeAndFilepath(currentPage,pageSize,tbFlowClass);
        List<Map<String, Object>> records = page.getRecords();
        for (int i = 0; i < records.size(); i++) {
            Map<String, Object> record = records.get(i);            //deviceID转换
            String deviceId = record.get("deviceId").toString();
            if(deviceId.length()!=4)
            {
                List<Device> deviceList = deviceService.fetchDeviceData(Integer.parseInt(deviceId), "", "02");
                record.put("deviceId", deviceList.get(0).getDeviceName());
            }
            AtmosphereController.renameFilepath(record);

        }
        return ResultTemplate.success(page);
    }

    /*
     * 根据id删除和查询条件删除，若id列表存在，只根据id列表删除，否则，根据条件删除，必须存在至少一个删除条件
     * */
    @PreAuthorize("hasAnyAuthority('api_tbFlow_DelByIds')")
    @RequestMapping(value = "/api/tbFlow/DelByIds")
    @Transactional
    public ResultTemplate<Object> SpeDelById(@RequestBody JSONObject jsonObject) {
        List<String> idArray = jsonObject.getBeanList("ids", String.class);//id列表
        String filepath = jsonObject.getStr("filepath");
        String dateSelected = jsonObject.getStr("observationTime");
        String deviceId = jsonObject.getStr("deviceId");

        if (idArray != null && idArray.isEmpty()) {
            //根据条件删除
             if ( deviceId==null&&(filepath == null || filepath.trim().isEmpty() )&& (dateSelected == null || "".equals(dateSelected.trim()))) {
                        return ResultTemplate.fail("无效参数！");
                    }
            tbFlowService.delByIdList(new ArrayList<>(), dateSelected, filepath,deviceId);
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
            tbFlowService.delByIdList(idList, dateSelected, filepath,deviceId);
        } catch (Exception e) {
            return ResultTemplate.fail("删除时出错！");
        }
        return ResultTemplate.success("删除成功！");
    }

}

