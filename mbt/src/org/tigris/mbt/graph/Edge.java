package org.tigris.mbt.graph;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.tigris.mbt.Keywords;
import org.tigris.mbt.Util;

public class Edge extends AbstractElement {

	private String guardKey = new String();
	private Float weightKey = new Float(0);

	public Edge() {
		super();
	}

	public Edge(Edge edge) {
		super(edge);
		this.guardKey = edge.guardKey;
		this.weightKey = edge.weightKey;
	}

	public Edge(Edge A, Edge B) {
		super(A, B);
		if (A.getFullLabelKey().length() > B.getFullLabelKey().length()) {
			this.guardKey = A.guardKey;
			this.weightKey = A.weightKey;
		} else {
			this.guardKey = B.guardKey;
			this.weightKey = B.weightKey;
		}
	}

	public float getWeightKey() {
		return weightKey;
	}

	public void setWeightKey(float weightKey) {
		if (weightKey < 0 || weightKey > 1)
			throw new RuntimeException("The value of weight, must be between 0 <= weight <= 1");
		this.weightKey = weightKey;
	}

	public String getGuardKey() {
		return guardKey;
	}

	public void setGuardKey(String guardKey) {
		this.guardKey = guardKey;
	}
	
	/**
	 * The label of an edge has the following format:
	 * Label Parameter [Guard] / Action1;Action2;ActionN;
	 * Keyword
	 * Where the Label, Parameter. Guard, Actions and Keyword are optional.
	 * @param str
	 * @return
	 */
	static public String[] getGuardAndActions( String str ) {
		Pattern p = Pattern.compile("(.*)", Pattern.MULTILINE);
		Matcher m = p.matcher(str);
		String label = null;
		String[] guardAndAction = {"",""}; 
		if (m.find()) {
			label = m.group(1);

			// Look for a Guard
			Pattern firstLinePattern = Pattern.compile("\\[(.*)\\]\\s*/|\\[(.*)\\]\\s*$", Pattern.MULTILINE);
			Matcher firstLineMatcher = firstLinePattern.matcher(label);
			if (firstLineMatcher.find()) {
				// Since we have 2 groups in the pattern, we have to check which
				// one is valid.
				String guard = firstLineMatcher.group(1);
				if (guard == null) {
					guard = firstLineMatcher.group(2);
				}
				guardAndAction[0] = guard;
			}

			// Look for Actions
			// To simplify this we wash the string by removing the guard
			// from a temporary string and make the search.
			String washedLabel = label.replace(guardAndAction[0], "");
			Pattern actionPattern = Pattern.compile("/\\s*(.*)\\s*$", Pattern.MULTILINE);
			Matcher actionMatcher = actionPattern.matcher(washedLabel);
			if (actionMatcher.find()) {
				guardAndAction[1] = actionMatcher.group(1);
			}
		}
		return guardAndAction;
	}

	/**
	 * The label of an edge has the following format:
	 * Label Parameter [Guard] / Action1;Action2;ActionN;
	 * Keyword
	 * Where the Label, Parameter. Guard, Actions and Keyword are optional.
	 * @param str
	 * @return
	 */
	static public String[] getLabelAndParameter( String str ) {
		Pattern p = Pattern.compile("(.*)", Pattern.MULTILINE);
		Matcher m = p.matcher(str);
		String label = null;
		String[] labelAndParameter = {"",""}; 
		if (m.find()) {
			label = m.group(1);

			// Look for the Label and Parameter
			Pattern firstLinePattern = Pattern.compile("^(\\w+)\\s?([^/^\\[]+)?", Pattern.MULTILINE);
			Matcher firstLineMatcher = firstLinePattern.matcher(label);
			if (firstLineMatcher.find()) {
				String label_key = firstLineMatcher.group(1);
				if (Keywords.isKeyWord(label_key)) {
					throw new RuntimeException("Edge has a label '" + label + "', which is a reserved keyword");
				}
				labelAndParameter[0] = label_key;

				String parameter = firstLineMatcher.group(2);
				if (parameter != null) {
					parameter = parameter.trim();
					labelAndParameter[1] = parameter;
				}
			}
		} else {
			throw new RuntimeException("Label for edge must be defined");
		}
		return labelAndParameter;
	}
	
	/**
	 * If weight is defined, find it...
	 * weight must be associated with a value, which depicts the
	 * probability for the edge to be executed.
	 * A value of 0.05 is the same as 5% chance of going down this road.
	 * @param str
	 * @return
	 */
	static public float getWeight( String str ) {
		Pattern p = Pattern.compile("\\n(weight\\s*=\\s*(.*))", Pattern.MULTILINE);
		Matcher m = p.matcher(str);
		Float weight = new Float(0);
		if (m.find()) {
			String value = m.group(2);
			try {
				weight = Float.valueOf(value.trim());
			} catch (NumberFormatException error) {
				throw new RuntimeException("For label: " + str + ", weight is not a correct float value: " + error.toString());
			}
		}
		return weight;
	}

	/**
	 * If BACKTRACK is defined, find it...
	 * If defined, it means that executing this edge may not lead to the
	 * desired vertex. So, if that happens, the model gives the user a chance to
	 * try another edge from the previous vertex (the source vertex of the
	 * edge defined with BACKTRACK)
	 * @param str
	 * @return
	 */
	static public Boolean isBacktrack( String str ) {
		Pattern p = Pattern.compile("\\n(BACKTRACK)", Pattern.MULTILINE);
		Matcher m = p.matcher(str);
		if (m.find()) {
			return true;
		}
		return false;
	}
}
