package cameo2rdfplugin;

import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.springframework.web.util.UriUtils;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.project.ProjectsManager;
import com.nomagic.magicdraw.foundation.MDObject;
import com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdmodels.Model;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Classifier;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.InstanceSpecification;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;

public class MagicDrawManager {

	static Collection<String> predefinedMagicDrawSysMLPackageNames = new HashSet<String>();
	static Collection<String> ignoredEObjectTypes = new HashSet<String>();
	public static String baseHTTPURI = "http://api.koneksys.com" + "/cameo";
	static String projectId;
	static Application magicdrawApplication;
	static Model model;
	public static Project project;
	public static ProjectsManager projectsManager;
	public static Set<String> mappedEObjects = new HashSet<String>();

	public static synchronized void loadSysMLProject(Project project, StringBuffer buffer) {

		projectId = project.getName();
		initializeCollections();

		// List of packages not to load
		predefinedMagicDrawSysMLPackageNames.add("SysML");
		predefinedMagicDrawSysMLPackageNames.add(UriUtils.encodePath("Matrix Templates Profile", "UTF-8"));
		predefinedMagicDrawSysMLPackageNames.add(UriUtils.encodePath("UML Standard Profile", "UTF-8"));
		//predefinedMagicDrawSysMLPackageNames.add("QUDV_Library");
		predefinedMagicDrawSysMLPackageNames.add(UriUtils.encodePath("QUDV Library", "UTF-8"));
		predefinedMagicDrawSysMLPackageNames.add("QUDV");
		predefinedMagicDrawSysMLPackageNames.add("PrimitiveValueTypes");
		//predefinedMagicDrawSysMLPackageNames.add("MD_Customization_for_SysML");
		predefinedMagicDrawSysMLPackageNames.add(UriUtils.encodePath("MD Customization for SysML", "UTF-8"));
		predefinedMagicDrawSysMLPackageNames.add("ISO-80000");
		predefinedMagicDrawSysMLPackageNames.add("ISO-80000-Extension");
		//predefinedMagicDrawSysMLPackageNames.add("MD_Customization_for_ViewsViewpoints");
		predefinedMagicDrawSysMLPackageNames.add(UriUtils.encodePath("MD Customization for ViewsViewpoints", "UTF-8"));
		//predefinedMagicDrawSysMLPackageNames.add("MD_Customization_for_Requirements");
		predefinedMagicDrawSysMLPackageNames.add(UriUtils.encodePath("MD Customization for Requirements", "UTF-8"));				
		//predefinedMagicDrawSysMLPackageNames.add("Free_Form_Elements_Profile");
		predefinedMagicDrawSysMLPackageNames.add(UriUtils.encodePath("Free Form Elements Profile", "UTF-8"));
		//predefinedMagicDrawSysMLPackageNames.add("MagicGrid_Profile");
		predefinedMagicDrawSysMLPackageNames.add(UriUtils.encodePath("MagicGrid Profile", "UTF-8"));
		predefinedMagicDrawSysMLPackageNames.add("ParametricExecutionProfile");
		predefinedMagicDrawSysMLPackageNames.add("SimulationProfile");
		
	
	
		
		
		// ignored EObject Types
		ignoredEObjectTypes.add("profile_application");
		ignoredEObjectTypes.add("profile");
		ignoredEObjectTypes.add("stereotype");
		ignoredEObjectTypes.add("element_import");
		ignoredEObjectTypes.add("diagram");
		ignoredEObjectTypes.add("extension_end");
		ignoredEObjectTypes.add("extension");

		// mapping MagicDraw SysML model
		mapSysMLModel(project, buffer);

	}

	private static void initializeCollections() {

		predefinedMagicDrawSysMLPackageNames.clear();
		ignoredEObjectTypes.clear();
		mappedEObjects.clear();
	}

	

	private static void mapSysMLModel(Project project, StringBuffer buffer) {

		Model mdSysMLModel = project.getModel();
		EObject eObject = (EObject) mdSysMLModel;
		mapEObject(eObject, buffer);

		return;

	}

