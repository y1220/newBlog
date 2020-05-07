package it.course.myblog.service;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.Context;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MailService {
	
	
	@Value("${smtp.host.name}")
	private String SMTP_HOST_NAME;
	
	@Value("${smtp.host.port}")
	private String SMTP_HOST_PORT;
	
	@Value("${smtp.mail.sender}")
	private String SMTP_AUTH_USER;
	
	@Value("${smtp.password.sender}")
	private String SMTP_AUTH_PWD;
    
    private final String[] CC_ADDRESS = null;

    
    public void send(String[] TO_ADDRESS, String reason, String identifier) throws AddressException, MessagingException  { 	
    	
        Properties props = System.getProperties();
        props.put("mail.smtp.socketFactory.port", SMTP_HOST_PORT);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.host", SMTP_HOST_NAME);
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.ssl.enable", "true");
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(SMTP_AUTH_USER));
        InternetAddress[] addTo = new InternetAddress[TO_ADDRESS.length];
        for (int i = 0; i < addTo.length; i++) {
            addTo[i] = new InternetAddress(TO_ADDRESS[i]);
        }
        message.setRecipients(Message.RecipientType.TO, addTo);
        if (CC_ADDRESS != null) {
            InternetAddress[] addCc = new InternetAddress[CC_ADDRESS.length];
            for (int i = 0; i < addCc.length; i++) {
                addCc[i] = new InternetAddress(CC_ADDRESS[i]);
            }
            message.setRecipients(Message.RecipientType.CC, addCc);
        }
        
        
        if(reason.equals("forgot")) {
        	message.setSubject("MyBlog: forgot password");
            message.setText("In order to change your password click on the link below:\n "
            		+ "http://localhost:8081/api/auth/change-password/"+identifier);
        	
        }
        //message.setSubject("MyBlog: confirm your email");
        //message.setText("In order to confirm your account clink on the link below:\n ");
        
        //message.setSubject("MyBlog: Your account is now active");
        //message.setText("Thank for your registration");

        Transport transport = session.getTransport("smtp");
        transport.connect(SMTP_HOST_NAME, SMTP_AUTH_USER, SMTP_AUTH_PWD);
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    }

}
