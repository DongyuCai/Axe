package org.jw.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 类操作工具类
 * Created by CaiDongYu on 2016/4/8.
 */
public final class ClassUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassUtil.class);


    /**
     * 获取类加载器
     */
    public static ClassLoader getClassLoader(){
        return Thread.currentThread().getContextClassLoader();
    }

    private ClassUtil() {}
    
    /**
     * 加载类
     */
    public static Class<?> loadClass(String className,boolean isInitialized){
        Class<?> cls;
        try {
            cls = Class.forName(className, isInitialized, getClassLoader());
        } catch (ClassNotFoundException e) {
            LOGGER.error("load class failure",e);
            throw new RuntimeException(e);
        } catch (Error e){
            LOGGER.error("load class failure",e);
            throw new RuntimeException(e);
        }
        return cls;
    }

    /**
     * 获取指定包名下，所有的类
     */
    public static Set<Class<?>> getClassSet(String packageName){
        Set<Class<?>> classSet = new HashSet<>();
        try {
            if(packageName == null) return classSet;

            Enumeration<URL> urls = getClassLoader().getResources(packageName.replace(".","/"));
            while(urls.hasMoreElements()){
                URL url = urls.nextElement();
                if(url != null){
                    String protocol = url.getProtocol();
                    if(protocol.equals("file")){
                        String packagePath = url.getPath().replaceAll("%20"," ");
                        addClass(classSet, packagePath, packageName);
                    } else if(protocol.equals("jar")){
                        JarURLConnection jarURLConnection = (JarURLConnection)url.openConnection();
                        if(jarURLConnection != null){
                            JarFile jarFile = jarURLConnection.getJarFile();
                            if(jarFile != null){
                                String packagePath = packageName.replace(".","/");
                                Enumeration<JarEntry> jarEntries = jarFile.entries();
                                while(jarEntries.hasMoreElements()){
                                    JarEntry jarEntry = jarEntries.nextElement();
                                    String jarEntryName = jarEntry.getName();
                                    //此处注意，jar包entries是所有jar包中资源，所以需要过滤掉不需要的
                                    //TODO:另外，这个地方没有注意到spi问题，有些spi的资源，可能还是会报ClassDefNotFound异常
                                    if(!jarEntryName.startsWith(packagePath)){
                                        continue;
                                    }
                                    if(jarEntryName.endsWith(".class")){
                                        String className = jarEntryName.substring(0,jarEntryName.lastIndexOf(".")).replace("/",".");
                                        doAddClass(classSet, className);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("get class set failure",e);
            throw new RuntimeException(e);
        }
        return classSet;
    }

    private static void addClass(Set<Class<?>> classSet, String packagePath, String packageName) {
        File[] files = new File(packagePath).listFiles(file -> (file.isFile() && file.getName().endsWith(".class")) || file.isDirectory());
        for(File file : files){
            String fileName = file.getName();
            if(file.isFile()){
                String className = fileName.substring(0,fileName.lastIndexOf("."));
                if(StringUtil.isNotEmpty(packagePath)){
                    className = packageName+"."+className;
                }
                doAddClass(classSet, className);
            }else{
                String subPackagePath = fileName;
                if(StringUtil.isNotEmpty(packagePath)){
                    subPackagePath = packagePath + "/" + subPackagePath;
                }
                String subPackageName = fileName;
                if(StringUtil.isNotEmpty(packageName)){
                    subPackageName = packageName + "." + subPackageName;
                }
                addClass(classSet, subPackagePath, subPackageName);
            }

        }
    }

    private static void doAddClass(Set<Class<?>> classSet, String className) {
        Class<?> cls = loadClass(className, false);
        classSet.add(cls);
    }

}

