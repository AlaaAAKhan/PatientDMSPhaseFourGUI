/**
 * Alaa Khan
 * CEN 3024C - 13950 - Software Development 1
 * November 12, 2025
 * patient.java
 *
 * This is a constructor class. It creates a default constructor, so it does not take in any parameters. This is so
 * that the patient object can be created, but its fields will be populated later based on the user's input.
 * */

public class patient {
    String ID;
    String name;
    String address;
    String doctor;
    String insurance;
    char dayAttending;

    patient() {
        this.ID = ID;
        this.name = name;
        this.address = address;
        this.doctor = doctor;
        this.insurance = insurance;
    }

    //GETTERS
    public String getID() {
        return ID;
    }
    public String getName() {
        return name;
    }
    public String getAddress() {
        return address;
    }
    public String getDoctor() {
        return doctor;
    }
    public String getInsurance() {
        return insurance;
    }
    public char getDayAttending() {
        return dayAttending;
    }

    //SETTERS
    public void setID(String ID) {
        this.ID = ID;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }
    public void setInsurance(String insurance) {
        this.insurance = insurance;
    }
    public void setDayAttending(char dayAttending) {
        this.dayAttending = dayAttending;
    }

}
