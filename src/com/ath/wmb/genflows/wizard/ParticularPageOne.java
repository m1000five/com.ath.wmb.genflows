package com.ath.wmb.genflows.wizard;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.ath.esqltool.domain.BAthParticularProject;
import com.ath.wmb.genflows.general.AnalyzerFlow;
import com.ath.wmb.genflows.general.FacadeConstants;

public class ParticularPageOne extends WizardPage {

	private Composite container;

	private String namespace;

	private String domain;
	private String srvname;
	private String oprname;
	private String projectname;
	private String orgname;
	private String bankid;
	private String channel;

	private LinkedHashSet<String> setOperations = new LinkedHashSet<String>();
	private LinkedHashSet<String> setOthersNamespaces = new LinkedHashSet<String>();

	private Combo comboDomains;
	private Combo comboChannels;
	private Combo comboOperations;

	private Text srvnameText;
	private Text oprnameText;
	private Text orgText;
	private Text bankidText;
	private Text textWSDLLocation;

	private Button checkCustomOperation;

	private Label labelProjName;
	private Label labelProjValue;

	private Button mButtonSelection;
	private Button mButtonWsdl;

	private BAthParticularProject particularProject;

	private Document documentWSDL = null;

	private String nameOfWSDL;

	private String wsdlBinding;

	private String wsdlPort;

	private String wsdlSvcPort;

	public ParticularPageOne(ISelection selection) {
		super("Specific Basic Page");

		setTitle("Basic");
		setDescription("Specific Wizard: Basic data for the new Specific");

		namespace = "";
		domain = "";
		srvname = "";
		oprname = "";
		projectname = "";
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;

		container.setLayout(layout);

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = GridData.FILL;

		Label labelWsdl = new Label(container, SWT.NONE);
		labelWsdl.setText(FacadeConstants.WSDL_LABEL); 

		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench == null ? null : workbench.getActiveWorkbenchWindow();
		IWorkbenchPage activePage = window == null ? null : window.getActivePage();

		IEditorPart editor = activePage == null ? null : activePage.getActiveEditor();
		IEditorInput input = editor == null ? null : editor.getEditorInput();
		

		IProject project = input.getAdapter(IProject.class);
		if (project == null) {
			IResource resource = input.getAdapter(IResource.class);
			if (resource != null) {
				project = resource.getProject();

			}
		} 
		
		IFile ifile = (IFile)Platform.getAdapterManager().getAdapter(input, IFile.class);
		File flowFile = ifile.getRawLocation().makeAbsolute().toFile();
		
//		/CardPswdAssignmentSvcFcdWs
//		C:\Users\milton.vega\runtime-EclipseApplication\CardPswdAssignmentSvcFcdWs\com\ath\services\customers\CardPswdAssignmentSvcFcdWs_REQ.msgflow
//		CardPswdAssignmentSvcFcdWs/com/ath/services/customers/CardPswdAssignmentSvcFcdWs_REQ.msgflow
		
		System.out.println(project.getFullPath());
		System.out.println(flowFile.getAbsolutePath());
		System.out.println(input.getToolTipText()); 
		
		if (project.getFullPath().toString().indexOf("FcdWs") != -1 && input.getToolTipText().indexOf(".msgflow") != -1) {
			System.out.println("Es flujo");
			
			
		} else {
			return;
		}
		
		AnalyzerFlow analyzerFlow = new AnalyzerFlow();
		try {
			
			analyzerFlow.init(flowFile);
			analyzerFlow.parseFacadeFlow();
			
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
		
		

		setControl(container);
		setPageComplete(false);

	}

	public static IProject getCurrentProject() {
		ISelectionService selectionService = Workbench.getInstance().getActiveWorkbenchWindow().getSelectionService();

		ISelection selection = selectionService.getSelection(); 

		IProject project = null;
		if (selection instanceof IStructuredSelection) {
			Object element = ((IStructuredSelection) selection).getFirstElement();

			if (element instanceof IResource) {
				project = ((IResource) element).getProject();
			}
		}
		return project;
	}

	public void setParticularProject(BAthParticularProject particularProject) {
		this.particularProject = particularProject;
	}

}
