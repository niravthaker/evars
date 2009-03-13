/*******************************************************************************
 * Copyright (c) 2009 Nirav Thaker.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *******************************************************************************/
package name.nirav.opath.parse;

/**
 * 
 * 
 */
public class Scanner {
	private interface Comparator {
		boolean matches(char ch);
	}

	private String stream;
	private int pos = 0;
	private Token currentToken;
	private Comparator javaIDComparator = new Comparator() {
		public boolean matches(char ch) {
			return Character.isJavaIdentifierPart(ch);
		}
	};

	public enum Type {
		SLASH('/'),
		DSLASH("//"),
		EQ('='),
		NEQ("!="),
		LT('<'),
		GT('>'),
		STAR('*'),
		LSQBR('['),
		RSQBR(']'),
		DOT('.'),
		DOTDOT(".."),
		ATR('@'),
		NUMBER(),
		MI('#'),
		LITERAL('\''),
		QNAME,
		EOF;
		char c;
		private String str;

		Type() {
			c = 0;
			str = "";
		}

		Type(String str) {
			this.str = str;
		}

		Type(char c) {
			this.c = c;
		}

		@Override
		public String toString() {
			return (int) c == 0 ? str.length() != 0 ? name() + ":"+ str : name() : Character.toString(c);
		}
	}

	public static class Token {
		public Token(Type tp) {
			type = tp;
		}

		public Token(Type type, Object val) {
			this.type = type;
			value = val;
		}

		Type type;
		Object value;

		@Override
		public String toString() {
			return value == null ? type.toString() : type + "=" + value;
		}
	}

	public Scanner() {
	}

	public Scanner(String str) {
		init(str);
	}

	public void init(String stream) {
		if (stream == null || stream.trim().length() == 0)
			throw new IllegalArgumentException("Input string can not be null or empty");
		this.stream = stream;
	}

	public Token moveNext() {
		for (; pos < stream.length();) {
			Character ch = this.stream.charAt(pos++);
			switch (ch) {
			case '>':
					return createToken(Type.GT);
			case '<':
				return createToken(Type.LT);
			case '#':
					return createToken(Type.MI);
			case '=':
				return createToken(Type.EQ);
			case '!':
				if (pos < stream.length() && stream.charAt(pos) == '=') {
					pos++;
					return createToken(Type.NEQ);
				}
			case '*':
				return createToken(Type.STAR, "*");
			case '[':
				return createToken(Type.LSQBR);
			case ']':
				return createToken(Type.RSQBR);
			case '.':
				if (pos < stream.length() && stream.charAt(pos) == '.') {
					pos++;
					return createToken(Type.DOTDOT);
				} else
					return createToken(Type.DOT);
			case '@':
				pos++;
				return createToken(Type.ATR, readAString(new Comparator() {
					public boolean matches(char ch) {
						return ch == '/' || ch == '=' ? false : true;
					}
				}).toString());
			case '\'':
//				String literalTok = readAString(javaIDComparator).toString();
//				pos++;
				return createToken(Type.LITERAL);
			case '/':
				if (pos < stream.length() && stream.charAt(pos) == '/') {
					pos++;
					return createToken(Type.DSLASH);
				} else
					return createToken(Type.SLASH);

			case ' ':
			case '\t':
			case '\r':
			case '\n':
				while (pos < stream.length() && Character.isWhitespace((stream.charAt(pos++))))
					;
				if (pos >= stream.length())
					throw new RuntimeException("EOF");
				pos--;
				break;
			default:
				if (Character.isDigit(ch)) {
					return createToken(Type.NUMBER, Integer.parseInt(readAString(new Comparator() {
						public boolean matches(char ch) {
							return Character.isDigit(ch);
						}
					}).toString()));
				}
				else if (Character.isLetter(ch)) {
					return createToken(Type.QNAME, readAString(javaIDComparator).toString());
				}
				else {
					throw new IllegalArgumentException("Found unacceptable symbol : " + ch);
				}
			}
		}
		if(currentToken.type == Type.EOF)
			throw new IllegalStateException("Already at EOF!");
		return createToken(Type.EOF);
	}

	private Token createToken(Type type) {
		Token token = new Token(type);
		this.currentToken = token;
//		print();
		return token;
	}

	private Token createToken(Type type, Object obj) {
		Token token = new Token(type, obj);
		this.currentToken = token;
//		print();
		return token;
	}

	private StringBuilder readAString(Comparator comp) {
		int i = pos - 1;
		char c;
		StringBuilder buffer = new StringBuilder();
		while (i < stream.length() && comp.matches((c = stream.charAt(i)))) {
			buffer.append(c);
			i++;
		}
		pos = i;
		return buffer;
	}

	protected void print() {
		System.err.println(this.stream);
		System.err.flush();
		for (int i = 0; i < this.pos - 1; i++)
			System.err.print(" ");
		System.err.println("^ 	Current tok: '" + this.currentToken + "'");
		System.err.flush();
	}

	public Token getCurrentToken() {
		return this.currentToken;
	}
}
