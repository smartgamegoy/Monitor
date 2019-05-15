package com.jetec.Monitor.SupportFunction.PDF;

/**
 * 針對接收到的每個裝置的資料做解析
 * 每個裝置內有幾列數據流
 * 1-3列內的數據流可放左右各50列資料為一頁
 * 4-6列內的數據流只放單一50行資料為一頁
 * 每頁的PDF放置最多50行資料
 * 每個裝置的資料須分開不可混雜
 * 每個裝置的資料PDF需合併在同一份
 */

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.jetec.Monitor.SupportFunction.LogMessage;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HeaderHandler extends PdfPageEventHelper {

    private String TAG = "HeaderHandler";
    private LogMessage logMessage = new LogMessage();
    private List<String> setName = new ArrayList<>();
    private List<String> setAddress = new ArrayList<>();
    private List<Integer> setColumn = new ArrayList<>();
    private List<Integer> setPage = new ArrayList<>();
    private Font nameFont, addressFont;
    private int count = 0, page = 0, devicetitle = 0, writter = 0;

    public void setHeader(List<String> pdftitle, List<Integer> device_count, ArrayList<List<String>> recordList) {
        try {
            String address = "";
            int getcount = 0;
            setColumn.clear();
            setPage.clear();
            setName.clear();
            setAddress.clear();

            BaseFont bfChinese = null;
            bfChinese = BaseFont.createFont("STSongStd-Light", "UniGB-UTF16-H", true);
            nameFont = new Font(bfChinese, 12, Font.NORMAL);
            addressFont = new Font(bfChinese, 6, Font.NORMAL);

            for(int i = 0; i < recordList.size(); i++){
                List<String> dataList = new ArrayList<>();
                dataList.clear();
                dataList = recordList.get(i);
                if(address.matches("")){
                    address = dataList.get(0);
                    setAddress.add(dataList.get(0));
                    setName.add(dataList.get(1));
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
                        setAddress.add(dataList.get(0));
                        setName.add(dataList.get(1));
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
            logMessage.showmessage(TAG,"setColumn = " + setColumn);
            logMessage.showmessage(TAG,"setPage = " + setPage);
            logMessage.showmessage(TAG,"setName = " + setName);
            logMessage.showmessage(TAG,"setAddress = " + setAddress);
        } catch (DocumentException | IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte canvas = writer.getDirectContentUnder();// 220 820
        Phrase header = new Phrase();

        Phrase name = new Phrase(setName.get(writter), nameFont);
        Phrase address = new Phrase(setAddress.get(writter), addressFont);

        header.add(name);
        header.add(address);
        //Phrase footer = new Phrase(String.valueOf(pageNumber) + "/" + allpage, ffont);

        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, header, 45, 810, 0);

        writter++;
    }
}
