/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package cn.odboy.modules.security.rest;

import cn.hutool.core.util.IdUtil;
import cn.odboy.system.logging.annotation.Log;
import cn.odboy.exception.BadRequestException;
import cn.odboy.modules.security.config.LoginProperties;
import cn.odboy.modules.security.contanst.LoginCodeEnum;
import cn.odboy.modules.security.context.TokenProvider;
import cn.odboy.modules.security.service.OnlineUserServiceImpl;
import cn.odboy.security.RsaProperties;
import cn.odboy.security.annotation.AnonymousDeleteMapping;
import cn.odboy.security.annotation.AnonymousGetMapping;
import cn.odboy.security.annotation.AnonymousPostMapping;
import cn.odboy.modules.security.config.SecurityProperties;
import cn.odboy.system.security.model.AuthUserDto;
import cn.odboy.system.security.model.JwtUserDto;
import cn.odboy.util.RedisUtil;
import cn.odboy.util.RsaUtil;
import cn.odboy.util.SecurityUtil;
import cn.odboy.util.StringUtil;
import com.wf.captcha.base.Captcha;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Zheng Jie
 * @date 2018-11-23
 * 授权、根据token获取用户详细信息
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Api(tags = "系统：系统授权接口")
public class AuthorizationController {
    private final SecurityProperties properties;
    private final RedisUtil redisUtil;
    private final OnlineUserServiceImpl onlineUserService;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserDetailsService userDetailsService;
    private final LoginProperties loginProperties;

    @Log("用户登录")
    @ApiOperation("登录授权")
    @AnonymousPostMapping(value = "/login")
    public ResponseEntity<Object> login(@Validated @RequestBody AuthUserDto authUser, HttpServletRequest request) throws Exception {
        // 密码解密
        String password = RsaUtil.decryptByPrivateKey(RsaProperties.privateKey, authUser.getPassword());
        // 查询验证码
        String code = redisUtil.get(authUser.getUuid(), String.class);
        // 清除验证码
        redisUtil.del(authUser.getUuid());
        if (StringUtil.isBlank(code)) {
            throw new BadRequestException("验证码不存在或已过期");
        }
        if (StringUtil.isBlank(authUser.getCode()) || !authUser.getCode().equalsIgnoreCase(code)) {
            throw new BadRequestException("验证码错误");
        }
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(authUser.getUsername(), password);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final JwtUserDto jwtUserDto = (JwtUserDto) authentication.getPrincipal();
        String token = tokenProvider.createToken(jwtUserDto);
        // 返回 token 与 用户信息
        Map<String, Object> authInfo = new HashMap<>(2) {{
            put("token", properties.getTokenStartWith() + token);
            put("user", jwtUserDto);
        }};
        if (loginProperties.isSingleLogin()) {
            // 踢掉之前已经登录的token
            onlineUserService.kickOutForUsername(authUser.getUsername());
        }
        // 保存在线信息
        onlineUserService.save(jwtUserDto, token, request);
        // 返回登录信息
        return ResponseEntity.ok(authInfo);
    }

    @Log("获取授权码")
    @ApiOperation("获取授权码")
    @AnonymousPostMapping(value = "/getAccessToken")
    public ResponseEntity<Object> getAccessToken(@Validated @RequestBody AuthUserDto authUser, HttpServletRequest request) throws Exception {
        if (authUser.getUsername() == null && authUser.getPassword() == null) {
            throw new BadRequestException("获取授权码失败, 无效授权信息");
        }
        // 生成令牌与第三方系统获取令牌方式
        UserDetails userDetails = userDetailsService.loadUserByUsername(authUser.getUsername());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final JwtUserDto jwtUserDto = (JwtUserDto) authentication.getPrincipal();
        String token = tokenProvider.createToken(jwtUserDto);
        // 返回 token 与 用户信息
        Map<String, Object> authInfo = new HashMap<>(2) {{
            put("token", properties.getTokenStartWith() + token);
            put("user", jwtUserDto);
        }};
        if (loginProperties.isSingleLogin()) {
            // 踢掉之前已经登录的token
            onlineUserService.kickOutForUsername(authUser.getUsername());
        }
        // 保存在线信息
        onlineUserService.save(jwtUserDto, token, request);
        // 返回登录信息
        return ResponseEntity.ok(authInfo);
    }

    @ApiOperation("获取用户信息")
    @GetMapping(value = "/info")
    public ResponseEntity<UserDetails> getUserInfo() {
        return ResponseEntity.ok(SecurityUtil.getCurrentUser());
    }

    @ApiOperation("获取验证码")
    @AnonymousGetMapping(value = "/code")
    public ResponseEntity<Object> getCode() {
        // 获取运算的结果
        Captcha captcha = loginProperties.getCaptcha();
        String uuid = properties.getCodeKey() + IdUtil.simpleUUID();
        //当验证码类型为 arithmetic时且长度 >= 2 时，captcha.text()的结果有几率为浮点型
        String captchaValue = captcha.text();
        if (captcha.getCharType() - 1 == LoginCodeEnum.ARITHMETIC.ordinal() && captchaValue.contains(".")) {
            captchaValue = captchaValue.split("\\.")[0];
        }
        // 保存
        redisUtil.set(uuid, captchaValue, loginProperties.getLoginCodeProperties().getExpiration(), TimeUnit.MINUTES);
        // 验证码信息
        Map<String, Object> imgResult = new HashMap<String, Object>(2) {{
            put("img", captcha.toBase64());
            put("uuid", uuid);
        }};
        return ResponseEntity.ok(imgResult);
    }

    @ApiOperation("退出登录")
    @AnonymousDeleteMapping(value = "/logout")
    public ResponseEntity<Object> logout(HttpServletRequest request) {
        String token = tokenProvider.getToken(request);
        onlineUserService.logout(token);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
