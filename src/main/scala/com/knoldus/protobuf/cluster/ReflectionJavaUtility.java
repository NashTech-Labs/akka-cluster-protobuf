package com.knoldus.protobuf.cluster;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.ExtendedActorSystem;
import akka.remote.WireFormats;
import akka.remote.serialization.ProtobufSerializer;
import com.google.protobuf.ByteString;
import scala.Option;

import java.lang.reflect.*;
import java.util.Arrays;

public class ReflectionJavaUtility {

    public static final String PROTO_SUFFIX = "Proto";

    private ReflectionJavaUtility() {
    }

    public static Object createInstanceOfProtoClassFromClass(String className, Class o, Object data) throws Exception {
        Class<?> protoClass = Class.forName(className + PROTO_SUFFIX);
        if (protoClass.getConstructors().length != 1) {
            throw new RuntimeException();
        } else {
            return createInstanceOfProtoClassFromClass(o, protoClass, data, null);
        }
    }

    public static Object createInstanceOfClassFromProtoClass(String className, Class o, Object data, ExtendedActorSystem system) throws Exception {
        Class<?> projectClass = Class.forName(className.substring(0, (className.length() - PROTO_SUFFIX.length())));
        if (projectClass.getConstructors().length != 1) {
            throw new RuntimeException();
        } else {
            return createInstanceOfProtoClassFromClass(o, projectClass, data, system);
        }
    }

    private static Object createInstanceOfProtoClassFromClass(Class<?> from, Class<?> to, Object data, ExtendedActorSystem system) throws Exception {
        Constructor<?> protoClassConstructor = to.getConstructors()[0];
        Object[] protoClassData = Arrays.stream(from.getDeclaredFields())
                .map(field -> {
                    field.setAccessible(true);
                    try {
                        Object value = extractValueFromPrimitiveField(field, data);
                        if (value == null) {
                            return extractValueFromObjectField(field, data, system);
                        }
                        return value;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return Option.empty();
                    }
                })
                .toArray();
        return protoClassConstructor.newInstance(protoClassData);
    }

    private static Object extractValueFromObjectField(Field field, Object data, ExtendedActorSystem system) throws Exception {
        if (field.getType() == ActorRef.class) {
            System.out.println("I am in ActorRef type : " + field.getType());
            return getActorRefDataValue(field, data);
        } else if (field.getType() == ByteString.class) {
            System.out.println("I am in ByteString type : " + field.getType());
            return evaluateByteStringType(field, data, system);
        } else if (field.getType() == Option.class) {
            System.out.println("I am in Option type : " + field.getType());
            return evaluateScalaOptionType(field, data, system);
        } else {
            System.out.println("I am in Object type : " + field.getType());
            return getObjectValue(field, data);
        }
    }

    private static Object extractValueFromPrimitiveField(Field field, Object data) {
        if (field.getType() == boolean.class) {
            System.out.println("I am in boolean type : " + field.getType());
            return getBooleanValue(field, data);
        } else if (field.getType() == byte.class) {
            System.out.println("I am in byte type : " + field.getType());
            return getByteValue(field, data);
        } else if (field.getType() == char.class) {
            System.out.println("I am in char type : " + field.getType());
            return getCharValue(field, data);
        } else if (field.getType() == short.class) {
            System.out.println("I am in short type : " + field.getType());
            return getShortValue(field, data);
        } else if (field.getType() == int.class) {
            System.out.println("I am in int type : " + field.getType());
            return getIntValue(field, data);
        } else if (field.getType() == long.class) {
            System.out.println("I am in long type : " + field.getType());
            return getLongValue(field, data);
        } else if (field.getType() == float.class) {
            System.out.println("I am in float type : " + field.getType());
            return getFloatValue(field, data);
        } else if (field.getType() == double.class) {
            System.out.println("I am in double type : " + field.getType());
            return getDoubleValue(field, data);
        } else {
            System.out.println("No matched value in primitive : " + field.getType());
            return null;
        }
    }

    private static ByteString actorRefToByteString(ActorRef actorRef) {
        WireFormats.ActorRefData refData = ProtobufSerializer.serializeActorRef(actorRef);
        return ByteString.copyFrom(refData.toByteString().toByteArray());
    }

    private static ByteString getActorRefDataValue(Field field, Object data) {
        try {
            return actorRefToByteString((ActorRef) field.get(data));
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static Object byteStringToActorRef(ByteString byteString, ExtendedActorSystem system) {
        try {
            WireFormats.ActorRefData refData = WireFormats.ActorRefData.parseFrom(akka.protobuf.ByteString.copyFrom(byteString.toByteArray()));
            return ProtobufSerializer.deserializeActorRef(system, refData);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

    }

    private static Object evaluateByteStringType(Field field, Object data, ExtendedActorSystem system) {
        try {
            return byteStringToActorRef((ByteString) field.get(data), system);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static Object evaluateScalaOptionType(Field field, Object data, ExtendedActorSystem system) throws Exception {
        Type actualTypeArgument = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
        if (actualTypeArgument == ActorRef.class) {
            return ((Option) field.get(data)).map(ref -> {
                ActorRef actorRef = (ActorRef) ref;
                return actorRefToByteString(actorRef);
            });
        } else if (actualTypeArgument == ByteString.class) {
            return ((Option) field.get(data)).map(byteString -> {
                ByteString bytString = (ByteString) byteString;
                return byteStringToActorRef(bytString, system);
            });
        } else {
            return getObjectValue(field, data);
        }
    }

    private static boolean getBooleanValue(Field field, Object data) {
        try {
            return field.getBoolean(data);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static byte getByteValue(Field field, Object data) {
        try {
            return field.getByte(data);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static char getCharValue(Field field, Object data) {
        try {
            return field.getChar(data);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return 'x';
        }
    }

    private static short getShortValue(Field field, Object data) {
        try {
            return field.getShort(data);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static int getIntValue(Field field, Object data) {
        try {
            return field.getInt(data);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static long getLongValue(Field field, Object data) {
        try {
            return field.getLong(data);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static float getFloatValue(Field field, Object data) {
        try {
            return field.getFloat(data);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static double getDoubleValue(Field field, Object data) {
        try {
            return field.getDouble(data);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static Object getObjectValue(Field field, Object data) {
        try {
            return field.get(data);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}
