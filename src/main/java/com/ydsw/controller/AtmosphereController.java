package com.ydsw.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fengwenyi.api.result.ResultTemplate;
import com.ydsw.domain.Atmosphere;
import com.ydsw.domain.Device;
import com.ydsw.service.AtmosphereService;
import com.ydsw.service.DeviceService;
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
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RestController
public class AtmosphereController {
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private AtmosphereService atmosphereService;
    //根据时间、设备号查询气象数据
    @PreAuthorize("hasAnyAuthority('api_atmosphere')")
    @PostMapping(value = "/api/atmosphere")
    public ResultTemplate<Object> getAtmosphereByObservationTime(
            @RequestBody Map<String, String> requestBody) {
        String time = requestBody.get("time");
        String device = requestBody.get("device");
        List<Atmosphere> atmosphereList = atmosphereService.fetchDataByObservationTimeAndDevice(time, device);
        // 创建一个列表来存储结果
        List<Map<String, Object>> responseList = new ArrayList<>();
        // 根据 time 字段的后两位进行排序
        atmosphereList.sort((a1, a2) -> {
            String time1 = a1.getObservationTime();
            String time2 = a2.getObservationTime();
            int lastTwo1 = (time1 != null && time1.length() >= 2) ? Integer.parseInt(time1.substring(time1.length() - 2)) : 0;
            int lastTwo2 = (time2 != null && time2.length() >= 2) ? Integer.parseInt(time2.substring(time2.length() - 2)) : 0;
            return Integer.compare(lastTwo1, lastTwo2);
        });
        // 遍历每个 Atmosphere 对象并构建返回的 Map
        for (Atmosphere atmosphere : atmosphereList) {
            Map<String, Object> atmosphereMap = new HashMap<>();
            atmosphereMap.put("atmosphere", atmosphere);
            String timeValue = atmosphere.getObservationTime();
            int lastTwoDigits = (timeValue != null && timeValue.length() >= 2)
                    ? Integer.parseInt(timeValue.substring(timeValue.length() - 2))
                    : 0;
            atmosphereMap.put("lastTwoDigits", lastTwoDigits);
            responseList.add(atmosphereMap);
        }
        return ResultTemplate.success(responseList);
    }
    /*
     * 按年份和设备号查询气象平均数据
     * */
    @PreAuthorize("hasAnyAuthority('api_atmosphere_selectByYearAndDevice')")
    @PostMapping(value = "/api/atmosphere/selectByYearAndDevice")
    public ResultTemplate<Object> selectByYearAndDevice(@RequestBody Map<String, String> requestBody) {
        String observationTime = requestBody.get("observationTime");
        String deviceId = requestBody.get("deviceId");
        if(observationTime.length()!=4)
        {
            return ResultTemplate.fail("年查询格式错误！");
        }
        if(deviceId.isEmpty())
        {
            return ResultTemplate.fail("站点不可为空！");
        }
        double windSpeed=0.0;
        double rainfall=0.0;
        double atmosphereTemperature=0.0;
        double soilTemperature=0.0;
        double digitalPressure=0.0;
        double simpleTotalRadiation=0.0;
        double windDirection=0.0;
        double soilHumidity=0.0;
        double atmosphereHumidity=0.0;
        double pm25=0.0;
        double salinity=0.0;
        double negativeOxygenIon=0.0;
        double rainfallAccumulation=0.0;
        double radiationAccumulation=0.0;
        double pm10=0.0;

        List<Atmosphere> atmosphereList = atmosphereService.fetchDataByObservationTimeAndDevice(observationTime, deviceId);
        // 创建一个列表来存储结果
        Map<String, Object> atmosphereMap = new HashMap<>();
        calculAveForAto(atmosphereList,atmosphereMap,windSpeed,rainfall,atmosphereTemperature,soilTemperature,digitalPressure,simpleTotalRadiation,windDirection,soilHumidity,atmosphereHumidity,pm25,salinity,negativeOxygenIon,rainfallAccumulation,radiationAccumulation,pm10);
        atmosphereMap.put("year",observationTime);
        return ResultTemplate.success(atmosphereMap);
    }
    /*
    * 按月份和设备号查询气象平均数据
    * */
    @PreAuthorize("hasAnyAuthority('api_atmosphere_selectByMonthAndDevice')")
    @PostMapping(value = "/api/atmosphere/selectByMonthAndDevice")
    public ResultTemplate<Object> selectByMonthAndDevice(@RequestBody Map<String, String> requestBody) {
        String observationTime = requestBody.get("observationTime");
        String deviceId = requestBody.get("deviceId");
        if(observationTime.length()!=7)
        {
            return ResultTemplate.fail("月查询格式错误！");
        }
        if(deviceId.isEmpty())
        {
            return ResultTemplate.fail("站点不可为空！");
        }
        double windSpeed=0.0;
        double rainfall=0.0;
        double atmosphereTemperature=0.0;
        double soilTemperature=0.0;
        double digitalPressure=0.0;
        double simpleTotalRadiation=0.0;
        double windDirection=0.0;
        double soilHumidity=0.0;
        double atmosphereHumidity=0.0;
        double pm25=0.0;
        double salinity=0.0;
        double negativeOxygenIon=0.0;
        double rainfallAccumulation=0.0;
        double radiationAccumulation=0.0;
        double pm10=0.0;

        List<Atmosphere> atmosphereList = atmosphereService.fetchDataByObservationTimeAndDevice(observationTime, deviceId);
        // 创建一个列表来存储结果
        Map<String, Object> atmosphereMap = new HashMap<>();
        calculAveForAto(atmosphereList,atmosphereMap,windSpeed,rainfall,atmosphereTemperature,soilTemperature,digitalPressure,simpleTotalRadiation,windDirection,soilHumidity,atmosphereHumidity,pm25,salinity,negativeOxygenIon,rainfallAccumulation,radiationAccumulation,pm10);
        atmosphereMap.put("month",observationTime);
        return ResultTemplate.success(atmosphereMap);
    }
    /*-
    * 求气象数据总量和平均值
    * */
    private void calculAveForAto(List<Atmosphere> atmosphereList,Map<String, Object> atmosphereMap,
                                 double windSpeed,double rainfall,
                                 double atmosphereTemperature,double soilTemperature,double digitalPressure,double simpleTotalRadiation,
                                 double windDirection,double soilHumidity,double atmosphereHumidity,double pm25,
                                 double salinity,double negativeOxygenIon,double rainfallAccumulation,double radiationAccumulation,double pm10){
        int sum=0;
        for (Atmosphere atmosphere : atmosphereList) {
            windSpeed+=Double.parseDouble(atmosphere.getWindSpeed());
            rainfall+=Double.parseDouble(atmosphere.getRainfall());
            atmosphereTemperature+=Double.parseDouble(atmosphere.getAtmosphereTemperature());
            soilTemperature+=Double.parseDouble(atmosphere.getSoilTemperature());
            digitalPressure+=Double.parseDouble(atmosphere.getDigitalPressure());
            simpleTotalRadiation+=Double.parseDouble(atmosphere.getSimpleTotalRadiation());
            windDirection+=Double.parseDouble(atmosphere.getWindDirection());
            soilHumidity+=Double.parseDouble(atmosphere.getSoilHumidity());
            atmosphereHumidity+=Double.parseDouble(atmosphere.getAtmosphereHumidity());
            pm25+=Double.parseDouble(atmosphere.getPm25());
            salinity+=Double.parseDouble(atmosphere.getSalinity());
            negativeOxygenIon+=Double.parseDouble(atmosphere.getNegativeOxygenIon());
            rainfallAccumulation+=Double.parseDouble(atmosphere.getRainfall());
            radiationAccumulation+=Double.parseDouble(atmosphere.getRadiationAccumulation());
            pm10+=Double.parseDouble(atmosphere.getPm10());
            sum++;
        }
        windSpeed=windSpeed/(double)sum;
        rainfall=rainfall/(double)sum;
        atmosphereTemperature=atmosphereTemperature/(double)sum;
        soilTemperature=soilTemperature/(double)sum;
        digitalPressure=digitalPressure/(double)sum;
        simpleTotalRadiation=simpleTotalRadiation/(double)sum;
        windDirection=windDirection/(double)sum;
        soilHumidity=soilHumidity/(double)sum;
        atmosphereHumidity=atmosphereHumidity/(double)sum;
        pm25=pm25/(double)sum;
        salinity=salinity/(double)sum;
        negativeOxygenIon=negativeOxygenIon/(double)sum;
        rainfallAccumulation=rainfallAccumulation/(double)sum;
        radiationAccumulation=radiationAccumulation/(double)sum;
        pm10=pm10/(double)sum;
        atmosphereMap.put("windSpeed", String.format("%.2f",windSpeed));
        atmosphereMap.put("rainfall", String.format("%.2f",rainfall));
        atmosphereMap.put("atmosphereTemperature", String.format("%.2f",atmosphereTemperature));
        atmosphereMap.put("soilTemperature", String.format("%.2f",soilTemperature));
        atmosphereMap.put("digitalPressure", String.format("%.2f",digitalPressure));
        atmosphereMap.put("simpleTotalRadiation", String.format("%.2f",simpleTotalRadiation));
        atmosphereMap.put("windDirection", String.format("%.2f",windDirection));
        atmosphereMap.put("soilHumidity", String.format("%.2f",soilHumidity));
        atmosphereMap.put("atmosphereHumidity", String.format("%.2f",atmosphereHumidity));
        atmosphereMap.put("pm25", String.format("%.2f",pm25));
        atmosphereMap.put("salinity", String.format("%.2f",salinity));
        atmosphereMap.put("negativeOxygenIon",String.format("%.2f",negativeOxygenIon));
        atmosphereMap.put("rainfallAccumulation", String.format("%.2f",rainfallAccumulation));
        atmosphereMap.put("radiationAccumulation", String.format("%.2f",radiationAccumulation));
        atmosphereMap.put("pm10", String.format("%.2f",pm10));
        atmosphereMap.put("sum", sum);
    }
    /**
     * 根据观测地点查询最近一条气象数据
     *
     * @param requestBody
     * @return
     */
    @PreAuthorize("hasAnyAuthority('api_findCurrentAtmosphereByDevice')")
    @PostMapping(value = "/api/findCurrentAtmosphereByDevice")
    public ResultTemplate<Object> findCurrentAtmosphereByDevice(
            @RequestBody Map<String, String> requestBody) {
        String device = requestBody.get("device");
        String time = requestBody.get("time");
        Atmosphere atmosphereList = atmosphereService.findCurrentAtmosphereByDevice(device,time);
        //log.info(atmosphereList.toString());
        //log.info("获取气象数据成功：observationTime={}",observationTime);
        return ResultTemplate.success(atmosphereList);
    }

