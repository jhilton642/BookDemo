package com.example.demo.controllers;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Arrays;

public class Godzilla {

    public static void main(String[] args) {
        String reverseUrl = "com.example.demo";
        String entity = "cow";
        buildMVC(reverseUrl, entity, true, true, true);
    }

    public static void buildMVC(String reverseUrl, String entity, boolean findBy, boolean createHTML, boolean createJava) {
        Class c;
        Field[] fields;

        try {
            c = Class.forName(reverseUrl + ".models." + proper(entity));
            fields = c.getDeclaredFields();
            if (createHTML) {
                buildListingPage(createFile("src\\main\\resources\\templates\\" + entity + "List.html"), entity, fields, "", findBy);
                buildEditPage(createFile("src\\main\\resources\\templates\\" + entity + "Edit.html"), entity, fields, " Edit");
                buildDetailsPage(createFile("src\\main\\resources\\templates\\" + entity + "Details.html"), entity, fields, " Details");
            }
            if (createJava) {
                buildService(createFile("src\\main\\java\\com\\example\\demo\\services\\" + proper(entity) + "Service.java"), entity, fields, reverseUrl, findBy);
                buildRepository(createFile("src\\main\\java\\com\\example\\demo\\repository\\" + proper(entity) + "Repository.java"), entity, fields, reverseUrl, findBy);
                buildController(createFile("src\\main\\java\\com\\example\\demo\\controllers\\" + proper(entity) + "Controller.java"), entity, fields, reverseUrl);
            }
        } catch (Throwable e) {
            System.err.println(e);
        }
    }

    private static void buildEditPage(PrintWriter out, String entity, Field[] fields, String action) {

        boolean hasImage = Arrays.stream(fields).anyMatch(f -> f.getName().equals("imageUrl"));
        String type;

        printHeader(out, entity, action);
        out.print("    <h2>" + proper(entity) + " Details</h2>\n");
        out.print("    <div>\n");
        out.print("        <form class=\"form-horizontal\" th:object=\"${" + entity + "}\" th:action=\"@{/" + entity + "s/save}\" method=\"post\"" + (hasImage ? "enctype=\"multipart/form-data\">\n" : ">\n"));

        for (Field f : fields) {
            if (f.getName().equals("id") || f.getName().equals("version")) {
                out.print("            <input type=\"hidden\" th:field=\"*{" + f.getName() + "}\"/>\n");
            } else {
                out.print("            <div class=\"form-group\">\n");
                out.print("                <label class=\"col-sm-2 control-label\">" + proper(f.getName()) + ":</label>\n");
                out.print("                <div class=\"col-sm-10\">\n");
                if (f.getName().equals("imageUrl")) {
                    out.print("                    <input type=\"hidden\" th:field=\"*{imageUrl}\"/>\n");
                    out.print("                    <input type=\"file\" class=\"form-control\" name=\"file\" /> <br/>\n");
                } else {
                    type = getType(f.getType());
                    out.print("                    <input type=\"" + type + "\" class=\"form-control\" th:field=\"*{" + f.getName() + "}\"/>\n");
                }
                out.print("                </div>\n");
                out.print("            </div>\n");
            }
        }
        out.print("            <div class=\"row\">\n");
        out.print("                <button type=\"submit\" class=\"btn btn-default\">Submit</button>\n");
        out.print("            </div>\n");
        out.print("        </form>\n");
        out.print("        <a href='/" + entity + "s/'>Cancel Edit</a>\n");
        out.print("    </div>\n");
        out.print("</div>\n");
        out.print("\n");
        out.print("</body>\n");
        out.print("</html>\n");
        out.close();
    }

    private static String getType(Class<?> type) {
        switch (type.getTypeName()) {
            case "string"   :   return "text";
            case "int"      :   return "number";
            case "boolean"  :   return "checkbox";
            default         :   return "text";
        }
    }


