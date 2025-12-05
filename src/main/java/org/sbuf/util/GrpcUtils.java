package com.enelx.bfw.framework.util;

import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;

public class GrpcUtils {

    private static final String BUILDER = "Builder";
    private static final String METHOD_GET_PREFIX = "get";
    private static final String METHOD_GRPC_ADD_PREFIX = "add";
    private static final String METHOD_GRPC_SET_PREFIX = "set";
    private static final String METHOD_GRPC_PUT_ALL_PREFIX = "putAll";
    private static final String METHOD_GRPC_ADD_ALL_PREFIX = "addAll";

    private GrpcUtils() {
    }
    
    public static <T extends MessageOrBuilder> void copyProperties(Object source, T target) {
        copyProperties(source, target, null);
    }

    public static <T extends MessageOrBuilder> void copyProperties(Object source, T target,
            Map<String, Function<Object, Object>> converterMap) {

        if (source == null || !(target instanceof Message.Builder builder)) {
            return;
        }

        Class<?> sourceClass = source.getClass();
        Class<?> builderClass = builder.getClass();

        Arrays.stream(sourceClass.getMethods()).filter(GrpcUtils::isGetter).forEach(getter -> {
            String fieldName = getter.getName().substring(3); // getXxx → Xxx
            String camelCaseFieldName = getCamelCaseFieldName(fieldName);

            try {
                Object value = getter.invoke(source);
                if (value == null) {
                    return;
                }

                // Applica converter se presente
                value = applyConverter(converterMap, camelCaseFieldName, value);

                // Ordine logico di gestione: Map → Collection → Nested → Setter
                if (handleMapField(builder, builderClass, fieldName, value))
                    return;
                if (handleCollectionField(builder, builderClass, fieldName, value, converterMap))
                    return;
                if (handleNestedMessage(builder, builderClass, fieldName, value, converterMap))
                    return;

                // Setter semplice
                invokeSetter(builder, builderClass, fieldName, value);

            } catch (Exception e) {
                // puoi loggare ignored.getMessage() per debug
            }
        });
    }

    public static <T> void copyProperties(MessageOrBuilder grpcSource, T pojoTarget) {
        copyProperties(grpcSource, pojoTarget, null);
    }

    public static <T> void copyProperties(MessageOrBuilder source, T target,
                                                  Map<String, Function<Object, Object>> converterMap) {
        if (source == null || target == null) return;

        Class<?> sourceClass = source.getClass();
        Class<?> targetClass = target.getClass();

        Arrays.stream(sourceClass.getMethods())
                .filter(GrpcUtils::isGetter)
                .forEach(getter -> {
                    String fieldName = getter.getName().substring(3);
                    String camelCaseFieldName = getCamelCaseFieldName(fieldName);

                    try {
                        Object value = getter.invoke(source);
                        if (!GrpcUtils.isNonDefaultValue(value)) return;

                        value = applyConverter(converterMap, camelCaseFieldName, value);

                        //Order: Collection → Nested → Setter
                        if (handleCollectionField(target, targetClass, fieldName, value, converterMap)) return;
                        if (handleNestedPojo(target, targetClass, fieldName, value, converterMap)) return;

                        invokeSetter(target, targetClass, fieldName, value);

                    } catch (Exception e) {

                    }
                });
    }

    private static boolean isGetter(Method method) {
        return method.getName().startsWith(METHOD_GET_PREFIX) && method.getParameterCount() == 0;
    }

    private static Object applyConverter(Map<String, Function<Object, Object>> converterMap, String fieldName,
            Object value) {
        if (converterMap == null)
            return value;
        Function<Object, Object> converter = converterMap.get(fieldName);
        return (converter != null) ? converter.apply(value) : value;
    }

    private static boolean handleMapField(Message.Builder builder, Class<?> builderClass, String fieldName,
            Object value) throws ReflectiveOperationException {
        if (!(value instanceof Map<?, ?> mapValue))
            return false;

        Method putAll = findMethod(builderClass, METHOD_GRPC_PUT_ALL_PREFIX + fieldName, Map.class);
        if (putAll != null) {
            putAll.invoke(builder, mapValue);
            return true;
        }
        return false;
    }

