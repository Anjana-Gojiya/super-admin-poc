package com.example.superadminscript.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class EmailService {

    private JavaMailSender javaMailSender;
    private Environment environment;

    public EmailService(JavaMailSender javaMailSender, Environment environment) {
        this.javaMailSender = javaMailSender;
        this.environment = environment;
    }

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    public void sendEmail(EmailArgs emailArgs) {
        if(emailArgs.getToRecipients() != null && !emailArgs.getToRecipients().isEmpty()) {
            try {

                //todo : replace content
                String htmlContent = emailArgs.getContent();

                MimeMessage message = javaMailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message,true);

                helper.setFrom(new InternetAddress(environment.getProperty("spring.from.mail")));
                helper.setSubject(emailArgs.getSubject());
                helper.setText(htmlContent,true);

                //set to recipients
                InternetAddress[] addresses = new InternetAddress[emailArgs.getToRecipients().size()];
                for (int i = 0; i < emailArgs.getToRecipients().size(); i++) {
                    addresses[i] = new InternetAddress(emailArgs.getToRecipients().get(i));
                }
                helper.setTo(addresses);

                //set cc recipients
                if(emailArgs.getCcRecipients() != null && !emailArgs.getCcRecipients().isEmpty()) {
                    addresses = new InternetAddress[emailArgs.getCcRecipients().size()];
                    for (int i = 0; i < emailArgs.getCcRecipients().size(); i++) {
                        addresses[i] = new InternetAddress(emailArgs.getCcRecipients().get(i));
                    }
                    helper.setCc(addresses);
                }

                //set bcc recipients
                if(emailArgs.getBccRecipients() != null && !emailArgs.getBccRecipients().isEmpty()) {
                    addresses = new InternetAddress[emailArgs.getBccRecipients().size()];
                    for (int i = 0; i < emailArgs.getCcRecipients().size(); i++) {
                        addresses[i] = new InternetAddress(emailArgs.getBccRecipients().get(i));
                    }
                    helper.setBcc(addresses);
                }

                //add attachment
                if(emailArgs.getAttachments() != null && !emailArgs.getAttachments().isEmpty()) {
                    for (String attachment : emailArgs.getAttachments()) {
                        FileSystemResource file = new FileSystemResource(new File(attachment));
                        helper.addAttachment(file.getFilename(),file);
                    }
                }

                javaMailSender.send(message);

                logger.info("Email Sent Successfully");

            } catch (MessagingException e) {
                logger.info(e.getMessage());
            } catch (Exception e) {
                logger.info(e.getMessage());
            }
        }
    }
}

