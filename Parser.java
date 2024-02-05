import java.io.IOException;
import java.util.StringTokenizer;
import java.util.HashMap;

/**
 * Parse a DOPL source file and report either that the file is ok
 * or it contains an error.
 * Parsing terminates on the first error.
 * 
 * @author
 * @version
 */
public class Parser {

  String[] tokens;
  int index = 0;
  int count = 0;
  HashMap<String, String> map = new HashMap<>();
  HashMap<String, String> variables = new HashMap<>();

  /**
   * Create a parser.
   * 
   * @param filename The file to be translated.
   * @throws IOException on any input issue.
   */
  public Parser(String filename)
      throws IOException {
    createTable();
    Tokenizer Tokenizer = new Tokenizer();
    String fileAsString = Tokenizer.read(filename, map);
    tokens = fileAsString.split(" ");

  }

  /**
   * Parse the DOPL source file.
   * 
   * @throws IOException on any input issue.
   */

  public void parse()
      throws IOException {
    Boolean test = parseProgram();
    if(test){
      System.out.println("OK");
    }
    else{
      System.out.println("ERROR");
    }
   

  }

  public void createTable() {

    map.put("start", "program"); // needs a space
    map.put("finish", "program");
    map.put("integer", "dataType"); // needs a space
    map.put("character", "dataType"); // needs a space
    map.put("logical", "dataType"); // needs a space
    map.put("if", "conditional"); // needs a space IF character after it is NOT (
    map.put("then", "conditional"); // needs a space
    map.put("else", "conditional"); // needs a space
    map.put("endif", "conditional"); // needs a ;
    map.put("print", "print"); // needs a space
    map.put("loopif", "loop"); // needs a space IF character after it is NOT (
    map.put("do", "loop");
    map.put("endloop", "loop"); // needs a ;
    map.put(".plus.", "arithmeticOp");
    map.put(".minus.", "arithmeticOp");
    map.put(".mul.", "arithmeticOp");
    map.put(".div.", "arithmeticOp");
    map.put(".and.", "logicalOp");
    map.put(".or.", "logicalOp");
    map.put(".eq.", "relationalOp");
    map.put(".ne.", "relationalOp");
    map.put(".lt.", "relationalOp");
    map.put(".gt.", "relationalOp");
    map.put(".le.", "relationalOp");
    map.put(".ge.", "relationalOp");
    map.put(".not.", "unaryOp");
    map.put("<-", "symbol");
    map.put(";", "symbol");
    map.put("\"", "symbol");
    map.put(",", "symbol");
    map.put("(", "symbol");
    map.put(")", "symbol");

  }

  public Boolean contains(String word) {
    for (String key : map.keySet()) {
      if (word.equals(key)) {
        return true;
      }

    }
    return false;
  }

  public Boolean parseProgram() {
   
    if (!tokens[index].equals("start")) {

      return false;
    }
    if ((index + 1) < tokens.length) {
      index++;
    } else {
      System.out.println("ERROR");
    }

    if (!parseDeclarations()) {

      return false;
    }
    index--;
    
    if (!parseStatements()) {
      return false;
    }
    if (count > 0) {
      index++;
    }
    
    if (!tokens[tokens.length-1].equals("finish")) {
      return false;
    }
    index++;


    

    return true;

  }

  public Boolean parseDeclarations() {

    // add loop to keep parsing declarations
    String token;

    token = tokens[index];
    index++;

    while (token.equals("character") || token.equals("integer") ||
        token.equals("logical")) {
      String dataType = token;
     
      if (!parseDeclaration(dataType)) {
        return false;
      }

      if (!tokens[index].equals(";")) {

       
        return false;
      }
      index++;
      if (index < tokens.length) {
        token = tokens[index];
      } else {
        return false;
      }

      index++;

    }
    return true;
  }

  public Boolean parseDeclaration(String dataType) {
    String variable = tokens[index];
  
    index++;
    if (!Character.isLetter(variable.charAt(0)) || map.containsKey(variable)) { // If string starts with a letter, it's
                                                                                // a correct variable
      return false;
    }
    variables.put(variable, dataType);

    while (tokens[index].equals(",")) {
      index++;
      // if after "," is a known variable OR symbol OR index == length
      if (index < tokens.length && !map.containsKey(tokens[index]) && Character.isLetter(tokens[index].charAt(0))) {
        variable = tokens[index];
      } else {

        return false;
      }

      if (!Character.isLetter(variable.charAt(0))) {
        return false;
      }
      index++;

      variables.put(variable, dataType);

    }
    return true;
  }

