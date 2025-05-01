package com.esprit.wonderwise.Controller.BackOffice;

import com.esprit.wonderwise.Model.Country;
import com.esprit.wonderwise.Service.CountryService;
import com.esprit.wonderwise.Service.RatingService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;

import java.util.*;

import javafx.stage.Stage;

public class DashboardRatingController {
    @FXML
    private javafx.scene.control.Button exportPdfButton;

    @FXML
    private Label totalRatingsLabel;
    @FXML
    private Label likesPercentLabel;
    @FXML
    private Label dislikesPercentLabel;
    @FXML
    private Label topCountryLabel;
    @FXML
    private Label highestAvgCountryLabel;
    @FXML
    private Label lowestAvgCountryLabel;
    @FXML
    private ListView<String> topLikedCountriesList;
    @FXML
    private ListView<String> topDislikedCountriesList;
    @FXML
    private ProgressBar likesProgressBar;
    @FXML
    private ProgressBar dislikesProgressBar;

    private final CountryService countryService = new CountryService();
    private final RatingService ratingService = new RatingService();

    @FXML
    public void initialize() {
        // Run heavy computation in a background thread
        new Thread(() -> {
            List<Country> countries = countryService.readAll();
            Map<Integer, Integer> likesMap = new HashMap<>();
            Map<Integer, Integer> dislikesMap = new HashMap<>();
            int totalRatings = 0;
            double totalLikes = 0;
            double totalDislikes = 0;
            double highestAvg = -1;
            double lowestAvg = Double.MAX_VALUE;
            String topCountry = "-";
            String highestAvgCountry = "-";
            String lowestAvgCountry = "-";
            int maxRatings = 0;

            for (Country country : countries) {
                int likes = ratingService.countLikes(country.getId());
                int dislikes = ratingService.countDislikes(country.getId());
                likesMap.put(country.getId(), likes);
                dislikesMap.put(country.getId(), dislikes);
                int countryTotal = likes + dislikes;
                totalRatings += countryTotal;
                totalLikes += likes;
                totalDislikes += dislikes;
                double avg = countryTotal > 0 ? (double) likes / countryTotal * 5 : 0;
                if (countryTotal > maxRatings) {
                    maxRatings = countryTotal;
                    topCountry = country.getName() + " (" + countryTotal + ")";
                }
                if (countryTotal > 0 && avg > highestAvg) {
                    highestAvg = avg;
                    highestAvgCountry = country.getName() + String.format(" (%.2f)", avg);
                }
                if (countryTotal > 0 && avg < lowestAvg) {
                    lowestAvg = avg;
                    lowestAvgCountry = country.getName() + String.format(" (%.2f)", avg);
                }
            }

            // Update UI on JavaFX Application Thread
            int finalTotalRatings = totalRatings;
            double finalTotalLikes = totalLikes;
            double finalTotalDislikes = totalDislikes;
            String finalTopCountry = topCountry;
            String finalHighestAvgCountry = highestAvgCountry;
            String finalLowestAvgCountry = lowestAvgCountry;

            Platform.runLater(() -> {
                // Set labels
                totalRatingsLabel.setText(String.valueOf(finalTotalRatings));
                topCountryLabel.setText(finalTopCountry);
                highestAvgCountryLabel.setText(finalHighestAvgCountry);
                lowestAvgCountryLabel.setText(finalLowestAvgCountry);

                // Top liked/disliked countries
                List<Map.Entry<Integer, Integer>> sortedLikes = new ArrayList<>(likesMap.entrySet());
                sortedLikes.sort((a, b) -> b.getValue() - a.getValue());
                ObservableList<String> topLiked = FXCollections.observableArrayList();
                for (int i = 0; i < Math.min(3, sortedLikes.size()); i++) {
                    int cid = sortedLikes.get(i).getKey();
                    int val = sortedLikes.get(i).getValue();
                    String cname = countries.stream().filter(c -> c.getId() == cid).findFirst().map(Country::getName).orElse("?");
                    topLiked.add(cname + " (" + val + ")");
                }
                topLikedCountriesList.setItems(topLiked);

                List<Map.Entry<Integer, Integer>> sortedDislikes = new ArrayList<>(dislikesMap.entrySet());
                sortedDislikes.sort((a, b) -> b.getValue() - a.getValue());
                ObservableList<String> topDisliked = FXCollections.observableArrayList();
                for (int i = 0; i < Math.min(3, sortedDislikes.size()); i++) {
                    int cid = sortedDislikes.get(i).getKey();
                    int val = sortedDislikes.get(i).getValue();
                    String cname = countries.stream().filter(c -> c.getId() == cid).findFirst().map(Country::getName).orElse("?");
                    topDisliked.add(cname + " (" + val + ")");
                }
                topDislikedCountriesList.setItems(topDisliked);

                // Progress bars
                double likeRatio = finalTotalRatings > 0 ? finalTotalLikes / finalTotalRatings : 0;
                double dislikeRatio = finalTotalRatings > 0 ? finalTotalDislikes / finalTotalRatings : 0;
                likesProgressBar.setProgress(likeRatio);
                dislikesProgressBar.setProgress(dislikeRatio);

                // Set percentage labels
                int likesPercent = (int) Math.round(likeRatio * 100);
                int dislikesPercent = (int) Math.round(dislikeRatio * 100);
                likesPercentLabel.setText(likesPercent + "%");
                dislikesPercentLabel.setText(dislikesPercent + "%");
            });
        }).start();
    }

