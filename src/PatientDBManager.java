/*
 *
 * CEN 3024C - 13950 - Software Development 1
 * October 26, 2025
 * allPatients.java
 *
 * This is a utility class; it contains all the functions used in the program: the CRUD functions. These are to add,
 * remove, update, and display the total number of patients, as well as a custom function that counts the total number
 * of patients attending on a certain day.
 * */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class allPatients {
    Scanner input = new Scanner(System.in);
    ArrayList<patient> patientsList = new ArrayList<patient>();

    /* GUI: C: CREATE
     When adding a patient manually, the user will enter the ID, name, address, doctor, insurance, and
     attending day of the patient in the GUI and send that information over to this method.
     Here, the ID and days attending are checked because they need to meet certain requirements.
     After ensuring it meets the requirements, this method adds the patient to the list.
    */
    public void addPatient(patient p) throws IllegalArgumentException {
        if (p.getID().length() != 7 || !checkIfAllInts(p.getID())) {
            throw new IllegalArgumentException("The ID must be 7 digits exactly containing only digits"); }
        if (checkIfExists(p.getID())) {
            throw new IllegalArgumentException("This ID already exists"); }

        if (!validDay(String.valueOf(p.getDayAttending()))) {
            throw new IllegalArgumentException("The day attending is not valid. Please enter a valid day attending (M, T, W, R, F)"); }

        patientsList.add(p);
    }



    /*
     Takes in the inputted ID and loops through each character to ensure they are all integers. If they are, it
     returns a "true" statement to the IDChecker, allowing for the code to continue. Else, it will return "false" and
     cause the ID Checker to restart and ask for a different ID.
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

    /*
     Takes in the inputted ID and loops through each character to ensure it does not already exist in the system. If
     it does, it returns a "true" statement to the IDChecker, cause the ID Checker to restart and ask for a different ID.
     Else, it will return "false" and allow for the code to continue.
    */
    public boolean checkIfExists(String patientID) {
        for (int i = 0; i < patientsList.size(); i++) {
            if (patientsList.get(i).getID().equals(patientID)) {
                return true;
            }
        }
        return false;
    }


    /*
   Takes in the inputted day as a String and converts it to a char. If it does not contain any letters, it will
   return a "true" statement to the dayChecker() method, allowing for the code to continue. Else, it will return
   "false" and cause the dayChecker() method to restart and ask for a different day
   */
    public boolean checkIfChar(String patientDay){
        char c = patientDay.charAt(0);
        if (Character.isDigit(c)) {
            return false;
        } else {
            return true;
        }
    }

    /*
     Takes in the inputted day as a String. If it is one of the acceptable days of attendance, it will return a "true"
     statement to the dayChecker() method, allowing for the code to continue. Else, it will return "false" and cause
     the dayChecker() method to restart and ask for a different day
     */
    public boolean validDay(String patientDay){
        String c = patientDay;
        if (c.equals("M") || c.equals("T") || c.equals("W") || c.equals("R") || c.equals("F")) {
            return true;
        } else {
            return false;
        }
    }


    /*
     GUI C: CREATE ...cont'd
     This method receives a filepath from the GUI. Assuming the data is in the following format
     "ID-Name-Address-Doctor-Insurance-Day", it will set the inputted data to its associating fields of ID, Name,
     Address, Doctor, Insurance, and Day, before adding it all to the list of patient. The tokenizer is to recognize
     the split between field and detect the range of information to take in per field. It specifies that the divider it
     is looking for is going to be the "-" dash. Should the inputted filepath not work, an error is thrown.

     There is also a counter to keep track of how many patients from the file are being added to the list. This number is
     then sent back to the PatientGUI class.
      */
    public int addPatientFromTxtFile(String filepath) throws Exception{
        int count = 0;
        File file = new File(filepath);
        if (!file.exists()) {
            throw new java.io.FileNotFoundException("File Not Found");
        }

        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            StringTokenizer token = new StringTokenizer(line, "-");

            if (token.countTokens() == 6){
                try {
                    patient p1 = new patient();
                    p1.setID(token.nextToken());
                    p1.setName(token.nextToken());
                    p1.setAddress(token.nextToken());
                    p1.setDoctor(token.nextToken());
                    p1.setInsurance(token.nextToken());
                    p1.setDayAttending(token.nextToken().charAt(0));

                    addPatient(p1);
                    count++;
                } catch (Exception e){
                    System.err.println("Skipped invalid patient record: " + line);
                }
            }

        }
        scanner.close();
        return count;
    }

    /*
    GUI: R: REMOVE
    This class removes a patient based on their ID. It receives the patient ID and searches the list with a
    simple for-loop. It first ensures that the ID entered meets the requirements of a valid ID before it performs the
    function. Based on the result, a boolean answer of True or False is returned, determining the "success" of the removal
    or not.
     */
    public boolean removePatientByID(String patientID) {
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

        if (patientToRemove != null) {
            patientsList.remove(patientToRemove);
            System.out.println("\nPatient ID: " + patientID + " removed");
            return true;
        } else {
            System.out.println("\nPatient ID: " + patientID + " does not exist");
            return false;
        }
    }

    /*
    GUI: U: UPDATE
    This class takes receives the ID of the patient the user wants to change an attribute of. After making sure that it is
    a valid ID, the specific attribute they want to update (that is also received) is used to run the switch case. Then, it
    updates the attribute with the new info (that has been received). Finally, it sends back a boolean answer of True or False
    determining the "success" of the update or not.
     */
    public boolean updatePatientByID(String patientID, int AttributeIndex, String newAtt) throws IllegalArgumentException {
        patient patientToUpdate = null;
        for (patient p : patientsList) {
            if (p.getID().equals(patientID)) {
                patientToUpdate = p;
                break;
            }
        }
        if (patientToUpdate == null) {
            return false;
        }

        switch (AttributeIndex) {
            case 1:
                if (!newAtt.matches("^\\d{7}$")) {
                    throw new IllegalArgumentException("IDs must be 7 digits, all integers, and not already in the system.");
                }
                if (checkIfExists(newAtt)) {
                    throw new IllegalArgumentException("There is already a patient in the system with this ID");
                }
                patientToUpdate.setID(newAtt);
                break;

            case 2: patientToUpdate.setName(newAtt); break;

            case 3: patientToUpdate.setAddress(newAtt); break;

            case 4: patientToUpdate.setDoctor(newAtt); break;

            case 5: patientToUpdate.setInsurance(newAtt); break;

            case 6:
                String day = newAtt.trim().toUpperCase();
                if (day.length() != 1) {
                    throw new IllegalArgumentException("Day must be a single character (M, T, W, R, F).");
                }
                if (!validDay(day)) {
                    throw new IllegalArgumentException("Day must be a valid character (M, T, W, R, F).");
                }

                patientToUpdate.setDayAttending(newAtt.toUpperCase().charAt(0));
                break;

            default:
                return false;
        }

        return true;
    }


    /*D: DISPLAY
    Loop to display patient from patientsList */
    public String getPatientsListAsString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CURRENT LIST OF PATIENTS:\n");
        for (patient p : patientsList) {
            builder.append("ID: ").append(p.getID())
                    .append(", Name: ").append(p.getName())
                    .append(", Address: ").append(p.getAddress())
                    .append(", Doctor: ").append(p.getDoctor())
                    .append(", Insurance: ").append(p.getInsurance())
                    .append(", Day Attending: ").append(p.getDayAttending())
                    .append("\n");
        }
        return builder.toString();
    }


    /*
    Takes in the day of the week as a string. Sets a counter to 0, and then loops through the list of patients. Using
    the getDayAttending(), every time it sees a match between the day the user inputted and the day a patient has set
    to attend, the counter will increase. Once the end of the list is reached, it will return the total number of
    attending patients for that day.
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

    /*
    Receives the day of the week as a string. First, determines if the day meets the requirements for a valid Day. If so,
    it begins a counter and sets it to the amount that patientCounter calculates for it. It then sends that amount back.
     */
    public int getDayCount(String day) throws IllegalArgumentException {
        if (day.length() != 1 || !checkIfChar(day)) {
            throw new IllegalArgumentException("Day must be a single Character (M, T, W, R, F).");
        }

        String dayUpper = day.toUpperCase();
        if (!validDay(dayUpper)) {
            throw new IllegalArgumentException("Day must be a valid day (M, T, W, R, F).");
        }

        int dayCount = patientCounter(dayUpper, patientsList);

        return dayCount;
    }

}
