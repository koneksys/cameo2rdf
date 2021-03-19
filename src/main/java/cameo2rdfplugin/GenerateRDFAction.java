package cameo2rdfplugin;

import java.awt.event.ActionEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.xml.datatype.DatatypeConfigurationException;




import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.properties.BooleanProperty;
import com.nomagic.magicdraw.properties.PropertyID;
import com.nomagic.magicdraw.properties.PropertyManager;
import com.nomagic.magicdraw.ui.browser.actions.DefaultBrowserAction;
import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;



public class GenerateRDFAction extends DefaultBrowserAction {

	/**
	* 
	*/
	private static final long serialVersionUID = 4501968202702572083L;
	/**
	* 
	*/

	private PropertyManager properties = null;

	public GenerateRDFAction() {
		super("", "Generate RDF", null, null);
		properties = new PropertyManager();
		properties.addProperty(new BooleanProperty(PropertyID.SHOW_DIAGRAM_INFO, true));

	}

	private void displayWarning(String text) {
		JOptionPane.showMessageDialog(MDDialogParentProvider.getProvider().getDialogParent(), text);

	}

	public void actionPerformed(ActionEvent e) {
		StringBuffer buffer = new StringBuffer();
		Project project = Application.getInstance().getProject();
		MagicDrawManager.loadSysMLProject(project, buffer);
	
		try {			
			FileWriter fileWriter = new FileWriter("generated_rdf.ttl");
			fileWriter.append(buffer);

			fileWriter.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		JOptionPane.showMessageDialog(null, "SysML Model exported to RDF");
	}

}
