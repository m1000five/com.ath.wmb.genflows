package com.ath.wmb.genflows.wizard;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashSet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
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
import com.ath.wmb.genflows.general.AnalyzerWsdl;
import com.ath.wmb.genflows.general.ParticularConstants;
import com.ath.wmb.genflows.handlers.ErrorHandlerInterface;
import com.ath.wmb.genflows.general.ParticularConstants;

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
	private String nameOfWSDL;
	private String wsdlBinding;
	private String wsdlPort;
	private String wsdlSvcPort;
	private String nameOfSelectWSDL;
	private String facadeProjectName;

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

	private IFile wsdlFile;
	
	private Document facadeWsdl;
	private Document selectWSDL;

	private StringBuffer errorMsg = new StringBuffer();

	private ErrorHandlerInterface customHandlerInterface;

	private Label labelSpecificWsdl;

	public ParticularPageOne(ISelection selection) {
		super("Specific Basic Page");

		setTitle("Basic");
		setDescription("Specific Wizard: Basic data for the new Specific");

		namespace = "";
		domain = "";
		orgname = "";
		channel = "";
		srvname = "";
		oprname = "";
		projectname = "";
		bankid = "";
	}

	@Override
	public void createControl(Composite parent) {

		processFlow();

		if (wsdlFile != null) {
			processWsdl();
		}

		container = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;

		container.setLayout(layout);

		Label label2 = new Label(container, SWT.NONE); 
		label2.setText(ParticularConstants.WSDL_LABEL);

		Group group = new Group(container, SWT.NONE);
		group.setLayout(new GridLayout(3, false));

		Button checkPassthrough = new Button(group, SWT.CHECK);
		checkPassthrough.setText(ParticularConstants.PASSTHROUGH);
		checkPassthrough.setSelection(true);

		checkPassthrough.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {

				Button btn = (Button) event.getSource();
				System.out.println(btn.getSelection());
				if (btn.getSelection()) {
					mButtonWsdl.setEnabled(false);
				} else {
					mButtonWsdl.setEnabled(true);
				}

			}
		});

		mButtonWsdl = new Button(group, SWT.NONE);
		mButtonWsdl.setEnabled(false);
		mButtonWsdl.setText("...");
		mButtonWsdl.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				FileDialog dlg = new FileDialog(mButtonWsdl.getShell(), SWT.OPEN);
				dlg.setText("Open");
				String path = dlg.open();
				if (path == null || path.length() == 0 || path.indexOf("wsdl") == -1) {
					return;
				}
				labelSpecificWsdl.setText(path);
				try {

					File inputSource = new File(path);

					nameOfSelectWSDL = (inputSource.getName());
					if (nameOfSelectWSDL.indexOf(".") != -1) {
						setSrvname(nameOfSelectWSDL.substring(0, nameOfSelectWSDL.indexOf(".")));
						srvnameText.setText(getSrvname());
					}

					AnalyzerWsdl analyzerWsdl = new AnalyzerWsdl();
					analyzerWsdl.parse(inputSource);
					selectWSDL = (analyzerWsdl.getDocument());

					namespace = analyzerWsdl.getNamespace();
					setOthersNamespaces = analyzerWsdl.getNamespaces();
					oprname = analyzerWsdl.getOprname();
					oprnameText.setText(oprname);
					setOperations = analyzerWsdl.getSetOperations();

					String arrayOperations[];
					if (setOperations != null && !setOperations.isEmpty()) {
						arrayOperations = new String[setOperations.size()];
						Iterator<String> iterator = setOperations.iterator();
						int i = 0;
						while (iterator.hasNext()) {
							String next = iterator.next();
							arrayOperations[i] = next;
							i++;
						}
					} else {
						arrayOperations = new String[1];
						arrayOperations[0] = "";
					}
					comboOperations.setItems(arrayOperations);
					if (oprname != null) {
						comboOperations.setText(oprname);
					}
				} catch (Exception e2) {
					e2.printStackTrace();
				}

			}
		});
		
		labelSpecificWsdl = new Label(group, SWT.NONE);
		labelSpecificWsdl.setText("");
		labelSpecificWsdl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label label5 = new Label(container, SWT.NONE);
		label5.setText(ParticularConstants.SERVICE_NAME_LABEL);
		srvnameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		srvnameText.setText(getSrvname());
		srvnameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label labelOperation = new Label(container, SWT.NONE);
		labelOperation.setText(ParticularConstants.OP_NAME_LABEL);

		comboOperations = (new Combo(container, SWT.READ_ONLY));
		comboOperations.setBounds(50, 50, 150, 65);

		String arrayOperations[];
		if (setOperations != null && !setOperations.isEmpty()) {
			arrayOperations = new String[setOperations.size()];
			Iterator<String> iterator = setOperations.iterator();
			int i = 0;
			while (iterator.hasNext()) {
				String next = iterator.next();
				arrayOperations[i] = next;
				i++;
			}
		} else {
			arrayOperations = new String[1];
			arrayOperations[0] = "";
		}
		comboOperations.setItems(arrayOperations);
		if (oprname != null) {
			comboOperations.setText(oprname);
		}

		Label label7 = new Label(container, SWT.NONE);
		label7.setText(ParticularConstants.MSG_BANK_LABEL);

		orgText = new Text(container, SWT.BORDER | SWT.SINGLE);
		orgText.setText(orgname);
		orgText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));


		Label label4 = new Label(container, SWT.NONE);
		label4.setText(ParticularConstants.DOMAIN_LABEL);

		comboDomains = new Combo(container, SWT.READ_ONLY);
		comboDomains.setBounds(50, 50, 150, 65);

		String arraydomains[] = { "accounts", "customers", "payments" };

		comboDomains.setItems(arraydomains);
		comboDomains.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				domain = comboDomains.getText();
			}
		});

		if (domain != null && domain.length() > 0) {
			comboDomains.setText(domain);
		}

		Label labelChannelDom = new Label(container, SWT.NONE);
		labelChannelDom.setText(ParticularConstants.CHANNEL_LABEL);

		comboChannels = new Combo(container, SWT.READ_ONLY);
		comboChannels.setBounds(50, 50, 150, 65);

		String arrayIfxDomains[] = { "PB", "BM", "BABN" };

		comboChannels.setItems(arrayIfxDomains);
		comboChannels.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {

				channel = (comboChannels.getText());

			}
		});

		if (channel != null && channel != "") {
			comboChannels.setText(channel);
		}

		Label label8 = new Label(container, SWT.NONE);
		label8.setText(ParticularConstants.MSG_BANKID_LABEL);
		bankidText = new Text(container, SWT.BORDER | SWT.SINGLE);
		bankidText.setText(bankid);
		bankidText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		labelProjName = new Label(container, SWT.NONE);
		labelProjName.setText("Project Specific Name:");
		labelProjValue = new Label(container, SWT.NONE);
		labelProjValue.setText("");
		labelProjValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		
		

		
		if (errorMsg.length() > 0) {
			MessageDialog.openInformation(getShell(), "Specific Generation", errorMsg.toString());
		}

		MyModifyListener listener = new MyModifyListener();

		srvnameText.addModifyListener(listener);
		comboDomains.addModifyListener(listener);
		comboChannels.addModifyListener(listener);
		orgText.addModifyListener(listener);
		bankidText.addModifyListener(listener);

		setControl(container);
		setPageComplete(false);

	}

	private class MyModifyListener implements ModifyListener {
		public void modifyText(ModifyEvent e) {
			setPageComplete(false);

			setSrvname(srvnameText.getText().trim());
			if (StringUtils.isBlank(getSrvname())) {
				setErrorMessage("ERROR: Service Name Null");
				return;
			}

			setDomain(comboDomains.getText());
			if (StringUtils.isBlank(getDomain())) {
				setErrorMessage("ERROR: Domain Null");
				return;
			}

			setChannel((comboChannels.getText()));
			if (StringUtils.isBlank(getChannel())) {
				setErrorMessage("ERROR: Channel Null");
				return;
			}

			setOrgname((orgText.getText().trim()));
			if (StringUtils.isBlank(getOrgname())) {
				setErrorMessage("ERROR: ORG Name Null");
				return;
			}

			setProjectname(getSrvname() + "_" + getOrgname());

			labelProjName.setText("Project Name:");
			labelProjName.setVisible(true);
			labelProjValue.setText(getProjectname());
			labelProjValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			particularProject.setDomain(getDomain());
			particularProject.setSrvName(getSrvname());
			particularProject.setOprName(getOprname());

			//
			// int length = projectname.length();
			//

			setBankid((bankidText.getText().trim()));
			//
			// if (length > 60) {
			// setErrorMessage("The length of Project Name is very long");
			// } else {
			// setErrorMessage(null);
			// }

			setErrorMessage(null);

			setPageComplete(true);

		}
	}

	private void processWsdl() {
		AnalyzerWsdl analyzerWsdl = new AnalyzerWsdl();

		try {
			analyzerWsdl.parse(wsdlFile.getRawLocation().makeAbsolute().toFile());

			facadeWsdl = analyzerWsdl.getDocument();

			namespace = analyzerWsdl.getNamespace();
			setOthersNamespaces = analyzerWsdl.getNamespaces();

			oprname = analyzerWsdl.getOprname();

			setOperations = analyzerWsdl.getSetOperations();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void processFlow() {

		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench == null ? null : workbench.getActiveWorkbenchWindow();
		IWorkbenchPage activePage = window == null ? null : window.getActivePage();

		IEditorPart editor = activePage == null ? null : activePage.getActiveEditor();
		IEditorInput input = editor == null ? null : editor.getEditorInput();

		if (input == null) {
			errorMsg.append("Please select the initial Facade Flow.");
			return;
		}
		//TODO revisar esta logica de project 
		IProject project = input.getAdapter(IProject.class);
		if (project == null) {
			IResource resource = input.getAdapter(IResource.class);
			if (resource != null) {
				project = resource.getProject();
			}

			try {
				processContainer(project);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}

		IFile ifile = (IFile) Platform.getAdapterManager().getAdapter(input, IFile.class);
		File flowFile = ifile.getRawLocation().makeAbsolute().toFile();

		// /CardPswdAssignmentSvcFcdWs
		// C:\Users\milton.vega\runtime-EclipseApplication\CardPswdAssignmentSvcFcdWs\com\ath\services\customers\CardPswdAssignmentSvcFcdWs_REQ.msgflow
		// CardPswdAssignmentSvcFcdWs/com/ath/services/customers/CardPswdAssignmentSvcFcdWs_REQ.msgflow

		System.out.println(project.getFullPath());
		System.out.println(flowFile.getAbsolutePath());
		System.out.println(input.getToolTipText());

		if (project.getFullPath().toString().indexOf("FcdWs") != -1
				&& input.getToolTipText().indexOf(".msgflow") != -1) {
			System.out.println("Es flujo");
		} else {

			errorMsg.append("The resource: ");
			if (input.getToolTipText().length() > 0) {
				errorMsg.append(input.getToolTipText());
			} else {
				errorMsg.append(project.getFullPath().toString());
			}
			errorMsg.append(
					" - Is Not a Flow of Facade. Please select the initial Facade Flow.");

			return;
		}

		AnalyzerFlow analyzerFlow = new AnalyzerFlow();
		try {

			analyzerFlow.init(flowFile);
			analyzerFlow.parseFacadeFlow();

			oprname = analyzerFlow.getOprname();
			domain = analyzerFlow.getStrDomain();
			srvname = analyzerFlow.getAppSrvName();
			channel = analyzerFlow.getChannel();
			bankid = analyzerFlow.getBankId();

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

	}

	// FILE------->.project
	// FILE------->20.GCAM.FO.73.Categorizacion_CardPswdAssignment.sql
	// FILE------->20.GCAM.FO.73.Reverso_Categorizacion_CardPswdAssignment.sql
	// FILE------->CardPswdAssignmentSvc.wsdl
	// FILE------->CardPswdAssignmentSvc.xml
	// FILE------->Colas_MigracionBUS_CardPswdAssignment.mq
	// FILE------->Reverso_Colas_MigracionBUS_CardPswdAssignment.mq
	// FILE------->build.xml
	// FILE------->CardPswdAssignmentSvcFcdWs_REQ.esql
	// FILE------->CardPswdAssignmentSvcFcdWs_REQ.msgflow
	// FILE------->CardPswdAssignmentSvcFcdWs_RES.msgflow

	void processContainer(IContainer container) throws CoreException {
		IResource[] members = container.members();

		for (IResource member : members) {
			if (member instanceof IContainer) {
				processContainer((IContainer) member);
			} else if (member instanceof IFile) {
				System.out.println("FILE------->" + member.getName());
				if (member.getName().indexOf(".wsdl") != -1 || member.getName().indexOf(".WSDL") != -1) {
					wsdlFile = (IFile) member;
					break;
				}
			}
		}
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

	public String getSrvname() {
		return srvname;
	}

	public void setSrvname(String srvname) {
		this.srvname = srvname;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getOprname() {
		return oprname;
	}

	public void setOprname(String oprname) {
		this.oprname = oprname;
	}

	public String getProjectname() {
		return projectname;
	}

	public void setProjectname(String projectname) {
		this.projectname = projectname;
	}

	public String getOrgname() {
		return orgname;
	}

	public void setOrgname(String orgname) {
		this.orgname = orgname;
	}

	public String getBankid() {
		return bankid;
	}

	public void setBankid(String bankid) {
		this.bankid = bankid;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public void setErrorhandler(ErrorHandlerInterface customHandlerInterface) {
		this.customHandlerInterface = customHandlerInterface;

	}

}