    private static void buildListingPage(PrintWriter out, String entity, Field[] fields, String action, boolean findBy) {

        printHeader(out, entity, action);

        out.print("<a href='/" + entity + "s/new'>Create " + proper(entity) + "</a>\n");
        if (findBy) {
            out.print("    <form class=\"form-horizontal\" th:action=\"@{/" + entity + "s/search}\" method=\"post\">\n");
            out.print("        <div class=\"form-group\">\n");
            for (Field f : fields) {
                if (f.getName().equals("id") || f.getName().equals("version") || f.getName().equals("imageUrl"))
                    continue;
                out.print("        <label class=\"col-sm-1 control-label\">" + proper(f.getName()) + ":</label>\n");
                out.print("        <div class=\"col-sm-2\">\n");
                out.print("            <input type=\"text\" class=\"form-control\" name=\"" + f.getName() + "\" th:value=\"${" + f.getName() + "}\"/>\n");
                out.print("        </div>\n");
            }
            out.print("        <div class=\"col-sm-2\">\n");
            out.print("            <button type=\"submit\" class=\"btn btn-default\">Submit</button>\n");
            out.print("        </div>\n");
            out.print("    </div>\n");
            out.print("    </form>\n");
        }
        out.print("    <hr>\n");
        out.print("    <div th:if=\"${not #lists.isEmpty(" + entity + "s)}\">\n");
        out.print("        <h3>" + proper(entity) + " List</h3>\n");
        out.print("        <table class=\"table table-striped\">\n");
        out.print("            <tr>\n");
        for (Field f : fields) {
            if (f.getName().equals("version")) continue;
            out.print("                <th>" + proper(f.getName()) + "</th>\n");
        }
        out.print("                <th>View</th>\n");
        out.print("                <th>Edit</th>\n");
        out.print("                <th>Delete</th>\n");
        out.print("            </tr>\n");
        out.print("            <tr th:each=\"" + entity + " : ${" + entity + "s}\">\n");
        for (Field f : fields) {
            if (f.getName().equals("version")) continue;
            if (f.getName().equals("imageUrl"))
                out.print("                <td><img th:src=\"${" + entity + ".imageUrl}\" width=\"35\" height=\"40\"/></td>\n");
            else
                out.print("                <td th:text=\"${" + entity + "." + f.getName() + "}\">" + proper(f.getName()) + "</td>\n");
        }
        out.print("\n");
        out.print("                <td><a th:href=\"${ '/" + entity + "s/'        + " + entity + ".id}\">View</a></td>\n");
        out.print("                <td><a th:href=\"${ '/" + entity + "s/edit/'   + " + entity + ".id}\">Edit</a></td>\n");
        out.print("                <td><a th:href=\"${ '/" + entity + "s/delete/' + " + entity + ".id}\">Delete</a></td>\n");
        out.print("            </tr>\n");
        out.print("        </table>\n");
        out.print("    </div>\n");
        out.print("</div>\n");
        out.print("</body>\n");
        out.print("</html>\n");
        out.close();
    }

    public static void printHeader(PrintWriter out, String entity, String action) {
        out.print("<!DOCTYPE html>\n");
        out.print("<html xmlns:th=\"http://www.thymeleaf.org\">\n");
        out.print("<head lang=\"en\">\n");
        out.print("    <title>" + proper(entity) + action + "</title>\n");
        out.print("    <!--/*/ <th:block th:include=\"fragments/headerinc :: head\"></th:block> /*/-->\n");
        out.print("</head>\n");
        out.print("<body>\n");
        out.print("<div class=\"container\">\n");
        out.print("    <!--/*/ <th:block th:include=\"fragments/header :: header\"></th:block> /*/-->\n");
    }

    public static void buildDetailsPage(PrintWriter out, String entity, Field[] fields, String action) {

        printHeader(out, entity, action);

        out.print("    <h2>" + proper(entity) + action + "</h2>\n");
        out.print("    <div>\n");
        out.print("        <form class=\"form-horizontal\">\n");

        for (Field f : fields) {
            if (f.getName().equals("id") || f.getName().equals("version")) continue;
            out.print("            <div class=\"form-group\">\n");
            out.print("                <label class=\"col-sm-2 control-label\">" + proper(f.getName()) + ":</label>\n");
            out.print("                <div class=\"col-sm-10\">\n");
            if (f.getName().equals("imageUrl"))
                out.print("                    <p><img th:src=\"${" + entity + ".imageUrl}\" width=\"75\" height=\"80\"/></p>\n");
            else
                out.print("                    <p class=\"form-control-static\" th:text=\"${" + entity + "." + f.getName() + "}\">" + proper(f.getName()) + "</p>\n");
            out.print("                </div>\n");
            out.print("            </div>\n");
        }
        out.print("        </form>\n");
        out.print("        <a href='/" + entity + "s/'>Back to " + proper(entity) + " List</a>\n");
        out.print("    </div>\n");
        out.print("</div>\n");
        out.print("\n");
        out.print("</body>\n");
        out.print("</html>\n");
        out.close();
    }

