# Cameo to RDF Plugin 

The code was developed and tested for **Cameo Systems Modeler** v19.0 SP2 and v19.0 SP4.

## 1. Installing the plugin ##
The Cameo2RDF plugin is available as a zip file as an artifact
TODO: insert location

1. Unzip **cameo2rdfplugin-X.Y.zip** 
1. Copy the ***cameo2rdfplugin-X.Y folder*** into your *{Cameo root dir}/plugins* folder. 
1. **Restart Cameo**

The other files which are part of the artifact publication can be ignored.

The zip file contains the plugin code as a jar, as well as a plugin.xml file, and a lib folder with the jar dependencies. Cameo can then load the Cameo2RDF plugin during runtime.

## 2. Using the plugin 

Open any SysML model, select the top-level model element in the containment view at the top left, right-click and select **SysML to RDF-> Generate RDF**. The generated RDF will appear at **{Cameo root dir}/generated_rdf.ttl**.


## 3. Building the plugin  

1. Set the ***CAMEO\_INSTALL\_DIR*** environment variable to point to the root installation directory of Cameo 
2. Run **gradle build** in the root directory.
3. The built version of the Cameo plugin folder will be located at ***/build/pluginzip/cameo2rdfplugin.zip***  

## 4. Implementation Notes

### Ignored SysML packages and element types

The Cameo2RDF plugin treats every SysML element as an EMF EObject and performs the transformation to RDF independent of the SysML schema. The implementation code is thus very simple. 


The transformation starts with the top-level SysML model element and then automatically traverses through its children and children of children etc. (e.g. SysML packages , SysML blocks, SysML part properties, etc.). 

The transformation does not distinguish between user-defined SysML packages and imported "library" SysML packages. Without filter, the transformation would transform not just the SysML model into RDF, but also all imported packages such as related to the QUDV units or the UML metamodel, resulting in a big RDF file. In order to prevent the tranformation of unnecessary packages, the implementation is currently set to ignore the packages listed below, as well as certain types of SysML elements.

**Ignored Packages**:

- SysML
- Matrix Templates Profile
- UML Standard Profile
- QUDV Library
- QUDV
- PrimitiveValueTypes
- MD Customization for SysML
- ISO-80000
- ISO-80000-Extension
- MD\_Customization\_for_ViewsViewpoints
- MD\_Customization\_for_Requirements
- Free\_Form\_Elements\_Profile
- MagicGrid\_Profile
- ParametricExecutionProfile
- SimulationProfile  




**Ignored SysML Elements**

- profile_application
- profile
- stereotype
- element_import
- diagram
- extension_end
- extension


### URL encoding

Identifiers of SysML elements can contain spaces or special characters. However, the equivalent identifier of a SysML element in RDF is a URL which cannot contain special characters. 

Therefore, percent-encoding was used to ensure that all generated URLs in RDF are valid. Percent-encoding, also known as URL encoding, is a method to encode arbitrary data in a Uniform Resource Identifier (URI) using only the limited US-ASCII characters legal within a URI. 



 

## 4. Possible Improvements 

### Configuration of Ignored SysML Packages and Elements

Right now, the list of ignored packages and SysML elements is hard-coded in the implementation. Ideally, a user could define this info in a file which would be an input to the transformation. 

### Configuration of Generated RDF

These aspects rleated to URLs in the generated RDF are right now hard-coded. They could be configurable and defined in an input file for the transformation

- baseURL of all generated RDF resources  
- location of generated RDF
- RDF syntax (right now just Turtle) 



### Additional Deployment options

- Command-line tool
- AWS lambda function

### Automated Testing
The transformation could be packaged as a standalone OSGI application for automated tests without needing to launch Cameo manually.