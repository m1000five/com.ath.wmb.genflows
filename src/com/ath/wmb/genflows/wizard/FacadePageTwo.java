package com.ath.wmb.genflows.wizard;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
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
import com.ath.esqltool.domain.BAthParticularProject;
import com.ath.wmb.genflows.Activator;



public class FacadePageTwo extends WizardPage {

	
	private Composite container;
	
	private Integer numberParticulars = 0;

	private Button checkImpl;

	private Combo comboparticulars;
	private Button[] radiosParticular;
	private Group groupparticular;
	private Combo comboapps;
	private Combo combocntls;
	
	private Group group;
	private Text searchText;
	private Button searchcntlbutton;
	
	private Button addParticularButton;
	private Button clearParticularButton;
	private org.eclipse.swt.widgets.List listWidgetsParts;
	
	private List<BAthParticularProject> listParticulars = null;
	
	
	
	private BAthFacadeProject ctrlProject;

	public FacadePageTwo(ISelection selection) {
		super("Facade Particulars Page");
		setTitle("Particulars");
		setDescription("Facade Wizard: Add particulars to Facade");
		
		listParticulars = new ArrayList<BAthParticularProject>();

	}

	

	@Override
	public void createControl(Composite parent) {
		
		
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 1;
		
		

		checkImpl = new Button(container, SWT.CHECK);
		checkImpl.setText("Defined");
		checkImpl.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, true, 1, 1));
		checkImpl.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {

				Button btn = (Button) event.getSource();
				System.out.println(btn.getSelection());

				if (btn.getSelection()) {
					setDefaultVisibility("IMPLEMENTABLE");
					radiosParticular[0].setSelection(true);
				} else {
					setDefaultVisibility("NOT_IMPLEMENTABLE");
				}
			}
		});

		// Group group = new Group(container, SWT.NONE);
		// group.setLayout(new GridLayout(1, false));

		// GridLayout layout = new GridLayout();
		// container.setLayout(new GridLayout(1, false));

		radiosParticular = new Button[3];
		radiosParticular[0] = new Button(container, SWT.RADIO);
		radiosParticular[0].setSelection(true);
		radiosParticular[0].setText("FMG");
		radiosParticular[0].setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, true, 1, 1));
		radiosParticular[0].addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {

				Button btn = (Button) event.getSource();
				System.out.println(btn.getSelection());

				if (btn.getSelection()) {
					setDefaultVisibility("IS_FMG");
				} else {
					setDefaultVisibility("IS_CNTL");
				}
			}
		});

		radiosParticular[1] = new Button(container, SWT.RADIO);
		radiosParticular[1].setText("CNTL");
		radiosParticular[1].setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, true, 1, 1));

		groupparticular = new Group(container, SWT.NONE);
		groupparticular.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//groupfmg.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, true, 1, 1));
		groupparticular.setLayout(new GridLayout(3, false));

		Label label4 = new Label(groupparticular, SWT.NONE);
		label4.setText("App");

		List<Object> listapps = null;
		

		comboapps = new Combo(groupparticular, SWT.READ_ONLY);
		comboapps.setBounds(50, 50, 150, 65);
		String arrayapps[];
		if (listapps != null) {
//			arrayapps = new String[listapps.size()];
//			Iterator<BAppBo> it = listapps.iterator();
//			int i = 0;
//			while (it.hasNext()) {
//				BAppBo bAppBo = (BAppBo) it.next();
//				arrayapps[i] = bAppBo.getApplication_id();
//				i++;
//
//			}
		} else {
			
		}
		arrayapps = new String[1];
		arrayapps[0] = "AUT";

		comboapps.setItems(arrayapps);

		comboapps.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				System.out.println(comboapps.getText());
				ILog log = Activator.getDefault().getLog();
