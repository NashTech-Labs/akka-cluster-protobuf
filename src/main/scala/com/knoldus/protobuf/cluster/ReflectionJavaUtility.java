package com.knoldus.protobuf.cluster;

import akka.actor.ActorRef;
import akka.remote.WireFormats;
import akka.remote.serialization.ProtobufSerializer;
import com.google.protobuf.ByteString;
import scala.Option;

import java.lang.reflect.*;
import java.util.Arrays;

public class ReflectionJavaUtility {

    private ReflectionJavaUtility() {}

    public static Object createInstanceOfProtoClassFromClass(String className, Class o, Object data) throws Exception {
        Class<?> protoClass = Class.forName(className + "Proto");
        if (protoClass.getConstructors().length != 1) {
            throw new RuntimeException();
        } else {
            return createInstanceOfProtoClassFromClass(o, protoClass, data);
        }
    }

    private static Object createInstanceOfProtoClassFromClass(Class<?> from, Class<?> to, Object data) throws Exception {
        Constructor<?> protoClassConstructor = to.getConstructors()[0];
        Object[] protoClassData = Arrays.stream(from.getDeclaredFields())
                .map(field -> method(field, data))
                .toArray();
        return protoClassConstructor.newInstance(protoClassData);
    }

    private static Object method(Field field, Object data) {
        field.setAccessible(true);
        if (field.getType() == ActorRef.class) {
            System.out.println("I am in ActorRef type : " + field.getType());
            return actorRefPathToByteString(getActorRefDataValue(field, data));
        } else if (field.getType() == boolean.class) {
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
        } else if (field.getType() == Option.class) {
            System.out.println("I am in Option type : " + field.getType());
            try {
                return evaluateScalaOptionType(field, data);
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        } else {
            System.out.println("I am in Object type : " + field.getType());
            return getObjectValue(field, data);
        }
    }

    private static ByteString actorRefPathToByteString(WireFormats.ActorRefData refData) {
        return ByteString.copyFrom(refData.toByteString().toByteArray());
    }

    private static Object evaluateScalaOptionType(Field field, Object data) throws Exception {
        Type actualTypeArgument = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
        if (actualTypeArgument == ActorRef.class) {
            Option option = ((Option) field.get(data)).map(ref -> {
                ActorRef actorRef = (ActorRef) ref;
                WireFormats.ActorRefData refData = ProtobufSerializer.serializeActorRef(actorRef);
                return actorRefPathToByteString(refData);
            });
            return option;
        } else {
            return getObjectValue(field, data);
        }
    }

    private static WireFormats.ActorRefData getActorRefDataValue(Field field, Object data) {
        try {
            WireFormats.ActorRefData refData = ProtobufSerializer.serializeActorRef(((ActorRef) field.get(data)));
            return refData;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
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
