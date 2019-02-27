package com.example.hivedemo.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScanDataUtil {

    // 进行电话号码、身份证号码、IMEI、IMSI的验证
    public static Map<String, String> scanSentiveWords(String FileContent) {
        Map resultParams = new HashMap<>();
        StringBuffer sb = new StringBuffer();
        ArrayList<String> TelList = checkTel(FileContent);
        boolean hasTel = false;
        if(TelList.size()>0){
            hasTel = true;
            for(int i=0;i<TelList.size();i++){
                sb.append("Tel:");
                if(i==TelList.size()-1){
                    sb.append(TelList.get(i)+"\n");
                }else{
                    sb.append(TelList.get(i)+",");
                }
            }
        }
        System.out.println("是否含有电话号码===>" + hasTel);

        ArrayList<String> IdCardList = twoLevelAuthIDCard(FileContent);
        boolean hasIdCard = false;
        if(IdCardList.size()>0){
            hasIdCard = true;
            for(int i=0;i<IdCardList.size();i++){
                sb.append("IdCard:");
                if(i==IdCardList.size()-1){
                    sb.append(IdCardList.get(i)+"\n");
                }else{
                    sb.append(IdCardList.get(i)+",");
                }
            }
        }
        System.out.println("是否含有身份证号码===>" + hasIdCard);

        ArrayList<String> ESList = scanMEAndMS(FileContent);
        boolean hasES = false;
        if(ESList.size()>0){
            hasES = true;
            for(int i=0;i<ESList.size();i++){
                sb.append("MEMS:");
                if(i==ESList.size()-1){
                    sb.append(ESList.get(i)+"\n");
                }else{
                    sb.append(ESList.get(i)+",");
                }
            }
        }
        System.out.println("是否含有ME或者MS===>" + hasES);
        String sensitiveContext = sb.toString();
        if (hasTel == true || hasIdCard == true || hasES == true) {
            resultParams.put("result", "1");
            resultParams.put("content", "文件内容含有敏感信息");
            resultParams.put("sensitiveContext", sensitiveContext);
            return resultParams;
        }
        resultParams.put("result", "2");
        resultParams.put("content", "文件内容不含有敏感信息");
        return resultParams;
    }

    /** 电话号码校验 */
    public static ArrayList<String> checkTel(String text) {
        ArrayList<String> telList = new ArrayList<String>();
        boolean hasTel = false;
        Pattern pattern = Pattern.compile("(?<!\\d)(?:(?:1[358]\\d{9})|(?:861[358]\\d{9}))(?!\\d)");
        Matcher matcher = pattern.matcher(text);
        //StringBuffer bf = new StringBuffer(64);
        while (matcher.find()) {
            telList.add(matcher.group());
            //bf.append(matcher.group()).append(",");
        }
        //int len = bf.length();
        int len = telList.size();
        if (len > 0) {
            //bf.deleteCharAt(len - 1);
            System.out.println(telList.toString());
        }

		/*if (null == bf.toString().trim() || "" == bf.toString().trim() || len == 0) {
			return hasTel;
		} else if (len > 0) {
			hasTel = true;
			return hasTel;
		}*/
        return telList;
    }

    /** 两级认证身份证号码;初步:18位校验;精确:有效性校验 */
    public static ArrayList<String> twoLevelAuthIDCard(String FileContent) {
        ArrayList<String> idCardList = new ArrayList<String>();
        String regex = "\\d{17}[[0-9],0-9xX]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(FileContent);
        boolean returnFlag = false;
        int count = 0;
        while (matcher.find()) {
            returnFlag = precAuthIDCard(matcher.group());
            if (returnFlag == true) {
                idCardList.add(matcher.group());
                count = 1;
            }
        }
        if (count == 1) {
            System.out.println("返回的是true，说明含有身份证");
            //return true;
        } else {
            System.out.println("返回的是false，说明不含有身份证");
        }
        return idCardList;
    }

    public static boolean precAuthIDCard(String FileContent) {
        String idCard = FileContent;
        Pattern pattern1 = Pattern
                .compile("^(\\d{6})(19|20)(\\d{2})(1[0-2]|0[1-9])(0[1-9]|[1-2][0-9]|3[0-1])(\\d{3})(\\d|X|x)?$");
        Matcher matcher = pattern1.matcher(idCard);
        int[] prefix = new int[] { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };
        int[] suffix = new int[] { 1, 0, 10, 9, 8, 7, 6, 5, 4, 3, 2 };
        if (matcher.matches()) {
            Map<String, String> cityMap = initCityMap();
            if (cityMap.get(idCard.substring(0, 2)) == null) {
                return false;
            }
            int idCardWiSum = 0; // 用来保存前17位各自乖以加权因子后的总和
            for (int i = 0; i < 17; i++) {
                idCardWiSum += Integer.valueOf(idCard.substring(i, i + 1)) * prefix[i];
            }

            int idCardMod = idCardWiSum % 11;// 计算出校验码所在数组的位置
            String idCardLast = idCard.substring(17);// 得到最后一位身份证号码

            // 如果等于2，则说明校验码是10，身份证号码最后一位应该是X
            if (idCardMod == 2) {
                if (idCardLast.equalsIgnoreCase("x")) {
                    System.out.println(idCard);
                    return true;
                } else {
                    return false;
                }
            } else {
                // 用计算出的验证码与最后一位身份证号码匹配，如果一致，说明通过，否则是无效的身份证号码
                if (idCardLast.equals(suffix[idCardMod] + "")) {
                    System.out.println(idCard);
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    // IMEI和IMSI扫描(初步扫描，只要含有15位数字,即表示含有)
    public static ArrayList<String> scanMEAndMS(String FileContent) {
        ArrayList<String> ESList = new ArrayList<String>();
        String reg14 = "\\d{14}";
        String reg15 = "\\d{15}";
        String reg16 = "\\d{16}";
        Pattern pattern14 = Pattern.compile(reg14);
        Matcher matcher14 = pattern14.matcher(FileContent);
        Pattern pattern15 = Pattern.compile(reg15);
        Matcher matcher15 = pattern15.matcher(FileContent);
        Pattern pattern16 = Pattern.compile(reg16);
        Matcher matcher16 = pattern16.matcher(FileContent);
        boolean hasMatcher16 = matcher16.find();
        boolean hasMEOrMS = false;
        if (hasMatcher16 == false) {
            // while (matcher15.find()){
            // System.out.println ("含有IMEI或者IMSI等敏感信息");
            // System.out.println (matcher15.group());
            // hasMEOrMS=true;
            // }
            if (matcher15.find() == false) {
                if (matcher14.find() == false) {

                } else {
                    System.out.println(matcher14.group());
                    ESList.add(matcher14.group());
//					hasMEOrMS = true;
                }
            } else {
                System.out.println(matcher15.group());
                ESList.add(matcher15.group());
//				hasMEOrMS = true;
            }
        } else {
            System.out.println(matcher16.group());
            ESList.add(matcher16.group());
//			hasMEOrMS = true;
        }
        return ESList;
    }

    private static Map<String, String> initCityMap() {
        Map<String, String> cityMap = new HashMap<String, String>();
        cityMap.put("11", "北京");
        cityMap.put("12", "天津");
        cityMap.put("13", "河北");
        cityMap.put("14", "山西");
        cityMap.put("15", "内蒙古");
        cityMap.put("21", "辽宁");
        cityMap.put("22", "吉林");
        cityMap.put("23", "黑龙江");
        cityMap.put("31", "上海");
        cityMap.put("32", "江苏");
        cityMap.put("33", "浙江");
        cityMap.put("34", "安徽");
        cityMap.put("35", "福建");
        cityMap.put("36", "江西");
        cityMap.put("37", "山东");
        cityMap.put("41", "河南");
        cityMap.put("42", "湖北");
        cityMap.put("43", "湖南");
        cityMap.put("44", "广东");
        cityMap.put("45", "广西");
        cityMap.put("46", "海南");
        cityMap.put("50", "重庆");
        cityMap.put("51", "四川");
        cityMap.put("52", "贵州");
        cityMap.put("53", "云南");
        cityMap.put("54", "西藏");
        cityMap.put("61", "陕西");
        cityMap.put("62", "甘肃");
        cityMap.put("63", "青海");
        cityMap.put("64", "宁夏");
        cityMap.put("65", "新疆");
        // cityMap.put("71", "台湾");
        // cityMap.put("81", "香港");
        // cityMap.put("82", "澳门");
        // cityMap.put("91", "国外");
        // System.out.println(cityMap.keySet().size());
        return cityMap;
    }
}
