package cameo2rdfplugin;



import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

import javax.swing.JOptionPane;

import com.nomagic.magicdraw.commandline.CommandLineAction;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.project.ProjectDescriptor;
import com.nomagic.magicdraw.core.project.ProjectDescriptorsFactory;
import com.nomagic.magicdraw.core.project.ProjectsManager;


class CommandLineActionExample implements CommandLineAction
{
	@Override
	public byte execute(String[] args)
	{
		System.out.println("------------------- This code is executed in running application environment -------------------");
		System.out.println("SysML to RDF Output can be found in the log file.");
		printArguments(args);
		return 0;
	}

	private static int printArguments(String[] args)
	{
		if (args != null && args.length == 1)
		{
			String filePath = args[0];
			System.out.println("filePath: " + filePath );
			System.out.println("args: " + args );
			
			
			if(!filePath.endsWith(".mdzip")) {
				System.out.println("Could not execute translation to RDF. Argument specifying mdzip file location is not referring to a .mdzip file.");
				return 1;
			}
			
			//File fileSystemObtainedFile = new File(filePath);
			
			
			final File file = new File(filePath);
			final ProjectDescriptor projectDescriptor = ProjectDescriptorsFactory.createProjectDescriptor(file.toURI());
			if (projectDescriptor == null)
			{
				System.out.println("Project descriptor was not created for " + file.getAbsolutePath());
				return -1;
			}
			final ProjectsManager projectsManager = Application.getInstance().getProjectsManager();
			projectsManager.loadProject(projectDescriptor, true);
			final Project project = projectsManager.getActiveProject();
			if (project == null)
			{
				System.out.println("Project " + file.getAbsolutePath() + " was not loaded.");
				return -1;
			}
			
			
			
			
			
//			System.out.println(".mdzip file location:");
//			System.out.println(String.join(", ", args));						
			StringBuffer buffer = new StringBuffer();
//			Application magicdrawApplication = Application.getInstance();
//			ProjectsManager projectsManager = magicdrawApplication.getProjectsManager();
//			ProjectDescriptor projectDescriptor = ProjectDescriptorsFactory.createProjectDescriptor(fileSystemObtainedFile.toURI());						
//			projectsManager.loadProject(projectDescriptor, true);									
//			Project project = projectsManager.getActiveProject();
			System.out.println("project.getName():" + project.getName());
			MagicDrawManager.loadSysMLProject(project, buffer);
		
			try {			
				FileWriter fileWriter = new FileWriter("generated_rdf.ttl");
				fileWriter.append(buffer);

				fileWriter.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			System.out.println("SysML Model exported to RDF");
			
			
			
			
			
		}
		else
		{
			System.out.println("Could not execute translation to RDF. Argument specifying mdzip file location is missing.");
			return 1;
		}
		return 0;
	}
}

