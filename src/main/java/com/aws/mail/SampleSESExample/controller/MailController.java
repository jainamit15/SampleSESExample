package com.aws.mail.SampleSESExample.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.simpleemail.model.Template;
import com.aws.mail.SampleSESExample.model.EmailTemplate;
import com.aws.mail.SampleSESExample.model.TransactionalEmailData;
import com.aws.mail.SampleSESExample.service.MailService;

@RestController
@RequestMapping("/mail")
public class MailController {

	@Autowired
	public MailService mailService;

	@GetMapping("/sendMail")
	public String sendMail(@RequestParam String sendMailFrom, @RequestParam String sendMailTo) {
		return mailService.sendMail(sendMailFrom, sendMailTo);
	}

	@GetMapping("/createTemplate")
	public String createTemplate() {
		return mailService.createTemplate();
	}
	
	@GetMapping("/deleteTemplate/{templateName}")
	public String deleteTemplate(@PathVariable String templateName) {
		return mailService.deleteTemplate(templateName);
	}

	@GetMapping("/retrieveTemplate/{templateName}")
	public ResponseEntity<Template> retrieveTemplate(@PathVariable String templateName) {
		return mailService.retrieveEmailTemplate(templateName);
	}
	
	@GetMapping("/updateTemplate/{templateName}")
	public String updateTemplate(@PathVariable String templateName) {
		return mailService.updateTemplate(templateName);
	}
	
	@PostMapping("/sendTemplateMail")
	public String sendTemplateEmail(@RequestBody TransactionalEmailData transactionalEmailData,@RequestParam String senderEmail,@RequestParam String receiverEmail) {
		return mailService.sendTemplateEmail(transactionalEmailData, senderEmail, receiverEmail);
	}
}