    public static void buildService(PrintWriter out, String entity, Field[] fields, String reverseURL, boolean findBy) {
        out.print("package " + reverseURL + ".services;\n");
        out.print("\n");
        out.print("import " + reverseURL + ".models." + proper(entity) + ";\n");
        out.print("import " + reverseURL + ".repository." + proper(entity) + "Repository;\n");
        out.print("import org.springframework.beans.factory.annotation.Autowired;\n");
        out.print("import org.springframework.stereotype.Service;\n");
        out.print("\n");
        out.print("import java.util.Optional;\n");
        out.print("\n");
        out.print("@Service\n");
        out.print("public class " + proper(entity) + "Service {\n");
        out.print("    @Autowired\n");
        out.print("    private " + proper(entity) + "Repository " + entity + "Repository;\n");
        out.print("\n");
        out.print("    public Iterable<" + proper(entity) + "> listAll" + proper(entity) + "s() {\n");
        out.print("        return " + entity + "Repository.findAll();\n");
        out.print("    }\n");
        out.print("\n");
        out.print("    public " + proper(entity) + " get" + proper(entity) + "ById(Integer id) {\n");
        out.print("        Optional<" + proper(entity) + "> o" + proper(entity) + " = " + entity + "Repository.findById(id);\n");
        out.print("        return o" + proper(entity) + ".orElse(null);\n");
        out.print("    }\n");
        out.print("\n");
        out.print("    public " + proper(entity) + " save" + proper(entity) + "(" + proper(entity) + " " + entity + ") {\n");
        out.print("        return " + entity + "Repository.save(" + entity + ");\n");
        out.print("    }\n");
        out.print("\n");
        out.print("    public void delete" + proper(entity) + "(Integer id) {\n");
        out.print("        Optional<" + proper(entity) + "> o" + proper(entity) + " = " + entity + "Repository.findById(id);\n");
        out.print("        o" + proper(entity) + ".ifPresent(" + entity + " -> " + entity + "Repository.delete(" + entity + "));\n");
        out.print("    }\n");
        out.print("\n");
        out.print("    public " + proper(entity) + " add(" + proper(entity) + " " + entity + ") {\n");
        out.print("        return " + entity + "Repository.save(" + entity + ");\n");
        out.print("    }\n");
        out.print("\n");
        if (findBy) {
            for (Field f : fields) {
                if (f.getName().equals("id") || f.getName().equals("version")) continue;
                String type = f.getType().getSimpleName();
                out.print("    public Iterable<" + proper(entity) + "> findBy" + proper(f.getName()) + "(" + type + " " + f.getName() + ") {\n");
                out.print("        return " + entity + "Repository.findBy" + proper(f.getName()) + "(" + f.getName() + ");\n");
                out.print("    }\n");
            }
        }
        out.print("}\n");
        out.close();
    }

    public static void buildRepository(PrintWriter out, String entity, Field[] fields, String reverseURL, boolean findBy) {
        out.print("package " + reverseURL + ".repository;\n");
        out.print("\n");
        out.print("import " + reverseURL + ".models." + proper(entity) + ";\n");
        out.print("import org.springframework.data.repository.CrudRepository;\n");
        out.print("import org.springframework.stereotype.Repository;\n");
        if (findBy) {
            out.print("\nimport java.util.List;\n");
        }
        out.print("\n");
        out.print("@Repository\n");
        out.print("public interface " + proper(entity) + "Repository extends CrudRepository<" + proper(entity) + ", Integer> {\n");
        if (findBy) {
            for (Field f : fields) {
                if (f.getName().equals("id") || f.getName().equals("version")) continue;
                String type = f.getType().getSimpleName();
                out.print("    List<" + proper(entity) + "> findBy" + proper(f.getName()) + "(" + type + " " + f.getName() + ");\n");
            }
        }
        out.print("}\n");
        out.close();
    }

