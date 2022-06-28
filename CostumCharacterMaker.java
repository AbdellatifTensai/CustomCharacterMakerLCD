
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import jssc.SerialPort;
import jssc.SerialPortException;

class CostumCharacterMaker{

    JFrame frame;
    JPanel mainPanel;
    JPanel topPanel;
    JButton saveButton;	
    JButton clearButton;
    GridLayout panelsGrid;
    GridLayout checkBoxesGrid;
    ArrayList<JPanel> panels;
    ArrayList<JCheckBox> checkBoxes;
    ArrayList<ArrayList> checkBoxesLists;
    int[] pattern = new int[40];
    int[] slaves;
    int[] patternIndex;
    String output;
    static String input;
    static SerialPort serialPort;
    
    static Thread t = new Thread() {
    	@Override public void run(){
    		while(serialPort.isOpened()){
    			try {
    				input = serialPort.readString();
    				System.out.println(input == null? "" : input);
    				Thread.sleep(200);
    			}catch(SerialPortException e){e.printStackTrace();} 
    			 catch (InterruptedException e){e.printStackTrace();}
    		}
    	}
    };
    
    public static void main(String[] args){
        CostumCharacterMaker ccm = new CostumCharacterMaker();
        ccm.go();
        ccm.initialize();
    	t.start();
    }
   private void initialize(){
	   serialPort = new SerialPort("COM9");
	   try{
		   serialPort.openPort();
	       serialPort.setParams(SerialPort. BAUDRATE_115200, 
	    		   SerialPort.DATABITS_8, 
	    		   SerialPort.STOPBITS_1, 
	    		   SerialPort.PARITY_NONE);
	
	   }catch(Exception e){e.printStackTrace();}
}
    private void go(){
        frame = new JFrame("Costum Character Maker");
        panelsGrid = new GridLayout(2,4);
        checkBoxesGrid = new GridLayout(8,5);
        mainPanel = new JPanel(panelsGrid);
        topPanel = new JPanel(new FlowLayout());
        
        saveButton = new JButton("Send");
        topPanel.add(saveButton);
        saveButton.addActionListener(new BSaveActionListener());
        
        clearButton = new JButton("Clear");
        topPanel.add(clearButton);
        clearButton.addActionListener(new BClearActionListener());

        panels = new ArrayList<JPanel>();
        checkBoxesLists = new ArrayList<ArrayList>();
        
        for(int x=0;x<8;x++){
            JPanel panel = new JPanel(checkBoxesGrid);
            panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
            ArrayList<JCheckBox> checkBoxes = new ArrayList<JCheckBox>();
            for(int y=0;y<40;y++){
                JCheckBox c = new JCheckBox();
                c.setSelected(false);
                checkBoxes.add(c);
                panel.add(c);
            }
            panels.add(panel);
            mainPanel.add(panel);
            checkBoxesLists.add(checkBoxes);
        }

        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800,600);
        frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
        frame.getContentPane().add(BorderLayout.NORTH, topPanel);

        
        
    }

    private class BSaveActionListener implements ActionListener{
    	public void actionPerformed(ActionEvent ev){
    		int index =0;
    		if(serialPort.isOpened()){
    			for(ArrayList<JCheckBox> array : checkBoxesLists){
					output = "<";
	                for(JCheckBox c : array){
	                    pattern[array.indexOf(c)] = c.isSelected()? 1: 0;
	                    //System.out.print(pattern[array.indexOf(c)]);
	                    output += pattern[array.indexOf(c)];
	                    //System.out.print((array.indexOf(c)+1) % 5 == 0 ? ">"+ index :"");
	                    output += (array.indexOf(c)+1) % 5 == 0 ? ">"+ index : "";

	                    index = (array.indexOf(c)+1) % 5 == 0 ? index + 1: index + 0;
	                    //System.out.print((array.indexOf(c)-4) % 5 == 0 && (array.indexOf(c)-4 != 35) ? "<":"");
	                    output += (array.indexOf(c)-4) % 5 == 0 && (array.indexOf(c)-4 != 35) ? "<":"";
	                    
	                    if((array.indexOf(c)+1)% 5 == 0){ 
//	                    	System.out.println(output);
	                    	try {
	                    		serialPort.writeString(output);
	                    		Thread.sleep(50);
	                    	}catch(SerialPortException e){e.printStackTrace();} 
	                    	catch (InterruptedException e){e.printStackTrace();}
	                    	output = "";
	                    }
	                }
//	            System.out.println("");
                }
            }
            
        }
    }
    private class BClearActionListener implements ActionListener{
    	public void actionPerformed(ActionEvent ev) {
    		for(ArrayList<JCheckBox> array: checkBoxesLists) {
    			for(JCheckBox c:array) {
    				c.setSelected(false);
    			}
    		}
    	}
    }
}

