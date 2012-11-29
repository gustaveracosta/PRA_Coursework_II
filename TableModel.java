package uk.kcl.inf._4ccspra.coursework2;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

/**
 * Creates a table model for JTable.
 * The header title, amount of rows and columns are determined by the values captured by the constructor method.
 * 
 * @author Gustavo Costa
 */
public class TableModel extends AbstractTableModel {
	
	private Map data = new HashMap();
	private int lengthRow, lengthCol;
	private String[] headerArray;
	
	/**
	 * Constructor method.
	 * Initialise the headerArray, lengthRow and lenghCol fields accordingly to values received in the parameters.
	 * @param headerArray header of table (columns' name).
	 * @param lengthRow number of rows in the table
	 * @param lengthCol number of columns in the table
	 */
	public TableModel(String[] headerArray, int lengthRow, int lengthCol){
		this.headerArray = headerArray;
		this.lengthRow = lengthRow;
		this.lengthCol = lengthCol;	
	}
	
	/** Replaces default header for headerArray contents. */
	@Override
	public String getColumnName(int col){
		return headerArray[col];
	}
	
	/** Returns the number of columns. */
	@Override
	public int getColumnCount() {
		return lengthCol;
	}
	
	/** Returns the number of rows */
	@Override
	public int getRowCount() {
		return lengthRow;
	}
	
	/** Returns a Dimension object of rows and columns */
	@Override
	public Object getValueAt(int row, int col) {
		return data.get(new Dimension(row, col));
	}
	
	/** Places an object in the table at a particular coordinate given by the dimension object. */
	@Override
	public void setValueAt(Object data, int row, int col){
		Dimension coord = new Dimension(row, col);
		this.data.put(coord, data);
		fireTableCellUpdated(row, col);
	}

}