    public static void buildController(PrintWriter out, String entity, Field[] fields, String reverseURL) {

        boolean hasImage = Arrays.stream(fields).anyMatch(f -> f.getName().equals("imageUrl"));

        out.print("package " + reverseURL + ".controllers;\n");
        out.print("\n");
        out.print("import " + reverseURL + ".models." + proper(entity) + ";\n");
        out.print("import " + reverseURL + ".services." + proper(entity) + "Service;\n");
        out.print("import org.springframework.beans.factory.annotation.Autowired;\n");
        if (hasImage) out.print("import org.springframework.core.env.Environment;\n");
        out.print("import org.springframework.stereotype.Controller;\n");
        out.print("import org.springframework.ui.Model;\n");
        out.print("import org.springframework.web.bind.annotation.*;\n");
        if (hasImage) {
            out.print("import org.springframework.web.multipart.MultipartFile;\n");
            out.print("\n");
            out.print("import java.io.IOException;\n");
            out.print("import java.nio.file.Files;\n");
            out.print("import java.nio.file.Path;\n");
            out.print("import java.nio.file.Paths;\n");
        }
        out.print("\n");
        out.print("@Controller\n");
        out.print("@RequestMapping(\"/" + entity + "s\")            //  this will add " + entity + "s to the start of all URL endpoints\n");
        out.print("public class " + proper(entity) + "Controller {\n");
        out.print("\n");
        out.print("    @Autowired\n");
        out.print("    //    @Autowired will request SpringBoot to find the " + proper(entity) + "Service class and instantiate one for us\n");
        out.print("    //    and assign (INJECT) the class property with the value. This is Dependency Injection.\n");
        out.print("    //    our class depends on this service and SpringBoot will inject it into our class\n");
        out.print("    private " + proper(entity) + "Service " + entity + "Service;\n");
        out.print("\n");
        if (hasImage) {
            out.print("    /**\n");
            out.print("     *         Environment     Provides access the the application.properties file. The getProperty method will retrieve\n");
            out.print("     *                         the value of a property which we can use in the code. For instance in this Controller\n");
            out.print("     *                         we are interested in the destination folder for the images we will be uploading. We save\n");
            out.print("     *                         the folder in the properties files. This property is consistent and available to any\n");
            out.print("     *                         code that wishes to use these same folders\n");
            out.print("     */\n");
            out.print("    @Autowired\n");
            out.print("    private Environment environment;\n");
            out.print("\n");
        }
        out.print("    @RequestMapping(\"/\")                                    //  this code will be reached by /" + entity + "s/\n");
        out.print("    public String index(Model model) {\n");
        out.print("        //  get a list of all " + entity + "s add to the model and list them\n");
        out.print("        Iterable<" + proper(entity) + "> " + entity + "s = " + entity + "Service.listAll" + proper(entity) + "s();\n");
        out.print("        model.addAttribute(\"" + entity + "s\", " + entity + "s);\n");
        out.print("\n");
        out.print("        //  the the " + entity + "List page will be happy to display it\n");
        out.print("        return \"" + entity + "List\";\n");
        out.print("    }\n");
        out.print("\n");
        out.print("    //  let's CREATE a new " + entity + "\n");
        out.print("    @RequestMapping(\"/new\")\n");
        out.print("    public String new" + proper(entity) + "(Model model){\n");
        out.print("        //  since we do not have a " + entity + ", let's send an empty " + entity + " to the " + entity + "Edit page\n");
        out.print("        model.addAttribute(\"" + entity + "\", new " + proper(entity) + "());\n");
        out.print("        return \"" + entity + "Edit\";\n");
        out.print("    }\n");
        out.print("\n");
        out.print("    //  id will be the key to the " + entity + " we want to READ from the database\n");
        out.print("    @RequestMapping(\"/{id}\")\n");
        out.print("    public String read" + proper(entity) + "(@PathVariable Integer id, Model model){\n");
        out.print("        //  find in the database a " + entity + " with id = to our PathVariable\n");
        out.print("        " + proper(entity) + " " + entity + " = " + entity + "Service.get" + proper(entity) + "ById(id);\n");
        out.print("\n");
        out.print("        //  did we find a " + entity + "?\n");
        out.print("        if ( " + entity + " != null ) {\n");
        out.print("            //  yes. add the " + entity + " to the model and display the " + entity + "Details page\n");
        out.print("            model.addAttribute(\"" + entity + "\", " + entity + ");\n");
        out.print("            return \"" + entity + "Details\";\n");
        out.print("        }\n");
        out.print("        else {\n");
        out.print("            //  no, we did not find a " + entity + ". Display an error message\n");
        out.print("            model.addAttribute(\"message\", \"The " + proper(entity) + " Id: \" + id + \" was not found in the database\");\n");
        out.print("            return \"404\";       //  " + entity + " (page) not found\n");
        out.print("        }\n");
        out.print("    }\n");
        out.print("\n");
        out.print("    //  id will be the key to the " + entity + " we want to UPDATE\n");
        out.print("    @RequestMapping(\"/edit/{id}\")\n");
        out.print("    public String update" + proper(entity) + "(@PathVariable Integer id, Model model){\n");
        out.print("        //  find the " + entity + " in the database and send that data to the " + entity + "Edit page\n");
        out.print("        model.addAttribute(\"" + entity + "\", " + entity + "Service.get" + proper(entity) + "ById(id));\n");
        out.print("        return \"" + entity + "Edit\";\n");
        out.print("    }\n");
        out.print("\n");
        out.print("    //  we have finished making our changes to our " + entity + ". The data is POSTed back to the server\n");
        out.print("    //  all of the data is saved in a " + proper(entity) + " object and UPDATEd in the database.\n");
        out.print("    @RequestMapping(value = \"/save\", method = RequestMethod.POST)\n");
        out.print("    public String save" + proper(entity) + "(" + proper(entity) + " " + entity + (hasImage ? ", MultipartFile file" : "") + "){\n");
        if (hasImage) {
            out.print("        //    find where we need to save the file on the server. application.properties has an property call eta.uploadFolder\n");
            out.print("        //    it is defined with the folder destination for our upload files \n");
            out.print("        String uploadFolder = environment.getProperty(\"eta.uploadFolder\");\n");
            out.print("        \n");
            out.print("        //    load the file and give us back the location of the image to include that in our " + proper(entity) + " record in the database\n");
            out.print("        if (! file.isEmpty()) {\n");
            out.print("            String fileName = uploadFile(file, uploadFolder, \"images\");\n");
            out.print("            " + entity + ".setImageUrl(fileName);                                    //    update imageUrl property with our image\n");
            out.print("        }\n");
        }
        out.print("        //  all we have to do is save the " + entity + "\n");
        out.print("        " + entity + "Service.save" + proper(entity) + "(" + entity + ");\n");
        out.print("        //  go to the list all " + entity + "s page when complete\n");
        out.print("        return \"redirect:/" + entity + "s/\";\n");
        out.print("    }\n");
        out.print("\n");
        out.print("    //  using the id from the URL find and DELETE our " + entity + "\n");
        out.print("    @RequestMapping(\"/delete/{id}\")\n");
        out.print("    public String delete" + proper(entity) + "(@PathVariable Integer id){\n");
        out.print("        " + entity + "Service.delete" + proper(entity) + "(id);\n");
        out.print("        //  go to the list all " + entity + "s page when complete\n");
        out.print("        return \"redirect:/" + entity + "s/\";\n");
        out.print("    }\n");
        out.print("\n/*\n");
        out.print("    //  using the whatever from the search form get all " + entity + "s by this whatever\n");
        out.print("    @RequestMapping(value = \"/search\", method = RequestMethod.POST)\n");
        out.print("    public String search" + proper(entity) + "(@RequestParam String Whatever, Model model ){\n");
        out.print("        //  SEARCH for all " + entity + "s by Whatever\n");
        out.print("        Iterable<" + proper(entity) + "> list = " + entity + "Service.findByWhatever(Whatever);\n");
        out.print("\n");
        out.print("        //  pass the list of " + entity + "s by Whatever\n");
        out.print("        model.addAttribute(\"Whatever\", Whatever);\n");
        out.print("        model.addAttribute(\"" + entity + "s\", list);\n");
        out.print("\n");
        out.print("        //  the the " + entity + "List page will be happy to display it\n");
        out.print("        return \"" + entity + "List\";\n");
        out.print("    }\n*/\n");
        if (hasImage)
            uploadFile(out);
        out.print("}\n");
        out.close();
    }