    private static boolean handleCollectionField(Message.Builder builder, Class<?> builderClass, String fieldName,
            Object value, Map<String, Function<Object, Object>> converterMap) throws ReflectiveOperationException {
        if (!(value instanceof Collection<?> collection))
            return false;
        if (collection.isEmpty())
            return true; // nulla da copiare

        Object firstElement = collection.iterator().next();

        // Caso 1: tipi primitivi o wrapper
        if (isPrimitiveOrWrapper(firstElement.getClass())) {
            Method adder = findMethod(builderClass, METHOD_GRPC_ADD_ALL_PREFIX + fieldName, Iterable.class);
            if (adder != null) {
                adder.invoke(builder, collection);
            }
            return true;
        }

        // Caso 2: lista di oggetti complessi (Message)
        List<Message> mappedList = new ArrayList<>();
        for (Object item : collection) {
            Message.Builder subBuilder = tryFindSubBuilder(builderClass, fieldName);
            if (subBuilder != null) {
                copyProperties(item, subBuilder, converterMap);
                mappedList.add(subBuilder.build());
            }
        }

        Method addAll = findMethod(builderClass, METHOD_GRPC_ADD_ALL_PREFIX + fieldName, Iterable.class);
        if (addAll != null) {
            addAll.invoke(builder, mappedList);
        } else {
            Method addOne = findSingleAddMethod(builderClass, fieldName);
            if (addOne != null) {
                for (Message m : mappedList) {
                    addOne.invoke(builder, m);
                }
            }
        }
        return true;
    }

    private static boolean handleCollectionField(Object target, Class<?> targetClass, String fieldName,
                                                 Object value, Map<String, Function<Object, Object>> converterMap) throws Exception {
        if (!(value instanceof List<?> sourceList)) return false;
        if (sourceList.isEmpty()) return false;

        String setterName = "set" + fieldName;
        for (Method setter : targetClass.getMethods()) {
            if (setter.getName().equalsIgnoreCase(setterName) && setter.getParameterCount() == 1) {
                Type type = setter.getGenericParameterTypes()[0];
                if (!(type instanceof ParameterizedType pt)) return false;

                Type[] typeArgs = pt.getActualTypeArguments();
                if (typeArgs.length != 1 || !(typeArgs[0] instanceof Class<?> elementType)) return false;

                Collection<Object> targetList = new ArrayList<>();
                for (Object item : sourceList) {
                    if (GrpcUtils.isPrimitiveOrWrapper(item.getClass())) {
                        targetList.add(item);
                    } else {
                        Object pojoItem = elementType.getDeclaredConstructor().newInstance();
                        copyProperties((MessageOrBuilder) item, pojoItem, converterMap);
                        targetList.add(pojoItem);
                    }
                }

                setter.invoke(target, targetList);
                return true;
            }
        }

        return false;
    }

    private static boolean handleNestedPojo(Object target, Class<?> targetClass, String fieldName,
                                            Object value, Map<String, Function<Object, Object>> converterMap) throws Exception {
        if (!(value instanceof MessageOrBuilder nestedGrpc)) return false;

        String setterName = "set" + fieldName;
        for (Method setter : targetClass.getMethods()) {
            if (setter.getName().equalsIgnoreCase(setterName) && setter.getParameterCount() == 1) {
                Class<?> paramType = setter.getParameterTypes()[0];
                Object nestedPojo = paramType.getDeclaredConstructor().newInstance();
                copyProperties(nestedGrpc, nestedPojo, converterMap);
                setter.invoke(target, nestedPojo);
                return true;
            }
        }

        return false;
    }

