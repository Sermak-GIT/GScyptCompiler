package com.company;

import javax.swing.*;
import java.util.*;

import static com.company.Main.MathState.*;
import static java.lang.System.err;
import static java.lang.System.out;

public class Main {



    String[] input = {
            "pi := 3",
            "10 * as i | i % 2 | pi + -1 / ( 2 * i - 1) | pi + 1 / (2 * i - 1) |",
            "csay pi"
    };

    Set<String> functs = new HashSet<>();
    ArrayList<String> vars = new ArrayList<>();

    private void init() {
        functs.add("csay");
    }

    public static void main(String[] args) {
        Main m = new Main();
        m.init();
        out.println(m.read());

        /*String[] s = {""};
        Stack<String> nwrd = new Stack<>();
        for (int i = 0; i < s.length; i++) {
            nwrd.add(s[s.length - i - 1]);
        }
        out.println(m.mathPars(nwrd, S, 0));
        out.println(m.mathPars(nwrd, S, 0) == null);
        //out.println(m.mathPars(nwrd, S, 0).contains("null"));*/
    }

    private String read() {
        String parsed = "";
        for (int i = 0; i < input.length; i++) {
            parsed += readL(input[i], i);
        }
        return parsed;
    }

    private String readL(String line, int iline) {
        if (Objects.equals(line.trim(), "")) {
            return "";
        }
        String[] wrd = line.split(" ");
        switch (wrd[0]) {
            case "csay": {
                return csay(wrd);
            } case "|": {
                return ifBranch(wrd, iline);
            } default: {
                switch (wrd[1]) {
                    case ":=": {
                        if (functs.contains(wrd[2])) {
                            return fdecl(wrd, iline);
                        } else {
                            return vdecl(wrd);
                        }
                    } case "*": {
                        if (Objects.equals(wrd[2], "as"))
                        return forLoop(wrd, iline);
                    } default: {
                        String s = mathPars(mathTokenize(line), S, 0);
                        if (s != null) {
                            return wrd[0] + " = " + line;
                        }
                    }
                }
                err.println("Error @" + (iline + 1) + "  " + line);
                //return line;
                return null;
            }
        }
    }

    private String ifBranch(String[] wrd, int iline) {
        String con = "";
        String f = "";
        String e = "";

        int j = 0;
        for (int i = 0; i < wrd.length; i++) {
            if (Objects.equals(wrd[i], "|")) {
                j++;
            } else if (j == 1) {
                con += wrd[i] + " ";
            } else if (j == 2) {
                f += wrd[i] + " ";
            } else if (j == 3) {
                e += wrd[i] + " ";
            }
        }

        String code = "if (" + con.trim() + ") {\n";
        code += readL(f, iline);
        code += "} else {\n";
        code += readL(f, iline);
        code += "}\n";
        return code;
    }

    private String forLoop(String[] wrd, int iline) {
        String code = "for (int " + wrd[3] + " = 0; " + wrd[3] + " < " + wrd[0] + "; " + wrd[3] + "++) {\n";
        String line = "";
        for (int i = 4; i < wrd.length; i++) {
            line += wrd[i] + " ";
        }
        line = line.trim();
        String[] s = line.split(", ");
        for (String ss:s) {
            code += readL(ss.trim(), iline);
        }
        code += "}\n";
        return code;
    }

    private String csay(String[] wrd) {
        //String digits = "0123456789+-*/() ";
        String code = "";
        code += "System.out.println(";
        Stack<String> nwrd = new Stack<>();
        for (int i = 1; i < wrd.length; i++) {
            nwrd.add(wrd[wrd.length - i - 1]);
        }
        String mathParsed = mathPars(nwrd, S, 0);
        /*
        for (int i = 1; i < wrd.length; i++) {
            if (!digits.contains(wrd[i]) && !vars.contains(wrd[i]) && !functs.contains(wrd[i])) {
                for (char c : wrd[i].toCharArray()) {
                    if (!digits.contains(c + "")) {
                        compable = false;
                        break;
                    }
                }
            }
        }
        */
        if (mathParsed != null) {
            code += mathParsed;
            code = code.trim();
        } else {
            code += "\"";
            for (int i = 1; i < wrd.length; i++) {
                code += wrd[i];
                code += " ";
            }
            code = code.trim();
            code += "\"";
        }
        code += ");\n";
        return  code;
    }

