/*******************************************************************************
 * Copyright (c) 2015 UT-Battelle, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Alex McCaskey - Initial API and implementation and/or initial documentation
 *
 *******************************************************************************/
package org.eclipse.ice.developer.actions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.internal.lifecyclemapping.model.LifecycleMappingMetadataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The BuildICEHandler is a subclass of AbstractHandler that provides 
 * an execute implementation that grabs the ICE Build Launch Configuration 
 * files and launches them with the ILaunchManager to build ICE in its 
 * entirety.
 * 
 * @author Alex McCaskey
 *
 */
public class BuildICEHandler extends AbstractHandler {

	/**
	 * Logger for handling event messages and other information.
	 */
	protected static final Logger logger = LoggerFactory.getLogger(GitCloneHandler.class);

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		// Get the Launch Manager
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();

		if (manager != null) {
			IProject aggregatorProject = ResourcesPlugin.getWorkspace().getRoot().getProject("org.eclipse.ice.aggregator");

			if (aggregatorProject != null) {
				// Get the Launch Configurations files
				IFile mvnInstall = aggregatorProject.getFile("Build ICE.launch");

				// Get the Launch Configuration from those files
				ILaunchConfiguration installConfig = manager.getLaunchConfiguration(mvnInstall);

				// Create and launch the Job. 
				Job job = new WorkspaceJob("Building ICE") {

					@Override
					public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
						installConfig.launch(ILaunchManager.RUN_MODE, monitor);
						return Status.OK_STATUS;
					}

				};
				job.schedule();
			} else {
				logger.info("Info: Could not build ICE because it hasn't been cloned yet. Please clone ICE to the current workspace.");
			}
		} else {
			logger.error("Error in launching the ICE Build - Could not get the Launch Manager.");
		}
		return null;
	}

}
