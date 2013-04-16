package components;

import java.util.ArrayList;
import java.util.Iterator;

public class MusicFileList {
	
	private ArrayList<MusicFile> musicFileList;
	private MusicFile nextRecord;
	
	public MusicFileList() {
		this.musicFileList = new ArrayList<MusicFile>();
	}
	
	public void addFile(String fileName, String title, String disc, int track) {
		musicFileList.add(new MusicFile(fileName, title, disc, track));
	}
	
	public String getTitle(String fileName){
		Iterator<MusicFile> iterator = musicFileList.iterator();
		while (iterator.hasNext()) {
			nextRecord = iterator.next();
			if(nextRecord.getFileName() == fileName) {
				return nextRecord.getTitle();
			}
		}
		return null;
	}
	
	public String getDisc(String fileName){
		Iterator<MusicFile> iterator = musicFileList.iterator();
		while (iterator.hasNext()) {
			nextRecord = iterator.next();
			if(nextRecord.getFileName() == fileName) {
				return nextRecord.getDisc();
			}
		}
		return null;
	}
	
	// FIXME: did not work yet
	public String[] getTracks(String fileName){
		Iterator<MusicFile> iterator = musicFileList.iterator();
		while (iterator.hasNext()) {
			nextRecord = iterator.next();
			
		}
		return null;
	}
	
	public String[] getFileNames() {
		ArrayList<String> listFiles = new ArrayList<String>();
		Iterator<MusicFile> iterator = musicFileList.iterator();
		while (iterator.hasNext()) {
			nextRecord = iterator.next();
			listFiles.add(nextRecord.getFileName());
		}
		
		String[] fileNames = new String[listFiles.size()];
		for (int i = 0; i < listFiles.size(); i++){
			fileNames[i] = listFiles.get(i);
		}
		return fileNames;
	}
}
