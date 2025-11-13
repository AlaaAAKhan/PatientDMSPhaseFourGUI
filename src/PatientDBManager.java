/**
 * Alaa Khan
 * CEN 3024C - 13950 - Software Development 1
 * November 12, 2025
 * PatientDBManager.java
 *
 * This is a utility class; it contains all the functions used in the program: the CRUD functions. These are to add,
 * remove, update, and display the total number of patients, as well as a custom function that counts the total number
 * of patients attending on a certain day.
 * */

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

public class PatientDBManager {
    ArrayList<patient> patientsList = new ArrayList<patient>();

    /**
     * @param rs Contains the patient data needed to be extracted to create a patient object.
     * @throws SQLException In case the connection with the server is interrupted
     *
     * This is a helper method. It sets the ResultSet row into a Patient Object.
      */
    private patient createPatientFromResultSet(ResultSet rs) throws SQLException {
        patient p = new patient();
        p.setID(rs.getString("ID"));
        p.setName(rs.getString("Name"));
        p.setAddress(rs.getString("Address"));
        p.setDoctor(rs.getString("Doctor"));
        p.setInsurance(rs.getString("Insurance"));
        String day = rs.getString("Day_Attending");
        p.setDayAttending(day.charAt(0));

        return p;
    }

    /**
     * C: CREATE:
     * @param p The patient object needed to establish the patient object.
     * @throws SQLException In case the connection with the server is interrupted
     * @throws IllegalArgumentException In case the input does not meet the attribute requirements
     *
     * This method is used when manually adding a single patient into the system. Calls the checker methods to ensure
     * the entered ID and Day are valid. In order to integrate the action of adding the patient object into the system,
     * the SQL statement for adding a patient is set into a String that is then called once the patient needs to be added.
     */
    public void addPatient(patient p) throws SQLException, IllegalArgumentException {
        if (p.getID().length() != 7 || !checkIfAllInts(p.getID())) {
            throw new IllegalArgumentException("The ID must be 7 digits exactly containing only digits"); }

        if (!validDay(String.valueOf(p.getDayAttending()))) {
            throw new IllegalArgumentException("The day attending is not valid. Please enter a valid day attending (M, T, W, R, F)"); }

        String sql = "INSERT INTO patients (ID, Name, Address, Doctor, Insurance, Day_attending) VALUES (?,?,?,?,?,?)";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getID());
            ps.setString(2, p.getName());
            ps.setString(3, p.getAddress());
            ps.setString(4, p.getDoctor());
            ps.setString(5, p.getInsurance());
            ps.setString(6, String.valueOf(p.getDayAttending()));

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Failed to insert patient, no rows were affected.");
            }
        }
    }



    /**
     * @param patientID The patient ID that the user inputted.
     * @return Whether the requirement is successful or not, a boolean (true or false) is returned.
     *
     * Takes in the inputted ID and loops through each character to ensure they are all integers. If they are, it
     * returns a "true" statement to the IDChecker, allowing for the code to continue. Else, it will return "false" and
     * cause the ID Checker to restart and ask for a different ID.
    */
    public boolean checkIfAllInts(String patientID) {
//        for (char c : patientID.toCharArray()) {
//            if (!Character.isDigit(c)) {
//                return false;
//            }
//        }
//
//        return true;

        return patientID.matches("\\d+");
    }


    /**
     * @param patientDay The patient's day of attendance that the user inputted.
     * @return Whether the requirement is successful or not, a boolean (true or false) is returned.
     *
     * Takes in the inputted day as a String and converts it to a char. If it does not contain any letters, it will
     * return a "true" statement to the dayChecker() method, allowing for the code to continue. Else, it will return
     * "false" and cause the dayChecker() method to restart and ask for a different day
   */
    public boolean checkIfChar(String patientDay){
        char c = patientDay.charAt(0);
        if (Character.isDigit(c)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     *  @param patientDay The patient's day of attendance that the user inputted.
     *  @return Whether the requirement is successful or not, a boolean (true or false) is returned.
     *
     * Takes in the inputted day as a String. If it is one of the acceptable days of attendance, it will return a "true"
     * statement to the dayChecker() method, allowing for the code to continue. Else, it will return "false" and cause
     * the dayChecker() method to restart and ask for a different day
     */
    public boolean validDay(String patientDay){
        String c = patientDay;
        if (c.equals("M") || c.equals("T") || c.equals("W") || c.equals("R") || c.equals("F")) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * C: CREATE ...cont'd
     * @param filepath The filepath of the location of the textfile containing patient data that needs to be batch-added.
     * @return The integer amount of patients that were successfully added from the textfile into the system.
     * @throws Exception Covers both SQLException and IllegalArgumentException should either a validation error
     * OR database error arises (such as a duplicate ID or database connection issues)
     *
     * This method receives a filepath from the GUI. Assuming the data is in the following format
     * "ID-Name-Address-Doctor-Insurance-Day", it will set the inputted data to its associating fields of ID, Name,
     * Address, Doctor, Insurance, and Day, before adding it all to the list of patient. The tokenizer is to recognize
     * the split between field and detect the range of information to take in per field. It specifies that the divider it
     * is looking for is going to be the "-" dash. Should the inputted filepath not work, an error is thrown.
     *
     * There is also a counter to keep track of how many patients from the file are being added to the list. This number is
     * then sent back to the PatientGUI class.
      */
    public int addPatientFromTxtFile(String filepath) throws Exception{
        int count = 0;
        File file = new File(filepath);
        if (!file.exists()) {
            throw new FileNotFoundException("File Not Found");
        }


        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            try {
                StringTokenizer token = new StringTokenizer(line, "-");
                if (token.countTokens() != 6) {
                    throw new IllegalArgumentException("Incorrect number of attributes");
                }

                patient p1 = new patient();
                p1.setID(token.nextToken());
                p1.setName(token.nextToken());
                p1.setAddress(token.nextToken());
                p1.setDoctor(token.nextToken());
                p1.setInsurance(token.nextToken());
                p1.setDayAttending(token.nextToken().charAt(0));

                addPatient(p1);
                count++;
                } catch (IllegalArgumentException | SQLException e) {
                System.err.println("Skipped invalid patient record or failed DB insertion for line: \"" + line + "\". Error: " + e.getMessage());
            }

        }
        scanner.close();
        return count;
    }

    /**
     * R: REMOVE
     * @param patientID The patient ID that the user inputted needing to be removed.
     * @return The integer amount of rows affected by the end of the method; ensures whether removal was successful or not.
     * @throws SQLException In case the connection with the server is interrupted
     *
     * This class removes a patient based on their ID. It receives the patient ID and searches the list with a
     * simple for-loop. It first ensures that the ID entered meets the requirements of a valid ID before it performs the
     * function. In order to integrate the action of removing the patient object from the system, the SQL statement for
     * removing a patient is set into a String that is then called once the patient needs to be removed.
     */
    public boolean removePatientByID(String patientID) throws SQLException {
        if (patientID.length() != 7 || !checkIfAllInts(patientID)) {
            return false;
        }

        patient patientToRemove = null;
        for (patient p : patientsList) {
            if (p.getID().equals(patientID)) {
                patientToRemove = p;
                break;
            }
        }

        String sql = "DELETE FROM patients WHERE ID = ?";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patientID);
            int affectedRows = ps.executeUpdate();

            return affectedRows > 0;
        }

    }

    /**
     * U: UPDATE
     * @param patientID The patient ID that the user inputted needing to be updated.
     * @param AttributeIndex An integer representing the index of the field that requires updating.
     * @param newAtt A String statement that is the new information the user wants to replace the previously established
     *               information with.
     * @return The integer amount of rows affected by the end of the method; ensures whether removal was successful or not.
     * @throws SQLException In case the connection with the server is interrupted
     * @throws IllegalArgumentException In case the input does not meet the attribute requirements
     *
     * This class takes receives the ID of the patient the user wants to change an attribute of, the attribute index of
     * the field that requires updating, and the new information they would like to replace with the previous established
     * attribute. The specific attribute they want to update is used to run the switch case. Then, it updates the attribute
     * with the new info (that has been received). In order to integrate the action of updating the patient's attribute
     * from the system, the SQL statement for updating a patient's attribute is set into a String that is then called once
     * the attribute needs to be updated.
     */
    public boolean updatePatientByID(String patientID, int AttributeIndex, String newAtt) throws SQLException, IllegalArgumentException {
        String fieldName;
        switch (AttributeIndex) {
            case 1: fieldName = "ID"; break;
            case 2: fieldName = "Name"; break;
            case 3: fieldName = "Address"; break;
            case 4: fieldName = "Doctor"; break;
            case 5: fieldName = "Insurance"; break;
            case 6: fieldName = "Day_attending"; if (newAtt.length() > 1) throw new IllegalArgumentException("Day must be a single character");
            break;

            default:
                return false;
        }

        String sql = "UPDATE patients SET " + fieldName + " = ? WHERE ID = ?";

        try (Connection conn = DBConnectionManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newAtt);
            ps.setString(2, patientID);

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0; //Only true if a record was updated
        } catch (SQLException e) {
            throw e;
        }
    }

    /**
     * D: DISPLAY
     * @return the List of patients in the system.
     * @throws SQLException In case the connection with the server is interrupted
     *
     * Fetches all the patients currently in the system to display them in the JTable by creating a List of the
     * patients, and then sending back the SQL Statement needed to display all the patients in that list into a String.
     */
    public List<patient> getAllPatients() throws SQLException {
        List<patient> patients = new ArrayList<>();
        String sql = "SELECT ID, Name, Address, Doctor, Insurance, Day_attending FROM patients ORDER by ID";

        try (Connection conn = DBConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                patients.add(createPatientFromResultSet(rs));
            }
        }
        return patients;
    }

    /**
     * @param day The day of the week as a String, as provided by the user.
     * @param ListOfPatients The list of patients that will need to be iterated through to check for day matches.
     * @return The total number of attending patients for the specified day.
     *
     * Sets a counter to 0, and then loops through the list of patients. Using the getDayAttending(), every time it sees
     * a match between the day the user inputted and the day a patient has set to attend, the counter will increase.
     * Once the end of the list is reached, it will return the total number of attending patients for that day.
     */
    public int patientCounter(String day, ArrayList<patient> ListOfPatients) {
        int attending = 0;
        for (patient p : ListOfPatients) {
            if (day.equals(String.valueOf(p.getDayAttending()))) {
                attending++;
            }
        }
        return attending;
    }

    /**
     * @param day The day of the week as a String, as provided by the user.
     * @return The total number of attending patients for the specified day.
     * @throws SQLException In case the connection with the server is interrupted
     * @throws IllegalArgumentException In case the input does not meet the attribute requirements
     *
     * Receives the day of the week as a string. First, determines if the day meets the requirements for a valid Day. If so,
     * it creates an SQL statement that will count the patients, and then send that amount back in the form of a String.
     */
    public int getDayCount(String day) throws SQLException, IllegalArgumentException {
        if (day.length() != 1 || !checkIfChar(day)) {
            throw new IllegalArgumentException("Day must be a single Character (M, T, W, R, F).");
        }

        String dayUpper = day.toUpperCase();
        if (!validDay(dayUpper)) {
            throw new IllegalArgumentException("Day must be a valid day (M, T, W, R, F).");
        }
        int dayCount = 0;
        String sql = "SELECT COUNT(*) AS total FROM patients WHERE Day_attending = ?";
        try (Connection conn = DBConnectionManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dayUpper);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    dayCount = rs.getInt("total");
                }
            }
        }

        return dayCount;
    }

}