    /*
     * @param jsonArray
     * 根据日期分页模糊查询
     * 气象
     * */
    @PreAuthorize("hasAnyAuthority('api_atmosphere_pageQurey')")
    @PostMapping(value = "/api/atmosphere/pageQurey")
    public ResultTemplate<Object> pageQurey(@RequestBody JSONObject jsonObject) {
        int currentPage = jsonObject.getInt("currentPage");
        int pageSize = jsonObject.getInt("pageSize");
        if (currentPage < 1) {
            return ResultTemplate.fail("无效页码！");
        } else if (pageSize < 1) {
            return ResultTemplate.fail("无效单位！");
        }
        Atmosphere atmosphereClass = JSONUtil.toBean(jsonObject, Atmosphere.class);
        IPage<Map<String, Object>> page = atmosphereService.fetchDataByObservationTimeAndFilepath(currentPage, pageSize, atmosphereClass);
        List<Map<String, Object>> records = page.getRecords();
        for (int i = 0; i < records.size(); i++) {
            Map<String, Object> record = records.get(i);            //deviceID转换
            String deviceId = record.get("deviceId").toString();
            if (deviceId.length() != 4) {
                List<Device> deviceList = deviceService.fetchDeviceData(Integer.parseInt(deviceId), "", "03");
                record.put("deviceId", deviceList.get(0).getDeviceName());
            }
            renameFilepath(record);
        }
        return ResultTemplate.success(page);
    }
    /*
    * 文件绝对路径反演文件名
    * */
    static void renameFilepath(Map<String, Object> record) {
        String filepathObj = (String) record.get("filepath");
        if (filepathObj == null || "".equals(filepathObj.trim())) {
            return;
        }
        String sep = FileSystems.getDefault().getSeparator();//分隔符
        String[] filepathList = new String[0];
        if(filepathObj.contains(sep)) {
            filepathList = filepathObj.split(Pattern.quote(sep));
        }
        else if(filepathObj.contains("/")) {
            filepathList = filepathObj.split("/");
        }
        if(filepathList.length == 0) {
            return;
        }
        String[] filepathNameList = filepathList[filepathList.length - 1].split(Pattern.quote("."));
        int filepathNameBegin = 0;
        int filepathNameEnd = filepathNameList.length - 2;
        StringBuilder sb1 = new StringBuilder();
        for (int j = filepathNameBegin; j <= filepathNameEnd; j++) {
            sb1.append(filepathNameList[j]);
        }
        filepathNameList = sb1.toString().split("-");
        int begin = 2;
        int end = filepathNameList.length - 5;
        StringBuilder sb = new StringBuilder();
        for (int j = begin; j <= end; j++) {
            sb.append(filepathNameList[j]);
            if (j != end) {
                sb.append("-");
            }
        }
        String result = sb.toString();
        record.put("filepath", result);
    }