	private static void mapEObject(EObject eObject, StringBuffer buffer) {
		String eObjectUniqueID = EcoreUtil.getIdentification(eObject);
		if (mappedEObjects.contains(eObjectUniqueID)) {
			return;
		} else {
			mappedEObjects.add(eObjectUniqueID);
		}

		if (eObject instanceof MDObject) {
			MDObject mdObject = (MDObject) eObject;
			String subjectType = mdObject.getHumanType();

			// String subjectType = mdObject.getHumanType();
//			subjectType = subjectType.replaceAll("\\n", "-").replaceAll(" ", "_").replaceAll("\\r", "-")
//					.replaceAll("\\s", "_");
			
			subjectType = UriUtils.encodePath(subjectType, "UTF-8");
			
			subjectType = subjectType.toLowerCase();

			if (ignoredEObjectTypes.contains(subjectType)) {
				return;
			}

			if (subjectType.equals("instance_specification")) {
				if (getStereotypedElement(mdObject) != null && getStereotypeClassifier(mdObject) != null) {
					return;
				}				
			}

			String subjectIdentifier = getQualifiedNameOrID(mdObject);

			// avoid to translate to RDF all UML metamodel elements
			for (String packageName : predefinedMagicDrawSysMLPackageNames) {
				if (subjectIdentifier.startsWith(packageName)) {
					return;
				}
			}

			subjectIdentifier = UriUtils.encodePath(subjectIdentifier, "UTF-8");
			
			String subjectURI = baseHTTPURI + "/" + projectId + "/" + subjectType + "/"
					+ subjectIdentifier;
//			String subjectURI = baseHTTPURI + "/" + projectId + "/" + subjectType + "/"
//					+ subjectIdentifier.replaceAll("\\n", "-").replaceAll(" ", "_");
			String typePredicateURI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";

			String subjectTypeURI = baseHTTPURI + "/vocab/" + subjectType;

			// triple indicating type
			buffer.append("<");
			buffer.append(subjectURI);
			buffer.append(">");
			buffer.append(" ");
			buffer.append("<");
			buffer.append(typePredicateURI);
			buffer.append(">");
			buffer.append(" ");
			buffer.append("<");
			buffer.append(subjectTypeURI);
			buffer.append(">");
			buffer.append(" .");
			buffer.append("\n");

			for (EAttribute eAttribute : eObject.eClass().getEAllAttributes()) {
				String attributeName = eAttribute.getName();
				String attributeOwnerName = eAttribute.getEContainingClass().getName();

				String attributePredicateURI = baseHTTPURI + "/vocab/" + attributeOwnerName + "#" + attributeName;
				String attributeValue = null;
				Object objectValue = eObject.eGet(eAttribute);
				if (objectValue instanceof String) {
					attributeValue = (String) objectValue;
				} else if (objectValue instanceof org.eclipse.emf.common.util.Enumerator) {
					Enumerator attributeEnumValue = (Enumerator) objectValue;
					attributeValue = attributeEnumValue.getLiteral();
				}

				if (attributeValue != null) {
					if (!attributeValue.equals("")) {

						// attributeValue = attributeValue.replaceAll("\\\"", "");
						attributeValue = UriUtils.encodePath(attributeValue, "UTF-8"); 
						//attributeValue = attributeValue.replace("\n", "").replace("\r", "");

						byte[] utf8Bytes;
						try {
							utf8Bytes = attributeValue.getBytes("UTF8");
							attributeValue = new String(utf8Bytes, "UTF8");

							// triple indicating attribute value
							buffer.append("<");
							buffer.append(subjectURI);
							buffer.append(">");
							buffer.append(" ");
							buffer.append("<");
							buffer.append(attributePredicateURI);
							buffer.append(">");
							buffer.append(" ");
							buffer.append("\"");
							buffer.append(attributeValue);
							buffer.append("\"");
							buffer.append(" .");
							buffer.append("\n");

						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

				}

			}

			for (EReference eReference : eObject.eClass().getEAllReferences()) {

				Object referencedObject = eObject.eGet(eReference);

				if (referencedObject != null) {

					if (referencedObject instanceof List<?>) {

						EList eList = (EList) referencedObject;

						for (Object objectInList : eList) {
							if (objectInList instanceof MDObject) {

								mapEReference(subjectURI, eReference, objectInList, buffer);
								EObject eReferencedObject = (EObject) objectInList;
								mapEObject(eReferencedObject, buffer);
							}

						}
					}

					else if (referencedObject instanceof MDObject) {
						mapEReference(subjectURI, eReference, referencedObject, buffer);
						EObject eReferencedObject = (EObject) referencedObject;
						mapEObject(eReferencedObject, buffer);
					}

				}

			}

		}

	}

	static void mapEReference(String subjectURI, EReference eReference, Object referencedObject, StringBuffer buffer) {
		EObject eReferencedObject = (EObject) referencedObject;
		MDObject mdRefenrecedObject = (MDObject) eReferencedObject;
		String objectType = mdRefenrecedObject.getHumanType();

		objectType = UriUtils.encodePath(objectType, "UTF-8");
		
//		objectType = objectType.replaceAll("\\n", "-").replaceAll(" ", "_").replaceAll("\\r", "-").replaceAll("\\s",
//				"_");
		
		
		
		objectType = objectType.toLowerCase();

		String predicateURI = baseHTTPURI + "/vocab/" + eReference.getEContainingClass().getName() + "#"
				+ eReference.getName();
		String objectIdentifier = getQualifiedNameOrID(mdRefenrecedObject);

		String objectURI = baseHTTPURI + "/" + projectId + "/" + objectType + "/" + objectIdentifier;
//		String objectURI = baseHTTPURI + "/" + projectId + "/" + objectType + "/" + objectIdentifier
//				.replaceAll("\\n", "-").replaceAll(" ", "_").replaceAll("\\r", "-").replaceAll("\\s", "_");

		
		
		
		// triple indicating reference
		buffer.append("<");
		buffer.append(subjectURI);
		buffer.append(">");
		buffer.append(" ");
		buffer.append("<");
		buffer.append(predicateURI);
		buffer.append(">");
		buffer.append(" ");
		buffer.append("<");
		buffer.append(objectURI);
		buffer.append(">");
		buffer.append(" .");
		buffer.append("\n");
	}

	public static String getQualifiedNameOrID(Element element) {
		String qfOrID = null;
		if (element instanceof NamedElement) {
			NamedElement namedElement = (NamedElement) element;
			if (namedElement.getName().equals("")) {
				qfOrID = element.getID();
			} else {
				qfOrID = ((NamedElement) element).getQualifiedName();
				qfOrID = UriUtils.encodePath(qfOrID, "UTF-8");
				//qfOrID = ((NamedElement) element).getQualifiedName().replaceAll("\\n", "-").replaceAll(" ", "_");	
			}
		} else {
			qfOrID = element.getID();
		}
		return qfOrID;
	}

	public static Element getStereotypedElement(MDObject element) {
		if (element instanceof InstanceSpecification) {
			InstanceSpecification ins = (InstanceSpecification) element;
			return ins.getStereotypedElement();
		}
		return null;

	}

	public static List<Classifier> getStereotypeClassifier(MDObject element) {
		if (element instanceof InstanceSpecification) {
			InstanceSpecification ins = (InstanceSpecification) element;
			return ins.getClassifier();
		}
		return null;

	}

	public static String getQualifiedNameOrID(MDObject element) {
		String qfOrID = null;
		if (element instanceof NamedElement) {
			NamedElement namedElement = (NamedElement) element;
			if (namedElement.getName().equals("")) {
				qfOrID = element.getID();
			} else {
				qfOrID = ((NamedElement) element).getQualifiedName();
				qfOrID = UriUtils.encodePath(qfOrID, "UTF-8");
				//qfOrID = ((NamedElement) element).getQualifiedName().replaceAll("\\n", "-").replaceAll(" ", "_");
			}
		} else {
			qfOrID = element.getID();
		}
		return qfOrID;
	}

}
