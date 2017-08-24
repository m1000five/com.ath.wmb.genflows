package com.ath.wmb.genflows.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.ath.wmb.genflows.wizard.FacadeWizard;

import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class TemplateHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		
		WizardDialog wizardDialog = new WizardDialog(window.getShell(), new FacadeWizard());
		if (wizardDialog.open() == Window.OK) {
			System.out.println("Ok pressed");
		} else {
			System.out.println("Cancel pressed");
		}
		
		
		return null;
	}
}
