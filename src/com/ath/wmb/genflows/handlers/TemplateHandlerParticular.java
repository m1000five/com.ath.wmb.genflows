package com.ath.wmb.genflows.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import com.ath.wmb.genflows.wizard.ParticularWizard;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class TemplateHandlerParticular extends AbstractHandler {

	private StringBuffer errorMsg = new StringBuffer();

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		final ParticularWizard particularWizard = new ParticularWizard();
		final WizardDialog wizardDialog = new WizardDialog(window.getShell(), particularWizard);

		particularWizard.setErrorhandler(new ErrorHandlerInterface() {
			@Override
			public void onError() {
				wizardDialog.close();
			}
		});
		
		if (validateFacadeFlow()) {
			if (wizardDialog.open() == Window.OK) {
				System.out.println("Ok pressed");
			} else {
				System.out.println("Cancel pressed");
			}
		} else {
			MessageDialog.openInformation(window.getShell(), "Specific Generation", errorMsg.toString());
		}


		return null;
	}

	private boolean validateFacadeFlow() {

		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench == null ? null : workbench.getActiveWorkbenchWindow();
		IWorkbenchPage activePage = window == null ? null : window.getActivePage();

		IEditorPart editor = activePage == null ? null : activePage.getActiveEditor();
		IEditorInput input = editor == null ? null : editor.getEditorInput();

		if (input == null) {
			errorMsg.setLength(0);
			errorMsg.append("Please select the initial Facade Flow.");
			return false;

		}

		System.out.println(input.getToolTipText());  

		if (input.getToolTipText().indexOf("FcdWs") != -1 && input.getToolTipText().indexOf(".msgflow") != -1) {
			System.out.println("Es flujo");
		} else {
			errorMsg.setLength(0);
			errorMsg.append(" El recurso: ");
			errorMsg.append(input.getToolTipText());
			errorMsg.append(
					" - Is Not a Flow of Facade. Please select the initial Facade Flow.");

			return false;
		}
		return true;

	}

}
