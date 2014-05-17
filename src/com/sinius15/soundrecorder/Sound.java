package com.sinius15.soundrecorder;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

public class Sound{

	public Mixer mixer = null;
	public Mixer.Info mixerInfo = null;
	public TargetDataLine targetLine = null;
	public AudioInputStream audioInputStream = null;
	
	public Thread recThread = new Thread();
	public File saveFile;
	boolean isRecording = false;
	
	public String name;
	
	public Sound(Mixer mixer, String name) throws LineUnavailableException {
		this.mixer = mixer;
		this.mixerInfo = mixer.getMixerInfo();
		
		for(Info i : mixer.getTargetLineInfo()){
			if(i.getLineClass().getName().equals("javax.sound.sampled.TargetDataLine")){
				targetLine = (TargetDataLine) mixer.getLine(i);
				break;
			}
		}
		
		if(targetLine == null)
			throw new LineUnavailableException("Could not find right line");
		if (!AudioSystem.isLineSupported(targetLine.getLineInfo()))
			throw new LineUnavailableException("Line type not supported.");
	}
	
	public void startRecording(File saveFilea) throws IOException, LineUnavailableException{
		if(isRecording)
			throw new IOException("Already recroding!");
		this.saveFile = saveFilea;
		targetLine.open();
		targetLine.start();
		audioInputStream = new AudioInputStream(targetLine);
		recThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, saveFile);
				} catch (IOException e) {
					SoundRecorder.gui.showErrorMessage("Could not start recording on line "+mixerInfo.getName()+"   :(" + System.lineSeparator()
							+ "Error message: " + e.getMessage());
					e.printStackTrace();
				}
			}
		});
		recThread.start();
		isRecording = true;
	}
	
	public void stopRecording(){
		if(!isRecording)
			return;
		targetLine.flush();
		targetLine.stop();
		targetLine.close();
		isRecording = false;
		try {
			audioInputStream.close();
		} catch (IOException e) {
			SoundRecorder.gui.showErrorMessage("Could not finish up the recording on line "+mixerInfo.getName()+"   :(" + System.lineSeparator()
					+ "Error message: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public String getName(){
		return name;
	}
	
}
