package com.ydsw.utils;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ydsw.domain.SpectralReflectance;
import com.ydsw.domain.TbWaterLevel;
import com.ydsw.pojo.excel.WaterExcel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author zhangkaifei
 * //@description: TODO
 * //@date 2024/7/25  10:03
 * //@Version 1.0
 */
@Slf4j
public class ExcelParserUtils {

    public static Map<String, String> CTE = new HashMap<>();
    //将Excel列转为对应WaterExcel字段名
    public static void CTEIntilized() {
        //重新提交测试//水位
        CTE.put("        月份                \n    水位\n日期\n\n\n", "day");
        CTE.put("一月", "jan");
        CTE.put("二月", "feb");
        CTE.put("三月", "mar");
        CTE.put("四月", "apr");
        CTE.put("五月", "may");
        CTE.put("六月", "june");
        CTE.put("七月", "july");
        CTE.put("八月", "aug");
        CTE.put("九月", "sept");
        CTE.put("十月", "oct");
        CTE.put("十一月", "nov");
        CTE.put("十二月", "dec");

        //水体理化
        CTE.put("站点", "deviceId");
        CTE.put("水温", "waterTemperature");
        CTE.put("pH", "ph");
        CTE.put("浊度", "turbidity");
        CTE.put("电导率 ", "conductivity");
        CTE.put("溶解氧 ", "dissolvedOxygen");
        CTE.put("透明度 ", "transparency");
        CTE.put("高锰酸盐指数", "codmn");
        CTE.put("TSS ", "tss");
        CTE.put("TN", "tn");
        CTE.put("TP", "tp");
        CTE.put("叶绿素", "chlorophyll");

        //气象
        //CTE.put("序号","id");
        CTE.put("Date", "observationTime");
        CTE.put("风速(m/s)", "windSpeed");
        CTE.put("雨量(mm)", "rainfall");
        CTE.put("大气温度(℃)", "atmosphereTemperature");
        CTE.put("土壤温度(℃)", "soilTemperature");
        CTE.put("数字气压(hPa)", "digitalPressure");
        //模板易错点
        CTE.put("简易总辐射 (W/m2)", "simpleTotalRadiation");
        CTE.put("风向(°)", "windDirection");
        CTE.put("土壤湿度(%RH)", "soilHumidity");
        CTE.put("大气湿度(%RH)", "atmosphereHumidity");
        CTE.put("PM2.5 (ug/m3)", "pm25");
        CTE.put("盐分(mg/L)", "salinity");
        CTE.put("负氧离子(个)", "negativeOxygenIon");
        CTE.put("雨量累计(mm)", "rainfallAccumulation");
        CTE.put("辐射累计(MJ/m2)", "radiationAccumulation");
        CTE.put("PM10 (ug/m3)", "pm10");
        CTE.put("相对湿度（%）", "relativeHumidity");
        CTE.put("AQI指数", "aqiIndex");
        CTE.put("首要污染物", "primaryPollutant");
        CTE.put("空气质量等级", "airQualityLevel");
        CTE.put("PM2.5(μg/m3)", "pm25");
        CTE.put("PM10(μg/m3)", "pm10");
        CTE.put("二氧化硫(μg/m3)", "sulfurDioxide");
        CTE.put("二氧化氮(μg/m3)", "nitrogenDioxide");
        CTE.put("一氧化碳（mg/m3）", "carbonMonoxide");
        CTE.put("臭氧(μg/m3)", "ozone");
        CTE.put("8小时臭氧（μg/m3）", "ozone8Hour");

        //径流//待定
        CTE.put("\n" +
                "               站点              \n" +
                "        流量\n" +
                "日期\n" +
                "\n" +
                "\n", "day");
        CTE.put("伊河东湾", "data1");
        CTE.put("伊河陆浑坝下", "data2");
    }
    /**
     * 从excel文件解析到bean
     * <p>
     * //@param inputStream   文件输入流
     * //@param clazz         解析类
     * //@param sheetNum      sheet编号，从0开始
     * //@param fieldsRowNum  对应字段行号，从1开始
     * //@param dataRowNum    数据行号，从1开始
     * //@param singleRCArray 单独解析的位置，为一个行列坐标，从0开始[{0,1},{2,3}]
     * //@param excelType     excel文件类型"xlsx"或者"xls"
     * //@param clazz
     * //@return 解析的类数组
     */
    public static <T> JSONObject parseExcelFile(InputStream inputStream, Class<T> clazz,
                                                int sheetNum, int fieldsRowNum, int dataRowNum,
                                                JSONArray singleRCArray, String excelType) {
        List<T> objectList = new ArrayList<>();
        JSONObject resultJSONObject = new JSONObject();
        int rownum = 0;
        int j = 0;
        CTEIntilized();
        try {
            Workbook workbook = "xls".equals(excelType) ? new HSSFWorkbook(inputStream) : new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(sheetNum); // Assuming the data is in the first sheet
            // Get the field names from the class
            Field[] fields = clazz.getDeclaredFields();
            //首先解析，单个位置，保存在list中
            List<String> singleRCList = new ArrayList<>();
            for (Object obj : singleRCArray) {
                List listObj = (List) obj;
                int R = (int) listObj.get(0);
                int C = (int) listObj.get(1);
                Row row = sheet.getRow(R);

                Cell cell = row.getCell(C);
                if (cell == null) {
                    singleRCList.add("");
                } else {
                    CellType cellType = cell.getCellType();
                    // 这里只需要把可能的数据类型都加上就可以了；不过一般也就那么基本数据类型加上字符串
                    if (cellType == CellType.NUMERIC) {
                        singleRCList.add(String.valueOf(cell.getNumericCellValue()));
                    } else if (cellType == CellType.STRING) {
                        singleRCList.add(cell.getStringCellValue());
                    } else if (cellType == CellType.BOOLEAN) {
                        singleRCList.add(String.valueOf(cell.getBooleanCellValue()));
                    } else if (cellType == CellType.BLANK) {
                        singleRCList.add(null);
                    }
                    else {
                        //暂不支持的类型
                        throw new TypeNotPresentException("暂不支持" + cellType + "类型", null);
                    }
                }
            }
            List<String> objectFieldNameList = Arrays.stream(fields).map(Field::getName).toList();
            //获取excel第fieldsRowNum行作为对象的字段名
            List<String> excelFieldNameList = new ArrayList<>();
            // 迭代行
            for (int m = 0; m <= sheet.getLastRowNum(); m++) {

                Row row = sheet.getRow(m);
                rownum++;
                if (rownum < fieldsRowNum) {
                    continue;
                }
                T object = (T) clazz.newInstance(); // Create an instance of the class
                //获取excel表的第fieldsRowNum行作为字段名
                if (rownum == fieldsRowNum) {
                    for (int n = 0; n < row.getLastCellNum(); n++) {
                        Cell cell = row.getCell(n);
                        if(cell==null){
                            continue;
                        }
                        excelFieldNameList.add(cell.getStringCellValue());
                    }
                } else if (rownum >= dataRowNum) {//获取数据行
                    j = 0;
                    for (int n = 0; n < row.getLastCellNum(); n++) {//迭代列
                        Cell cell = row.getCell(n);
                        if(n>=excelFieldNameList.size()||n>=objectFieldNameList.size()){
                            continue;
                        }
                        if (!objectFieldNameList.contains(excelFieldNameList.get(j)) && !CTE.containsKey(excelFieldNameList.get(j))) {
                            //过滤excel中存在，但类中不存在的字段
                            j++;
                            continue;
                        }
                        //注意，这里如果尝试获取类里面没有的字段，会抛异常，因此excel表结构最好协商定下来。当然有了上一步的判断，这个问题不会发生
                        Field field = null;
                        try {
                            field = clazz.getDeclaredField(excelFieldNameList.get(j));
                        } catch (NoSuchFieldException e) {//水位特殊变量处理
                            field = clazz.getDeclaredField(CTE.get(excelFieldNameList.get(j)));
                        }
                        //开启编辑权限
                        field.setAccessible(true);
                        if (cell == null) {
                            field.set(object, "");
                        } else {
                            CellType cellType = cell.getCellType();
                            // 这里只需要把可能的数据类型都加上就可以了；不过一般也就那么基本数据类型加上字符串
                            if (cellType == CellType.NUMERIC) {
                                field.set(object, String.valueOf(cell.getNumericCellValue()));
                            } else if (cellType == CellType.STRING) {
                                field.set(object, cell.getStringCellValue());
                            } else if (cellType == CellType.BOOLEAN) {
                                field.set(object, cell.getBooleanCellValue());
                            } else if (cellType == CellType.BLANK) {
                                field.set(object, cell.getStringCellValue());
                            } else {
                                //暂不支持的类型
                                throw new TypeNotPresentException("暂不支持" + cellType + "类型", null);
                            }
                        }
                        j++;
                        //关闭编辑权限
                        field.setAccessible(false);
                    }
                    objectList.add(object);
                }
            }
            //System.out.println(JSONUtil.parseArray(objectList).toString());
            resultJSONObject.put("classlist", objectList);
            //resultJSONObject.put("singleRCList", JSONUtil.parseArray(singleRCList));
            workbook.close();
            inputStream.close();
        } catch (IOException | ReflectiveOperationException e) {
            log.error(e.getMessage(), e);
            e.printStackTrace();
        } catch (IllegalStateException e) {
            log.error(rownum + "行，" + (j + 1) + "列,  " + e.getMessage());
            e.printStackTrace();
        }
        return resultJSONObject;
    }

