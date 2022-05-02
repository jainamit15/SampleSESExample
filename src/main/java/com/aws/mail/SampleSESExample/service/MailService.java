package com.aws.mail.SampleSESExample.service;

import org.springframework.http.ResponseEntity;

import com.amazonaws.services.simpleemail.model.Template;
import com.aws.mail.SampleSESExample.model.TransactionalEmailData;

public interface MailService {

	String sendMail(String sendMailFrom, String sendMailTo);

	ResponseEntity<Template> retrieveEmailTemplate(String templateName);

	String createTemplate();

	String deleteTemplate(String templateName);

	String updateTemplate(String templateName);

	String sendTemplateEmail(TransactionalEmailData transactionalEmailData, String senderEmail, String receiverEmail);
}
