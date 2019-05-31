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
import java.rmi.Naming;
import java.sql.Date;
import java.text.SimpleDateFormat;

import javax.net.ssl.SSLSocket;

public class ClientSend extends Thread{
	SSLSocket socket;
    DataInputStream dis;
    DataOutputStream dos;
    FileInputStream fis;
    BufferedInputStream bis;
    FileOutputStream fos;
    BufferedOutputStream bos;
    BufferedReader reader;
	BufferedReader reader2; 
	PrintWriter writer;
	RmiOp remoteObj;
    String path = System.getProperty("user.home") + "\\Desktop\\catHubClient";

	public ClientSend(SSLSocket socket) {
		this.socket = socket;
	}
	@Override
	public void run() {
		try {
			reader = new BufferedReader(new InputStreamReader(System.in));
			reader2	= new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream());
            dos = new DataOutputStream(socket.getOutputStream());
        	dis = new DataInputStream(socket.getInputStream());
			remoteObj =(RmiOp)Naming.lookup("rmi://localhost:1099/HelloRemote");

			while(true) {
				System.out.println();
				System.out.print("catHub >> ");
				String str = reader.readLine();
				if(str.equals("bye")) {
					break;}
				else if(str.equals("ls")) {
			 		try {
						String msg = remoteObj.getFileList();
						System.out.println();
						System.out.println(msg);
					}catch(Exception e) {
						e.printStackTrace();
					}
			 		continue;
				}
				else if(str.equals("pull")) {
			 		try {
						String msg = remoteObj.getFileList();
						System.out.println();
						System.out.println("[다음의 객체들을 서버로부터 가져왔습니다.]");
						System.out.println();
						System.out.println(msg);

					}catch(Exception e) {
						e.printStackTrace();
					}
			 		
					writer.println(str);
					writer.flush();

					while(true) {
						String filename = dis.readUTF();
						if(filename.equals("end")) break;
				        try {
				        	long fileSize = dis.readLong();
				            String path = System.getProperty("user.home") + "\\Desktop\\catHubClient\\";
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
				        } catch (IOException e) {
				            e.printStackTrace();
				        }
					}
					continue;
				}
				else if(str.equals("push")) {

					writer.println(str);
					writer.flush();
		    		long time = System.currentTimeMillis(); 
		    		SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
		    		String curTime = dayTime.format(new Date(time));
					System.out.println();
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

					dos.writeUTF("end");
					dos.flush();					
					continue;
				}
				writer.println(str);
				writer.flush();
				while(true) {
					String recv = reader2.readLine();
					if(recv.equals("end")) break;
					System.out.println();
					if(recv.equals("404")) {  System.out.println(" 커맨드를 찾을 수 없다구요!"); break;}
					else {  System.out.println(recv);}
				}
			}
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}finally {
			try {
				socket.close();
			}catch(Exception e) {}
		}
	}
}