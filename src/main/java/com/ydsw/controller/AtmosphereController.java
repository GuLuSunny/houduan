package com.ydsw.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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

        List<Atmosphere> atmosphereList = atmosphereService.fetchDataByObservationTimeAndDevice(observationTime, deviceId);
        // 创建一个列表来存储结果
        Map<String, Object> atmosphereMap = new HashMap<>();
        calculAveForAto(atmosphereList, atmosphereMap);
        atmosphereMap.put("year", observationTime);
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

        List<Atmosphere> atmosphereList = atmosphereService.fetchDataByObservationTimeAndDevice(observationTime, deviceId);
        // 创建一个列表来存储结果
        Map<String, Object> atmosphereMap = new HashMap<>();
        calculAveForAto(atmosphereList, atmosphereMap);
        atmosphereMap.put("month", observationTime);
        return ResultTemplate.success(atmosphereMap);
    }

    /*
     * 判断字符串是否为有效数值数据
     */
    private boolean isValidNumericValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        // 检查是否为"缺测"或类似标记
        if ("缺测".equals(value.trim()) || "null".equalsIgnoreCase(value.trim())) {
            return false;
        }
        try {
            // 尝试转换为Double，验证是否为有效数字
            Double.parseDouble(value.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /*
     * 计算气象数据平均值（改进版：每个字段单独统计有效数据）
     */
    private void calculAveForAto(List<Atmosphere> atmosphereList, Map<String, Object> atmosphereMap) {
        // 初始化各字段的累加值和计数
        double windSpeedSum = 0.0;
        int windSpeedCount = 0;

        double rainfallSum = 0.0;
        int rainfallCount = 0;

        double atmosphereTemperatureSum = 0.0;
        int atmosphereTemperatureCount = 0;

        double soilTemperatureSum = 0.0;
        int soilTemperatureCount = 0;

        double digitalPressureSum = 0.0;
        int digitalPressureCount = 0;

        double simpleTotalRadiationSum = 0.0;
        int simpleTotalRadiationCount = 0;

        double windDirectionSum = 0.0;
        int windDirectionCount = 0;

        double soilHumiditySum = 0.0;
        int soilHumidityCount = 0;

        double atmosphereHumiditySum = 0.0;
        int atmosphereHumidityCount = 0;

        double pm25Sum = 0.0;
        int pm25Count = 0;

        double salinitySum = 0.0;
        int salinityCount = 0;

        double negativeOxygenIonSum = 0.0;
        int negativeOxygenIonCount = 0;

        double rainfallAccumulationSum = 0.0;
        int rainfallAccumulationCount = 0;

        double radiationAccumulationSum = 0.0;
        int radiationAccumulationCount = 0;

        double pm10Sum = 0.0;
        int pm10Count = 0;

        // 新增字段
        double relativeHumiditySum = 0.0;
        int relativeHumidityCount = 0;

        double aqiIndexSum = 0.0;
        int aqiIndexCount = 0;

        double sulfurDioxideSum = 0.0;
        int sulfurDioxideCount = 0;

        double nitrogenDioxideSum = 0.0;
        int nitrogenDioxideCount = 0;

        double carbonMonoxideSum = 0.0;
        int carbonMonoxideCount = 0;

        double ozoneSum = 0.0;
        int ozoneCount = 0;

        double ozone8HourSum = 0.0;
        int ozone8HourCount = 0;

        // 遍历数据列表
        for (Atmosphere atmosphere : atmosphereList) {
            // 处理每个字段，只累加有效数据
            if (isValidNumericValue(atmosphere.getWindSpeed())) {
                windSpeedSum += Double.parseDouble(atmosphere.getWindSpeed());
                windSpeedCount++;
            }

            if (isValidNumericValue(atmosphere.getRainfall())) {
                rainfallSum += Double.parseDouble(atmosphere.getRainfall());
                rainfallCount++;
            }

            if (isValidNumericValue(atmosphere.getAtmosphereTemperature())) {
                atmosphereTemperatureSum += Double.parseDouble(atmosphere.getAtmosphereTemperature());
                atmosphereTemperatureCount++;
            }

            if (isValidNumericValue(atmosphere.getSoilTemperature())) {
                soilTemperatureSum += Double.parseDouble(atmosphere.getSoilTemperature());
                soilTemperatureCount++;
            }

            if (isValidNumericValue(atmosphere.getDigitalPressure())) {
                digitalPressureSum += Double.parseDouble(atmosphere.getDigitalPressure());
                digitalPressureCount++;
            }

            if (isValidNumericValue(atmosphere.getSimpleTotalRadiation())) {
                simpleTotalRadiationSum += Double.parseDouble(atmosphere.getSimpleTotalRadiation());
                simpleTotalRadiationCount++;
            }

            if (isValidNumericValue(atmosphere.getWindDirection())) {
                windDirectionSum += Double.parseDouble(atmosphere.getWindDirection());
                windDirectionCount++;
            }

            if (isValidNumericValue(atmosphere.getSoilHumidity())) {
                soilHumiditySum += Double.parseDouble(atmosphere.getSoilHumidity());
                soilHumidityCount++;
            }

            if (isValidNumericValue(atmosphere.getAtmosphereHumidity())) {
                atmosphereHumiditySum += Double.parseDouble(atmosphere.getAtmosphereHumidity());
                atmosphereHumidityCount++;
            }

            if (isValidNumericValue(atmosphere.getPm25())) {
                pm25Sum += Double.parseDouble(atmosphere.getPm25());
                pm25Count++;
            }

            if (isValidNumericValue(atmosphere.getSalinity())) {
                salinitySum += Double.parseDouble(atmosphere.getSalinity());
                salinityCount++;
            }

            if (isValidNumericValue(atmosphere.getNegativeOxygenIon())) {
                negativeOxygenIonSum += Double.parseDouble(atmosphere.getNegativeOxygenIon());
                negativeOxygenIonCount++;
            }

            if (isValidNumericValue(atmosphere.getRainfallAccumulation())) {
                rainfallAccumulationSum += Double.parseDouble(atmosphere.getRainfallAccumulation());
                rainfallAccumulationCount++;
            }

            if (isValidNumericValue(atmosphere.getRadiationAccumulation())) {
                radiationAccumulationSum += Double.parseDouble(atmosphere.getRadiationAccumulation());
                radiationAccumulationCount++;
            }

            if (isValidNumericValue(atmosphere.getPm10())) {
                pm10Sum += Double.parseDouble(atmosphere.getPm10());
                pm10Count++;
            }

            // 新增字段处理
            if (isValidNumericValue(atmosphere.getRelativeHumidity())) {
                relativeHumiditySum += Double.parseDouble(atmosphere.getRelativeHumidity());
                relativeHumidityCount++;
            }

            if (isValidNumericValue(atmosphere.getAqiIndex())) {
                aqiIndexSum += Double.parseDouble(atmosphere.getAqiIndex());
                aqiIndexCount++;
            }

            if (isValidNumericValue(atmosphere.getSulfurDioxide())) {
                sulfurDioxideSum += Double.parseDouble(atmosphere.getSulfurDioxide());
                sulfurDioxideCount++;
            }

            if (isValidNumericValue(atmosphere.getNitrogenDioxide())) {
                nitrogenDioxideSum += Double.parseDouble(atmosphere.getNitrogenDioxide());
                nitrogenDioxideCount++;
            }

            if (isValidNumericValue(atmosphere.getCarbonMonoxide())) {
                carbonMonoxideSum += Double.parseDouble(atmosphere.getCarbonMonoxide());
                carbonMonoxideCount++;
            }

            if (isValidNumericValue(atmosphere.getOzone())) {
                ozoneSum += Double.parseDouble(atmosphere.getOzone());
                ozoneCount++;
            }

            if (isValidNumericValue(atmosphere.getOzone8Hour())) {
                ozone8HourSum += Double.parseDouble(atmosphere.getOzone8Hour());
                ozone8HourCount++;
            }
        }

        // 计算各字段平均值（避免除零错误）
        atmosphereMap.put("windSpeed", windSpeedCount > 0 ? String.format("%.2f", windSpeedSum / windSpeedCount) : "0.00");
        atmosphereMap.put("rainfall", rainfallCount > 0 ? String.format("%.2f", rainfallSum / rainfallCount) : "0.00");
        atmosphereMap.put("atmosphereTemperature", atmosphereTemperatureCount > 0 ? String.format("%.2f", atmosphereTemperatureSum / atmosphereTemperatureCount) : "0.00");
        atmosphereMap.put("soilTemperature", soilTemperatureCount > 0 ? String.format("%.2f", soilTemperatureSum / soilTemperatureCount) : "0.00");
        atmosphereMap.put("digitalPressure", digitalPressureCount > 0 ? String.format("%.2f", digitalPressureSum / digitalPressureCount) : "0.00");
        atmosphereMap.put("simpleTotalRadiation", simpleTotalRadiationCount > 0 ? String.format("%.2f", simpleTotalRadiationSum / simpleTotalRadiationCount) : "0.00");
        atmosphereMap.put("windDirection", windDirectionCount > 0 ? String.format("%.2f", windDirectionSum / windDirectionCount) : "0.00");
        atmosphereMap.put("soilHumidity", soilHumidityCount > 0 ? String.format("%.2f", soilHumiditySum / soilHumidityCount) : "0.00");
        atmosphereMap.put("atmosphereHumidity", atmosphereHumidityCount > 0 ? String.format("%.2f", atmosphereHumiditySum / atmosphereHumidityCount) : "0.00");
        atmosphereMap.put("pm25", pm25Count > 0 ? String.format("%.2f", pm25Sum / pm25Count) : "0.00");
        atmosphereMap.put("salinity", salinityCount > 0 ? String.format("%.2f", salinitySum / salinityCount) : "0.00");
        atmosphereMap.put("negativeOxygenIon", negativeOxygenIonCount > 0 ? String.format("%.2f", negativeOxygenIonSum / negativeOxygenIonCount) : "0.00");
        atmosphereMap.put("rainfallAccumulation", rainfallAccumulationCount > 0 ? String.format("%.2f", rainfallAccumulationSum / rainfallAccumulationCount) : "0.00");
        atmosphereMap.put("radiationAccumulation", radiationAccumulationCount > 0 ? String.format("%.2f", radiationAccumulationSum / radiationAccumulationCount) : "0.00");
        atmosphereMap.put("pm10", pm10Count > 0 ? String.format("%.2f", pm10Sum / pm10Count) : "0.00");

        // 新增字段平均值
        atmosphereMap.put("relativeHumidity", relativeHumidityCount > 0 ? String.format("%.2f", relativeHumiditySum / relativeHumidityCount) : "0.00");
        atmosphereMap.put("aqiIndex", aqiIndexCount > 0 ? String.format("%.2f", aqiIndexSum / aqiIndexCount) : "0.00");
        atmosphereMap.put("sulfurDioxide", sulfurDioxideCount > 0 ? String.format("%.2f", sulfurDioxideSum / sulfurDioxideCount) : "0.00");
        atmosphereMap.put("nitrogenDioxide", nitrogenDioxideCount > 0 ? String.format("%.2f", nitrogenDioxideSum / nitrogenDioxideCount) : "0.00");
        atmosphereMap.put("carbonMonoxide", carbonMonoxideCount > 0 ? String.format("%.2f", carbonMonoxideSum / carbonMonoxideCount) : "0.00");
        atmosphereMap.put("ozone", ozoneCount > 0 ? String.format("%.2f", ozoneSum / ozoneCount) : "0.00");
        atmosphereMap.put("ozone8Hour", ozone8HourCount > 0 ? String.format("%.2f", ozone8HourSum / ozone8HourCount) : "0.00");

        // 字符串字段直接取最新值（如果有的话）
        if (!atmosphereList.isEmpty()) {
            Atmosphere latestData = atmosphereList.get(0);
            atmosphereMap.put("primaryPollutant", latestData.getPrimaryPollutant() != null ? latestData.getPrimaryPollutant() : "");
            atmosphereMap.put("airQualityLevel", latestData.getAirQualityLevel() != null ? latestData.getAirQualityLevel() : "");
        } else {
            atmosphereMap.put("primaryPollutant", "");
            atmosphereMap.put("airQualityLevel", "");
        }

        // 各字段的有效数据数量
        Map<String, Integer> validCounts = new HashMap<>();
        validCounts.put("windSpeed", windSpeedCount);
        validCounts.put("rainfall", rainfallCount);
        validCounts.put("atmosphereTemperature", atmosphereTemperatureCount);
        validCounts.put("soilTemperature", soilTemperatureCount);
        validCounts.put("digitalPressure", digitalPressureCount);
        validCounts.put("simpleTotalRadiation", simpleTotalRadiationCount);
        validCounts.put("windDirection", windDirectionCount);
        validCounts.put("soilHumidity", soilHumidityCount);
        validCounts.put("atmosphereHumidity", atmosphereHumidityCount);
        validCounts.put("pm25", pm25Count);
        validCounts.put("salinity", salinityCount);
        validCounts.put("negativeOxygenIon", negativeOxygenIonCount);
        validCounts.put("rainfallAccumulation", rainfallAccumulationCount);
        validCounts.put("radiationAccumulation", radiationAccumulationCount);
        validCounts.put("pm10", pm10Count);
        validCounts.put("relativeHumidity", relativeHumidityCount);
        validCounts.put("aqiIndex", aqiIndexCount);
        validCounts.put("sulfurDioxide", sulfurDioxideCount);
        validCounts.put("nitrogenDioxide", nitrogenDioxideCount);
        validCounts.put("carbonMonoxide", carbonMonoxideCount);
        validCounts.put("ozone", ozoneCount);
        validCounts.put("ozone8Hour", ozone8HourCount);

        atmosphereMap.put("validCounts", validCounts);
        atmosphereMap.put("totalRecords", atmosphereList.size());
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
    @PostMapping(value = "/api/atmosphere/insertOnly")
    @Transactional
    public ResultTemplate<Object> atmosphereInsertOnly(@RequestBody JSONObject jsonObject) {
        Atmosphere atmosphere = JSONUtil.toBean(jsonObject, Atmosphere.class);

        List<Device> deviceList=deviceService.fetchDeviceData(atmosphere.getDeviceId(), "", "03");
        if(deviceList==null)
        {
            return ResultTemplate.fail("不存在的站点！");
        }else if(deviceList.isEmpty())
        {
            return ResultTemplate.fail("不存在的站点！");
        }
//        if (atmosphere.getDeviceId().equals(atmosphere.getDeviceId())||atmosphere.getObservationTime().equals(atmosphere.getObservationTime())) {
//            return ResultTemplate.fail(deviceList.get(0).getDeviceName()+"站点"+atmosphere.getObservationTime()+"的数据已经存在！");
//        }
         atmosphere.setCreateTime(new Date());
         atmosphere.setType("form");
         atmosphere.setStatus(0);

        // 调用服务层保存数据
        Boolean flag = atmosphereService.save(atmosphere);

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
    @PostMapping(value = "/api/atmosphere/updateOnly")
    public ResultTemplate<Object> updateOnly(@RequestBody JSONObject jsonObject){
        Atmosphere atmosphere = jsonObject.toBean(Atmosphere.class);
        List<String> idArray = jsonObject.getBeanList("ids", String.class);
        List<Integer> idList = new ArrayList<>();
        ModelFileStatusController.ArrayStrToInt(idArray,idList);
        LambdaUpdateWrapper<Atmosphere> wrapper = new LambdaUpdateWrapper<>();
        // 检查 idList 是否为空
        if (!idList.isEmpty()) {
            wrapper.in(Atmosphere::getId, idList);
        }

        if (StringUtils.isNotBlank(atmosphere.getObservationTime())) {
            wrapper.like(Atmosphere::getObservationTime, atmosphere.getObservationTime());
        }

        if (StringUtils.isNotBlank(atmosphere.getFilepath())) {
            wrapper.like(Atmosphere::getFilename, atmosphere.getFilepath());
        }

        if (atmosphere.getDeviceId()!=null) {
            wrapper.eq(Atmosphere::getDeviceId, atmosphere.getDeviceId());
        }

        wrapper.eq(Atmosphere::getStatus, 0);

        // 检查是否有有效的更新条件
        if (wrapper.isNonEmptyOfWhere()&&wrapper.isNonEmptyOfEntity()||wrapper.isNonEmptyOfNormal()) {
            boolean flag = atmosphereService.update(atmosphere, wrapper);
            if (flag) {
                return ResultTemplate.success();
            }
            return ResultTemplate.fail();
        } else {
            // 处理没有有效条件的情况
            log.warn("更新条件为空，跳过更新操作");
            return ResultTemplate.fail("无更新参数！");
        }
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




