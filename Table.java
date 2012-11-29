package uk.kcl.inf._4ccspra.coursework2;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * This class a window in BorderLayout manager layout.
 * A JTable is placed at the Centre. At the South are placed 2 labels (Average and DrawnDown) containing their values.
 * The <code>String url</code> captured by the constructor is divided in lines and then into columns. 
 * The last column of each line corresponds to the adjusted price.
 * The adjusted price are coloured in green, red or black.
 * The data can be displayed in chronological or reverse chronological order.
 * 
 * @author Gustavo Costa
 */
public class Table extends JFrame {
	private String tableTitle;
	private ArrayList<String[]> urlDataLine;
	private double[] adjPrice;
	private JTable jt;
	
	/**
	 * Constructor method.
	 * Set the JFrame title and make it visible.
	 * Initialises the table title and reverseOrder instance fields.
	 * The <code>String url<code> is sent to be formatted.
	 * @param url url data received from the Yahoo query.
	 * @param tableTitle title of the window.
	 * @param reverseOrder true if data must be displayed in reverse chronological order, chronological order if false.
	 */
	public Table(String url, String tableTitle, boolean reverseOrder){
		super(tableTitle);
		setLayout(new BorderLayout());

		this.tableTitle = tableTitle;
 
		widgets(url, reverseOrder);			
		
		setVisible(true);
		pack();
		setLocationRelativeTo(null);			
	}

	/** Accessor method.
	 * @param tableTitle title of the table. 
	 */
	public String getTitle(){
		return tableTitle;
	}

	/**
	 * Format data before populating table.
	 * Stores adjusted price as double in adjPrice local field.
	 * Calls method to format colour.
	 */
	private void formatData(){	
		adjPrice = new double[urlDataLine.size()];

		// no need to compare last element with previous as it doesn't exist
		for(int i=urlDataLine.size()-1; i >= 0; i--){
			String[] line = urlDataLine.get(i);
			adjPrice[i] = Double.parseDouble(line[6]);
			if(i != urlDataLine.size()-1)
				line[6] = setPriceColor(adjPrice[i], adjPrice[i+1]);	
			urlDataLine.set(i, line);
		}
	}

	/**
	 * Set colours in HTML format for the adjusted current price.
	 * If current adjusted price is greater than the previous adjusted price set colour to green,
	 * red if smaller or black if unchanged.
	 * @param curPrice current adjusted price
	 * @param oldPrice previous adjusted price
	 * @return curPrice current price formated in HTML given curPrice current price and oldPrice previous price
	 */
	private String setPriceColor(double curPrice, double oldPrice){
		if(curPrice > oldPrice) 
			return  "<html><font color=green>" +curPrice+ "</font></html>";
		else if(curPrice < oldPrice) 
			return  "<html><font color=red>" +curPrice+ "</font></html>";
		else
			return "" + curPrice;
	}

	/**
	 * Computes the average of the adjusted prices.
	 * The average is rounded off up to two decimal places.
	 * @param array array containing adjusted prices.
	 * @return String average of adjusted prices.
	 */
	private String calculateAverage(double[] array){
		double total = 0;
		for(int i=0; i < array.length; i++)
			total += array[i];

		total /= array.length;
		total *= 100;
		int t = (int)Math.round(total);
		total = (double)t / 100;

		return "" + total;
	}

	/**
	 * Computes the maximal DrawnDown of the adjusted prices.
	 * The DrawDown is rounded off up to two decimal places.
	 * @param NAV array with adjusted prices
	 * @return MDD double MDD - maximal DrawDown given NAV - adjusted prices 
	 */
	private double calculateDrawDown(double[] NAV){
		double MDD = 0;
		double DD;
		double peak = -99999;

		for(int i = NAV.length-1; i >= 0 ; i--){
			if(NAV[i] > peak)
				peak = NAV[i];
			else{
				DD = peak - NAV[i];
				if(DD > MDD)
					MDD = DD;
			}
		}		
		MDD *= 100;
		int m = (int)Math.round(MDD);
		MDD = (double)m / 100;
		
		return MDD;

	}

	/** Populate table with values in <code>ArrayList<String[]> urlDataLine</code> in reverse chronological order */
	private void fillTableReverse(){
		for(int i=0; i < urlDataLine.size(); i++){
			String[] line  = urlDataLine.get(i);
			for(int j=0; j < 7; j++){
				jt.setValueAt(line[j], i, j);
			}
		}
	}

	/** 
	 * Populate table with values in <code>ArrayList<String[]> urlDataLine</code>. in chronological order
	 * Reverse the order of the rows in the array then set the values to the table.
	 * */
	private void fillTableChron(){
		ArrayList<String[]> temp = new ArrayList<String[]>();

		for(int i=urlDataLine.size()-1; i >= 0; i--){
			temp.add(urlDataLine.get(i));
		}
		
		urlDataLine = temp;

		for(int i=0; i < urlDataLine.size(); i++){
			String[] line  = urlDataLine.get(i);
			for(int j=0; j < 7; j++){
				jt.setValueAt(line[j], i, j);
			}
		}
	}

	/**
	 * Divide the string received from Yahoo and divide it into lines.
	 * Thereafter, divide every line into columns and store this result in a <code>ArrayList<String[]></code>.
	 * Creates a table model for the table and format the adj. price column values with colours as required.
	 * Populates the table in reverse chronological order or chronological order if <code>boolean reverseOrder</code> is false.
	 * Add table to the Centre. Average and DrawDown are placed in the south.
	 * 
	 * @param url
	 * @param reverseOrder
	 */
	private void widgets(String url, boolean reverseOrder){
		String[] urlData = url.split("\n");	
		urlDataLine = new ArrayList<String[]>();

		// skips first row that contains the header title
		for(int i=1; i < urlData.length; i++){
			urlDataLine.add(urlData[i].split(","));							
		}
		if(urlDataLine.size() > 0){
			// table model (header, row, col)
			TableModel tm = new TableModel(urlData[0].split(","), urlDataLine.size(), urlDataLine.get(0).length);
			jt = new JTable(tm);

			formatData();

			if(reverseOrder){
				fillTableReverse();
			}else{
				fillTableChron();
			}

			JScrollPane jsp = new JScrollPane(jt);
			add(jsp, BorderLayout.CENTER);

			JPanel jp = new JPanel(new FlowLayout());
			JLabel jl;

			jl = new JLabel("Maximal drawdown: " + calculateDrawDown(adjPrice)+ ", ");
			jp.add(jl);

			jl = new JLabel("Adj. price average: " + calculateAverage(adjPrice));
			jp.add(jl);

			add(jp, BorderLayout.SOUTH);
		}
	}
}
