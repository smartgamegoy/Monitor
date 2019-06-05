package com.jetec.Monitor.MPChart.CreatPDF;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class HeaderHandler extends PdfPageEventHelper {

    private Phrase header;

    public void setHeader(Phrase header) {
        this.header = header;
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte canvas = writer.getDirectContentUnder();// 220 820
        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, header, 45, 810, 0);
    }
}
