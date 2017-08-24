package com.ath.wmb.genflows.wizard;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import com.ath.esqltool.delegates.BAthGenerator;
import com.ath.esqltool.domain.BAthFacadeProject;
import com.ath.esqltool.util.BUtil;
import com.ath.wmb.genflows.Activator;



public class FacadeWizard extends Wizard implements INewWizard {

	private ISelection selection;

	protected FacadePageOne one;
	protected FacadePageTwo two;
	protected FacadePageThree three;

	
	boolean step1CreateProject = false;
	boolean step2CreateInserts = false;
	boolean step3PreCreateQueues = false;
	boolean step4CreateQueues = false;

	private BAthFacadeProject facadeProject = new BAthFacadeProject();

	public FacadeWizard() {
		super();
		setNeedsProgressMonitor(true);

	}

	@Override
	public String getWindowTitle() {
		return "Generate New Facade";
	}

	@Override
	public void addPages() {

		facadeProject = new BAthFacadeProject();

//		Map<String, String> param = new HashMap<String, String>();
//
//		param.put("hostname", "10.85.84.142");
//		param.put("db", "dsbrk");
//		param.put("username", "usrbrk");
//		param.put("password", "usrbrk");

		ILog log = Activator.getDefault().getLog();

		one = new FacadePageOne(selection);
		two = new FacadePageTwo(selection);
		three = new FacadePageThree(selection);

		
		
		one.setFacadeProject(facadeProject);
		two.setFacadeProject(facadeProject);
		three.setFacadeProject(facadeProject);

		addPage(one);
		addPage(two);
		addPage(three);
	}

