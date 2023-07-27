package com.example.superadminscript.email;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class EmailArgs {

    private List<String> toRecipients;
    private List<String> ccRecipients;
    private List<String> bccRecipients;
    private String subject;
    private List<String> attachments;
    private List<Object> arguments;
//    private EmailTemplateType emailTemplateType;
    private String content;

}
