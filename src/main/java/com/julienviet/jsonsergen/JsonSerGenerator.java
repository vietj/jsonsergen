package com.julienviet.jsonsergen;

import io.vertx.codegen.DataObjectModel;
import io.vertx.codegen.Generator;
import io.vertx.codegen.PropertyInfo;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.format.*;
import io.vertx.codegen.type.*;
import io.vertx.codegen.writer.CodeWriter;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.TypeElement;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class JsonSerGenerator extends Generator<DataObjectModel> {

  private Case formatter;
  private ProcessingEnvironment processingEnvironment;

  public JsonSerGenerator(ProcessingEnvironment processingEnvironment) {
    this.kinds = Collections.singleton("dataObject");
    this.name = "data_object_json_serializers";
    this.processingEnvironment = processingEnvironment;
  }

  @Override
  public Collection<Class<? extends Annotation>> annotations() {
    return Collections.singletonList(DataObject.class);
  }

  @Override
  public String filename(DataObjectModel model) {
    boolean gen = model.getAnnotations().stream().anyMatch(ann -> ann.getName().equals(JsonSerGen.class.getName()));
    if (model.isClass() && gen) {
      return model.getFqn() + "JsonSerializer.java";
    }
    return null;
  }

  @Override
  public String render(DataObjectModel model, int index, int size, Map<String, Object> session) {

    formatter = getCase(model);

    StringWriter buffer = new StringWriter();
    PrintWriter writer = new PrintWriter(buffer);
    CodeWriter code = new CodeWriter(writer);
    String visibility= model.isPublicConverter() ? "public" : "";
    boolean inheritConverter = model.getInheritConverter();

    writer.print("package " + model.getType().getPackageName() + ";\n");
    writer.print("\n");
    writer.print("import io.vertx.core.json.JsonObject;\n");
    writer.print("import io.vertx.core.json.JsonArray;\n");
    writer.print("import io.vertx.core.json.impl.JsonUtil;\n");
    writer.print("import java.time.Instant;\n");
    writer.print("import java.time.format.DateTimeFormatter;\n");
    writer.print("import java.util.Base64;\n");
    writer.print("\n");
    writer.print("/**\n");
    writer.print(" * Converter and mapper for {@link " + model.getType() + "}.\n");
    writer.print(" * NOTE: This class has been automatically generated from the {@link " + model.getType() + "} original class using Vert.x codegen.\n");
    writer.print(" */\n");
    code
      .codeln("public class " + model.getType().getSimpleName() + "JsonSerializer {"
      ).newLine();

    writer.print("\n");

    //
    writer.print("  private static final com.fasterxml.jackson.core.JsonFactory JSON_FACTORY = com.fasterxml.jackson.core.JsonFactory.builder().recyclerPool(new com.julienviet.jsonsergen.FastThreadLocalRecyclerPool()).build();\n");

    writer.print("  private static com.fasterxml.jackson.core.JsonGenerator createGenerator(java.io.OutputStream out) throws java.io.IOException {\n");
    writer.print("    return JSON_FACTORY.createGenerator(out);\n");
    writer.print("  }\n");



    writer.print("\n");
    genToJson(visibility, inheritConverter, model, writer);

    writer.print("}\n");
    return buffer.toString();
  }

  private boolean isJsonSerializable(TypeInfo type) {
    TypeElement typeElt = processingEnvironment.getElementUtils().getTypeElement(type.getName());
    if (typeElt != null) {
      List<? extends AnnotationMirror> annotations = typeElt.getAnnotationMirrors();
      for (AnnotationMirror annotation : annotations) {
        if (((TypeElement)annotation.getAnnotationType().asElement()).getQualifiedName().contentEquals(JsonSerGen.class.getName())) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean isJsonSerializable(PropertyInfo prop) {
    TypeInfo type = prop.getType();
    switch (type.getKind()) {
      case API:
        return prop.getType().getName().equals("io.vertx.core.buffer.Buffer");
      case STRING:
      case PRIMITIVE:
      case BOXED_PRIMITIVE:
      case JSON_OBJECT:
      case JSON_ARRAY:
      case ENUM:
      case OBJECT:
        return true;
      case OTHER:
        DataObjectInfo dataObject = type.getDataObject();
        if (dataObject != null) {
          if (isJsonSerializable(type) || dataObject.isSerializable()) {
            return true;
          }
        }
        break;
      default:
        return false;
    }
    return false;
  }

  private void genToJson(String visibility, boolean inheritConverter, DataObjectModel model, PrintWriter writer) {
    String simpleName = model.getType().getSimpleName();
    writer.print("  " + visibility + " static String toJsonString(" + simpleName + " obj) {\n");
    writer.print("    return \"todo\";\n");
    writer.print("  }\n");
    writer.print("\n");
    writer.print("  " + visibility + " static <T> io.vertx.core.buffer.Buffer toJsonBuffer(T obj, java.util.function.BiConsumer<T, com.fasterxml.jackson.core.JsonGenerator> cons) {\n");
    writer.print("    com.fasterxml.jackson.core.util.BufferRecycler br = JSON_FACTORY._getBufferRecycler();\n");
    writer.print("    try (com.fasterxml.jackson.core.util.ByteArrayBuilder bb = new com.fasterxml.jackson.core.util.ByteArrayBuilder(br)) {\n");
    writer.print("      com.fasterxml.jackson.core.JsonGenerator generator = createGenerator(bb);\n");
    writer.print("      cons.accept(obj, generator);\n");
    writer.print("      generator.close();\n");
    writer.print("      byte[] result = bb.toByteArray();\n");
    writer.print("      bb.release();\n");
    writer.print("      return io.vertx.core.buffer.Buffer.buffer(result);\n");
    writer.print("    } catch (java.io.IOException e) {\n");
    writer.print("      throw new io.vertx.core.json.EncodeException(e.getMessage(), e);\n");
    writer.print("    } finally {\n");
    writer.print("      br.releaseToPool();\n");
    writer.print("    }\n");
    writer.print("  }\n");
    writer.print("\n");
    writer.print("  " + visibility + " static io.vertx.core.buffer.Buffer toJsonBuffer(" + simpleName + " obj) {\n");
    writer.print("    return toJsonBuffer(obj, (o, gen) -> toJson2(o, gen));\n");
    writer.print("  }\n");
    writer.print("\n");
    writer.print("  " + visibility + " static io.vertx.core.buffer.Buffer toJsonBuffer(" + simpleName + "[] list) {\n");
    writer.print("    return toJsonBuffer(list, (o, gen) -> toJson2(o, gen));\n");
    writer.print("  }\n");
    writer.print("\n");
    writer.print("  " + visibility + " static io.vertx.core.buffer.Buffer toJsonBuffer(Iterable<" + simpleName + "> list) {\n");
    writer.print("    return toJsonBuffer(list, (o, gen) -> toJson2(o, gen));\n");
    writer.print("  }\n");
    writer.print("\n");
    writer.print("  " + visibility + " static void toJson(" + simpleName + "[] list, com.fasterxml.jackson.core.JsonGenerator generator) throws java.io.IOException {\n");
    writer.print("    generator.writeStartArray();\n");
    writer.print("    for (" + simpleName + " obj : list) {\n");
    writer.print("      toJson(obj, generator);\n");
    writer.print("    }\n");
    writer.print("    generator.writeEndArray();\n");
    writer.print("  }\n");
    writer.print("\n");
    writer.print("  " + visibility + " static void toJson(Iterable<" + simpleName + "> list, com.fasterxml.jackson.core.JsonGenerator generator) throws java.io.IOException {\n");
    writer.print("    generator.writeStartArray();\n");
    writer.print("    for (" + simpleName + " obj : list) {\n");
    writer.print("      toJson(obj, generator);\n");
    writer.print("    }\n");
    writer.print("    generator.writeEndArray();\n");
    writer.print("  }\n");
    writer.print("\n");
    writer.print("  " + visibility + " static void toJson2(" + simpleName + " obj, com.fasterxml.jackson.core.JsonGenerator generator) {\n");
    writer.print("    try {\n");
    writer.print("      toJson(obj, generator);\n");
    writer.print("    }\n");
    writer.print("    catch(java.io.IOException e) {\n");
    writer.print("      throw new io.vertx.core.json.EncodeException(e.getMessage(), e);\n");
    writer.print("    }\n");
    writer.print("  }\n");
    writer.print("\n");
    writer.print("  " + visibility + " static void toJson2(" + simpleName + "[] list, com.fasterxml.jackson.core.JsonGenerator generator) {\n");
    writer.print("    try {\n");
    writer.print("      toJson(list, generator);\n");
    writer.print("    }\n");
    writer.print("    catch(java.io.IOException e) {\n");
    writer.print("      throw new io.vertx.core.json.EncodeException(e.getMessage(), e);\n");
    writer.print("    }\n");
    writer.print("  }\n");
    writer.print("\n");
    writer.print("  " + visibility + " static void toJson2(Iterable<" + simpleName + "> list, com.fasterxml.jackson.core.JsonGenerator generator) {\n");
    writer.print("    try {\n");
    writer.print("      toJson(list, generator);\n");
    writer.print("    }\n");
    writer.print("    catch(java.io.IOException e) {\n");
    writer.print("      throw new io.vertx.core.json.EncodeException(e.getMessage(), e);\n");
    writer.print("    }\n");
    writer.print("  }\n");
    writer.print("  " + visibility + " static void toJson(" + simpleName + " obj, com.fasterxml.jackson.core.JsonGenerator generator) throws java.io.IOException {\n");
    writer.print("    generator.writeStartObject();\n");
    model.getPropertyMap().values().forEach(prop -> {
      if ((prop.isDeclared() || inheritConverter) && prop.getGetterMethod() != null) {
        if (!isJsonSerializable(prop)) {
          writer.print("    // Property " + prop.getName() + " / " + prop.getType() + " is not serializable\n");
          return;
        }
        ClassKind propKind = prop.getType().getKind();
        if (propKind.basic) {
          if (propKind == ClassKind.STRING) {
            genPropToJson("", "", prop, writer);
          } else {
            switch (prop.getType().getSimpleName()) {
              case "char":
              case "Character":
                genPropToJson("Character.toString(", ")", prop, writer);
                break;
              default:
                genPropToJson("", "", prop, writer);
            }
          }
        } else {
          DataObjectInfo dataObject = prop.getType().getDataObject();
          if (dataObject != null) {
            if (dataObject.isSerializable()) {
              String m;
              MapperInfo mapperInfo = dataObject.getSerializer();
              String match;
              switch (mapperInfo.getKind()) {
                case SELF:
                  m = "";
                  match = "." + String.join(".", mapperInfo.getSelectors()) + "()";
                  break;
                case STATIC_METHOD:
                  m = mapperInfo.getQualifiedName() + "." + String.join(".", mapperInfo.getSelectors()) + "(";
                  match = ")";
                  break;
                default:
                  throw new UnsupportedOperationException();
              }
              genPropToJson(m, match, prop, writer);
            } else if (isJsonSerializable(prop.getType())) {
              genPropToJson("", "", prop, writer);
            }
          } else {
            switch (propKind) {
              case API:
                if (prop.getType().getName().equals("io.vertx.core.buffer.Buffer")) {
                  genPropToJson("BASE64_ENCODER.encodeToString(", ".getBytes())", prop, writer);
                }
                break;
              case ENUM:
                genPropToJson("", ".name()", prop, writer);
                break;
              case JSON_OBJECT:
              case JSON_ARRAY:
              case OBJECT:
                genPropToJson("", "", prop, writer);
                break;
              case OTHER:
                if (prop.getType().getName().equals(Instant.class.getName())) {
                  genPropToJson("DateTimeFormatter.ISO_INSTANT.format(", ")", prop, writer);
                }
                break;
            }
          }
        }
      }
    });

    writer.print("    generator.writeEndObject();\n");
    writer.print("  }\n");
  }

  private void generatePropertyName(String indent, String jsonPropertyName, PrintWriter writer) {
    writer.print(indent + "generator.writeFieldName(\"" + jsonPropertyName + "\");\n");
  }

  private void generateProperty(String indent, String before, String expr, String after, TypeInfo type, PrintWriter writer) {
    if (type.getKind() == ClassKind.PRIMITIVE) {
      switch (type.getSimpleName()) {
        case "byte":
        case "short":
        case "int":
        case "long":
        case "double":
        case "float":
          writer.print(indent + "generator.writeNumber(" + before + expr + after + ");\n");
          break;
        case "boolean":
          writer.print(indent + "generator.writeBoolean(" + before + expr + after + ");\n");
          break;
      }
    } else {
      writer.print(indent + "if (" + expr + " != null) {\n");
      switch (type.getKind()) {
        case STRING:
          writer.print(indent + "  generator.writeString(" + before + expr + after + ");\n");
          break;
        case BOXED_PRIMITIVE:
          switch (type.getSimpleName()) {
            case "Byte":
            case "Short":
            case "Integer":
            case "Long":
            case "Double":
            case "Float":
              writer.print(indent + "  generator.writeNumber(" + before + expr + after + ");\n");
              break;
            case "Boolean":
              writer.print(indent + "  generator.writeBoolean(" + before + expr + after + ");\n");
              break;
          }
          break;
        case ENUM:
          writer.print(indent + "  generator.writeString(" + before + expr + after + ");\n");
          break;
        case JSON_OBJECT:
          writer.print(indent + "  io.vertx.core.json.jackson.JacksonCodec.encodeJson(" + before + expr + after + ".getMap(), generator);\n");
          break;
        case JSON_ARRAY:
          writer.print(indent + "  io.vertx.core.json.jackson.JacksonCodec.encodeJson(" + before + expr + after + ".getList(), generator);\n");
          break;
        case OTHER:
          DataObjectInfo dataObject = type.getDataObject();
          if (dataObject != null) {
            if (isJsonSerializable(type)) {
              writer.print(indent + "  " + type.getName() + "JsonSerializer.toJson(" + before + expr + after + ", generator);\n");
            } else if (dataObject.isSerializable()) {
              writer.print(indent + "  io.vertx.core.json.jackson.JacksonCodec.encodeJson(" + before + expr + after + ", generator);\n");
            } else {
              writer.print(indent + "  // TODO kind=" + type.getKind() + "\n");
            }
          }
          break;
        case OBJECT:
          writer.print(indent + "  io.vertx.core.json.jackson.JacksonCodec.encodeJson(" + before + expr + after + ", generator);\n");
          break;
        default:
          writer.print(indent + "  // TODO kind=" + type.getKind() + "\n");
          break;
      }
      writer.print(indent + "} else {\n");
      writer.print(indent + "  generator.writeNull();\n");
      writer.print(indent + "}\n");
    }
  }

  private void genPropToJson(String before, String after, PropertyInfo prop, PrintWriter writer) {
    String indent = "    ";
    String jsonPropertyName = LowerCamelCase.INSTANCE.to(formatter, prop.getName());
    if (prop.isList() || prop.isSet()) {
      generatePropertyName(indent, jsonPropertyName, writer);
      writer.print(indent + "if (obj." + prop.getGetterMethod() + "() != null) {\n");
      writer.print(indent + "  generator.writeStartArray();\n");
      writer.print(indent + "  for (" + prop.getType().getName() + " _elt : obj." + prop.getGetterMethod() + "()) {\n");
      generateProperty("        ", before, "_elt", after, prop.getType(), writer);
      writer.print(indent + "  }\n");
      writer.print(indent + "  generator.writeEndArray();\n");
      writer.print(indent + "} else {\n");
      writer.print(indent + "  generator.writeNull();\n");
      writer.print(indent + "}\n");
    } else if (prop.isMap()) {
      generatePropertyName(indent, jsonPropertyName, writer);
      writer.print(indent + "if (obj." + prop.getGetterMethod() + "() != null) {\n");
      writer.print(indent + "  generator.writeStartObject();\n");
      writer.print(indent + "  for (java.util.Map.Entry<String, " + prop.getType().getName() + "> _elt : obj." + prop.getGetterMethod() + "().entrySet()) {\n");
      writer.print(indent + "    generator.writeFieldName(_elt.getKey());\n");
      generateProperty("        ", before, "_elt", ".getValue()" + after, prop.getType(), writer);
      writer.print(indent + "  }\n");
      writer.print(indent + "  generator.writeEndObject();\n");
      writer.print(indent + "} else {\n");
      writer.print(indent + "  generator.writeNull();\n");
      writer.print(indent + "}\n");
    } else {
      generatePropertyName(indent, jsonPropertyName, writer);
      generateProperty("    ", before, "obj." + prop.getGetterMethod() + "()", after, prop.getType(), writer);
    }
  }

  private Case getCase(DataObjectModel model) {
    AnnotationValueInfo abc = model.getAnnotationContainer();
    ClassTypeInfo cti = (ClassTypeInfo) abc.getMember("jsonPropertyNameFormatter");
    switch (cti.getName()) {
      case "io.vertx.codegen.format.CamelCase":
        return CamelCase.INSTANCE;
      case "io.vertx.codegen.format.SnakeCase":
        return SnakeCase.INSTANCE;
      case "io.vertx.codegen.format.LowerCamelCase":
        return LowerCamelCase.INSTANCE;
      case "io.vertx.codegen.format.KebabCase":
        return KebabCase.INSTANCE;
      case "io.vertx.codegen.format.QualifiedCase":
        return QualifiedCase.INSTANCE;
      default:
        throw new UnsupportedOperationException("Todo");
    }
  }
}
