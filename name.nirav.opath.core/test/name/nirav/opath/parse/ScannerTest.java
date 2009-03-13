/*******************************************************************************
 * Copyright (c) 2009 Nirav Thaker.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *******************************************************************************/
package name.nirav.opath.parse;

import junit.framework.TestCase;
import name.nirav.opath.parse.Scanner;
import name.nirav.opath.parse.Scanner.Token;
import name.nirav.opath.parse.Scanner.Type;

import org.junit.Test;

/**
 * 
 * 
 */
public class ScannerTest extends TestCase {

	/**
	 * Test method for {@link name.nirav.opath.parse.Scanner#moveNext()}.
	 */
	@Test
	public void testMoveNextFail() {
		try {
			new Scanner("    ");
			fail();
		} catch (Exception e) {
		}
	}

	@Test
	public void testMoveNext() {
		assertEquals(6, countToken(new Scanner("//test/*/@id")));
		assertEquals(9, countToken(new Scanner("//.[name = 'three']")));
	}

	@Test
	public void testPath() {
		assertEquals(12, countToken(new Scanner("/document/vendor[1]/contact[2]")));
		assertEquals(4, countToken(new Scanner("/bean/integers")));
		assertEquals(3, countToken(new Scanner("bean/integers")));
		assertEquals(9, countToken(new Scanner("my/child/./bean/integers")));
	}

	@Test
	public void testMoveNextAbbr() {
		assertEquals(13, countToken(new Scanner("./@document/vendor[1]/contact[2]")));
		assertEquals(6, countToken(new Scanner("/bean/../integers")));
		assertEquals(11, countToken(new Scanner("/map[@name='foo']/bar")));
		assertEquals(13, countToken(new Scanner("/map[@name='foo']/./bar")));
		assertEquals(10, countToken(new Scanner("//arritem/*/@idx[3] = 'literal'")));
		assertEquals(8, countToken(new Scanner("//object/../parent/@child[4]")));
	}

	@Test
	public void testMoveNextRelational() {
		assertEquals(3, countToken(new Scanner("1 > 1 ")));
		assertEquals(3, countToken(new Scanner("1 < 1 ")));
		assertEquals(3, countToken(new Scanner("1 > 1 ")));
		assertEquals(3, countToken(new Scanner("1 < 1 ")));
		assertEquals(5, countToken(new Scanner("3     <       2 >      1  ")));
		assertEquals(3, countToken(new Scanner("2!=2")));
		assertEquals(3, countToken(new Scanner("abc#test")));
		assertEquals(5, countToken(new Scanner("abc[#test]")));
		assertEquals(6, countToken(new Scanner("1 > 1 < 23 = 4")));
	}

	private int countToken(Scanner scanner) {
		int i = 0;
		while (true) {
			try {
				Token moveNext = scanner.moveNext();
				if (moveNext.type == Type.EOF)
					return i;
				i++;
			} catch (RuntimeException e) {
				if (e.getMessage().indexOf("EOF") != -1)
					return i;
				else
					return -1;
			}
		}
	}

}
