package com.aws.mail.SampleSESExample.model;

public class EmailTemplate {
	private TemplateContent templateContent;
	private String templateName;

	public TemplateContent getTemplateContent() {
		return templateContent;
	}

	public void setTemplateContent(TemplateContent templateContent) {
		this.templateContent = templateContent;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

}
