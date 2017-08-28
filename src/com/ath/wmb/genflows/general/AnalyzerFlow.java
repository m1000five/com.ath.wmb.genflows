package com.ath.wmb.genflows.general;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.StringTokenizer;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class AnalyzerFlow {

	private LinkedHashSet<String> setNamespaces = new LinkedHashSet<String>();
	private LinkedHashSet<String> setAttrSoapNode = new LinkedHashSet<String>();

	private String namespace;
	private String oprname;
	private String wsdlSvcPort;
	private Document document;
	private DocumentBuilderFactory dbf;
	private XPath xpath;
	private boolean facadeNode;
	private String wsdlRelativePath;
	private String wsdlBinding;
	private String wsdlPort;
	private String portType;
	private String context;
	private String appSrvName;
	private String strDomain;
	private String bankId;
	private String channel;

	public AnalyzerFlow() {
		super();
		dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true); // This is really important, without it that XPath does not work

		xpath = XPathFactory.newInstance().newXPath();
	}

	public LinkedHashSet<String> getNamespaces() {
		return getSetNamespaces();
	}

	public void init(File inputSource) throws ParserConfigurationException, SAXException, IOException {
		dbf.setNamespaceAware(true); // This is really important, without it that XPath does not work
		DocumentBuilder db = dbf.newDocumentBuilder();
		setDocument(db.parse(inputSource)); // inputSource, inputStream or file which contains your XML.
	}

	public void parseFacadeFlow() throws XPathExpressionException {

		NamespaceContext ecore = new NamespaceContextMap("ecore", "http://www.eclipse.org/emf/2002/Ecore", "xmi",
				"http://www.omg.org/XMI");

		xpath.setNamespaceContext(ecore);

		/// ecore:EPackage/@xmlns:ComIbmSOAPInput.msgnode
		// xmlns:ComIbmSOAPInput.msgnode="ComIbmSOAPInput.msgnode"
		//// ecore:EPackage/eClassifiers/composition/nodes

		Node currentSoapNode = (Node) xpath.evaluate(
				"//ecore:EPackage/eClassifiers/composition/nodes[@xmi:type='ComIbmSOAPInput.msgnode:FCMComposite_1']",
				getDocument(), XPathConstants.NODE);

		if (currentSoapNode != null && currentSoapNode.getAttributes() != null) {
			setFacadeNode(true);
			NamedNodeMap attributes = currentSoapNode.getAttributes();
			for (int j = 0; j < attributes.getLength(); j++) {
				Node attr = attributes.item(j);

				System.out.println("NODE->attr->" + attr.getNodeName() + " " + attr.getNodeValue());
				
//				NODE->attr->location 112,148
//				NODE->attr->selectedBinding CardPswdAssignmentSvcBinding
//				NODE->attr->selectedPort CardPswdAssignmentSvcPort
//				NODE->attr->selectedPortType CardPswdAssignmentSvc
//				NODE->attr->targetNamespace urn://grupoaval.com/customers/v1/
//				NODE->attr->urlSelector /Customers/Services/CardPswdAssignmentSvc
//				NODE->attr->useHTTPTransport true
//				NODE->attr->wsdlFileName CardPswdAssignmentSvc.wsdl
//				NODE->attr->xmi:id FCMComposite_1_3
//				NODE->attr->xmi:type ComIbmSOAPInput.msgnode:FCMComposite_1
				
				if (attr.getNodeName().equals("targetNamespace")) {
					namespace = attr.getNodeValue();
				} else if (attr.getNodeName().equals("wsdlFileName")) {
					setWsdlFileName(attr.getNodeValue());
				} else if (attr.getNodeName().equals("selectedBinding")) {
					wsdlBinding = attr.getNodeValue();
				} else if (attr.getNodeName().equals("selectedPort")) {
					wsdlPort = attr.getNodeValue();
				} else if (attr.getNodeName().equals("selectedPortType")) {
					setPortType(attr.getNodeValue());
				} else if (attr.getNodeName().equals("urlSelector")) {
					setContext(attr.getNodeValue());
					
					if (getContext() != null && getContext().length() > 0) {
						StringTokenizer tokenizer = new StringTokenizer(getContext(), "/");
						setStrDomain(((String) tokenizer.nextElement()).toLowerCase());
					}
					
				}  

				getSetAttrSoapNode().add(attr.getNodeValue());
			}

		} else {
			setFacadeNode(false);
			
			return;
		}
		
		
//		<eStructuralFeatures xmi:type="ecore:EAttribute" xmi:id="Property.UDP_Application" name="UDP_Application" 
//		defaultValueLiteral="CardPswdAssignmentSvc">
		
		Node udpAppNode = (Node) xpath.evaluate(
				"//ecore:EPackage/eClassifiers/eStructuralFeatures[@name='UDP_Application']",
				getDocument(), XPathConstants.NODE);

		if (udpAppNode != null && udpAppNode.getAttributes() != null) {
			setFacadeNode(true);
			NamedNodeMap attributes = udpAppNode.getAttributes();
			for (int j = 0; j < attributes.getLength(); j++) {
				Node attr = attributes.item(j);
				System.out.println("NODE->attr->" + attr.getNodeName() + " " + attr.getNodeValue());
				if (attr.getNodeName().equals("defaultValueLiteral")) {
					setAppSrvName(attr.getNodeValue());
				}  
			}
		}
		
		Node udpBankidNode = (Node) xpath.evaluate(
				"//ecore:EPackage/eClassifiers/eStructuralFeatures[@name='UDP_BankId']",
				getDocument(), XPathConstants.NODE);

		if (udpBankidNode != null && udpBankidNode.getAttributes() != null) {
			setFacadeNode(true);
			NamedNodeMap attributes = udpBankidNode.getAttributes();
			for (int j = 0; j < attributes.getLength(); j++) {
				Node attr = attributes.item(j);
				System.out.println("NODE->attr->" + attr.getNodeName() + " " + attr.getNodeValue());
				if (attr.getNodeName().equals("defaultValueLiteral")) {
					setBankId(attr.getNodeValue());
				}  
			}
		}
		
		
		Node udpChannel = (Node) xpath.evaluate(
				"//ecore:EPackage/eClassifiers/eStructuralFeatures[@name='UDP_Channel']",
				getDocument(), XPathConstants.NODE);

		if (udpChannel != null && udpChannel.getAttributes() != null) {
			setFacadeNode(true);
			NamedNodeMap attributes = udpChannel.getAttributes();
			for (int j = 0; j < attributes.getLength(); j++) {
				Node attr = attributes.item(j);
				System.out.println("NODE->attr->" + attr.getNodeName() + " " + attr.getNodeValue());
				if (attr.getNodeName().equals("defaultValueLiteral")) {
					setChannel(attr.getNodeValue());
				}  
			}
		}


	}

	public LinkedHashSet<String> getSetNamespaces() {
		return setNamespaces;
	}

	public void setSetNamespaces(LinkedHashSet<String> setNamespaces) {
		this.setNamespaces = setNamespaces;
	}



	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getOprname() {
		return oprname;
	}

	public void setOprname(String oprname) {
		this.oprname = oprname;
	}

	public String getWsdlBinding() {
		return wsdlBinding;
	}

	public void setWsdlBinding(String wsdlBinding) {
		this.wsdlBinding = wsdlBinding;
	}

	public String getWsdlPort() {
		return wsdlPort;
	}

	public void setWsdlPort(String wsdlPort) {
		this.wsdlPort = wsdlPort;
	}

	public String getWsdlSvcPort() {
		return wsdlSvcPort;
	}

	public void setWsdlSvcPort(String wsdlSvcPort) {
		this.wsdlSvcPort = wsdlSvcPort;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public boolean isFacadeNode() {
		return facadeNode;
	}

	public void setFacadeNode(boolean facadeNode) {
		this.facadeNode = facadeNode;
	}

	public LinkedHashSet<String> getSetAttrSopNode() {
		return getSetAttrSoapNode();
	}

	public void setSetAttrSopNode(LinkedHashSet<String> setAttrSopNode) {
		this.setSetAttrSoapNode(setAttrSopNode);
	}

	public LinkedHashSet<String> getSetAttrSoapNode() {
		return setAttrSoapNode;
	}

	public void setSetAttrSoapNode(LinkedHashSet<String> setAttrSoapNode) {
		this.setAttrSoapNode = setAttrSoapNode;
	}

	public String getWsdlFileName() {
		return getWsdlRelativePath();
	}

	public void setWsdlFileName(String wsdlFileName) {
		this.setWsdlRelativePath(wsdlFileName);
	}

	public String getPortType() {
		return portType;
	}

	public void setPortType(String portType) {
		this.portType = portType;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getWsdlRelativePath() {
		return wsdlRelativePath;
	}

	public void setWsdlRelativePath(String wsdlRelativePath) {
		this.wsdlRelativePath = wsdlRelativePath;
	}

	public String getAppSrvName() {
		return appSrvName;
	}

	public void setAppSrvName(String appSrvName) {
		this.appSrvName = appSrvName;
	}

	public String getStrDomain() {
		return strDomain;
	}

	public void setStrDomain(String strDomain) {
		this.strDomain = strDomain;
	}

	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

}
