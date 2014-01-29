package org.graphwalker.cli;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
  /*
   * Given a String, and an index, get the atom starting at that index
   */
  public static String getAtom(String s, int i) {
    int j = i;
    for (; j < s.length(); ) {
      if (Character.isJavaIdentifierPart(s.charAt(j))) {
        j++;
      } else {
        return s.substring(i, j);
      }
    }
    return s.substring(i, j);
  }

  private static String getString(String s, int i) {
    int j = i;
    for (; j < s.length(); ) {
      if (s.charAt(j) != '"') {
        j++;
      } else {
        return s.substring(i, j);
      }
    }
    return s.substring(i, j);
  }

  public static List<Token> lex(String input) {
    List<Token> result = new ArrayList<Token>();
    for (int i = 0; i < input.length(); ) {
      switch (input.charAt(i)) {
        case '(':
          result.add(new Token(Type.LPAREN, "("));
          i++;
          break;
        case ')':
          result.add(new Token(Type.RPAREN, ")"));
          i++;
          break;
        case ',':
          result.add(new Token(Type.COMMA, ")"));
          i++;
          break;
        default:
          if (Character.isWhitespace(input.charAt(i))) {
            i++;
          } else  if (input.charAt(i) == '"') {
            String string = getString(input, ++i);
            result.add(new Token(Type.STRING, string));
            i += string.length() + 1;
          } else {
            String atom = getAtom(input, i);
            if (atom.equalsIgnoreCase("OR")) {
              result.add(new Token(Type.OR, atom));
            } else if (atom.equalsIgnoreCase("AND")) {
              result.add(new Token(Type.AND, atom));
            } else {
              result.add(new Token(Type.ATOM, atom));
            }
            i += atom.length();
          }
          break;
      }
    }
    return result;
  }

  public static enum Type {
    LPAREN, RPAREN, OR, AND, ATOM, COMMA, STRING;
  }

  public static class Token {
    public final Type t;
    public final String c; // contents mainly for atom tokens

    // could have column and line number fields too, for reporting errors later
    public Token(Type t, String c) {
      this.t = t;
      this.c = c;
    }

    public String toString() {
      if (t == Type.ATOM) {
        return "ATOM<" + c + ">";
      } else if (t == Type.STRING) {
        return "STRING<" + c + ">";
      }
      return t.toString();
    }
  }
}