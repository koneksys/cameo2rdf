package cameo2rdfplugin;

import com.nomagic.actions.AMConfigurator;

import com.nomagic.actions.*;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;

//import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdmodels.Model;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.magicdraw.actions.BrowserContextAMConfigurator;
import com.nomagic.magicdraw.actions.MDActionsCategory;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.ui.browser.Tree;
import com.nomagic.magicdraw.ui.browser.actions.DefaultBrowserAction;


public class Cameo2RDFConfigurator implements BrowserContextAMConfigurator, AMConfigurator
{

	/**
	 * Action which should be added to the tree.
	 */
	private MDActionsCategory cat = null;
	
  
    private DefaultBrowserAction generateRDFFromMagicDrawModelAction     = new GenerateRDFAction();
    
    
   
	
	/**
	 * Creates configurator for adding given action.
	 * @param action action to be added to manager.
	 */
	public Cameo2RDFConfigurator()	{
		//this.ac = ac;
	       
        // ADDING ACTION TO CONTAINMENT BROWSER
		
 
	}

	/**
	 * @see com.nomagic.magicdraw.actions.BrowserContextAMConfigurator#configure(com.nomagic.actions.ActionsManager, com.nomagic.magicdraw.ui.browser.Tree)
	 */
	public void configure(ActionsManager mngr, Tree tree) 	{
		if(tree.getSelectedNode() == null) { 
			return;
		}
		ActionsCategory  cat = new ActionsCategory(null,null);

		if(Application.getInstance().getProject() == null) { 
			return;
		}
		
     
        cat = new MDActionsCategory("SysML to RDF","SysML to RDF");
		cat.setNested(true);

		Object userObject = tree.getSelectedNode().getUserObject();		
				
		if (userObject instanceof Model) {      
			cat.addAction(generateRDFFromMagicDrawModelAction);		
			mngr.addCategory(cat);
		} 
		

	}

	/**
	 * @see com.nomagic.actions.AMConfigurator#configure(com.nomagic.actions.ActionsManager)
	 */
	public void configure(ActionsManager mngr)	{
		// adding action separator
		mngr.addCategory(cat);
	}

	public int getPriority()
	{
		return AMConfigurator.MEDIUM_PRIORITY;
	}
}
