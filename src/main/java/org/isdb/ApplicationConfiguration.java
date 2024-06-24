package org.isdb;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Properties;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

@Configuration
@EnableScheduling
public class ApplicationConfiguration {

	@Value("${pimonitoring.piUserName}")
	private String piUserName;

	@Value("${pimonitoring.piPass}")
	private String piPass;

	@Value("${pimonitoring.ceriticate}")
	private Resource certPath;

	@Value("${spring.mail.host}")
	private String mailHost;
	
	@Value("${spring.mail.port}")
	private int mailPort;
	
	@Value("${spring.mail.username}")
	private String mailUsername;
	
	@Value("${spring.mail.password}")
	private String mailPassword;
	
	@Value("${spring.mail.properties.mail.smtp.auth}")
	private boolean auth;
	
	@Value("${spring.mail.properties.mail.smtp.starttls.enable}")
	private boolean tlsEnable;

	@Bean
	@Profile("tomcat")
	public JavaMailSender getJavaMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(mailHost);
		mailSender.setPort(mailPort);

		mailSender.setUsername(mailUsername);
		mailSender.setPassword(mailPassword);

		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", auth);
		props.put("mail.smtp.starttls.enable", "true");

		return mailSender;
	}

	@Bean
	@Primary
	ITemplateResolver thymeleafTemplateResolver() {
		ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
		templateResolver.setPrefix("mail-templates/");
		templateResolver.setSuffix(".html");
		templateResolver.setTemplateMode("HTML");
		templateResolver.setCharacterEncoding("UTF-8");
		return templateResolver;
	}

	@Bean
	SpringTemplateEngine thymeleafTemplateEngine(ITemplateResolver templateResolver) {
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);
		templateEngine.setTemplateEngineMessageSource(emailMessageSource());
		return templateEngine;
	}

	@Bean
	ResourceBundleMessageSource emailMessageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename("mailMessages");
		return messageSource;
	}

	@Bean
	Client jerseyClient() throws Exception {
		SSLContext sslContext = sslContext();
		ClientConfig clientConfig = new ClientConfig();
		clientConfig.property(ClientProperties.CONNECT_TIMEOUT, 60 * 1000);
		clientConfig.property(ClientProperties.READ_TIMEOUT, 60 * 1000);
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(piUserName, piPass);
		clientConfig.register(feature);

		return JerseyClientBuilder.newBuilder().sslContext(sslContext).withConfig(clientConfig).build();
	}

	SSLContext sslContext() throws Exception {
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		X509Certificate caCert;
		try (InputStream inputStream = certPath.getInputStream()) {
			caCert = (X509Certificate) cf.generateCertificate(inputStream);
		}

		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		ks.load(null, null);
		ks.setCertificateEntry("caCert", caCert);

		javax.net.ssl.TrustManagerFactory tmf = javax.net.ssl.TrustManagerFactory
				.getInstance(javax.net.ssl.TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(ks);

		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(null, tmf.getTrustManagers(), new java.security.SecureRandom());
		return sslContext;
	}
}
