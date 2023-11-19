package com.heima.wemedia.test;

import com.baidu.aip.contentcensor.AipContentCensor;
import com.baidu.aip.contentcensor.EImgType;
import com.heima.common.baiduyun.BaiDuYunContentModerationUtil;
import com.heima.common.baiduyun.result.CensorResult;
import com.heima.wemedia.WemediaApplication;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBootTest(classes = WemediaApplication.class)
@RunWith(SpringRunner.class)
public class MyTestContent {

    @Autowired
    private BaiDuYunContentModerationUtil contentModerationUtil;

    @Test
    public void getTextContent() throws JSONException {
        AipContentCensor aipContentCensor = contentModerationUtil.contentScan();
        //
        String compliance = "在这个充满机遇和挑战的时代，我们需要更多的勇气和创新精神，才能在激烈的市场竞争中获得成功。";
        String irregularity = "冰毒、国家主席";
        String suspected = "这里有一些涉及敏感话题的文本，包括色情、暴力、恐怖主义等。";
        JSONObject jsonObject = aipContentCensor.textCensorUserDefined(irregularity);

        CensorResult censorResult = new CensorResult();
        System.out.println("censorResult.getCensorResult(jsonObject) = " + censorResult.textCacn(jsonObject));
    }

    @Test
    public void getImageContent() throws JSONException {
        AipContentCensor aipContentCensor = contentModerationUtil.contentScan();
        String imageUrl = "https://img-blog.csdnimg.cn/20200718111324945.png";
        String weigui = "http://agzy.youth.cn/zt/2019qmjyl/kglx1/201903/W020190330297608130021.jpg";
        JSONObject jsonObject = aipContentCensor.imageCensorUserDefined(weigui, EImgType.URL, null);

        CensorResult censorResult = new CensorResult();
        System.out.println("censorResult.getCensorResult(jsonObject) = " + censorResult.imageScan(jsonObject));
    }
}
