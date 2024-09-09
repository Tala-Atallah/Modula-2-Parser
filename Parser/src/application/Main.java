// Tala Atallah - 1202575
package application;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class Main extends Application {
	File file;
	static Map<String, Integer> terminals = new HashMap<>(); // Hashmap to store the terminals headings for the table
	static Map<String, Integer> nonterminals = new HashMap<>(); // Hashmap to store the non-terminals headings for the
																// table
	static Map<Integer, String> allTokens = new HashMap<>(); // all valid tokens from the scanner will be stored with
																// its index in this hashmap
	static Map<Integer, Integer> lines = new HashMap<>(); // each token position will be stored with the line it's
															 // presented in

	static Label label = new Label(); // label to display the final result
	// these are the terminals headings each with there index
	static {
		terminals.put("name", 0);
		terminals.put(".", 1);
		terminals.put("module", 2);
		terminals.put(";", 3);
		terminals.put("begin", 4);
		terminals.put("end", 5);
		terminals.put("const", 6);
		terminals.put("=", 7);
		terminals.put("var", 8);
		terminals.put(":", 9);
		terminals.put(",", 10);
		terminals.put("integer-value", 11);
		terminals.put("real-value", 12);
		terminals.put("char", 13);
		terminals.put("procedure", 14);
		terminals.put(":=", 15);
		terminals.put("(", 16);
		terminals.put(")", 17);
		terminals.put("+", 18);
		terminals.put("-", 19);
		terminals.put("*", 20);
		terminals.put("/", 21);
		terminals.put("mod", 22);
		terminals.put("div", 23);
		terminals.put("readint", 24);
		terminals.put("readreal", 25);
		terminals.put("readchar", 26);
		terminals.put("readln", 27);
		terminals.put("writeint", 28);
		terminals.put("writereal", 29);
		terminals.put("writechar", 30);
		terminals.put("writeln", 31);
		terminals.put("if", 32);
		terminals.put("then", 33);
		terminals.put("else", 34);
		terminals.put("while", 35);
		terminals.put("do", 36);
		terminals.put("loop", 37);
		terminals.put("until", 38);
		terminals.put("exit", 39);
		terminals.put("call", 40);
		terminals.put("|=", 41);
		terminals.put("<", 42);
		terminals.put("<=", 43);
		terminals.put(">", 44);
		terminals.put(">=", 45);
		terminals.put("integer", 46);
		terminals.put("real", 47);
	}
	// nonterminals heading each with there index
	static {
		nonterminals.put("module-decl", 0);
		nonterminals.put("module-heading", 1);
		nonterminals.put("block", 2);
		nonterminals.put("declarations", 3);
		nonterminals.put("const-decl", 4);
		nonterminals.put("const-list", 5);
		nonterminals.put("var-decl", 6);
		nonterminals.put("var-list", 7);
		nonterminals.put("var-item", 8);
		nonterminals.put("name-list", 9);
		nonterminals.put("more-names", 10);
		nonterminals.put("data-type", 11);
		nonterminals.put("procedure-decl", 12);
		nonterminals.put("procedure-heading", 13);
		nonterminals.put("stmt-list", 14);
		nonterminals.put("statement", 15);
		nonterminals.put("ass-stmt", 16);
		nonterminals.put("exp", 17);
		nonterminals.put("exp-prime", 18);
		nonterminals.put("term", 19);
		nonterminals.put("term-prime", 20);
		nonterminals.put("factor", 21);
		nonterminals.put("add-oper", 22);
		nonterminals.put("mul-oper", 23);
		nonterminals.put("read-stmt", 24);
		nonterminals.put("write-stmt", 25);
		nonterminals.put("write-list", 26);
		nonterminals.put("more-write-value", 27);
		nonterminals.put("write-item", 28);
		nonterminals.put("if-stmt", 29);
		nonterminals.put("else-part", 30);
		nonterminals.put("while-stmt", 31);
		nonterminals.put("loop-stmt", 32);
		nonterminals.put("exit-stmt", 33);
		nonterminals.put("call-stmt", 34);
		nonterminals.put("condition", 35);
		nonterminals.put("relational-oper", 36);
		nonterminals.put("name-value", 37);
		nonterminals.put("value", 38);
	}
	// The parsing table is entered in a static way in a 2D array
	private static final String[][] parsingTable = {
			{ null, null, "module-heading    declarations    block    name   .", null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null },
			{ null, null, "module        name      ;", null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null },
			{ null, null, null, null, "begin        stmt-list         end ", null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null },
			{ null, null, null, null, "const-decl    var-decl     procedure-decl", null,
					"const-decl    var-decl     procedure-decl", null, "const-decl    var-decl     procedure-decl",
					null, null, null, null, null, "const-decl    var-decl     procedure-decl", null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null },
			{ null, null, null, null, "", null, "const    const-list", null, "", null, null, null, null, null, "", null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null },
			{ "name      =    value      ;      const-list", null, null, null, "", null, null, null, "", null, null,
					null, null, null, "", null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null },
			{ null, null, null, null, "", null, null, null, "var    var-list", null, null, null, null, null, "", null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null },
			{ "var-item     ;      var-list", null, null, null, "", null, null, null, null, null, null, null, null,
					null, "", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null },
			{ "name-list       :       data-type", null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null },
			{ "name     more-names", null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null },
			{ null, null, null, null, null, null, null, null, null, "", ",     name-list", null, null, null, null, null,
					null, "", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null },
			{ null, null, null, null, null, null, null, null, null, null, null, null, null, "char", null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, "integer", "real" },
			{ null, null, null, null, "", null, null, null, null, null, null, null, null, null,
					"procedure-heading     declarations      block     name  ;     procedure-decl", null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null },
			{ null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					"procedure        name      ; ", null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null },
			{ "statement     ;     stmt-list", null, null, "statement     ;     stmt-list",
					"statement     ;     stmt-list", "", null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, "statement     ;     stmt-list",
					"statement     ;     stmt-list", "statement     ;     stmt-list", "statement     ;     stmt-list",
					"statement     ;     stmt-list", "statement     ;     stmt-list", "statement     ;     stmt-list",
					"statement     ;     stmt-list", "statement     ;     stmt-list", null, "",
					"statement     ;     stmt-list", null, "statement     ;     stmt-list", "",
					"statement     ;     stmt-list", "statement     ;     stmt-list", null, null, null, null, null,
					null, null },
			{ "ass-stmt", null, null, "", "block", null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, "read-stmt", "read-stmt", "read-stmt", "read-stmt",
					"write-stmt", "write-stmt", "write-stmt", "write-stmt", "if-stmt", null, null, "while-stmt", null,
					"loop-stmt", null, "exit-stmt", "call-stmt", null, null, null, null, null, null, null },
			{ "name     :=      exp", null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null },
			{ "term      exp-prime", null, null, null, null, null, null, null, null, null, null, "term      exp-prime",
					"term      exp-prime", null, null, null, "term      exp-prime", null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null },
			{ null, null, null, "", null, null, null, null, null, null, null, null, null, null, null, null, null, "",
					"add-oper     term     exp-prime", "add-oper     term     exp-prime", null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null },
			{ "factor        term-prime", null, null, null, null, null, null, null, null, null, null,
					"factor        term-prime", "factor        term-prime", null, null, null,
					"factor        term-prime", null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null },
			{ null, null, null, "", null, null, null, null, null, null, null, null, null, null, null, null, null, "",
					"", "", "mul-oper       factor       term-prime", "mul-oper       factor       term-prime",
					"mul-oper       factor       term-prime", "mul-oper       factor       term-prime", null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null },
			{ "name-value", null, null, null, null, null, null, null, null, null, null, "name-value", "name-value",
					null, null, null, "( exp )", null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null },
			{ null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, "+", "-", null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null },
			{ null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, "*", "/", "mod", "div", null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null },
			{ null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, "readint	( name-list )", "readreal	( name-list )",
					"readchar 	( name-list )", "readln", null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null },
			{ null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, "writeint 	( name-list )",
					"writereal 	( name-list )", "writechar 	( name-list )", "writeln", null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null },
			{ "write-item   more-write-value", null, null, null, null, null, null, null, null, null, null,
					"write-item   more-write-value", "write-item   more-write-value", null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null },
			{ null, null, null, null, null, null, null, null, null, null, ",     write-list", null, null, null, null,
					null, null, "", null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null },
			{ "name", null, null, null, null, null, null, null, null, null, null, "value", "value", null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null },
			{ null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					"if  condition   then   stmt-list   else-part    end", null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null },
			{ null, null, null, null, null, "", null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					"else     stmt-list", null, null, null, null, null, null, null, null, null, null, null, null,
					null },
			{ null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, "while      condition       do      stmt-list   end", null, null, null, null, null,
					null, null, null, null, null, null, null },
			{ null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, "loop      stmt-list       until        condition", null, null, null, null,
					null, null, null, null, null, null },
			{ null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, "exit", null, null, null, null, null, null, null, null },
			{ null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, "call name", null, null, null, null, null, null, null },
			{ "name-value       relational-oper        name-value", null, null, null, null, null, null, null, null,
					null, null, "name-value       relational-oper        name-value",
					"name-value       relational-oper        name-value", null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null },
			{ null, null, null, null, null, null, null, "=", null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, "|=", "<", "<=", ">", ">=", null, null },

			{ "name", null, null, null, null, null, null, null, null, null, null, "value", "value", null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null },
			{ null, null, null, null, null, null, null, null, null, null, null, "integer-value", "real-value", null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null } };

	@Override
	public void start(Stage primaryStage) {
		Button button = new Button("Choose A file");
		FileChooser filechooser = new FileChooser(); // we will use file chooser to choose the file we want to read
		List<String> validTokens = new ArrayList<>(); // the validtokens from the scanner will be stored here

		button.setOnAction(e -> {
			file = filechooser.showOpenDialog(primaryStage);
			if (file != null) {
				try {
					String inputfile = new String(Files.readAllBytes(file.toPath())); // read the file
					List<String> tokens = new ArrayList<>();
					StringBuilder token = new StringBuilder();
					StringBuilder sb = new StringBuilder(); // this string builder is to store all lexical errors
					char[] chars = inputfile.toCharArray(); // convert the file read into array of characters
					int line = 1; // count the lines of the file
					int tokcounter = 0; // this counter will be used to track the position of the token
					boolean scannerChecker = true; // this variable will be updated if there is a lexical error or not
					// each time you read a new file everything stored should be cleared
					lines.clear();
					validTokens.clear();
					allTokens.clear();
					// we will go through the file and take a character by character
					for (int i = 0; i < chars.length; i++) {
						char c = chars[i];
						if (c == '\n') // any new line detected we should increment the line counter
							line++;
						if (Character.isWhitespace(c)) { // any white spaces detected , new line , tab , space . etc..
							if (token.length() > 0) { // check if there is content in the stringbuilder to add to it
								tokens.add(token.toString()); // add the current token to the tokens array list
								lines.put(tokcounter++, line); // each token position is assigned with its line # in the
																// hashmap
								token.setLength(0); // after each true condition the string builder (token is reset to
													// zero) so it can proceed with other tokens
							}
							// to check if we should treat the number with a dot separately or as one token (real value)
							// we should check if a digit or dot exist
						} else if (Character.isDigit(c) || c == '.') {
							// if the current char is dot then check if the string builder already has stored something
							// typically it should store an integer , then check if the stored token in the string builder
							// is an integer by calling the function. and i <chars.length to check if there exist 
							// characters after the dot. besides checking if the next char is a digit
							if (c == '.' && token.length() > 0 && integerChecker(token.toString()) 
									&& i < chars.length - 1 && Character.isDigit(chars[i + 1])) {
								 
								token.append(c); // if all the above are true then append the dot to the string builder
							} else if (Character.isDigit(c)) { // else if c was a digit append it to the stringbuilder
								token.append(c);
							} else { // else it will be considered as a new token
								if (token.length() > 0) {
									tokens.add(token.toString()); // add it to the tokens list
									lines.put(tokcounter++, line);  // add its position and line in a the hash lines
									token.setLength(0); // clear the string builder
								}
								token.append(c); 
							}
						} else if ("().=+-*/<>:;|,{}".indexOf(c) != -1) { // we will check if the symbols exist , -1
																			// means the character doesn't exist
																			// that there is no current position
							if (token.length() > 0) {
								tokens.add(token.toString());
								lines.put(tokcounter++, line);
								token.setLength(0);

							}
							if (i < chars.length - 1 && ((c == '>' && chars[i + 1] == '=') // i < chars.length checks if
																							// there is characters after
																							// our current character
									// for the current token i should checkif there what exist in the next index , 
									// if one meets the conditions the symbol will be treated as one symbol
									|| (c == '<' && chars[i + 1] == '=') || (c == ':' && chars[i + 1] == '=') 

									|| (c == '|' && chars[i + 1] == '='))) {
								tokens.add("" + c + chars[i + 1]); // if true then add the current character with the
																	// next character
								lines.put(tokcounter++, line); // assign its line number

								i++;
							} else {
								tokens.add("" + c); // if not true then add the current character only (single token)
								lines.put(tokcounter++, line);

							}

						} else {
							token.append(c); // if the above conditions are false then append the character to the
												// string builder
						}
					}
					if (token.length() > 0) { // and then add the contents of string builder in the tokens list
						tokens.add(token.toString());
						lines.put(tokcounter++, line);

					}

					int position = 0;
					// we will loop through the araylist of tokens generated in the previous part
					for (String currToken : tokens) {
						if (!currToken.isEmpty()) {
							if (tokensChecker(currToken)) {
								if (reservedChecker(currToken)) { // call the function reserved word
									validTokens.add(currToken); // then add the current token in the valid array list
									allTokens.put(position, currToken); // and add it also to alltokens array list with
																		// its position
								} else if (symbolChecker(currToken)) { // check if it is a symbol and add it to the
																		// arraylist
									validTokens.add(currToken);
									allTokens.put(position, currToken);

								} else if (integerChecker(currToken)) { // if the token is an integer value
									validTokens.add("integer-value"); // then replace the value with integer-value to be
																		// then proceesed in the parser
									allTokens.put(position, currToken); // but we will put the value itself in all
																		// tokens array to access it if needed when an
																		// error occurs

								} else if (realChecker(currToken)) { // check if its real
									validTokens.add("real-value");
									allTokens.put(position, currToken);
								} else if (nameChecker(currToken)) { // check if its a name
									validTokens.add("name");
									allTokens.put(position, currToken);

								}
							} else { // if didn't match any of the above conditions then an invalid token will be
										// detected
								sb.append("Lexical Error: Invalid token  " + currToken + "  at line "
										+ lines.get(position - 1)).append("\n"); // append all lexical errors
								label.setText(sb.toString()); // display all lexical errors at once
								scannerChecker = false; // the checker is set to zero in order not to complete with the
														// parser
							}
						}
						position++;
					}

					if (scannerChecker == true) { // if everything in the scanner is true then go to the parser
						boolean res = parser(validTokens); // the valid tokens array will be given to the parser to
															// check
						// checking the result came from the parser and printing it just for more validation
						if (res)
							System.out.println("Successful Parsing");
						else
							System.out.println("Error while parsing: for more details look at the UI");
					}
				} catch (IOException e1) {
					System.err.println(e1.getMessage());

				}
			}
			;
		});

		VBox root = new VBox();
		root.setSpacing(20);
		root.getChildren().addAll(button,label);
		root.setAlignment(Pos.CENTER);
		Scene scene = new Scene(root, 400, 400);

		primaryStage.setScene(scene);
		primaryStage.show();

	}

	public boolean tokensChecker(String token) {
		// this function checks if the token is a valid token by checking if it's a name
		// , symbol, reserved word, integer or real
		if (reservedChecker(token) || symbolChecker(token) || nameChecker(token) == true
				|| integerChecker(token) == true || realChecker(token)) {
			return true;
		}

		return false;

	}

	public boolean reservedChecker(String token) {
		// this function returns true if the token is one of the reserved words listed
		// in the switch case
		switch (token) {
		case "module":
		case "begin":
		case "end":
		case "const":
		case "var":
		case "integer":
		case "real":
		case "char":
		case "procedure":
		case "mod":
		case "div":
		case "readint":
		case "readreal":
		case "readchar":
		case "readln":
		case "writeint":
		case "writereal":
		case "writechar":
		case "writeln":
		case "if":
		case "then":
		case "else":
		case "while":
		case "do":
		case "loop":
		case "until":
		case "exit":
		case "call":
			return true;

		default:
			break;

		}
		return false;
	}

	public boolean symbolChecker(String token) {

		// this function returns true if the token is one of the symbols listed in the
		// switch case

		switch (token) {
		case "=":
		case ">=":
		case "<=":
		case "<":
		case ">":
		case ";":
		case ".":
		case ":":
		case ")":
		case "(":
		case "{":
		case "}":
		case ",":
		case ":=":
		case "|=":
		case "+":
		case "-":
		case "*":
		case "/":
			return true;
		default:
			break;
		}

		return false;
	}

	public boolean nameChecker(String token) {
		char arrayofCharacters[] = token.toCharArray();
		// name checker
		if (Character.isLetter(token.charAt(0))) { // check if the first character is letter
			for (int i = 0; i < arrayofCharacters.length; i++) { // do a for loop and check the other characters if
																	// they're a letter or not
				char c = arrayofCharacters[i];
				if (!Character.isLetterOrDigit(c)) { // if our current character is not a letter or digit then return
														// false
					return false;
				}

			}
			return true; // if it checked all the token and it didn't return false then it's true
		}

		return false; // it will return false if the first character is not a letter

	}

	public boolean integerChecker(String token) {
		boolean integerchecker = true;
		char arrayofCharacters[] = token.toCharArray();
		// will go through the token and check if each is a digit , if so then it is an
		// integer else it's not and will return false
		for (int i = 0; i < arrayofCharacters.length; i++) {
			char c = arrayofCharacters[i];
			if (!Character.isDigit(c)) {
				integerchecker = false;
				break;
			}

		}
		if (integerchecker == true)
			return true;
		return false;

	}

	public boolean realChecker(String token) {
		boolean isdot = false; // it will keep track if there is a dot or not
		for (int i = 0; i < token.length(); i++) { // for loop on the token
			char c = token.charAt(i); // take the character of index i
			if (c == '.') { // if the current character is dot
				if (isdot) { // then check the isdot ( because if it was set to true this means multiple dots
								// were found and that's wrong)
					return false;
				}
				isdot = true; // if it is not dot this means this is the first dot found , set it to true
			} else if (!Character.isDigit(c)) { // then check the other characters if they're not numbers then return
												// false. it's not a real number
				return false;
			}
		}
		return isdot;
	}

	public static boolean parser(List<String> validTokens) {
		// for the parser we need to have the array list generated of valid tokens and a
		// stack
		Stack<String> parserStack = new Stack<>();
		String topOftheStack;
		String currToken;
		String productionRule;
		int counter = 0; // this counter is used to see the index of each token
		parserStack.push("module-decl"); // we need to push the first starting point in the stack
		validTokens.add("."); // this dot is added to handle the case if there is no dot at the end of
								// the code
		while (!parserStack.empty()) {
			// if the stack is not empty
			topOftheStack = parserStack.peek(); // take the top of the stack
			currToken = validTokens.get(0); // and the first token from the array

			if (topOftheStack.equals(currToken)) { // if top of the stack is equal to the token taken
				parserStack.pop(); // then pop the top of the stack
				validTokens.remove(0); // and remove the first token
				counter++; // keep the counter of indices updated so we can access them later from the
							// hashmap if an error occurs in a specific token

			} else if (nonterminals.containsKey(topOftheStack)) { // if the stack top is a nonterminal then we should
																	// access the parsing table
				int row = nonterminals.get(topOftheStack); // the row will be the stack top
				Integer column = terminals.get(currToken); // and the column will be the terminal ( current token we got
															// from the arraylist)
				if (column == null) { // if the column (terminal) is null then there is an error
					label.setText("Error: Unexpected token " + allTokens.get(counter - 1) + " on line "
							+ lines.get(counter - 1));
					// for each error occurs we should clear the stack and the array list so if we
					// read another file they won't be affected from what is left in the array and
					// the stack
					parserStack.clear();
					validTokens.clear();
					return false; // return false if there is parsing error
				}
				productionRule = parsingTable[row][column]; // the production rule will be accessed from the
															// parsingtable defined above
				if (productionRule == null) {
					// if the production rule is null then there will be an error
					label.setText("Error: Unexpected symbol on  " + allTokens.get(counter - 1) + " on line "
							+ lines.get(counter - 1));
					parserStack.clear();
					validTokens.clear();
					// the array and the stack will be cleared and return false
					return false;
				}
				parserStack.pop(); // if the production rule is not null then pop from the stack
				if (!productionRule.equals("")) { // check if the production rule is not lambda ( i referred to lambda
													// as "" in my production table)
					String[] prod = productionRule.trim().split("\\s+"); // if true then take the production rule and
																			// store it as strings after splitting each
																			// space
					for (int i = prod.length - 1; i >= 0; i--) { // do a reverse for loop to access the production rule
																	// from the end
						parserStack.push(prod[i]); // then push the splitted production rule in the stack
					}
				}
			}

			else {
				// anything else will be considered as error
				label.setText("Error: Unexpected symbol on  " + allTokens.get(counter - 1) + " on line "
						+ lines.get(counter - 1));
				parserStack.clear();
				validTokens.clear();
				return false;

			}
		}
		// after the stack is empty we should check if there is left tokens in the
		// arraylist
		// in order for the parsing to be true we should check that we have only one
		// element and it equals to dot
		// this dot is the dot that we put in the begining to ensure successful parsing
		if (validTokens.size() == 1 && validTokens.get(0).equals(".")) {
			label.setText("Parsed Successfuly"); 
			validTokens.clear(); // clear the array so you won't face any problems in the next file
			return true; // and return true
		} else {
			label.setText("Error in Parsing");
			validTokens.clear();
			return false;
		}

	}

	public static void main(String[] args) {
		launch(args);

	}
}