    @FXML
    public void handleExportPdf() {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Export Dashboard Ratings PDF");
        fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        fileChooser.setInitialFileName("DashboardRatings_" + java.time.LocalDate.now() + ".pdf");
        java.io.File file = fileChooser.showSaveDialog(exportPdfButton.getScene().getWindow());
        if (file == null) return;
        try {
            com.lowagie.text.Document document = new com.lowagie.text.Document();
            com.lowagie.text.pdf.PdfWriter writer = com.lowagie.text.pdf.PdfWriter.getInstance(document, new java.io.FileOutputStream(file));
            document.open();

            // Background color
            com.lowagie.text.Rectangle page = document.getPageSize();
            com.lowagie.text.pdf.PdfContentByte canvas = writer.getDirectContentUnder();
            canvas.setColorFill(new java.awt.Color(249, 250, 252));
            canvas.rectangle(0, 0, page.getWidth(), page.getHeight());
            canvas.fill();

            // Header bar with logo above centered title
            com.lowagie.text.pdf.PdfPTable header = new com.lowagie.text.pdf.PdfPTable(1);
            header.setWidthPercentage(100);
            header.getDefaultCell().setBorder(com.lowagie.text.Rectangle.NO_BORDER);

            // Try absolute path first, then fallback to relative
            com.lowagie.text.Image logo = null;
            try {
                java.io.File logoFile = new java.io.File("C:\\Users\\lpatron\\OneDrive\\Desktop\\New folder (4)\\WonderwiseKhalilV5\\Wonderwise\\src\\main\\resources\\com\\esprit\\wonderwise\\icons\\rate.png");
                if (logoFile.exists()) {
                    logo = com.lowagie.text.Image.getInstance(logoFile.getAbsolutePath());
                } else {
                    logo = com.lowagie.text.Image.getInstance("src/main/resources/com/esprit/wonderwise/icons/rate.png");
                }
                logo.scaleAbsolute(40, 40);
                logo.setAlignment(com.lowagie.text.Image.ALIGN_CENTER);
            } catch (Exception e) { /* Ignore if logo not found */ }

            if (logo != null) {
                com.lowagie.text.pdf.PdfPCell logoCell = new com.lowagie.text.pdf.PdfPCell(logo, false);
                logoCell.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
                logoCell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
                logoCell.setVerticalAlignment(com.lowagie.text.Element.ALIGN_MIDDLE);
                logoCell.setPaddingBottom(8f);
                header.addCell(logoCell);
            }
            com.lowagie.text.Font headerFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 28, com.lowagie.text.Font.BOLD, new java.awt.Color(52, 152, 219));
            com.lowagie.text.Paragraph titlePara = new com.lowagie.text.Paragraph("Country Ratings\nOverview", headerFont);
            titlePara.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
            titlePara.setSpacingAfter(16f);
            com.lowagie.text.pdf.PdfPCell titleCell = new com.lowagie.text.pdf.PdfPCell(titlePara);
            titleCell.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
            titleCell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
            titleCell.setVerticalAlignment(com.lowagie.text.Element.ALIGN_MIDDLE);
            titleCell.setPaddingBottom(10f);
            header.addCell(titleCell);
            document.add(header);
            document.add(new com.lowagie.text.Paragraph(" "));
            document.add(new com.lowagie.text.Paragraph(" "));


            // Statistics Card
            com.lowagie.text.pdf.PdfPTable statTable = new com.lowagie.text.pdf.PdfPTable(2);
            statTable.setWidthPercentage(60);
            statTable.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
            com.lowagie.text.Font statLabelFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 15, com.lowagie.text.Font.BOLD, new java.awt.Color(44, 62, 80));
            com.lowagie.text.Font statValueFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 15, com.lowagie.text.Font.NORMAL, new java.awt.Color(41, 128, 185));
            String[][] stats = {
                    {"Total Ratings", totalRatingsLabel.getText()},
                    {"Most Rated", topCountryLabel.getText()},
                    {"Highest Average", highestAvgCountryLabel.getText()},
                    {"Lowest Average", lowestAvgCountryLabel.getText()}
            };
            for (String[] row : stats) {
                com.lowagie.text.pdf.PdfPCell labelCell = new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Phrase(row[0], statLabelFont));
                labelCell.setBackgroundColor(new java.awt.Color(236, 240, 241));
                labelCell.setBorderColor(new java.awt.Color(52, 152, 219));
                labelCell.setPadding(8f);
                statTable.addCell(labelCell);
                com.lowagie.text.pdf.PdfPCell valueCell = new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Phrase(row[1], statValueFont));
                valueCell.setBorderColor(new java.awt.Color(52, 152, 219));
                valueCell.setPadding(8f);
                statTable.addCell(valueCell);
            }
            statTable.setSpacingAfter(20f);
            document.add(statTable);

            // Top 3 Countries by Likes Card
            com.lowagie.text.Font likesTitleFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 17, com.lowagie.text.Font.BOLD, new java.awt.Color(39, 174, 96));
            com.lowagie.text.Paragraph likesTitle = new com.lowagie.text.Paragraph("Top 3 Countries by Likes", likesTitleFont);
            likesTitle.setSpacingAfter(16f);
            document.add(likesTitle);
            com.lowagie.text.pdf.PdfPTable likesTable = new com.lowagie.text.pdf.PdfPTable(1);
            likesTable.setWidthPercentage(40);
            for (int i = 0; i < topLikedCountriesList.getItems().size(); i++) {
                com.lowagie.text.pdf.PdfPCell cell = new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Phrase(topLikedCountriesList.getItems().get(i)));
                cell.setBorderColor(new java.awt.Color(39, 174, 96));
                cell.setPadding(7f);
                if (i % 2 == 0) cell.setBackgroundColor(new java.awt.Color(236, 240, 241));
                cell.setBorderWidth(1.2f);
                cell.setCellEvent(new com.lowagie.text.pdf.PdfPCellEvent() {
                    public void cellLayout(com.lowagie.text.pdf.PdfPCell cell, com.lowagie.text.Rectangle rect, com.lowagie.text.pdf.PdfContentByte[] canvases) {
                        canvases[com.lowagie.text.pdf.PdfPTable.BASECANVAS].roundRectangle(rect.getLeft(), rect.getBottom(), rect.getWidth(), rect.getHeight(), 8f);
                    }
                });
                likesTable.addCell(cell);
            }
            likesTable.setSpacingAfter(20f);
            document.add(likesTable);

            // Top 3 Countries by Dislikes Card
            com.lowagie.text.Font dislikesTitleFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 17, com.lowagie.text.Font.BOLD, new java.awt.Color(231, 76, 60));
            com.lowagie.text.Paragraph dislikesTitle = new com.lowagie.text.Paragraph("Top 3 Countries by Dislikes", dislikesTitleFont);
            dislikesTitle.setSpacingAfter(16f);
            document.add(dislikesTitle);
            com.lowagie.text.pdf.PdfPTable dislikesTable = new com.lowagie.text.pdf.PdfPTable(1);
            dislikesTable.setWidthPercentage(40);
            for (int i = 0; i < topDislikedCountriesList.getItems().size(); i++) {
                com.lowagie.text.pdf.PdfPCell cell = new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Phrase(topDislikedCountriesList.getItems().get(i)));
                cell.setBorderColor(new java.awt.Color(231, 76, 60));
                cell.setPadding(7f);
                if (i % 2 == 0) cell.setBackgroundColor(new java.awt.Color(253, 237, 236));
                cell.setBorderWidth(1.2f);
                cell.setCellEvent(new com.lowagie.text.pdf.PdfPCellEvent() {
                    public void cellLayout(com.lowagie.text.pdf.PdfPCell cell, com.lowagie.text.Rectangle rect, com.lowagie.text.pdf.PdfContentByte[] canvases) {
                        canvases[com.lowagie.text.pdf.PdfPTable.BASECANVAS].roundRectangle(rect.getLeft(), rect.getBottom(), rect.getWidth(), rect.getHeight(), 8f);
                    }
                });
                dislikesTable.addCell(cell);
            }
            dislikesTable.setSpacingAfter(20f);
            document.add(dislikesTable);

            // Ratings Distribution Card
            com.lowagie.text.Font distFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 15, com.lowagie.text.Font.BOLD, new java.awt.Color(52, 73, 94));
            com.lowagie.text.Paragraph distTitle = new com.lowagie.text.Paragraph("Ratings Distribution", distFont);
            distTitle.setSpacingAfter(16f);
            document.add(distTitle);
            com.lowagie.text.pdf.PdfPTable distTable = new com.lowagie.text.pdf.PdfPTable(2);
            distTable.setWidthPercentage(30);
            String[][] distRows = {
                    {"Likes %", likesPercentLabel.getText()},
                    {"Dislikes %", dislikesPercentLabel.getText()}
            };
            for (String[] row : distRows) {
                com.lowagie.text.pdf.PdfPCell labelCell = new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Phrase(row[0], statLabelFont));
                labelCell.setBackgroundColor(new java.awt.Color(236, 240, 241));
                labelCell.setBorderColor(new java.awt.Color(52, 73, 94));
                labelCell.setPadding(8f);
                distTable.addCell(labelCell);
                com.lowagie.text.pdf.PdfPCell valueCell = new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Phrase(row[1], statValueFont));
                valueCell.setBorderColor(new java.awt.Color(52, 73, 94));
                valueCell.setPadding(8f);
                distTable.addCell(valueCell);
            }
            distTable.setSpacingAfter(30f);
            document.add(distTable);

            // Footer bar (centered, date only to yyyy-MM-dd HH:mm)
            com.lowagie.text.pdf.PdfPTable footer = new com.lowagie.text.pdf.PdfPTable(1);
            footer.setWidthPercentage(100);
            com.lowagie.text.Font footerFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 11, com.lowagie.text.Font.ITALIC, new java.awt.Color(127, 140, 141));
            java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String formattedDate = java.time.LocalDateTime.now().format(dtf);
            com.lowagie.text.Paragraph footerText = new com.lowagie.text.Paragraph("Generated by Wonderwise Dashboard - " + formattedDate, footerFont);
            footerText.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
            com.lowagie.text.pdf.PdfPCell footerCell = new com.lowagie.text.pdf.PdfPCell(footerText);
            footerCell.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
            footerCell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
            footerCell.setPadding(10f);
            footer.addCell(footerCell);
            document.add(footer);

            document.close();
            javafx.application.Platform.runLater(() -> {
                com.esprit.wonderwise.Utils.DialogUtils.showCustomDialog(
                        "PDF Exported",
                        "PDF exported successfully as " + file.getName(),
                        true,
                        getCurrentStage()
                );
            });
        } catch (Exception e) {
            javafx.application.Platform.runLater(() -> {
                com.esprit.wonderwise.Utils.DialogUtils.showCustomDialog(
                        "PDF Export Error",
                        "Failed to export PDF: " + e.getMessage(),
                        false,
                        getCurrentStage()
                );
            });
        }
    }

    // Helper to get the main Stage for custom dialogs
    private Stage getCurrentStage() {
        return (Stage) exportPdfButton.getScene().getWindow();
    }
}