package slak;

import static slak.Token.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Transpiles pseudocode to JavaScript.
 * @author Stefan Silviu
 */
public class Parser {
	/** Raw input, output and token list. */
	private String input, output, tokenList;
	/** List of tokens. */
	private ArrayList<Token> tokens = new ArrayList<Token>();
	/** List of referenced variables. */
	private ArrayList<String> varReferences = new ArrayList<String>();
	/** Keys are indices in the token array and values are the data(string, number or variable identifier) */
	private HashMap<Integer, String> metadata = new HashMap<Integer, String>();
	
	/**
	 * Gets the pseudocode from a file and outputs the JavaScript to another.
	 * If one of the files is not specified it will default to files in the current directory(in.txt, out.txt, tokens.cfg).
	 * @param in the path to the input file
	 * @param out the path to the output file
	 * @param cfg the path to the config file
	 */
	public Parser(String in, String out, String cfg) {
		if (in == null) in = System.getProperty("user.dir")+"/in.txt";
		if (out == null) out = System.getProperty("user.dir")+"/out.txt";
		if (cfg == null) cfg = System.getProperty("user.dir")+"/tokens.cfg";
		try (FileWriter fw = new FileWriter(new File(out))) {
			input = readFile(in);
			tokenList = readFile(cfg);
			parsePseudocode();
			fw.write(output);
		} catch (IOException e) {
			throw new IllegalArgumentException("IO error: check input files.\n", e);
		}
	}
	
	/**
	 * Creates JavaScript from given data and stores it in {@link #output}.
	 * @param rawInput the pseudocode
	 * @param rawTokenList the token list
	 */
	public Parser(String rawInput, String rawTokenList) {
		input = rawInput;
		tokenList = rawTokenList;
	}
	
	/**
	 * Gets the data from a file as a string.
	 * @param path the file path
	 * @return the string
	 * @throws IOException
	 */
	private static String readFile(String path) throws IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
			StringBuilder input = new StringBuilder();
			String tmp; while ((tmp = br.readLine()) != null) input.append(tmp+"\n");
			return input.toString();
		}
	}
	
	/**
	 * Makes the operations required to transform the input pseudocode to JavaScript using the stored data.
	 * @return the JavaScript code
	 */
	public void parsePseudocode() {
		putTokens();
		setTokenLength();
		tokenize();
		dataToJS();
	}
	
	/**
	 * Set all 1-char tokens isOneChar field to true, and the multi-char tokens to false.
	 */
	private void setTokenLength() {
		for (Token ts : Token.values()) {
			if (ts.getValue().length() == 1) ts.setOneChar(true);
			else ts.setOneChar(false);
		}
	}
	
	/**
	 * Set the token enums to the given values. If a value is not specified, it will default to the values in {@link Token}.
	 */
	private void putTokens() {
		String[] lines = tokenList.split("\n");
		for (int i = 0; i < lines.length; i++) {
			String[] line = lines[i].split("@", 2);
			//If any token matches the given name (line[0]), set its value to the provided one (line[1]).
			for (Token st : Token.values()) if (st.toString().equals(line[0].trim())) st.setValue(line[1].trim());
		}
	}
	
	/**
	 * Outputs and throws the exception.
	 * @param ex the exception
	 */
	private void outputAndThrow(RuntimeException ex) {
		output += "\n" + ex.getMessage();
		System.out.println(output);
		throw ex;
	}
	
	/**
	 * Transforms tokens with metadata in valid JavaScript.
	 * @return the JS code
	 */
	private void dataToJS() {
		StringBuilder code = new StringBuilder();
		for (int i = 0; i < tokens.size(); i++) {
			switch (tokens.get(i)) {
			case INPUT:
				if (tokens.get(i+1) != VARIABLE_NAME) outputAndThrow(new RuntimeException("Error: Expected variable identifier.\n"));
				if (isReferenced(metadata.get(i+1))) outputAndThrow(new RuntimeException("Error: Cannot read an already created variable.\n"));
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
						i = appendUntil(COMMA, code, i);
						code.append(");\n");
						if (i+1 >= tokens.size()) break;
					}
				} else outputAndThrow(new RuntimeException("Error: Can only output variables, constants or expressions.\n"));
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
					i = appendUntil(LINE, code, i);
					code.append(";\n");
				} else outputAndThrow(new RuntimeException("Error: Expected assignment.\n"));
				break;
			case IF:
				code.append("if (");
				i++;
				i = appendUntil(THEN, code, i);
				code.append(") {\n");
				break;
			case ELSE:
				code.append("} else {\n");
				break;
			case WHILE:
				code.append("while (");
				i++;
				i = appendUntil(EXECUTE, code, i);
				code.append(") {\n");
				break;
			case END:
				code.append("}\n");
				break;

			default:
				break;
			}
		}
		output = code.toString();
	}
	
	/**
	 * Appends tokens until the specified token is encountered.
	 * @param what the token to stop at
	 * @param code where to append
	 * @param i index to use
	 * @return the updated index
	 */
	private int appendUntil(Token what, StringBuilder code, int i) {
		while (tokens.get(i) != what) {
			if (tokens.get(i) == STRING || tokens.get(i) == NUMBER) code.append(metadata.get(i));
			else if (tokens.get(i) == VARIABLE_NAME) {
				if (isReferenced(metadata.get(i))) code.append(metadata.get(i));
				else outputAndThrow(new RuntimeException("Error: Variable undefined.\n"));
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
	 * Checks if the variable has been referenced before.
	 * @param item the variable
	 * @return whether it has been referenced
	 */
	private boolean isReferenced(String item) {
		if (varReferences.contains(item)) return true;
		else return false;
	}
	
	/**
	 * Creates a list of tokens and their metadata from the input string.
	 */
	private void tokenize() {
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
				string.insert(0, "\"");
				string.append("\"");
				tokens.add(STRING);
				metadata.put(tokens.size()-1, string.toString());
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
	}
	
	//Getters:
	
	public String getInput() {
		return input;
	}

	public String getOutput() {
		return output;
	}
	
	public String getTokenList() {
		return tokenList;
	}

	public ArrayList<Token> getTokens() {
		return tokens;
	}

	public ArrayList<String> getVarReferences() {
		return varReferences;
	}

	public HashMap<Integer, String> getMetadata() {
		return metadata;
	}
}
