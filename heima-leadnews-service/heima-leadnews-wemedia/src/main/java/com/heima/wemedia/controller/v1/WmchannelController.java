package com.heima.wemedia.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.wemedia.service.WmChannelService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/v1/channel")
public class WmchannelController {

    @Resource
    private WmChannelService wmChannelService;

    @GetMapping("/channels")
    public ResponseResult findAll() {
        return wmChannelService.findAll();
    }
}