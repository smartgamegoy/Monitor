package com.jetec.Monitor.SupportFunction.PDF;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.jetec.Monitor.SupportFunction.LogMessage;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class FooterHandler extends PdfPageEventHelper {

    private String TAG = "FooterHandler";
    private Font ffont = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
    private LogMessage logMessage = new LogMessage();
    private Phrase header = new Phrase();
    private List<Integer> setColumn = new ArrayList<>();
    private List<Integer> setPage = new ArrayList<>();
    private int count = 0, devicetitle = 0, allpage = 0;

    public void setallpage(List<String> pdftitle, List<Integer> device_count, ArrayList<List<String>> recordList){
        try {
            String address = "";
            int getcount = 0;
            setColumn.clear();
            setPage.clear();

            for(int i = 0; i < recordList.size(); i++){
                List<String> dataList = new ArrayList<>();
                dataList.clear();
                dataList = recordList.get(i);
                if(address.matches("")){
                    address = dataList.get(0);
                    JSONArray jsonArray = new JSONArray(dataList.get(3));
                    for(int j = 0; j < jsonArray.length(); j = j + 4){
                        count++;
                    }
                    if(count <= 3){
                        if(device_count.get(getcount) % 100 == 0){
                            devicetitle = device_count.get(getcount) / 100;
                        }
                        else {
                            devicetitle = (device_count.get(getcount) / 100) + 1;
                        }
                        getcount++;
                        setPage.add(devicetitle);
                    }
                    else {
                        if(device_count.get(getcount) % 50 == 0){
                            devicetitle = device_count.get(getcount) / 50;
                        }
                        else {
                            devicetitle = (device_count.get(getcount) / 50) + 1;
                        }
                        getcount++;
                        setPage.add(devicetitle);
                    }
                    setColumn.add(count);
                    count = 0;
                }
                else {
                    if(!dataList.get(0).matches(address)){
                        address = dataList.get(0);
                        JSONArray jsonArray = new JSONArray(dataList.get(3));
                        for(int j = 0; j < jsonArray.length(); j = j + 4){
                            count++;
                        }
                        if(count <= 3){
                            if(device_count.get(getcount) % 100 == 0){
                                devicetitle = device_count.get(getcount) / 100;
                            }
                            else {
                                devicetitle = (device_count.get(getcount) / 100) + 1;
                            }
                            getcount++;
                            setPage.add(devicetitle);
                        }
                        else {
                            if(device_count.get(getcount) % 50 == 0){
                                devicetitle = device_count.get(getcount) / 50;
                            }
                            else {
                                devicetitle = (device_count.get(getcount) / 50) + 1;
                            }
                            getcount++;
                            setPage.add(devicetitle);
                        }
                        setColumn.add(count);
                        count = 0;
                    }
                }
            }

            for(int i = 0; i < setPage.size(); i++){
                allpage =  allpage + setPage.get(i);
            }

            logMessage.showmessage(TAG,"allpage = " + allpage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onEndPage(PdfWriter writer, Document document) {

        PdfContentByte cb = writer.getDirectContent();

        int pageNumber = writer.getPageNumber();
        Phrase footer = new Phrase(String.valueOf(pageNumber) + "/" + allpage, ffont);
        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                footer,
                (document.right() - document.left()) / 2 + document.leftMargin(),
                document.bottom() - 10, 0);
    }
}
