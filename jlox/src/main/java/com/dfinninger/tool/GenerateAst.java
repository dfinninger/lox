package com.dfinninger.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class GenerateAst {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: generate_ast <output_directory>");
            System.exit(64);
        }
        String outputDir = args[0];

        defineAst(outputDir, "Expr", Arrays.asList(
                "Binary   : Expr left, Token operator, Expr right",
                "Grouping : Expr expression",
                "Literal  : Object value",
                "Unary    : Token operator, Expr right"
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

        // The AST Classes
        for (String type : types) {
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim();
            defineType(writer, baseName, className, fields);
        }

        writer.println("}");
        writer.close();
    }

    private static void defineType(PrintWriter writer, String baseName, String className, String fieldList) {
        // Class definition
        writer.println();
        writer.format("    static class %s extends %s {\n", className, baseName);

        // Fields
        String[]fields = fieldList.split(", ");
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
        writer.println("    }");
    }
}
