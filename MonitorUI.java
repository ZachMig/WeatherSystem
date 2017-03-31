//******************************************************************************
//
// File:    MonitorUI.java
// Package: ---
// Unit:    Class MonitorUI
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

import java.text.DecimalFormat;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Class MonitorUI provides the monitor user interface for the Weather System.
 *
 * @author  Alan Kaminsky
 * @version 11-Apr-2016
 */
public class MonitorUI
	{

// Hidden data members.

	private static final int GAP = 10;

	private JFrame frame;
	private static JTextField avgTemp;
	private static DecimalFormat df;

// Hidden constructors.

	/**
	 * Construct a new monitor UI.
	 */
	private MonitorUI()
		{
		df = new DecimalFormat("0.00000");
		
		frame = new JFrame ("Monitor");
		JPanel panel = new JPanel();
		panel.setLayout (new BoxLayout (panel, BoxLayout.X_AXIS));
		frame.add (panel);
		panel.setBorder (BorderFactory.createEmptyBorder (GAP, GAP, GAP, GAP));

		panel.add (new JLabel ("Average temperature:"));
		panel.add (Box.createHorizontalStrut (GAP));

		avgTemp = new JTextField (10);
		avgTemp.setEditable (false);
		panel.add (avgTemp);
		avgTemp.setText(df.format(0));
		frame.pack();
		frame.setVisible (true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}

// Exported operations.

	/**
	 * An object holding a reference to a monitor UI.
	 */
	private static class UIRef
		{
		public MonitorUI ui;
		}

	/**
	 * Construct a new monitor UI.
	 */
	public static MonitorUI create()
		{
		final UIRef ref = new UIRef();
		onSwingThreadDo (new Runnable()
			{
			public void run()
				{
				ref.ui = new MonitorUI();
				}
			});
		return ref.ui;
		}
	
	/**
	 * Updates the panel with the new average temperature
	 * @param temp
	 * 			The new temperature
	 */
	public void updateAverage(final double temp) {
		onSwingThreadDo(new Runnable() {
			public void run() {
				avgTemp.setText(df.format(temp));
			}
		});
	}
	
// Hidden operations.

	/**
	 * Execute the given runnable object on the Swing thread.
	 */
	private static void onSwingThreadDo
		(Runnable task)
		{
		try
			{
			SwingUtilities.invokeAndWait (task);
			}
		catch (Throwable exc)
			{
			exc.printStackTrace (System.err);
			System.exit (1);
			}
		}

	}