package com.s2s.util;

/**
 * Created with IntelliJ IDEA.
 * User: dosapati
 * Date: 4/27/13
 * Time: 4:28 PM
 * To change this template use File | Settings | File Templates.
 */


/**
 * Created with IntelliJ IDEA.
 * User: dosapati
 * Date: 7/5/12
 * Time: 10:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class MemoryUtil {


    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }


    public static void printCurrentMemoryLeftOut(){
        System.gc();
        String memoryStr = "Max memory ->" + MemoryUtil.humanReadableByteCount(Runtime.getRuntime().maxMemory(), true) + " - Free memory left out is - >" + MemoryUtil.humanReadableByteCount((Runtime.getRuntime().freeMemory()), true);
        System.out.println(memoryStr);
        //ApplicationLogger.log(memoryStr);

    }
}

