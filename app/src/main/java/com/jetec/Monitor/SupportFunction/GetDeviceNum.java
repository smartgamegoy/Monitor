package com.jetec.Monitor.SupportFunction;

import android.content.Context;

import com.jetec.Monitor.R;

import java.util.ArrayList;
import java.util.List;

public class GetDeviceNum {

    private String TAG = "GetDeviceNum";
    private LogMessage logMessage = new LogMessage();
    private Context context;
    private List<String> nameList = new ArrayList<>();

    public GetDeviceNum(Context context) {
        this.context = context;
        nameList.clear();
        for (int i = 0; i < Value.model_name.length(); i++) {
            nameList.add(String.valueOf(Value.model_name.charAt(i)));
        }
    }

    public String get(String str) {

        String renum = "";

        if (str.startsWith("SPK")) {
            if (str.substring(str.indexOf("+") + 1).matches("0001.0")) {
                renum = "On";
            } else {
                renum = "Off";
            }
        } else if (str.startsWith("RL")) {
            char model = 0;
            String newStr = str.substring(str.indexOf("+") + 1, str.indexOf("."))
                    .replaceFirst("^0*", "");
            if (!newStr.matches("")) {
                if (Integer.valueOf(newStr) > Value.model_name.length() || Integer.valueOf(newStr) == 0) {
                } else {
                    model = Value.model_name.charAt(Integer.valueOf(newStr) - 1);
                }
            }
            if (String.valueOf(model).matches("P")) {
                renum = context.getString(R.string.P);
            } else if (String.valueOf(model).matches("M")) {
                renum = context.getString(R.string.M);
            } else if (String.valueOf(model).matches("C") ||
                    String.valueOf(model).matches("D") ||
                    String.valueOf(model).matches("E")) {
                renum = context.getString(R.string.C);
            } else if (String.valueOf(model).matches("T")) {
                renum = context.getString(R.string.T);
            } else if (String.valueOf(model).matches("H")) {
                renum = context.getString(R.string.H);
            } else if (String.valueOf(model).matches("Z")) {
                renum = context.getString(R.string.percent);
            } else if (String.valueOf(model).matches("W")) {
                renum = context.getString(R.string.W);
            } else {
                renum = context.getString(R.string.none);
            }
        } else if (str.startsWith("ADR")) {
            renum = str.substring(str.indexOf("+") + 1, str.indexOf("."))
                    .replaceFirst("^0*", "");
        } else if (str.startsWith("PR")) {
            if (str.substring(str.indexOf("+") + 1).matches("0000.0")) {
                renum = "0";
            } else {
                renum = str.substring(str.indexOf("+") + 1, str.indexOf("."))
                        .replaceFirst("^0*", "");
            }
        } else {
            if (str.startsWith("IH") || str.startsWith("IL") || str.startsWith("EH") || str.startsWith("EL") || str.startsWith("PV")) {
                int count = Integer.valueOf(str.substring(2, 3));
                if (nameList.get(count - 1).matches("T")) {
                    if (str.contains("+")) {
                        if (str.substring(str.indexOf("+") + 1).matches("0000.0")) {
                            renum = "0";
                        } else {
                            String newStr = str.substring(str.indexOf("+") + 1)
                                    .replaceFirst("^0*", "");
                            if (newStr.startsWith(".")) {
                                renum = "0" + newStr;
                            } else {
                                if (newStr.contains(".")) {
                                    if (newStr.substring(newStr.indexOf(".") + 1, newStr.indexOf(".") + 2).matches("0")) {
                                        renum = newStr.substring(0, newStr.indexOf("."));
                                    } else {
                                        renum = newStr;
                                    }
                                } else {
                                    renum = newStr;
                                }
                            }
                        }
                    } else {
                        if (str.substring(str.indexOf("-") + 1).matches("0000.0")) {
                            renum = "0";
                        } else {
                            String newStr = str.substring(str.indexOf("-") + 1)
                                    .replaceFirst("^0*", "");
                            if (newStr.startsWith(".")) {
                                renum = "-" + "0" + newStr;
                            } else {
                                if (newStr.contains(".")) {
                                    if (newStr.substring(newStr.indexOf(".") + 1, newStr.indexOf(".") + 2).matches("0")) {
                                        renum = "-" + newStr.substring(0, newStr.indexOf("."));
                                    } else {
                                        renum = "-" + newStr;
                                    }
                                } else {
                                    renum = "-" + newStr;
                                }
                            }
                        }
                    }
                } else if (nameList.get(count - 1).matches("H")) {
                    if (str.contains("+")) {
                        if (str.substring(str.indexOf("+") + 1).matches("0000.0")) {
                            renum = "0";
                        } else {
                            String newStr = str.substring(str.indexOf("+") + 1)
                                    .replaceFirst("^0*", "");
                            if (newStr.startsWith(".")) {
                                renum = "0" + newStr;
                            } else {
                                if (newStr.contains(".")) {
                                    if (newStr.substring(newStr.indexOf(".") + 1, newStr.indexOf(".") + 2).matches("0")) {
                                        renum = newStr.substring(0, newStr.indexOf("."));
                                    } else {
                                        renum = newStr;
                                    }
                                } else {
                                    renum = newStr;
                                }
                            }
                        }
                    }
                } else if (nameList.get(count - 1).matches("C") ||
                        nameList.get(count - 1).matches("D") ||
                        nameList.get(count - 1).matches("E")) {
                    if (str.contains("+")) {
                        if (str.substring(str.indexOf("+") + 1).matches("0000.0")) {
                            renum = "0";
                        } else {
                            renum = str.substring(str.indexOf("+") + 1, str.indexOf("."))
                                    .replaceFirst("^0*", "");
                        }
                    }
                } else if (nameList.get(count - 1).matches("M")) {
                    if (str.contains("+")) {
                        if (str.substring(str.indexOf("+") + 1).matches("0000.0")) {
                            renum = "0";
                        } else {
                            renum = str.substring(str.indexOf("+") + 1, str.indexOf("."))
                                    .replaceFirst("^0*", "");
                        }
                    }
                } else if (nameList.get(count - 1).matches("Z")) {
                    if (str.contains("+")) {
                        if (str.substring(str.indexOf("+") + 1).matches("0000.0")) {
                            renum = "0";
                        } else {
                            renum = str.substring(str.indexOf("+") + 1, str.indexOf("."))
                                    .replaceFirst("^0*", "");
                        }
                    }
                } else if (nameList.get(count - 1).matches("P")) {
                    if (str.contains("+")) {
                        if (str.substring(str.indexOf("+") + 1).matches("0000.0")) {
                            renum = "0";
                        } else {
                            renum = str.substring(str.indexOf("+") + 1, str.indexOf("."))
                                    .replaceFirst("^0*", "");
                        }
                    }
                } else if (nameList.get(count - 1).matches("W")) {
                    if (str.contains("+")) {
                        if (str.substring(str.indexOf("+") + 1).matches("0000.0")) {
                            renum = "0";
                        } else {
                            renum = str.substring(str.indexOf("+") + 1, str.indexOf("."))
                                    .replaceFirst("^0*", "");
                        }
                    }
                }
                /*for (int i = 0; i < nameList.size(); i++) {
                    if (nameList.get(i).matches("T")) {
                        if (str.contains("+")) {
                            if (str.substring(str.indexOf("+") + 1).matches("0000.0")) {
                                renum = "0";
                            } else {
                                String newStr = str.substring(str.indexOf("+") + 1)
                                        .replaceFirst("^0*", "");
                                if (newStr.startsWith(".")) {
                                    renum = "0" + newStr;
                                } else {
                                    renum = newStr;
                                }
                            }
                        } else {
                            if (str.substring(str.indexOf("-") + 1).matches("0000.0")) {
                                renum = "0";
                            } else {
                                String newStr = str.substring(str.indexOf("-") + 1)
                                        .replaceFirst("^0*", "");
                                if (newStr.startsWith(".")) {
                                    renum = "-" + "0" + newStr;
                                } else {
                                    renum = "-" + newStr;
                                }
                            }
                        }
                        logMessage.showmessage(TAG, "T = " + renum);
                    } else if (nameList.get(i).matches("H")) {
                        if (str.substring(str.indexOf("+") + 1).matches("0000.0")) {
                            renum = "0";
                        } else {
                            renum = str.substring(str.indexOf("+") + 1, str.indexOf("."))
                                    .replaceFirst("^0*", "");
                        }
                    } else if (nameList.get(i).matches("C") ||
                            nameList.get(i).matches("D") ||
                            nameList.get(i).matches("E")) {
                        if (str.substring(str.indexOf("+") + 1).matches("0000.0")) {
                            renum = "0";
                        } else {
                            renum = str.substring(str.indexOf("+") + 1, str.indexOf("."))
                                    .replaceFirst("^0*", "");
                        }
                    } else if (nameList.get(i).matches("M")) {
                        if (str.substring(str.indexOf("+") + 1).matches("0000.0")) {
                            renum = "0";
                        } else {
                            renum = str.substring(str.indexOf("+") + 1, str.indexOf("."))
                                    .replaceFirst("^0*", "");
                        }
                    } else if (nameList.get(i).matches("Z")) {
                        if (str.substring(str.indexOf("+") + 1).matches("0000.0")) {
                            renum = "0";
                        } else {
                            renum = str.substring(str.indexOf("+") + 1, str.indexOf("."))
                                    .replaceFirst("^0*", "");
                        }
                    } else if (nameList.get(i).matches("P")) {
                        if (str.substring(str.indexOf("+") + 1).matches("0000.0")) {
                            renum = "0";
                        } else {
                            renum = str.substring(str.indexOf("+") + 1, str.indexOf("."))
                                    .replaceFirst("^0*", "");
                        }
                    } else if (nameList.get(i).matches("W")) {
                        if (str.substring(str.indexOf("+") + 1).matches("0000.0")) {
                            renum = "0";
                        } else {
                            renum = str.substring(str.indexOf("+") + 1, str.indexOf("."))
                                    .replaceFirst("^0*", "");
                        }
                    }
                    logMessage.showmessage(TAG, "for = " + renum);
                    logMessage.showmessage(TAG, "i = " + i);
                }*/

            } else {
                if (str.startsWith("INTER")) {
                    str = str.substring(str.indexOf("INTER") + 5);
                    int i = Integer.valueOf(str);
                    if (i == 3600)
                        renum = "1h";
                    else if (60 <= i && i < 3600) {
                        int j = i / 60;
                        int k = i % 60;
                        if (k == 0)
                            renum = String.valueOf(j) + "m";
                        else
                            renum = String.valueOf(j) + "m" + String.valueOf(k) + "s";
                    } else
                        renum = String.valueOf(i) + "s";
                }
            }
        }
        return renum;
    }
}
