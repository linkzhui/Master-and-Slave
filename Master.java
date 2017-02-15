
import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Master 
{ 
 public static void main(String[] args) throws IOException 
   { 
	 if (args.length != 2 && args[0]!="-p") {
         System.err.println("./master -p port_number\n");
         System.exit(-1);
     }
    ServerSocket serverSocket = null; 
    

    try { 
         serverSocket = new ServerSocket(Integer.parseInt(args[1])); 
        } 
    catch (IOException e) 
        { 
         System.err.println("Could not listen on port: "+args[1]); 
         System.exit(-1); 
        } 

    
    System.out.println ("Waiting for connection.....");
    
    threadcom executeinputcommand = new threadcom();
    Thread t =new Thread(executeinputcommand);
    t.start();
    boolean breakout =true;

    
    while (breakout){
    	try { 
            Socket slaveSocket = serverSocket.accept(); 
            threadslave slave=new threadslave(slaveSocket);
            Thread g=new Thread(slave);
            g.start();
            
           } 
       catch (IOException e) 
           { 
            System.err.println("Accept failed."); 
            System.exit(-1); 
           } 
    	
    }
    

    
    

    
    serverSocket.close();
    
   } 

} 
//use to store the global variable
class socklist{

	public static List<Socket> q = Collections.synchronizedList(new LinkedList<>());
	public static List<String> date = Collections.synchronizedList(new LinkedList<>());
	public int count=0;
	socklist(){

	   }

	}
class threadslave implements Runnable{
	private Socket slave;
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	Calendar cal=Calendar.getInstance();
	threadslave(Socket slave){
		this.slave=slave;
	}
	public void run(){
		
	    //BufferedReader in = new BufferedReader( new InputStreamReader( slave.getInputStream()));
		synchronized(socklist.q){
			socklist.q.add(slave);
		}
		synchronized(socklist.date){
			socklist.date.add(dateFormat.format(cal.getTime()).toString());
		} 
		
		
	}
}
class threadcom implements Runnable{
	
	private boolean keepdo = true;
	private String IP;
	private int port;
	private Socket slave;
	
