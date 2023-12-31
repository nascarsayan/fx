package com.example.demojavafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StudentController implements Initializable {

    private Connection connection;

    @FXML
    private Button buttonAdd;

    @FXML
    private Button buttonDelete;

    @FXML
    private Button buttonUpdate;

    @FXML
    private TableColumn<Student, String> columnCourse;

    @FXML
    private TableColumn<Student, Integer> columnId;

    @FXML
    private TableColumn<Student, String> columnMobile;

    @FXML
    private TableColumn<Student, String> columnName;

    @FXML
    private TableView<Student> tableStudents;

    @FXML
    private TextField textFieldCourse;

    @FXML
    private TextField textFieldMobile;

    @FXML
    private TextField textFieldName;

    @FXML
    void add(ActionEvent event) {
        String name = textFieldName.getText();
        String mobile = textFieldMobile.getText();
        String course = textFieldCourse.getText();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO registration (name, mobile, course) VALUES (?, ?, ?)");
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, mobile);
            preparedStatement.setString(3, course);
            preparedStatement.executeUpdate();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Student Registation");
            alert.setHeaderText("Student Added");
            alert.setContentText("Student " + name + " is added with course " + course + ".");
            alert.showAndWait();

            table();

            textFieldName.setText("");
            textFieldMobile.setText("");
            textFieldCourse.setText("");
            textFieldName.requestFocus();
        } catch (SQLException ex) {
            Logger.getLogger(StudentController.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }

    private void table() {
        ObservableList<Student> students = FXCollections.observableArrayList();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT id, name, mobile, course FROM registration"
            );
            ResultSet rs = preparedStatement.executeQuery();
            {
                while (rs.next()) {
                    Student student = new Student();
                    student.setId(rs.getInt("id"));
                    student.setName(rs.getString("name"));
                    student.setMobile(rs.getString("mobile"));
                    student.setCourse(rs.getString("course"));
                    students.add(student);
                }
            }

            tableStudents.setItems(students);
            columnId.setCellValueFactory(f -> f.getValue().idProperty().asObject());
            columnName.setCellValueFactory(f -> f.getValue().nameProperty());
            columnMobile.setCellValueFactory(f -> f.getValue().mobileProperty());
            columnCourse.setCellValueFactory(f -> f.getValue().courseProperty());
        } catch (SQLException ex) {
            Logger.getLogger(StudentController.class.getName())
                    .log(Level.SEVERE, null, ex);
        }

        tableStudents.setRowFactory (tv -> {
            TableRow<Student> row = new TableRow<>();
            row.setOnMouseClicked (event -> {
                if (event.getClickCount() == 1 && (!row.isEmpty())) {
                    int selectedIndex = tableStudents.getSelectionModel().getSelectedIndex();
                    int selectedId = Integer.parseInt(
                            String.valueOf(
                                    tableStudents.getItems().get(selectedIndex).getId()
                            )
                    );
                    textFieldName.setText(tableStudents.getItems().get(selectedIndex).getName());
                    textFieldMobile.setText(tableStudents.getItems().get(selectedIndex).getMobile());
                    textFieldCourse.setText(tableStudents.getItems().get(selectedIndex).getCourse());
                }
            });
            return row;
        });
    }

    @FXML
    void delete(ActionEvent event) {
        int selectedIndex = tableStudents.getSelectionModel().getSelectedIndex();
        int selectedId = Integer.parseInt(String.valueOf(tableStudents.getItems().get(selectedIndex).getId()));
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM registration WHERE id = ?");
            preparedStatement.setInt(1, selectedId);
            preparedStatement.executeUpdate();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Student Registration");

            alert.setHeaderText("Student deleted");
            alert.setContentText("Student with id " + selectedId + " deleted");
            alert.showAndWait();
            table();
        }
        catch (SQLException ex) {
            Logger.getLogger(StudentController.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    void update(ActionEvent event) {
        int selectedIndex = tableStudents.getSelectionModel().getSelectedIndex();
        int selectedId = Integer.parseInt(
                String.valueOf(
                        tableStudents.getItems().get(selectedIndex).getId()
                )
        );

        String name = textFieldName.getText();
        String mobile = textFieldMobile.getText();
        String course = textFieldCourse.getText();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE registration SET name = ?, mobile = ?, course = ? WHERE id = ?");
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, mobile);
            preparedStatement.setString(3, course);
            preparedStatement.setInt(4, selectedId);
            preparedStatement.executeUpdate();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Student Registration");

            alert.setHeaderText("Student updated.");
            alert.setContentText("Student updated with id = " + selectedId);
            alert.showAndWait();
            table();
        } catch (SQLException ex) {
            Logger.getLogger(StudentController.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }

    private void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/college",
                    "dev",
                    "pass"
            );
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(
            URL url,
            ResourceBundle resourceBundle
    ) {
        connect();
        table();
    }
}
