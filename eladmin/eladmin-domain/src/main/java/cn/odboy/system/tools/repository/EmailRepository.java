package cn.odboy.system.tools.repository;

import cn.hutool.extra.mail.Mail;
import cn.hutool.extra.mail.MailAccount;
import cn.odboy.system.tools.domain.EmailConfig;
import cn.odboy.system.tools.model.EmailVo;
import cn.odboy.exception.BadRequestException;
import cn.odboy.system.tools.service.EmailConfigService;
import cn.odboy.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 邮箱 控制器
 *
 * @author odboy
 * @date 2025-01-17
 */
@Component
@RequiredArgsConstructor
public class EmailRepository {
    private final EmailConfigService emailConfigService;
    @Transactional(rollbackFor = Exception.class)
    public void send(EmailVo emailVo) {
        EmailConfig emailConfig = emailConfigService.describeEmailConfig();
        if (emailConfig.getPass() == null) {
            throw new BadRequestException("请先配置，再操作");
        }
        // 封装
        MailAccount account = new MailAccount();
        // 设置用户
        String user = emailConfig.getFromUser().split("@")[0];
        account.setUser(user);
        account.setHost(emailConfig.getHost());
        account.setPort(Integer.parseInt(emailConfig.getPort()));
        account.setAuth(true);
        try {
            // 对称解密
            account.setPass(EncryptUtil.desDecrypt(emailConfig.getPass()));
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
        account.setFrom(emailConfig.getUser() + "<" + emailConfig.getFromUser() + ">");
        // ssl方式发送
        account.setSslEnable(true);
        // 使用STARTTLS安全连接
        account.setStarttlsEnable(true);
        // 解决jdk8之后默认禁用部分tls协议，导致邮件发送失败的问题
        account.setSslProtocols("TLSv1 TLSv1.1 TLSv1.2");
        String content = emailVo.getContent();
        // 发送
        try {
            int size = emailVo.getTos().size();
            Mail.create(account)
                    .setTos(emailVo.getTos().toArray(new String[size]))
                    .setTitle(emailVo.getSubject())
                    .setContent(content)
                    .setHtml(true)
                    //关闭session
                    .setUseGlobalSession(false)
                    .send();
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }
}
