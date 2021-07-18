package com.lagou.edu.frame.start.ioc.autowired;

import com.lagou.edu.frame.annotation.ioc.Autowired;
import com.lagou.edu.frame.start.ioc.BeanFactory;

public class TypeProcess {
    BeanFactory beanFactory;

    public TypeProcess(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public <T> T GetValueBasedOnClassTypeAndAnnotation(Class<?> type, Autowired annotation, String referenceClassName) throws Exception {
        Object o = null;
        String value = "";
        String typeName = "";
        if (annotation != null) {
            value = annotation.value();
        }
        //将if语句条件升级成b，以后改条件只需改一处
        boolean b = value.equals("");
        try {
            //测试下面方法，或许可以大量减少if。。。else语句
            //父类.class.isAssignableFrom(子类.class)
            if (type == String.class) {
                typeName = "String";
                if (!b) {
                    o = value;
                }
                System.out.println("String");
            } else if (type == Byte.class) {
                typeName = "Byte";
                if (!b) {
                    o = Byte.valueOf(value);
                }
                System.out.println("Byte");
            } else if (type == Short.class) {
                typeName = "Short";
                if (!b) {
                    o = Short.valueOf(value);
                }
                System.out.println("Short");
            } else if (type == Integer.class) {
                typeName = "Integer";
                if (!b) {
                    o = Integer.valueOf(value);
                }
                System.out.println("Integer");
            } else if (type == Double.class) {
                typeName = "Double";
                if (!b) {
                    o = Double.valueOf(value);
                }
                System.out.println("Double");
            } else if (type == Float.class) {
                typeName = "Float";
                if (!b) {
                    o = Float.valueOf(value);
                }
                System.out.println("Float");
            } else if (type == Long.class) {
                typeName = "Long";
                if (!b) {
                    o = Long.valueOf(value);
                }
                System.out.println("Long");
            } else if (type == Character.class) {
                typeName = "Character";
                if (!b) {
                    o = value.charAt(0);
                }
                System.out.println("Character");
            } else if (type == Boolean.class) {
                typeName = "Boolean";
                if (!b) {
                    o = Boolean.valueOf(value);
                }
                System.out.println("Boolean");
            } else if (type == null) {
                typeName = "null";
                System.out.println("null");
            } else if (type == byte.class) {
                typeName = "byte";
                if (b) {
                    o = (byte) 0;
                } else {
                    o = Byte.parseByte(value);
                }
                System.out.println("byte");
            } else if (type == short.class) {
                typeName = "short";
                if (b) {
                    o = (short) 0;
                } else {
                    o = Short.parseShort(value);
                }
                System.out.println("short");
            } else if (type == int.class) {
                typeName = "int";
                if (b) {
                    o = 0;
                } else {
                    o = Integer.parseInt(value);
                }
                System.out.println("int");
            } else if (type == double.class) {
                typeName = "double";
                if (b) {
                    o = 0d;
                } else {
                    o = Double.parseDouble(value);
                }
                System.out.println("double");
            } else if (type == float.class) {
                typeName = "float";
                if (b) {
                    o = 0f;
                } else {
                    o = Float.parseFloat(value);
                }
                System.out.println("float");
            } else if (type == long.class) {
                typeName = "long";
                if (b) {
                    o = 0L;
                } else {
                    o = Long.parseLong(value);
                }
                System.out.println("long");
            } else if (type == char.class) {
                typeName = "char";
                if (b) {
                    o = '\u0000';
                } else {
                    o = value.charAt(0);
                }
                System.out.println("char");
            } else if (type == boolean.class) {
                typeName = "boolean";
                if (b) {
                    o = false;
                } else {
                    o = Boolean.parseBoolean(value);
                }
                System.out.println("boolean");
            } else {
                //没有处理如果是接口类型取它的实现类，如果是多个实现类，必须指定类全名，否则报错
                typeName = type.getSimpleName();
                //优先通过注解指定的名称查找并注入类，如未指定，使用类型注入
                if (b) {
                    if (type.isInterface()) {
                        throw new Exception("接口必须在注解中指定实现类的全限定名");
                    } else {
                        value = type.getName();
                    }
                }
                //一级缓存中没有则可以通过value创建
                o = beanFactory.GetBean(typeName, value, referenceClassName);
                System.out.println("Custom");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("value \"" + value + "\" cast to \"" + typeName + "\" type failure");
        }
        return (T) o;
    }

}
