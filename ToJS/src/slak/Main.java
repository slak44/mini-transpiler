package slak;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static slak.Token.*;

public class Main {
	public static void main(String[] args) {
		parseFromFile(args[0], args[1]);
	}
	
	/**
	 * Gets the pseudocode from a file and outputs the JavaScript to another.
	 * If one of the files is not specified it will default to the current directory.
	 * @param in the input file
	 * @param out the output file
	 * @return whether the operation succedded
	 */
	public static boolean parseFromFile(String in, String out) {
		if (in == null) in = System.getProperty("user.dir")+"/in.txt";
		if (out == null) out = System.getProperty("user.dir")+"/out.txt";
		try (BufferedReader br = new BufferedReader(new FileReader(new File(in)));
				FileWriter fw = new FileWriter(new File(out))) {
			StringBuilder input = new StringBuilder();
			String tmp;
			while ((tmp = br.readLine()) != null) input.append(tmp+"\n");
			fw.write(parsePseudocode(input.toString()));
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Transpiles pseudocode into JavaScript.
	 * @param s the pseudocode
	 * @return the JavaScript code
	 */
	public static String parsePseudocode(String s) {
		for (Token ts : Token.values()) {
			if (ts.getValue().length() == 1) ts.setOneChar(true);
			else ts.setOneChar(false);
		}
		//Keys are the indices of the token array and values are the data(string, number or variable identifier)
		HashMap<Integer, String> metadata = new HashMap<Integer, String>();
		ArrayList<Token> tokens = tokenize(s, metadata);
		return dataToJS(tokens, metadata);
	}
	
	/**
	 * Transforms tokens with metadata in valid JavaScript.
	 * @param tokens the token list
	 * @param metadata the metadata list
	 * @return the JS code
	 */
	private static String dataToJS(ArrayList<Token> tokens, HashMap<Integer, String> metadata) {
		StringBuilder code = new StringBuilder();
		ArrayList<String> varReferences = new ArrayList<String>();
		for (int i = 0; i < tokens.size(); i++) {
			switch (tokens.get(i)) {
			case INPUT:
				if (tokens.get(i+1) != VARIABLE_NAME) throw new RuntimeException("Error: Expected variable identifier.\n");
				if (isReferenced(varReferences, metadata.get(i+1))) throw new RuntimeException("Error: Cannot read an already created variable.\n");
				else {
					code.append("var "+metadata.get(i+1)+" = prompt();\n");
					varReferences.add(metadata.get(i+1));
					i++;
				}
				break;
			case OUTPUT:
				i++;
				if (tokens.get(i).isOutputable()) {
					while (tokens.get(i).isOutputable()) {
						if (tokens.get(i) == COMMA) {
							i++;
							if (i >= tokens.size()) break;
							continue;
						}
						code.append("alert(");
						i = appendUntil(COMMA, tokens, metadata, varReferences, code, i);
						code.append(");\n");
						if (i+1 >= tokens.size()) break;
					}
				} else throw new RuntimeException("Error: Can only output variables, constants or expressions.\n");
				i--;
				break;
			case VARIABLE_NAME:
				if (i+1 >= tokens.size()) break;
				if (tokens.get(i+1) == ASSIGNMENT) {
					if (!varReferences.contains(metadata.get(i))) {
						code.append("var ");
						varReferences.add(metadata.get(i));
					}
					code.append(metadata.get(i)+" = ");
					i+=2;
					i = appendUntil(LINE, tokens, metadata, varReferences, code, i);
					code.append(";\n");
				} else throw new RuntimeException("Error: Expected assignment.\n");
				break;
			case IF:
				code.append("if (");
				i++;
				i = appendUntil(THEN, tokens, metadata, varReferences, code, i);
				code.append(") {\n");
				break;
			case WHILE:
				code.append("while (");
				i++;
				i = appendUntil(EXECUTE, tokens, metadata, varReferences, code, i);
				code.append(") {\n");
				break;
			case END:
				code.append("}\n");
				break;

			default:
				break;
			}
		}
		return code.toString();
	}
	
	/**
	 * Appends tokens until the specified token is encountered.
	 * @param what the token to stop at
	 * @param tokens list of tokens
	 * @param metadata token metadata
	 * @param varReferences variable reference list
	 * @param code where to append
	 * @param i index to use
	 * @return the updated index
	 */
	private static int appendUntil(Token what, ArrayList<Token> tokens, HashMap<Integer, String> metadata, ArrayList<String> varReferences, StringBuilder code, int i) {
		while (tokens.get(i) != what) {
			if (tokens.get(i) == STRING || tokens.get(i) == NUMBER) code.append(metadata.get(i));
			else if (tokens.get(i) == VARIABLE_NAME) {
				if (isReferenced(varReferences, metadata.get(i))) code.append(metadata.get(i));
				else throw new RuntimeException("Error: Variable undefined.\n");
			}
			else if (tokens.get(i) == PLUS) code.append("+");
			else if (tokens.get(i) == MINUS) code.append("-");
			else if (tokens.get(i) == MULTIPLY) code.append("*");
			else if (tokens.get(i) == DIVIDE) code.append("/");
			else if (tokens.get(i) == REMAINDER) code.append("%");
			else if (tokens.get(i) == TRUE) code.append("true");
			else if (tokens.get(i) == FALSE) code.append("false");
			else if (tokens.get(i) == AND) code.append("&&");
			else if (tokens.get(i) == OR) code.append("||");
			else if (tokens.get(i) == NOT) code.append("!");
			else if (tokens.get(i) == MORE_EQUALS) code.append(">=");
			else if (tokens.get(i) == LESS_EQUALS) code.append("<=");
			else if (tokens.get(i) == MORE_THAN) code.append(">");
			else if (tokens.get(i) == LESS_THAN) code.append("<");
			else if (tokens.get(i) == EQUALS) code.append("==");
			else if (tokens.get(i) == NOT_EQUALS) code.append("!=");
			else if (tokens.get(i) == LPAR) code.append("(");
			else if (tokens.get(i) == RPAR) code.append(")");
			else if (tokens.get(i) == LSQPAR) code.append("Number.parseInt(");
			else if (tokens.get(i) == RSQPAR) code.append(")");
			else if (tokens.get(i).isOutputable()) code.append(tokens.get(i).getValue());
			else break;
			if (i+1 >= tokens.size()) break;
			i++;
		}
		return i;
	}
	
	/**
	 * Checks if the given item is in the specified list.
	 * @param a the list
	 * @param item the item
	 * @return whether it is or not contained
	 */
	private static boolean isReferenced(ArrayList<String> a, String item) {
		if (a.contains(item)) return true;
		else return false;
	}
	
	/**
	 * Gets a list of tokens and their metadata from the input string.
	 * @param input the input string
	 * @param metadata where to store metadata
	 * @return the token list
	 */
	private static ArrayList<Token> tokenize(String input, HashMap<Integer, String> metadata) {
		ArrayList<Token> tokens = new ArrayList<Token>();
		//Checks char-by-char
		charLoop:
		for (int i = 0; i < input.length(); i++) {
			//Check if there's a new line
			if (input.charAt(i) == '\n') {
				tokens.add(LINE);
				continue;
			}
			
			//Iterate through the tokens and find if any matches
			for (Token st : Token.values()) {
				//Do not test for these because they have separate cases below
				if (st.equals(NUMBER) || st.equals(VARIABLE_NAME) || st.equals(STRING) || st.equals(LINE)) continue;
				//Skip whitespace & newlines
				if (Character.isWhitespace(input.charAt(i))) continue;
				
				//Checks for mono-character identifiers
				if (st.isOneChar() && input.charAt(i) == st.getValue().toCharArray()[0]) {
					tokens.add(st);
					continue charLoop;
				}
				
				//Checks for multi-character identifiers
				try{
					if (input.substring(i, i + st.getValue().length()).equals(st.getValue())) {
						tokens.add(st);
						i += st.getValue().length()-1;
						continue charLoop;
					} else continue;
				} catch(StringIndexOutOfBoundsException e) {continue;}
				//Ignore this exception because the identifiers might be larger than the input string. 
			}
			
			//Check if there is a string
			if (input.charAt(i) == '"') {
				i++;
				if (i >= input.length()) break;
				StringBuilder string = new StringBuilder();
				while (input.charAt(i) != '"') {
					string.append(input.charAt(i));
					i++;
					if (i >= input.length()) break;
				}
				tokens.add(STRING);
				metadata.put(tokens.size()-1, string.toString());
				i--;
				continue charLoop;
			}
			
			//Check if there is a number
			if (Character.isDigit(input.charAt(i))) {
				StringBuilder digits = new StringBuilder();
				while (Character.isDigit(input.charAt(i)) || input.charAt(i) == '.') {
					digits.append(input.charAt(i));
					i++;
					if (i >= input.length()) break;
				}
				if (digits.length() != 0) {
					tokens.add(NUMBER);
					metadata.put(tokens.size()-1, digits.toString());
					i--;
					continue charLoop;
				}
			}
			
			//Check if there is a variable reference/creation
			if (Character.isAlphabetic(input.charAt(i)) && !Character.isWhitespace(input.charAt(i))) {
				StringBuilder varName = new StringBuilder();
				while ( ( Character.isAlphabetic(input.charAt(i)) || Character.isDigit(input.charAt(i)) )
						&& !Character.isWhitespace(input.charAt(i))) {
					varName.append(input.charAt(i));
					i++;
					if (i >= input.length()) break;
				}
				if (varName.length() != 0) {
					tokens.add(VARIABLE_NAME);
					metadata.put(tokens.size()-1, varName.toString());
					i--;
					continue charLoop;
				}
			}
		}
		return tokens;
	}
}
