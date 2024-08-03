package com.julienviet.jsonsergen;

import io.vertx.codegen.DataObjectModel;
import io.vertx.codegen.Generator;
import io.vertx.codegen.PropertyInfo;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.format.*;
import io.vertx.codegen.type.*;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.TypeElement;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.time.Instant;
import java.util.*;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class JsonSerGenerator extends Generator<DataObjectModel> {

  private Case formatter;
  private ProcessingEnvironment processingEnvironment;
  private List<WriterPlugin> writerPlugins = new ArrayList<>();
  private WriterPlugin writerPlugin;

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

    AnnotationValueInfo info = model.getAnnotations().stream().filter(ann -> ann.getName().equals(JsonSerGen.class.getName())).findFirst().get();
    List<String> backends = (List<String>) info.getMember("backends");
    writerPlugins.clear();
    backends.forEach(backendName -> {
      for (Backend backend : Backend.values()) {
        if (backendName.equals(backend.name())) {
          writerPlugins.add(backend.plugin);
        }
      }
    });

    formatter = getCase(model);

    StringWriter buffer = new StringWriter();
    IndentableWriter writer = new IndentableWriter(buffer);
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
    writer.print("public class " + model.getType().getSimpleName() + "JsonSerializer {\n");

    // Blank
    writer.print("\n");
    writer.indent();

    //
    writerPlugins.forEach(p -> {
      writerPlugin = p;
      writer.print("public static class " + p.scope() + " {\n");
      writer.indent();
      p.beginClass(writer, model.getFqn());
      genToJson(visibility, inheritConverter, model, writer);
      writer.unindent();
      writer.print("}\n");
    });

    if (writerPlugins.size() > 0) {
      writer.print("public static io.vertx.core.buffer.Buffer toJsonBuffer(" + model.getFqn() + " obj) {\n");
      writer.print("  return " + writerPlugins.get(0).scope() + ".toJsonBuffer(obj);\n");
      writer.print("}\n");
      writer.print("public static io.vertx.core.buffer.Buffer toJsonBuffer(java.util.List<" + model.getFqn() + "> obj) {\n");
      writer.print("  return " + writerPlugins.get(0).scope() + ".toJsonBuffer(obj);\n");
      writer.print("}\n");
      writer.print("public static io.vertx.core.buffer.Buffer toJsonBuffer(" + model.getFqn() + "[] obj) {\n");
      writer.print("  return " + writerPlugins.get(0).scope() + ".toJsonBuffer(obj);\n");
      writer.print("}\n");
    }

    writer.unindent();
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

  private boolean first;

  private void genToJson(String visibility, boolean inheritConverter, DataObjectModel model, IndentableWriter writer) {
    declared.clear();
    writerPlugin.beginObject(writer, model.getFqn());
    writer.indent();
    model.getPropertyMap().values().forEach(prop -> {
      String type;
      switch (prop.getKind()) {
        case VALUE:
          type = prop.getType().getName();
          break;
        case LIST:
          type = "java.util.List<" + prop.getType().getName() + ">";
          break;
        case SET:
          type = "java.util.Set<" + prop.getType().getName() + ">";
          break;
        case MAP:
          type = "java.util.Map<String, " + prop.getType().getName() + ">";
          break;
        default:
          throw new AssertionError();
      }
      writer.print(type + " " + prop.getName() + ";\n");
    });
    writerPlugin.genWriteBeginObject(writer);
    first = true;
    model.getPropertyMap().values().forEach(prop -> {
      if ((prop.isDeclared() || inheritConverter) && prop.getGetterMethod() != null) {
        if (!isJsonSerializable(prop)) {
          writer.print("// Property " + prop.getName() + " / " + prop.getType() + " is not serializable\n");
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

    writerPlugin.genWriteEndObject(writer);
    writer.unindent();
    writerPlugin.endObject(writer, model.getFqn());
  }

  private void generatePropertyName(String jsonPropertyName, IndentableWriter writer) {
    writerPlugin.genWriteFieldName(writer, '"' + jsonPropertyName + '"');
  }

  private void generateProperty(String before, String expr, String after, TypeInfo type, IndentableWriter writer) {
    if (type.getKind() == ClassKind.PRIMITIVE) {
      switch (type.getSimpleName()) {
        case "byte":
        case "short":
        case "int":
        case "long":
        case "double":
        case "float":
          writerPlugin.genWriteNumber(writer, before + expr + after, type.getName());
          break;
        case "boolean":
          writerPlugin.genWriteBoolean(writer, before + expr + after);
          break;
      }
    } else {
      writer.print("if (" + expr + " != null) {\n");
      writer.indent();
      switch (type.getKind()) {
        case STRING:
          writerPlugin.genWriteString(writer, before + expr + after);
          break;
        case BOXED_PRIMITIVE:
          switch (type.getSimpleName()) {
            case "Byte":
            case "Short":
            case "Integer":
            case "Long":
            case "Double":
            case "Float":
              writerPlugin.genWriteNumber(writer, before + expr + after, type.getName());
              break;
            case "Boolean":
              writerPlugin.genWriteBoolean(writer, before + expr + after);
              break;
          }
          break;
        case ENUM:
          writerPlugin.genWriteString(writer, before + expr + after);
          break;
        case JSON_OBJECT:
          writerPlugin.genWriteJson(writer, before + expr + after);
          break;
        case JSON_ARRAY:
          writerPlugin.genWriteJson(writer, before + expr + after);
          break;
        case OTHER:
          DataObjectInfo dataObject = type.getDataObject();
          if (dataObject != null) {
            if (isJsonSerializable(type)) {
              writerPlugin.genWriteJsonSerGen(writer, type.getName() + "JsonSerializer." + writerPlugin.scope(), before + expr + after);
            } else if (dataObject.isSerializable()) {
              writerPlugin.genWriteJson(writer, before + expr + after);
            } else {
              writer.print("// TODO kind=" + type.getKind() + "\n");
            }
          }
          break;
        case OBJECT:
//          writer.print("io.vertx.core.json.jackson.JacksonCodec.encodeJson(" + before + expr + after + ", generator);\n");
          break;
        default:
          writer.print("// TODO kind=" + type.getKind() + "\n");
          break;
      }
      writer.unindent();
      writer.print("} else {\n");
      writer.indent();
      writerPlugin.genWriteNull(writer);
      writer.unindent();
      writer.print("}\n");
    }
  }

  private Set<String> declared = new HashSet<>();

  private void genPropToJson(String before, String after, PropertyInfo prop, IndentableWriter writer) {
    if (first) {
      first = false;
    } else {
      writerPlugin.genWriteObjectSeparator(writer);
    }
    String jsonPropertyName = LowerCamelCase.INSTANCE.to(formatter, prop.getName());
    generatePropertyName(jsonPropertyName, writer);
    writer.print(prop.getName() + " = obj." + prop.getGetterMethod() + "();\n");
    if (prop.isList() || prop.isSet()) {
      writer.print("if (" + prop.getName() + " != null) {\n");
      writer.indent();
      writerPlugin.genWriteBeginArray(writer);
      if (prop.isList()) {
        writer.print("int len_ = " + prop.getName() + ".size();\n");
        writer.print("for (int i_ = 0;i_ < len_;i_++) {\n");
        writer.indent();
        writer.print("if (i_ > 0) {\n");
        writer.indent();
        writerPlugin.genWriteArraySeparator(writer);
        writer.unindent();
        writer.print("}\n");
        writer.print(prop.getType().getName() + " elt_ = " + prop.getName() + ".get(i_);\n");
        generateProperty(before, "elt_", after, prop.getType(), writer);
        writer.unindent();
        writer.print("}\n");
      } else {
        writer.print("for (java.util.Iterator<" + prop.getType().getName() + "> i_ = " +  prop.getName() + ".iterator();i_.hasNext();) {\n");
        writer.indent();
        writer.print(prop.getType().getName() + " elt_ = i_.next();\n");
        generateProperty(before, "elt_", after, prop.getType(), writer);
        writer.print("if (i_.hasNext()) {\n");
        writer.indent();
        writerPlugin.genWriteArraySeparator(writer);
        writer.unindent();
        writer.print("}\n");
        writer.unindent();
        writer.print("}\n");
      }

      writerPlugin.genWriteEndArray(writer);
      writer.unindent();
      writer.print("} else {\n");
      writer.indent();
      writerPlugin.genWriteNull(writer);
      writer.unindent();
      writer.print("}\n");
    } else if (prop.isMap()) {
      writer.print("if (" + prop.getName() + " != null) {\n");
      writer.indent();
      writerPlugin.genWriteBeginObject(writer);
      writer.print("for (java.util.Map.Entry<String, " + prop.getType().getName() + "> _elt : " + prop.getName() + ".entrySet()) {\n");
      writer.indent();
      writerPlugin.genWriteFieldName(writer, "_elt.getKey()");
      generateProperty(before, "_elt", ".getValue()" + after, prop.getType(), writer);
      writer.unindent();
      writer.print("}\n");
      writerPlugin.genWriteEndObject(writer);
      writer.unindent();
      writer.print("} else {\n");
      writer.indent();
      writerPlugin.genWriteNull(writer);
      writer.unindent();
      writer.print("}\n");
    } else {
      generateProperty(before, prop.getName(), after, prop.getType(), writer);
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
