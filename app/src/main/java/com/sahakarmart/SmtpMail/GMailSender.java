package com.sahakarmart.SmtpMail;



import java.security.Security;
import java.util.ArrayList;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Created by ubuntu on 5/7/17.
 */

public class GMailSender extends javax.mail.Authenticator {
    private String mailhost = "smtp.gmail.com";
    private String user;
    private String password;
    private Session session;

    static {
        Security.addProvider(new JSSEProvider());
    }

    public GMailSender(String user, String password) {
        this.user = user;
        this.password = password;

        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", mailhost);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");

        session = Session.getDefaultInstance(props, this);
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password);
    }



    // for Send Mail With Multiple Attachment
    public synchronized void sendMailWithAttach(String subject, String body,
                                      String sender, String recipients,ArrayList<String> filename) throws Exception {
        MimeMessage message = new MimeMessage(session);
        // Create the message part
        BodyPart messageBodyPart = new MimeBodyPart();
        DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/plain"));
        message.setSender(new InternetAddress(sender));
        message.setSubject(subject);
        message.setDataHandler(handler);

        if (recipients.indexOf(',') > 0)
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
        else
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));




        // Create a multipar message
        Multipart multipart = new MimeMultipart();


        messageBodyPart.setText(body);

        // Set text message part
        multipart.addBodyPart(messageBodyPart);

        // for multiple attachments

        for (int i = 0 ;i<filename.size() ;i++)
        {

            // Part two is attachment
            messageBodyPart = new MimeBodyPart();

            if(filename.get(i) != null)
            {
                DataSource source = new FileDataSource(filename.get(i));
                messageBodyPart.setDataHandler(new DataHandler(source));

                messageBodyPart.setFileName(filename.get(i));
                multipart.addBodyPart(messageBodyPart);
            }




        }


        // Send the complete message parts
        message.setContent(multipart);
        Transport.send(message);
    }


    // for Only Text (subject & body )
    public synchronized void sendMail(String subject, String body,
                                      String sender, String recipients) throws Exception {
        MimeMessage message = new MimeMessage(session);
        // Create the message part
        BodyPart messageBodyPart = new MimeBodyPart();
        DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/plain"));
        message.setSender(new InternetAddress(sender));
        message.setSubject(subject);
        message.setDataHandler(handler);

        if (recipients.indexOf(',') > 0)
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
        else
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));




        // Create a multipar message
        Multipart multipart = new MimeMultipart();


        messageBodyPart.setText(body);

        // Set text message part
        multipart.addBodyPart(messageBodyPart);

        // Send the complete message parts
        message.setContent(multipart);
        Transport.send(message);
    }


    public static void addAttachment(Multipart multipart, String filename) throws MessagingException {
        DataSource source = new FileDataSource(filename);
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(filename);
        multipart.addBodyPart(messageBodyPart);
    }
}