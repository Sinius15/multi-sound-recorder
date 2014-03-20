package com.sinius15.soundrecorder;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;

import com.sinius15.lib.YAMLFile;
import com.sinius15.soundrecorder.gui.AudioGui;

public class SoundRecorder {

	public ArrayList<Sound> sounds = new ArrayList<>();
	public AudioGui gui;
	public YAMLFile config = new YAMLFile(false);
	public File configFile = new File("config.yml");
	
	public final String VERSION = "beta1";
	
	public SoundRecorder(){
		Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
		
		config.Load(configFile);
		if(config.getString("version") == null){
			config = new YAMLFile(false);
			config.addString("version", VERSION);
			config.addString("defaultSaveFolder", "");
			config.addString("defaultRecordName", "");
			try {
				config.Save(configFile);
			} catch (Exception e) {
			}
		}
		
		gui = new AudioGui(this);
		
		for(Mixer.Info mixerInfo : mixerInfos){
			Mixer mixer = AudioSystem.getMixer(mixerInfo);
			if(mixer.getClass().getName().contains("DirectAudioDevice")){
				if(mixerInfo.getDescription().contains("Capture")){
					try {
						sounds.add(new Sound(mixer));
					} catch (LineUnavailableException e) {
						e.printStackTrace();
					}
				}
			}
		}
		gui.updateChecks();
		gui.setVisible(true);
	}
	
	public void startRecording(ArrayList<String> in, String savePath, String recordingName){
		String fileStart = savePath + "\\" + "Audio Recording "+recordingName + " on "+new SimpleDateFormat("yyyy-MM-dd 'at' HH.mm.ss").format(System.currentTimeMillis()) + "\\";
		File temp = new File(fileStart);
		temp.mkdirs();
		for(String s : in){
			Sound sound = getSound(s);
			if(sound == null)
				gui.showErrorMessage("Could not find mixer " + s);
			try {
				sound.startRecording(new File(fileStart + " " + s + ".wav"));
			} catch (IOException | LineUnavailableException e) {
				gui.showErrorMessage("Could not start recording on line " + sound.mixerInfo.getName());
			}
		}
	}
	
	public void stopRecording(){
		for(Sound s : sounds){
			s.stopRecording();
		}
	}
	
	public Sound getSound(String name){
		for(Sound s : sounds)
			if(s.mixerInfo.getName().equals(name))
				return s;
		return null;
	}
	
	public static void main(String[] args) {
		new SoundRecorder();
	}
	
}
