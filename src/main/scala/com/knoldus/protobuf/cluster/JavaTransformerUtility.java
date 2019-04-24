package com.knoldus.protobuf.cluster;

import akka.actor.ActorRef;
import akka.actor.ExtendedActorSystem;
import akka.remote.WireFormats;
import akka.remote.serialization.ProtobufSerializer;
import com.google.protobuf.ByteString;
import scala.Enumeration;
import scala.Option;
import scalapb.GeneratedEnum;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class JavaTransformerUtility implements ReflectionUtility {

    static final List<String> predefineIgnoredFields = Arrays.asList(
            "serialVersionUID",
            "__serializedSizeCachedValue"
    );

    private JavaTransformerUtility() {
    }

    public static Object createInstanceOfProtoClassFromClass(String className, Class<?> clazzType, Object clazzTypeData) throws Exception {
        Class<?> protoClass = Class.forName(className + PROTO_SUFFIX);
        if (protoClass.getConstructors().length != 1) {
            throw new TransformerUtilityException(protoClass.getCanonicalName() + " class doesn't contains only one constructor", null);
        } else {
            return createInstanceOfProtoClassFromClass(clazzType, protoClass, clazzTypeData, null);
        }
    }

    public static Object createInstanceOfClassFromProtoClass(String className, Class<?> protobufSerializableClazz, Object protobufSerializableData, ExtendedActorSystem system) throws Exception {
        Class<?> clazz = Class.forName(className.substring(0, (className.length() - PROTO_SUFFIX.length())));
        if (clazz.getConstructors().length != 1) {
            throw new TransformerUtilityException(clazz.getCanonicalName() + " class doesn't contains only one constructor", null);
        } else {
            return createInstanceOfProtoClassFromClass(protobufSerializableClazz, clazz, protobufSerializableData, system);
        }
    }

    private static Object createInstanceOfProtoClassFromClass(Class<?> from, Class<?> to, Object data, ExtendedActorSystem system) throws Exception {
        Constructor<?> protoClassConstructor = to.getConstructors()[0];

        Object[] protoClassData = Arrays.stream(from.getDeclaredFields())
                .filter(field -> !predefineIgnoredFields.contains(field.getName()))
                .map(field -> {
                    try {
                        if (to.getDeclaredField(field.getName()).getType() == Option.class && field.getType() != Option.class) {
                            return Option.apply(extractValueFromField(field, data, system));
                        } else if (field.getType() == Option.class && to.getDeclaredField(field.getName()).getType() != Option.class) {
                            Option option = (Option) extractValueFromField(field, data, system);
                            return option.getOrElse(() -> null);
                        } else {
                            return extractValueFromField(field, data, system);
                        }
                    } catch (Exception ex) {
                        String errorMessage = "Failed to convert " + from.getCanonicalName() + " class " + field.getName() + " filed to "
                                + to.getCanonicalName() + " class " + field.getName() + " field";
                        throw new TransformerUtilityException(errorMessage, ex);
                    }
                }).toArray();
        return protoClassConstructor.newInstance(protoClassData);
    }

    private static Object extractValueFromField(Field field, Object data, ExtendedActorSystem system) {
        field.setAccessible(true);
        Object value = extractValueFromPrimitiveField(field, data);
        if (value == null) {
            return extractValueFromObjectField(field, data, system);
        }
        return value;
    }

    private static Object extractValueFromPrimitiveField(Field field, Object data) {
        if (field.getType() == boolean.class) {
            System.out.println("I am in boolean type : " + field.getType());
            return extractValueFromField(obj -> field.getBoolean(obj), field, data);
        } else if (field.getType() == byte.class) {
            System.out.println("I am in byte type : " + field.getType());
            return extractValueFromField(obj -> field.getByte(obj), field, data);
        } else if (field.getType() == char.class) {
            System.out.println("I am in char type : " + field.getType());
            return extractValueFromField(obj -> field.getChar(obj), field, data);
        } else if (field.getType() == short.class) {
            System.out.println("I am in short type : " + field.getType());
            return extractValueFromField(obj -> field.getShort(obj), field, data);
        } else if (field.getType() == int.class) {
            System.out.println("I am in int type : " + field.getType());
            return extractValueFromField(obj -> field.getInt(obj), field, data);
        } else if (field.getType() == long.class) {
            System.out.println("I am in long type : " + field.getType());
            return extractValueFromField(obj -> field.getLong(obj), field, data);
        } else if (field.getType() == float.class) {
            System.out.println("I am in float type : " + field.getType());
            return extractValueFromField(obj -> field.getFloat(obj), field, data);
        } else if (field.getType() == double.class) {
            System.out.println("I am in double type : " + field.getType());
            return extractValueFromField(obj -> field.getDouble(obj), field, data);
        } else {
            System.out.println("No matched value in primitive : " + field.getType());
            return null;
        }
    }

    private static Object extractValueFromObjectField(Field field, Object data, ExtendedActorSystem system) {
        if (field.getType() == String.class) {
            System.out.println("I am in String type : " + field.getType());
            return extractValueFromField(obj -> field.get(obj), field, data);
        } else if (field.getType() == ActorRef.class) {
            System.out.println("I am in ActorRef type : " + field.getType());
            return extractValueFromField(obj -> actorRefToByteString((ActorRef) field.get(obj)), field, data);
        } else if (field.getType() == ByteString.class) {
            System.out.println("I am in ByteString type : " + field.getType());
            return extractValueFromField(obj -> byteStringToActorRef((ByteString) field.get(obj), system), field, data);
        } else if (field.getType() == Option.class) {
            System.out.println("I am in Option type : " + field.getType());
            return evaluateScalaOptionType(field, data, system);
        } else if (field.getType() == Enumeration.Value.class) {
            System.out.println("I am in Enumeration.Value type : " + field.getType());
            Enumeration.Value value = (Enumeration.Value) extractValueFromField(obj -> field.get(obj), field, data);
            return resolveScalaEnumeration(value);
        } else if (GeneratedEnum.class.isAssignableFrom(field.getType())) {
            String enumerationClassName = findEnumerationClassName(field);
            GeneratedEnum value = (GeneratedEnum) extractValueFromField(obj -> field.get(obj), field, data);
            return ScalaTransformerUtility.convertGeneratedEnumValueToEnumerationValue(enumerationClassName, value.index());
        } else {
            Object value = extractValueFromField(obj -> field.get(obj), field, data);
            return resolveNestedObjects(value, system);
        }
    }

    private static Object resolveNestedObjects(Object value, ExtendedActorSystem system) {
        String valueClassName = value.getClass().getCanonicalName();
        try {
            if (valueClassName.endsWith(PROTO_SUFFIX)) {
                return createInstanceOfClassFromProtoClass(valueClassName, value.getClass(), value, system);
            } else {
                return createInstanceOfProtoClassFromClass(valueClassName, value.getClass(), value);
            }
        } catch (Exception ex) {
            String errorMessage = "Unable to resolve nested objects of class" + valueClassName + "class.";
            throw new TransformerUtilityException(errorMessage, ex);
        }
    }

    private static Object resolveScalaEnumeration(Enumeration.Value value) {

        Class<?> classType = ScalaTransformerUtility.findEnumerationOuterType(value.getClass(), value);
        return ScalaTransformerUtility.convertEnumerationValueToGeneratedEnumValue(classType, value.id());
    }

    private static ByteString actorRefToByteString(ActorRef actorRef) {
        WireFormats.ActorRefData refData = ProtobufSerializer.serializeActorRef(actorRef);
        return ByteString.copyFrom(refData.toByteString().toByteArray());
    }

    private static Object byteStringToActorRef(ByteString byteString, ExtendedActorSystem system) {
        try {
            WireFormats.ActorRefData refData = WireFormats.ActorRefData
                    .parseFrom(akka.protobuf.ByteString.copyFrom(byteString.toByteArray()));
            return ProtobufSerializer.deserializeActorRef(system, refData);
        } catch (Exception ex) {
            String errorMessage = "Unable to parse ByteString " + byteString + " to ActorRef";
            throw new TransformerUtilityException(errorMessage, ex);
        }
    }

    private static boolean isPrimitive(String typeName) {
        return Arrays.asList(
                Boolean.class.getName(), Byte.class.getName(), Character.class.getName(),
                Short.class.getName(), Integer.class.getName(), Long.class.getName(), Float.class.getName(), Double.class
        ).contains(typeName);
    }

    private static Object evaluateScalaOptionType(Field field, Object data, ExtendedActorSystem system) {
        Option fieldValue = (Option) extractValueFromField(obj -> field.get(obj), field, data);

        return fieldValue.map(optionValue -> {
            if (isPrimitive(optionValue.getClass().getTypeName())) {
                return optionValue;
            } else if (optionValue instanceof ActorRef) {
                ActorRef actorRef = (ActorRef) optionValue;
                return actorRefToByteString(actorRef);
            } else if (optionValue instanceof ByteString) {
                ByteString bytString = (ByteString) optionValue;
                return byteStringToActorRef(bytString, system);
            } else if (optionValue instanceof Enumeration.Value) {
                Enumeration.Value value = (Enumeration.Value) optionValue;
                return resolveScalaEnumeration(value);
            } else if (optionValue instanceof GeneratedEnum) {
                String enumerationClassName = findEnumerationClassName(field);
                GeneratedEnum value = (GeneratedEnum) optionValue;
                return ScalaTransformerUtility.convertGeneratedEnumValueToEnumerationValue(enumerationClassName, value.index());
            } else {
                return resolveNestedObjects(optionValue, system);
            }
        });
    }

    private static <R> R extractValueFromField(CheckedFunction<R, Object> function, Field field, Object data) {
        try {
            return function.apply(data);
        } catch (Exception ex) {
            String errorMessage = "Class " + data.getClass().getName() + " field " + field.getName() + "contains Invalid data";
            throw new InvalidFieldDataException(errorMessage, ex);
        }
    }

    private static String findEnumerationClassName(Field field) {
        String fieldTypeName = field.getType().getCanonicalName();
        return fieldTypeName.substring(0, fieldTypeName.length() - PROTO_SUFFIX.length()) + "$";
    }
}
