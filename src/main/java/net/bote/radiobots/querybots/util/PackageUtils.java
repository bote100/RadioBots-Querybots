package net.bote.radiobots.querybots.util;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.util.function.Consumer;

/**
 * @author Elias Arndt | bote100
 * Created on 23.07.2019
 */

public class PackageUtils {

    public static <T> void performForClasses(ClassLoader classLoader, Class<T> superClass, Consumer<Class<? extends T>> execute, String packageName, String... subPackages) {
        String[] var5 = subPackages;
        int var6 = subPackages.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            String subPackage = var5[var7];
            performForClasses(classLoader, superClass, execute, packageName + "." + subPackage);
        }

    }

    public static <T> void performForClasses(Class<T> superClass, Consumer<Class<? extends T>> execute, String packageName) {
        (new Reflections(new Object[]{packageName, new SubTypesScanner(true)})).getSubTypesOf(superClass).forEach(execute::accept);
    }
}
