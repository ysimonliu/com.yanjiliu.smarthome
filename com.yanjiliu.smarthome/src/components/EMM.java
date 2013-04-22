package components;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.avis.client.*;

import pseudoRPC.EMMPseudoRPCServerStub;
import pseudoRPC.Message;


public class EMM {

	public static final String TEST_FILE_LOCATION = "H:\\git\\com.yanjiliu.smarthome\\com.yanjiliu.smarthome\\src\\testFiles\\EMMTest.txt";
	private FileReader fr;
	private BufferedReader br;
	private EMMPseudoRPCServerStub server;
	private MusicFileList mfl;
	private String lineContent, dataFileName, fileName, title, disc, track, from, query, temp;
	private String[] values;
	// take the program input parameters
	private static String dataFileNameInput, elvinURL;

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
		this.server = new EMMPseudoRPCServerStub(elvinURL, mfl);
	}
	
	/**
	 * main method, takes two argument, [fileName] and [elvinURL]
	 * @param args
	 */
	public static void main(String[] args) {
		// read parameters. first parameter is dataFileName, second is elvinURL
		//FIXME: change back after test
		/*if (args.length == 2) {
			dataFileName = args[0];
			elvinURL = args[1];
		} else {
			System.exit(1);
		}*/
		dataFileNameInput = TEST_FILE_LOCATION;
		elvinURL = Message.DEFAULT_ELVIN_URL;
		
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
				mfl.addFile(fileName, title, disc, track);
			}

		}
	}
	
	// exit the EMM component gracefully
	public static void exit() {
		System.exit(0);
	}
}