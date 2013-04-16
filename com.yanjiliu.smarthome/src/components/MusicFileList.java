package components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeMap;

public class MusicFileList {
	
	private ArrayList<MusicFile> musicFileList;
	private MusicFile musicFile;
	
	/**
	 * Constructor. initialize the musicFileList
	 */
	public MusicFileList() {
		this.musicFileList = new ArrayList<MusicFile>();
	}
	
	/**
	 * This method adds an instance of MusicFile to the list. duplicates is allowed
	 * @param fileName
	 * @param title
	 * @param disc
	 * @param track
	 */
	public void addFile(String fileName, String title, String disc, int track) {
		musicFileList.add(new MusicFile(fileName, title, disc, track));
	}
	
	/**
	 * This method adds an instance of MusicFile to the list. duplicates is allowed. Here track is String not int
	 * @param fileName
	 * @param title
	 * @param disc
	 * @param track
	 */
	public void addFile(String fileName, String title, String disc, String track) {
		musicFileList.add(new MusicFile(fileName, title, disc, track));
	}
	
	/**
	 * Return the title given the fileName of a music file in the list
	 * @param fileName
	 * @return
	 */
	public String getTitle(String fileName){
		Iterator<MusicFile> iterator = musicFileList.iterator();
		while (iterator.hasNext()) {
			musicFile = iterator.next();
			if(musicFile.getFileName() == fileName) {
				return musicFile.getTitle();
			}
		}
		return null;
	}
	
	/**
	 * Return the disc given the fileName of a music list
	 * @param fileName
	 * @return
	 */
	public String getDisc(String fileName){
		Iterator<MusicFile> iterator = musicFileList.iterator();
		while (iterator.hasNext()) {
			musicFile = iterator.next();
			if(musicFile.getFileName() == fileName) {
				return musicFile.getDisc();
			}
		}
		return null;
	}
	
	/**
	 * Given the disc name, return a list of titles in the order of track number
	 * @param fileName
	 * @return
	 */
	public String[] getTracks(String disc){
		// I use tree map to store a tuple (track number, title name). Treemap is hashmaps but sorted by its keys
		TreeMap<Integer, String> trackTitleTuple = new TreeMap<Integer, String>();
		Iterator<MusicFile> iterator = musicFileList.iterator();
		// iterate through the list and search by disc name, put (track, title) tuples into tree maps
		while (iterator.hasNext()) {
			musicFile = iterator.next();
			if (musicFile.getDisc() == disc) {
				trackTitleTuple.put(musicFile.getTrack(), musicFile.getTitle());
			}
		}
		// return the values of tree maps in the order of sorted key, which is the track number
		return (String[]) trackTitleTuple.values().toArray();
	}
	
	/**
	 * Get a list of file names stored alphabetically
	 * @return
	 */
	public String[] getFiles() {
		// use array list, because size of array is unknown
		ArrayList<String> listFiles = new ArrayList<String>();
		
		// iterate through list and add file names to array list
		Iterator<MusicFile> iterator = musicFileList.iterator();
		while (iterator.hasNext()) {
			musicFile = iterator.next();
			listFiles.add(musicFile.getFileName());
		}
		
		// cast array list to string[]
		String[] fileNames = new String[listFiles.size()];
		for (int i = 0; i < listFiles.size(); i++){
			fileNames[i] = listFiles.get(i);
		}
		
		// sort the names
		Arrays.sort(fileNames);
		
		return fileNames;
	}
}
