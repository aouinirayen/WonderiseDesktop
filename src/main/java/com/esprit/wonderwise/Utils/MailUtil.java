package com.esprit.wonderwise.Utils;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class MailUtil {
    public static void sendEmail(String to, String subject, String userName) {
        final String from = "charfifatmaezzahra@gmail.com";
        final String password = "cwchcilatcgydobw"; // À sécuriser via variables d'environnement

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);

            String content =
                    "<!DOCTYPE html>" +
                            "<html lang='fr'>" +
                            "<head>" +
                            "<meta charset='UTF-8'>" +
                            "<title>Email WonderWise</title>" +
                            "<style>" +
                            "  .btn { background-color: #007BFF; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px; font-size: 16px; display: inline-block; }" +
                            "  .bold-blue { font-weight: bold; color: #007BFF; }" +
                            "</style>" +
                            "</head>" +
                            "<body style='font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f0f2f5;'>" +
                            "<table width='100%' style='padding: 30px 0;'>" +
                            "<tr><td align='center'>" +
                            "<table width='600' style='background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.1);'>" + // Maintained original width

                            // En-tête bleu, texte plus grand et en gras
                            "<tr style='background-color: #007BFF;'>" +
                            "<td style='padding: 20px; text-align: center; color: white; font-size: 28px; font-weight: bold;'>Équipe WonderWise</td>" +
                            "</tr>" +

                            // Contenu principal
                            "<tr><td style='padding: 30px; font-size: 16px; color: #333333;'>" +
                            "<p style='font-size: 18px;'>Bonjour " + userName + ",</p>" +
                            "<p>Nous tenons à vous informer que nous prenons en considération votre réclamation.</p>" +
                            "<p>Nous vous invitons à visiter notre plateforme pour découvrir nos nouvelles fonctionnalités.</p>" +
                            "<p style='text-align: center;'><a href='https://wonderwise.com' class='btn'>Visiter WonderWise</a></p>" +
                            "<p class='bold-blue'>Bien cordialement,<br>L'équipe WonderWise</p>" +
                            "</td></tr>" +

                            // Pied de page
                            "<tr style='background-color: #f7f7f7;'>" +
                            "<td style='text-align: center; padding: 20px; font-size: 12px; color: #888888;'>" +
                            "<p>&copy; 2025 WonderWise. Tous droits réservés.</p>" +
                            "<p><a href='mailto:support@wonderwise.com' style='color: #888;'>support@wonderwise.com</a></p>" +
                            "</td></tr>" +

                            "</table>" +
                            "</td></tr>" +
                            "</table>" +
                            "</body></html>";

            message.setContent(content, "text/html; charset=utf-8");

            Transport.send(message);
            System.out.println("✅ Email envoyé à " + to);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
