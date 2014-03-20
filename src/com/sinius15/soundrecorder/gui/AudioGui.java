package com.sinius15.soundrecorder.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

import com.sinius15.soundrecorder.Sound;
import com.sinius15.soundrecorder.SoundRecorder;

public class AudioGui extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private SoundRecorder myAudioSystem;

	private JPanel checkPanel;
	private JTextField pathField;
	private JButton btnBrowse;
	private JPanel panel;
	private JTextField nameField;
	private JLabel lblName;
	private JButton btnStartRecording;
	
	public AudioGui(SoundRecorder audioTest) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e2) {
			e2.printStackTrace();
		}
		this.myAudioSystem = audioTest;
		setTitle("AudioGui");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 410, 322);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JLabel lblRecorders = new JLabel("Recorders:");
		contentPane.add(lblRecorders, BorderLayout.NORTH);
		
		checkPanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) checkPanel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		contentPane.add(checkPanel);
		
		panel = new JPanel();
		panel.setPreferredSize(new Dimension(360, 74));
		contentPane.add(panel, BorderLayout.SOUTH);
		panel.setLayout(null);
		
		btnStartRecording = new JButton("Start Recording");
		btnStartRecording.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(btnStartRecording.getText().startsWith("Start")){
					ArrayList<String> out = new ArrayList<>();
					for(Component c : checkPanel.getComponents()){
						if(c instanceof JCheckBox){
							JCheckBox box = (JCheckBox) c;
							if(box.isSelected())
								out.add(box.getName());
						}
					}
					myAudioSystem.startRecording(out, pathField.getText(), nameField.getText());
					btnStartRecording.setText("Stop Recording");
				}else{
					myAudioSystem.stopRecording();
					btnStartRecording.setText("Start Recording");
				}
				
			}
		});
		btnStartRecording.setBounds(0, 46, 380, 26);
		panel.add(btnStartRecording);
		
		pathField = new JTextField(myAudioSystem.config.getString("defaultSaveFolder"));
		pathField.setBounds(77, 3, 233, 21);
		panel.add(pathField);
		pathField.setColumns(10);
		
		btnBrowse = new JButton("browse");
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser filechooser = new JFileChooser();
				filechooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				filechooser.setAcceptAllFileFilterUsed(false);
				filechooser.setDialogType(JFileChooser.OPEN_DIALOG);
				int returnErrorNR = filechooser.showSaveDialog(null);
				if(returnErrorNR != 0)
					return;
				String path = filechooser.getSelectedFile().getAbsolutePath();
				pathField.setText(path);
			}
		});
		btnBrowse.setBounds(311, 2, 69, 23);
		panel.add(btnBrowse);
		
		JLabel lblSaveFolder = new JLabel("save folder");
		lblSaveFolder.setBounds(0, 5, 70, 16);
		panel.add(lblSaveFolder);
		
		nameField = new JTextField(myAudioSystem.config.getString("defaultRecordName"));
		nameField.setBounds(77, 25, 233, 21);
		panel.add(nameField);
		nameField.setColumns(10);
		
		lblName = new JLabel("name");
		lblName.setBounds(0, 27, 55, 16);
		panel.add(lblName);
	}
	
	public void showErrorMessage(String message){
		JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	public void updateChecks(){
		checkPanel.removeAll();
		for(Sound s : myAudioSystem.sounds){
			JCheckBox box = new JCheckBox(s.mixerInfo.getName());
			box.setName(s.mixerInfo.getName());
			box.setPreferredSize(new Dimension(350, 20));
			checkPanel.add(box);
		}
	}
}
