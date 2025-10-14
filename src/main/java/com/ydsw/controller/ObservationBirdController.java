package com.ydsw.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fengwenyi.api.result.ResultTemplate;
import com.ydsw.domain.ObservationBird;
import com.ydsw.service.ObservationBirdService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.net.URLEncoder;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.*;
import java.util.regex.Pattern;
@Slf4j
@RestController
public class ObservationBirdController {
    @Autowired
    ObservationBirdService observationBirdService;

    /*
     * 表单插入
     * */
    @PreAuthorize("hasAnyAuthority('api_ObservationBird_insert')")
    @PostMapping(value = "/api/ObservationBird/insert")
    @Transactional
    public ResultTemplate<Object> ObservationBirdinsert(@RequestBody JSONObject jsonObject) {
        ObservationBird observationBird = JSONUtil.toBean(jsonObject.toString(), ObservationBird.class);

        observationBird.setCreateTime(new Date());

        boolean flag = observationBirdService.saveBatch((Collection<ObservationBird>) observationBird);
        if (flag) {
            return ResultTemplate.success("成功！");
        }
        return ResultTemplate.fail("录入失败");
    }

    /*
     * 分页查询
     * */
    @PreAuthorize("hasAnyAuthority('api_ObservationBird_pageQuery')")
    @PostMapping(value = "/api/ObservationBird/pageQuery")
    public ResultTemplate<Object> ObservationBirdPageQuery(@RequestBody JSONObject jsonObject) {

        int currentPage = jsonObject.get("currentPage") == null ? 1 : jsonObject.getInt("currentPage");
        int pageSize = jsonObject.get("pageSize") == null ? 10 : jsonObject.getInt("pageSize");

        IPage<Map<String, Object>> Result = null;
        try {
            ObservationBird observationBird = JSONUtil.toBean(jsonObject.toString(), ObservationBird.class);
            Result = observationBirdService.selectPage(currentPage, pageSize, observationBird);
            List<Map<String, Object>> maps = Result.getRecords();
            for (Map<String, Object> map : maps) {
                Integer sid = (Integer) map.get("speciesId");
                Integer fid = (Integer) map.get("familyId");
                Integer oid = (Integer) map.get("orderId");
                List<Map<String, Object>> res = observationBirdService.selectNameById(sid, fid, oid);
                if (res.isEmpty()) {
                    return ResultTemplate.fail("视图错误！");
                }
                map.put("speciesId", res.get(0).get("sname"));
                map.put("familyId", res.get(0).get("fname"));
                map.put("orderId", res.get(0).get("oname"));

                Integer groupId = (Integer) map.get("watchPiId");
                res = observationBirdService.selectGroupById(groupId);
                if (res.isEmpty()) {
                    return ResultTemplate.fail("视图错误！");
                }
                map.put("watchPiId", res.get(0).get("groupName"));
            }
        } catch (Exception e) {
            return ResultTemplate.fail(e.getMessage());
        }
        return ResultTemplate.success(Result);
    }

