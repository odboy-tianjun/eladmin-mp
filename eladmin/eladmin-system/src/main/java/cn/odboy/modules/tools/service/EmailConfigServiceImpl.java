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
package cn.odboy.modules.tools.service;

import cn.odboy.system.tools.domain.EmailConfig;
import cn.odboy.modules.tools.mapper.EmailConfigMapper;
import cn.odboy.system.tools.service.EmailConfigService;
import cn.odboy.util.EncryptUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Zheng Jie
 * @date 2018-12-26
 */
@Service
@RequiredArgsConstructor
public class EmailConfigServiceImpl extends ServiceImpl<EmailConfigMapper, EmailConfig> implements EmailConfigService {
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmailConfig updateEmailConfig(EmailConfig emailConfig) throws Exception {
        EmailConfig localEmailConfig = describeEmailConfig();
        if (!emailConfig.getPass().equals(localEmailConfig.getPass())) {
            // 对称加密
            emailConfig.setPass(EncryptUtil.desEncrypt(emailConfig.getPass()));
        }
        emailConfig.setId(localEmailConfig.getId());
        saveOrUpdate(emailConfig);
        return emailConfig;
    }

    @Override
    public EmailConfig describeEmailConfig() {
        EmailConfig emailConfig = getById(1L);
        return emailConfig == null ? new EmailConfig() : emailConfig;
    }
}
