package com.ath.wmb.genflows.wizard;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.ath.esqltool.domain.BAthFacadeProject;
import com.ath.esqltool.domain.BAthOrchestable;
import com.ath.esqltool.domain.BAthSpecificBo;
import com.ath.wmb.genflows.general.FacadeConstants;




public class FacadePageTwo extends WizardPage {
	//TODO terminar esta seccion del wizard para agregar especificos y generar los proyectos
	
	private Composite container;
	
	private Integer numberOrchestables = 0;

	private Button checkPassthrough;

	private Combo comboparticulars;
	private Button[] radiosParticular;
	private Group groupparticular;
	private Combo combobanks;
	
	
	private Group group;
	private Text codserviceText;
	private Button searchcntlbutton;
	
	private Button addParticularButton;
	private Button clearParticularButton;
	private org.eclipse.swt.widgets.List listDescSteps;
	
	private List<BAthOrchestable> listOrchestables = new ArrayList<BAthOrchestable>();
	private List<BAthSpecificBo> listSpecificsBo = new ArrayList<BAthSpecificBo>();
	
	private BAthFacadeProject facadeProject;

	public FacadePageTwo(ISelection selection) {
		super("Facade Specific Page");
		setTitle("Specifics");
		setDescription("Facade Wizard: Add Specific to Facade");

	}

	