    /*
     * 返回所有sheet的名称以及所有sheet第一个元素。用逗号分割.水位、径流、气象、水体理化用
     * */
    public static Map<String, String> getSheetNamesAndFirstCellOfExcel(InputStream inputStream, String excelType) {
        try {
            Workbook workbook = "xls".equals(excelType) ? new HSSFWorkbook(inputStream) : new XSSFWorkbook(inputStream);
            Map<String, String> SheetNamesAndFirstCell = new HashMap<>();
            SheetNamesAndFirstCell.put("sheetNames", "");
            SheetNamesAndFirstCell.put("firstCell", "");
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);

                if (i != 0) {
                    SheetNamesAndFirstCell.put("sheetNames", SheetNamesAndFirstCell.get("sheetNames") + ",");
                    SheetNamesAndFirstCell.put("firstCell", SheetNamesAndFirstCell.get("firstCell"));
                }

                Row row = sheet.getRow(0);
                if (row == null) {
                    continue; // 如果第一行为空，跳过当前循环
                }

                SheetNamesAndFirstCell.put("sheetNames", SheetNamesAndFirstCell.get("sheetNames") + sheet.getSheetName());
                Cell cell = row.getCell(0);
                if (cell != null) {
                    SheetNamesAndFirstCell.put("firstCell", SheetNamesAndFirstCell.get("firstCell") + cell.getStringCellValue());
                }
            }
            return SheetNamesAndFirstCell;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
     * 返回所有包含传入字符串的sheet
     * */
    public static List<Sheet> RETSheetsByName(InputStream inputStream, String excelType, String sheetNameOf) {
        try {
            Workbook workbook = ("xls".equals(excelType)) ? new HSSFWorkbook(inputStream) : new XSSFWorkbook(inputStream);
            List<Sheet> sheetList = new ArrayList<>();
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                String sheetName = sheet.getSheetName();
                if (sheetName.length() >= sheetNameOf.length()) {
                    if (sheetName.startsWith(sheetNameOf)) {
                        sheetList.add(sheet);
                    }
                }
            }
            return sheetList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
     *光谱专用解析
     * */
    public static Map<String, String> parseExcelFileForSpectral(Sheet sheet, String excelType) {
        Map<String, String> Result = new HashMap<>();
        Result.put("wavelength", "");
        Result.put("data", "");
        Result.put("deviceId", "");
        boolean isNotFirstdata = false;
        boolean isNotFirstid = false;
        for (int m = 0; m <= sheet.getLastRowNum(); m++) {
            Row row = sheet.getRow(m);
            if (row == null) {
                break;
            }
            for (int i = 0; i <= row.getLastCellNum(); i++) {
                Cell cell = row.getCell(i);
                if (cell == null) {
                    continue;
                } else if (m == 0) {
                    String str = "";
                    if (!isNotFirstid) {
                        isNotFirstid = true;
                    } else {
                        str = ",";
                    }
                    CellType cellType = cell.getCellType();
                    if (cellType == CellType.NUMERIC) {
                        str += String.format("%.0f", cell.getNumericCellValue());
                        Result.put("deviceId", Result.get("deviceId") + str);
                    } else if (cellType == CellType.STRING || cellType == CellType.BLANK) {
                        str += cell.getStringCellValue();
                        Result.put("deviceId", Result.get("deviceId") + str);
                    } else {
                        //暂不支持的类型
                        throw new TypeNotPresentException("暂不支持" + cellType + "类型", null);
                    }
                } else if (i == 0) {
                    CellType cellType = cell.getCellType();
                    String str = "";
                    if (m != 1) {
                        str = ",";
                    }
                    if (cellType == CellType.NUMERIC) {
                        str += String.format("%.0f", cell.getNumericCellValue());
                        Result.put("wavelength", Result.get("wavelength") + str);
                    } else if (cellType == CellType.STRING || cellType == CellType.BLANK) {
                        str += cell.getStringCellValue();
                        Result.put("wavelength", Result.get("wavelength") + str);
                    } else {
                        //暂不支持的类型
                        throw new TypeNotPresentException("暂不支持" + cellType + "类型", null);
                    }
                } else {
                    CellType cellType = cell.getCellType();
                    String str = "";
                    if (!isNotFirstdata) {
                        isNotFirstdata = true;
                    } else {
                        str = ",";
                    }
                    if (cellType == CellType.NUMERIC) {
                        str += String.format("%.6f", cell.getNumericCellValue());
                        Result.put("data", Result.get("data") + str);
                    } else if (cellType == CellType.STRING || cellType == CellType.BLANK) {
                        str += cell.getStringCellValue();
                        Result.put("data", Result.get("data") + str);
                    } else {
                        //暂不支持的类型
                        throw new TypeNotPresentException("暂不支持" + cellType + "类型", null);
                    }
                }
            }
        }
        return Result;
    }
    /*
    * @param str 任意字符串
    * @return str的数字和‘-’部分，顺序
     * */
    //获取字符串中的字符和‘-部分’
    public static String getOnlyNumsFromStr(String str) {
        StringBuilder strNum = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) >= '0' && str.charAt(i) <= '9' || str.charAt(i) == '-') {
                strNum.append(str.charAt(i));
            }
        }
        return strNum.toString();
    }

    /**
     * 返回文件名和路径
     *
     * @param fileName 文件名
     * @param fileType 文件后缀
     * @return 路径和文件名
     * //@throws IOException
     */
    public static Map<String, String> getFileNameAndPath(String fileName, String fileType, String excelType) throws IOException {
        Date date = new Date();
        //时间转换
        String datastr = new SimpleDateFormat("yyyy年-MM月-dd日", Locale.CHINESE).format(date);
        String resultMon = datastr.substring(0, 9);
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
        sb.append("Trash");
        sb.append(sep);
        sb.append(excelType);
        sb.append(sep);
        sb.append(resultMon);
        sb.append(sep);
        String savePath = sb.toString();
        String newName = fileName + "-" + datastr + "-" + UUID.randomUUID().toString().replace("-", "") + "." + fileType;
        Map<String, String> map = new HashMap<>();
        map.put("fileName", newName);
        map.put("savePath", savePath);
        return map;
    }

    /**
     * 保存文件
     *
     * @param filePath 文件路径
     * @param fileName 文件名
     * @param fileMul  文件
     * @throws IOException
     */
    public static void saveFile(String filePath, String fileName, MultipartFile fileMul) throws IOException {
        File floder = new File(filePath);
        if (!floder.exists()) {
            floder.mkdirs();
        }
        //创建临时文件
        File fileCur = new File(filePath + fileName);
        fileCur.createNewFile();
        fileMul.transferTo(fileCur);
    }

    /*
     * 测试区域
     * */
    /*
     * 水位本地测试案例，通过调试或输出到控制台查看结果。其余部分可在下面另起一个函数在main函数中运行
     * */
    public static void watetlevelExcelLocalFileTest() throws FileNotFoundException {
        //模拟数据
        String pathname = "C:\\Users\\Administrator\\Desktop\\项目资源\\附6.陆浑水库2021年水位—河南大学.xls";
        String userUid = UUID.randomUUID().toString();
        String fileType = "xls";
        String userName = "admin";

        File file = new File("C:\\Users\\Administrator\\Desktop\\项目资源\\附6.陆浑水库2021年水位—河南大学.xls");
        //("D:\\Desktop\\陆浑湖\\code\\上传模板\\附6.陆浑水库2021年水位—河南大学 - 副本.xls");
        InputStream inputStream = new FileInputStream(file);//读取sheet和次要数据
        InputStream inputStream1 = new FileInputStream(file);//读取主要数据
        JSONArray singleRCArray = new JSONArray();
        List list = new ArrayList();
        list.add(1);
        list.add(0);
        singleRCArray.add(list);

        Map<String, String> SheetNamesAndFirstCell = getSheetNamesAndFirstCellOfExcel(inputStream, fileType);
        String sheetNames = SheetNamesAndFirstCell.get("sheetNames");
        String firstCell = SheetNamesAndFirstCell.get("firstCell");
        String strs[] = firstCell.split(",");
        String year = getOnlyNumsFromStr(strs[0]);

        JSONObject waterObj = ExcelParserUtils.parseExcelFile(inputStream1, WaterExcel.class, 0, 3, 4, singleRCArray, "xls");
        //log.info(waterObj.toString());
        JSONArray peoples = (JSONArray) waterObj.get("classlist");
        List<WaterExcel> t = JSONUtil.toList(peoples, WaterExcel.class);

        //实时简化对应 //1-"01" 2-"02"
        String[] numsString = new String[12];
        for (int i = 0; i < 9; i++) {
            numsString[i] = "0" + (i + 1);
        }
        numsString[9] = "10";
        numsString[10] = "11";
        numsString[11] = "12";
        List<TbWaterLevel> tbWaterLevelList = new ArrayList<>();
        //类对应转换//除径流和水位外不需要单独处理
        for (WaterExcel p : t) {
            if (p.getDay().isEmpty()) {
                break;
            }
            String ResultTime = "";
            String[] Result = new String[12];
            System.out.println(p);
            String day = p.getDay().substring(0, p.getDay().length() - 2);
            if (day.length() == 1) {
                day = "0" + day;
            }
            //导入数据

            p.putInArray(Result);
            for (int i = 0; i < 12; i++) {
                if (Result[i].isEmpty() || Result[i] == null) {
                    continue;
                } else {
                    ResultTime = "";
                    ResultTime += sheetNames + "-";
                    ResultTime += numsString[i] + "-";
                    ResultTime += day;
                    //时间
                    TbWaterLevel tbWaterLevel = new TbWaterLevel(Result[i], ResultTime, new Date(), fileType, userName, pathname,"", userUid, 0);
                    System.out.println(tbWaterLevel);
                    tbWaterLevelList.add(tbWaterLevel);
                }
            }
        }
    }

    /*
     *光谱测试
     * */
    public static void spectralReflectance() {
        //模拟数据
        String pathname = "C:\\Users\\Administrator\\Desktop\\项目资源\\附5.陆浑湖水体理化数据2021-河南大学.xlsx";
        String userUid = UUID.randomUUID().toString();
        String fileType = "xlsx";
        String userName = "admin";
        List<SpectralReflectance> spectralReflectanceList = new ArrayList<>();
        File file = new File(pathname);
        try {
            InputStream inputStream1 = new FileInputStream(file);
            List<Sheet> sheetList = RETSheetsByName(inputStream1, "xlsx", "光谱反射率");
            if (sheetList.isEmpty()) {
                System.out.println("未找到符合条件的sheet");
                return;
            }
            Map<String, String> oneOfResult = new HashMap<>();
            for (Sheet sheet : sheetList) {
                oneOfResult = parseExcelFileForSpectral(sheet, fileType);
                System.out.println(oneOfResult);

                String[] wavelengths = oneOfResult.get("wavelength").split(",");
                String[] datas = oneOfResult.get("data").split(",");
                String[] deviceIds = oneOfResult.get("deviceId").split(",");
                String observationTime = sheet.getSheetName().substring(5);
                Date date = new Date();
                for (int i = 0; i < deviceIds.length; i++) {
                    String deviceId = deviceIds[i];
                    for (int j = 0; j < wavelengths.length; j++) {
                        String wavelength = wavelengths[j];
                        String data = datas[i + j * deviceIds.length];
                        SpectralReflectance spectralReflectance = new SpectralReflectance(wavelength, Integer.parseInt(deviceId), data, observationTime, date, fileType, userName, pathname,"", userUid, 0);
                        spectralReflectanceList.add(spectralReflectance);
                    }
                    System.out.println(spectralReflectanceList);
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
/*
* Excel填充测试
* */
    public static void ObservationBirdExcelFill() throws IOException {

        try {
            String observationTime="2024-11-11";
            String[] dateStrs = observationTime.split("-");
            String FirstCellText = dateStrs[0] + "年" + dateStrs[1] + "月" + dateStrs[2] + "日鸟类同步调查数据汇总表";
            String weatherStr = "天气:多云" ;
            //创建

            String fileName = FirstCellText + ".xls";
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
            sb.append("Trash");
            sb.append(sep);
            sb.append(fileName);
            File testFile = new File(sb.toString());
            File fileParent = testFile.getParentFile();//返回的是File类型,可以调用exsit()等方法
            String fileParentPath = testFile.getParent();//返回的是String类型
            if (!fileParent.exists()) {
                fileParent.mkdirs();// 能创建多级目录
            }
            if (!testFile.exists()) {
                testFile.createNewFile();//有路径才能创建文件
            }
            String pathname=Paths.get("").toAbsolutePath().toString()+"\\src\\main\\resources\\static\\excelTemplate\\鸟类模板.xls";
            File mFile=new File(pathname);
            FileUtils.copyFile(mFile, testFile);
            String templateFileName = Paths.get("").toAbsolutePath().toString()+ "\\demo" + File.separator + "fill" + File.separator + "composite.xlsx";
            InputStream inputStream = new FileInputStream(testFile);
            Workbook workbook =new HSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            HSSFSheet hssfSheet = (HSSFSheet) sheet;
            int cRowNum=4;
            HSSFRow firstDataRow = hssfSheet.createRow(cRowNum);
            HSSFRow row = hssfSheet.getRow(0);
           // row.createCell(0);
            row.getCell(0).setCellValue(FirstCellText);
            row = hssfSheet.getRow(1);
            //row.createCell(0);
            row.getCell(0).setCellValue(weatherStr);

            //写入
            OutputStream out = null;
            try {
                // 创建工作簿 HSSFWorkbook
                // 创建工作表 HSSFSheet
                // 创建行 HSSFRow
                // 如果有合并单元格需求创建单元格范围地址 CellRangeAddress
                // 创建 HSSFCellStyle
                // 创建 HSSFCell

                // 输出到对应文件里面
                out = new FileOutputStream(testFile);
                // 将流写入工作簿中
                workbook.write(out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }
    public static void main(String[] args) throws IOException {
        //watetlevelExcelLocalFileTest();
        //spectralReflectance();
        //ObservationBirdExcelFill();
    }


}
