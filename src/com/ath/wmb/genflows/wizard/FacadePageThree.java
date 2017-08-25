package com.ath.wmb.genflows.wizard;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.ath.esqltool.domain.BAthFacadeProject;








public class FacadePageThree extends WizardPage {

	private Composite container;
	
	private Text textProjectLocation;
	private Text textTemplateLocation;
	private Text textQueueManName;
	private Text textIdeRequirement;
	

	private Button mButtonSelection;
	private Button mButtonTemplates;

	private Button checkUpdateDb;
	private Button checkCreateQueues;
	private Button checkDefaultTemplates;
	
	private boolean customPathOfTemplates;
	
	private BAthFacadeProject facadeProject;

	public FacadePageThree(ISelection selection) {
		super("Facade Project Location and Resume");
		setTitle("Location and Resume");
		setDescription("Facade Wizard: Select the location of new project");
		customPathOfTemplates = false;

	}

	@Override
	public void createControl(Composite parent) {
		
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 1;

		Group groupLocation = new Group(container, SWT.NONE);
		groupLocation.setLayout(new GridLayout(2, false));
		groupLocation.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		textProjectLocation = new Text(groupLocation, SWT.SINGLE | SWT.BORDER);
		
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = GridData.FILL;
		textProjectLocation.setLayoutData(gd);

		String wLocation = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();

		textProjectLocation.setText(wLocation);

		mButtonSelection = new Button(groupLocation, SWT.NONE);
		mButtonSelection.setText("...");
		mButtonSelection.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dlg = new DirectoryDialog(mButtonSelection.getShell(), SWT.OPEN);
				dlg.setText("Open");
				String path = dlg.open();
				if (path == null || path.length() == 0) {
					setPageComplete(false);
					return;
				}
				textProjectLocation.setText(path);
				System.out.println(textProjectLocation.getText());
				setPageComplete(true);
			}
		});

