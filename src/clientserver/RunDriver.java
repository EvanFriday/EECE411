package clientserver;
import java.io.IOException;


public class RunDriver {
	
	final static int ACCEPTING_DATA = 0;
	final static int PROPAGATING_DATA = 1;
	final static int RECEIVE_ONLY = 0;
	final static int GOSSIP = 1;

	public static int STATUS = ACCEPTING_DATA;
	public static int MODE;
	
	public static void main(String[] args) throws OutOfMemoryError, IOException{
		Server server = new Server(9999);
		
		//TODO: Call fileReader(), to populate addressList
		//server.fileReader(file_name);
		
		try{
		 MODE = (Integer) Integer.parseInt(args[0]);
		}
		catch(ArrayIndexOutOfBoundsException e){
			MODE = 0;
		}
		
		if(MODE != RECEIVE_ONLY && MODE != GOSSIP){
			System.err.print("You have specified an invalid mode");	
		}
		
		while(true){
			switch(STATUS){
			case ACCEPTING_DATA:
				//read in new data

				System.out.print("WAITING FOR INCOMING UPDATE\n");
				server.acceptUpdate();
				//Depending on mode specified, either continuously accept data, or propagate as well
				switch(MODE){
				case RECEIVE_ONLY: 	
					STATUS=ACCEPTING_DATA;
					break;
				case GOSSIP:
					STATUS=PROPAGATING_DATA;
					break;
				default: 			
				}
				
				break;
			case PROPAGATING_DATA:
				//Connect to other nodes, and send data.
				server.propagate();
				STATUS=ACCEPTING_DATA;
				break;
			default:
				System.out.println("somehow we are no in the state machine...\n");
			}
		}
	}
	
	

}