    /*
     * 根据id删除和查询条件删除，若id列表存在，只根据id列表删除，否则，根据条件删除，必须存在至少一个删除条件
     * */
    @PreAuthorize("hasAnyAuthority('api_atmosphere_DelByIds')")
    @RequestMapping(value = "/api/atmosphere/DelByIds")
    @Transactional
    public ResultTemplate<Object> SpeDelById(@RequestBody JSONObject jsonObject) {
        List<String> idArray = jsonObject.getBeanList("ids", String.class);//id列表
        String filepath = jsonObject.getStr("filepath");
        String dateSelected = jsonObject.getStr("observationTime");
        String deviceId = jsonObject.getStr("deviceId");
        if (idArray != null && idArray.isEmpty()) {
            if (deviceId == null && (filepath == null || filepath.trim().isEmpty()) && (dateSelected == null || "".equals(dateSelected.trim()))) {
                return ResultTemplate.fail("无效参数！");
            }//根据条件删除
            atmosphereService.delByIdList(new ArrayList<>(), dateSelected, filepath, deviceId);
            return ResultTemplate.success("删除成功！");
        }
        List<Integer> idList = new ArrayList<>();
        ModelFileStatusController.ArrayStrToInt(idArray,idList);
        try {
            atmosphereService.delByIdList(idList, dateSelected, filepath, deviceId);
        } catch (Exception e) {
            return ResultTemplate.fail("删除时出错！");
        }
        return ResultTemplate.success("删除成功！");
    }

