

import java.io.*;
import java.net.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;




public class Slave {
	



	
    public static void main(String[] args) throws IOException {
        if (args.length != 4 ) {
            System.err.println("./slave -h IP_address -p port_number\n");
            System.exit(-1);
        }
        
        String hostname="";
        int port=0;
        if(args[0].equals("-h") && args[2].equals("-p"))
        {
            hostname=args[1];
            port=Integer.parseInt(args[3]);
        }
        else if(args[2].equals("-h") && args[0].equals("-p")){
            hostname=args[3];
            port=Integer.parseInt(args[1]);
        }
        else
        {
        	System.err.println("./slave -h IP_address -p port_number\n");
            System.exit(-1);
        }

        try
        {
            Socket slavesock=new Socket(hostname,port);
            System.out.println("connect to master successful");
	        PrintWriter out=new PrintWriter(slavesock.getOutputStream(),true);
            BufferedReader in=new BufferedReader(new InputStreamReader(slavesock.getInputStream()));
            
            boolean keepgoing = true;
            while (keepgoing){
            	System.out.println("waiting for master command");
            	String inline;
                inline=in.readLine(); //command should like connect www.google.com 80
                System.out.println("message from master : "+inline);
                thread1 thread123 = new thread1(inline);
                Thread t =new Thread(thread123);
                t.start();

            	
            }
            
            out.close();
            in.close();
            slavesock.close();
            
            
        }catch (UnknownHostException e)
        {
            System.err.println("Cannot define the host "+hostname+"\n");
            System.exit(-1);
            
        }
        catch (IOException e)
        {
        	System.err.println("Could not get I/O for connect to "+hostname+"\n");
        	System.exit(-1);
        }
        
         
        

    }
    
}
class socklist1{

	public static List<Socket> socklist = Collections.synchronizedList(new LinkedList<>());
	public static List<String> IPlist = Collections.synchronizedList(new LinkedList<>());
	public static List<String> portlist = Collections.synchronizedList(new LinkedList<>());
	socklist1(){

	   }

	}
class thread1 implements Runnable{
	private String inline;
	Socket sock;
	String IP;
	int port;
	String url;
	StringBuilder sb=new StringBuilder();
	
	thread1(String inline){
		this.inline=inline;

		
	}
	
	public void run(){
		String[] splited = inline.split("\\s+");
		if(splited[0].equals("connect")){
			//execute the connect command from master
			IP=splited[1];
			port=Integer.parseInt(splited[2]);
			synchronized(socklist1.socklist){				
				try{
					sock=new Socket(IP,port);
					socklist1.socklist.add(sock);
					 
					System.out.println("slave successfully connect to "+IP+" "+port+"\n");
					PrintWriter out=new PrintWriter(sock.getOutputStream(),true);
		            BufferedReader in=new BufferedReader(new InputStreamReader(sock.getInputStream()));
		            if (splited.length==4 && splited[3].equals("keepalive")){
						//set the socket to keep alive
		            	
						sock.setKeepAlive(true);
						if (sock.getKeepAlive()== true){
							System.out.println("The socket set keep alive successful!");
						}
					}
					else if(splited.length==4 && splited[3]contains("url")){
						url=generatestring();
						System.out.println(String.format("send search request: %s",url));
						sb.append(String.format("GET /#q=%s HTTP/1.1\r\n",url));
						sb.append(String.format("Host: %s:%s\r\n",IP,splited[2]));
						sb.append("Connection: keep-alive\r\n");
						sb.append("Upgrade-Insecure-Requests: 1");
						sb.append("Accept-Language: en;\r\n\r\n");
						sb.append("Accept-Encoding: gzip, deflate, sdch\r\n");
						sb.append("Accept: text/html,text/plain,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\r\n");
						sb.append("\r\n");    //the header is finished
						out.write(sb.toString());
						System.out.println(String.format("send header: %s",sb.toString()));
						out.flush();
						String line=null;
						/*int contentLength = 0;
						do{
							//parser the header of the respond
							line=in.readLine();
							if(line.startsWith("HTTP/1.1")){
								//the status of the respond
								String[] output=line.split(" ");
								if(output[1] == "200"){
									System.out.println("target receive request successful");
									
									
								}
								else{
									//send request to target error
									System.err.println("unable to response the request from target");
									System.exit(-1);
								}
								
							}
							else if(line.startsWith("Content-Length")){
								//content length of the respond
								String[] output1=line.split(" ");
								contentLength=Integer.parseInt(output1[1]);
								System.out.println("the total size of content length is: "+contentLength);
							}
							
						}while (!line.equals("\r\n")); //the end of the header
						*/
						while ((line=in.readLine())!=null){
							//print the content of the search respond
							System.out.println(line);
							
						}
						out.close();
						in.close();
						
						
					}
					
					
				}
				catch (UnknownHostException e)
		        {
		            System.err.println("Cannot define the host "+IP+"\n");
		            System.exit(-1);
		            
		        }
		        catch (IOException e)
		        {
		        	System.err.println("Could not get I/O for connect to "+IP+"\n");
		        	System.exit(-1);
		        }
			}
			synchronized(socklist1.portlist){
				socklist1.portlist.add(splited[2]);
			}
			synchronized(socklist1.IPlist){
				socklist1.IPlist.add(IP);
			}
			
			}
		else if(splited[0].equals("disconnect")){
			
			if (splited.length==3){
				IP=splited[1];
				String port1=splited[2];
				synchronized(socklist1.socklist){
					for(int count=0;socklist1.socklist.size()>count;count++){
						if(socklist1.portlist.get(count).equals(port1)&&socklist1.IPlist.get(count).equals(IP)){
							
							synchronized(socklist1.socklist){
								try {
									
									
									socklist1.socklist.get(count).close();
									socklist1.socklist.remove(count);
									System.out.println("disconnect from "+IP+" "+port1+" successful");
								} catch (IOException e) {
									System.out.println("close socket fail");
									System.exit(-1);
								}
							}
							synchronized(socklist1.portlist){
								
								socklist1.portlist.remove(count);
							}
							synchronized(socklist1.IPlist){
								socklist1.IPlist.remove(count);
							}
							count--;
							
						}
						
					}
				}
				
			}
			else if(splited.length==2){
				
				IP=splited[1];
				synchronized(socklist1.socklist){
					for(int count=0;socklist1.socklist.size()>count;count++){
						
						
						if(socklist1.IPlist.get(count).equals(IP)){
							
							
							synchronized(socklist1.socklist){
								try {
									
									
									socklist1.socklist.get(count).close();
									socklist1.socklist.remove(count);
									System.out.println("disconnect from "+IP+" successful");
								} catch (IOException e) {
									System.out.println("close socket fail");
									System.exit(-1);
								}
							}
							synchronized(socklist1.portlist){
								
								socklist1.portlist.remove(count);
							}
							synchronized(socklist1.IPlist){
								socklist1.IPlist.remove(count);
							}
							count--;
							
						}
						
					}
				
				}
				
			}
			else{
				System.out.println("Illegal input from master");
			}
			
		}
		else{
			System.out.println("Unknow command, please input again");
		}
		
	}
	



	
	private String generatestring(){
		//generate a lower letter string between 1 char and 10 chars
		StringBuilder sb = new StringBuilder();
		int num=(int)(Math.random()*9)+1;
		for (int i =0;i<num;i++){
			int charnum=(int)(Math.random()*23);
			char a = (char) ((int) 'a'+charnum);
			sb.append(a);
		}
		
		return sb.toString();
	}
}




