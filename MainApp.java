package uk.kcl.inf._4ccspra.coursework2;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
/**
 * Creates a window that queries the Yahoo historical search website. 
 * The user can enter a stock symbol in uppercase letters, numbers and period.
 * A begin and end date can be chosen from JComboBoxes.
 * The default begin date is 1/1/2000 and end date is current date.
 * Also, intervals can be selected accordingly to users choice (daily, weekly or monthly).
 * The results can be displayed in chronological or reverse chronological order.
 * Reverse chronological order is the default order.
 * 
 * @author Gustavo Costa
 */
public class MainApp extends JFrame implements ActionListener{
	
	private static Pattern pattern = Pattern.compile("[A-Z\\d\\.]{0,8}");
	
	private JTextField jtf;
	private JComboBox jcbBD, jcbBM, jcbBY, jcbED, jcbEM, jcbEY, jcbI;
	private String[] arrayDay, arrayMonth, arrayYear, arrayInterval;
	private int bD, bM, bY, eD, eM, eY, inter;	
	private String[] currentDate;
	private boolean reverseOrder = false;
	private boolean leap;
	private JFrame[] jfTable = new JFrame[5];
	private int counter = 0;

	/**
	 * Constructor method.
	 * Set title to JFrame, close operation and manager layout.
	 * Get dateFormat and add widgets to frame.
	 */
	public MainApp(){
		super("Stock Market Historical Search");
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		DateFormat dateFormat = new SimpleDateFormat("dd,MM,yyyy");
		Date date = new Date();
		currentDate = dateFormat.format(date).split(",");

		setLayout(new GridBagLayout());

		widgets();

		pack();
		setLocationRelativeTo(null);
	}