//		Group groupTemplates = new Group(container, SWT.NONE);
//		groupTemplates.setLayout(new GridLayout(3, false));
//		groupTemplates.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//
//		checkDefaultTemplates = new Button(groupTemplates, SWT.CHECK);
//		checkDefaultTemplates.setText("Select custom templates?");
//
//		checkDefaultTemplates.addSelectionListener(new SelectionAdapter() {
//
//			@Override
//			public void widgetSelected(SelectionEvent event) {
//
//				Button btn = (Button) event.getSource();
//				System.out.println(btn.getSelection());
//
//				if (btn.getSelection()) {
//					setPageComplete(false);
//					mButtonTemplates.setEnabled(true);
//				} else {
//					setPageComplete(true);
//					mButtonTemplates.setEnabled(false);
//				}
//
//			}
//		});
//
//		mButtonTemplates = new Button(groupTemplates, SWT.NONE);
//		mButtonTemplates.setEnabled(false);
//		mButtonTemplates.setText("...");
//		mButtonTemplates.addSelectionListener(new SelectionListener() {
//
//			public void widgetDefaultSelected(SelectionEvent e) {
//			}
//
//			public void widgetSelected(SelectionEvent e) {
//				DirectoryDialog dlg = new DirectoryDialog(mButtonTemplates.getShell(), SWT.OPEN);
//				dlg.setText("Open");
//				String path = dlg.open();
//				if (path == null || path.length() == 0) {
//					setPageComplete(false);
//					return;
//				}
//
//				try {
//					File folder = new File(path);
//					File[] listOfFiles = folder.listFiles();
//					
//					List<String> listTemplates = BAthTemplates.map;
//					Iterator<String> itTemplates = listTemplates.iterator();
//					
//					String aFileNotFound = null;
//					while (itTemplates.hasNext()) {
//						String template = (String) itTemplates.next();
//						
//						boolean located = false;
//						
//						for (int i = 0; i < listOfFiles.length; i++) {
//							if (listOfFiles[i].isFile()) {
//								System.out.println("File " + listOfFiles[i].getName());
//								if (template.equalsIgnoreCase(listOfFiles[i].getName())) {
//									located = true;
//									break;
//								}
//							}
//						}
//						if (!located) {
//							aFileNotFound = template;
//							break;
//						}
//					}
//					
//					textTemplateLocation.setText(path);
//					
//					if (aFileNotFound != null) {
//						setErrorMessage("ERROR: Template not found: " + aFileNotFound);
//						setPageComplete(false);
//					} else {
//						customPathOfTemplates = true;
//						setPageComplete(true);
//					}
//				} catch (Exception e2) {
//					e2.printStackTrace();
//					setPageComplete(false);
//				}
//				
//				
//			}
//		});
//
//		textTemplateLocation = new Text(groupTemplates, SWT.SINGLE | SWT.BORDER);
//		textTemplateLocation.setEditable(false);
//		textTemplateLocation.setLayoutData(gd);
		
		
//		Group groupUpdateDb = new Group(container, SWT.NONE);
//		groupUpdateDb.setLayout(new GridLayout(1, false));
//		groupUpdateDb.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//
//		checkUpdateDb = new Button(groupUpdateDb, SWT.CHECK); 
//		checkUpdateDb.setText("Insert new Facade into BD?");
//		checkUpdateDb.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, true, 1, 1));
		
		
		Group groupQueues = new Group(container, SWT.NONE);
		groupQueues.setLayout(new GridLayout(2, false));
		groupQueues.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		
		checkCreateQueues = new Button(groupQueues, SWT.CHECK);
		checkCreateQueues.setText("Create Queues in Local Queue Manager?");
		checkCreateQueues.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, true, 1, 1));
		
		checkCreateQueues.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				Button btn = (Button) event.getSource();
				System.out.println(btn.getSelection());
				if (btn.getSelection()) {
					textQueueManName.setEditable(true);
				} else {
					textQueueManName.setEditable(false);
				}

			}
		});
		
		textQueueManName = new Text(groupQueues, SWT.SINGLE | SWT.BORDER);
		textQueueManName.setEditable(false);
		textQueueManName.setLayoutData(gd);
		
		Group groupRequirement = new Group(container, SWT.NONE);
		groupRequirement.setLayout(new GridLayout(2, false));
		groupRequirement.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		
		Label label4 = new Label(groupRequirement, SWT.NONE);
		label4.setText("Id Requirement (Optional)");
		
		textIdeRequirement = new Text(groupRequirement, SWT.SINGLE | SWT.BORDER);
		textIdeRequirement.setLayoutData(gd);
		textIdeRequirement.setText(facadeProject.getIdeRequirement());

		setControl(container);

		if (wLocation == null || wLocation.length() == 0) {
			setPageComplete(false);
		}

	}


	public Text getTextProjectLocation() {
		return textProjectLocation;
	}

	public void setTextProjectLocation(Text textProjectLocation) {
		this.textProjectLocation = textProjectLocation;
	}

	public Button getCheckUpdateDb() {
		return checkUpdateDb;
	}

	public boolean isCustomPathOfTemplates() {
		return customPathOfTemplates;
	}

	public void setCustomPathOfTemplates(boolean customPathOfTemplates) {
		this.customPathOfTemplates = customPathOfTemplates;
	}
	
	public Text getTextTemplateLocation() {
		return textTemplateLocation;
	}

	public void setTextTemplateLocation(Text textTemplateLocation) {
		this.textTemplateLocation = textTemplateLocation;
	}

	public Button getCheckCreateQueues() {
		return checkCreateQueues;
	}

	public void setCheckCreateQueues(Button checkCreateQueues) {
		this.checkCreateQueues = checkCreateQueues;
	}

	public Text getTextQueueManName() {
		return textQueueManName;
	}

	public void setTextQueueManName(Text textQueueManName) {
		this.textQueueManName = textQueueManName;
	}
	
	public BAthFacadeProject getFacadeProject() {
		return facadeProject;
	}

	public void setFacadeProject(BAthFacadeProject ctrlProject) {
		this.facadeProject = ctrlProject;
	}

	public Text getTextIdeRequirement() {
		return textIdeRequirement;
	}

	public void setTextIdeRequirement(Text textIdeRequirement) {
		this.textIdeRequirement = textIdeRequirement;
	} 
	
	
	
	

}
