package com.jetec.Monitor.SupportFunction;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.jetec.Monitor.Listener.GetSearchList;
import com.jetec.Monitor.R;
import org.json.JSONArray;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class SearchNewList {

    private String TAG = "SearchNewList";
    private List<List<String>> newSavelist;
    private List<String> newTimeList, idList;
    private GetUnit getUnit = new GetUnit();
    private LogMessage logMessage = new LogMessage();

    public SearchNewList() {
        newSavelist = new ArrayList<>();
        newTimeList = new ArrayList<>();
        idList = new ArrayList<>();
        newSavelist.clear();
        newTimeList.clear();
        idList.clear();
    }

    public void timeSearchList(Context context, GetSearchList getSearchList, String chose2, SimpleDateFormat sdf,
                               String timecomparison, List<String> timeList, ArrayList<List<String>> saveList, List<String> nameList) {
        try {
            if (chose2.matches("＞")) {
                if (sdf.parse(timecomparison).after(sdf.parse(timeList.get(timeList.size() - 1)))
                        || sdf.parse(timecomparison).before(sdf.parse(timeList.get(0)))) {
                    Toast.makeText(context, context.getString(R.string.wrong), Toast.LENGTH_SHORT).show();
                } else {
                    for (int i = 0; i < timeList.size(); i++) {
                        if (sdf.parse(timecomparison).before(sdf.parse(timeList.get(i)))) {
                            newTimeList.add(timeList.get(i));
                            idList.add(String.valueOf(i + 1));
                        }
                    }
                    for (int i = 0; i < saveList.size(); i++) {
                        List<String> newList = new ArrayList<>();
                        newList.clear();
                        for (int j = 0; j < timeList.size(); j++) {
                            if (sdf.parse(timecomparison).before(sdf.parse(timeList.get(j)))) {
                                newList.add(saveList.get(i).get(j));
                            }
                        }
                        newSavelist.add(newList);
                    }
                    showpage(getSearchList, nameList);
                }
            } else if (chose2.matches("≧")) {
                if (sdf.parse(timecomparison).after(sdf.parse(timeList.get(timeList.size() - 1)))
                        || sdf.parse(timecomparison).before(sdf.parse(timeList.get(0)))) {
                    Toast.makeText(context, context.getString(R.string.wrong), Toast.LENGTH_SHORT).show();
                } else {
                    for (int i = 0; i < timeList.size(); i++) {
                        if (sdf.parse(timecomparison).before(sdf.parse(timeList.get(i)))
                                || sdf.parse(timecomparison).equals(sdf.parse(timeList.get(i)))) {
                            newTimeList.add(timeList.get(i));
                            idList.add(String.valueOf(i + 1));
                        }
                    }
                    for (int i = 0; i < saveList.size(); i++) {
                        List<String> newList = new ArrayList<>();
                        newList.clear();
                        for (int j = 0; j < timeList.size(); j++) {
                            if (sdf.parse(timecomparison).before(sdf.parse(timeList.get(j)))
                                    || sdf.parse(timecomparison).equals(sdf.parse(timeList.get(j)))) {
                                newList.add(saveList.get(i).get(j));
                            }
                        }
                        newSavelist.add(newList);
                    }
                    showpage(getSearchList, nameList);
                }
            } else if (chose2.matches("＝")) {
                if (sdf.parse(timecomparison).after(sdf.parse(timeList.get(timeList.size() - 1)))
                        || sdf.parse(timecomparison).before(sdf.parse(timeList.get(0)))) {
                    Toast.makeText(context, context.getString(R.string.wrong), Toast.LENGTH_SHORT).show();
                } else {
                    for (int i = 0; i < timeList.size(); i++) {
                        if (sdf.parse(timecomparison).equals(sdf.parse(timeList.get(i)))) {
                            newTimeList.add(timeList.get(i));
                            idList.add(String.valueOf(i + 1));
                        }
                    }
                    for (int i = 0; i < saveList.size(); i++) {
                        List<String> newList = new ArrayList<>();
                        newList.clear();
                        for (int j = 0; j < timeList.size(); j++) {
                            if (sdf.parse(timecomparison).equals(sdf.parse(timeList.get(j)))) {
                                newList.add(saveList.get(i).get(j));
                            }
                        }
                        newSavelist.add(newList);
                    }
                    showpage(getSearchList, nameList);
                }
            } else if (chose2.matches("≦")) {
                if (sdf.parse(timecomparison).after(sdf.parse(timeList.get(timeList.size() - 1)))
                        || sdf.parse(timecomparison).before(sdf.parse(timeList.get(0)))) {
                    Toast.makeText(context, context.getString(R.string.wrong), Toast.LENGTH_SHORT).show();
                } else {
                    for (int i = 0; i < timeList.size(); i++) {
                        if (sdf.parse(timecomparison).after(sdf.parse(timeList.get(i)))
                                || sdf.parse(timecomparison).equals(sdf.parse(timeList.get(i)))) {
                            newTimeList.add(timeList.get(i));
                            idList.add(String.valueOf(i + 1));
                        }
                    }
                    for (int i = 0; i < saveList.size(); i++) {
                        List<String> newList = new ArrayList<>();
                        newList.clear();
                        for (int j = 0; j < timeList.size(); j++) {
                            if (sdf.parse(timecomparison).after(sdf.parse(timeList.get(j)))
                                    || sdf.parse(timecomparison).equals(sdf.parse(timeList.get(j)))) {
                                newList.add(saveList.get(i).get(j));
                            }
                        }
                        newSavelist.add(newList);
                    }
                    showpage(getSearchList, nameList);
                }
            } else if (chose2.matches("＜")) {
                if (sdf.parse(timecomparison).after(sdf.parse(timeList.get(timeList.size() - 1)))
                        || sdf.parse(timecomparison).before(sdf.parse(timeList.get(0)))) {
                    Toast.makeText(context, context.getString(R.string.wrong), Toast.LENGTH_SHORT).show();
                } else {
                    for (int i = 0; i < timeList.size(); i++) {
                        if (sdf.parse(timecomparison).after(sdf.parse(timeList.get(i)))) {
                            newTimeList.add(timeList.get(i));
                            idList.add(String.valueOf(i + 1));
                        }
                    }
                    for (int i = 0; i < saveList.size(); i++) {
                        List<String> newList = new ArrayList<>();
                        newList.clear();
                        for (int j = 0; j < timeList.size(); j++) {
                            if (sdf.parse(timecomparison).after(sdf.parse(timeList.get(j)))) {
                                newList.add(saveList.get(i).get(j));
                            }
                        }
                        newSavelist.add(newList);
                    }
                    showpage(getSearchList, nameList);
                }
            } else
                Toast.makeText(context, context.getString(R.string.wrong), Toast.LENGTH_SHORT).show();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void idSearchList(Context context, GetSearchList getSearchList, String chose1, String chose2,
                             String text, List<String> nameList, List<String> spinnerList, List<String> timeList,
                             ArrayList<List<String>> saveList) {
        int index = spinnerList.indexOf(chose1) - 3;
        Log.e(TAG, "saveList = " + saveList.size());
        if (chose1.matches(spinnerList.get(2))) {
            if (chose2.matches("＞")) {
                int i = Integer.valueOf(text) - 1;
                int j = timeList.size() - 1;
                for (; i < j; i++) {
                    newTimeList.add(timeList.get(i));
                    idList.add(String.valueOf(i + 2));
                }
                for (int k = 0; k < saveList.size(); k++) {
                    i = Integer.valueOf(text) - 1;
                    List<String> newList = new ArrayList<>();
                    newList.clear();
                    for (; i < j; i++) {
                        newList.add(saveList.get(k).get(i));
                    }
                    newSavelist.add(newList);
                }
                showpage(getSearchList, nameList);
            } else if (chose2.matches("≧")) {
                int i = Integer.valueOf(text) - 1;
                int j = timeList.size() - 1;
                for (; i <= j; i++) {
                    newTimeList.add(timeList.get(i - 1));
                    idList.add(String.valueOf(i + 1));
                }
                for (int k = 0; k < saveList.size(); k++) {
                    i = Integer.valueOf(text) - 1;
                    List<String> newList = new ArrayList<>();
                    newList.clear();
                    for (; i <= j; i++) {
                        newList.add(saveList.get(k).get(i - 1));
                    }
                    newSavelist.add(newList);
                    logMessage.showmessage(TAG, "newList" + k + " = " + newList.size());
                }
                logMessage.showmessage(TAG, "newTimeList = " + newTimeList.size());
                logMessage.showmessage(TAG, "idList = " + idList.size());
                logMessage.showmessage(TAG, "newSavelist = " + newSavelist);
                showpage(getSearchList, nameList);
            } else if (chose2.matches("＝")) {
                int i = Integer.valueOf(text) - 1;
                int j = timeList.size() - 1;
                newTimeList.add(timeList.get(i - 1));
                idList.add(String.valueOf(i + 1));
                for (int k = 0; k < saveList.size(); k++) {
                    List<String> newList = new ArrayList<>();
                    newList.clear();
                    newList.add(saveList.get(k).get(i - 1));
                    newSavelist.add(newList);
                }
                showpage(getSearchList, nameList);
            } else if (chose2.matches("≦")) {
                int i = Integer.valueOf(text) - 1;
                for (int j = 0; j <= i; j++) {
                    newTimeList.add(timeList.get(j));
                    idList.add(String.valueOf(j + 1));
                }
                for (int k = 0; k < saveList.size(); k++) {
                    List<String> newList = new ArrayList<>();
                    newList.clear();
                    for (int j = 0; j <= i; j++) {
                        newList.add(saveList.get(k).get(j));
                    }
                    newSavelist.add(newList);
                }
                showpage(getSearchList, nameList);
            } else if (chose2.matches("＜")) {
                int i = Integer.valueOf(text) - 1;
                for (int j = 0; j < i; j++) {
                    newTimeList.add(timeList.get(j));
                    idList.add(String.valueOf(j + 1));
                }
                for (int k = 0; k < saveList.size(); k++) {
                    List<String> newList = new ArrayList<>();
                    newList.clear();
                    for (int j = 0; j < i; j++) {
                        newList.add(saveList.get(k).get(j));
                    }
                    newSavelist.add(newList);
                }
                showpage(getSearchList, nameList);
            } else {
                Toast.makeText(context, context.getString(R.string.wrong), Toast.LENGTH_SHORT).show();
            }
        } else {
            if (chose2.matches("＞")) {
                float i = Float.valueOf(text);
                for (int k = 0; k < timeList.size(); k++) {
                    if ((Float.valueOf(saveList.get(index).get(k)) * 10) > i * 10) {
                        newTimeList.add(timeList.get(k));
                        idList.add(String.valueOf(k + 1));
                    }
                }
                for (int j = 0; j < saveList.size(); j++) {
                    List<String> newList = new ArrayList<>();
                    newList.clear();
                    for (int k = 0; k < timeList.size(); k++) {
                        if ((Float.valueOf(saveList.get(index).get(k)) * 10) > i * 10) {
                            newList.add(saveList.get(j).get(k));
                        }
                    }
                    newSavelist.add(newList);
                }
                showpage(getSearchList, nameList);
            } else if (chose2.matches("≧")) {
                float i = Float.valueOf(text);
                for (int k = 0; k < timeList.size(); k++) {
                    if ((Float.valueOf(saveList.get(index).get(k)) * 10) >= i * 10) {
                        newTimeList.add(timeList.get(k));
                        idList.add(String.valueOf(k + 1));
                    }
                }
                for (int j = 0; j < saveList.size(); j++) {
                    List<String> newList = new ArrayList<>();
                    newList.clear();
                    for (int k = 0; k < timeList.size(); k++) {
                        if ((Float.valueOf(saveList.get(index).get(k)) * 10) >= i * 10) {
                            newList.add(saveList.get(j).get(k));
                        }
                    }
                    newSavelist.add(newList);
                }
                showpage(getSearchList, nameList);
            } else if (chose2.matches("＝")) {
                float i = Float.valueOf(text);
                for (int k = 0; k < timeList.size(); k++) {
                    if ((Float.valueOf(saveList.get(index).get(k)) * 10) == i * 10) {
                        newTimeList.add(timeList.get(k));
                        idList.add(String.valueOf(k + 1));
                    }
                }
                for (int j = 0; j < saveList.size(); j++) {
                    List<String> newList = new ArrayList<>();
                    newList.clear();
                    for (int k = 0; k < timeList.size(); k++) {
                        if ((Float.valueOf(saveList.get(index).get(k)) * 10) == i * 10) {
                            newList.add(saveList.get(j).get(k));
                        }
                    }
                    newSavelist.add(newList);
                }
                showpage(getSearchList, nameList);
            } else if (chose2.matches("≦")) {
                float i = Float.valueOf(text);
                for (int k = 0; k < timeList.size(); k++) {
                    if ((Float.valueOf(saveList.get(index).get(k)) * 10) <= i * 10) {
                        newTimeList.add(timeList.get(k));
                        idList.add(String.valueOf(k + 1));
                    }
                }
                for (int j = 0; j < saveList.size(); j++) {
                    List<String> newList = new ArrayList<>();
                    newList.clear();
                    for (int k = 0; k < timeList.size(); k++) {
                        if ((Float.valueOf(saveList.get(index).get(k)) * 10) <= i * 10) {
                            newList.add(saveList.get(j).get(k));
                        }
                    }
                    newSavelist.add(newList);
                }
                showpage(getSearchList, nameList);
            } else if (chose2.matches("＜")) {
                float i = Float.valueOf(text);
                for (int k = 0; k < timeList.size(); k++) {
                    if ((Float.valueOf(saveList.get(index).get(k)) * 10) < i * 10) {
                        newTimeList.add(timeList.get(k));
                        idList.add(String.valueOf(k + 1));
                    }
                }
                for (int j = 0; j < saveList.size(); j++) {
                    List<String> newList = new ArrayList<>();
                    newList.clear();
                    for (int k = 0; k < timeList.size(); k++) {
                        if ((Float.valueOf(saveList.get(index).get(k)) * 10) < i * 10) {
                            newList.add(saveList.get(j).get(k));
                        }
                    }
                    newSavelist.add(newList);
                }
                showpage(getSearchList, nameList);
            }
        }
    }

    private void showpage(GetSearchList getSearchList, List<String> nameList) {
        List<List<String>> ListArr = new ArrayList<>();
        ListArr.clear();
        ListArr.add(nameList);
        ListArr.add(newTimeList);
        ListArr.add(idList);
        ListArr.addAll(newSavelist);
        JSONArray newListjson = new JSONArray(ListArr);
        getSearchList.toshowlist(newListjson.toString());
    }
}
