package org.sunshine.core.sms;

import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.sunshine.core.sms.model.FileMailMessage;
import org.sunshine.core.tool.support.Try;

import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author Teamo
 * @since 2023/5/5
 */
public class MailTemplateImpl implements MailTemplate {

    private final JavaMailSender javaMailSender;

    static {
        System.setProperty("mail.mime.splitlongparameters", "false");
        System.setProperty("mail.mime.charset", "UTF-8");
    }

    public MailTemplateImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void send(SimpleMailMessage... simpleMailMessage) {
        javaMailSender.send(simpleMailMessage);
    }

    @Override
    public void sendWithFile(FileMailMessage... fileMailMessage) {
        PropertyMapper mapper = PropertyMapper.get();
        MimeMessage[] mimeMessages = Arrays.stream(fileMailMessage).map(Try.apply(mailMessage -> {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            mapper.from(mailMessage::getSender).to(Try.accept(helper::setFrom));
            mapper.from(mailMessage::getCc).to(Try.accept(helper::setCc));
            mapper.from(mailMessage::getTo).to(Try.accept(helper::setTo));
            mapper.from(mailMessage::getText).to(Try.accept(helper::setText));
            mapper.from(mailMessage::getTitle).to(Try.accept(helper::setSubject));
            mapper.from(mailMessage::getFile).to(Try.accept(file -> helper.addAttachment(MimeUtility.encodeWord(mailMessage.getAttachmentName()), file)));
            return message;
        })).toArray(MimeMessage[]::new);
        javaMailSender.send(mimeMessages);
    }
}