	/** Adds widgets to the window in GridBagLayout. */
	private void widgets() {
		JLabel jl;

		arrayDay = getDay();
		arrayMonth = getMonth();
		arrayYear = getYear();
		arrayInterval = getInterval();

		GridBagConstraints gbc = new GridBagConstraints();
		// If weight is left as default the widgets do not expand as window is resized.
		gbc.weightx = 0.5;
		gbc.weighty = 0.5;
		gbc.insets = new Insets(5,5,5,5); // padding	

		// labels' orientations
		gbc.anchor = GridBagConstraints.EAST;

		gbc.gridx = 0; gbc.gridy = 0; // x-row, y-column
		jl = new JLabel("Stock Symbol:"); 
		add(jl, gbc);

		gbc.gridx = 0; gbc.gridy = 1;
		jl = new JLabel("Begin:");
		add(jl, gbc);

		gbc.gridx = 0; gbc.gridy = 2;
		jl = new JLabel("End:");
		add(jl, gbc);

		gbc.gridx = 0; gbc.gridy = 3;
		jl = new JLabel("Interval:");
		add(jl, gbc);

		// all other components to horizontal orientation
		gbc.fill = GridBagConstraints.HORIZONTAL;

		gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 2; // JTextField, 2 cells wide
		jtf = new JTextField(10);
		add(jtf, gbc);

		gbc.gridwidth = 1; // reset to default

		gbc.gridx = 3; gbc.gridy = 1;
		jcbBY = new JComboBox(arrayYear);
		jcbBY.setSelectedIndex(30); // default date
		jcbBY.addActionListener(this);
		add(jcbBY, gbc);

		gbc.gridx = 2; gbc.gridy = 1;
		jcbBM = new JComboBox(arrayMonth);
		jcbBM.addActionListener(this);
		add(jcbBM, gbc);

		gbc.gridx = 1; gbc.gridy = 1;
		jcbBD = new JComboBox(arrayDay);
		jcbBD.addActionListener(this);
		add(jcbBD, gbc);

		gbc.gridx = 3; gbc.gridy = 2;
		jcbEY = new JComboBox(arrayYear);
		jcbEY.setSelectedIndex(Integer.parseInt(currentDate[2]) - 1970);  // default date
		jcbEY.addActionListener(this);
		add(jcbEY, gbc);

		gbc.gridx = 2; gbc.gridy = 2;
		jcbEM = new JComboBox(arrayMonth);
		jcbEM.setSelectedIndex(Integer.parseInt(currentDate[1])-1);  // default date
		jcbEM.addActionListener(this);
		add(jcbEM, gbc);

		gbc.gridx = 1; gbc.gridy =  2;
		jcbED = new JComboBox(arrayDay);
		leap = isLeapYear(arrayYear[jcbEY.getSelectedIndex()]);
		int indexMonth = jcbEM.getSelectedIndex();
		fixDay(leap, indexMonth, jcbED);
		jcbED.setSelectedIndex(Integer.parseInt(currentDate[0])-1); // default date
		jcbED.addActionListener(this);
		add(jcbED, gbc);

		gbc.gridx = 1; gbc.gridy = 3;
		jcbI = new JComboBox(arrayInterval);
		jcbI.setSelectedIndex(2);  // default date
		add(jcbI, gbc);

		gbc.gridx = 1; gbc.gridy = 4;
		JButton jbLookUp = new JButton("LookUp"); 
		jbLookUp.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				bD = jcbBD.getSelectedIndex(); bM = jcbBM.getSelectedIndex(); bY = jcbBY.getSelectedIndex();
				eD = jcbED.getSelectedIndex(); eM = jcbEM.getSelectedIndex(); eY = jcbEY.getSelectedIndex();	
				inter = jcbI.getSelectedIndex();

				if(!isEmpty() && isValidText() && isValidDate())
					queryYahoo();				
			}
		});
		add(jbLookUp, gbc);

		gbc.gridx = 2; gbc.gridy = 4; gbc.gridwidth = 2;
		final JRadioButton jrb = new JRadioButton("Chronological Order");
		jrb.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				if(jrb.isSelected())
					reverseOrder = true;
				else
					reverseOrder = false;
			}
		});
		add(jrb, gbc);	
	}

	/**
	 * Calls readURL method from URLReader class. If null is returned a error message is display.
	 * If a not null value is returned, a window is created by using an object of the Table class and stored in a JFrame array.
	 * A String title is generated for every object of Table class and it is associated with the object. 
	 * If any window of the Table class is closed, then the object is deleted from the JFrame array and the association is removed.
	 * If a query generates the same title of an existing object, then the associated object (i.e. the window) with identical 
	 * title is brought to the front of any other window already being displayed. 
	 */
	private void queryYahoo(){

		try{
			String url = URLReader.readURL("http://ichart.yahoo.com/table.csv?s=" +jtf.getText()+ 
					"&a=" +bM+ "&b=" +(bD+1)+ "&c=" +arrayYear[bY]+ "&d=" +eM+ "&e=" +(eD+1)+ "&f=" +arrayYear[eY]+ 
					"&g=" +arrayInterval[inter].substring(0,1));

			String tableTitle = getTableTitle();

			if(counter == jfTable.length)
				jfTable = doubleArraySize(jfTable);

			boolean query = true;

			for(int i=0; i < counter; i++){
				if(tableTitle.equals(jfTable[i].getTitle())){
					jfTable[i].toFront(); // brings window to front
					query = false;
				}
			}

			if(query){
				jfTable[counter] = new Table(url, tableTitle, reverseOrder);
				jfTable[counter++].addWindowListener(new WindowAdapter(){
					@Override
					public void windowClosing(WindowEvent e) {
						for(int i=0; i < counter; i++){
							if(e.getSource() == jfTable[i] && i < counter - 1)
								jfTable[i] = jfTable[i+1];
						}
						jfTable[counter-1] = null; // facilitate java garbage collection
						counter--;
					}
				});
			}

		}catch(Exception e){
			JOptionPane.showMessageDialog(null, "Stock symbol not found!", "ERROR", JOptionPane.ERROR_MESSAGE);
		}

	}

	/**
	 * Copy elements and double the size of a JFrame array given in the parameters.
	 * @param jf JFrame array.
	 * @return JFrame array reference given JFrame array.
	 */
	private JFrame[] doubleArraySize(JFrame[] jf){
		return Arrays.copyOf(jf, jf.length * 2);
	}

	/**
	 * Implemented method of ActionListener for JComboBoxes.
	 * Calls method fixDay to adjust the number of days in JComboBox.
	 * If the action comes from a JComboBox of the begin date then the number of days in the JComboBox begin day is adjusted, 
	 * end day otherwise.
	 */
	@Override 
	public void actionPerformed(ActionEvent e) {
		JComboBox jcb = (JComboBox)e.getSource();
		int indexMonth;
		if(jcb == jcbBY || jcb == jcbBM || jcb == jcbBD){
			leap = isLeapYear(arrayYear[jcbBY.getSelectedIndex()]);
			indexMonth = jcbBM.getSelectedIndex();
			fixDay(leap, indexMonth, jcbBD);			
		}else{
			leap = isLeapYear(arrayYear[jcbEY.getSelectedIndex()]);
			indexMonth = jcbEM.getSelectedIndex();
			fixDay(leap, indexMonth, jcbED);			
		}
	}

	/**
	 * Computes if a given year is a leap year.
	 * @param yearString year to be checked.
	 * @return true if given yearString year is a leap year, false otherwise.
	 */
	private boolean isLeapYear(String yearString){
		int year = Integer.parseInt(yearString);

		if(((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0))
			return true;
		else
			return false;
	}

	/**
	 * Adjust the number of days in JComboBox accordingly to the western calendar.
	 * (e.g. if month given is February and the JComboBox contains 31 days and 
	 * it is not a leap year then we need to display only 28 days.
	 * Then, the numbers: '31, 30 and 29' must be removed from the JComboBox. Therefore, if the JComboBox
	 * is displaying either one of the days to be removed (31, 30 or 29) 
	 * then we set the JComboBox to display the last number available which will be 28).
	 * Call appropriate method to deal with each month accordingly to the amount of days required.
	 * @param leap true if is leap year, false otherwise.
	 * @param month month index of JComboBox.
	 * @param jcb JComboBox to be fixed.
	 */
	private void fixDay(boolean leap, int month, JComboBox jcb){

		int numberOfDays = jcb.getItemCount();
		int day = jcb.getSelectedIndex() + 1; // add 1 to day for easier comparison

		switch(++month){
		case 2:
			if(leap)
				twentyNineDays(numberOfDays, day, jcb);
			else
				twentyEightDays(numberOfDays, day, jcb);
			break;
		case 4:
		case 6:
		case 9:
		case 11:
			thirtyDays(numberOfDays, day, jcb);
			break;
		default:
			thirtyOneDays(numberOfDays, day, jcb);
			break;
		}
	}
	
	/**
	 * Adjust JComboBox by adding or removing items(the numbers representing days) from the JComboBox.
	 * If a selection index is being displayed we change index to latest available index before removing items.
	 * The current amount of days being displayed is given by the parameters <code>int numberOfDays</code>.
	 * @param numberOfDays current amount of days in the JComboBox
	 * @param day selection index of JComboBox. One unit is added to day to facilitate comparison.
	 * @param jcb JComboBox to be adjusted
	 */
	private void thirtyOneDays(int numberOfDays, int day, JComboBox jcb){
		if(numberOfDays == 28){
			jcb.addItem("29"); 
			jcb.addItem("30"); 
			jcb.addItem("31");
		}else if(numberOfDays == 29){
			jcb.addItem("30"); 
			jcb.addItem("31");
		}else if(numberOfDays == 30){
			jcb.addItem("31");
		}
	}
	
	/**
	 * Adjust JComboBox by adding or removing items(the numbers representing days) from the JComboBox.
	 * If a selection index is being displayed we change index to latest available index before removing items.
	 * The current amount of days being displayed is given by the parameters <code>int numberOfDays</code>.
	 * @param numberOfDays current amount of days in the JComboBox
	 * @param day selection index of JComboBox. One unit is added to day to facilitate comparison.
	 * @param jcb JComboBox to be adjusted
	 */
	private void thirtyDays(int numberOfDays, int day, JComboBox jcb){
		if(numberOfDays == 28){
			jcb.addItem("29"); 
			jcb.addItem("30");
		}else if(numberOfDays == 29){
			jcb.addItem("30");
		}else if(numberOfDays == 31){
			if(day == 31) 
				jcb.setSelectedIndex(29);
			jcb.removeItem("31");
		}
	}
	
	/**
	 * Adjust JComboBox by adding or removing items(the numbers representing days) from the JComboBox.
	 * If a selection index is being displayed we change index to latest available index before removing items.
	 * The current amount of days being displayed is given by the parameters <code>int numberOfDays</code>.
	 * @param numberOfDays current amount of days in the JComboBox
	 * @param day selection index of JComboBox. One unit is added to day to facilitate comparison.
	 * @param jcb JComboBox to be adjusted
	 */
	private void twentyNineDays(int numberOfDays, int day, JComboBox jcb){
		if(numberOfDays == 28){
			jcb.addItem("29");
		}else if(numberOfDays == 30){
			if(day == 30) 
				jcb.setSelectedIndex(28);
			jcb.removeItem("30");
		}else if(numberOfDays == 31){
			if(day == 30 || day == 31) 
				jcb.setSelectedIndex(28); 
			jcb.removeItem("30"); 
			jcb.removeItem("31");					
		}
	}

	/**
	 * Adjust JComboBox by adding or removing items(the numbers representing days) from the JComboBox.
	 * If a selection index is being displayed we change index to latest available index before removing items.
	 * The current amount of days being displayed is given by the parameters <code>int numberOfDays</code>.
	 * @param numberOfDays current amount of days in the JComboBox
	 * @param day selection index of JComboBox. One unit is added to day to facilitate comparison.
	 * @param jcb JComboBox to be adjusted
	 */
	private void twentyEightDays(int numberOfDays, int day, JComboBox jcb){
		if(numberOfDays == 29){
			if(day == 29) 
				jcb.setSelectedIndex(27);
			jcb.removeItem("29");
		}else if(numberOfDays == 30){
			if(day == 29 || day == 30) 
				jcb.setSelectedIndex(27);
			jcb.removeItem("29"); 
			jcb.removeItem("30");
		}else if(numberOfDays == 31){
			if(day == 29 || day == 30 || day == 31) 
				jcb.setSelectedIndex(27);
			jcb.removeItem("29"); 
			jcb.removeItem("30"); 
			jcb.removeItem("31");
		}
	}

	/**
	 * Generates a String array with 31 numbers from 1 to 31. 
	 * @return array String array of days.
	 */
	private String[] getDay(){
		String array[] = new String[31];
		int day = 1;
		for(int i=0; i<array.length; i++)
			array[i] = "" + (day++);
		return array;
	}

	/**
	 * Generates a String array with months of the year.
	 * @return array String array of months.
	 */
	private String[] getMonth(){
		String array[] = {
				"January",
				"February",
				"March",
				"April",
				"May",
				"June",
				"July",
				"August",
				"Setember",
				"October",
				"November",
				"December"
		};
		return array;
	}

	/**
	 * Generates a String array containing years from 1970 to current year.
	 * @return array String array of years.
	 */
	private String[] getYear(){
		String array[] = new String[Integer.parseInt(currentDate[2]) - 1969];
		int year = 1970;
		for(int i=0; i<array.length; i++)
			array[i] = "" + (year++);
		return array;
	}

	/**
	 * Generates a String array containing period intervals (daily, weekly and monthly).
	 * @return array String array of intervals.
	 */
	private String[] getInterval(){
		String array[] = {
				"daily",
				"weekly",
				"monthly"
		};
		return array;
	}

	/**
	 * Checks if the JTextField text is empty.
	 * @return true if JTextField text is empty, false otherwise.
	 */
	private boolean isEmpty(){
		return jtf.getText().equals("");
	}

	/**
	 * Checks if only if JTextField text is a valid Format.
	 * Valid format:
	 * 1) do not contain more than 8 characters, numbers or symbols in total;
	 * 2) characters must be from A to Z in capital letters or numbers from 0 to 9 or a period(.).
	 * If not in valid format a warning pop up window is displayed for feedback.
	 * @return true if JTextField text is in valid format, false otherwise.
	 */
	private boolean isValidText(){
		Matcher matcher = pattern.matcher(jtf.getText());

		if(!matcher.matches()){
			JOptionPane.showMessageDialog(null, "'" + jtf.getText() + 
					"' must be maximum 8 characters in length (uppercase, digits and period)!", "WARNING!", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		return true;
	}

	/**
	 * Checks if the begin date is not greater than the end date from JComboBoxes.
	 * A warning pop up window is displayed if begin date is greater than end date.
	 * @return true if begin date is smaller or equals to end date, false otherwise.
	 */
	private boolean isValidDate(){	
		if(bY > eY || (bY == eY && bM > eM) || (bY == eY && bM == eM && bD > eD)){
			JOptionPane.showMessageDialog(null, "Begin year: " +arrayDay[bD]+ "/" +arrayMonth[bM]+ "/" +arrayYear[bY] + 
					" must be smaller or equals to end year: " +
					arrayDay[eD]+ "/" +arrayMonth[eM]+ "/" +arrayYear[eY] , "WARNING!", JOptionPane.WARNING_MESSAGE);
			return false;
		}else
			return true;
	}

	/**
	 * Format the table title. Table title contains the stock symbol, the date from and to, the interval and
	 * the chronological order.
	 * @return the formated table title.
	 */
	private String getTableTitle(){
		if(reverseOrder)
			return jtf.getText().toUpperCase() + ": " +arrayYear[eY]+ "-" +(eM+1)+ "-" +(eD+1)+ " to " + 
			arrayYear[bY]+ "-" +(bM+1)+ "-" +(bD+1)+ " (" +arrayInterval[inter]+ ", chronological)";
		else
			return jtf.getText().toUpperCase() + ": " +arrayYear[bY]+ "-" +(bM+1)+ "-" +(bD+1)+ " to " +
			arrayYear[eY]+ "-" +(eM+1)+ "-" +(eD+1)+ " (" +arrayInterval[inter]+ ", reverse chronological)";
	}

	/**
	 * Main method. Program starts from here. 
	 * Calls this class constructor and set JFrame to be visible.
	 * @param args 
	 */
	public static void main(String[] args){
		new MainApp().setVisible(true);
	}

}
