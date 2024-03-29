package com.craftinginterpreters.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class GenerateAST {
    public static void main (String[] args) throws IOException {
	if (args.length != 1) {
	    System.err.println ("Usage: generate_ast <output_dir>");
	    System.exit (1);
	}
	
	String outputDir = args [0];

	defineAST (outputDir, "Expr", Arrays.asList (
						     "Binary : Expr left, Token operator, Expr right",
						     "Grouping : Expr expression",
						     "Literal : Object value",
						     "Unary : Token operator, Expr right"
						     ));
    }

    private static void defineAST (String outputDir, String baseName, List <String> types) throws IOException {
	String path = outputDir + "/" + baseName + ".java";
	PrintWriter writer = new PrintWriter (path, "UTF-8");

	writer.println ("package com.craftinginterpreters.lox;");
	writer.println ();
	writer.println ("import java.util.List;");
	writer.println ();
	writer.println ("abstract class " + baseName + " {");

	defineVisitor (writer, baseName, types);

	for (String type : types) {
	    String[] typeSplit = type.split (":");
	    String className = typeSplit[0].trim ();
	    String fields = typeSplit[1].trim ();
	    defineType (writer, baseName, className, fields);
	}

	writer.println ();
	writer.println ("    abstract <R> R accept (Visitor <R> visitor);");

	writer.println ("}");
	writer.close ();
    }

    private static void defineType (PrintWriter writer, String baseName, String className, String fieldList) {
	writer.println ("    static class " + className + " extends " + baseName + " {");

	writer.println ();

	String [] fields = fieldList.split (", ");
	for (String field : fields) {
	    writer.println ("        final " + field + ";");
	}
	
	// Constructor
	writer.println ("        " + className + " (" + fieldList + ") {");

	// Store parameters in fields
	for (String field : fields) {
	    String fieldName = field.split (" ")[1];
	    writer.println ("            this." + fieldName + " = " + fieldName + ";");
	}

	writer.println("        }");

	writer.println ();
	writer.println ("        <R> R accept (Visitor <R> visitor) {");
	writer.println ("            return visitor.visit" + className + baseName + "(this);");
	writer.println ("        }");

        writer.println("    }");
	
    }

    private static void defineVisitor (PrintWriter writer, String baseName, List<String> types) {
	writer.println ("    interface Visitor <R> {");

	for (String type : types) {
	    String typeName = type.split (":")[0].trim ();
	    writer.println ("        R visit" + typeName + baseName + " (" + typeName + " " + baseName.toLowerCase () + ");");
	}
	writer.println ("    }");
    }
    
}
