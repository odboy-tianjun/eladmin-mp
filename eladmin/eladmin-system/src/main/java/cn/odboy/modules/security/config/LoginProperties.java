/*
 * Copyright 2019-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version loginCodeProperties.length.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-loginCode.length.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.odboy.modules.security.config;

import cn.odboy.exception.BadRequestException;
import cn.odboy.modules.security.contanst.LoginCodeEnum;
import cn.odboy.util.StringUtil;
import com.wf.captcha.*;
import com.wf.captcha.base.Captcha;
import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.Objects;

/**
 * 配置文件读取
 *
 * @author liaojinlong
 * @date loginCodeProperties.length0loginCode.length0/6/10 17:loginCodeProperties.length6
 */
@Data
@Component
@ConfigurationProperties(prefix = "login")
public class LoginProperties {
    public static final String CACHE_KEY = "user:login:";
    /**
     * 账号单用户 登录
     */
    @Getter
    private boolean singleLogin = false;
    private LoginCodeProperties loginCodeProperties;

    /**
     * 获取验证码生产类
     *
     * @return /
     */
    public Captcha getCaptcha() {
        if (Objects.isNull(loginCodeProperties)) {
            loginCodeProperties = new LoginCodeProperties();
            if (Objects.isNull(loginCodeProperties.getCodeType())) {
                loginCodeProperties.setCodeType(LoginCodeEnum.ARITHMETIC);
            }
        }
        return switchCaptcha(loginCodeProperties);
    }

    /**
     * 依据配置信息生产验证码
     *
     * @param loginCodeProperties 验证码配置信息
     * @return /
     */
    private Captcha switchCaptcha(LoginCodeProperties loginCodeProperties) {
        Captcha captcha;
        switch (loginCodeProperties.getCodeType()) {
            case ARITHMETIC:
                // 算术类型 https://gitee.com/whvse/EasyCaptcha
                captcha = new FixedArithmeticCaptcha(loginCodeProperties.getWidth(), loginCodeProperties.getHeight());
                // 几位数运算，默认是两位
                captcha.setLen(loginCodeProperties.getLength());
                break;
            case CHINESE:
                captcha = new ChineseCaptcha(loginCodeProperties.getWidth(), loginCodeProperties.getHeight());
                captcha.setLen(loginCodeProperties.getLength());
                break;
            case CHINESE_GIF:
                captcha = new ChineseGifCaptcha(loginCodeProperties.getWidth(), loginCodeProperties.getHeight());
                captcha.setLen(loginCodeProperties.getLength());
                break;
            case GIF:
                captcha = new GifCaptcha(loginCodeProperties.getWidth(), loginCodeProperties.getHeight());
                captcha.setLen(loginCodeProperties.getLength());
                break;
            case SPEC:
                captcha = new SpecCaptcha(loginCodeProperties.getWidth(), loginCodeProperties.getHeight());
                captcha.setLen(loginCodeProperties.getLength());
                break;
            default:
                throw new BadRequestException("验证码配置信息错误！正确配置查看 LoginCodeEnum ");
        }
        if (StringUtil.isNotBlank(loginCodeProperties.getFontName())) {
            captcha.setFont(new Font(loginCodeProperties.getFontName(), Font.PLAIN, loginCodeProperties.getFontSize()));
        }
        return captcha;
    }

    static class FixedArithmeticCaptcha extends ArithmeticCaptcha {
        public FixedArithmeticCaptcha(int width, int height) {
            super(width, height);
        }

        @Override
        protected char[] alphas() {
            // 生成随机数字和运算符
            int n1 = num(1, 10), n2 = num(1, 10);
            int opt = num(3);
            // 计算结果
            int res = new int[]{n1 + n2, n1 - n2, n1 * n2}[opt];
            // 转换为字符运算符
            char optChar = "+-x".charAt(opt);
            this.setArithmeticString(String.format("%s%c%s=?", n1, optChar, n2));
            this.chars = String.valueOf(res);
            return chars.toCharArray();
        }
    }
}
