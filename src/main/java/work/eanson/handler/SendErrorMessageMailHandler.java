package work.eanson.handler;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Long live freedom and fraternity, No 996
 * <pre>
 * 相信使用过Spring的众多开发者都知道Spring提供了非常好用的JavaMailSender接口实现邮件发送。
 * 在Spring Boot的Starter模块中也为此提供了自动化配置。下面通过实例看看如何在Spring Boot中使用JavaMailSender发送邮件。
 * </pre>
 *
 * @author eanson
 * @date 2020/6/14
 */
@Component
public class SendErrorMessageMailHandler {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String mailFrom;

    public void sendMailToAdmin(String subject, String content) {
        sendMail(subject, content, mailFrom);
    }

    public void sendMailToOthers(String subject, String content, String otherMail) {
        sendMail(subject, content, otherMail);
    }

    private void sendMail(String subject, String content, String otherMail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailFrom);
        message.setTo(otherMail);
        message.setSubject(subject);
        message.setText(content);

        mailSender.send(message);
    }

}
