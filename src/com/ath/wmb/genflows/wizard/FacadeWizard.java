package com.ath.wmb.genflows.wizard;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
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
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.ath.esqltool.delegates.BAthGenerator;
import com.ath.esqltool.delegates.BAthParticularGenerator;
import com.ath.esqltool.domain.BAthFacadeProject;
import com.ath.esqltool.domain.BAthOrchestable;
import com.ath.esqltool.domain.BAthParticularProject;
import com.ath.esqltool.domain.BAthSpecificBo;
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
	
	private List<BAthParticularProject> listParticulars = new ArrayList<BAthParticularProject>();

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
					MessageDialog.openInformation(getShell(), "GENFLOW Generation", "It's a problem in queue creation, please de-active create queues");
					return false; 
				}
			}
		}

		try {


			facadeProject.setCurrentDir(three.getTextProjectLocation().getText());
			System.out.println(three.getTextProjectLocation().getText());

			facadeProject.setNamespace(one.getNamespace());  


			facadeProject.setDomain(one.getDomain());
			facadeProject.setSrvName(one.getSrvname());
			facadeProject.setOprName(one.getOprname());
			facadeProject.setChannel(one.getChannel());
			facadeProject.setOrgName(one.getOrgname());
			facadeProject.setBankId(one.getBankid());
			
			facadeProject.setWsdlName(one.getNameOfWSDL());
			facadeProject.setWsdlRelativePathName("model/" + one.getNameOfWSDL());
			facadeProject.setWsdlBinding(one.getWsdlBinding());
			facadeProject.setWsdlPort(one.getWsdlPort());
			facadeProject.setWsdlSvcPort(one.getWsdlSvcPort());
			
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
				File file = new File(facadeProject.getProjectPath() + facadeProject.getWsdlRelativePathName());
				file.getParentFile().mkdirs(); 
				Result output = new StreamResult(file); 
				Source input = new DOMSource(one.getDocumentWSDL());
				transformer.transform(input, output);
			}
			
			step1CreateProject = true;
			
			
			if (two.getListSpecificsBo() != null && !two.getListSpecificsBo().isEmpty()) {
				
				Iterator<BAthSpecificBo> iterator = two.getListSpecificsBo().iterator();
				while (iterator.hasNext()) {
					BAthSpecificBo bAthSpecific = (BAthSpecificBo) iterator.next();
					
					BAthParticularProject particular = BAthParticularProject.valueOf(facadeProject);
					
//					particular.setName(bAthSpecific.getName() + "Svc_" + bAthSpecific.getBankOrg());
					particular.setOrgName(bAthSpecific.getBankOrg());
					particular.setBankId(particular.getBankId());//TODO agregar el bankID propio del especifico??
					particular.setCodService(bAthSpecific.getCodService());
					listParticulars.add(particular);
					
				}
				
				if (listParticulars != null && !listParticulars.isEmpty()) {
					
					Iterator<BAthParticularProject> iteratorParticulars = listParticulars.iterator();
					
					BAthParticularGenerator particularGen = new BAthParticularGenerator();
					
					while (iteratorParticulars.hasNext()) {
						BAthParticularProject bAthParticularProject = (BAthParticularProject) iteratorParticulars
								.next();
						
						particularGen.generar(bAthParticularProject);
					}
					
				}
				
			}
			

		} catch (Exception e) {
			log.log(new Status(IStatus.ERROR, "com.ath.wmb.genflows", e.getMessage(), e));
			MessageDialog.openInformation(getShell(), "GENFLOW Generation", e.getMessage());
			return false;
		}


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
			
			if (listParticulars != null && !listParticulars.isEmpty()) {
				Iterator<BAthParticularProject> iteratorParticulars = listParticulars.iterator();
				
				while (iteratorParticulars.hasNext()) {
					BAthParticularProject bAthParticularProject = (BAthParticularProject) iteratorParticulars
							.next();
					
					IProjectDescription descParticular = null;
					descParticular = ResourcesPlugin.getWorkspace().loadProjectDescription(new Path(bAthParticularProject.getProjectPath() + ".project"));
					IProject projectPart = ResourcesPlugin.getWorkspace().getRoot().getProject(bAthParticularProject.getName());
					projectPart.create(descParticular, null);
					projectPart.open(null);
					
				}
			}
			
			
			IWorkbench workbench = PlatformUI.getWorkbench();
			IWorkbenchWindow window = workbench == null ? null : workbench.getActiveWorkbenchWindow();
			IWorkbenchPage activePage = window == null ? null : window.getActivePage();

			if (project != null && activePage != null) {
				locateFlowAndOpen(project, activePage);
			}

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
	
	
	private boolean locateFlowAndOpen(IContainer container, IWorkbenchPage page) throws CoreException {
		IResource[] members = container.members();

		for (IResource member : members) {
			if (member instanceof IContainer) {
				boolean isOpen = locateFlowAndOpen((IContainer) member, page);
				if (isOpen) {
					return true;
				}
			} else if (member instanceof IFile) {
				System.out.println("FILE------->" + member.getName());
				if (member.getName().indexOf("REQ.msgflow") != -1 ) {
					IFile flowfile = (IFile) member;
					org.eclipse.ui.ide.IDE.openEditor(page, flowfile, true);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void init(IWorkbench arg0, IStructuredSelection arg1) {

	}

}
