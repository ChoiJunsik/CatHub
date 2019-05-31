import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;

import javax.net.ssl.SSLSocket;
public class ServerRecv extends Thread{
	SSLSocket socket;
    String path = System.getProperty("user.home") + "\\Desktop\\catHubServer";
    DataOutputStream dos;
    FileInputStream fis;
    BufferedInputStream bis;
    FileOutputStream fos;
    BufferedOutputStream bos;
    BufferedReader reader;
    PrintWriter writer;
    DataInputStream dis;
	public ServerRecv(SSLSocket socket) {
		this.socket = socket;
	}
	@Override
	public void run() {
		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream());
            dos = new DataOutputStream(socket.getOutputStream());
        	dis = new DataInputStream(socket.getInputStream());

			while(true) {
				String str = reader.readLine();
				if(str==null) { break;}
				else if(str.equals("pull")) {
					System.out.println("Request >> "+str);
		    		System.out.println();
		    		long time = System.currentTimeMillis(); 
		    		SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
		    		String curTime = dayTime.format(new Date(time));
		    		System.out.println("["+curTime+"]");
					File dirFile=new File(path);
					File []fileList=dirFile.listFiles();
					for(File tempFile : fileList) {
					  if(tempFile.isFile()) {
					    String tempPath=tempFile.getParent();
					    String tempFileName=tempFile.getName();
			            dos.writeUTF(tempFileName);
						dos.flush();
			            try {
			            File f = new File(tempPath+"\\"+tempFileName);
			            fis = new FileInputStream(f);
			            bis = new BufferedInputStream(fis);
			            dos.writeLong(f.length());
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
			          }
					}
		    		System.out.println();

					dos.writeUTF("end");
					dos.flush();
				    
				}
				else if(str.equals("push")) {
					System.out.println("Request >> "+str);
					System.out.println();
		    		long time = System.currentTimeMillis(); 
		    		SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
		    		String curTime = dayTime.format(new Date(time));
		    		System.out.println("["+curTime+"]");
					while(true) {
						String filename = dis.readUTF();
						if(filename.equals("end")) break;
				        try {
				        	long fileSize = dis.readLong();
				            String path = System.getProperty("user.home") + "\\Desktop\\catHubServer\\";
				            String fName = path+filename;
				            File f = new File(fName);
				            fos = new FileOutputStream(f);
				            bos = new BufferedOutputStream(fos);
				            int len;
				            int size = 4096;
				            byte[] data = new byte[size];
				            while (fileSize>0 && (len = dis.read(data,0,(int)Math.min(data.length, fileSize))) != -1) {
				                bos.write(data, 0, len);
				                fileSize -= len;
				            }
				            bos.flush();
				            fos.close();
				            System.out.println("  "+filename+" 을 전송받았습니다.");
				            System.out.println();
				        } catch (IOException e) {
				            e.printStackTrace();
				        }
					}

				}	
				else{
					System.out.println("Request >>"+str);

					writer.println("404");
					writer.flush();
				}
			   
			}
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	
	
}
