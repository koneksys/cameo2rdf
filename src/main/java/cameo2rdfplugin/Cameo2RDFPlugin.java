package cameo2rdfplugin;


import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager;
import com.nomagic.magicdraw.commandline.CommandLineActionManager;
import com.nomagic.magicdraw.plugins.Plugin;
import javax.swing.*;
public class Cameo2RDFPlugin extends Plugin
{
    
    
    
    public void init() {
        ActionsConfiguratorsManager manager = ActionsConfiguratorsManager.getInstance();
        Cameo2RDFConfigurator mbseConfigurator = null;               
		mbseConfigurator = new Cameo2RDFConfigurator();        
        manager.addContainmentBrowserContextConfigurator( mbseConfigurator ); 
        
        
        
        CommandLineActionManager.getInstance().addAction(new CommandLineActionExample());
        
        
     }

    /**
     * @see com.nomagic.magicdraw.plugins.Plugin#close()
     */
    public boolean close()
    {
        return true;
    }

    /**
     * @see com.nomagic.magicdraw.plugins.Plugin#isSupported()
     */
    public boolean isSupported()
    {
        return true;
    }

    
    
    
    
}