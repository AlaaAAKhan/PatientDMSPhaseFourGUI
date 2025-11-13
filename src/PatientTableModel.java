/**
 * Alaa Khan
 * CEN 3024C - 13950 - Software Development 1
 * November 12, 2025
 * PatientTableModel.java
 *
 * This class is created specifically to build the table view of the patients for the GUI. It creates a table model, uses
 * the Patient attributes as columns, and then gets and sets the values into the table to be displayed as needed.
 * */

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.ArrayList;

public class PatientTableModel extends AbstractTableModel {
    private List<patient> patients;
    private final String[] columnNames = {"ID", "Name", "Address", "Doctor", "Insurance", "Day Attending"};

    public PatientTableModel() {
        this.patients = new ArrayList<>();
    }

    public void setPatients(List<patient> patients) {
        this.patients = patients;
        fireTableDataChanged();  //Tells the JTable to redraw itself
    }

    @Override
    public int getRowCount() { return patients.size(); }

    @Override
    public int getColumnCount() { return columnNames.length; }

    @Override
    public String getColumnName(int column) { return columnNames[column]; }

    @Override
    public Object getValueAt(int row, int col) {
        patient p = patients.get(row);
        switch (col) {
            case 0: return p.getID();
            case 1: return p.getName();
            case 2: return p.getAddress();
            case 3: return p.getDoctor();
            case 4: return p.getInsurance();
            case 5: return p.getDayAttending();

            default: return null;
        }
    }

}
