package com.ath.wmb.genflows.wizard;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.IWizardPage;
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
import org.w3c.dom.Document;

import com.ath.esqltool.domain.BAthFacadeProject;
import com.ath.esqltool.util.AnalyzerWsdl;
import com.ath.wmb.genflows.Activator;
import com.ath.wmb.genflows.general.FacadeConstants;

public class FacadePageOne extends WizardPage {

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
	private HashMap<String, String> mapOpMsgs = new HashMap<String, String>();
	private HashMap<String, String> mapMsgElements = new HashMap<String, String>();

	private Combo comboDomains;
	private Combo comboChannels;
	private Combo comboOperations;

	private Text srvnameText;
	private Text oprnameText;
	private Text orgText;
	private Text bankidText;
	private Text textWSDLLocation;

	private Button checkAutoIdCntl;
	private Button checkCustomOperation;

	private Label labelProjName;
	private Label labelProjValue;

	private Button mButtonSelection;
	private Button mButtonWsdl;

	private BAthFacadeProject facadeProject;

	private Document documentWSDL = null;

	private String nameOfWSDL;

	private String wsdlBinding;

	private String wsdlPort;

	private String wsdlSvcPort;

	public FacadePageOne(ISelection selection) {
		super("Facade Basic Page");

		setTitle("Basic");
		setDescription("Facade Wizard: Basic data for the new Facade");

		namespace = "urn://grupoaval.com/[domain]/v1/";

		domain = "";
		setChannel("");
		srvname = "";
		oprname = "";
		setOrgname("");
		setBankid("");
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

		Group group = new Group(container, SWT.NONE);
		group.setLayout(new GridLayout(2, false));

		mButtonWsdl = new Button(group, SWT.NONE);
		mButtonWsdl.setEnabled(true);
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
				textWSDLLocation.setText(path);
				try {

					File inputSource = new File(path);

					setNameOfWSDL(inputSource.getName());
					if (getNameOfWSDL().indexOf(".") != -1) {
						srvname = getNameOfWSDL().substring(0, getNameOfWSDL().indexOf("."));
						srvnameText.setText(srvname);
					}

					AnalyzerWsdl analyzerWsdl = new AnalyzerWsdl();
					analyzerWsdl.parse(inputSource);
					setDocumentWSDL(analyzerWsdl.getDocument());

					namespace = analyzerWsdl.getNamespace();
					setOthersNamespaces = analyzerWsdl.getNamespaces();
					oprname = analyzerWsdl.getOprname();
					oprnameText.setText(oprname);
					setOperations = analyzerWsdl.getSetOperations();
					
					mapOpMsgs = analyzerWsdl.getMapOpMsgs();
					mapMsgElements = analyzerWsdl.getMapMsgElements();

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
					setWsdlBinding(analyzerWsdl.getWsdlBinding());
					setWsdlPort(analyzerWsdl.getWsdlPort());
					setWsdlSvcPort(analyzerWsdl.getWsdlSvcPort());
				} catch (Exception e2) {
					e2.printStackTrace();
				}

			}
		});

		textWSDLLocation = new Text(group, SWT.SINGLE | SWT.BORDER);
		textWSDLLocation.setEditable(false);
		textWSDLLocation.setLayoutData(gd);

		Label labelOperation = new Label(container, SWT.NONE);
		labelOperation.setText(FacadeConstants.OP_NAME_LABEL);

		Group groupOperation = new Group(container, SWT.NONE);
		groupOperation.setLayout(new GridLayout(3, false));

		comboOperations = (new Combo(groupOperation, SWT.READ_ONLY));
		comboOperations.setBounds(50, 50, 150, 65);

		checkCustomOperation = new Button(groupOperation, SWT.CHECK);
		checkCustomOperation.setText("Custom Operation");
		checkCustomOperation.setEnabled(false);
		checkCustomOperation.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {

			}
		});

		oprnameText = new Text(groupOperation, SWT.BORDER | SWT.SINGLE);
		oprnameText.setEnabled(false);
		oprnameText.setText(oprname);
		oprnameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label label5 = new Label(container, SWT.NONE);
		label5.setText(FacadeConstants.SERVICE_NAME_LABEL);
		srvnameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		srvnameText.setText(srvname);
		srvnameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label label4 = new Label(container, SWT.NONE);
		label4.setText(FacadeConstants.DOMAIN_LABEL);

		comboDomains = new Combo(container, SWT.READ_ONLY);
		comboDomains.setBounds(50, 50, 150, 65);

		String arraydomains[] = { "accounts", "customers", "inquiries", "payments", "prodschnsmngt", "transfers" };

		comboDomains.setItems(arraydomains);
		comboDomains.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				domain = comboDomains.getText();
			}
		});

		Label labelChannelDom = new Label(container, SWT.NONE);
		labelChannelDom.setText(FacadeConstants.CHANNEL_LABEL);

		comboChannels = new Combo(container, SWT.READ_ONLY);
		comboChannels.setBounds(50, 50, 150, 65);

		String arrayChannels[] = { "PB", "MB", "BM", "BABN", "BBS", "AVP", "OFICINAS", "NA", "PPA", "K7", "PORTAL", "OFVV",
				"INT", "PBB", "IVR", "CB" };

		comboChannels.setItems(arrayChannels);
		comboChannels.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {

				setChannel(comboChannels.getText());

			}
		});

		Label label7 = new Label(container, SWT.NONE);
		label7.setText(FacadeConstants.MSG_BANK_LABEL);

		orgText = new Text(container, SWT.BORDER | SWT.SINGLE);
		orgText.setText(getOrgname());
		orgText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Label label8 = new Label(container, SWT.NONE);
		label8.setText(FacadeConstants.MSG_BANKID_LABEL);
		bankidText = new Text(container, SWT.BORDER | SWT.SINGLE);
		bankidText.setText(getBankid());
		bankidText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label labelEmpty = new Label(container, SWT.NONE);
		Label labelEmpty2 = new Label(container, SWT.NONE);

		labelProjName = new Label(container, SWT.NONE);
		labelProjName.setText("Project Name");
		labelProjName.setVisible(false);
		labelProjValue = new Label(container, SWT.NONE);
		labelProjValue.setText("");
		labelProjValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		MyModifyListener listener = new MyModifyListener();
		//
		// idreqText.addModifyListener(listener);
		srvnameText.addModifyListener(listener);
		oprnameText.addModifyListener(listener);
		comboDomains.addModifyListener(listener);
		comboChannels.addModifyListener(listener);
		orgText.addModifyListener(listener);
		bankidText.addModifyListener(listener);
		//

		// required to avoid an error in the system
		setControl(container);
		setPageComplete(false);

	}

	private class MyModifyListener implements ModifyListener {
		public void modifyText(ModifyEvent e) {
			setPageComplete(false);

			oprname = oprnameText.getText().trim();
			if (StringUtils.isBlank(oprname)) {
				setErrorMessage("ERROR: Operation Name Null");
				return;
			}

			srvname = srvnameText.getText().trim();
			if (StringUtils.isBlank(srvname)) {
				setErrorMessage("ERROR: Service Name Null");
				return;
			}

			projectname = srvname + "FcdWs";

			labelProjName.setText("Project Name:");
			labelProjName.setVisible(true);
			labelProjValue.setText(projectname);
			labelProjValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			domain = comboDomains.getText();
			if (StringUtils.isBlank(domain)) {
				setErrorMessage("ERROR: Domain Null");
				return;
			}

			setChannel(comboChannels.getText());
			if (StringUtils.isBlank(getChannel())) {
				setErrorMessage("ERROR: Channel Null");
				return;
			}

			facadeProject.setDomain(getDomain());
			facadeProject.setSrvName(getSrvname());
			facadeProject.setOprName(getOprname());

			//
			// int length = projectname.length();
			//
			setOrgname(orgText.getText().trim());

			setBankid(bankidText.getText().trim());
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

	public String getSrvname() {
		return srvname;
	}

	public void setSrvname(String srvname) {
		this.srvname = srvname;
	}

	public String getOprname() {
		return oprname;
	}

	public void setOprname(String oprname) {
		this.oprname = oprname;
	}

	public Button getCheckAutoIdCntl() {
		return checkAutoIdCntl;
	}

	public void setCheckAutoIdCntl(Button checkAutoIdCntl) {
		this.checkAutoIdCntl = checkAutoIdCntl;
	}

	public String getProjectname() {
		return projectname;
	}

	public void setProjectname(String projectname) {
		this.projectname = projectname;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FacadePageOne [domain=");
		builder.append(domain);
		builder.append(", channel=");
		builder.append(getChannel());
		builder.append(", srvname=");
		builder.append(srvname);
		builder.append(", oprname=");
		builder.append(oprname);
		builder.append(", orgname=");
		builder.append(getOrgname());
		builder.append(", bankid=");
		builder.append(getBankid());
		builder.append("]");
		return builder.toString();
	}

	@Override
	public IWizardPage getNextPage() {
		ILog log = Activator.getDefault().getLog();
		log.log(new Status(IStatus.INFO, "com.ath.wmb.genflows", this.toString()));
		return super.getNextPage();
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
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

	public Document getDocumentWSDL() {
		return documentWSDL;
	}

	public void setDocumentWSDL(Document documentWSDL) {
		this.documentWSDL = documentWSDL;
	}

	public String getNameOfWSDL() {
		return nameOfWSDL;
	}

	public void setNameOfWSDL(String nameOfWSDL) {
		this.nameOfWSDL = nameOfWSDL;
	}

	public LinkedHashSet<String> getSetOthersNamespaces() {
		return setOthersNamespaces;
	}

	public void setSetOthersNamespaces(LinkedHashSet<String> setOthersNamespaces) {
		this.setOthersNamespaces = setOthersNamespaces;
	}

	public LinkedHashSet<String> getSetOperations() {
		return setOperations;
	}

	public void setSetOperations(LinkedHashSet<String> setOperations) {
		this.setOperations = setOperations;
	}

	public Button getCheckCustomOperation() {
		return checkCustomOperation;
	}

	public void setCheckCustomOperation(Button checkCustomOperation) {
		this.checkCustomOperation = checkCustomOperation;
	}

	public Combo getComboOperations() {
		return comboOperations;
	}

	public void setComboOperations(Combo comboOperations) {
		this.comboOperations = comboOperations;
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

	public BAthFacadeProject getFacadeProject() {
		return facadeProject;
	}

	public void setFacadeProject(BAthFacadeProject facadeProject) {
		this.facadeProject = facadeProject;
	}

	public HashMap<String, String> getMapOpMsgs() {
		return mapOpMsgs;
	}

	public void setMapOpMsgs(HashMap<String, String> mapOpMsgs) {
		this.mapOpMsgs = mapOpMsgs;
	}

	public HashMap<String, String> getMapMsgElements() {
		return mapMsgElements;
	}

	public void setMapMsgElements(HashMap<String, String> mapMsgElements) {
		this.mapMsgElements = mapMsgElements;
	}

}