  public Boolean parseStatements() {
    while ( index<tokens.length && (tokens[index].equals("if") || tokens[index].equals("print") ||
        tokens[index].equals("loopif") ||
        (Character.isLetter(tokens[index].charAt(0)) && variables.containsKey(tokens[index])))) {
      count++;
   
      
      if (tokens[index].equals("if")) {
        if(!parseConditional()){
          return false;
        }
        index++;



      } else if (tokens[index].equals("print")) {
       
        if(!parsePrint()){
          return false;
        }
        if (!tokens[index].equals(";")) {

          return false;
        }
        index++;

      } else if (tokens[index].equals("loopif")) {
        if(!parseLoop()){
          return false;
        }
        index++;
        if (!tokens[index].equals(";")) {

          return false;
        }
     

      } else if (Character.isLetter(tokens[index].charAt(0)) && variables.containsKey(tokens[index])) {
        if (!parseAssignment()) {

          return false;
        }

        if (!tokens[index].equals(";")) {

          return false;
        }
        index++;
   
        
      }
    }

    return true;
  }

  public Boolean parseConditional() { // TODO
    index++;
    if(!parseExpression()){
      return false;
    }
    if(!tokens[index].equals("then")){
      return false;
    }
    index++;
    if(!parseStatements()){
      return false;
    }
    if(tokens[index].equals("else")){
      index++;
      if(!parseStatements()){
        return false;
      }
    }
    if(!tokens[index].equals("endif")){
      return false;
    }
    index++;

    return true;
  }

  public Boolean parsePrint() { // TODO
  
    if (tokens[index].equals("print")) {
      index++;
      
    }
    if(!parseExpression()){
      return false;
    }
    

    return true;
  }

  public Boolean parseAssignment() {
    String token;
    token = tokens[index];
    index++;

    if (!variables.containsKey(token)) { // If variable being assigned exists
      return false;
    }

    if (!tokens[index].equals("<-")) { // Check if <- exists
      return false;
    }
    index++; // 555

    if (!parseExpression()) {

      return false;
    }

    return true;
  }

  private boolean parseExpression() {
    if (!parseTerm()) {
      return false;
    }
    index++;

    if (parseBinaryOP()) {
      index++;

      if (!parseExpression()) {

        return false;
      }
    }
  
    return true;
  }

  private boolean parseBinaryOP() {
    if (parseArithmeticOp()) {

      return true;
    } else if (parseLogicalOp()) {
      return true;
    } else if (parseRelationalOp()) {
      return true;
    }

    return false;
  }

  private boolean parseRelationalOp() {
    if (tokens[index].equals(".eq.") || tokens[index].equals(".ne.") || tokens[index].equals(".lt.")
        || tokens[index].equals(".gt.") ||
        tokens[index].equals(".le.") || tokens[index].equals(".ge.")) {
      return true;
    }
    return false;
  }

  private boolean parseLogicalOp() {
    if (tokens[index].equals(".and.") || tokens[index].equals(".or.")) {
      return true;
    }
    return false;
  }

  private boolean parseArithmeticOp() {

    if (tokens[index].equals(".plus.") || tokens[index].equals(".minus.") ||
        tokens[index].equals(".mul.") || tokens[index].equals(".div.")) {

      return true;
    }
    return false;
  }

  private boolean parseTerm() {
    if (isIntegerConstant()) {

      return true;
    } else if (isCharacterConstant()) {

      return true;
    } else if (tokens[index].equals("(")) {
      index++;
      if (!parseExpression()) {
        return false;
      }
      if (tokens[index].equals(")")) {
        return true;
      } else {
        return false;
      }
    } else if (variables.containsKey(tokens[index])) {

  
      return true;
    }

    else if (isUnaryOp()) {

      index++;

      if (!parseTerm()) {
        return false;
      } else {
        return true;
      }
    }

    return false;
  }

  private Boolean isIntegerConstant() {

    if (!Character.isDigit(tokens[index].charAt(0))) {
      return false;
    }
    
    return true;
  }

  private Boolean isCharacterConstant() {

    if (!tokens[index].equals("\"")) {

      return false;
    }
    index++;

    if (!Character.isAlphabetic(tokens[index].charAt(0)) || tokens[index].length() != 1) {

      return false;
    }
    index++;
    if (!tokens[index].equals("\"")) {

      return false;
    }

    return true;
  }

  private Boolean isUnaryOp() {
 
    if (!tokens[index].equals(".minus.") && !tokens[index].equals(".not.")) {
      return false;
    }

    return true;
  }

  public Boolean parseLoop() { // TODO
    index++;
    if(!parseExpression()){
      return false;
    }
    
    if(!tokens[index].equals("do")){
      return false;
    }
  
    index++;
    
    if(!parseStatements()){
     
      return false;
    }
    
    
    if(!tokens[index].equals("endloop")){
    
      return false;
    }

    return true;
  }
}
