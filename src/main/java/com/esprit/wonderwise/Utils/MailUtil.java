package com.esprit.wonderwise.Utils;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class MailUtil {
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String USERNAME = "aouinirayen79@gmail.com";
    private static final String PASSWORD = "lrsp prsu atir jdbh";

    public static void sendHtmlMail(String to, String subject, String htmlContent) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(USERNAME));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setContent(htmlContent, "text/html; charset=utf-8");

        Transport.send(message);
    }

    public static String blockUserTemplate(String username) {
        return "<div style='background:#f6f8fb;padding:32px 0;'>"
            + "<div style='max-width:520px;margin:0 auto;background:#fff;border-radius:18px;box-shadow:0 6px 32px rgba(44,62,80,0.09);overflow:hidden;border:1px solid #e3e7ee;'>"
            + "<div style='background:#e74c3c;padding:24px 0 16px 0;text-align:center;'>"
            + "<div style='font-size:26px;font-weight:700;color:#fff;letter-spacing:1px;'>Account Blocked</div>"
            + "</div>"
            + "<div style='padding:30px 32px 18px 32px;'>"
            + "<div style='font-size:17px;color:#222;font-weight:600;margin-bottom:8px;'>Hello <b>" + escapeHtml(username) + "</b>,</div>"
            + "<div style='font-size:15px;color:#333;margin-bottom:18px;'>We regret to inform you that your account has been <b>blocked</b> due to suspicious or inappropriate activity. Please contact support if you believe this is a mistake.</div>"
            + "</div>"
            + "<div style='background:#f6f8fb;text-align:center;padding:14px 0 9px 0;color:#888;font-size:13px;border-top:1px solid #e3e7ee;'>"
            + "Wonderwise &copy; 2025"
            + "</div>"
            + "</div>"
            + "</div>";
    }

    public static String birthdayTemplate(String username) {
        return "<div style='background: linear-gradient(135deg, #f5f7fa 0%, #e4e9f1 100%); padding: 40px 0; font-family: \"Segoe UI\", Arial, sans-serif;'>"
                + "<div style='max-width: 560px; margin: 0 auto; background: #ffffff; border-radius: 16px; box-shadow: 0 4px 20px rgba(0,0,0,0.08); overflow: hidden; border: 1px solid #d9e2ec;'>"
                + "<div style='background: linear-gradient(90deg, #6b7280 0%, #4b5563 100%); padding: 20px 0; text-align: center;'>"
                + "<div style='font-size: 28px; font-weight: 700; color: #ffffff; letter-spacing: 1.5px; text-transform: uppercase;'>Happy Birthday! 🎉</div>"
                + "</div>"
                + "<div style='padding: 24px 28px; text-align: center;'>"
                + "<div style='font-size: 18px; color: #1f2937; font-weight: 600; margin-bottom: 10px;'>Dear <b>" + escapeHtml(username) + "</b>,</div>"
                + "<div style='font-size: 15px; color: #374151; line-height: 1.5; margin-bottom: 20px;'>"
                + "Wishing you a day filled with joy, love, and cherished moments. <br>"
                + "May this year bring you closer to your dreams. <br>"
                + "From Wonderwise, have an amazing birthday!"
                + "</div>"
                + "<div style='margin-top: 20px;'>"
                + "<a href='https://i.giphy.com/LzwcNOrbA3aYvXK6r7.webp' style='display: inline-block; background: #6b7280; color: #fff; padding: 10px 24px; border-radius: 30px; text-decoration: none; font-size: 14px; font-weight: 600; box-shadow: 0 2px 8px rgba(0,0,0,0.15);'>Celebrate Now!</a>"
                + "</div>"
                + "</div>"
                + "<div style='background: #f5f7fa; text-align: center; padding: 12px 0; color: #6b7280; font-size: 13px; border-top: 1px solid #d9e2ec;'>"
                + "Wonderwise © 2025 | Crafted with Care"
                + "</div>"
                + "</div>"
                + "</div>";
    }

    public static String unblockUserTemplate(String username) {
        return "<div style='background:#f6f8fb;padding:32px 0;'>"
            + "<div style='max-width:520px;margin:0 auto;background:#fff;border-radius:18px;box-shadow:0 6px 32px rgba(44,62,80,0.09);overflow:hidden;border:1px solid #e3e7ee;'>"
            + "<div style='background:#27ae60;padding:24px 0 16px 0;text-align:center;'>"
            + "<div style='font-size:26px;font-weight:700;color:#fff;letter-spacing:1px;'>Account Unblocked</div>"
            + "</div>"
            + "<div style='padding:30px 32px 18px 32px;'>"
            + "<div style='font-size:17px;color:#222;font-weight:600;margin-bottom:8px;'>Hello <b>" + escapeHtml(username) + "</b>,</div>"
            + "<div style='font-size:15px;color:#333;margin-bottom:18px;'>We are pleased to inform you that your account has been <b>unblocked</b>. You can now access all Wonderwise services again.</div>"
            + "</div>"
            + "<div style='background:#f6f8fb;text-align:center;padding:14px 0 9px 0;color:#888;font-size:13px;border-top:1px solid #e3e7ee;'>"
            + "Wonderwise &copy; 2025"
            + "</div>"
            + "</div>"
            + "</div>";
    }

    // HTML template for ForgotPassword
    public static String templateForgotPassword(String username, String useremail, String token) {
        return "<div style='background:#f6f8fb;padding:32px 0;'>"
            + "<div style='max-width:520px;margin:0 auto;background:#fff;border-radius:18px;box-shadow:0 6px 32px rgba(44,62,80,0.09);overflow:hidden;border:1px solid #e3e7ee;'>"
            + "<div style='background:#4a90e2;padding:24px 0 16px 0;text-align:center;'>"
            + "<div style='font-size:26px;font-weight:700;color:#fff;letter-spacing:1px;'>Réinitialisation du mot de passe</div>"
            + "</div>"
            + "<div style='padding:30px 32px 18px 32px;'>"
            + "<div style='font-size:17px;color:#222;font-weight:600;margin-bottom:8px;'>Bonjour <b>" + escapeHtml(username) + "</b>,</div>"
            + "<div style='font-size:15px;color:#333;margin-bottom:18px;'>Nous avons reçu une demande de réinitialisation du mot de passe pour le compte associé à <b>" + escapeHtml(useremail) + "</b>.<br>Veuillez utiliser le code ci-dessous pour réinitialiser votre mot de passe :</div>"
            + "<div style='background:#f0f7ff;border:1px solid #4a90e2;color:#4a90e2;font-size:22px;font-weight:bold;border-radius:6px;padding:16px;text-align:center;margin:24px 0;letter-spacing:2px;'>"
            + escapeHtml(token)
            + "</div>"
            + "<div style='color:#444;font-size:14px;margin-bottom:12px;'>Si vous n'êtes pas à l'origine de cette demande, vous pouvez ignorer cet email.</div>"
            + "</div>"
            + "<div style='background:#f6f8fb;text-align:center;padding:14px 0 9px 0;color:#888;font-size:13px;border-top:1px solid #e3e7ee;'>"
            + "Wonderwise &copy; 2025"
            + "</div>"
            + "</div>"
            + "</div>";
    }


    public static String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

}