    public static void uploadFile(PrintWriter out) {
        out.print("    /**\n");
        out.print("     *        uploadFile\n");
        out.print("     * @param file                the file from the browser\n");
        out.print("     * @param uploadFolder        the folder to save the file to\n");
        out.print("     * @param subfolder           the particular subfolder for the file type\n");
        out.print("     * @return                    the name of the file to be saved to the database\n");
        out.print("     */\n");
        out.print("    public static String uploadFile(MultipartFile file, String uploadFolder, String subfolder) {\n");
        out.print("        String fileName = null;\n");
        out.print("        try {\n");
        out.print("            // Get the file and save it somewhere\n");
        out.print("            byte[] bytes = file.getBytes();              //    read the entire file into this buffer\n");
        out.print("            fileName = file.getOriginalFilename();       //    get the name of the file being uploaded\n");
        out.print("\n");
        out.print("            Path path = Paths.get(\".\");                //    what is the current directory?\n");
        out.print("            //    build a path to the upload folder\n");
        out.print("            path = Paths.get(path.toAbsolutePath() + uploadFolder + subfolder + \"/\" + fileName);\n");
        out.print("            Files.write(path, bytes);                    //    save the file to the upload folder\n");
        out.print("        } catch (IOException e) {\n");
        out.print("            e.printStackTrace();                         //    just in case things go bad\n");
        out.print("        }\n");
        out.print("        return \"/\" + subfolder + \"/\" + fileName;     //    return the 'relative' location of the file\n");
        out.print("    }\n");
    }

    public static PrintWriter createFile(String fileName) {
        PrintWriter out = null;
        if (fileName.contains("java")) {
            fileName = proper(fileName);
        }
        try {
            out = new PrintWriter(new FileWriter(fileName));

        } catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }
        return out;
    }

    public static String proper(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}