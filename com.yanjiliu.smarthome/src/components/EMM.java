package components;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import pseudoRPC.EMMPseudoRPCServerStub;
import pseudoRPC.Message;

/**
 * This class defines the Electronic Media Manager
 */
public class EMM {

	private FileReader fr;
	private BufferedReader br;
	private EMMPseudoRPCServerStub server;
	private MusicFileList mfl;
	private String lineContent, dataFileName, fileName, title, disc, track;
	private String[] values;
	// take the program input parameters
	private static String dataFileNameInput, elvinURL;

	/**
	 * Constructor, takes the data file name and the elvin URL
	 * @param dataFileNameInput
	 * @param elvinURL
	 */
	public EMM(String dataFileNameInput, String elvinURL){
		this.dataFileName = dataFileNameInput;
		// initialize the mfl, fr and br
		this.mfl = new MusicFileList();
		try {
			this.fr = new FileReader(dataFileName);
			this.br = new BufferedReader(fr);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// read file into data structure
		try {
			readFile(dataFileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// start the server stub
		this.server = new EMMPseudoRPCServerStub(elvinURL, this);
	}
	
	/**
	 * main method, takes two argument, [fileName] and [elvinURL]
	 * @param args
	 */
	public static void main(String[] args) {
		// read parameters. first parameter is dataFileName, second is elvinURL
		if (args.length == 2) {
			dataFileNameInput = args[0];
			elvinURL = args[1];
		} else {
			System.out.println("Error: there has to be exactly 2 parameters");
			System.exit(1);
		}
		
		// instantiate EMM, everything else is taken care of in the constructor
		EMM emm = new EMM(dataFileNameInput, elvinURL);
	}

	/**
	 * This method reads the predefined data file for the EMM. format is assumed correct with no violation
	 * @param fileName
	 * @throws Exception
	 */
	private void readFile(String dataFileName) throws Exception {
		while((lineContent = br.readLine()) != null) {
			
			// skip all possible empty lines
			while (lineContent.isEmpty()){
				lineContent = br.readLine();
				// if reached the end of file, break out of loop
				if (lineContent == null) break;
			}
			
			// if haven't reached the end of file, perform the following parsing
			if (lineContent!=null) {
				// first line is fileName
				values = lineContent.split(":");
				fileName = values[1].trim();
				lineContent = br.readLine();
			
				// second line is title
				values = lineContent.split(":");
				title = values[1].trim();
				lineContent = br.readLine();
			
				// third line is disc
				values = lineContent.split(":");
				disc = values[1].trim();
				lineContent = br.readLine();
			
				// fourth line is track
				values = lineContent.split(":");
				track = values[1].trim();
				lineContent = br.readLine();
			
				// add this record to music file list
				this.mfl.addFile(fileName, title, disc, track);
			}

		}
	}
	
	/**
	 * returns MFL
	 * @return
	 */
	public MusicFileList getMFL() {
		return this.mfl;
	}
	
	// exit the EMM component gracefully
	public void exit() {
		System.exit(0);
	}
}