package cn.edu.hstc.util;

import cn.edu.hstc.framework.AjaxResult;
import cn.edu.hstc.pojo.Record;
import com.mysql.jdbc.log.LogUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ResourceUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExcelUtil {
    private static final int MEMORY_THRESHOLD = 2 * 1024 * 1000;  //临时保存解析出的数据限制大小2MB
    private static final long MAX_FILE_SIZE = 4 * 1024 * 1000;  //限制文件大小为4MB
    private static final long MAX_REQUEST_SIZE = 6 * 1024 * 1000;  //总文件大小为6MB

    protected static final Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

    //文件上传至服务器
    public static String upload(HttpServletRequest request, HttpServletResponse response) {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(MEMORY_THRESHOLD);  //设置缓冲区大小，默认10KB 超出将使用临时文件上传
        factory.setRepository(new File(System.getProperty("java.io.tmpdir"))); //设置临时文件路径。Tomcat系统默认临时目录为“<tomcat安装目录>/temp/”
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setFileSizeMax(MAX_FILE_SIZE); //设置单个文件的大小
        upload.setSizeMax(MAX_REQUEST_SIZE);  //设置总文件的大小
        upload.setHeaderEncoding("UTF-8"); //解决上传"文件名"的中文乱码
        String uploadPath = request.getServletContext().getRealPath("/upload");
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            System.out.println("目录或文件不存在! 创建目标目录：" + uploadPath);
            uploadDir.mkdir();
        }
        String filePath = null;
        try {
            List<FileItem> formItems = upload.parseRequest(request);
            for (FileItem item : formItems) {
                if (!item.isFormField()) {
                    System.out.println(item.getFieldName() + ":" + item.getName() + ":" + item.getContentType() + ":" + item.getSize());
                    String fileName = new File(item.getName()).getName();
                    System.out.println("文件名为：" + fileName);
                    //拼接上传路径。存放路径+上传的文件名
                    filePath = uploadPath + File.separator + fileName;
                    System.out.println("文件路径为：" + filePath);
                    File storeFile = new File(filePath);
                    item.write(storeFile);
                    item.delete(); //删除处理文件上传时生成的临时文件
                    System.out.println("文件上传成功");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filePath;
    }

    //在浏览器中下载某张试卷的所有学生的考试记录  HSSF为.xls   XSSF为.xlsx
    public static String OutExcel(HttpServletRequest request, HttpServletResponse response, List<Record> records) throws Exception {
        String message = "fail";
        String filePath = URLDecoder.decode(ResourceUtils.getURL("classpath:").getPath(), "UTF-8") + "resources/exportExcel";
        File fileLocation = new File(filePath);
        if (!fileLocation.exists()) {
            fileLocation.mkdirs();
        }
        String createExcelName = ProjectUtil.getUuid().substring(0, 24) + ".xlsx";
        String exportFilePath = filePath + File.separator + createExcelName;
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();
        sheet.setColumnWidth(1,20*256);
        sheet.setColumnWidth(2,20*256);
        sheet.setColumnWidth(4,20*256);
        workbook.setSheetName(0, "score");
        //创建表中第一行
        XSSFRow row1 = sheet.createRow(0);
        row1.setHeight((short)(24*20));
        //单元格格式
        XSSFCellStyle xssfCellStyleHead = setBorderStyle(workbook, 3, "30");
        XSSFCellStyle xssfCellStyleContent = setBorderStyle(workbook, 3, "20");
        /*XSSFCellStyle textStyle = workbook.createCellStyle();
        textStyle.setAlignment(HorizontalAlignment.CENTER);   //水平居中
        textStyle.setVerticalAlignment(VerticalAlignment.CENTER);  //垂直剧中*/
        XSSFCell cell0 = row1.createCell(0, XSSFCell.CELL_TYPE_STRING);
        cell0.setCellValue("#");
        cell0.setCellStyle(xssfCellStyleHead);
        XSSFCell cell1 = row1.createCell(1, XSSFCell.CELL_TYPE_STRING);
        cell1.setCellValue("学号");
        cell1.setCellStyle(xssfCellStyleHead);
        XSSFCell cell2 = row1.createCell(2, XSSFCell.CELL_TYPE_STRING);
        cell2.setCellValue("姓名");
        cell2.setCellStyle(xssfCellStyleHead);
        XSSFCell cell3 = row1.createCell(3, XSSFCell.CELL_TYPE_STRING);
        cell3.setCellValue("得分");
        cell3.setCellStyle(xssfCellStyleHead);
        XSSFCell cell4 = row1.createCell(4, XSSFCell.CELL_TYPE_STRING);
        cell4.setCellValue("考试状态");
        cell4.setCellStyle(xssfCellStyleHead);
        response.setContentType("text/html;charset=UTF-8");
        for (int j = 0; j < records.size(); j++) {
            Record record = records.get(j);
            XSSFRow row = sheet.createRow(j + 1);
            XSSFCell c0 = row.createCell(0, XSSFCell.CELL_TYPE_STRING);
            XSSFCell c1 = row.createCell(1, XSSFCell.CELL_TYPE_STRING);
            XSSFCell c2 = row.createCell(2, XSSFCell.CELL_TYPE_STRING);
            XSSFCell c3 = row.createCell(3, XSSFCell.CELL_TYPE_STRING);
            XSSFCell c4 = row.createCell(4, XSSFCell.CELL_TYPE_STRING);
            c0.setCellValue(j + 1);
            c0.setCellStyle(xssfCellStyleContent);
            c1.setCellValue(record.getStudent().getStuNum());
            c1.setCellStyle(xssfCellStyleContent);
            c2.setCellValue(record.getStudent().getName());
            c2.setCellStyle(xssfCellStyleContent);
            c3.setCellValue(record.getPoint());
            c3.setCellStyle(xssfCellStyleContent);
            c4.setCellValue(record.getState());
            c4.setCellStyle(xssfCellStyleContent);
        }
        //向指定文件名的文件写出数据，在指定文件夹中创建指定.xls文件
        FileOutputStream fOut = new FileOutputStream(exportFilePath);
        //将从数据库中导出的数据写入fOut创建的输出文件中
        workbook.write(fOut);
        fOut.flush(); //冲出所有流
        fOut.close();  //关闭输出流
        File f = new File(exportFilePath);  //读入即将导出的.xls文件
        if (f.exists() && f.isFile()) {
            //输入从数据库导出的excel文件中的数据流
            FileInputStream fis = new FileInputStream(f);
            URLEncoder.encode(f.getName(), "utf-8");
            //新建一个字节数组，数据中转站，临时缓冲区
            byte[] b = new byte[fis.available()];
            //将文件中的流读取到字节数组中
            fis.read(b); //返回类型是读取到的字节数
            response.setCharacterEncoding("utf-8");
            createExcelName = records.get(0).getExam().getPaper().getName() + "(" + records.get(0).getStudent().getClasses().getName() + "成绩单).xlsx";
            createExcelName = new String(createExcelName.getBytes("gb2312"), "ISO8859-1");
            response.setHeader("Content-Disposition", "attachment; filename=" + createExcelName + "");
            //在浏览器创建下载文件
            ServletOutputStream out = response.getOutputStream();
            //在浏览器即将下载的文件中写入导出文件(.xlsx)中的字节数据
            out.write(b); //写给response再由response写到浏览器
            out.flush();
            out.close();
            if (fis != null) {
                fis.close();
            }
            f.delete();   //删除缓存文件
            message = "success";
            logger.info("从数据库导出" + records.get(0).getStudent().getClasses().getName() + "的《" + records.get(0).getExam().getPaper().getName() + "》" + records.size() + "条成绩数据！");
        }
        return message;
    }

    /*读取.xls文件内容，并返回一个数据集合*/
    public static ArrayList<ArrayList<Object>> readExcel2003AsList(InputStream is) {
        try {
            ArrayList<ArrayList<Object>> rowList = new ArrayList<ArrayList<Object>>();
            ArrayList<Object> colList;
            HSSFWorkbook wb = new HSSFWorkbook(is);
            HSSFSheet sheet = wb.getSheetAt(0);
            HSSFRow row;
            HSSFCell cell;
            Object value = null;
            for (int i = sheet.getFirstRowNum() + 1, rowCount = 0; rowCount < sheet.getPhysicalNumberOfRows(); i++) {
                row = sheet.getRow(i);
                colList = new ArrayList<Object>();
                if (row == null) {
                    if (i != sheet.getPhysicalNumberOfRows()) {// 判断是否是最后一行
                        rowList.add(colList);
                    }
                    return rowList;
                } else {
                    rowCount++;
                }
                for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
                    cell = row.getCell(j);
                    if (cell == null || cell.getCellType() == HSSFCell.CELL_TYPE_BLANK) {
                        if (j != row.getLastCellNum()) {
                            colList.add("");
                        }
                        continue;
                    }
                    if (null != cell) {
                        switch (cell.getCellType()) {
                            case HSSFCell.CELL_TYPE_NUMERIC:
                                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                    value = sdf.format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue())).toString();
                                    break;
                                } else {
                                    Double d = cell.getNumericCellValue();
                                    DecimalFormat df = new DecimalFormat("#.##");
                                    value = df.format(d);
                                }
                                break;
                            case HSSFCell.CELL_TYPE_STRING:
                                value = cell.getStringCellValue();
                                break;
                            case HSSFCell.CELL_TYPE_BOOLEAN:
                                value = cell.getBooleanCellValue() + "";
                                break;
                            case HSSFCell.CELL_TYPE_FORMULA:
                                value = cell.getCellFormula() + "";
                                break;
                            case HSSFCell.CELL_TYPE_BLANK:
                                value = "";
                                break;
                            case HSSFCell.CELL_TYPE_ERROR:
                                value = "非法字符";
                                break;
                            default:
                                value = "未知类型";
                                break;
                        }

                    }
                    colList.add(value);
                }
                rowList.add(colList);
            }
            if (is != null) {
                is.close();
            }
            return rowList;
        } catch (Exception e) {
            System.out.println("exceptionXLS");
            return null;
        }
    }

    /*读取.xlsx文件内容，并返回一个数据集合*/
    public static ArrayList<ArrayList<Object>> readExcel2007(InputStream is) {
        try {
            ArrayList<ArrayList<Object>> rowList = new ArrayList<ArrayList<Object>>();
            ArrayList<Object> colList;
            XSSFWorkbook wb = new XSSFWorkbook(is);
            XSSFSheet sheet = wb.getSheetAt(0);
            XSSFRow row;
            XSSFCell cell;
            Object value = null;

            for (int i = sheet.getFirstRowNum() + 1, rowCount = 0; rowCount < sheet.getPhysicalNumberOfRows(); i++) {
                row = sheet.getRow(i);
                colList = new ArrayList<Object>();
                if (row == null) {
                    if (i != sheet.getPhysicalNumberOfRows()) {
                        rowList.add(colList);
                    }
                    return rowList;
                } else {
                    rowCount++;
                }
                for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
                    cell = row.getCell(j);
                    if (cell == null || cell.getCellType() == HSSFCell.CELL_TYPE_BLANK) {
                        if (j != row.getLastCellNum()) {
                            colList.add("");
                        }
                        continue;
                    }

                    if (null != cell) {
                        switch (cell.getCellType()) {
                            case HSSFCell.CELL_TYPE_NUMERIC:
                                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                    value = sdf.format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue())).toString();
                                    break;
                                } else {
                                    Double d = cell.getNumericCellValue();
                                    DecimalFormat df = new DecimalFormat("#.##");
                                    value = df.format(d);
                                }
                                break;

                            case HSSFCell.CELL_TYPE_STRING:
                                value = cell.getStringCellValue();
                                break;

                            case HSSFCell.CELL_TYPE_BOOLEAN:
                                value = cell.getBooleanCellValue() + "";
                                break;

                            case HSSFCell.CELL_TYPE_FORMULA:
                                value = cell.getCellFormula() + "";
                                break;

                            case HSSFCell.CELL_TYPE_BLANK:
                                value = "";
                                break;

                            case HSSFCell.CELL_TYPE_ERROR:
                                value = "非法字符";
                                break;

                            default:
                                value = "未知类型";
                                break;
                        }

                    }
                    colList.add(value);
                }
                rowList.add(colList);
            }
            if (is != null) {
                is.close();
            }
            return rowList;
        } catch (Exception e) {
            System.out.println("exceptionXLSX");
            return null;
        }
    }

    /**
     * 功能描述: 设置单元格样式
     */
    private static XSSFCellStyle setBorderStyle(XSSFWorkbook wb, int cellIndex, String backWidth){
        XSSFCellStyle cellStyle = wb.createCellStyle();
        XSSFFont font = null;
        switch (cellIndex){
            case 0:
                cellStyle.setBorderBottom(BorderStyle.DASHED); //下边框
                cellStyle.setBorderLeft(BorderStyle.DASHED);//左边框
                cellStyle.setBorderTop(BorderStyle.DASHED);//上边框
                cellStyle.setBorderRight(BorderStyle.DASHED);//右边框
                break;
            case 1:
                cellStyle.setBorderBottom(BorderStyle.MEDIUM); //下边框
                cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex()); //下边框颜色
                cellStyle.setBorderLeft(BorderStyle.MEDIUM_DASHED);//左边框
                cellStyle.setBorderTop(BorderStyle.MEDIUM);//上边框
                cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex()); //上边框颜色
                cellStyle.setBorderRight(BorderStyle.MEDIUM_DASHED);//右边框
                cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);//设置前景填充样式
                cellStyle.setFillForegroundColor(HSSFColor.BLACK.index);//前景填充色
                break;
            case 2:
                cellStyle.setBorderBottom(BorderStyle.MEDIUM); //下边框
                cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex()); //下边框颜色
                cellStyle.setBorderLeft(BorderStyle.DASHED);//左边框
                cellStyle.setBorderTop(BorderStyle.MEDIUM);//上边框
                cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex()); //上边框颜色
                cellStyle.setBorderRight(BorderStyle.DASHED);//右边框
                break;
            case 3:
                cellStyle.setBorderBottom(BorderStyle.MEDIUM); //下边框
                cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex()); //下边框颜色
                cellStyle.setBorderLeft(BorderStyle.DASHED);//左边框
                cellStyle.setBorderTop(BorderStyle.MEDIUM);//上边框
                cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex()); //上边框颜色
                cellStyle.setBorderRight(BorderStyle.DASHED);//右边框
                font = wb.createFont();
                font.setFontName("宋体");
                if("20".equals(backWidth)){
                    font.setFontHeightInPoints((short) 14);//设置字体大小
                }else if("30".equals(backWidth)){
                    font.setFontHeightInPoints((short) 16);//设置字体大小
                    font.setBold(true);//加粗
                }else if("40".equals(backWidth)){
                    font.setFontHeightInPoints((short) 18);//设置字体大小
                }else if("50".equals(backWidth)){
                    font.setFontHeightInPoints((short) 20);//设置字体大小
                }else if("60".equals(backWidth)){
                    font.setFontHeightInPoints((short) 22);//设置字体大小
                }else{
                    font.setFontHeightInPoints((short) 14);//设置字体大小
                }
                cellStyle.setFont(font);//选择需要用到的字体格式
                cellStyle.setAlignment(HorizontalAlignment.CENTER);//水平居中
                cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中
                break;
            case 4:
                cellStyle.setBorderBottom(BorderStyle.MEDIUM); //下边框
                cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex()); //下边框颜色
                cellStyle.setBorderLeft(BorderStyle.DASHED);//左边框
                cellStyle.setBorderTop(BorderStyle.MEDIUM);//上边框
                cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex()); //上边框颜色
                cellStyle.setBorderRight(BorderStyle.DASHED);//右边框
                font = wb.createFont();
                font.setFontName("宋体");
                font.setFontHeightInPoints((short) 14);//设置字体大小
                cellStyle.setFont(font);//选择需要用到的字体格式
                cellStyle.setAlignment(HorizontalAlignment.CENTER);//水平居中
                cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中
                cellStyle.setWrapText(true);//设置自动换行
                break;
            case 5:
                cellStyle.setBorderBottom(BorderStyle.MEDIUM); //下边框
                cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex()); //下边框颜色
                cellStyle.setBorderLeft(BorderStyle.DASHED);//左边框
                cellStyle.setBorderTop(BorderStyle.MEDIUM);//上边框
                cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex()); //上边框颜色
                cellStyle.setBorderRight(BorderStyle.DASHED);//右边框
                font = wb.createFont();
                font.setFontName("宋体");
                if("20".equals(backWidth)){
                    font.setFontHeightInPoints((short) 12);//设置字体大小
                }else if("30".equals(backWidth)){
                    font.setFontHeightInPoints((short) 14);//设置字体大小
                }else if("40".equals(backWidth)){
                    font.setFontHeightInPoints((short) 16);//设置字体大小
                }else if("50".equals(backWidth)){
                    font.setFontHeightInPoints((short) 18);//设置字体大小
                }else if("60".equals(backWidth)){
                    font.setFontHeightInPoints((short) 20);//设置字体大小
                }else {
                    font.setFontHeightInPoints((short) 12);//设置字体大小
                }
                cellStyle.setFont(font);//选择需要用到的字体格式
                cellStyle.setAlignment(HorizontalAlignment.CENTER);//水平居中
                cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中
                break;
            case 6:
                cellStyle.setBorderBottom(BorderStyle.MEDIUM); //下边框
                cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex()); //下边框颜色
                cellStyle.setBorderLeft(BorderStyle.DASHED);//左边框
                cellStyle.setBorderTop(BorderStyle.MEDIUM);//上边框
                cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex()); //上边框颜色
                cellStyle.setBorderRight(BorderStyle.DASHED);//右边框
                font = wb.createFont();
                font.setFontName("宋体");
                if(ObjectUtils.isEmpty(backWidth)  || "20".equals(backWidth)){
                    font.setFontHeightInPoints((short) 14);//设置字体大小
                }else{
                    font.setFontHeightInPoints((short) 16);//设置字体大小
                }
                cellStyle.setFont(font);//选择需要用到的字体格式
                cellStyle.setAlignment(HorizontalAlignment.CENTER);//水平居中
                cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中
                cellStyle.setWrapText(true);//设置自动换行
                cellStyle.setRotation((short)255); //设置文字竖向排列
                break;
            case 7:
                cellStyle.setBorderBottom(BorderStyle.MEDIUM); //下边框
                cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex()); //下边框颜色
                cellStyle.setBorderLeft(BorderStyle.DASHED);//左边框
                cellStyle.setBorderTop(BorderStyle.MEDIUM);//上边框
                cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex()); //上边框颜色
                cellStyle.setBorderRight(BorderStyle.DASHED);//右边框
                break;
            case 8:
                cellStyle.setBorderBottom(BorderStyle.MEDIUM); //下边框
                cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex()); //下边框颜色
                cellStyle.setBorderLeft(BorderStyle.DASHED);//左边框
                cellStyle.setBorderTop(BorderStyle.MEDIUM);//上边框
                cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex()); //上边框颜色
                cellStyle.setBorderRight(BorderStyle.DASHED);//右边框
                cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);//设置前景填充样式
                cellStyle.setFillForegroundColor(HSSFColor.BLACK.index);//前景填充色
                break;
            case 9:
                cellStyle.setBorderBottom(BorderStyle.DASHED); //下边框
                cellStyle.setBorderLeft(BorderStyle.DASHED);//左边框
                cellStyle.setBorderTop(BorderStyle.DASHED);//上边框
                cellStyle.setBorderRight(BorderStyle.DASHED);//右边框
                break;
            default:
                cellStyle.setBorderBottom(BorderStyle.DASHED); //下边框
                cellStyle.setBorderLeft(BorderStyle.DASHED);//左边框
                cellStyle.setBorderTop(BorderStyle.DASHED);//上边框
                cellStyle.setBorderRight(BorderStyle.DASHED);//右边框
                break;

        }
        return cellStyle;
    }
    /*public static String importTopicsExcel(InputStream is) {
        //集合的集合{{......},{......},{......}}
        List<ArrayList<Object>> list = readExcel2003AsList(is);
        int insert_num = 0;
        for (int i = 0, j = list.size(); i < j; i++) {
            //得到集合中的一个集合，也就是表中的一行数据
            List<Object> row = list.get(i);
            //将一行数据挨个写入map集合{{name:..},{sex:...},{position:...},...}
            if (row.size() == 0) {
                break;
            }
            String course = row.get(0).toString();
            String stage = row.get(1).toString();
            int stage_id = new ExStageDaoImpl().findStage(course, stage).getId();
            int type_id = Integer.parseInt(row.get(2).toString().substring(0, 1));
            int level_id = Integer.parseInt(row.get(3).toString().substring(0, 1)) + 1;
            String question = row.get(4).toString();
            String optA = "A."+row.get(5).toString();
            String optB = "B."+row.get(6).toString();
            String optC = "C."+row.get(7).toString();
            String optD = "D."+row.get(8).toString();
            String optE = "E."+row.get(9).toString();
            String optF = "F."+row.get(10).toString();
            String optG = "G."+row.get(11).toString();
            String opts = optA + "\n" + optB + "\n" + optC + "\n" + optD + "\n";
            if (!row.get(9).toString().equals("")) {
                opts = opts + optE + "\n";
            }
            if (!row.get(10).toString().equals("")) {
                opts = opts + optF + "\n";
            }
            if (!row.get(11).toString().equals("")) {
                opts = opts + optG + "\n";
            }
            String answer = row.get(12).toString();
            String analysis = row.get(13).toString();
            if(analysis.equals("")){
                analysis="无";
            }
            if(course==""||stage==""||row.get(2).toString()==""||row.get(3).toString()==""||question==""||optA=="A."||optB=="B."||optC=="C."||optD=="D."||answer==""){
                continue;
            }
            Topic topic = new Topic(question, opts, answer, analysis, type_id, level_id, stage_id);
            new TopicDaoImpl().insertTopic(topic);
            insert_num++;
        }
        System.out.println("从excel中导出试题数据并插入数据库" + insert_num + "条数据！");
        if (insert_num == 0) {
            return "FALL";
        }
        return "OK";
    }*/
}
