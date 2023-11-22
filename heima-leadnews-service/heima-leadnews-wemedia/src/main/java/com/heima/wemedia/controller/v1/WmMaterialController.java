package com.heima.wemedia.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.utils.thread.WmThreadLocalUtil;
import com.heima.wemedia.service.WmMaterialService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/v1/material")
public class WmMaterialController {

    @Resource
    private WmMaterialService wmMaterialService;


    @PostMapping("/upload_picture")
    public ResponseResult uploadPicture(MultipartFile multipartFile) {
        return wmMaterialService.uploadPicture(multipartFile);
    }

    @PostMapping("/list")
    public ResponseResult findList(@RequestBody WmMaterialDto dto) {
        return wmMaterialService.findList(dto);
    }

    @GetMapping("/del_picture/{id}")
    public ResponseResult delPicture(@PathVariable Integer id){
        if (id == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"无效参数");
        }
        return wmMaterialService.delPicture(id);
    }

    @GetMapping("/collect/{id}")
    public ResponseResult collect(@PathVariable Integer id){
        if (id == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"无效参数");
        }
        return wmMaterialService.collect(WmThreadLocalUtil.getUser().getId(), id);
    }

    @GetMapping("/cancel_collect/{id}")
    public ResponseResult cancelCollect(@PathVariable Integer id){
        if (id == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"无效参数");
        }
        return wmMaterialService.cancelCollect(WmThreadLocalUtil.getUser().getId(), id);
    }

}
