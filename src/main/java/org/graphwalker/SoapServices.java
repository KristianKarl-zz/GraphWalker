//This file is part of the GraphWalker java package
//The MIT License
//
//Copyright (c) 2010 graphwalker.org
//
//Permission is hereby granted, free of charge, to any person obtaining a copy
//of this software and associated documentation files (the "Software"), to deal
//in the Software without restriction, including without limitation the rights
//to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//copies of the Software, and to permit persons to whom the Software is
//furnished to do so, subject to the following conditions:
//
//The above copyright notice and this permission notice shall be included in
//all copies or substantial portions of the Software.
//
//THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//THE SOFTWARE.

package org.graphwalker;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

import javax.jws.WebService;

import org.apache.log4j.Logger;
import org.graphwalker.GUI.App;
import org.graphwalker.exceptions.GeneratorException;
import org.graphwalker.exceptions.InvalidDataException;
import org.graphwalker.exceptions.StopConditionException;
import org.jdom.JDOMException;

@WebService
public class SoapServices {

	private static Logger logger = Util.setupLogger(SoapServices.class);
	private Vector<String> stepPair = new Vector<String>();
	private String xmlFile = "";
	private boolean hardStop = false;

	public SoapServices() {
	}

	public SoapServices(String xmlFile) throws StopConditionException, GeneratorException, IOException, JDOMException, InterruptedException {
		if (xmlFile != null) {
			this.xmlFile = xmlFile;
			Util.loadMbtAsWSFromXml(Util.getFile(this.xmlFile));
		}
		Reset();
	}

	public boolean SetCurrentVertex(String newVertex) {
		logger.debug("SOAP service SetCurrentVertex recieving: " + newVertex);
		boolean value = ModelBasedTesting.getInstance().setCurrentVertex(newVertex);
		logger.debug("SOAP service SetCurrentVertex returning: " + value);
		return value;
	}

	public String GetDataValue(String data) {
		logger.debug("SOAP service getDataValue recieving: " + data);
		String value = "";
		try {
			value = ModelBasedTesting.getInstance().getDataValue(data);
		} catch (InvalidDataException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.error(e);
		}
		logger.debug("SOAP service getDataValue returning: " + value);
		return value;
	}

	public String ExecAction(String action) {
		logger.debug("SOAP service ExecAction recieving: " + action);
		String value = "";
		try {
			value = ModelBasedTesting.getInstance().execAction(action);
		} catch (InvalidDataException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.error(e);
		}
		logger.debug("SOAP service ExecAction returning: " + value);
		return value;
	}

	public void PassRequirement(String pass) {
		logger.debug("SOAP service PassRequirement recieving: " + pass);
		if (pass.equalsIgnoreCase("TRUE"))
			ModelBasedTesting.getInstance().passRequirement(true);
		else if (pass.equalsIgnoreCase("FALSE"))
			ModelBasedTesting.getInstance().passRequirement(false);
		else
			logger.error("SOAP service PassRequirement dont know how to handle: " + pass + "\nOnly the strings true or false are permitted");
	}

	public String GetNextStep() {
		logger.debug("SOAP service getNextStep");
		try {
			String value = "";

			if (!ModelBasedTesting.getInstance().hasNextStep() && (stepPair.size() == 0)) {
				return value;
			}

			if (stepPair.size() == 0) {
				try {
					stepPair = new Vector<String>(Arrays.asList(ModelBasedTesting.getInstance().getNextStep()));
				} catch (Exception e) {
					hardStop = true;
					return "";
				}
			}

			value = (String) stepPair.remove(0);
			value = value.replaceAll("/.*$", "");
			String addInfo = "";

			if (stepPair.size() == 1) {
				ModelBasedTesting.getInstance().logExecution(ModelBasedTesting.getInstance().getMachine().getLastEdge(), addInfo);
				if (ModelBasedTesting.getInstance().isUseStatisticsManager()) {
					ModelBasedTesting.getInstance().getStatisticsManager().addProgress(ModelBasedTesting.getInstance().getMachine().getLastEdge());
				}
			} else {
				ModelBasedTesting.getInstance().logExecution(ModelBasedTesting.getInstance().getMachine().getCurrentVertex(), addInfo);
				if (ModelBasedTesting.getInstance().isUseStatisticsManager()) {
					ModelBasedTesting.getInstance().getStatisticsManager()
					    .addProgress(ModelBasedTesting.getInstance().getMachine().getCurrentVertex());
				}
			}
			return value;
		} finally {
			if (ModelBasedTesting.getInstance().isUseGUI()) {
				App.getInstance().setButtons();
			}
		}
	}

	public boolean HasNextStep() {
		logger.debug("SOAP service hasNextStep");
		boolean value = false;
		if (hardStop) {
			value = false;
		} else {
			if (stepPair.size() != 0) {
				value = true;
			} else {
				value = ModelBasedTesting.getInstance().hasNextStep();
			}
		}
		logger.debug("SOAP service hasNextStep returning: " + value);
		if (value == false)
			logger.info(ModelBasedTesting.getInstance().getStatisticsString());
		return value;
	}

	public boolean Reload() {
		logger.debug("SOAP service reload");
		boolean retValue = true;
		try {
			Util.loadMbtAsWSFromXml(Util.getFile(this.xmlFile));
		} catch (Exception e) {
			Util.logStackTraceToError(e);
			retValue = false;
		}
		Reset();
		logger.debug("SOAP service reload returning: " + retValue);
		return retValue;
	}

	public boolean Load(String xmlFile) {
		logger.debug("SOAP service load recieving: " + xmlFile);
		if ( xmlFile == null ) {
			logger.error( "Web service 'Load' needs a valid xml file name." );
			logger.debug("SOAP service load returning: false");
			return false;
		}
		if (new File(xmlFile).canRead()==false) {
			logger.error( "Web service 'Load' needs a readable xml file name. Check the file: '" + xmlFile + "'" );
			logger.debug("SOAP service load returning: false");
			return false;
		}
		this.xmlFile = xmlFile;
		boolean retValue = true;
		try {
			Util.loadMbtAsWSFromXml(Util.getFile(this.xmlFile));
		} catch (Exception e) {
			Util.logStackTraceToError(e);
			retValue = false;
		}
		Reset();
		logger.debug("SOAP service load returning: " + retValue);
		return retValue;
	}

	public String GetStatistics() {
		logger.debug("SOAP service getStatistics");
		return ModelBasedTesting.getInstance().getStatisticsVerbose();
	}

	private void Reset() {
		hardStop = false;
		stepPair.clear();
	}
}
