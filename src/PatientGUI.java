/**
 * CEN 3024C - 13950 - Software Development 1
 * November 12, 2025
 * PatientGUI.java
 *
 * This class is created specifically to create a GUI for the DMS. All CRUD operations, as well as the custom function, can be performed
 * by interacting through the GUI instead of the command-line interface. When we receive the information from the GUI, we send it over to
 * the respective function in the PatientDBManager class to deal with it, oftentimes needing to make the connection between the patient
 * object as handled in the GUI and the database server.
 *
 * Each function will have its own tab in the GUI on the left side of the screen while the list of patients will be displayed on the right.
 * */

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class PatientGUI extends JFrame {
    private PatientDBManager patientManager;   //instance of the core logic class
    private PatientTableModel tableModel;
    private JTable patientTable;


    public PatientGUI() {
        patientManager = new PatientDBManager();

        setTitle("Patient Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 1000);

        tableModel = new PatientTableModel();
        patientTable = new JTable(tableModel);
        patientTable.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(patientTable);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Add Patient", createAddPanel());
        tabbedPane.addTab("Upload Patients", createTxtFilePanel());
        tabbedPane.addTab("Remove Patient", createRemovePanel());
        tabbedPane.addTab("Update Patient", createUpdatePanel());
        tabbedPane.addTab("Count Patients", createCustomPanel());
        tabbedPane.addTab("Connect Database", createConnectionPanel());

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tabbedPane, scrollPane);
        splitPane.setDividerLocation(435);

        add(splitPane, BorderLayout.CENTER);

        setVisible(true);
        refreshPatientDisplay();
    }

    /**
     * This method is regularly called to update the list of patients that is displayed after changes have been made to
     * a patient object or the patient list. It is called when a standalone Refresh button is clicked in case any changes
     * that were made are not immediately displayed.
     */

    private void refreshPatientDisplay() {
        try {
            List<patient> patients = patientManager.getAllPatients();
            tableModel.setPatients(patients);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Error: Could not load patient data. \n" + e.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method is for the tab used when the user wants to establish a connection between the GUI and a server.
     * @return The completed panel
     */
    private JPanel createConnectionPanel(){
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField urlField = new JTextField(DBConnectionManager.URL, 30); // Use the default URL as a hint
        JTextField userField = new JTextField(DBConnectionManager.USER, 30);
        JPasswordField passwordField = new JPasswordField(DBConnectionManager.PASSWORD, 30);

        // 2. Button
        JButton connectButton = new JButton("Set & Test Connection");

        // 3. Add Components to Panel
        panel.add(new JLabel("Server URL:"));
        panel.add(urlField);
        panel.add(new JLabel("Username:"));
        panel.add(userField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        // Add the button to the last row, spanning two columns
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(connectButton);
        panel.add(buttonPanel);
        panel.add(new JLabel("")); // Placeholder to complete the 4x2 grid

        // 4. Action Listener for the Button
        connectButton.addActionListener(e -> {
            // Retrieve and store new connection parameters
            String newURL = urlField.getText().trim();
            String newUser = userField.getText().trim();
            String newPassword = new String(passwordField.getPassword()).trim();

            // Update the DBConnectionManager
            DBConnectionManager.setConnectionParams(newURL, newUser, newPassword);

            try {
                // Attempt to get a connection using the new parameters
                Connection testCon = DBConnectionManager.getConnection();
                if (testCon != null) {
                    JOptionPane.showMessageDialog(panel,
                            "Successfully connected to the database!",
                            "Connection Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    testCon.close(); // Close the test connection
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(panel,
                        "Failed to connect: " + ex.getMessage(),
                        "Connection Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PatientGUI().setVisible(true));
    }

    /**
     * This method is for the tab used when the user wants to manually add a single patient into the system.
     * @return The completed panel
     */
    private JPanel createAddPanel() {
        JPanel panel = new JPanel(new GridLayout(7, 2, 5, 5));
        panel.setBackground(new Color(220, 230, 255));

        JTextField IDField = new JTextField(10);
        JTextField NameField = new JTextField(10);
        JTextField AddressField = new JTextField(10);
        JTextField DoctorField = new JTextField(10);
        JTextField InsuranceField = new JTextField(10);
        JTextField DayField = new JTextField(10);
        JButton addButton = new JButton("Add Patient");

        panel.add(new JLabel("ID (7 Digits): "));
        panel.add(IDField);
        panel.add(new JLabel("Name: "));
        panel.add(NameField);
        panel.add(new JLabel("Address: "));
        panel.add(AddressField);
        panel.add(new JLabel("Doctor: "));
        panel.add(DoctorField);
        panel.add(new JLabel("Insurance: "));
        panel.add(InsuranceField);
        panel.add(new JLabel("Day: "));
        panel.add(DayField);
        panel.add(new JLabel());

        panel.add(addButton);

        addButton.addActionListener(e -> {
            try {
                patient p = new patient();

                p.setID(IDField.getText());
                p.setName(NameField.getText());
                p.setAddress(AddressField.getText());
                p.setDoctor(DoctorField.getText());
                p.setInsurance(InsuranceField.getText());
                p.setDayAttending(DayField.getText().toUpperCase().charAt(0));

                patientManager.addPatient(p);
                refreshPatientDisplay();
                JOptionPane.showMessageDialog(this, "Patient " + p.getName() + " has been successfully added", "Success", JOptionPane.INFORMATION_MESSAGE);

                //clear the fields after a successful addition
                IDField.setText(""); NameField.setText(""); AddressField.setText("");
                DoctorField.setText(""); InsuranceField.setText("");DayField.setText("");
            } catch (IllegalArgumentException ex) {JOptionPane.showMessageDialog(this, "Validation Error: \n" + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error while adding: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        return panel;
    }

    /**
     * This method is for the tab used when the user wants to add a batch of patients based on information from a textfile.
     * @return The completed panel
     */
    private JPanel createTxtFilePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(220, 230, 255));

        JTextField filePath = new JTextField(35);
        filePath.setText("Enter file path here");
        JButton uploadButton = new JButton("Upload Patients from a Text File");

        JPanel inputPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        inputPanel.setBackground(new Color(220, 230, 255));
        inputPanel.add(new JLabel("File Path: "));
        inputPanel.add(filePath);
        inputPanel.add(uploadButton);
        inputPanel.add(new JLabel(""));

        panel.add(inputPanel, BorderLayout.NORTH);

        uploadButton.addActionListener(e -> {
            String path = filePath.getText();
            try {
                int addedCount = patientManager.addPatientFromTxtFile(path);
                refreshPatientDisplay();
                JOptionPane.showMessageDialog(this, addedCount + " patient(s) successfully uploaded from the file.", "Import successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (java.io.FileNotFoundException ex){
                JOptionPane.showMessageDialog(this, "Error: File not found at the entered filepath.", "File Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error while adding: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Processing Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        return panel;
    }


    /**
     * This method is for the tab used when the user wants to remove a patient from the system.
     * @return The completed panel
     */
    private JPanel createRemovePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(new Color(220, 230, 255));

        JTextField IDField = new JTextField(10);
        JButton removeButton = new JButton("Remove Patient");
        panel.add(new JLabel("Enter the ID of the patient to be removed: "));
        panel.add(IDField);
        panel.add(removeButton);

        removeButton.addActionListener(e -> {
            String IDToRemove = IDField.getText().trim();

            if (IDToRemove.isEmpty() || IDToRemove.length() != 7 || !IDToRemove.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "Patient ID is not valid", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                boolean success = patientManager.removePatientByID(IDToRemove);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Patient " + IDToRemove + " has been successfully removed", "Success", JOptionPane.INFORMATION_MESSAGE);
                    IDField.setText("");
                    refreshPatientDisplay();
                } else {
                    JOptionPane.showMessageDialog(this, "Patient with ID: " + IDToRemove + " was not found", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error while removing: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    /**
     * This method is for the tab used when the user wants to update one of a patient's attributes.
     * @return The completed panel
     */
    private JPanel createUpdatePanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.setBackground(new Color(220, 230, 255));

        JTextField IDField = new JTextField(10);
        String[] attributes = {"1. ID", "2. Name", "3. Address", "4. Doctor", "5. Insurance", "6. Day attending"};
        JComboBox<String> attributeSelector = new JComboBox<>(attributes);
        JTextField newAttField = new JTextField(35);
        JButton updateButton = new JButton("Update Patient");

        panel.add(new JLabel("ID of the Patient to be updated: "));
        panel.add(IDField);
        panel.add(new JLabel("Please select the attribute to change: "));
        panel.add(attributeSelector);
        panel.add(new JLabel("Enter the new attribute: "));
        panel.add(newAttField);
        panel.add(new JLabel());
        panel.add(updateButton);

        updateButton.addActionListener(e -> {
            String IDToUpdate = IDField.getText().trim();
            int selectedIndex = attributeSelector.getSelectedIndex() + 1;
            String newAtt = newAttField.getText().trim();

            try {
                if (!IDToUpdate.matches("^\\d{7}$")) {
                    throw new IllegalArgumentException("ID must be 7 digits, and only numbers");
                }

                boolean success = patientManager.updatePatientByID(IDToUpdate, selectedIndex, newAtt);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Patient " + IDToUpdate + " has been successfully updated", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshPatientDisplay();
                } else {
                    JOptionPane.showMessageDialog(this, "Update failed. Please check the ID entered, and ensure it exists in the system.", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Patient ID is not valid", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                String message = ex.getMessage().contains("Duplicate entry") ? "Error: ID already exists in the system." : "Error while Updating: " + ex.getMessage();
                JOptionPane.showMessageDialog(this, message, "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        return panel;
    }

    /**
     * This method is for the tab used when the user wants to know the total amount of patients the can expect to attend
     * on a certain day, as specified by the user.
     * @return The completed panel
     */
    private JPanel createCustomPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(220, 230, 255));

        JPanel customPanel = new JPanel(new FlowLayout());
        JTextField dayField = new JTextField(5);
        JButton countButton = new JButton("Total Attending Patients");
        JLabel countResult = new JLabel("Result: 0");

        customPanel.add(new JLabel("Day attending: "));
        customPanel.add(dayField);
        customPanel.add(countButton);
        customPanel.add(countResult);

        countButton.addActionListener(e -> {
            String day = dayField.getText().toUpperCase();

            try {
                if (day.length() != 1 || !day.matches("[MTWRF]")) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid day (M, T, W, R, F", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int count = patientManager.getDayCount(day);
                countResult.setText(count + " Patients will be attending on " + day);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error while counting: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Validation Error" + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton displayAllButton = new JButton("Display All Patients");
        JButton refreshButton = new JButton("Refresh Patients");
        refreshButton.addActionListener(e -> {
            refreshPatientDisplay();
            JOptionPane.showMessageDialog(this, "Patient list updated", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.add(refreshButton);
        panel.add(customPanel, BorderLayout.CENTER);
        panel.add(controlPanel, BorderLayout.SOUTH);

        return panel;
    }

}