    /*
     * 根据id删除和查询条件删除，若id列表存在，只根据id列表删除，否则，根据条件删除，必须存在至少一个删除条件
     * */
    @PreAuthorize("hasAnyAuthority('api_ObservationBird_delByIds')")
    @RequestMapping(value = "/api/ObservationBird/delByIds")
    @Transactional
    public ResultTemplate<Object> delByIds(@RequestBody JSONObject jsonObject) {
        List<Integer> idArray = jsonObject.getBeanList("ids", Integer.class);//id列表
        ObservationBird observationBird = JSONUtil.toBean(jsonObject.toString(), ObservationBird.class);
        //根据条件删除
        if (idArray.isEmpty()) {
            try {
                observationBirdService.delByIdList(new ArrayList<>(), observationBird);
                return ResultTemplate.success("删除成功！");
            } catch (Exception e) {
                return ResultTemplate.fail("删除时出错！");
            }
        }
        try {
            observationBirdService.delByIdList(idArray, observationBird);
        } catch (Exception e) {
            return ResultTemplate.fail("删除时出错！");
        }
        return ResultTemplate.success("删除成功！");
    }
/*
* 根据种/科/目名/组名反演对应外键
* */
    private void StrNameToIntId(@RequestBody JSONObject jsonObject) {
        String species = jsonObject.getStr("speciesId");
        String order = jsonObject.getStr("orderId");
        String family = jsonObject.getStr("familyId");
        String group = jsonObject.getStr("watchPiId");
        if (group == null) {
            jsonObject.set("watchPiId", null);
        } else if (group.isEmpty()) {
            jsonObject.set("watchPiId", null);
        } else {
            List<Map<String, Object>> groupid = observationBirdService.selectIdByGroup(group);
            jsonObject.set("watchPiId", groupid.get(0).get("id"));
        }
        removeNotAlive(jsonObject, species, order, family);
        List<Map<String, Object>> idgroup = observationBirdService.selectIdBySpecies(species, family, order);
        jsonObject.set("speciesId", idgroup.get(0).get("sid"));
        jsonObject.set("familyId", idgroup.get(0).get("fid"));
        jsonObject.set("orderId", idgroup.get(0).get("oid"));
        removeNotAlive(jsonObject, species, order, family);
    }
/*
* 将空值用对应的赋值方法置空，防止出现空错误
* */
    private void removeNotAlive(@RequestBody JSONObject jsonObject, String species, String order, String family) {
        if (order == null) {
            jsonObject.set("orderId", null);
        } else if (order.isEmpty()) {
            jsonObject.set("orderId", null);
        }
        if (family == null) {
            jsonObject.set("familyId", null);
        } else if (family.isEmpty()) {
            jsonObject.set("familyId", null);
        }
        if (species == null) {
            jsonObject.set("speciesId", null);
        } else if (species.isEmpty()) {
            jsonObject.set("speciesId", null);
        }
    }

    /*
     * 鸟类数据按日期导出Excel
     * */
    @PreAuthorize("hasAnyAuthority('api_ObservationBird_ExcelByDate')")
    @PostMapping(value = "/api/ObservationBird/ExcelByDate")
    @Transactional
    public ResultTemplate<Object> ObservationBirdOutExcelByDate(@RequestBody JSONObject jsonObject, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> data = null;
        String fileName = null;
        Workbook workbook;
        try {
//            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Calendar calendar = Calendar.getInstance();
//            ParsePosition pos = new ParsePosition(0);
            String observationTime = jsonObject.getStr("observationTime");

//            String observationPeriodBegin = observationTime + " 00:00:00";
//            Date date = format.parse(observationPeriodBegin, pos);
//            calendar.setTime(date);
//            calendar.add(Calendar.DATE, 1);
//            String observationPeriodEnd = format.format(calendar.getTime());
            data = observationBirdService.selectAllByDay(observationTime, null,null,null, null);
            if (data.isEmpty()) {
                return ResultTemplate.fail("该日没有观测数据！");
            }
            int numN1 = 0;
            int numN2 = 0;
            int Sum1 = 0;
            int Sum2 = 0;
            //写入前数据处理
            for (Map<String, Object> map : data) {
                Integer sid = (Integer) map.get("speciesId");
                Integer fid = (Integer) map.get("familyId");
                Integer oid = (Integer) map.get("orderId");
                List<Map<String, Object>> res = observationBirdService.selectNameById(sid, fid, oid);
                if (res.isEmpty()) {
                    return ResultTemplate.fail("视图错误！");
                }
                map.put("speciesId", res.get(0).get("sname"));
                map.put("familyId", res.get(0).get("fname"));
                map.put("orderId", res.get(0).get("oname"));
                map.put("nameLatin", res.get(0).get("nameLatin"));
                if (res.get(0).get("constantId") != null) {
                    map.put("constantId", "N" + res.get(0).get("constantId").toString());
                    if ((Integer) res.get(0).get("constantId") == 1) {
                        numN1++;
                    } else if ((Integer) res.get(0).get("constantId") == 2) {
                        numN2++;
                    }
                } else {
                    map.put("constantId", null);
                }

            }
            String[] dateStrs = observationTime.split("-");
            String FirstCellText = dateStrs[0] + "年" + dateStrs[1] + "月" + dateStrs[2] + "日鸟类同步调查数据汇总表";
            String weatherStr = "天气：" + data.get(0).get("weather").toString().split(",")[0];


            //创建

            fileName = FirstCellText + ".xls";
            //路径设置,为本地磁盘
            String p = Paths.get("").toAbsolutePath().toString();
            String sep = FileSystems.getDefault().getSeparator();
            // 如果分隔符是反斜杠，则需要进行转义
            String[] paths = p.split(Pattern.quote(sep));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < paths.length - 1; i++) {
                sb.append(paths[i]);
                sb.append(sep);
            }
            String pathname = sb.toString() + "excelTemplate"+sep+"鸟类模板.xls";
            sb.append("Trash");
            sb.append(sep);
            sb.append(fileName);
            File testFile = new File(sb.toString());
            File fileParent = testFile.getParentFile();//返回的是File类型,可以调用exsit()等方法
            if (!fileParent.exists()) {
                fileParent.mkdirs();// 能创建多级目录
            }
            if (!testFile.exists()) {
                testFile.createNewFile();//有路径才能创建文件
            }
            File mFile = new File(pathname);
            FileUtils.copyFile(mFile, testFile);
            InputStream inputStream = new FileInputStream(testFile);
            workbook = new HSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            HSSFSheet hssfSheet = (HSSFSheet) sheet;

            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);//设置垂直居中
            cellStyle.setAlignment(HorizontalAlignment.CENTER);//设置水平居中
            cellStyle.setBorderBottom(BorderStyle.THIN); // 下边框
            cellStyle.setBorderLeft(BorderStyle.THIN);// 左边框
            cellStyle.setBorderTop(BorderStyle.THIN);// 上边框
            cellStyle.setBorderRight(BorderStyle.THIN);// 右边框

