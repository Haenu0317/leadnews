package com.heima.common.baiduyun.result;

import lombok.Data;
import lombok.ToString;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *     审核内容的返回结果
 * </p>
 * @Classname CensorResult
 * @Date 2023/7/13 9:39
 * @Created ZFC
 */
@Data
@ToString
public class CensorResult {
    /**
     * 	请求唯一id
     */
    private Long log_id;

    /**
     * 审核结果，可取值：合规、不合规、疑似、审核失败
     */
    private String conclusion;

    /**
     * 	不合规项描述信息
     */
    private String msg;

    /**
     * 0:低质灌水、1:违禁违规、2:文本色情、3:敏感信息、4:恶意推广、5:低俗辱骂 6:恶意推广-联系方式、7:恶意推广-软文推广
     */
    private Integer subType;

    /**
     * 审核结果类型，可取值1.合规，2.不合规，3.疑似，4.审核失败
     */
    private Integer conclusionType;

    /**
     * 敏感关键字
     */
    private List<String> words;


     /**
     * 文本审核
     * @param jsonObject
     * @return
     */
    public CensorResult textCacn(JSONObject jsonObject) throws JSONException {
        CensorResult censorResult = new CensorResult();

        //审核内容为空直接返回
        if (jsonObject.length() == 0 || jsonObject == null){
            return censorResult;
        }

        //唯一编号
        censorResult.setLog_id((Long) jsonObject.get("log_id"));
        //审核结果类型，可取值1、2、3、4，分别代表1：合规，2：不合规，3：疑似，4：审核失败
        Integer conclusionType = (Integer) jsonObject.get("conclusionType");
        censorResult.setConclusionType(conclusionType);


        if (conclusionType!=1){
            JSONArray jsonArray = (JSONArray) jsonObject.get("data");

            for (int i = 0; i < jsonArray.length(); i++) {
                // 获取当前位置上的 JSONObject
                JSONObject jsonData = jsonArray.getJSONObject(i);
                // 获取 JSONObject 中的 msg 属性值 并赋值
                censorResult.setMsg((String) jsonData.get("msg"));
                // 获取 JSONObject 中的 conclusion 属性值 并赋值
                censorResult.setConclusion((String) jsonData.get("conclusion"));
                // 获取 JSONObject 中的 msg 属性值 并赋值
                censorResult.setMsg((String) jsonData.get("msg"));
                // 获取 JSONObject 中的 subType 属性值 并赋值
                censorResult.setSubType((Integer) jsonData.get("subType"));

                //文本违规原因的详细信息 遍历
                JSONArray hitsArray = jsonData.getJSONArray("hits");

                if (hitsArray!=null && hitsArray.length()>0){
                    for (int h = 0; h < hitsArray.length(); h++) {
                        JSONObject hits_key = hitsArray.getJSONObject(h);
                        //获取文本命中词库的关键词
                        JSONArray words = hits_key.getJSONArray("words");

                        if (words.length() >0 && words != null){
                            //定义list 存储审核失败的文字
                            List<String> list = new ArrayList<>();
                            for (int w = 0; w < words.length(); w++) {
                                String word_name = words.getString(w);
                                list.add(word_name);
                            }
                            censorResult.setWords(list);
                        }
                    }
                }
            }
        }

        return censorResult;
    }

    /**
     * 图片审核
     * @param jsonObject
     * @return
     */
    public CensorResult imageScan(JSONObject jsonObject) throws JSONException {
        CensorResult censorResult = new CensorResult();

        //审核内容为空直接返回
        if (jsonObject.length() == 0 || jsonObject == null){
            return censorResult;
        }

        //唯一编号
        censorResult.setLog_id((Long) jsonObject.get("log_id"));
        //审核结果类型，可取值1、2、3、4，分别代表1：合规，2：不合规，3：疑似，4：审核失败
        Integer conclusionType = (Integer) jsonObject.get("conclusionType");
        censorResult.setConclusionType(conclusionType);

        if (conclusionType!=1){
            JSONArray jsonArray = (JSONArray) jsonObject.get("data");

            for (int i = 0; i < jsonArray.length(); i++) {
                // 获取当前位置上的 JSONObject
                JSONObject jsonData = jsonArray.getJSONObject(i);
                // 获取 JSONObject 中的 msg 属性值 并赋值
                censorResult.setMsg((String) jsonData.get("msg"));
                // 获取 JSONObject 中的 conclusion 属性值 并赋值
                censorResult.setConclusion((String) jsonData.get("conclusion"));
                // 获取 JSONObject 中的 msg 属性值 并赋值
                censorResult.setMsg((String) jsonData.get("msg"));
                // 获取 JSONObject 中的 subType 属性值 并赋值
                censorResult.setSubType((Integer) jsonData.get("subType"));

                //文本违规原因的详细信息 遍历
                JSONArray starsList = jsonData.getJSONArray("stars");

                if (starsList.length() >0 && starsList != null){
                    //定义list 存储审核失败的文字
                    List<String> list = new ArrayList<>();
                    for (int j = 0; j < starsList.length(); j++) {
                        JSONObject starsData = starsList.getJSONObject(j);
                        list.add(starsData.getString("name"));
                    }
                    censorResult.setWords(list);
                }
            }

        }
        return censorResult;
    }
}
