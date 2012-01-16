/*
 * #%L
 * GraphWalker Core
 * %%
 * Copyright (C) 2011 - 2012 GraphWalker
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.graphwalker.webservice;

import org.graphwalker.core.GraphWalker;
import org.graphwalker.core.GraphWalkerImpl;
import org.graphwalker.core.model.Element;

import javax.jws.WebService;

@WebService
/**
 * <p>SoapServiceImpl class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public class SoapServiceImpl implements SoapService {
    
    private GraphWalker myGraphWalker;
    
    /** {@inheritDoc} */
    public void load(String file) {
        myGraphWalker = new GraphWalkerImpl(file);
    }

    /**
     * <p>reload.</p>
     */
    public void reload() {

    }

    /**
     * <p>hasNextStep.</p>
     *
     * @return a boolean.
     */
    public boolean hasNextStep() {
        return myGraphWalker.hasNextStep();
    }
    
    /**
     * <p>getNextStep.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getNextStep() {
        Element modelElement = myGraphWalker.getNextStep();
        return modelElement.getName();
    }
    

    /*

	public boolean SetCurrentVertex(String newVertex) {
		logger.debug("SOAP service SetCurrentVertex recieving: " + newVertex);
		boolean value = mbt.setCurrentVertex(newVertex);
		logger.debug("SOAP service SetCurrentVertex returning: " + value);
		return value;
	}

	public String GetDataValue(String data) {
		logger.debug("SOAP service getDataValue recieving: " + data);
		String value = "";
		try {
			value = mbt.getDataValue(data);
		} catch (InvalidDataException e) {
			logger.error(e);
		}
		logger.debug("SOAP service getDataValue returning: " + value);
		return value;
	}

	public String ExecAction(String action) {
		logger.debug("SOAP service ExecAction recieving: " + action);
		String value = "";
		try {
			value = mbt.execAction(action);
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
		if ("TRUE".equalsIgnoreCase(pass)) {
			mbt.passRequirement(true);
		} else if ("FALSE".equalsIgnoreCase(pass)) {
			mbt.passRequirement(false);
		} else {
			logger.error("SOAP service PassRequirement dont know how to handle: " + pass + "\nOnly the strings true or false are permitted");
		}
	}

	public boolean Reload() {
		logger.debug("SOAP service reload");
		boolean retValue = true;
		boolean useGui = mbt.isUseGUI();
		try {
			if (!this.xmlFile.isEmpty()) {
				mbt = Util.loadMbtAsWSFromXml(Util.getFile(this.xmlFile));
				//if (useGui) {
				//	mbt.setUseGUI();
				//}
			}
		} catch (Exception e) {
			Util.logStackTraceToError(e);
			retValue = false;
		} finally {
			if (mbt.isUseGUI()) {
				//mbt.getGui().setMbt(mbt);
				mbt.getGui().setButtons();
				mbt.getGui().updateLayout();
			}
		}
		Reset();
		logger.debug("SOAP service reload returning: " + retValue);
		return retValue;
	}

	public String GetStatistics() {
		logger.debug("SOAP service getStatistics");
		return mbt.getStatisticsVerbose();
	}
    */
}
