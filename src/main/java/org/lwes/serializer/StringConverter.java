package org.lwes.serializer;

public class StringConverter {

    public static <T> String[] strArray(T[] arr){
        String[] sArr = new String[arr.length];
        for(int i=0; i<arr.length; i++)
            sArr[i] = arr[i]==null ? null : arr[i].toString();
        
        return sArr;
    }
    
    public static String[] strArray(boolean[] arr){
        String[] sArr = new String[arr.length];
        for(int i=0; i<arr.length; i++)
            sArr[i] = String.valueOf(arr[i]);
        
        return sArr;
    }
    
    public static String[] strArray(byte[] arr){
        String[] sArr = new String[arr.length];
        for(int i=0; i<arr.length; i++)
            sArr[i] = String.valueOf(arr[i]);
        
        return sArr;
    }
    
    public static String[] strArray(short[] arr){
        String[] sArr = new String[arr.length];
        for(int i=0; i<arr.length; i++)
            sArr[i] = String.valueOf(arr[i]);
        
        return sArr;
    }
    
    public static String[] strArray(int[] arr){
        String[] sArr = new String[arr.length];
        for(int i=0; i<arr.length; i++)
            sArr[i] = String.valueOf(arr[i]);
        
        return sArr;
    }
    
    public static String[] strArray(long[] arr){
        String[] sArr = new String[arr.length];
        for(int i=0; i<arr.length; i++)
            sArr[i] = String.valueOf(arr[i]);
        
        return sArr;
    }
    
    public static String[] strArray(float[] arr){
        String[] sArr = new String[arr.length];
        for(int i=0; i<arr.length; i++)
            sArr[i] = String.valueOf(arr[i]);
        
        return sArr;
    }
    
    public static String[] strArray(double[] arr){
        String[] sArr = new String[arr.length];
        for(int i=0; i<arr.length; i++)
            sArr[i] = String.valueOf(arr[i]);

        return sArr;
    }
    
}