	@Override
	public boolean performFinish() {
		// Print the result to the console
		System.out.println(one.getSrvname());
		

		System.out.println(three.getTextProjectLocation().getText());

		ILog log = Activator.getDefault().getLog();
		

		if (step1CreateProject) {
			if (three.getCheckCreateQueues().getSelection()) {
				if (step3PreCreateQueues && !step4CreateQueues) {
					MessageDialog.openInformation(getShell(), "FW2 Generation", "It's a problem in queue creation, please de-active create queues");
					return false; 
				}
			}
		}

		try {

			

//			if (one.getCheckAutoIdCntl().getSelection()) {
////				List<BCntlBo> listcntls = databaseDao.findAllCtnlByCriteria(700000, 999891);
////				Iterator<BCntlBo> it = listcntls.iterator();
////				boolean comienzo = false;
////				int idx = 0;
////				while (it.hasNext()) {
////					BCntlBo cntlBo = (BCntlBo) it.next();
////					if (!comienzo) {
////						idx = cntlBo.getFacade_id();
////						comienzo = true;
////						idx++;
////						continue;
////					}
////					if (idx != cntlBo.getFacade_id().intValue()) {
////						break;
////					}
////					idx++;
//				}
//				facadeProject.setSrvId(idx);
//			} else {
//				facadeProject.setSrvId(Integer.parseInt(one.getCtrlid()));
//			}

			facadeProject.setCurrentDir(three.getTextProjectLocation().getText());
			System.out.println(three.getTextProjectLocation().getText());

			facadeProject.setNamespace(one.getNamespace());  

						
//			Iterator<BStepOrchestable> iterator = two.getListStepsOrchestables().iterator();
//			
//			while (iterator.hasNext()) {
//				BStepOrchestable bStepOrchestable = (BStepOrchestable) iterator.next();
//				
//					if (bStepOrchestable.getType().equalsIgnoreCase("FMG")) {
//						
//						BFmgBo bFmgBo = databaseDao.findFmgById(bStepOrchestable.getId());
//						if (bFmgBo != null) {
//							facadeProject.addStep(bFmgBo);
//						}
//
//					} else if (bStepOrchestable.getType().equalsIgnoreCase("CNTL")) {
//						
//						BCntlBo cntlBo = databaseDao.findCtnlById(bStepOrchestable.getId());
//						if (cntlBo != null) {
//							facadeProject.addStep(cntlBo);
//						}
//					} else {
//						BStepOrchestable defaultstep = new BFmgBo(0, "FMG.DEST.00000", "ESB_DEST_NomServicio_NomOperacion_FMG");
//						facadeProject.addStep(defaultstep);
//					}
//			}

			facadeProject.setDomain(one.getDomain());
			facadeProject.setSrvName(one.getSrvname());
			facadeProject.setOprName(one.getOprname());
			facadeProject.setChannel(one.getChannel());
			facadeProject.setOrgName(one.getOrgname());
			facadeProject.setBankId(one.getBankid());
			
			facadeProject.setIdeRequirement(three.getTextIdeRequirement().getText());
			try {
				facadeProject.setSrvId(Integer.parseInt(facadeProject.getIdeRequirement()));
			} catch (Exception e) {}

			System.out.println(facadeProject);

			log.log(new Status(IStatus.INFO, "com.ath.wmb.genflows", facadeProject.toString()));

			if (!facadeProject.validate()) {
				((FacadePageThree) getPage("Facade Project Location and Resume"))
						.setErrorMessage(facadeProject.getErrorMessage());
				return false;
			}
			
			facadeProject.setSetNamespaces(one.getSetOthersNamespaces());
			
			HashMap<String, String> mapNamespaces = BUtil.genOthersNamespaces(one.getSetOthersNamespaces());
			
			facadeProject.setMapOthersNamespaces(mapNamespaces);

			BAthGenerator generator = new BAthGenerator(); 

			
			generator.generar(facadeProject);
			
			if (one.getDocumentWSDL() != null) {
				Transformer transformer = TransformerFactory.newInstance().newTransformer();
				Result output = new StreamResult(new File(facadeProject.getProjectPath() + one.getNameOfWSDL()));
				Source input = new DOMSource(one.getDocumentWSDL());
				transformer.transform(input, output);
			}
			
			step1CreateProject = true;

		} catch (Exception e) {
			log.log(new Status(IStatus.ERROR, "com.ath.wmb.genflows", e.getMessage(), e));
			MessageDialog.openInformation(getShell(), "FW2 Generation", e.getMessage());
			return false;
		}

//		try {
//
//			if (three.getCheckUpdateDb().getSelection()) {
//				databaseDao.insert(facadeProject);
//
//				Iterator<BStepOrchestable> itSteps = facadeProject.getListSteps().iterator();
//
//				while (itSteps.hasNext()) {
//					BStepOrchestable bStepOrchestable = itSteps.next();
//
//					if (bStepOrchestable.getId() > 0) {
//						if (bStepOrchestable.getInputMq().toUpperCase().startsWith("FMG")) {
//							databaseDao.insertOrchestable(facadeProject, bStepOrchestable);
//						}
//					}
//
//				}
//
//			}
//
//			
//
//		} catch (IOException ioe) {
//			ioe.printStackTrace();
//			log.log(new Status(IStatus.ERROR, "com.ath.wmb.genflows", ioe.getMessage(), ioe));
//		} catch (Exception e) {
//			if (step3PreCreateQueues) {
//				
//			}
//			log.log(new Status(IStatus.ERROR, "com.ath.wmb.genflows", e.getMessage(), e));
//		}

		try {

			IProjectDescription description = null;

			System.out.println("Importando:" + facadeProject.getProjectPath() + ".project");
			log.log(new Status(IStatus.INFO, "com.ath.wmb.genflows",
					"Importando:" + facadeProject.getProjectPath() + ".project"));

			description = ResourcesPlugin.getWorkspace()
					.loadProjectDescription(new Path(facadeProject.getProjectPath() + ".project"));
			System.out.println(description);
			System.out.println(description.getName());
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(description.getName());
			project.create(description, null);
			project.open(null);

		} catch (CoreException exception_p) {
			exception_p.printStackTrace();
			log.log(new Status(IStatus.ERROR, "com.ath.wmb.genflows", exception_p.getMessage(), exception_p));
		} catch (Exception e) {
			if (step3PreCreateQueues) {
//				pcfCM.DisplayException(e);
			}
			log.log(new Status(IStatus.ERROR, "com.ath.wmb.genflows", e.getMessage(), e));
		}
		
		String additionalMsg = "";
		if (step1CreateProject) {
				if (step3PreCreateQueues && !step4CreateQueues) {
					additionalMsg = "It's a problem in queue creation.";
				}
		}


		MessageDialog.openInformation(getShell(), "Facade Generation",
				"A new project: " + facadeProject.getName() + " has been generated. " + additionalMsg);

		return true; 
	}

	@Override
	public void init(IWorkbench arg0, IStructuredSelection arg1) {

	}

}
