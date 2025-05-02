package com.esprit.wonderwise.Utils;

import com.esprit.wonderwise.Model.User;
import com.esprit.wonderwise.Service.UserService;
import java.time.LocalDate;
import java.util.List;

public class BirthdayUtils {
    // Check all users, and print/send a birthday email if today is their birthday
    public static void checkAndSendBirthdayEmails() {
        UserService userService = new UserService();
        List<User> users = userService.getAllUsers();
        LocalDate today = LocalDate.now();
        for (User user : users) {
            LocalDate dob = user.getDateOfBirth();
            if (dob != null && dob.getMonthValue() == today.getMonthValue() && dob.getDayOfMonth() == today.getDayOfMonth()) {
                String html = MailUtil.birthdayTemplate(user.getUsername());
                try {
                    MailUtil.sendHtmlMail(
                        user.getEmail(),
                        "Happy Birthday!",
                        html
                    );
                    System.out.println("[Birthday Email SENT] To: " + user.getEmail());
                } catch (Exception e) {
                    System.err.println("Failed to send birthday email to " + user.getEmail());
                    e.printStackTrace();
                }
            }
        }
    }
}
