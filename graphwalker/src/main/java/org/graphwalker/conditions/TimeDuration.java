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

package org.graphwalker.conditions;

/**
 * Stops test execution after a certain amount of time has passed.
 * 
 * @author Johan Tejle
 * 
 */
public class TimeDuration extends StopCondition {

	private double duration;
	private double start_time;

	@Override
	public boolean isFulfilled() {
		return getFulfilment() >= 0.99999;
	}

	public TimeDuration(long seconds) {
		this.start_time = System.currentTimeMillis();
		this.duration = seconds * 1000;
	}

	@Override
	public double getFulfilment() {
		return (System.currentTimeMillis() - this.start_time) / this.duration;
	}

	@Override
	public String toString() {
		return "DURATION=" + (duration / 1000) + "s";
	}

	public void restartTime() {
		start_time = System.currentTimeMillis();
	}

}
