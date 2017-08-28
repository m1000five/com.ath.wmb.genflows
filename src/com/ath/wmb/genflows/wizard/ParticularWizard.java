package com.ath.wmb.genflows.wizard;

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

import com.ath.esqltool.delegates.BAthParticularGenerator;
import com.ath.esqltool.domain.BAthParticularProject;
import com.ath.wmb.genflows.Activator;

public class ParticularWizard extends Wizard implements INewWizard {

	private ISelection selection;

	protected ParticularPageOne one;

	protected ParticularPageTwo two;

	boolean step1CreateProject = false;
	boolean step2CreateInserts = false;
	boolean step3PreCreateQueues = false;
	boolean step4CreateQueues = false;

	private BAthParticularProject particularProject = new BAthParticularProject();


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
			
			particularProject.setIdeRequirement(two.getTextIdeRequirement().getText());
			
			log.log(new Status(IStatus.INFO, "com.ath.wmb.genflows", particularProject.toString()));
			
			BAthParticularGenerator generator = new BAthParticularGenerator(); 
			generator.generar(particularProject);
			
			step1CreateProject = true;
			
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
//				pcfCM.DisplayException(e);
			}
			log.log(new Status(IStatus.ERROR, "com.ath.wmb.genflows", e.getMessage(), e));
		}
		

		return true;
	}

	@Override
	public void init(IWorkbench arg0, IStructuredSelection arg1) {

	}

}