//				try {
//
//					listparticulars = databaseDao.findByAppId(comboapps.getText());
//
//					System.out.println(listparticulars);
//
//					if (listparticulars != null && !listparticulars.isEmpty()) {
//
//						String arrayparticulars[];
//
//						arrayparticulars = new String[listparticulars.size()];
//						Iterator<BParticularBo> it = listparticulars.iterator();
//						int i = 0;
//						while (it.hasNext()) {
//							BParticularBo bParticularBo = (BParticularBo) it.next();
//							arrayparticulars[i] = bParticularBo.getParticular_id() + "|"+ bParticularBo.getParticular_input_mq() + "|" + bParticularBo.getParticular_name();
//							i++;
//
//						}
//
//						comboparticulars.setItems(arrayparticulars);
//					}
//
//				} catch (Exception e) {
//					e.printStackTrace();
//					log.log(new Status(IStatus.ERROR, "com.ath.wmb.genflows", e.getMessage(), e));
//				}

			}
		});

		comboparticulars = new Combo(groupparticular, SWT.READ_ONLY);
		comboparticulars.setBounds(50, 50, 550, 65);
		String arrayparticulars[] = { "                                                                " };
		comboparticulars.setItems(arrayparticulars);
		
		

		group = new Group(container, SWT.NONE);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//groupcntl.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, true, 1, 1));
		group.setLayout(new GridLayout(4, false));

		Label labelCntl = new Label(group, SWT.NONE);
		labelCntl.setText("Cntl:");

		searchText = new Text(group, SWT.BORDER | SWT.SINGLE);
		searchText.setText("");

		searchcntlbutton = new Button(group, SWT.BUTTON1);
		searchcntlbutton.setText("Search");

		searchcntlbutton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (event.widget == searchcntlbutton) {
					System.out.println("buscar");
					ILog log = Activator.getDefault().getLog();
					if (searchText.getText() != null && searchText.getText() != ""
							&& searchText.getText().length() > 5) {
						
						Integer ctrl = null;
						try {
							ctrl = Integer.parseInt(searchText.getText());
						} catch (NumberFormatException e) {}						
						
//						try {
//							String arraycntls[];
//							if(ctrl != null) { 
//								System.out.println(ctrl);
//								BCntlBo cntlBo = databaseDao.findCtnlById(ctrl);
//								System.out.println(cntlBo);
//								arraycntls = new String[1];
//								arraycntls[0] = cntlBo.getFacade_id() + "|" + cntlBo.getService_name();
//								
//								combocntls.setItems(arraycntls);
//								listcntls = new ArrayList<BCntlBo>();
//								listcntls.add(cntlBo);
//							} else {
//								listcntls = databaseDao.findAllCtnlByCriteria(cntlsearchText.getText());
//								System.out.println(listcntls);
//								
//								
//								
//								if (listcntls != null) {
//									arraycntls = new String[listcntls.size()];
//									Iterator<BCntlBo> it = listcntls.iterator();
//									int i = 0;
//									while (it.hasNext()) {
//										BCntlBo cntlBo = (BCntlBo) it.next();
//										arraycntls[i] = cntlBo.getFacade_id() + "|" + cntlBo.getFacade_name();
//										i++;
//
//									}
//								} else {
//									arraycntls = new String[1];
//									arraycntls[0] = "                         ";
//								}
//
//								combocntls.setItems(arraycntls);
//								
//								
//								
//							}
//						} catch (Exception e) {
//							e.printStackTrace();
//							log.log(new Status(IStatus.ERROR, "com.ath.wmb.genflows", e.getMessage(), e));
//						}

					}

					

				}
			}
		});
		
		
		
		
		combocntls = new Combo(group, SWT.READ_ONLY);
		combocntls.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//combocntls.setBounds(50, 50, 750, 65); 
		String cntls[] = {"                      "};
		
		combocntls.setItems(cntls);

		addParticularButton = new Button(container, SWT.BUTTON1);
		addParticularButton.setText("Add Particular");

		addParticularButton.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, true, 1, 1));
		
		addParticularButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (event.widget == addParticularButton) {
					if (checkImpl.getSelection()) {
						if(radiosParticular[0].getSelection()){//ES FMG
							//bParticularBo.getParticular_id() + "|"+ bParticularBo.getParticular_input_mq() + "-" + bParticularBo.getParticular_name();
							//arrayStepsStrings[i].split("\\|");
							String[] arrayParticular = comboparticulars.getText().split("\\|");
							
							if (arrayParticular.length > 2) {
								
//								BParticularBo fmgBo = new BParticularBo(arrayParticular);
//								
//								listParticulars.add(fmgBo);

								setPageComplete(true);
							}
						} else {
							
//							String[] arrayCntls = combocntls.getText().split("\\|");
//							
//							if (arrayCntls.length > 1) {
//								
//								BCntlBo cntlBo = new BCntlBo(arrayCntls);
//								
//								listParticulars.add(cntlBo);
//								setPageComplete(true);
//							}
							
						}
					} else {
						
						//listParticulars.add(new BUndefinedStep());
						
						setPageComplete(true);
					}
					
					listWidgetsParts.removeAll();
					
					Iterator<BAthParticularProject> iterator = listParticulars.iterator();
					numberParticulars = 0; 
					while (iterator.hasNext()) {
						BAthParticularProject bStepOrchestable = (BAthParticularProject) iterator.next();
//						listSteps.add("Step " + numberSteps + "->|"+ bStepOrchestable.getLongDescription());
						numberParticulars++;
					}
					

					checkImpl.setSelection(false);
					radiosParticular[0].setSelection(false);
					radiosParticular[1].setSelection(false);
					
					setDefaultVisibility("DEFAULT");
				}
			}
		});
		
		
		
		

		listWidgetsParts = new org.eclipse.swt.widgets.List(container, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		listWidgetsParts.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, true, 1, 1));
		
		//listSteps.setBounds(50, 50, 650, 65);
		
		listWidgetsParts.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		
		
		clearParticularButton = new Button(container, SWT.BUTTON1);
		clearParticularButton.setText("Clear");

		clearParticularButton.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, true, 1, 1));
		
		
		clearParticularButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (event.widget == clearParticularButton) {
					listParticulars.clear();
					listWidgetsParts.removeAll();
					setNumberSteps(0);
				}
			}
		});
		
		

		setDefaultVisibility("DEFAULT");

		
		setControl(container);
		setPageComplete(true);
	}
	
	
	
	
	
	
	
	public void setDefaultVisibility(String typeChange) {
		if (typeChange == "DEFAULT") {

			checkImpl.setVisible(true);

			radiosParticular[0].setVisible(false);
			radiosParticular[1].setVisible(false);

			groupparticular.setVisible(false);
			comboapps.setVisible(false);
			comboparticulars.setVisible(false);

			group.setVisible(false);
			searchText.setVisible(false);
			searchcntlbutton.setVisible(false);


			addParticularButton.setVisible(true);
			clearParticularButton.setVisible(true);
			listWidgetsParts.setVisible(true);
		} else if (typeChange == "IMPLEMENTABLE") {
			radiosParticular[0].setVisible(true);
			radiosParticular[1].setVisible(true);

			groupparticular.setVisible(true);
			comboapps.setVisible(true);
			comboparticulars.setVisible(true);

			group.setVisible(false);
			searchText.setVisible(false);
			searchcntlbutton.setVisible(false);

			addParticularButton.setVisible(true);
			clearParticularButton.setVisible(true);
			listWidgetsParts.setVisible(true);

		} else if (typeChange == "NOT_IMPLEMENTABLE") {
			radiosParticular[0].setVisible(false);
			radiosParticular[1].setVisible(false);

			groupparticular.setVisible(false);
			comboapps.setVisible(false);
			comboparticulars.setVisible(false);

			group.setVisible(false);
			searchText.setVisible(false);
			searchcntlbutton.setVisible(false);

			addParticularButton.setVisible(true);
			clearParticularButton.setVisible(true);
			listWidgetsParts.setVisible(true);

		} else if (typeChange == "IS_FMG") {
			radiosParticular[0].setVisible(true);
			radiosParticular[1].setVisible(true);

			groupparticular.setVisible(true);
			comboapps.setVisible(true);
			comboparticulars.setVisible(true);

			group.setVisible(false);
			searchText.setVisible(false);
			searchcntlbutton.setVisible(false);
			
			addParticularButton.setVisible(true);
			clearParticularButton.setVisible(true);
			listWidgetsParts.setVisible(true);

		} else if (typeChange == "IS_CNTL") {
			radiosParticular[0].setVisible(true);
			radiosParticular[1].setVisible(true);

			groupparticular.setVisible(false);
			comboapps.setVisible(false);
			comboparticulars.setVisible(false);

			group.setVisible(true);
			searchText.setVisible(true);
			searchcntlbutton.setVisible(true);

			addParticularButton.setVisible(true);
			clearParticularButton.setVisible(true);
			listWidgetsParts.setVisible(true);

		}

	}
	
	
	
	


	

	public Button getCheckImpl() {
		return checkImpl;
	}

	public void setCheckImpl(Button checkImpl) {
		this.checkImpl = checkImpl;
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
		return searchText;
	} 

	public void setCntlsearchText(Text cntlsearchText) {
		this.searchText = cntlsearchText;
	}

	
	public org.eclipse.swt.widgets.List getListSteps() {
		return listWidgetsParts;
	}

	public void setListSteps(org.eclipse.swt.widgets.List listSteps) {
		this.listWidgetsParts = listSteps;
	}

	public Integer getNumberSteps() {
		return numberParticulars; 
	}

	public void setNumberSteps(Integer numberSteps) {
		this.numberParticulars = numberSteps;
	}


	public BAthFacadeProject getFacadeProject() {
		return ctrlProject;
	}

	public void setFacadeProject(BAthFacadeProject ctrlProject) {
		this.ctrlProject = ctrlProject;
	}



	public List<BAthParticularProject> getListStepsOrchestables() {
		return listParticulars;
	}



	public void setListStepsOrchestables(List<BAthParticularProject> listStepsOrchestables) {
		this.listParticulars = listStepsOrchestables;
	}
	
	
	
	
	
	
	
	
	
	
}
