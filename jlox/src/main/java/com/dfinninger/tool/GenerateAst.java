package com.dfinninger.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: generate_ast <output_directory>");
            System.exit(64);
        }
        String outputDir = args[0];

        defineAst(outputDir, "Expr", Arrays.asList(
                "Assign   : Token name, Expr value",
                "Binary   : Expr left, Token operator, Expr right",
                "Grouping : Expr expression",
                "Literal  : Object value",
                "Unary    : Token operator, Expr right",
                "Variable : Token name"
        ));

        defineAst(outputDir, "Stmt", Arrays.asList(
                "Block      : List<Stmt> statements",
                "Expression : Expr expression",
                "Print      : Expr expression",
                "Var        : Token name, Expr initializer"
        ));
    }

    private static void defineAst(String outputDir, String baseName, List<String> types) throws IOException {
        String path = String.format("%s/%s.java", outputDir, baseName);
        PrintWriter writer = new PrintWriter(path, StandardCharsets.UTF_8);

        writer.println("package com.dfinninger.lox;");
        writer.println();
        writer.println("import java.util.List;");
        writer.println();
        writer.format("abstract class %s {\n", baseName);

        defineVisitor(writer, baseName, types);

        // The AST Classes
        for (String type : types) {
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim();
            defineType(writer, baseName, className, fields);
        }

        // The base accept() method
        writer.println();
        writer.println("    abstract <R> R accept(Visitor<R> visitor);");

        writer.println("}");
        writer.close();
    }

    public static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
        writer.println("\n    interface Visitor<R> {");
        for (String type : types) {
            String typeName = type.split(":")[0].trim();
            writer.format("\n        R visit%s%s(%s %s);\n", typeName, baseName, typeName, baseName.toLowerCase());
        }
        writer.println("    }");
    }

    private static void defineType(PrintWriter writer, String baseName, String className, String fieldList) {
        // Class definition
        writer.println();
        writer.format("    static class %s extends %s {\n", className, baseName);

        // Fields
        String[] fields = fieldList.split(", ");
        for (String field : fields) {
            writer.format("        final %s;\n", field);
        }
        writer.println();

        //Constructor
        writer.format("        %s(%s) {\n", className, fieldList);
        // Store parameters in fields
        for (String field : fields) {
            String name = field.split(" ")[1];
            writer.format("            this.%s = %s;\n", name, name);
        }
        writer.println("        }");

        // Visitor Pattern
        writer.println();
        writer.println("        @Override");
        writer.println("        <R> R accept(Visitor<R> visitor) {");
        writer.format("            return visitor.visit%s%s(this);\n", className, baseName);
        writer.println("        }");
        writer.println("    }");
    }
}
