package top.jrhong.library.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

@Component
public class EmailMessageUtil{

    @Autowired
    JavaMailSenderImpl mailSender;

    @Value("${check.mail.sender}")
    private String sender;

    @Value("${check.mail.subject}")
    private String subject;

    @Value("${check.mail.greetings}")
    private String greetings;

    @Value("${check.mail.end}")
    private String end;
    /**
     * 发送邮件
     *
     * @return 提示信息
     */
    public String sendMessage(String email,String code)throws MailException {
        //引入编码工具类
        SimpleMailMessage message = new SimpleMailMessage();
        //发送者
        message.setFrom(sender);
        //发送邮件地址
        message.setTo(email);
        //标题
        message.setSubject(subject);
        //内容
        message.setText(greetings+code+end);
        mailSender.send(message);
        return "send success";
    }

}
