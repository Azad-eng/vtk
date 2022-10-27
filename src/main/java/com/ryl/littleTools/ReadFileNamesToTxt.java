package com.ryl.littleTools;

import java.io.File;

/**
 * @author: EFL-ryl
 */
public class ReadFileNamesToTxt {
    public static void main(String[] args) {
        File file = new File("D:\\Toolkits\\VTK\\VTK-9.2.2\\build\\lib");
        for (File listFile : file.listFiles()) {
            System.out.println(listFile.getName());
        }
    }
}
