//******************************************************************************
//
// File:    SensorUI.java
// Package: ---
// Unit:    Class SensorUI
//
// This Java source file is copyright (C) 2016 by Alan Kaminsky. All rights
// reserved. For further information, contact the author, Alan Kaminsky, at
// ark@cs.rit.edu.
//
// This Java source file is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by the Free
// Software Foundation; either version 3 of the License, or (at your option) any
// later version.
//
// This Java source file is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
// details.
//
// You may obtain a copy of the GNU General Public License on the World Wide Web
// at http://www.gnu.org/licenses/gpl.html.
//
//******************************************************************************

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Class SensorUI provides the sensor user interface for the Weather System.
 * 
 * @author Alan Kaminsky
 * @version 11-Apr-2016
 */
public class SensorUI {

	// Hidden data members.

	private static final int GAP = 10;
	private static final int WIDTH = 100;

	private JFrame frame;
	private JSpinner temperature;
	
	private ViewListener viewListener;
	
	private long id;
	
	// Hidden constructors.

	/**
	 * Construct a new sensor UI.
	 */
	private SensorUI() {
		id = System.currentTimeMillis();
		
		frame = new JFrame("Sensor");
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		frame.add(panel);
		panel.setBorder(BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP));

		panel.add(new JLabel("Temperature:"));
		panel.add(Box.createHorizontalStrut(GAP));

		temperature = new JSpinner();
		Dimension d = temperature.getPreferredSize();
		d.width = WIDTH;
		temperature.setPreferredSize(d);
		temperature.setMinimumSize(d);
		temperature.setMaximumSize(d);
		temperature.getEditor().setEnabled(false);
		panel.add(temperature);

		// Report temperature four times a second. 
		Timer timer = new Timer(250, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				report();
			}
		});
		timer.start();
		
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	// Exported operations.

	/**
	 * An object holding a reference to a sensor UI.
	 */
	private static class UIRef {
		public SensorUI ui;
	}

	/**
	 * Construct a new sensor UI.
	 */
	public static SensorUI create() {
		final UIRef ref = new UIRef();
		onSwingThreadDo(new Runnable() {
			public void run() {
				ref.ui = new SensorUI();
			}
		});
		return ref.ui;
	}

	/** 
	* Set the view listener
	* @param viewListener 
	* 			the ViewListener
	*/
	public void setViewListener(final ViewListener viewListener) {
		onSwingThreadDo(new Runnable() {
			public void run() {
				SensorUI.this.viewListener = viewListener;
			}
		});
	}

	// Hidden operations.
	
	/**
	 * Reports the temperature of this sensor to the viewListener 
	 */
	private void report() {
		try {
			if (viewListener != null) {
				int temp = (Integer) temperature.getValue();
				viewListener.report(null, System.currentTimeMillis(), temp);
			}
		} catch (IOException exc) {
			exc.printStackTrace(System.err);
			System.exit(1);
		}
	}
		

	/**
	 * Execute the given runnable object on the Swing thread.
	 */
	private static void onSwingThreadDo(Runnable task) {
		try {
			SwingUtilities.invokeAndWait(task);
		} catch (Throwable exc) {
			exc.printStackTrace(System.err);
			System.exit(1);
		}
	}

}