	@Override
	public void createControl(Composite parent) {
		
		
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 1;
		
		

		checkPassthrough = new Button(container, SWT.CHECK);
		checkPassthrough.setEnabled(false);
		checkPassthrough.setText("Is Passthrough?");
		checkPassthrough.setSelection(true);
		checkPassthrough.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, true, 1, 1));
		checkPassthrough.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {

				
			}
		});

		

		groupparticular = new Group(container, SWT.NONE);
		groupparticular.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//groupfmg.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, true, 1, 1));
		groupparticular.setLayout(new GridLayout(4, false));

		Label label4 = new Label(groupparticular, SWT.NONE);
		label4.setText(FacadeConstants.MSG_BANK_LABEL);

		List<Object> listapps = null;
		

		setCombobanks(new Combo(groupparticular, SWT.READ_ONLY));
		getCombobanks().setBounds(50, 50, 150, 65);
		String arraybanks[] = {"BAVV", "BBOG", "BPOP", "BOCC"};

		getCombobanks().setItems(arraybanks);

		getCombobanks().addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				

			}
		});

		

		Label labelCntl = new Label(groupparticular, SWT.NONE);
		labelCntl.setText("Cod Service:");

		codserviceText = new Text(groupparticular, SWT.BORDER | SWT.SINGLE);
		codserviceText.setText("");

		

		addParticularButton = new Button(container, SWT.BUTTON1);
		addParticularButton.setText("Add Specific");

		addParticularButton.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, true, 1, 1));
		
		addParticularButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (event.widget == addParticularButton) {
					BAthSpecificBo athSpecificBo = new BAthSpecificBo();
					
					athSpecificBo.setName(facadeProject.getSrvName());
					athSpecificBo.setBankOrg(combobanks.getText());
					athSpecificBo.setCodService(codserviceText.getText());
					
					getListOrchestables().add(athSpecificBo); 
					getListSpecificsBo().add(athSpecificBo); 
					
					listDescSteps.removeAll();
					
					Iterator<BAthOrchestable> iterator = getListOrchestables().iterator();
					numberOrchestables = 1; 
					
					while (iterator.hasNext()) {
						BAthOrchestable bStepOrchestable = (BAthOrchestable) iterator.next();
						listDescSteps.add("Specific " + numberOrchestables + "->|"+ bStepOrchestable.getLongDescription());
						numberOrchestables++;
					}
					

					checkPassthrough.setSelection(true);
					
				}
			}
		});
		
		

		listDescSteps = new org.eclipse.swt.widgets.List(container, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		listDescSteps.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, true, 1, 1));
		
		//listSteps.setBounds(50, 50, 650, 65);
		
		listDescSteps.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		
		
		clearParticularButton = new Button(container, SWT.BUTTON1);
		clearParticularButton.setText("Clear");

		clearParticularButton.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, true, 1, 1));
		
		clearParticularButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (event.widget == clearParticularButton) {
					listOrchestables.clear();
					listSpecificsBo.clear();
					listDescSteps.removeAll();
					setNumberParticulars(0);
				}
			}
		});
		
		

		//setDefaultVisibility("DEFAULT");

		
		setControl(container);
		setPageComplete(true);
	}
	
	
	
	
	
	
	
	public void setDefaultVisibility(String typeChange) {
		if (typeChange == "DEFAULT") {

			checkPassthrough.setVisible(true);

			radiosParticular[0].setVisible(false);
			radiosParticular[1].setVisible(false);

			groupparticular.setVisible(false);
			getCombobanks().setVisible(false);
			comboparticulars.setVisible(false);

			group.setVisible(false);
			codserviceText.setVisible(false);
			searchcntlbutton.setVisible(false);


			addParticularButton.setVisible(true);
			clearParticularButton.setVisible(true);
			listDescSteps.setVisible(true);
		} else if (typeChange == "IMPLEMENTABLE") {
			radiosParticular[0].setVisible(true);
			radiosParticular[1].setVisible(true);

			groupparticular.setVisible(true);
			getCombobanks().setVisible(true);
			comboparticulars.setVisible(true);

			group.setVisible(false);
			codserviceText.setVisible(false);
			searchcntlbutton.setVisible(false);

			addParticularButton.setVisible(true);
			clearParticularButton.setVisible(true);
			listDescSteps.setVisible(true);

		} else if (typeChange == "NOT_IMPLEMENTABLE") {
			radiosParticular[0].setVisible(false);
			radiosParticular[1].setVisible(false);

			groupparticular.setVisible(false);
			getCombobanks().setVisible(false);
			comboparticulars.setVisible(false);

			group.setVisible(false);
			codserviceText.setVisible(false);
			searchcntlbutton.setVisible(false);

			addParticularButton.setVisible(true);
			clearParticularButton.setVisible(true);
			listDescSteps.setVisible(true);

		} else if (typeChange == "IS_FMG") {
			radiosParticular[0].setVisible(true);
			radiosParticular[1].setVisible(true);

			groupparticular.setVisible(true);
			getCombobanks().setVisible(true);
			comboparticulars.setVisible(true);

			group.setVisible(false);
			codserviceText.setVisible(false);
			searchcntlbutton.setVisible(false);
			
			addParticularButton.setVisible(true);
			clearParticularButton.setVisible(true);
			listDescSteps.setVisible(true);

		} else if (typeChange == "IS_CNTL") {
			radiosParticular[0].setVisible(true);
			radiosParticular[1].setVisible(true);

			groupparticular.setVisible(false);
			getCombobanks().setVisible(false);
			comboparticulars.setVisible(false);

			group.setVisible(true);
			codserviceText.setVisible(true);
			searchcntlbutton.setVisible(true);

			addParticularButton.setVisible(true);
			clearParticularButton.setVisible(true);
			listDescSteps.setVisible(true);

		}

	}
	
	
	
	


	

	public Button getCheckImpl() {
		return checkPassthrough;
	}

	public void setCheckImpl(Button checkImpl) {
		this.checkPassthrough = checkImpl;
	}

	public Button[] getRadiosParticular() {
		return radiosParticular;
	}

	public void setRadiosParticular(Button[] radiosParticularOrCntl) {
		this.radiosParticular = radiosParticularOrCntl;
	}

	public Group getGroupfmg() {
		return groupparticular;
	}

	public void setGroupfmg(Group groupfmg) {
		this.groupparticular = groupfmg;
	}

	public Group getGroupcntl() {
		return group;
	}

	public void setGroupcntl(Group groupcntl) {
		this.group = groupcntl;
	}

	public Text getCntlsearchText() {
		return codserviceText;
	} 

	public void setCntlsearchText(Text cntlsearchText) {
		this.codserviceText = cntlsearchText;
	}

	
	public org.eclipse.swt.widgets.List getListSteps() {
		return listDescSteps;
	}

	public void setListSteps(org.eclipse.swt.widgets.List listSteps) {
		this.listDescSteps = listSteps;
	}

	public Integer getNumberParticulars() {
		return numberOrchestables; 
	}

	public void setNumberParticulars(Integer numberSteps) {
		this.numberOrchestables = numberSteps;
	}


	public BAthFacadeProject getFacadeProject() {
		return facadeProject;
	}

	public void setFacadeProject(BAthFacadeProject ctrlProject) {
		this.facadeProject = ctrlProject;
	}



	public List<BAthOrchestable> getListStepsOrchestables() {
		return getListOrchestables();
	}



	public void setListStepsOrchestables(List<BAthOrchestable> listStepsOrchestables) {
		this.setListOrchestables(listStepsOrchestables);
	}



	public Combo getCombobanks() {
		return combobanks;
	}



	public void setCombobanks(Combo combobanks) {
		this.combobanks = combobanks;
	}



	public List<BAthSpecificBo> getListSpecificsBo() {
		return listSpecificsBo;
	}



	public void setListSpecificsBo(List<BAthSpecificBo> listSpecificsBo) {
		this.listSpecificsBo = listSpecificsBo;
	}



	public List<BAthOrchestable> getListOrchestables() {
		return listOrchestables;
	}



	public void setListOrchestables(List<BAthOrchestable> listOrchestables) {
		this.listOrchestables = listOrchestables;
	}
	
	
	
	
	
	
	
	
	
	
}
