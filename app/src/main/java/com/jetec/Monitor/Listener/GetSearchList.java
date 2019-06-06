package com.jetec.Monitor.Listener;

import com.jetec.Monitor.SupportFunction.LogMessage;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class GetSearchList {

    private String TAG = "GetSearchList";
    private SearchListener searchListener;
    private LogMessage logMessage = new LogMessage();

    public void setListener(SearchListener mSearchListener) {
        searchListener = mSearchListener;
    }

    public void toshowlist(String list) {
        if (searchListener != null) {
            searchListener.checkList(list);
        }
    }

    public void getshowlist(String newListjson) {
        try {
            logMessage.showmessage(TAG, "newListjson = " + newListjson);
            List<List<String>> showList = new ArrayList<>();
            List<String> nameList = new ArrayList<>();
            List<String> idList = new ArrayList<>();
            List<String> timeList = new ArrayList<>();
            List<List<String>> saveList = new ArrayList<>();
            nameList.clear();
            idList.clear();
            timeList.clear();
            saveList.clear();
            showList.clear();

            JSONArray logjson = new JSONArray(newListjson);
            String namejson = logjson.get(0).toString();
            JSONArray namejsonlist = new JSONArray(namejson);
            for (int i = 0; i < namejsonlist.length(); i++) {
                nameList.add(namejsonlist.get(i).toString());
            }

            String idjson = logjson.get(1).toString();
            JSONArray idjsonlist = new JSONArray(idjson);
            for (int i = 0; i < idjsonlist.length(); i++) {
                idList.add(idjsonlist.get(i).toString());
            }

            String timejson = logjson.get(2).toString();
            JSONArray timejsonlist = new JSONArray(timejson);
            for (int i = 0; i < timejsonlist.length(); i++) {
                timeList.add(timejsonlist.get(i).toString());
            }

            for (int i = 3; i < logjson.length(); i++) {
                List<String> getjsonlist = new ArrayList<>();
                getjsonlist.clear();
                String savejson = logjson.get(i).toString();
                JSONArray savejsonlist = new JSONArray(savejson);
                for (int j = 0; j < savejsonlist.length(); j++) {
                    getjsonlist.add(savejsonlist.get(j).toString());
                }
                saveList.add(getjsonlist);
            }

            showList.add(nameList);
            showList.add(idList);
            showList.add(timeList);
            showList.addAll(saveList);

            logMessage.showmessage(TAG, "saveList = " + saveList);

            searchListener.setList(showList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