    private static boolean handleNestedMessage(Message.Builder builder, Class<?> builderClass, String fieldName,
            Object value, Map<String, Function<Object, Object>> converterMap) throws ReflectiveOperationException {
        Method subBuilderMethod = findMethod(builderClass, METHOD_GET_PREFIX + fieldName + BUILDER);
        if (subBuilderMethod == null)
            return false;

        Object subBuilder = subBuilderMethod.invoke(builder);
        if (subBuilder instanceof MessageOrBuilder subMsg) {
            copyProperties(value, subMsg, converterMap);
            return true;
        }
        return false;
    }

    private static void invokeSetter(Message.Builder builder, Class<?> builderClass, String fieldName, Object value)
            throws ReflectiveOperationException {
        Method setter = findMethod(builderClass, METHOD_GRPC_SET_PREFIX + fieldName, value.getClass());
        if (setter != null) {
            setter.invoke(builder, value);
        }
    }

    private static void invokeSetter(Object target, Class<?> targetClass, String fieldName, Object value) throws Exception {
        String setterName = "set" + fieldName;
        for (Method method : targetClass.getMethods()) {
            if (method.getName().equalsIgnoreCase(setterName) && method.getParameterCount() == 1) {
                method.invoke(target, value);
                return;
            }
        }
    }
    
    // Helper per cercare un metodo compatibile
    private static Method findMethod(Class<?> clazz, String name, Class<?>... paramTypes) {
        for (Method m : clazz.getMethods()) {
            if (m.getName().equals(name) && m.getParameterCount() == paramTypes.length
                    && (paramTypes.length == 0 || isParameterTypesCompatible(m.getParameterTypes(), paramTypes))) {
                return m;
            }
        }
        return null;
    }

    // Cerca addXxx(T value)
    private static Method findSingleAddMethod(Class<?> clazz, String fieldName) {
        for (Method m : clazz.getMethods()) {
            if (m.getName().equals(METHOD_GRPC_ADD_PREFIX + fieldName) && m.getParameterCount() == 1) {
                return m;
            }
        }
        return null;
    }

    // Crea un builder per un oggetto annidato
    private static Message.Builder tryFindSubBuilder(Class<?> parentBuilderClass, String fieldName) {
        try {
            // es. getAddressBuilder() o addAddressBuilder()
            Method subBuilderMethod = findMethod(parentBuilderClass, METHOD_GET_PREFIX + fieldName + BUILDER);
            if (subBuilderMethod != null) {
                return (Message.Builder) subBuilderMethod.invoke(parentBuilderClass.getDeclaredConstructor().newInstance());
            }
        } catch (Exception ignored) {}

        // se non esiste getXxxBuilder, prova a dedurre il tipo dal nome
        try {
            String className = parentBuilderClass.getName()
                    .replace("OuterClass$", "") // per i nested types
                    .replace(BUILDER, fieldName);
            Class<?> subClass = Class.forName(className);
            Method newBuilder = subClass.getMethod("newBuilder");
            return (Message.Builder) newBuilder.invoke(null);
        } catch (Exception ignored) {}

        return null;
    }

    private static boolean isParameterTypesCompatible(Class<?>[] methodTypes, Class<?>[] paramTypes) {
        for (int i = 0; i < methodTypes.length; i++) {
            if (!methodTypes[i].isAssignableFrom(paramTypes[i])) {
                return false;
            }
        }
        return true;
    }

    private static boolean isPrimitiveOrWrapper(Class<?> type) {
        return type.isPrimitive()
                || type.equals(String.class)
                || Number.class.isAssignableFrom(type)
                || type.equals(Boolean.class)
                || type.equals(Character.class);
    }

    private static String getCamelCaseFieldName(String fieldName) {
        char[] c = fieldName.toCharArray();
        c[0] = Character.toLowerCase(c[0]);
        return new String(c);
    }

    private static boolean isNonDefaultValue(Object value) {
        if (value == null) return false;
        if (value instanceof String str) {
            return !str.isBlank();
        }
        if (value instanceof Number num) {
            return num.doubleValue() != 0;
        }
        if (value instanceof Boolean bool) {
            return bool;
        }
        if (value instanceof Collection<?> collection) {
            return !collection.isEmpty();
        }
        return true;
    }
}
