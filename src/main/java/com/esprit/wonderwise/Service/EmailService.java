package com.esprit.wonderwise.Service;

import com.esprit.wonderwise.Model.offre;
import com.esprit.wonderwise.Model.reservation;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class EmailService {

    private static final String USERNAME = "farahsoltani235@gmail.com"; // badlou bl email mtaaaak
    private static final String PASSWORD = "vlmicbpjjbgbconz"; // badel el  password
    private static final String SENDER_NAME = "WonderWise Support";

    private static void sendEmail(String recipient, String subject, String content) {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USERNAME, SENDER_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject(subject);
            message.setContent(content, "text/html");

            Transport.send(message);
            System.out.println("✅ Email sent to " + recipient);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Failed to send email: " + e.getMessage());
        }
    }

    public static void sendReservationConfirmation(String recipient, String userName, reservation res, offre offer) {
        String subject = "✨ WonderWise Confirmation de Réservation #" + res.getId();
        StringBuilder emailContent = new StringBuilder();

        // Email HTML structure with inline CSS
        emailContent.append("<!DOCTYPE html>")
                .append("<html lang='en'>")
                .append("<head>")
                .append("<meta charset='UTF-8'>")
                .append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>")
                .append("<title>Confirmation de Réservation</title>")
                .append("</head>")
                .append("<body style='margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;'>")
                .append("<table role='presentation' width='100%' cellspacing='0' cellpadding='0' style='background-color: #f4f4f4; padding: 20px;'>")
                .append("<tr>")
                .append("<td align='center'>")
                .append("<table role='presentation' width='600' cellspacing='0' cellpadding='0' style='background-color: #ffffff; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);'>")
                .append("<tr>")
                .append("<td style='background-color: #1a73e8; padding: 20px; text-align: center; border-top-left-radius: 8px; border-top-right-radius: 8px;'>")
                .append("<h1 style='color: #ffffff; margin: 0; font-size: 24px;'>WonderWise</h1>")
                .append("<p style='color: #e8f0fe; margin: 5px 0 0; font-size: 14px;'>Votre Voyage Commence Ici</p>")
                .append("</td>")
                .append("</tr>")
                .append("<tr>")
                .append("<td style='padding: 30px; text-align: center;'>")
                .append("<h2 style='color: #333333; font-size: 22px; margin: 0 0 20px;'>Bonjour ").append(userName).append(",</h2>")
                .append("<p style='color: #555555; font-size: 16px; line-height: 1.5; margin: 0 0 20px;'>Merci d'avoir réservé avec <strong>WonderWise</strong> ! Votre réservation a été reçue avec succès.</p>")
                .append("<table role='presentation' width='100%' cellspacing='0' cellpadding='10' style='background-color: #f9f9f9; border-radius: 6px; margin: 20px 0;'>")
                .append("<tr>")
                .append("<td style='text-align: left; border-bottom: 1px solid #e0e0e0;'>")
                .append("<strong style='color: #1a73e8;'>Réservation #").append(res.getId()).append("</strong>")
                .append("</td>")
                .append("</tr>")
                .append("<tr>")
                .append("<td style='text-align: left; color: #555555; font-size: 14px;'>")
                .append("<strong>Destination:</strong> ").append(offer.getTitre()).append(" - ").append(offer.getPays())
                .append("</td>")
                .append("</tr>")
                .append("<tr>")
                .append("<td style='text-align: left; color: #555555; font-size: 14px;'>")
                .append("<strong>Nombre de Personnes:</strong> ").append(res.getNombrePersonne())
                .append("</td>")
                .append("</tr>")
                .append("<tr>")
                .append("<td style='text-align: left; color: #555555; font-size: 14px;'>")
                .append("<strong>Montant Total:</strong> ").append(String.format("%.2f TND", offer.getPrix() * res.getNombrePersonne()))
                .append("</td>")
                .append("</tr>")
                .append("<tr>")
                .append("<td style='text-align: left; color: #555555; font-size: 14px;'>")
                .append("<strong>Date de Réservation:</strong> ").append(res.getDateReservation().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                .append("</td>")
                .append("</tr>")
                .append("<tr>")
                .append("<td style='text-align: left; color: #555555; font-size: 14px;'>")
                .append("<strong>Mode de Paiement:</strong> ").append(res.getModePaiement())
                .append("</td>")
                .append("</tr>")
                .append("</table>");

        if ("Carte bancaire".equals(res.getModePaiement())) {
            emailContent.append("<p style='color: #555555; font-size: 16px; line-height: 1.5; margin: 0 0 20px;'>Vous avez été redirigé vers la page de paiement Stripe pour finaliser votre paiement. Veuillez vous assurer que le paiement est effectué pour confirmer votre réservation.</p>");
        } else {
            emailContent.append("<p style='color: #555555; font-size: 16px; line-height: 1.5; margin: 0 0 20px;'>Votre réservation est en attente de confirmation. Nous vous contacterons bientôt avec plus de détails.</p>");
        }

        emailContent.append("<p style='color: #555555; font-size: 16px; line-height: 1.5; margin: 0 0 20px;'>Nous sommes ravis de vous aider à planifier votre voyage ! Pour toute question, n'hésitez pas à nous contacter.</p>")
                .append("<a href='mailto:support@wonderwise.com' style='display: inline-block; padding: 10px 20px; background-color: #34c759; color: #ffffff; text-decoration: none; border-radius: 5px; font-size: 16px;'>Contacter le Support</a>")
                .append("</td>")
                .append("</tr>")
                .append("<tr>")
                .append("<td style='background-color: #1a73e8; padding: 20px; text-align: center; border-bottom-left-radius: 8px; border-bottom-right-radius: 8px;'>")
                .append("<p style='color: #e8f0fe; font-size: 14px; margin: 0;'>Équipe Support WonderWise</p>")
                .append("<p style='color: #e8f0fe; font-size: 12px; margin: 5px 0 0;'>Email: <a href='mailto:support@wonderwise.com' style='color: #e8f0fe; text-decoration: underline;'>support@wonderwise.com</a> | Téléphone: +216 123 456 78</p>")
                .append("<p style='color: #e8f0fe; font-size: 12px; margin: 5px 0 0;'>Ceci est un email automatique, merci de ne pas répondre directement.</p>")
                .append("</td>")
                .append("</tr>")
                .append("</table>")
                .append("</td>")
                .append("</tr>")
                .append("</table>")
                .append("</body>")
                .append("</html>");

        sendEmail(recipient, subject, emailContent.toString());
    }
}