    private String vdecl(String[] wrd) {
        String code = "";
        vars.add(wrd[0]);

        Stack<String> nwrd = new Stack<>();
        for (int i = 1; i < wrd.length; i++) {
            nwrd.add(wrd[wrd.length - i - 1]);
        }
        String mathParsed = mathPars(nwrd, S, 0);
        /*
        for (int i = 1; i < wrd.length; i++) {
            if (!digits.contains(wrd[i]) && !vars.contains(wrd[i]) && !functs.contains(wrd[i])) {
                for (char c : wrd[i].toCharArray()) {
                    if (!digits.contains(c + "")) {
                        compable = false;
                        break;
                    }
                }
            }
        }
        */

        if (mathParsed != null) {
            code += "double " + wrd[0] + " = ";
            code += mathParsed;
            code = code.trim();
            code += ";\n";
        } else {
            code += "String " + wrd[0] + " = ";
            code += "\"";
            for (int i = 2; i < wrd.length; i++) {
                code += wrd[i];
                code += " ";
            }
            code = code.trim();
            code += "\";\n";
        }
        return code;
    }

    private String fdecl(String[] wrd, int iline) {
        String code = "public void " + wrd[0] + "() {\n";
        String line = "";
        for (int i = 2; i < wrd.length; i++) {
            line += wrd[i] + " ";
        }
        line = line.trim();
        String[] s = line.split(", ");
        for (String ss:s) {
            code += readL(ss.trim(), iline);
        }
        code += "}\n";
        return code;
    }

    enum MathState {VAR, OPERATOR, NUM, PARL, PARR, S}

