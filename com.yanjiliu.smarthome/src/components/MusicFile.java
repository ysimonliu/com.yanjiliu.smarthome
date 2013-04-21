package components;

public class MusicFile {

	private String fileName, title, disc;
	private int track;
	
	/**
	 * Constructor with track being integer
	 * @param fileName
	 */
	public MusicFile(String fileName, String title, String disc, int track) {
		this.fileName = fileName;
		this.title = title;
		this.disc = disc;
		this.track = track;
	}
	
	/**
	 * Constructor with track being String
	 * @param fileName
	 */
	public MusicFile(String fileName, String title, String disc, String track) {
		this.fileName = fileName;
		this.title = title;
		this.disc = disc;
		this.track = Integer.parseInt(track.trim());
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setDisc(String disc) {
		this.disc = disc;
	}
	
	public void setTrack(String track) {
		this.track = Integer.parseInt(track);
	}
	
	public void setTrack(int track) {
		this.track = track;
	}
	
	public String getFileName() {
		return this.fileName;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public String getDisc() {
		return this.disc;
	}
	
	public int getTrack() {
		return this.track;
	}
	
}
