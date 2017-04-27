package com.task.mail;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by blanke on 2017/4/26.
 */
@Service
public class MailUtils {
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private VelocityEngine velocityEngine;
    @Value("${spring.mail.username}")
    private String username;

    public boolean sendRegisterEmail(String email, String captcha) {
        Map<String, Object> contents = new HashMap<>();
        contents.put("email", email);
        contents.put("captcha", captcha);
        return sendTemplateMail(email, "请验证你的注册信息", "register.vm",
                contents);
    }

    private boolean sendTemplateMail(String sendTo, String title,
                                     String tempFilePath,
                                     Map<String, Object> contents) {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "utf-8");
                message.setTo(sendTo);
                message.setFrom(username);
                message.setSubject(title);
                message.setText(VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
                        tempFilePath, "utf-8", contents), true);
            }
        };
        try {
            this.javaMailSender.send(preparator);
            return true;
        } catch (MailException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
