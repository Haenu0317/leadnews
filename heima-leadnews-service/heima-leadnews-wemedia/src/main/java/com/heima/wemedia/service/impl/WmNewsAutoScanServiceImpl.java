package com.heima.wemedia.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONArray;
import com.baidu.aip.contentcensor.AipContentCensor;
import com.heima.apis.article.IArticleClient;
import com.heima.common.baiduyun.BaiDuYunContentModerationUtil;
import com.heima.common.baiduyun.result.CensorResult;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.wemedia.mapper.WmChannelMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmUserMapper;
import com.heima.wemedia.service.WmNewsAutoScanService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
@Transactional
public class WmNewsAutoScanServiceImpl implements WmNewsAutoScanService {

    @Resource
    private WmNewsMapper wmNewsMapper;

    @Resource
    private BaiDuYunContentModerationUtil contentModerationUtil;

    @Resource
    private FileStorageService fileStorageService;

    @Resource
    private IArticleClient articleClient;

    @Resource
    private WmChannelMapper wmChannelMapper;

    @Resource
    private WmUserMapper wmUserMapper;

    /**
     * 自媒体文章审核
     *
     * @param id 自媒体文章id
     */
    @Override
    @Async //异步方法
    public void autoScanWmNews(Integer id) {

        WmNews wmNews = wmNewsMapper.selectById(id);
        if (wmNews == null) {
            throw new RuntimeException("WmNewsAutoScanServiceImpl-文章不存在");
        }

        if (wmNews.getStatus().equals(WmNews.Status.SUBMIT.getCode())) {
            //提取
            Map<String, Object> textAndImages = handleTextAndImages(wmNews);

            //审核文本
            boolean isTextScan = handleTextScan((String) textAndImages.get("content"), wmNews);

            if (!isTextScan) {
                log.error("WmNewsAutoScanServiceImpl-文章审核，当前文章中存在违规内容");
                return;
            }

            //审核图片
            boolean isImageScan = handleImageScan((List<String>) textAndImages.get("images"), wmNews);
            if (!isImageScan){
                log.error("WmNewsAutoScanServiceImpl-文章审核，当前文章中图片存在违规内容");
                return;
            }


            //4.审核成功，保存app端的相关的文章数据
            ResponseResult responseResult = saveAppArticle(wmNews);
            if (!responseResult.getCode().equals(200)) {
                throw new RuntimeException("WmNewsAutoScanServiceImpl-文章审核，保存app端相关文章数据失败");
            }
            //回填article_id
            wmNews.setArticleId((Long) responseResult.getData());
            updateWmNews(wmNews,(short) 9,"审核成功");

        }


    }

    /**
     * 保存app端的文章数据
     *
     * @param wmNews
     * @return
     */
    private ResponseResult saveAppArticle(WmNews wmNews) {
        ArticleDto articleDto = new ArticleDto();
        BeanUtil.copyProperties(wmNews, articleDto);
        if (wmNews.getArticleId() == null){
            articleDto.setId(null);
        }
        articleDto.setLayout(Integer.valueOf(wmNews.getType()));

        //频道
        WmChannel wmChannel = wmChannelMapper.selectById(wmNews.getChannelId());
        if (wmChannel != null) {
            articleDto.setChannelName(wmChannel.getName());
        }

        //作者
        articleDto.setAuthorId(wmNews.getUserId());
        WmUser wmUser = wmUserMapper.selectById(wmNews.getUserId());
        if (wmUser != null) {
            articleDto.setAuthorName(wmUser.getName());
        }

        //设置文章id
        if (wmNews.getArticleId() != null) {
            articleDto.setId(wmNews.getArticleId());
        }
        articleDto.setCreatedTime(new Date());
        ResponseResult responseResult = articleClient.saveArticle(articleDto);
        return responseResult;
    }

    /**
     * 审核图片
     *
     * @param images
     * @param wmNews
     * @return
     */
    private boolean handleImageScan(List<String> images, WmNews wmNews) {
        boolean flag = true;
        if (images == null || images.isEmpty()) {
            return flag;
        }

        //去重
        images = images.stream().distinct().collect(Collectors.toList());
        List<byte[]> imageList = new ArrayList<>();

        for (String image : images) {
            byte[] bytes = fileStorageService.downLoadFile(image);
            imageList.add(bytes);
        }
        AipContentCensor aipContentCensor = contentModerationUtil.contentScan();
        CensorResult censorResult = new CensorResult();
        try {
            for (byte[] bytes : imageList) {
                censorResult = censorResult.imageScan(aipContentCensor.imageCensorUserDefined(bytes, null));
                //审核失败
                if (censorResult.getConclusionType() == 2) {
                    flag = false;
                    updateWmNews(wmNews, 2, "当前文章中图片存在违规内容");

                } else if (censorResult.getConclusionType() == 3) {
                    flag = false;
                    updateWmNews(wmNews, 3, "当前文章中图片存在不确定内容");
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return flag;
    }

    /**
     * 审核文字
     *
     * @param content
     * @param wmNews
     * @return
     */
    private boolean handleTextScan(String content, WmNews wmNews) {
        boolean flag = true;

        if ((wmNews.getTitle() + "-" + content).length() == 1) {
            return flag;
        }

        AipContentCensor aipContentCensor = contentModerationUtil.contentScan();
        JSONObject jsonObject = aipContentCensor.textCensorUserDefined(content);
        CensorResult censorResult = new CensorResult();
        try {
            censorResult = censorResult.textCacn(jsonObject);
            //审核失败
            if (censorResult.getConclusionType() == 2) {
                flag = false;
                updateWmNews(wmNews, 2, "当前文章中存在违规内容");

            } else if (censorResult.getConclusionType() == 3) {
                flag = false;
                updateWmNews(wmNews, 3, "当前文章中存在不确定内容");
            }
        } catch (JSONException e) {
            flag = false;
            throw new RuntimeException(e);
        }


        return flag;
    }

    private void updateWmNews(WmNews wmNews, int status, String reason) {
        wmNews.setStatus((short) status);
        wmNews.setReason(reason);
        wmNewsMapper.updateById(wmNews);
    }

    /**
     * 从自媒体文章中提取文本和图片
     *
     * @param wmNews
     * @return
     */
    private Map<String, Object> handleTextAndImages(WmNews wmNews) {
        StringBuilder stringBuilder = new StringBuilder();
        List<String> images = new ArrayList<>();
        if (StringUtils.isNotBlank(wmNews.getContent())) {
            List<Map> maps = JSONArray.parseArray(wmNews.getContent(), Map.class);

            for (Map map : maps) {
                if (map.get("type").equals("text")) {
                    stringBuilder.append(map.get("value"));
                }

                if (map.get("type").equals("image")) {
                    images.add((String) map.get("value"));
                }
            }
        }
        //提取封面
        if (StringUtils.isNotBlank(wmNews.getImages())) {
            String[] split = wmNews.getImages().split(",");
            images.addAll(Arrays.asList(split));
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("content", stringBuilder.toString());
        resultMap.put("images", images);
        return resultMap;
    }
}