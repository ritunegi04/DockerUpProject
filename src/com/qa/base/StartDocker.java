package com.qa.base;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.HashMap;
import org.junit.Assert;

public class StartDocker {
	public static void main(String[] arg) throws InterruptedException,IOException
	{
		Runtime runtime=Runtime.getRuntime();
		FileReader fr;
		BufferedReader br;
		int count=0;
		boolean flag=false,err=false;
		Calendar cal=Calendar.getInstance();
		cal.add(Calendar.SECOND, 300);
		long stopNow=cal.getTimeInMillis();		
			File file=new File("DockerStart.bat");
			runtime.exec("cmd /c start "+file);
			File f; 
			Thread.sleep(60000);
			while((System.currentTimeMillis()<stopNow)&& count==0)
			{
				if(flag==true||err==true)
					break;
				
			f=new File("..\\performance-testing-framework\\DockerUp.txt");
			if(!f.exists())
			{
				continue;
			}
			fr = new FileReader(f);
			br=new BufferedReader(fr);
			String currLine=br.readLine();
			
			while(currLine!=null && !flag)
			{
				if (currLine.contains("ERROR"))
				{
					currLine=br.readLine();
					err=true;
					break;
				}
				else if((currLine.contains("grafana")|| currLine.contains("influxdb-timeshift-proxy")
						||currLine.contains("jenkins")||currLine.contains("portainer")||
						currLine.contains("telegraf")||currLine.contains("influxdb"))
						&& currLine.contains("Up"))
					{
						count++;
					}
				
				if(count==6)
				{
					flag=true;
				}
				currLine=br.readLine();
			}
			br.close();
			}
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("..\\performance-testing-framework\\DockerUp.txt", 
					true)));
		try
		{
	Assert.assertTrue(flag);
	out.println("Docker services started successfully");
		}
		catch(AssertionError e)
		{
			out.println("Issue Occurred while starting docker services");
		}
	finally
	{
		out.close();
		runtime.exec("taskkill /f /im cmd.exe");
	}
	}


}
