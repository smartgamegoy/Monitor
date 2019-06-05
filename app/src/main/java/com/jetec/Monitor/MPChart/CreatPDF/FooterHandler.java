package com.jetec.Monitor.MPChart.CreatPDF;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class FooterHandler extends PdfPageEventHelper {
    private Font ffont = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);

    protected int allpage;

    public void setallpage(int allpage){
        this.allpage = allpage;
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
