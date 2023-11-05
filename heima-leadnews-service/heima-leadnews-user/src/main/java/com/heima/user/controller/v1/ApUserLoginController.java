package com.heima.user.controller.v1;

import com.heima.model.common.dtos.LoginDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.user.service.ApUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "用户相关接口")
@RestController
@RequestMapping("/api/v1/login")
@RequiredArgsConstructor
public class ApUserLoginController {

    @Resource
    private ApUserService apUserService;


    @PostMapping("/login_auth")
    @ApiOperation("登录接口")
    public ResponseResult login(@RequestBody LoginDto loginDto){
       return apUserService.login(loginDto);
    }


}