	private String date;
	private Scanner getin=new Scanner(System.in);
	threadcom(){
		
	}
	public void run(){
		
		while (keepdo){
			int count=0;
			int found=0;
			String s=null;
			System.out.print(">");
			String commandinput=getin.nextLine();
			String[] splited =null;
			splited=commandinput.split(" ");
			
			if(splited[0].equals("connect")){
				IP=splited[2];
				port=Integer.parseInt(splited[3]);
				if(splited[1].equals("all")){
			    //Iterator i=socklist.q.iterator();
					
					while (socklist.q.size()>count)
					{
						
						slave =socklist.q.get(count);
						count++;
						if(splited.length==4){
							try {
								PrintWriter out = new PrintWriter(slave.getOutputStream(), true);
								s=splited[0]+" "+IP+" "+port;
								out.println(s);
								out.flush();
								
							}catch (IOException e) 
					        { 
						         System.err.println("send message to slave failed."); 
						         System.exit(-1); 
						     }
							
						}
						else if (splited.length==6){
						
							for(int i=0;i<Integer.parseInt(splited[4]);i++){
								try {
									PrintWriter out = new PrintWriter(slave.getOutputStream(), true);
									s=splited[0]+" "+IP+" "+port+" "+splited[5];
									out.println(s);
									out.flush();
									
								}catch (IOException e) 
						        { 
							         System.err.println("send message to slave failed."); 
							         System.exit(-1); 
							     }
								
								
							}
							
							
							
						}
						else if (splited.length==5){
							if (splited[4].contains("url")|| splited[4].equals("keepalive")){
								try {
									PrintWriter out = new PrintWriter(slave.getOutputStream(), true);
									s=splited[0]+" "+IP+" "+port+" "+splited[4];
									out.println(s);
									out.flush();
									
								}catch (IOException e) 
						        { 
							         System.err.println("send message to slave failed."); 
							         System.exit(-1); 
							     }
							}
							else{
								for(int i=0;i<Integer.parseInt(splited[4]);i++){
									try {
										PrintWriter out = new PrintWriter(slave.getOutputStream(), true);
										s=splited[0]+" "+IP+" "+port;
										out.println(s);
										out.flush();
										
									}catch (IOException e) 
							        { 
								         System.err.println("send message to slave failed."); 
								         System.exit(-1); 
								     }
								
							}
						}
						
					}
				}
				}
				else{
					//connect specific slave to specific server
					while(socklist.q.size()>count){
						slave =socklist.q.get(count);
						count++;
						if((slave.getInetAddress().getHostName().equals(splited[1])||
								slave.getInetAddress().getHostAddress().equals(splited[1]))){
							found++;
							if(splited.length==4){
								try {
									PrintWriter out = new PrintWriter(slave.getOutputStream(), true);
									s=splited[0]+" "+IP+" "+port;
									out.println(s);
									out.flush();
									
								}catch (IOException e) 
						        { 
							         System.err.println("send message to slave failed."); 
							         System.exit(-1); 
							     }
								
							}
							else if (splited.length==6){
								
								for(int i=0;i<Integer.parseInt(splited[4]);i++){
									try {
										PrintWriter out = new PrintWriter(slave.getOutputStream(), true);
										s=splited[0]+" "+IP+" "+port+" "+splited[5];
										out.println(s);
										out.flush();
										
									}catch (IOException e) 
							        { 
								         System.err.println("send message to slave failed."); 
								         System.exit(-1); 
								     }
									
									
								}
								
								
								
							}
							else if (splited.length==5){
								if (splited[4].equals("url")||splited[4].equals("keepalive")){
									try {
										PrintWriter out = new PrintWriter(slave.getOutputStream(), true);
										s=splited[0]+" "+IP+" "+port+" "+splited[4];
										out.println(s);
										out.flush();
										
									}catch (IOException e) 
							        { 
								         System.err.println("send message to slave failed."); 
								         System.exit(-1); 
								     }
								}
								else{
									for(int i=0;i<Integer.parseInt(splited[4]);i++){
										try {
											PrintWriter out = new PrintWriter(slave.getOutputStream(), true);
											s=splited[0]+" "+IP+" "+port;
											out.println(s);
											out.flush();
											
										}catch (IOException e) 
								        { 
									         System.err.println("send message to slave failed."); 
									         System.exit(-1); 
									     }
									
								}
							}
							
						}
							
						}
					}
					
					if(found==0){
						System.err.println("The slave IP address is invalid");
						System.exit(-1);
						
					}
				}
			}
			else if(splited[0].equals("disconnect")){

				IP=splited[2];
				if(splited[1].equals("all")){
			    //Iterator i=socklist.q.iterator();
					
					while (socklist.q.size()>count)
					{
						slave =socklist.q.get(count);
						count++;
						
						try {
							PrintWriter out = new PrintWriter(slave.getOutputStream(), true);
							if(splited.length==3){
								s=splited[0]+" "+IP;
								
							}
							else{
								s=splited[0]+" "+IP+" "+splited[3];
							}
							out.println(s);
							out.flush();
						
						}catch (IOException e) 
				        { 
					         System.err.println("send message to slave failed."); 
					         System.exit(1); 
					     } 

				}
				}
				else{
					while(socklist.q.size()>count){
						slave =socklist.q.get(count);
						count++;
						if((slave.getInetAddress().getHostName().equals(splited[1])||
								slave.getInetAddress().getHostAddress().equals(splited[1]))){
							found++;
							try {
								PrintWriter out = new PrintWriter(slave.getOutputStream(), true);
								
								if(splited.length==3){
									s=splited[0]+" "+IP;
									
								}
								else{
									s=splited[0]+" "+IP+" "+splited[3];
								}
								
								out.println(s);
								out.flush();
						
							}catch (IOException e) 
					        { 
						         System.err.println("send message to slave failed."); 
						         System.exit(-1); 
						     }
							
							
							
						}
					}
						
				
				
				
				}
				}
			
			else if(splited[0].equals("list")){
				while (socklist.q.size()>count)
				{
					
					slave = socklist.q.get(count);
					date=socklist.date.get(count);
					count++;
					System.out.println(slave.getInetAddress().getHostName()+"  "+slave.getInetAddress().getHostAddress()+"  "+slave.getPort()+"   "+date);
					
					}
				
			}
			else if (commandinput.equals("quit")){
				keepdo=false;
				
			}
			else{
				System.out.println("The input command is invalid, please input again");
			}
		}
		
		getin.close();
	}
}




