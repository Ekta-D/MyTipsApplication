package com.mytips;

import android.os.Environment;
import android.util.Log;

import com.mytips.Utils.Constants;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

/**
 * Created by Beesolver on 01-11-2017.
 */

public class GMail {

    final String emailPort = "587";// gmail's smtp port
    final String smtpAuth = "true";
    final String starttls = "true";
    final String emailHost = "smtp.gmail.com";

    String fromEmail;
    String fromPassword;
    List<String> toEmailList = new ArrayList<>();
    String emailSubject;
    String emailBody;

    Properties emailProperties;
    Session mailSession;
    MimeMessage emailMessage;
    Multipart multipart;
    BodyPart bodyPart, fileBodyPart;
    DataSource source;
    String email_file_name;

    public GMail() {

    }

    public GMail(String fromEmail, String fromPassword,
                 List toEmailList, String emailSubject, String emailBody, String email_file_name) {
        this.fromEmail = fromEmail;
        this.fromPassword = fromPassword;
        this.toEmailList = toEmailList;
        this.emailSubject = emailSubject;
        this.emailBody = emailBody;
        this.email_file_name = email_file_name;

        emailProperties = System.getProperties();
        emailProperties.put("mail.smtp.port", emailPort);
        emailProperties.put("mail.smtp.auth", smtpAuth);
        emailProperties.put("mail.smtp.starttls.enable", starttls);
        Log.i("GMail", "Mail server properties set.");
    }

    public MimeMessage createEmailMessage() throws AddressException,
            MessagingException, UnsupportedEncodingException {

        mailSession = Session.getDefaultInstance(emailProperties, null);
        emailMessage = new MimeMessage(mailSession);
        multipart = new MimeMultipart();
        bodyPart = new MimeBodyPart();
        bodyPart.setText(emailBody);
        String file_name = "Payroll.pdf";

        source = new FileDataSource(email_file_name);

        fileBodyPart = new MimeBodyPart();
        fileBodyPart.setDataHandler(new DataHandler(source));
        fileBodyPart.setFileName(file_name);

        multipart.addBodyPart(bodyPart);
        multipart.addBodyPart(fileBodyPart);


        emailMessage.setFrom(new InternetAddress(fromEmail, fromEmail));
        for (String toEmail : toEmailList) {
            Log.i("GMail", "toEmail: " + toEmail);
            emailMessage.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(toEmail));
        }

        emailMessage.setSubject(emailSubject);
        emailMessage.setContent(multipart);// for a html email

        Log.i("GMail", emailMessage.getContentType() + emailMessage.getFileName());
        return emailMessage;
    }

    public void sendEmail() throws AddressException, MessagingException {

        Transport transport = mailSession.getTransport("smtp");
        transport.connect(emailHost, fromEmail, fromPassword);
        Log.i("GMail", "allrecipients: " + emailMessage.getAllRecipients());
        //  Transport.send(emailMessage);
        //Transport.send(emailMessage, "beesolver.ekta@gmail.com", Constants.fromPassword);
        transport.sendMessage(emailMessage, emailMessage.getAllRecipients());
        transport.close();
        Log.i("GMail", "Email sent successfully.");
    }

}

