import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.net.ssl.SSLSocket;


public class FileDetect extends Thread {
	
	private static final String projPath = System.getProperty("user.home") + "\\Desktop\\catHubClient";
	
    private WatchKey watchKey;
    
    SSLSocket socket;
    PrintWriter writer;
    DataInputStream dis;
    DataOutputStream dos;
    FileInputStream fis;
    BufferedInputStream bis;
    RmiOp remoteObj;
    
    public FileDetect(SSLSocket socket) {
    	this.socket = socket;
    	
    }
    
    public void pushIt(Path paths) throws IOException {
    	writer = new PrintWriter(socket.getOutputStream());
    	dos = new DataOutputStream(socket.getOutputStream());
    	dis = new DataInputStream(socket.getInputStream());
    	
    	String inputname = projPath + "\\" + paths.getFileName();
        try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        writer.println("push");
		writer.flush();
		long time = System.currentTimeMillis(); 
		SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
		String curTime = dayTime.format(new Date(time));
		System.out.println();
		System.out.println("["+curTime+"]");
		File dirFile=new File(inputname);
		
		String tempFileName=dirFile.getName();
		
		dos.writeUTF(tempFileName);
		dos.flush();
        try {
        	
        	fis = new FileInputStream(dirFile);
            bis = new BufferedInputStream(fis);
            dos.writeLong(dirFile.length());
			dos.flush();
            
            int len;
            int size = 4096;
            byte[] data = new byte[size];
            while ((len = bis.read(data)) != -1) {
                dos.write(data, 0, len);
            }			  
            dos.flush();
			fis.close();
            System.out.println("  "+tempFileName+" 을 전송하였습니다.");
            } catch (IOException e) {
            	e.printStackTrace();}
          
		

		dos.writeUTF("end");
		dos.flush();
    }
    
    
    public void run(){
        //watchService 생성
    	try {
	        WatchService watchService = FileSystems.getDefault().newWatchService();
	        remoteObj =(RmiOp)Naming.lookup("rmi://localhost:1099/HelloRemote");
	        
	        Path path = Paths.get(projPath);
	        path.register(watchService,
	            StandardWatchEventKinds.ENTRY_CREATE,
	            StandardWatchEventKinds.ENTRY_DELETE,
	            StandardWatchEventKinds.ENTRY_MODIFY,
	            StandardWatchEventKinds.OVERFLOW);
        
        	
        	
            while(true) {
                try {
                    watchKey = watchService.take();
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                	break;
                }
                List<WatchEvent<?>> events = watchKey.pollEvents();
                for(WatchEvent<?> event : events) {
                    Kind<?> kind = event.kind();
                    Path paths = (Path)event.context();
                    if(kind.equals(StandardWatchEventKinds.ENTRY_CREATE)) {
                        System.out.println("\ncreated something in directory\n");
                       
                        try {
							Thread.sleep(100);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
                        
                        try {
							pushIt(paths);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}				
   

                    }else if(kind.equals(StandardWatchEventKinds.ENTRY_DELETE)) {
                        System.out.println("\ndelete something in directory\n");
                        
                        try {
							Thread.sleep(100);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
                        
                        try {
    						if( remoteObj.doFileDelete(""+paths) ) {
    							System.out.println();
    							//System.out.println("Delete Successfully");
    						}
    						else {
    							System.out.println();
    							System.out.println("Delete Failed!");
    						}
    						
    					}catch(Exception e) {
    						e.printStackTrace();
    					}
                        
                        
                        
                        
       
                    }else if(kind.equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
                        System.out.println("modified something in directory");
                        
                        
                        try {
							Thread.sleep(100);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
                        
                        try {
							pushIt(paths);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                        
                    }else if(kind.equals(StandardWatchEventKinds.OVERFLOW)) {
                        System.out.println("overflow");
                    }else {
                        System.out.println("hello world");
                    }
                }
                if(!watchKey.reset()) {
                    try {
                        watchService.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("");
                System.out.print("catHub >> ");
            }
    	} catch (IOException ioe) {
    		 System.out.println("IOException");
    		ioe.printStackTrace();
    	} catch (NotBoundException nbe){
    		 System.out.println("NotBoundException");
    		nbe.printStackTrace();
    	}catch (Exception e) {
    		 System.out.println("");
    		e.printStackTrace();
    	}

        
    }


    public String test() {
        System.out.println(projPath);        
        return "hello";
    }

}