    private String mathPars(Stack<String> wrd, MathState lastState, int pares) {
        if (wrd.isEmpty()) {
            if (pares == 0) {
                return "";
            }
            return null;
        } else {
            MathState currState = getState(wrd.peek());
            if (currState == null) return null;
            //out.println(wrd + "|" + lastState + "|" + currState);
            switch (lastState) {
                case S: {
                    switch (currState) {
                        case NUM: {
                            return wrd.pop() + " " + mathPars(wrd, currState, pares);
                        }
                        case VAR: {
                            return wrd.pop() + " " + mathPars(wrd, currState, pares);
                        }
                        case PARL: {
                            return wrd.pop() + " " + mathPars(wrd, currState, pares + 1);
                        }
                        case PARR: {
                            return wrd.pop() + " " + mathPars(wrd, currState, pares - 1);
                        }
                        case OPERATOR: {
                            if ("+".contains(wrd.peek())) {
                                wrd.pop();
                                return "1 " + mathPars(wrd, NUM, pares);
                            } else if ("-".contains(wrd.peek())) {
                                wrd.pop();
                                return "-1 " + mathPars(wrd, NUM, pares);
                            } else {
                                wrd.pop();
                                return null;
                            }
                        }
                    }
                }
                case NUM: {
                    switch (currState) {
                        case NUM: {
                            return " * " + wrd.pop() + " " + mathPars(wrd, currState, pares);
                        }
                        case VAR: {
                            return " * " + wrd.pop() + " " + mathPars(wrd, currState, pares);
                        }
                        case PARL: {
                            return " * " + wrd.pop() + " " + mathPars(wrd, currState, pares + 1);
                        }
                        case PARR: {
                            return " * " + wrd.pop() + " " + mathPars(wrd, currState, pares - 1);
                        }
                        case OPERATOR: {
                            return wrd.pop() + " " + mathPars(wrd, currState, pares);
                        }
                    }
                }
                case VAR: {
                    switch (currState) {
                        case NUM: {
                            return " * " + wrd.pop() + " " + mathPars(wrd, currState, pares);
                        }
                        case VAR: {
                            return " * " + wrd.pop() + " " + mathPars(wrd, currState, pares);
                        }
                        case PARL: {
                            return " * " + wrd.pop() + " " + mathPars(wrd, currState, pares + 1);
                        }
                        case PARR: {
                            return " * " + wrd.pop() + " " + mathPars(wrd, currState, pares - 1);
                        }
                        case OPERATOR: {
                            return wrd.pop() + " " + mathPars(wrd, currState, pares);
                        }
                    }
                }
                case OPERATOR: {
                    switch (currState) {
                        case NUM: {
                            return wrd.pop() + " " + mathPars(wrd, currState, pares);
                        }
                        case VAR: {
                            return wrd.pop() + " " + mathPars(wrd, currState, pares);
                        }
                        case PARL: {
                            return wrd.pop() + " " + mathPars(wrd, currState, pares + 1);
                        }
                        case PARR: {
                            return wrd.pop() + " " + mathPars(wrd, currState, pares - 1);
                        }
                        case OPERATOR: {
                            if ("+".contains(wrd.peek())) {
                                wrd.pop();
                                return "1 " + mathPars(wrd, NUM, pares);
                            } else if ("-".contains(wrd.peek())) {
                                wrd.pop();
                                return "-1 " + mathPars(wrd, NUM, pares);
                            } else {
                                wrd.pop();
                                return null;
                            }
                        }
                    }
                }
                case PARL: {
                    switch (currState) {
                        case NUM: {
                            return wrd.pop() + " " + mathPars(wrd, currState, pares + 1);
                        } case VAR: {
                            return wrd.pop() + " " + mathPars(wrd, currState, pares + 1);
                        }
                        case PARL: {
                            return wrd.pop() + " " + mathPars(wrd, currState, pares + 1);
                        }
                        case OPERATOR: {
                            if ("+".contains(wrd.peek()))
                                return "1 " + mathPars(wrd, currState, pares);
                            else if ("-".contains(wrd.peek()))
                                return "-1 " + mathPars(wrd, currState, pares);
                            else return null;
                        }
                    }
                } case PARR: {
                    switch (currState) {
                        case NUM: {
                            return " * " + wrd.pop() + " " + mathPars(wrd, currState, pares - 1);
                        }
                        case VAR: {
                            return " * " + wrd.pop() + " " + mathPars(wrd, currState, pares - 1);
                        }
                        case PARL: {
                            return " * " + wrd.pop() + " " + mathPars(wrd, currState, pares + 1);
                        }
                        case OPERATOR: {
                            return wrd.pop() + " " + mathPars(wrd, currState, pares);
                        }
                    }
                }
            }
            return null;
        }
    }

    private MathState getState(String s) {
        s = s.trim();
        if (vars.contains(s) || functs.contains(s)) {
            return VAR;
        } else if ("+-*/".contains(s)) {
            return OPERATOR;
        } else if ("(".contains(s)) {
            return PARL;
        } else if (")".contains(s)) {
            return PARR;
        } else {
            String digits = "0123456789";
            boolean state0 = true;
            for (char c : s.toCharArray()) {
                if (state0) {
                    if (!digits.contains(c  + "") && !"-".contains(c + "")) return null;
                    state0 = false;
                } else {
                    if (!digits.contains(c  + "")) return null;
                }
            }
        }
        return NUM;
    }

    private Stack<String> mathTokenize(String s) {
        char[] c = s.toCharArray();
        String token = "";
        Stack<String> tl = new Stack<>();
        for (int i = 0; i < c.length; i++) {
            if ("()+*/".contains(c[i] + "")) {
                tl.add(token);
                token = "";
                token += c[i];
                tl.add(token);
                token = "";
            } else if (" ".contains(c[i] + "")){
                tl.add(token);
                token = "";
            } else if ("-".contains(c[i] + "")) {
                char c1 = ' ';
                int j = i - 1;
                while (c1 == ' ') {
                    if (j < 0) {
                        c1 = ' ';
                        break;
                    }
                    c1 = c[j];
                    j--;
                }
                if (!"+-*/".contains(c1 + "")) {
                    tl.add(token);
                    token = "";
                    token += c[i];
                    tl.add(token);
                    token = "";
                } else {
                    token += c[i];
                }
            } else {
                token += c[i];
            }
        }
        tl.add(token);
        return tl;
    }
}
