package com.task.mail;

import com.task.bean.Task;
import com.task.bean.User;
import com.task.utils.DateUtils;
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
    private JavaMailSender javaMailSender;//spring 的 mail 库对象,spring-boot-starter-mail
    @Autowired
    private VelocityEngine velocityEngine;//邮件模板引擎,spring-boot-starter-velocity
    @Value("${spring.mail.username}")
    private String username;//application.properites 的属性读取出来,发件邮箱

    /**
     * 发送验证码邮件
     *
     * @param email
     * @param captcha
     * @return
     */
    public boolean sendCaptchaEmail(String email, String captcha) {
        Map<String, Object> contents = new HashMap<>();
        contents.put("email", email);
        contents.put("captcha", captcha);
        return sendTemplateMail(email, "请输入你的验证码",
                "get_captcha.vm", contents);
    }

    /**
     * 发送表单添加邮件
     *
     * @param task
     * @param user
     * @return
     */
    public boolean sendTaskAddEmail(Task task, User user) {
        Map<String, Object> contents = new HashMap<>();
        contents.put("email", user.getEmail());
        if (user.getNickName() != null) {
            contents.put("nickName", user.getNickName());
        }
        contents.put("taskName", task.getTitle());
        contents.put("endTime", DateUtils.formatData(task.getDeadlineTime()));
        return sendTemplateMail(user.getEmail(), "你有一个新的表单任务待提交",
                "task_add.vm", contents);
    }

    /**
     * 发送表单提醒邮件
     *
     * @param task
     * @param user
     * @return
     */
    public boolean sendTaskNotifyEmail(Task task, User user) {
        Map<String, Object> contents = new HashMap<>();
        contents.put("email", user.getEmail());
        if (user.getNickName() != null) {
            contents.put("nickName", user.getNickName());
        }
        contents.put("taskName", task.getTitle());
        contents.put("endTime", DateUtils.formatData(task.getDeadlineTime()));
        return sendTemplateMail(user.getEmail(),
                "管理员提醒你提交表单:" + task.getTitle(),
                "task_notify.vm", contents);
    }

    /**
     * 发送表单验证结果邮件
     *
     * @param task
     * @param user
     * @return
     */
    public boolean sendTaskVerifyEmail(Task task, User user, boolean isVerify) {
        Map<String, Object> contents = new HashMap<>();
        contents.put("email", user.getEmail());
        if (user.getNickName() != null) {
            contents.put("nickName", user.getNickName());
        }
        contents.put("taskName", task.getTitle());
        String resultMsg = "已被管理员驳回,请尽快修改重新提交";
        if (isVerify) {
            resultMsg = "已被管理员审核通过!";
        }
        return sendTemplateMail(user.getEmail(),
                "你的表单:" + task.getTitle() + " " + resultMsg,
                "task_notify.vm", contents);
    }

    /**
     * 调用 spring 的邮件模块API, 具体发送邮件
     *
     * @param sendTo
     * @param title
     * @param tempFilePath
     * @param contents
     * @return
     */
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
