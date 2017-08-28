package com.ath.wmb.genflows.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.ILog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

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

		return true;
	}

	@Override
	public void init(IWorkbench arg0, IStructuredSelection arg1) {

	}

}
