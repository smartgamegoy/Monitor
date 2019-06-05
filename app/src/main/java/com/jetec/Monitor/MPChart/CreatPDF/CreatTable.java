package com.jetec.Monitor.MPChart.CreatPDF;

import android.content.Context;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.jetec.Monitor.R;
import com.jetec.Monitor.SupportFunction.GetUnit;
import com.jetec.Monitor.SupportFunction.LogMessage;
import com.jetec.Monitor.SupportFunction.Value;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreatTable {

    private String TAG = "CreatTable";
    private ArrayList<List<String>> valueList;
    private ArrayList<String> timeList;
    private Context context;
    private int count = Value.model_num, page;
    private GetUnit getUnit = new GetUnit();
    private Font chineseFont;
    private LogMessage logMessage = new LogMessage();

    public void setList(Context context, ArrayList<List<String>> valueList, ArrayList<String> timeList, int page) {
        try {
            this.valueList = valueList;
            this.timeList = timeList;
            this.context = context;
            this.page = page;
            BaseFont bfChinese;
            bfChinese = BaseFont.createFont("STSongStd-Light", "UniGB-UTF16-H", true);
            chineseFont = new Font(bfChinese, 6, Font.NORMAL);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PdfPTable createTable() {
        List<String> celltitle = new ArrayList<>();
        celltitle.clear();
        PdfPTable table;
        float fntSize, lineSpacing;
        fntSize = 6.0f;
        lineSpacing = 10f;
        celltitle.add(context.getString(R.string.pdftime));
        for (int i = 0; i < count; i++) {
            celltitle.add(getUnit.getName(context, Value.getviewlist.get(i * 4)));
        }

        logMessage.showmessage(TAG, "celltitle = " + celltitle);

        if (count > 3) {
            table = new PdfPTable((count + 2) * 2);
            for (int i = 0; i < page; i++) {
                for (int j = 0; j < 2; j++) {
                    for (int k = 0; k < celltitle.size(); k++) {
                        PdfPCell cell = new PdfPCell(new Paragraph(new Phrase(lineSpacing, celltitle.get(k), chineseFont)));
                        if (k == 0) {
                            cell.setColspan(2);
                            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        }
                        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                        table.addCell(cell);
                    }
                }
                for (int j = (150 * i); j < 75 + (150 * i); j++) {
                    for (int k = 0; k < 2; k++) {
                        if ((j + (k * 75)) < timeList.size()) {
                            PdfPCell cell = new PdfPCell(new Paragraph(new Phrase(lineSpacing, timeList.get((j + (k * 75))),
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell.setColspan(2);
                            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            table.addCell(cell);
                        } else {
                            PdfPCell cell = new PdfPCell(new Paragraph(new Phrase(lineSpacing, "",
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell.setColspan(2);
                            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                            table.addCell(cell);
                        }
                        for (int l = 0; l < valueList.size(); l++) {
                            if ((j + (k * 75)) < valueList.get(l).size()) {  //saveList.get(l).get((j + (k * 75))
                                PdfPCell cell;
                                byte[] bytes = valueList.get(l).get((j + (k * 75))).getBytes();
                                if(bytes.length > 7){
                                    cell = new PdfPCell(new Paragraph(new Phrase(lineSpacing, "-",
                                            FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                                }
                                else {
                                    cell = new PdfPCell(new Paragraph(new Phrase(lineSpacing, valueList.get(l).get((j + (k * 75))),
                                            FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                                }
                                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                                table.addCell(cell);
                            } else {
                                PdfPCell cell = new PdfPCell(new Paragraph(new Phrase(lineSpacing, "",
                                        FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                                table.addCell(cell);
                            }
                        }
                    }
                }
            }
        } else {
            table = new PdfPTable((count + 2) * 4);
            for (int i = 0; i < page; i++) {
                for (int j = 0; j < 4; j++) {
                    for (int k = 0; k < celltitle.size(); k++) {
                        PdfPCell cell = new PdfPCell(new Paragraph(new Phrase(lineSpacing, celltitle.get(k), chineseFont)));
                        if (k == 0) {
                            cell.setColspan(2);
                            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        }
                        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                        table.addCell(cell);
                    }
                }
                for (int j = (300 * i); j < 75 + (300 * i); j++) {
                    for (int k = 0; k < 4; k++) {
                        if ((j + (k * 75)) < timeList.size()) {
                            PdfPCell cell = new PdfPCell(new Paragraph(new Phrase(lineSpacing, timeList.get((j + (k * 75))),
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell.setColspan(2);
                            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            table.addCell(cell);
                        } else {
                            PdfPCell cell = new PdfPCell(new Paragraph(new Phrase(lineSpacing, "",
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell.setColspan(2);
                            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                            table.addCell(cell);
                        }
                        for (int l = 0; l < valueList.size(); l++) {
                            if ((j + (k * 75)) < valueList.get(l).size()) {  //saveList.get(l).get((j + (k * 75))
                                PdfPCell cell;
                                byte[] bytes = valueList.get(l).get((j + (k * 75))).getBytes();
                                if(bytes.length > 7){
                                    cell = new PdfPCell(new Paragraph(new Phrase(lineSpacing, "-",
                                            FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                                }
                                else {
                                    cell = new PdfPCell(new Paragraph(new Phrase(lineSpacing, valueList.get(l).get((j + (k * 75))),
                                            FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                                }
                                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                                table.addCell(cell);
                            } else {
                                PdfPCell cell = new PdfPCell(new Paragraph(new Phrase(lineSpacing, "",
                                        FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                                table.addCell(cell);
                            }
                        }
                    }
                }
            }
        }
        return table;
    }
}
