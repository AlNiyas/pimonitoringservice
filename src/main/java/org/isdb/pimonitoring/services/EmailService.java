package org.isdb.pimonitoring.services;

import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

@Component
public class EmailService {

	private static Logger LOG = LoggerFactory.getLogger(EmailService.class);

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private SpringTemplateEngine thymeleafTemplateEngine;

	@Value("${pimonitoring.fromMail}")
	private String fromMal;

	@Value("${pimonitoring.toMail}")
	private String[] toMail;

	public void sendHtmlMessage(String subject, Map<String, Object> templateModel) throws Exception {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
		Context thymeleafContext = new Context();
		thymeleafContext.setVariables(templateModel);
		String htmlBody = thymeleafTemplateEngine.process("template-email.html", thymeleafContext);
		helper.setFrom(fromMal);
		helper.setTo(toMail);
		helper.setSubject(subject);
		helper.setText(htmlBody, true);
		mailSender.send(message);
		LOG.info("Mail send successfully");

	}

}