package com.ath.wmb.genflows.wizard;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

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

import com.ath.esqltool.delegates.BAthParticularGenerator;
import com.ath.esqltool.domain.BAthParticularProject;
import com.ath.esqltool.util.BUtil;
import com.ath.wmb.genflows.Activator;
import com.ath.wmb.genflows.handlers.ErrorHandlerInterface;

public class ParticularWizard extends Wizard implements INewWizard {

	private ISelection selection;

	protected ParticularPageOne one;

	protected ParticularPageTwo two;

	boolean step1CreateProject = false;
	boolean step2CreateInserts = false;
	boolean step3PreCreateQueues = false;
	boolean step4CreateQueues = false;

	private BAthParticularProject particularProject = new BAthParticularProject();

	private ErrorHandlerInterface customHandlerInterface;

	private ArrayList<IFile> listOfIfiles = new ArrayList<IFile>();

	public ParticularWizard() {
		super();
		setNeedsProgressMonitor(true);

	}

	@Override
	public String getWindowTitle() {
		return "Generate New Specific";
	}

	@Override
	public void addPages() {

		particularProject = new BAthParticularProject();

		ILog log = Activator.getDefault().getLog();

		one = new ParticularPageOne(selection);

		two = new ParticularPageTwo(selection);

		one.setParticularProject(particularProject);

		two.setParticularProject(particularProject);

		one.setErrorhandler(customHandlerInterface);

		// customHandlerInterface.onError();

		addPage(one);
		addPage(two);

	}

	@Override
	public boolean performFinish() {

		System.out.println(one.getSrvname());
		System.out.println(two.getTextProjectLocation().getText());

		ILog log = Activator.getDefault().getLog();

		try {

			particularProject.setCurrentDir(two.getTextProjectLocation().getText());
			System.out.println(two.getTextProjectLocation().getText());

			particularProject.setNamespace(one.getNamespace());

			particularProject.setDomain(one.getDomain());
			particularProject.setSrvName(one.getSrvname());
			particularProject.setOprName(one.getOprname());
			particularProject.setChannel(one.getChannel());
			particularProject.setOrgName(one.getOrgname());
			particularProject.setBankId(one.getBankid());
			particularProject.setPassthrough(one.isPassthrough());
			particularProject.setCodService(one.getCodService());

			particularProject.setIdeRequirement(two.getTextIdeRequirement().getText());
			particularProject.setFacadeName(one.getFacadeProjectName());
			particularProject.setSetNamespaces(one.getSetOthersNamespaces());
			particularProject.setSetSpecificNamespaces(one.getSetSpecificNamespaces());
			
			HashMap<String, String> mapNamespaces = BUtil.genOthersNamespaces(one.getSetOthersNamespaces());
			particularProject.setMapOthersNamespaces(mapNamespaces);
			
			HashSet<String> tmpSet = new HashSet<String>(); 
			tmpSet.addAll(mapNamespaces.keySet()); 
			
			HashMap<String, String> mapSpecificNamespaces = BUtil.genOthersNamespaces(one.getSetSpecificNamespaces(), tmpSet);

			particularProject.setMapSpecificNamespaces(mapSpecificNamespaces);

			log.log(new Status(IStatus.INFO, "com.ath.wmb.genflows", particularProject.toString()));

			BAthParticularGenerator generator = new BAthParticularGenerator();
			generator.generar(particularProject);

			step1CreateProject = true;

			
			if (particularProject.isPassthrough()) {
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
						processContainer(project);
						if (!listOfIfiles.isEmpty()) {
							Iterator<IFile> iterator = listOfIfiles.iterator();
							while (iterator.hasNext()) {
								IFile iFile = (IFile) iterator.next();
//								IBMdefined\org\w3\www\xml\_1998\namespace\xml.xsd
//								iFile.getFullPath(); 
								System.out.println("FILE->" + iFile.getProjectRelativePath().toString());
								File flowFile = iFile.getRawLocation().makeAbsolute().toFile();
								
								File newFile = (new File(particularProject.getProjectPath() + iFile.getProjectRelativePath().toString()));
								newFile.mkdirs();
								
								
								Files.copy(flowFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
							}
						}
					}
				}
			} else { 
				particularProject.setWsdlRelativePathName("model/" + one.getNameOfSelectWSDL());
				
				if (one.getSelectWSDL() != null) {
					Transformer transformer = TransformerFactory.newInstance().newTransformer();
					File file = new File(particularProject.getProjectPath() + particularProject.getWsdlRelativePathName());
					file.getParentFile().mkdirs(); 
					Result output = new StreamResult(file); 
					Source input = new DOMSource(one.getSelectWSDL());
					transformer.transform(input, output);
				}
			}
			
			

		} catch (Exception e) {
			log.log(new Status(IStatus.ERROR, "com.ath.wmb.genflows", e.getMessage(), e));
			MessageDialog.openInformation(getShell(), "GENFLOW Generation", e.getMessage());
			return false;
		}

		try {
			IProjectDescription description = null;

			System.out.println("Importando:" + particularProject.getProjectPath() + ".project");
			log.log(new Status(IStatus.INFO, "com.ath.wmb.genflows",
					"Importando:" + particularProject.getProjectPath() + ".project"));

			description = ResourcesPlugin.getWorkspace()
					.loadProjectDescription(new Path(particularProject.getProjectPath() + ".project"));
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
				// pcfCM.DisplayException(e);
			}
			log.log(new Status(IStatus.ERROR, "com.ath.wmb.genflows", e.getMessage(), e));
		}

		String additionalMsg = "";

		MessageDialog.openInformation(getShell(), "Specific Generation",
				"A new project: " + particularProject.getName() + " has been generated. " + additionalMsg);

		return true;
	}

	void processContainer(IContainer container) throws CoreException {
		IResource[] members = container.members();

		for (IResource member : members) {
			if (member instanceof IContainer) {
				processContainer((IContainer) member);
			} else if (member instanceof IFile) {
				System.out.println("FILE->" + member.getName());
				if (member.getName().indexOf(".wsdl") != -1 || member.getName().indexOf(".WSDL") != -1
						|| member.getName().indexOf(".xsd") != -1 || member.getName().indexOf(".XSD") != -1) {
					listOfIfiles.add((IFile) member);
				}
			}
		}
	}

	@Override
	public void init(IWorkbench arg0, IStructuredSelection arg1) {

	}

	public void setErrorhandler(ErrorHandlerInterface customErrorHandlerInterface) {
		this.customHandlerInterface = customErrorHandlerInterface;

	}

}
