package com.task.utils;

import com.task.bean.Content;
import com.task.bean.ContentItem;
import com.task.bean.Task;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by blanke on 2017/4/23.
 */
public class TaskExportUtils {

    private static void addCell(HSSFRow row, int index, String text, HSSFCellStyle style) {
        HSSFCell cell = row.createCell(index);
        cell.setCellValue(text);
        cell.setCellStyle(style);
    }

    public static HSSFWorkbook exportTaskExcel(Task task) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(task.getTitle());
        //设置为居中加粗
        HSSFCellStyle style = workbook.createCellStyle();
        HSSFFont font = workbook.createFont();
        font.setBold(true);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setFont(font);

        //设置列宽，setColumnWidth的第二个参数要乘以256，这个参数的单位是1/256个字符宽度
        sheet.setColumnWidth(2, 12 * 256);
        sheet.setColumnWidth(3, 17 * 256);

        //head
        List<String> heads = new ArrayList<>();
        heads.add("序号");
        heads.add("用户");
        heads.add("提交时间");
        heads.add("审核状态");
        for (com.task.bean.Field field : task.getFields()) {
            heads.add(field.getName());
        }

        //设置基本信息
        HSSFRow row = sheet.createRow(0);
        addCell(row, 0, task.getTitle(), style);
        CellRangeAddress address = new CellRangeAddress(0, 0, 0, heads.size() - 1);
        sheet.addMergedRegion(address);

        row = sheet.createRow(1);
        addCell(row, 0, task.getDescription(), style);
        address = new CellRangeAddress(1, 1, 0, heads.size() - 1);
        sheet.addMergedRegion(address);

        row = sheet.createRow(2);
        addCell(row, 0, "发布时间:", style);
        addCell(row, 1, DateUtils.formatData(task.getPublishTime()), style);
        address = new CellRangeAddress(2, 2, 1, 2);
        sheet.addMergedRegion(address);
        row = sheet.createRow(3);
        addCell(row, 0, "截止时间:", style);
        addCell(row, 1, DateUtils.formatData(task.getDeadlineTime()), style);
        address = new CellRangeAddress(3, 3, 1, 2);
        sheet.addMergedRegion(address);

        //添加 head
        row = sheet.createRow(4);
        for (int i = 0; i < heads.size(); i++) {
            addCell(row, i, heads.get(i), style);
        }
        //content
        Set<Content> contents = task.getContents();
        int rowIndex = 5;
        int i = rowIndex;
        for (Content content : contents) {
            HSSFRow rowItem = sheet.createRow(i);
            addCell(rowItem, 0, (i - 5 + 1) + "", style);//序号
            addCell(rowItem, 1, content.getUser().getNickName(), style);//用户
            addCell(rowItem, 2, DateUtils.formatData(content.getUpdatedAt()), style);//提交时间
            addCell(rowItem, 3, content.getStateStr(), style);//审核状态
            //按照顺序排列,可以优化
            int j = 4;
            for (com.task.bean.Field field : task.getFields()) {
                for (ContentItem contentItem : content.getItems()) {
                    if (contentItem.getField().equals(field)) {
                        addCell(rowItem, j++, contentItem.getValue(), style);
                        break;
                    }
                }
            }
            i++;
        }
        return workbook;
    }
}