    /**
     * 插入气象数据
     *
     * @param jsonArray 包含气象数据的JSON数组
     * @return 操作结果
     */
    @PreAuthorize("hasAnyAuthority('api_atmosphere_insert')")
    @PostMapping(value = "/api/atmosphere/insert")
    @Transactional
    public ResultTemplate<Object> atmosphereInsert(@RequestBody JSONArray jsonArray) {
        log.info(JSONUtil.toJsonStr(jsonArray)); // 假设JSONUtil是一个工具类，用于将对象转换为JSON字符串

        // 假设JSONUtil.toList方法可以将JSONArray转换为List<Atmosphere>
        List<Atmosphere> atmosphereList = JSONUtil.toList(jsonArray, Atmosphere.class);
        for (Atmosphere atmosphere : atmosphereList) {
            List<Atmosphere> atmosphereList1=atmosphereService.fetchDataByObservationTimeAndDevice(atmosphere.getObservationTime(), String.valueOf(atmosphere.getDeviceId()));
            for (Atmosphere atmosphere1 : atmosphereList1) {
                List<Device> deviceList=deviceService.fetchDeviceData(atmosphere1.getDeviceId(), "", "03");
                if(deviceList==null)
                {
                    return ResultTemplate.fail("不存在的站点！");
                }else if(deviceList.isEmpty())
                {
                    return ResultTemplate.fail("不存在的站点！");
                }
                if (atmosphere1.getDeviceId().equals(atmosphere.getDeviceId())||atmosphere1.getObservationTime().equals(atmosphere.getObservationTime())) {
                    return ResultTemplate.fail(deviceList.get(0).getDeviceName()+"站点"+atmosphere.getObservationTime()+"的数据已经存在！");
                }
            }
            atmosphere.setCreateTime(new Date());
            atmosphere.setType("form");
            atmosphere.setStatus(0);
        }
        // 调用服务层保存数据
        Boolean flag = atmosphereService.saveBatch(atmosphereList);

        // 根据操作结果返回相应信息
        if (flag) {
            return ResultTemplate.success();
        }
        return ResultTemplate.fail();
    }
    /*
    * 气象Excel提交接口
    * */
    @PreAuthorize("hasAnyAuthority('api_atmosphere_insertByExcel')")
    @PostMapping(value = "api/atmosphere/insertByExcel")
    @ResponseBody
    @Transactional
    public ResultTemplate<Object> atmosphere_insertByExcel(
            @RequestParam("createUserId") String userUid, @RequestParam("userName") String userName,
            @RequestParam("contactPhone") String contactPhone, @RequestParam("contactAddress") String contactAddress,
            @RequestParam("productionUnit") String productionUnit, @RequestParam("contactEmail") String contactEmail,
            @RequestParam("open") Integer open, @RequestParam("dataIntroduction") String dataIntroduction,
            @RequestParam MultipartFile fileMul) {

        if (userUid.isEmpty()) {
            return ResultTemplate.fail("获取用户id失败！");
        } else if (userName.isEmpty()) {
            return ResultTemplate.fail("用户名为空！");
        }

        List<Atmosphere> atmosphereList = new ArrayList<>();
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
            Map<String, String> maps = ExcelParserUtils.getFileNameAndPath(newName, fileType, "qixiang");
            String filePathName = maps.get("savePath") + maps.get("fileName");
            Map<String, String> sheetNames = ExcelParserUtils.getSheetNamesAndFirstCellOfExcel(inputStream, fileType);
            String firstCell = sheetNames.get("firstCell").substring(0, 17);


            String deviceName = firstCell.substring(0, firstCell.indexOf("气象"));
            if (deviceName.isEmpty()) {
                return ResultTemplate.fail("观测地点错误！");
            }
            JSONArray singleRCArray = new JSONArray();
            List list = new ArrayList();
            list.add(1);
            list.add(0);
            singleRCArray.add(list);

            inputStream = fileMul.getInputStream();
            JSONObject atmosphereData = ExcelParserUtils.parseExcelFile(inputStream, Atmosphere.class, 0, 2, 3, singleRCArray, fileType);
            JSONArray rows = atmosphereData.getJSONArray("classlist");

            atmosphereList = JSONUtil.toList(rows, Atmosphere.class);
            List<Device> deviceList = deviceService.fetchDeviceData(null, deviceName, "03");
            if(deviceList==null)
            {
                return ResultTemplate.fail("不存在的站点！");
            }else if(deviceList.isEmpty())
            {
                return ResultTemplate.fail("不存在的站点！");
            }
            Integer deviceId = deviceList.get(0).getId();

            for (Atmosphere atmosphere : atmosphereList) {
                List<Atmosphere> atmosphereList1=atmosphereService.fetchDataByObservationTimeAndDevice(atmosphere.getObservationTime(), String.valueOf(deviceId));
                for (Atmosphere atmosphere1 : atmosphereList1) {
                    if (atmosphere1.getDeviceId().equals(deviceId)||atmosphere1.getObservationTime().equals(atmosphere.getObservationTime())) {
                        return ResultTemplate.fail(deviceName+"站点"+atmosphere.getObservationTime()+"的数据已经存在！");
                    }
                }
                String regex = "[0-9]{4}-[0-9]{2}-[0-9]{2}";
                Pattern pattern = Pattern.compile(regex);
                Matcher m = pattern.matcher(atmosphere.getObservationTime());
                boolean dateFlag = m.matches();
                if (!dateFlag) {
                    return ResultTemplate.fail("请检查日期是否按模板填写！");
                }
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date date = formatter.parse(atmosphere.getObservationTime());
                } catch (Exception e) {
                    return ResultTemplate.fail("请检查日期是否按模板填写！");
                }
                atmosphere.atmosphereAdd(deviceId, new Date(), fileType, userName, filePathName,fileNameOutOfType, userUid,
                        0, contactPhone, contactAddress, productionUnit, contactEmail,
                        open, dataIntroduction);
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

        System.out.println("atmosphereList:"+atmosphereList);
//        boolean flag = atmosphereService.saveBatch(atmosphereList);
//        if (flag) {
//            return ResultTemplate.success("运行成功，文件已保存！");
//        }
        return ResultTemplate.fail("数据入库时出错！");
    }
    /*
    * 获取文件的路径和id
    * */
    @PreAuthorize("hasAnyAuthority('api_all_getFilepath')")
    @PostMapping(value = "/api/all/getFilepath")
    public ResultTemplate<Object> getFilepath(@RequestBody JSONObject jsonObject)
    {
        int currentPage = jsonObject.get("currentPage") == null ? 1 : jsonObject.getInt("currentPage");
        int pageSize = jsonObject.get("pageSize") == null ? 10 : jsonObject.getInt("pageSize");

        String className = String.valueOf(jsonObject.get("className"));
        if(Objects.equals(className, "03"))
        {
            className="atmosphere";
        } else if (Objects.equals(className, "02")) {
            className="tb_water_level";
        }
        else if (Objects.equals(className, "04")) {
            className="spectral_reflectance";
        }
        else if (Objects.equals(className, "05")) {
            className="water_physicochemistry";
        }
        else if (Objects.equals(className, "01")) {
            className="tb_flow";
        }
        else if (Objects.equals(className, "06")) {
            className="satelite_remote_sensing";
        }
        else if (Objects.equals(className, "07")) {
            className="drone_image";
        }else {
            return ResultTemplate.fail("数据类型错误！");
        }
        List<String> idArray = jsonObject.getBeanList("ids", String.class);
        List<Integer> idList = new ArrayList<>();
        ModelFileStatusController.ArrayStrToInt(idArray,idList);
        String filepath= "";
        if (jsonObject.get("filepath") != null) {
            filepath=String.valueOf(jsonObject.get("filepath"));
        }
        String observationTimeBegin = "";
        if (jsonObject.get("observationTimeBegin")!=null) {
            observationTimeBegin = String.valueOf(jsonObject.get("observationTimeBegin"));
        }
        String observationTimeEnd = "";
        if (jsonObject.get("observationTimeEnd")!=null) {
            observationTimeEnd =String.valueOf(jsonObject.get("observationTimeEnd"));
        }
        String type = "";
        if (jsonObject.get("type")!=null) {
            type = String.valueOf(jsonObject.get("type"));
            if(Objects.equals(type, "form"))
            {
                type="";
            }
        }
        String deviceId = "";
        if (jsonObject.get("deviceId")!=null) {
            deviceId = String.valueOf(jsonObject.get("deviceId"));
        }
        String deviceName = "";
        if (jsonObject.get("deviceName")!=null) {
            deviceName =String.valueOf(jsonObject.get("deviceName"));
        }
        String typeDetail = "";
        if (jsonObject.get("typeDetail")!=null) {
            typeDetail =String.valueOf(jsonObject.get("typeDetail"));
        }
        IPage<Map<String,Object>> data=atmosphereService.fetchFilepathByObservationTimeAndClassName(currentPage,pageSize,idList,observationTimeBegin,observationTimeEnd,className,filepath,type,typeDetail,deviceId,deviceName);
        for (Map<String,Object> map : data.getRecords()) {
            if(!map.containsKey("filepath"))
            {
                continue;
            }
            filepath=map.get("filepath").toString();
            map.put("filepathLink",filepath.replaceAll(".*Trash",""));
            filepath=map.get("filepath").toString();
            map.put("fileType",filepath.substring(filepath.lastIndexOf("."),filepath.length()));
            map.remove("filepath");

            map.put("observationPeriod", map.get("observationTimeBegin").toString()+"至"+map.get("observationTimeEnd").toString());
            map.remove("observationTimeBegin");
            map.remove("observationTimeEnd");
        }
        List<Map<String,Object>> res=new ArrayList<>();

        return ResultTemplate.success(data);

    }
    /*
    * 文件下载：相对路径下载
    * */
    @PreAuthorize("hasAnyAuthority('api_fileDownload')")
    @PostMapping(value = "api/fileDownload")
    public ResultTemplate<Object> modelDownload(@RequestBody Map<String, String> requestBody, HttpServletResponse response) throws IOException {
        String relativePath = requestBody.get("relativePath");
        if (relativePath == null) {
            return ResultTemplate.fail("非法路径!");
        }
        if (relativePath.isEmpty()) {
            return ResultTemplate.fail("空请求！");
        }
        String fileName="";
        String[] filepaths = relativePath.split(",");
        InputStream[] inputStreams = new InputStream[filepaths.length];
        ServletOutputStream outputStream=null;
        String p = Paths.get("").toAbsolutePath().toString();
        String sep = FileSystems.getDefault().getSeparator();
        // 如果分隔符是反斜杠，则需要进行转义
        String[] paths = p.split(Pattern.quote(sep));
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < paths.length - 1; j++) {
            sb.append(paths[j]);
            sb.append(sep);
        }
        sb.append("Trash");
        if(filepaths.length==1)
        {
            try {
                String[] split = filepaths[0].split("/");
                filepaths[0] = sb + filepaths[0];
                fileName = split[split.length - 1];
                String fileType = fileName.substring(fileName.lastIndexOf("."));
                File uploadFile = new File(filepaths[0]);
                Map<String, Object> map = new HashMap<>();
                map.put("filepath", fileName);
                renameFilepath(map);
                fileName = map.get("filepath").toString();
                outputStream = response.getOutputStream();
                response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName + fileType, StandardCharsets.UTF_8));
                response.setContentType("application/octet-stream");
                outputStream.write(FileUtil.readBytes(uploadFile));
                outputStream.flush();
                outputStream.close();
            } catch (IOException | IORuntimeException e) {
                return ResultTemplate.fail("文件"+fileName+"获取失败!");
            }
            return null;
        }
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        String time= sdf1.format(new Date());
        String zipName=time +".zip";
        String[] fileNames= new String[filepaths.length];
        List<Map<String, String>> mapList = new ArrayList<>();
        for (int i = 0; i < filepaths.length; i++) {
            try {
                String[] split = filepaths[i].split("/");
                filepaths[i] = sb + filepaths[i];
                fileName = split[split.length - 1];
                String fileType = fileName.substring(fileName.lastIndexOf("."));
                File uploadFile = new File(filepaths[i]);
                inputStreams[i] = Files.newInputStream(uploadFile.toPath());
                Map<String, Object> map = new HashMap<>();
                map.put("filepath", fileName);
                renameFilepath(map);
                fileName = map.get("filepath").toString()+fileType;
                fileNames[i] = fileName;


            } catch ( IORuntimeException e) {
                return ResultTemplate.fail("文件"+fileName+"获取失败!");
            }
        }

        outputStream = response.getOutputStream();
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(zipName, StandardCharsets.UTF_8));
        response.setContentType("application/octet-stream");
        ZipUtil.zip(outputStream,fileNames,inputStreams);
        outputStream.flush();
        outputStream.close();
        return null;
    }


    /*
    * 文件下载：根据搜索条件下载
    * */
    @PreAuthorize("hasAnyAuthority('api_fileDownloadById')")
    @PostMapping(value = "api/fileDownloadById")
    public ResultTemplate<Object> modelDownloadByElse(@RequestBody JSONObject jsonObject, HttpServletResponse response) throws IOException {
        int currentPage = jsonObject.get("currentPage") == null ? 1 : jsonObject.getInt("currentPage");
        int pageSize = jsonObject.get("pageSize") == null ? 10 : jsonObject.getInt("pageSize");

        String className = String.valueOf(jsonObject.get("className"));
        if(Objects.equals(className, "03"))
        {
            className="atmosphere";
        } else if (Objects.equals(className, "02")) {
            className="tb_water_level";
        }
        else if (Objects.equals(className, "04")) {
            className="spectral_reflectance";
        }
        else if (Objects.equals(className, "05")) {
            className="water_physicochemistry";
        }
        else if (Objects.equals(className, "01")) {
            className="tb_flow";
        }
        else if (Objects.equals(className, "06")) {
            className="satelite_remote_sensing";
        }
        else if (Objects.equals(className, "07")) {
            className="drone_image";
        }else {
            return ResultTemplate.fail("数据类型错误！");
        }
        List<String> idArray = jsonObject.getBeanList("ids", String.class);
        List<Integer> idList = new ArrayList<>();
        ModelFileStatusController.ArrayStrToInt(idArray,idList);
        String filepath= "";
        if (jsonObject.get("filepath") != null) {
            filepath=String.valueOf(jsonObject.get("filepath"));
        }
        String observationTimeBegin = "";
        if (jsonObject.get("observationTimeBegin")!=null) {
            observationTimeBegin = String.valueOf(jsonObject.get("observationTimeBegin"));
        }
        String observationTimeEnd = "";
        if (jsonObject.get("observationTimeEnd")!=null) {
            observationTimeEnd =String.valueOf(jsonObject.get("observationTimeEnd"));
        }
        String type = "";
        if (jsonObject.get("type")!=null) {
            type = String.valueOf(jsonObject.get("type"));
            if(Objects.equals(type, "form"))
            {
                type="";
            }
        }
        String deviceId = "";
        if (jsonObject.get("deviceId")!=null) {
            deviceId = String.valueOf(jsonObject.get("deviceId"));
        }
        String deviceName = "";
        if (jsonObject.get("deviceName")!=null) {
            deviceName =String.valueOf(jsonObject.get("deviceName"));
        }
        String typeDetail = "";
        if (jsonObject.get("typeDetail")!=null) {
            typeDetail =String.valueOf(jsonObject.get("typeDetail"));
        }
        IPage<Map<String,Object>> data=atmosphereService.fetchFilepathByObservationTimeAndClassName(currentPage,pageSize,idList,observationTimeBegin,observationTimeEnd,className,filepath,type,typeDetail,deviceId,deviceName);
        if(data.getRecords().isEmpty())
        {
            return ResultTemplate.fail("数据文件已被清除！");
        }
        String[] filepaths=new String[data.getRecords().size()];
        for (int i = 0; i < data.getRecords().size(); i++) {
            Map<String,Object> map = data.getRecords().get(i);
            filepaths[i]=map.get("filepath").toString();
        }
        InputStream[] inputStreams = new InputStream[filepaths.length];
        ServletOutputStream outputStream=null;
        String fileName="";
        if(filepaths.length==1)
        {
            try {
                String[] split = filepaths[0].split("/");
                fileName = split[split.length - 1];
                String fileType = fileName.substring(fileName.lastIndexOf("."));
                File uploadFile = new File(filepaths[0]);
                Map<String, Object> map = new HashMap<>();
                map.put("filepath", fileName);
                renameFilepath(map);
                fileName = map.get("filepath").toString();
                outputStream = response.getOutputStream();
                response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName + fileType, StandardCharsets.UTF_8));
                response.setContentType("application/octet-stream");
                outputStream.write(FileUtil.readBytes(uploadFile));
                outputStream.flush();
                outputStream.close();
            } catch (IOException | IORuntimeException e) {
                return ResultTemplate.fail("文件"+fileName+"获取失败!");
            }
            return null;
        }
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        String time= sdf1.format(new Date());
        String zipName=time +".zip";
        String[] fileNames= new String[filepaths.length];
        List<Map<String, String>> mapList = new ArrayList<>();
        for (int i = 0; i < filepaths.length; i++) {
            try {
                String[] split = filepaths[i].split("/");
                fileName = split[split.length - 1];
                String fileType = fileName.substring(fileName.lastIndexOf("."));
                File uploadFile = new File(filepaths[i]);
                inputStreams[i] = Files.newInputStream(uploadFile.toPath());
                Map<String, Object> map = new HashMap<>();
                map.put("filepath", fileName);
                renameFilepath(map);
                fileName = map.get("filepath").toString()+fileType;
                fileNames[i] = fileName;


            } catch (IOException | IORuntimeException e) {
                return ResultTemplate.fail("文件"+fileName+"获取失败!");
            }
        }

        outputStream = response.getOutputStream();
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(zipName, StandardCharsets.UTF_8));
        response.setContentType("application/octet-stream");
        ZipUtil.zip(outputStream,fileNames,inputStreams);
        outputStream.flush();
        outputStream.close();
        return null;
    }
}




