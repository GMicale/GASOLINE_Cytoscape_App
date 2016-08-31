import java.io.*;
import java.util.zip.DataFormatException;
import javax.swing.*;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

public class FileFormatTask extends AbstractTask
{
	private TaskMonitor taskMonitor = null;
	private File[] inputFiles;
	private String regex;
	private String errorMess;

	public FileFormatTask(File[] inputFiles, String regex) 
	{
		this.inputFiles = inputFiles;
		this.regex = regex;
		errorMess=null;
	}
    
	public void run(TaskMonitor taskMonitor)
	{
		if(taskMonitor==null)
			throw new IllegalStateException("Task Monitor is not set.");
		else if(taskMonitor!=null)
		{
			taskMonitor.setProgress(-1);
			taskMonitor.setStatusMessage("Verifying file format...");
		}
		for(int i=0;i<inputFiles.length;i++)
		{
			int line=1;
			try 
			{
				BufferedReader br=new BufferedReader(new FileReader(inputFiles[i]));
				String str="";
				//File data check
				if(regex==null)
				{
					while((str=br.readLine())!=null)
					{
						String[] split=str.split("\t");
						if(split.length>=3)
							Double.parseDouble(split[2]);
						else
							throw new DataFormatException();
						line++;
					}
					br.close();
				}
				else
				{
					while((str=br.readLine())!=null)
					{
						if(!str.matches(regex))
							throw new DataFormatException();
						line++;
					}
					br.close();
				}
			}
			catch(FileNotFoundException e)
			{
				errorMess="Error! File \""+inputFiles[i].getName()+"\" not found";
				return;
			}
			catch(IOException e)
			{
				errorMess="Error reading \""+inputFiles[i].getName()+"\"";
				return;
			}
			catch(DataFormatException e) 
			{
				errorMess="File \""+inputFiles[i].getName()+"\" is not in the correct format. Error at line "+line+"!";
				return;
			}
			catch(NumberFormatException nfe)
			{
				errorMess="File \""+inputFiles[i].getName()+"\" is not in the correct format. Error at line "+line+"!";
				return;
			}
		}
	}

	public void cancel() 
	{}

	public String getTitle() 
	{
		return "Verifying file format...";
	}
	
	public String getError()
	{
		return errorMess;
	}
	
}
