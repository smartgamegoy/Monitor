package com.jetec.Monitor.SupportFunction.PDF;

import android.annotation.SuppressLint;
import android.content.Context;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.jetec.Monitor.R;
import com.jetec.Monitor.SupportFunction.LogMessage;
import com.jetec.Monitor.SupportFunction.SQL.AlertRecord;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CreatTable {

    private Context context;
    private String TAG = "CreatTable";
    private LogMessage logMessage = new LogMessage();
    private int calculate = 0;

    @SuppressLint("UseSparseArrays")
    private Map<Integer, List<String>> timeList = new HashMap<>();
    @SuppressLint("UseSparseArrays")
    private Map<Integer, List<String>> aValue = new HashMap<>();
    @SuppressLint("UseSparseArrays")
    private Map<Integer, List<String>> bValue = new HashMap<>();
    @SuppressLint("UseSparseArrays")
    private Map<Integer, List<String>> cValue = new HashMap<>();
    @SuppressLint("UseSparseArrays")
    private Map<Integer, List<String>> dValue = new HashMap<>();
    @SuppressLint("UseSparseArrays")
    private Map<Integer, List<String>> eValue = new HashMap<>();
    @SuppressLint("UseSparseArrays")
    private Map<Integer, List<String>> fValue = new HashMap<>();

    private List<Integer> setColumn = new ArrayList<>();
    private List<Integer> setPage = new ArrayList<>();
    private List<String> setUnit = new ArrayList<>();

    public void setList(Context context, List<String> addresslist) {
        this.context = context;
        AlertRecord alertRecord = new AlertRecord(context);
        try {
            timeList.clear();
            aValue.clear();
            bValue.clear();
            cValue.clear();
            dValue.clear();
            eValue.clear();
            fValue.clear();
            setUnit.clear();
            setColumn.clear();
            setPage.clear();

            for (int i = 0; i < addresslist.size(); i++) {
                ArrayList<List<String>> newList = new ArrayList<>();
                newList.clear();
                newList = alertRecord.getRecordList(addresslist.get(i));

                List<String> setTime = new ArrayList<>();
                setTime.clear();

                List<String> setaList = new ArrayList<>();
                setaList.clear();

                List<String> setbList = new ArrayList<>();
                setbList.clear();

                List<String> setcList = new ArrayList<>();
                setcList.clear();

                List<String> setdList = new ArrayList<>();
                setdList.clear();

                List<String> seteList = new ArrayList<>();
                seteList.clear();

                List<String> setfList = new ArrayList<>();
                setfList.clear();

                List<String> getColumn = new ArrayList<>();
                getColumn.clear();
                getColumn = newList.get(0);
                JSONArray getJson = new JSONArray(getColumn.get(3));
                int Column = getJson.length() / 4;
                setColumn.add(Column);
                logMessage.showmessage(TAG, "getJson = " + getJson);
                for (int m = 0; m < getJson.length(); m = m + 4) {
                    setUnit.add(getJson.get(m).toString());
                }

                for (int j = 0; j < newList.size(); j++) {
                    List<String> getList = new ArrayList<>();
                    getList.clear();
                    getList = newList.get(j);
                    setTime.add(getList.get(1));
                    JSONArray jsonArray = new JSONArray(getList.get(3));
                    int k = jsonArray.length() / 4;

                    int devicetitle;
                    switch (k) {
                        case 1: {
                            if (newList.size() % 100 == 0) {
                                devicetitle = newList.size() / 100;
                            } else {
                                devicetitle = (newList.size() / 100) + 1;
                            }
                            setPage.add(devicetitle);
                            setaList.add(jsonArray.get(1).toString());
                        }
                        break;
                        case 2: {
                            if (newList.size() % 100 == 0) {
                                devicetitle = newList.size() / 100;
                            } else {
                                devicetitle = (newList.size() / 100) + 1;
                            }
                            setPage.add(devicetitle);
                            setaList.add(jsonArray.get(1).toString());
                            setbList.add(jsonArray.get(5).toString());
                        }
                        break;
                        case 3: {
                            if (newList.size() % 100 == 0) {
                                devicetitle = newList.size() / 100;
                            } else {
                                devicetitle = (newList.size() / 100) + 1;
                            }
                            setPage.add(devicetitle);
                            setaList.add(jsonArray.get(1).toString());
                            setbList.add(jsonArray.get(5).toString());
                            setcList.add(jsonArray.get(9).toString());
                        }
                        break;
                        case 4: {
                            setaList.add(jsonArray.get(1).toString());
                            setbList.add(jsonArray.get(5).toString());
                            setcList.add(jsonArray.get(9).toString());
                            setdList.add(jsonArray.get(13).toString());
                        }
                        break;
                        case 5: {
                            setaList.add(jsonArray.get(1).toString());
                            setbList.add(jsonArray.get(5).toString());
                            setcList.add(jsonArray.get(9).toString());
                            setdList.add(jsonArray.get(13).toString());
                            seteList.add(jsonArray.get(17).toString());
                        }
                        break;
                        case 6: {
                            setaList.add(jsonArray.get(1).toString());
                            setbList.add(jsonArray.get(5).toString());
                            setcList.add(jsonArray.get(9).toString());
                            setdList.add(jsonArray.get(13).toString());
                            seteList.add(jsonArray.get(17).toString());
                            setfList.add(jsonArray.get(21).toString());
                        }
                        break;
                        default:
                            break;
                    }
                }

                if (setTime.size() > 0) {
                    timeList.put(i, setTime);
                }
                if (setaList.size() > 0) {
                    aValue.put(i, setaList);
                }
                if (setbList.size() > 0) {
                    bValue.put(i, setbList);
                }
                if (setcList.size() > 0) {
                    cValue.put(i, setcList);
                }
                if (setdList.size() > 0) {
                    dValue.put(i, setdList);
                }
                if (seteList.size() > 0) {
                    eValue.put(i, seteList);
                }
                if (setfList.size() > 0) {
                    fValue.put(i, setfList);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<Integer> getColumn() {
        return setColumn;
    }

    public PdfPTable createTable(int geti){

        String a, b, c, d, e, f;
        float fntSize, lineSpacing;
        fntSize = 8.0f;
        lineSpacing = 10f;

        if (setColumn.get(geti) == 1) {
            calculate = calculate + setColumn.get(geti);
            PdfPTable table = new PdfPTable((setColumn.get(geti) + 2) * 2);
            PdfPCell cell, cell1;

            a = getunit(String.valueOf(setUnit.get(calculate - 1)));

            for (int j = 0; j < setPage.get(geti); j++) {
                for (int k = 0; k < 2; k++) {
                    cell = new PdfPCell(new Paragraph(new Phrase(lineSpacing, context.getString(R.string.pdfdate),
                            FontFactory.getFont(FontFactory.HELVETICA_BOLD, fntSize))));
                    cell.setColspan(2);
                    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                    cell.setMinimumHeight(18);
                    table.addCell(cell);

                    cell1 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, a,
                            FontFactory.getFont(FontFactory.HELVETICA_BOLD, fntSize))));
                    cell1.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                    cell1.setMinimumHeight(18);
                    table.addCell(cell1);
                }

                for (int k = (100 * j); k < 50 + (100 * j); k++) {
                    for (int l = 0; l < 2; l++) {
                        if ((k + (l * 50)) < Objects.requireNonNull(timeList.get(geti)).size()) {
                            cell = new PdfPCell(new Paragraph(new Phrase(lineSpacing, Objects.requireNonNull(timeList.get(geti)).get(k + (l * 50)),
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell.setColspan(2);
                            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell.setMinimumHeight((float) (14.6));
                            table.addCell(cell);
                        } else {
                            cell = new PdfPCell(new Paragraph(new Phrase(lineSpacing, "",
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell.setColspan(2);
                            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell.setMinimumHeight((float) (14.6));
                            table.addCell(cell);
                        }

                        if ((k + (l * 75)) < Objects.requireNonNull(aValue.get(geti)).size()) {
                            cell1 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, Objects.requireNonNull(aValue.get(geti)).get(k + (l * 50)),
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell1.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell1.setMinimumHeight((float) (14.6));
                            table.addCell(cell1);
                        } else {
                            cell1 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, "",
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell1.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell1.setMinimumHeight((float) (14.6));
                            table.addCell(cell1);
                        }
                    }
                }
            }
            return table;
        } else if (setColumn.get(geti) == 2) {
            calculate = calculate + setColumn.get(geti);
            PdfPTable table = new PdfPTable((setColumn.get(geti) + 2) * 2);
            PdfPCell cell, cell1, cell2;

            a = getunit(setUnit.get(calculate - 2));
            b = getunit(setUnit.get(calculate - 1));

            for (int j = 0; j < setPage.get(geti); j++) {
                for (int k = 0; k < 2; k++) {
                    cell = new PdfPCell(new Paragraph(new Phrase(lineSpacing, context.getString(R.string.pdfdate),
                            FontFactory.getFont(FontFactory.HELVETICA_BOLD, fntSize))));
                    cell.setColspan(2);
                    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                    cell.setMinimumHeight(18);
                    table.addCell(cell);

                    cell1 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, a,
                            FontFactory.getFont(FontFactory.HELVETICA_BOLD, fntSize))));
                    cell1.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                    cell1.setMinimumHeight(18);
                    table.addCell(cell1);

                    cell2 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, b,
                            FontFactory.getFont(FontFactory.HELVETICA_BOLD, fntSize))));
                    cell2.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                    cell2.setMinimumHeight(18);
                    table.addCell(cell2);
                }

                for (int k = (100 * j); k < 50 + (100 * j); k++) {
                    for (int l = 0; l < 2; l++) {
                        if ((k + (l * 50)) < Objects.requireNonNull(timeList.get(geti)).size()) {
                            cell = new PdfPCell(new Paragraph(new Phrase(lineSpacing, Objects.requireNonNull(timeList.get(geti)).get(k + (l * 50)),
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell.setColspan(2);
                            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell.setMinimumHeight((float) (14.6));
                            table.addCell(cell);
                        } else {
                            cell = new PdfPCell(new Paragraph(new Phrase(lineSpacing, "",
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell.setColspan(2);
                            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell.setMinimumHeight((float) (14.6));
                            table.addCell(cell);
                        }
                        if ((k + (l * 50)) < Objects.requireNonNull(aValue.get(geti)).size()) {
                            cell1 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, Objects.requireNonNull(aValue.get(geti)).get(k + (l * 50)),
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell1.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell1.setMinimumHeight((float) (14.6));
                            table.addCell(cell1);
                        } else {
                            cell1 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, "",
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell1.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell1.setMinimumHeight((float) (14.6));
                            table.addCell(cell1);
                        }

                        if ((k + (l * 50)) < Objects.requireNonNull(bValue.get(geti)).size()) {
                            cell2 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, Objects.requireNonNull(bValue.get(geti)).get(k + (l * 50)),
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell2.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell2.setMinimumHeight((float) (14.6));
                            table.addCell(cell2);
                        } else {
                            cell2 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, "",
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell2.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell2.setMinimumHeight((float) (14.6));
                            table.addCell(cell2);
                        }
                    }
                }
            }
            return table;
        } else if (setColumn.get(geti) == 3) {
            calculate = calculate + setColumn.get(geti);
            PdfPTable table = new PdfPTable((setColumn.get(geti) + 2) * 2);
            PdfPCell cell, cell1, cell2, cell3;

            a = getunit(String.valueOf(setUnit.get(calculate - 3)));
            b = getunit(String.valueOf(setUnit.get(calculate - 2)));
            c = getunit(String.valueOf(setUnit.get(calculate - 1)));

            for (int j = 0; j < setPage.get(geti); j++) {
                for (int k = 0; k < 2; k++) {
                    cell = new PdfPCell(new Paragraph(new Phrase(lineSpacing, context.getString(R.string.pdfdate),
                            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7.5f))));
                    cell.setColspan(2);
                    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                    cell.setMinimumHeight(18);
                    table.addCell(cell);

                    cell1 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, a,
                            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7.5f))));
                    cell1.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                    cell1.setMinimumHeight(18);
                    table.addCell(cell1);

                    cell2 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, b,
                            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7.5f))));
                    cell2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                    cell2.setMinimumHeight(18);
                    table.addCell(cell2);

                    cell3 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, c,
                            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7.5f))));
                    cell3.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                    cell3.setMinimumHeight(18);
                    table.addCell(cell3);
                }

                for (int k = (100 * j); k < 50 + (100 * j); k++) {
                    for (int l = 0; l < 2; l++) {
                        if ((k + (l * 50)) < Objects.requireNonNull(timeList.get(geti)).size()) {
                            cell = new PdfPCell(new Paragraph(new Phrase(lineSpacing, Objects.requireNonNull(timeList.get(geti)).get(k + (l * 50)),
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell.setColspan(2);
                            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell.setMinimumHeight((float) (14.6));
                            table.addCell(cell);
                        } else {
                            cell = new PdfPCell(new Paragraph(new Phrase(lineSpacing, "",
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell.setColspan(2);
                            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell.setMinimumHeight((float) (14.6));
                            table.addCell(cell);
                        }

                        if ((k + (l * 50)) < Objects.requireNonNull(aValue.get(geti)).size()) {
                            cell1 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, Objects.requireNonNull(aValue.get(geti)).get(k + (l * 50)),
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell1.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell1.setMinimumHeight((float) (14.6));
                            table.addCell(cell1);
                        } else {
                            cell1 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, "",
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell1.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell1.setMinimumHeight((float) (14.6));
                            table.addCell(cell1);
                        }

                        if ((k + (l * 50)) < Objects.requireNonNull(bValue.get(geti)).size()) {
                            cell2 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, Objects.requireNonNull(bValue.get(geti)).get(k + (l * 50)),
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell2.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell2.setMinimumHeight((float) (14.6));
                            table.addCell(cell2);
                        } else {
                            cell2 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, "",
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell2.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell2.setMinimumHeight((float) (14.6));
                            table.addCell(cell2);
                        }

                        if ((k + (l * 50)) < Objects.requireNonNull(cValue.get(geti)).size()) {
                            cell3 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, Objects.requireNonNull(cValue.get(geti)).get(k + (l * 50)),
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell3.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell3.setMinimumHeight((float) (14.6));
                            table.addCell(cell3);
                        } else {
                            cell3 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, "",
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell3.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell3.setMinimumHeight((float) (14.6));
                            table.addCell(cell3);
                        }
                    }
                }
            }
            return table;
        } else if (setColumn.get(geti) == 4) {
            calculate = calculate + setColumn.get(geti);
            PdfPTable table = new PdfPTable((setColumn.get(geti) + 2));
            PdfPCell cell, cell1, cell2, cell3, cell4;

            a = getunit(String.valueOf(setUnit.get(calculate - 4)));
            b = getunit(String.valueOf(setUnit.get(calculate - 3)));
            c = getunit(String.valueOf(setUnit.get(calculate - 2)));
            d = getunit(String.valueOf(setUnit.get(calculate - 1)));

            for (int j = 0; j < setPage.get(geti); j++) {
                cell = new PdfPCell(new Paragraph(new Phrase(lineSpacing, context.getString(R.string.pdfdate),
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, fntSize))));
                cell.setColspan(2);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                cell.setMinimumHeight(18);
                table.addCell(cell);

                cell1 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, a,
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, fntSize))));
                cell1.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                cell1.setMinimumHeight(18);
                table.addCell(cell1);

                cell2 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, b,
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, fntSize))));
                cell2.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                cell2.setMinimumHeight(18);
                table.addCell(cell2);

                cell3 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, c,
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, fntSize))));
                cell3.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                cell3.setMinimumHeight(18);
                table.addCell(cell3);

                cell4 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, d,
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, fntSize))));
                cell4.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                cell4.setMinimumHeight(18);
                table.addCell(cell4);

                for (int k = (100 * j); k < 50 + (100 * j); k++) {
                    for (int l = 0; l < 2; l++) {
                        if ((k + (l * 50)) < Objects.requireNonNull(timeList.get(geti)).size()) {
                            cell = new PdfPCell(new Paragraph(new Phrase(lineSpacing, Objects.requireNonNull(timeList.get(geti)).get(k + (l * 50)),
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell.setColspan(2);
                            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell.setMinimumHeight((float) (14.6));
                            table.addCell(cell);
                        } else {
                            cell = new PdfPCell(new Paragraph(new Phrase(lineSpacing, "",
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell.setColspan(2);
                            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell.setMinimumHeight((float) (14.6));
                            table.addCell(cell);
                        }

                        if ((k + (l * 50)) < Objects.requireNonNull(aValue.get(geti)).size()) {
                            cell1 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, Objects.requireNonNull(aValue.get(geti)).get(k + (l * 50)),
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell1.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell1.setMinimumHeight((float) (14.6));
                            table.addCell(cell1);
                        } else {
                            cell1 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, "",
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell1.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell1.setMinimumHeight((float) (14.6));
                            table.addCell(cell1);
                        }

                        if ((k + (l * 50)) < Objects.requireNonNull(bValue.get(geti)).size()) {
                            cell2 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, Objects.requireNonNull(bValue.get(geti)).get(k + (l * 50)),
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell2.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell2.setMinimumHeight((float) (14.6));
                            table.addCell(cell2);
                        } else {
                            cell2 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, "",
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell2.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell2.setMinimumHeight((float) (14.6));
                            table.addCell(cell2);
                        }

                        if ((k + (l * 50)) < Objects.requireNonNull(cValue.get(geti)).size()) {
                            cell3 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, Objects.requireNonNull(cValue.get(geti)).get(k + (l * 50)),
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell3.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell3.setMinimumHeight((float) (14.6));
                            table.addCell(cell3);
                        } else {
                            cell3 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, "",
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell3.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell3.setMinimumHeight((float) (14.6));
                            table.addCell(cell3);
                        }

                        if ((k + (l * 50)) < Objects.requireNonNull(dValue.get(geti)).size()) {
                            cell4 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, Objects.requireNonNull(dValue.get(geti)).get(k + (l * 50)),
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell4.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell4.setMinimumHeight((float) (14.6));
                            table.addCell(cell4);
                        } else {
                            cell4 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, "",
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell4.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell4.setMinimumHeight((float) (14.6));
                            table.addCell(cell4);
                        }
                    }
                }
            }
            return table;
        } else if (setColumn.get(geti) == 5) {
            calculate = calculate + setColumn.get(geti);
            PdfPTable table = new PdfPTable((setColumn.get(geti) + 2));
            PdfPCell cell, cell1, cell2, cell3, cell4, cell5;

            a = getunit(String.valueOf(setUnit.get(calculate - 5)));
            b = getunit(String.valueOf(setUnit.get(calculate - 4)));
            c = getunit(String.valueOf(setUnit.get(calculate - 3)));
            d = getunit(String.valueOf(setUnit.get(calculate - 2)));
            e = getunit(String.valueOf(setUnit.get(calculate - 1)));

            for (int j = 0; j < setPage.get(geti); j++) {
                cell = new PdfPCell(new Paragraph(new Phrase(lineSpacing, context.getString(R.string.pdfdate),
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, fntSize))));
                cell.setColspan(2);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                cell.setMinimumHeight(18);
                table.addCell(cell);

                cell1 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, a,
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, fntSize))));
                cell1.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                cell1.setMinimumHeight(18);
                table.addCell(cell1);

                cell2 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, b,
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, fntSize))));
                cell2.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                cell2.setMinimumHeight(18);
                table.addCell(cell2);

                cell3 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, c,
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, fntSize))));
                cell3.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                cell3.setMinimumHeight(18);
                table.addCell(cell3);

                cell4 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, d,
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, fntSize))));
                cell4.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                cell4.setMinimumHeight(18);
                table.addCell(cell4);

                cell5 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, e,
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, fntSize))));
                cell5.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                cell5.setMinimumHeight(18);
                table.addCell(cell5);

                for (int k = (100 * j); k < 50 + (100 * j); k++) {
                    for (int l = 0; l < 2; l++) {
                        if ((k + (l * 50)) < Objects.requireNonNull(timeList.get(geti)).size()) {
                            cell = new PdfPCell(new Paragraph(new Phrase(lineSpacing, Objects.requireNonNull(timeList.get(geti)).get(k + (l * 50)),
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell.setColspan(2);
                            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell.setMinimumHeight((float) (14.6));
                            table.addCell(cell);
                        } else {
                            cell = new PdfPCell(new Paragraph(new Phrase(lineSpacing, "",
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell.setColspan(2);
                            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell.setMinimumHeight((float) (14.6));
                            table.addCell(cell);
                        }

                        if ((k + (l * 50)) < Objects.requireNonNull(aValue.get(geti)).size()) {
                            cell1 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, Objects.requireNonNull(aValue.get(geti)).get(k + (l * 50)),
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell1.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell1.setMinimumHeight((float) (14.6));
                            table.addCell(cell1);
                        } else {
                            cell1 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, "",
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell1.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell1.setMinimumHeight((float) (14.6));
                            table.addCell(cell1);
                        }

                        if ((k + (l * 50)) < Objects.requireNonNull(bValue.get(geti)).size()) {
                            cell2 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, Objects.requireNonNull(bValue.get(geti)).get(k + (l * 50)),
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell2.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell2.setMinimumHeight((float) (14.6));
                            table.addCell(cell2);
                        } else {
                            cell2 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, "",
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell2.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell2.setMinimumHeight((float) (14.6));
                            table.addCell(cell2);
                        }

                        if ((k + (l * 50)) < Objects.requireNonNull(cValue.get(geti)).size()) {
                            cell3 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, Objects.requireNonNull(cValue.get(geti)).get(k + (l * 50)),
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell3.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell3.setMinimumHeight((float) (14.6));
                            table.addCell(cell3);
                        } else {
                            cell3 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, "",
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell3.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell3.setMinimumHeight((float) (14.6));
                            table.addCell(cell3);
                        }

                        if ((k + (l * 50)) < Objects.requireNonNull(dValue.get(geti)).size()) {
                            cell4 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, Objects.requireNonNull(dValue.get(geti)).get(k + (l * 50)),
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell4.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell4.setMinimumHeight((float) (14.6));
                            table.addCell(cell4);
                        } else {
                            cell4 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, "",
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell4.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell4.setMinimumHeight((float) (14.6));
                            table.addCell(cell4);
                        }

                        if ((k + (l * 50)) < Objects.requireNonNull(eValue.get(geti)).size()) {
                            cell5 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, Objects.requireNonNull(eValue.get(geti)).get(k + (l * 50)),
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell5.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell5.setMinimumHeight((float) (14.6));
                            table.addCell(cell5);
                        } else {
                            cell5 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, "",
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell5.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell5.setMinimumHeight((float) (14.6));
                            table.addCell(cell5);
                        }
                    }
                }
            }
            return table;
        } else if (setColumn.get(geti) == 6) {
            calculate = calculate + setColumn.get(geti);
            PdfPTable table = new PdfPTable((setColumn.get(geti) + 2));
            PdfPCell cell, cell1, cell2, cell3, cell4, cell5, cell6;

            a = getunit(String.valueOf(setUnit.get(calculate - 6)));
            b = getunit(String.valueOf(setUnit.get(calculate - 5)));
            c = getunit(String.valueOf(setUnit.get(calculate - 4)));
            d = getunit(String.valueOf(setUnit.get(calculate - 3)));
            e = getunit(String.valueOf(setUnit.get(calculate - 2)));
            f = getunit(String.valueOf(setUnit.get(calculate - 1)));

            for (int j = 0; j < setPage.get(geti); j++) {
                cell = new PdfPCell(new Paragraph(new Phrase(lineSpacing, context.getString(R.string.pdfdate),
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, fntSize))));
                cell.setColspan(2);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                cell.setMinimumHeight(18);
                table.addCell(cell);

                cell1 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, a,
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, fntSize))));
                cell1.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                cell1.setMinimumHeight(18);
                table.addCell(cell1);

                cell2 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, b,
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, fntSize))));
                cell2.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                cell2.setMinimumHeight(18);
                table.addCell(cell2);

                cell3 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, c,
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, fntSize))));
                cell3.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                cell3.setMinimumHeight(18);
                table.addCell(cell3);

                cell4 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, d,
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, fntSize))));
                cell4.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                cell4.setMinimumHeight(18);
                table.addCell(cell4);

                cell5 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, e,
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, fntSize))));
                cell5.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                cell5.setMinimumHeight(18);
                table.addCell(cell5);

                cell6 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, f,
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, fntSize))));
                cell6.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                cell6.setMinimumHeight(18);
                table.addCell(cell6);

                for (int k = (100 * j); k < 50 + (100 * j); k++) {
                    for (int l = 0; l < 2; l++) {
                        if ((k + (l * 50)) < Objects.requireNonNull(timeList.get(geti)).size()) {
                            cell = new PdfPCell(new Paragraph(new Phrase(lineSpacing, Objects.requireNonNull(timeList.get(geti)).get(k + (l * 50)),
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell.setColspan(2);
                            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell.setMinimumHeight((float) (14.6));
                            table.addCell(cell);
                        } else {
                            cell = new PdfPCell(new Paragraph(new Phrase(lineSpacing, "",
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell.setColspan(2);
                            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell.setMinimumHeight((float) (14.6));
                            table.addCell(cell);
                        }

                        if ((k + (l * 50)) < Objects.requireNonNull(aValue.get(geti)).size()) {
                            cell1 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, Objects.requireNonNull(aValue.get(geti)).get(k + (l * 50)),
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell1.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell1.setMinimumHeight((float) (14.6));
                            table.addCell(cell1);
                        } else {
                            cell1 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, "",
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell1.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell1.setMinimumHeight((float) (14.6));
                            table.addCell(cell1);
                        }

                        if ((k + (l * 50)) < Objects.requireNonNull(bValue.get(geti)).size()) {
                            cell2 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, Objects.requireNonNull(bValue.get(geti)).get(k + (l * 50)),
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell2.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell2.setMinimumHeight((float) (14.6));
                            table.addCell(cell2);
                        } else {
                            cell2 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, "",
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell2.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell2.setMinimumHeight((float) (14.6));
                            table.addCell(cell2);
                        }

                        if ((k + (l * 50)) < Objects.requireNonNull(cValue.get(geti)).size()) {
                            cell3 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, Objects.requireNonNull(cValue.get(geti)).get(k + (l * 50)),
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell3.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell3.setMinimumHeight((float) (14.6));
                            table.addCell(cell3);
                        } else {
                            cell3 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, "",
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell3.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell3.setMinimumHeight((float) (14.6));
                            table.addCell(cell3);
                        }

                        if ((k + (l * 50)) < Objects.requireNonNull(dValue.get(geti)).size()) {
                            cell4 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, Objects.requireNonNull(dValue.get(geti)).get(k + (l * 50)),
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell4.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell4.setMinimumHeight((float) (14.6));
                            table.addCell(cell4);
                        } else {
                            cell4 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, "",
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell4.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell4.setMinimumHeight((float) (14.6));
                            table.addCell(cell4);
                        }

                        if ((k + (l * 50)) < Objects.requireNonNull(eValue.get(geti)).size()) {
                            cell5 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, Objects.requireNonNull(eValue.get(geti)).get(k + (l * 50)),
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell5.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell5.setMinimumHeight((float) (14.6));
                            table.addCell(cell5);
                        } else {
                            cell5 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, "",
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell5.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell5.setMinimumHeight((float) (14.6));
                            table.addCell(cell5);
                        }

                        if ((k + (l * 50)) < Objects.requireNonNull(fValue.get(geti)).size()) {
                            cell6 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, Objects.requireNonNull(fValue.get(geti)).get(k + (l * 50)),
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell6.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell6.setMinimumHeight((float) (14.6));
                            table.addCell(cell6);
                        } else {
                            cell6 = new PdfPCell(new Paragraph(new Phrase(lineSpacing, "",
                                    FontFactory.getFont(FontFactory.HELVETICA, fntSize))));
                            cell6.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                            cell6.setMinimumHeight((float) (14.6));
                            table.addCell(cell6);
                        }
                    }
                }
            }
            return table;
        }
        else
            return null;
    }

    private String getunit(String unit) {    //Item + Unit
        String str = "";

        if (unit.matches("0")) {
            str = context.getString(R.string.pdfP) + "/" + "Mpa";
        } else if (unit.matches("1")) {
            str = context.getString(R.string.pdfP) + "/" + "Kpa";
        } else if (unit.matches("2")) {
            str = context.getString(R.string.pdfP) + "/" + "Pa";
        } else if (unit.matches("3")) {
            str = context.getString(R.string.pdfP) + "/" + "Bar";
        } else if (unit.matches("4")) {
            str = context.getString(R.string.pdfP) + "/" + "Mbar";
        } else if (unit.matches("5")) {
            str = context.getString(R.string.pdfP) + "/" + "Kg/cm" + (char) (178);
        } else if (unit.matches("6")) {
            str = context.getString(R.string.pdfP) + "/" + "psi";
        } else if (unit.matches("7")) {
            str = context.getString(R.string.pdfP) + "/" + "mh" + (char) (178) + "O";
        } else if (unit.matches("8")) {
            str = context.getString(R.string.pdfP) + "/" + "mmh" + (char) (178) + "O";
        } else if (unit.matches("9")) {
            str = context.getString(R.string.pdfT) + "/" + (char) (186) + "C";
        } else if (unit.matches("10")) {
            str = context.getString(R.string.pdfT) + "/" + (char) (186) + "F";
        } else if (unit.matches("11")) {
            str = context.getString(R.string.pdfH) + "/" + "%";
        } else if (unit.matches("12")) {
            str = context.getString(R.string.pdfCO) + "/" + "ppm";
        } else if (unit.matches("13")) {
            str = context.getString(R.string.pdfC) + "/" + "ppm";
        } else if (unit.matches("14")) {
            str = context.getString(R.string.pdfM) + "/" + (char) (181) + "g/m" + (char) (179);
        }else if (unit.matches("15")) {
            str = context.getString(R.string.pdfpercent) + "/" + "%";
        }
        return str;
    }
}