            CellStyle cellStyle1 = workbook.createCellStyle();
            cellStyle1.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyle1.setAlignment(HorizontalAlignment.CENTER);
            cellStyle1.setBorderBottom(BorderStyle.THIN);
            cellStyle1.setBorderLeft(BorderStyle.THIN);
            cellStyle1.setBorderTop(BorderStyle.THIN);
            cellStyle1.setBorderRight(BorderStyle.THIN);
            Font font = workbook.createFont();
            font.setBold(true);
            cellStyle1.setFont(font);
            //天气和时间标题
            HSSFRow row = hssfSheet.getRow(0);
            // row.createCell(0);
            row.getCell(0).setCellValue(FirstCellText);
            row = hssfSheet.getRow(1);
            //row.createCell(0);
            row.getCell(0).setCellValue(weatherStr);

            for (int i = 0; i < data.size(); i++) {
                int lSum1 = 0;
                int lSum2 = 0;
                row = hssfSheet.createRow(i + 4);
                row.createCell(0).setCellValue(i + 1);
                row.createCell(1).setCellValue(data.get(i).get("orderId").toString());
                row.createCell(2).setCellValue(data.get(i).get("familyId").toString());
                row.createCell(3).setCellValue(data.get(i).get("speciesId").toString());
                row.createCell(4).setCellValue("");
                if (data.get(i).get("nameLatin") != null) {
                    row.createCell(4).setCellValue(data.get(i).get("nameLatin").toString());
                }
                if (data.get(i).get("constantId") != null) {
                    row.createCell(5).setCellValue(data.get(i).get("constantId").toString());
                } else {
                    row.createCell(5).setCellValue("");
                }
                String[] groupIds = data.get(i).get("watchPiId").toString().split(",");
                String[] nums = data.get(i).get("number").toString().split(",");
                for (int j = 0; j < groupIds.length; j++) {
                    if (Integer.parseInt(groupIds[j]) <= 3) {
                        row.createCell(6 + Integer.parseInt(groupIds[j])).setCellValue(nums[j]);
                        lSum1 += Integer.parseInt(nums[j]);
                    } else {
                        row.createCell(7 + Integer.parseInt(groupIds[j])).setCellValue(nums[j]);
                        lSum2 += Integer.parseInt(nums[j]);
                    }
                }
                row.createCell(6).setCellValue(lSum1 + lSum2);
                row.createCell(10).setCellValue(lSum1);
                Sum1 += lSum1;
                row.createCell(14).setCellValue(lSum2);
                Sum2 += lSum2;
                for (int j = 0; j <= 14; j++) {
                    if (row.getCell(j) == null) {
                        row.createCell(j);
                    }
                    row.getCell(j).setCellStyle(cellStyle);
                }
                row.getCell(6).setCellStyle(cellStyle1);
            }
            row = hssfSheet.createRow(data.size() + 4);
            /*
             * 合并单元格
             * */
            int rows = hssfSheet.getLastRowNum() + 1;
            //合并起始行初始单元格内容
            if (data.size() > 1) {
                //计算合计
                int speciesNum = 1;
                int familyNum = 1;
                int orderNum = 1;
                row.createCell(0).setCellValue("合计");
                row.createCell(1);
                row.createCell(2);
                row.createCell(3);
                for (int j = 1; j < 4; j++) {
                    //合并起始行初始值
                    int firstRow = 4;
                    String firstValue = sheet.getRow(4).getCell(j).getStringCellValue();
                    for (int i = 4; i < rows; i++) {
                        row = hssfSheet.getRow(i);
                        Cell cell = row.getCell(j);
                        if (cell == null) {
                            continue;
                        }
                        if (!cell.getStringCellValue().equals(firstValue)) {

                            //单元格不相同时进行合并
                            //例如从第0行开始得到一个初始值，然后读取到第十行时内容变了，那么合并的截至行就是第十行减去一行，就是从第0行合并至第九行
                            if (i - 1 - firstRow > 0) {
                                hssfSheet.addMergedRegion(new CellRangeAddress(firstRow, i - 1, j, j));
                            }
                            if (j == 1) {
                                orderNum++;
                            } else if (j == 2) {
                                familyNum++;
                            } else {
                                speciesNum++;
                            }
                            //单元格内容不相同时重新赋值合同单元格的初始内容
                            firstValue = cell.getStringCellValue();
                            //单元格内容不相同时重新赋值合并的初始行
                            firstRow = i;
                        }
                    }
                }
                row.getCell(1).setCellValue(orderNum + "个目");
                row.getCell(2).setCellValue(familyNum + "个科");
                row.getCell(3).setCellValue(speciesNum + "个种");
                row.createCell(5).setCellValue("N1:" + numN1 + "种，N2:" + numN2 + "种");
                row.createCell(6).setCellValue(Sum1 + Sum2);
                row.createCell(10).setCellValue(Sum1);
                row.createCell(14).setCellValue(Sum2);
                for (int i = 0; i <= 14; i++) {
                    if (row.getCell(i) == null) {
                        row.createCell(i);
                    }
                    if (i == 6) {
                        row.getCell(6).setCellStyle(cellStyle1);
                        continue;
                    }
                    row.getCell(i).setCellStyle(cellStyle);
                }
                OutputStream outputStream = response.getOutputStream();
                response.reset();
                response.setContentType("application/msexcel");
                response.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
                workbook.write(outputStream);
                workbook.close();
                outputStream.flush();
                outputStream.close();
                return null;
            }
        } catch (Exception e) {
            return ResultTemplate.fail(e.getMessage());
        }
        return ResultTemplate.fail("未知中断！");
    }

    /*
     * 鸟类返回数据库所有目，科，种
     * */
    @PreAuthorize("hasAnyAuthority('api_ObservationBird_fetchData')")
    @PostMapping(value = "/api/ObservationBird/fetchData")
    public ResultTemplate<Object> ObservationBirdfetchData() {
        Map<String, Object> res = new HashMap<>();
        List<Map<String, Object>> res1 = observationBirdService.selectAllSpecies();
        List<Map<String, Object>> res2 = observationBirdService.selectAllFamily();
        List<Map<String, Object>> res3 = observationBirdService.selectAllOrder();
        List<Map<String, Object>> res4 = observationBirdService.selectAllGroup();
        for (Map<String, Object> map : res1) {
            String[] speciesName = map.get("speciesName").toString().split(",");
            String[] speciesNameLatin = map.get("speciesNameLatin").toString().split(",");
            String[] speciesIdStr = map.get("speciesId").toString().split(",");
            int[] speciesId = StringArrayToIntArray(speciesIdStr);
            map.put("speciesId", speciesId);
            map.put("speciesName", speciesName);
            map.put("speciesNameLatin", speciesNameLatin);
        }
        for (Map<String, Object> map : res2) {
            String[] familyName = map.get("familyName").toString().split(",");
            String[] familyIdStr = map.get("familyId").toString().split(",");
            int[] familyId = StringArrayToIntArray(familyIdStr);
            map.put("familyId", familyId);
            map.put("familyName", familyName);
        }
        for (Map<String, Object> map : res4) {
            String[] groupName = map.get("groupName").toString().split(",");
            String[] groupIdStr = map.get("groupId").toString().split(",");
            int[] groupId = StringArrayToIntArray(groupIdStr);
            map.put("groupId", groupId);
            map.put("groupName", groupName);
        }
        res.put("speciesTofamily", res1);
        res.put("familyToorder", res2);
        res.put("orders", res3);
        res.put("groups", res4);
        return ResultTemplate.success(res);
    }

    public int[] StringArrayToIntArray(String[] arr) {
        int[] array = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            array[i] = Integer.parseInt(arr[i]);
        }
        return array;
    }

    /*
     * 最近一天内各鸟类占比
     * */
    @PreAuthorize("hasAnyAuthority('api_ObservationBird_percenttageInLastDay')")
    @PostMapping(value = "/api/ObservationBird/percenttageInLastDay")
    public ResultTemplate<Object> ObservationBirdfetchpercenttageInLastDay() {
        List<Map<String, Object>> data = null;

        data = observationBirdService.selectMaxTimeBigin();
        String observationTime = data.get(0).get("observationTime").toString().substring(0, 10);
        if(data.isEmpty())
        {
            return ResultTemplate.fail("数据库数据异常!");
        }
        String observationPeriodBegin=observationTime.substring(0, 8)+"01";
        data = observationBirdService.selectAllByDay(null, observationPeriodBegin, observationTime,null,null);
        if (data.isEmpty()) {
            return ResultTemplate.fail("该日没有观测数据！");
        }
        for (Map<String, Object> map : data) {
            map.remove("watchPiId");
            map.remove("weather");
            map.remove("constantId");
            Integer sid = (Integer) map.get("speciesId");
            Integer fid = (Integer) map.get("familyId");
            Integer oid = (Integer) map.get("orderId");
            List<Map<String, Object>> res = observationBirdService.selectNameById(sid, fid, oid);
            if (res.isEmpty()) {
                return ResultTemplate.fail("视图错误！");
            }
            map.put("speciesId", res.get(0).get("sname"));
            map.put("familyId", res.get(0).get("fname"));
            map.put("orderId", res.get(0).get("oname"));
            map.put("nameLatin", res.get(0).get("nameLatin"));
        }
        int Sum = 0;
        for (Map<String, Object> datum : data) {

            int lSum = 0;
            String[] nums = datum.get("number").toString().split(",");
            for (String num : nums) {
                lSum += Integer.parseInt(num);
                Sum += Integer.parseInt(num);
            }
            datum.put("speciesId", datum.get("speciesId").toString());
            datum.put("num", lSum);
            datum.remove("number");
        }
        double y = Sum * 1.0;
        for (Map<String, Object> datum : data) {
            double x = 1.0 * (Integer) datum.get("num");
            NumberFormat percentInstance = NumberFormat.getPercentInstance();
            percentInstance.setMaximumFractionDigits(2);
            datum.put("percentage", percentInstance.format(x / y));
        }
        Map<String,Object> map = new HashMap<>();
        map.put("sum", Sum);
        map.put("data", data);
        map.put("month",observationTime.substring(0,4)+"年"+observationTime.substring(5,7)+"月");
        return ResultTemplate.success(map);
    }
    /*
    * 获取最近鸟类观测时间数据
    * */
    @PreAuthorize("hasAnyAuthority('api_ObservationBird_getObservationTime')")
    @PostMapping(value = "/api/ObservationBird/getObservationTime")
    public ResultTemplate<Object> getObservationTimes() {
        List<Map<String, Object>> mapList = observationBirdService.selectObservationTimes();
        Map<String, Object> res = new HashMap<>();
        StringBuilder observationTimesb = new StringBuilder();
        for (Map<String, Object> map : mapList) {
            observationTimesb.append(map.get("observationTime").toString()).append(",");
        }
        observationTimesb.setLength(observationTimesb.length() - 1);
        String[] observationTime = observationTimesb.toString().split(",");
        res.put("observationTime", observationTime);
        return ResultTemplate.success(res);
    }
}
