package cookbook.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Window;
import java.sql.Connection;
import java.sql.Statement;
import cookbook.DatabaseConnection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/*
 * Controller class for the add weeks popup UI defined in FXML.
 */

public class AddWeeksPopupController {
  private WeeklyDinnerListController weeklyDinnerListController;

  @FXML
  private Button confirmButton, cancelButton;
  @FXML
  private TextField StartDate, EndDate;

  public void initialize() {
    confirmButton.setOnAction(e -> addWeeks());

    cancelButton.setOnAction(e -> {
        // Get the Window the cancelButton is in
        Window window = cancelButton.getScene().getWindow();
        // Hide the Window
        window.hide();
    });
  }

  public void setWeeklyDinnerListController(WeeklyDinnerListController weeklyDinnerListController) {
    this.weeklyDinnerListController = weeklyDinnerListController;
  }

  public void addWeeks() {
    try {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(StartDate.getText(), formatter);
        LocalDate endDate = LocalDate.parse(EndDate.getText(), formatter);

        if (startDate.isAfter(endDate)) {
            // Show an error message
            System.out.println("Start date is after end date");
            return;
        }

        Connection connection = DatabaseConnection.getConnection();
        Statement statement = connection.createStatement();
        statement.executeUpdate("INSERT INTO Weeks (start_date, end_date) VALUES ('" + startDate + "', '" + endDate + "')");

        // Refresh the week list
        weeklyDinnerListController.loadWeeks();
    } catch (DateTimeParseException e) {
        System.out.println("Invalid date format");
    } catch (Exception e) {
        e.printStackTrace();
    }
  }
}