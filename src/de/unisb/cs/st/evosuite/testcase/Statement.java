/*
 * Copyright (C) 2009 Saarland University
 * 
 * This file is part of Javalanche.
 * 
 * Javalanche is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Javalanche is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser Public License
 * along with Javalanche.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.unisb.cs.st.evosuite.testcase;

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Abstract superclass of test case statements
 * 
 * @author Gordon Fraser
 *
 */
public abstract class Statement {

	protected static Logger logger = Logger.getLogger(Statement.class);

	VariableReference retval = null;
	
	protected List<Assertion> assertions = new ArrayList<Assertion>();
	
	protected Throwable exceptionThrown = null;

	/**
	 * Adjust all variables up to position by delta
	 * @param position
	 * @param delta
	 */
	public abstract void adjustVariableReferences(int position, int delta);

	/**
	 * Check if the statement makes use of var
	 * @param var
	 *   Variable we are checking for
	 * @return
	 *   True if var is referenced
	 */
	public abstract boolean references(VariableReference var);

	public abstract Throwable execute(Scope scope, PrintStream out) throws InvocationTargetException, IllegalArgumentException, IllegalAccessException, InstantiationException;
	
	/**
	 * RepOK function
	 * @return
	 *   True if this is a valid statement
	 */
	public abstract boolean isValid();
	
	/**
	 * Get Java representation of statement
	 * @return
	 */
	public abstract String getCode();
	
	/**
	 * Get Java representation of statement
	 * @return
	 */
	public abstract String getCode(Throwable exception);
	
	/**
	 * 
	 * @return
	 *   Generic type of return value
	 */
	public Type getReturnType() {
		return retval.getType();
	}

	/**
	 * 
	 * @return
	 *   Raw class of return value
	 */
	public Class<?> getReturnClass() {
		return (Class<?>)retval.getType();
	}

	/**
	 * Equality check
	 * @param s
	 *   Other statement
	 * @return
	 *   True if equals
	 */
	public abstract boolean equals(Statement s);
	
	/**
	 * Generate hash code
	 */
	public abstract int hashCode();
	
	
	/**
	 * @return
	 *   Variable representing return value
	 */
	public VariableReference getReturnValue() {
		return retval;
	}

	public abstract Set<VariableReference> getVariableReferences();

	public abstract void replace(VariableReference old_var, VariableReference new_var);
	
	/**
	 * Create copies of all attached assertions
	 * 
	 * @return
	 *   List of the assertion copies
	 */
	protected List<Assertion> cloneAssertions() {
		List<Assertion> copy = new ArrayList<Assertion>();
		for(Assertion a: assertions) {
			if(a == null) {
				logger.info("Assertion is null!");
				logger.info("Statement has assertions: "+assertions.size());
			} else
			copy.add(a.clone());
		}
		return copy;
	}
	
	/**
	 * Create deep copy of statement
	 */
	public abstract Statement clone();
	
	/**
	 * Check if there are assertions
	 * 
	 * @return
	 *   True if there are assertions
	 */
	public boolean hasAssertions() {
		return !assertions.isEmpty();
	}
	
	/**
	 * Add a new assertion to statement
	 * 
	 * @param assertion
	 *   Assertion to be added
	 */
	public void addAssertion(Assertion assertion) {
		/*
		for(Assertion a: assertions) {
			if(a.equals(assertion)) {
				logger.info("Already have this assertion!");
				logger.info(assertion.getCode());
				logger.info(a.getCode());
				return;
			}
		}
		*/
		if(assertion == null) {
			logger.warn("Trying to add null assertion!");
		} else {
			logger.info("Adding assertion");
			assertions.add(assertion);
		}
	}
	
	/**
	 * Get Java code representation of assertions
	 * 
	 * @return
	 *   String representing all assertions attached to this statement
	 */
	public String getAssertionCode() {
		String ret_val = "";
		for(Assertion a : assertions) {
			if(a != null)
				ret_val += a.getCode() +"\n";
		}
		return ret_val;
	}
	
	/**
	 * Fix variable references in assertions
	 * 
	 * @param position
	 * @param delta
	 */
	public void adjustAssertions(int position, int delta) {
		for(Assertion a : assertions) {
			if(a != null)
				a.source.adjust(delta, position);
		}
	}	
	
	/**
	 * Delete all assertions attached to this statement
	 */
	public void removeAssertions() {
		assertions.clear();
	}
}
