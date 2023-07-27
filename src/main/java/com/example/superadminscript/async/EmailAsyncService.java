package com.example.superadminscript.async;

import com.example.superadminscript.email.EmailArgs;
import com.example.superadminscript.email.EmailService;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class EmailAsyncService {

    private EmailService emailService;

    private Environment environment;

    public EmailAsyncService(EmailService emailService,Environment environment) {
        this.emailService = emailService;
        this.environment = environment;
    }

    @Async
    public void sendSuperAdminCredentials(List<Object> arguments){
        if(!arguments.isEmpty()) {
            Map<String,String> map = (Map<String, String>) arguments.get(0);
            EmailArgs emailArgs = EmailArgs.builder()
                    .subject("Login Credentials")
                    .toRecipients(Collections.singletonList(map.get("email")))
                    .content("<html>\n" +
                            "  <body>\n" +
                            "    <div>\n" +
                            "      <p>Hello "+ map.get("firstName") +",\n" +
                            "            <br>This is your credentials:\n" +
                            "                <br>Username : "+ map.get("email") +"\n" +
                            "                <br>Password : "+ map.get("password") +"\n" +
                            "      </p>\n" +
                            "    </div>\n" +
                            "  </body>\n" +
                            "</html>\n")
                    .build();
            emailService.sendEmail(emailArgs);
        }
    }
}
