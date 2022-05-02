package com.aws.mail.SampleSESExample.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.CreateTemplateRequest;
import com.amazonaws.services.simpleemail.model.CreateTemplateResult;
import com.amazonaws.services.simpleemail.model.DeleteTemplateRequest;
import com.amazonaws.services.simpleemail.model.DeleteTemplateResult;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.GetTemplateRequest;
import com.amazonaws.services.simpleemail.model.GetTemplateResult;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendEmailResult;
import com.amazonaws.services.simpleemail.model.SendTemplatedEmailRequest;
import com.amazonaws.services.simpleemail.model.SendTemplatedEmailResult;
import com.amazonaws.services.simpleemail.model.Template;
import com.amazonaws.services.simpleemail.model.UpdateTemplateRequest;
import com.amazonaws.services.simpleemail.model.UpdateTemplateResult;
import com.aws.mail.SampleSESExample.model.TransactionalEmailData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MailServiceImpl implements MailService {

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	public AmazonSimpleEmailService amazonSimpleEmailService;

	@Autowired
	RestTemplate restTemplate;
	@Value("${accessKey}")
	private String accessKey;

	@Value("${secretKey}")
	private String secretKey;

	@Value("${region}")
	private String region;

	private String templateName = "MyTemplate";
	private String templateData = "{ \"name\":\"John\", \"favoriteanimal\": \"Tiger\"}";


	public String sendMail(String sendMailFrom, String sendMailTo) {
		AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
		com.amazonaws.services.simpleemail.AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder
				.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(region).build();

		Destination destination = new Destination();
		List<String> toAddresses = new ArrayList<String>();
		toAddresses.add(sendMailTo);
		/*
		 * String[] Emails = to;
		 * 
		 * for (String email : Emails) { toAddresses.add(email); }
		 */

		destination.setToAddresses(toAddresses);
		SendTemplatedEmailRequest templatedEmailRequest = new SendTemplatedEmailRequest();
		templatedEmailRequest.withDestination(destination);
		templatedEmailRequest.withTemplate(templateName);
		templatedEmailRequest.withTemplateData(templateData);
		templatedEmailRequest.withSource(sendMailFrom);
		SendTemplatedEmailResult sendTemplatedEmail = client.sendTemplatedEmail(templatedEmailRequest);
		return sendTemplatedEmail.toString();
	}

	public void sendEmail() {

		String emailContent = "<!DOCTYPE html>\n" + "<html>\n" + "<head>\n" + "    <meta charset=\"utf-8\">\n"
				+ "    <title>Example HTML Email</title>\n" + "</head>\n"
				+ "<body style=\"background: whitesmoke; padding: 30px; height: 100%\">\n"
				+ "<h5 style=\"font-size: 18px; margin-bottom: 6px\">Hello All,</h5>\n"
				+ "<p style=\"font-size: 16px; font-weight: 500\">This is a simple email using AWS SES</p>\n"
				+ "<p><a target=\"_blank\" style=\"background-color: #199319; color: white;padding: 15px 25px; \" href=\"https://www.google.com/\">Google</a></p>"
				+ "</body>\n" + "</html>";

		String senderEmail = "sender@yopmail.com";
		String receiverEmail = "receiver@yopmail.com";
		String emailSubject = "Test Email Subject";

		try {
			SendEmailRequest sendEmailRequest = new SendEmailRequest()
					.withDestination(new Destination().withToAddresses(receiverEmail))
					.withMessage(new Message()
							.withBody(new Body().withHtml(new Content().withCharset("UTF-8").withData(emailContent)))
							.withSubject(new Content().withCharset("UTF-8").withData(emailSubject)))
					.withSource(senderEmail);
			SendEmailResult result = amazonSimpleEmailService.sendEmail(sendEmailRequest);
			System.out.println(result.getMessageId());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String createTemplate() {
		String emailContent = "<!DOCTYPE html>\n" + "<html lang=\"en\">\n" + "<head>\n"
				+ "    <meta charset=\"utf-8\">\n"
				+ "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n"
				+ "    <title>Example HTML Email</title>\n" + "</head>\n"
				+ "<body style=\"background: whitesmoke; padding: 30px; height: 100%\">\n"
				+ "<h5 style=\"font-size: 18px; margin-bottom: 6px\">Hello {{firstName}},</h5>\n"
				+ "<p style=\"font-size: 16px; font-weight: 500\">Your transaction PIN code is : {{pin}}</p>\n"
				+ "</body>\n" + "</html>";

		Template template = new Template();
		template.setTemplateName("TransactionalTemplate");
		template.setSubjectPart("Hello {{firstName}}");
		template.setHtmlPart(emailContent);

		CreateTemplateRequest request = new CreateTemplateRequest();
		request.setTemplate(template);
		CreateTemplateResult createTemplateResult = amazonSimpleEmailService.createTemplate(request);
		if (createTemplateResult.getSdkHttpMetadata().getHttpStatusCode() == 200) {
			return "Template Created successfully";
		} else {
			return "Template not Created successfully";
		}
	}

	public String updateTemplate(String templateName) {
		String emailContent = "<!DOCTYPE html>\n" + "<html lang=\"en\">\n" + "<head>\n"
				+ "    <meta charset=\"utf-8\">\n"
				+ "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n"
				+ "    <title>Example HTML Email</title>\n" + "</head>\n"
				+ "<body style=\"background: whitesmoke; padding: 30px; height: 100%\">\n"
				+ "<h5 style=\"font-size: 18px; margin-bottom: 6px\">Hello {{firstName}},</h5>\n"
				+ "<p style=\"font-size: 16px; font-weight: 500\">Your transaction PIN is : {{pin}}</p>\n" + "</body>\n"
				+ "</html>";

		Template template = new Template();
		template.setTemplateName(templateName);
		template.setSubjectPart("Hello {{firstName}}");
		template.setHtmlPart(emailContent);

		UpdateTemplateRequest request = new UpdateTemplateRequest();
		request.setTemplate(template);
		UpdateTemplateResult updateTemplateResult = amazonSimpleEmailService.updateTemplate(request);
		if (updateTemplateResult.getSdkHttpMetadata().getHttpStatusCode() == 200) {
			return "Template updated successfully";
		} else {
			return "Template not updated successfully";
		}
	}

	public String deleteTemplate(String templateName) {
		DeleteTemplateRequest request = new DeleteTemplateRequest();
		request.setTemplateName(templateName);
		DeleteTemplateResult deleteTemplate = amazonSimpleEmailService.deleteTemplate(request);
		if (deleteTemplate.getSdkHttpMetadata().getHttpStatusCode() == 200) {
			return "Template Delete successfully";
		} else {
			return "Template not Delete successfully";
		}
	}

	public String sendTemplateEmail(TransactionalEmailData transactionalEmailData, String senderEmail,String receiverEmail) {
		String uuid = UUID.randomUUID().toString();
		uuid = uuid.substring(uuid.length() - 6);
		String templateData = null;
		try {
			templateData = this.objectMapper.writeValueAsString(transactionalEmailData);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	
		SendTemplatedEmailRequest sendTemplatedEmailRequest = new SendTemplatedEmailRequest();
		sendTemplatedEmailRequest.setTemplate("TransactionalTemplate");
		sendTemplatedEmailRequest.setSource(senderEmail);
		sendTemplatedEmailRequest.setDestination(new Destination(Arrays.asList(receiverEmail)));
		sendTemplatedEmailRequest.setTemplateData(templateData);
		SendTemplatedEmailResult sendTemplatedEmail = amazonSimpleEmailService.sendTemplatedEmail(sendTemplatedEmailRequest);
		if (sendTemplatedEmail.getSdkHttpMetadata().getHttpStatusCode() == 200) {
			return "Email Sent Successfully";
		} else {
			return "Email not Send Successfully";
		}
	}

	@Override
	public ResponseEntity<Template> retrieveEmailTemplate(String templateName) {
		GetTemplateRequest emailTemplate = new GetTemplateRequest();
		emailTemplate.setTemplateName(templateName);
		GetTemplateResult getTemplateResult = amazonSimpleEmailService.getTemplate(emailTemplate);
		Template template = getTemplateResult.getTemplate();
		return new ResponseEntity<Template>(template, HttpStatus.OK);
	}

}
