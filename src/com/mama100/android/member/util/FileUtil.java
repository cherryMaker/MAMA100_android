package com.mama100.android.member.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

	// 新建一个文件夹
	public static void newFolder(String folderPath) {
		try {
			String filePath = folderPath;
			File myFilePath = new File(filePath);
			if (!myFilePath.exists()) {
				myFilePath.mkdir();
			}
		} catch (Exception e) {
			System.out.println("新建文件夹操作出错");
			LogUtils.loge("FileUtil", "新建文件夹操作出错");
			e.printStackTrace();
			LogUtils.loge("FileUtil", LogUtils.getStackTrace(e));
		}
	}

	// 删除文件夹
	public static void delFolder(String folderPath) {
		try {
			String filePath = folderPath;
			File delPath = new File(filePath);
			delPath.delete();
		} catch (Exception e) {
			System.out.println("删除文件夹操作出错");
			LogUtils.loge("FileUtil", "删除文件夹操作出错");
			e.printStackTrace();
			LogUtils.loge("FileUtil", LogUtils.getStackTrace(e));
		}
	}

	// 新建文件
	public static void createFile(String fileName) {
		try {
			String myFileName = fileName;
			File file = new File(myFileName);
			if (!file.exists()) {
				file.createNewFile();
			}
		} catch (Exception e) {
			System.out.println("新建文件操作出错");
			LogUtils.loge("FileUtil", "新建文件操作出错");
			e.printStackTrace();
			LogUtils.loge("FileUtil", LogUtils.getStackTrace(e));
		}
	}
	// 检查文件存在性
	public static boolean isFileExist(String fileName) {
			String myFileName = fileName;
			File file = new File(myFileName);
			if (!file.exists()) {
				return false;
			}
			return true;
	}

	// 删除文件
	public static void delFile(String fileName) {
		try {
			String myFileName = fileName;
			File file = new File(myFileName);
			file.delete();
		} catch (Exception e) {
			System.out.println("删除文件操作出错");
			LogUtils.loge("FileUtil", "删除文件操作出错");
			e.printStackTrace();
			LogUtils.loge("FileUtil", LogUtils.getStackTrace(e));
		}
	}
	
	public static void deleteAllFileInFolder(String foldername) {
		LogUtils.logi("deleteAllFileInFolder", "1-" + System.currentTimeMillis());
		File f = new File(foldername);
		if (f.exists()) {
			File[] fl = f.listFiles();
			for (int i = 0; i < fl.length; i++) {
				if (fl[i].delete()) {
				} else {
				}
			}
		} else {

		}
		LogUtils.logi("deleteAllFileInFolder", "2-" + System.currentTimeMillis());
	}
	public static void deleteAllFileInFolder(File file) {
		LogUtils.logi("deleteAllFileInFolder", "1-" + System.currentTimeMillis());
		if (file!=null&&file.exists()) {
			File[] fl = file.listFiles();
			for (int i = 0; i < fl.length; i++) {
				if (fl[i].delete()) {
				} else {
				}
			}
		} else {
			
		}
		LogUtils.logi("deleteAllFileInFolder", "2-" + System.currentTimeMillis());
	}
	
	
	/**********************************************************************
	 *  复制文件
	 **********************************************************************/
	
	/**
	   * 复制文件(以超快的速度复制文件)
	   * @author:suhj 2006-8-31
	   * @param srcFile 源文件File
	   * @param destDir 目标目录File
	   * @param newFileName 新文件名
	   * @return 实际复制的字节数，如果文件、目录不存在、文件为null或者发生IO异常，返回-1
	   * @return 复制成功为true，如果文件、目录不存在、文件为null或者发生IO异常，返回false
	   */
	public static boolean copyFile2(File srcFile,File destDir,String newFileName){
	   long copySizes = 0;
	   if(!srcFile.exists()){
	    System.out.println("源文件不存在");
	    copySizes = -1;
	    return false;
	   }
	   else if(!destDir.exists()){
	    System.out.println("目标目录不存在");
	    copySizes = -1;
	    return false;
	   }
	   else if(newFileName == null){
	    System.out.println("文件名为null");
	    copySizes = -1;
	    return false;
	   }
	   else{
	    try {
	     FileChannel fcin = new FileInputStream(srcFile).getChannel();
	     FileChannel fcout = new FileOutputStream(
	           new File(destDir,newFileName)).getChannel();
	     ByteBuffer buff = ByteBuffer.allocate(1024);
	     int b = 0 ,i = 0;
//	     long t1 = System.currentTimeMillis();
	     /*while(fcin.read(buff) != -1){
	      buff.flip();
	      fcout.write(buff);
	      buff.clear();
	      i++;
	     }*/
	     long size = fcin.size();
	     fcin.transferTo(0,fcin.size(),fcout);
	     //fcout.transferFrom(fcin,0,fcin.size());
	     //一定要分清哪个文件有数据，那个文件没有数据，数据只能从有数据的流向
	     //没有数据的文件
//	     long t2 = System.currentTimeMillis();
	     fcin.close();
	     fcout.close();
	     copySizes = size;
//	     long t = t2-t1;
//	     System.out.println("复制了" + i + "个字节\n" + "时间" + t);
//	     System.out.println("复制了" + size + "个字节\n" + "时间" + t);
	    } catch (FileNotFoundException e) {    
	     e.printStackTrace();
	     return false;
	    } catch (IOException e) {
	     e.printStackTrace();
	     return false;
	    }
	   }
	   return true;
	}
	
	
	/******************************************************
	 *  2012-08-25
	 ******************************************************/
	
	// 验证字符串是否为正确路径名的正则表达式
		private static String matches = "[A-Za-z]:\\\\[^:?\"><*]*";
		// 通过 sPath.matches(matches) 方法的返回值判断是否正确
		// sPath 为路径字符串
		
	    /**
	     *  根据路径删除指定的目录或文件，无论存在与否
	     *@param sPath  要删除的目录或文件
	     *@return 删除成功返回 true，否则返回 false。
	     */
	    public static boolean DeleteFolder(String sPath) {
	       boolean flag = false;
	        File file = new File(sPath);
	        // 判断目录或文件是否存在
	        if (!file.exists()) {  // 不存在返回 false
	        	LogUtils.logd("FileUtil", "清理SD卡临时文件夹 -- 文件不存在 "
						+ ".....................! \n"+ " current time is: "
						+ new Timestamp(System.currentTimeMillis()).toString());
	            return flag;
	        } else {
	            // 判断是否为文件
	            if (file.isFile()) {  // 为文件时调用删除文件方法
	                return deleteFile(sPath);
	            } else {  // 为目录时调用删除目录方法
	                boolean bool =  deleteDirectory(sPath);
	                LogUtils.logd("FileUtil", "清理SD卡临时文件夹 --删除目录"+sPath+"\n" + "结果-"+((bool)?"成功":"失败")
	    	        		+ ".....................! \n"+ " current time is: "
	    	        		+ new Timestamp(System.currentTimeMillis()).toString());
	                return bool;
	            }
	        }
	    }
	    
	    
		
	    /**
	     * 删除单个文件
	     * @param   sPath    被删除文件的文件名
	     * @return 单个文件删除成功返回true，否则返回false
	     */
	    public static boolean deleteFile(String sPath) {
	      boolean  flag = false;
	        File file = new File(sPath);
	        // 路径为文件且不为空则进行删除
	        if (file.isFile() && file.exists()) {
	            file.delete();
	            flag = true;
	        }
	        LogUtils.logd("FileUtil", "清理SD卡临时文件夹 --"+sPath+"\n" + "结果-"+((flag)?"成功":"失败")
					+ ".....................! \n"+ " current time is: "
					+ new Timestamp(System.currentTimeMillis()).toString());
	        return flag;
	        
	    }
		
		
	    /**
	     * 删除目录（文件夹）以及目录下的文件
	     * @param   sPath 被删除目录的文件路径
	     * @return  目录删除成功返回true，否则返回false
	     */
	    public static boolean deleteDirectory(String sPath) {
	        //如果sPath不以文件分隔符结尾，自动添加文件分隔符
	        if (!sPath.endsWith(File.separator)) {
	            sPath = sPath + File.separator;
	        }
	        
	        
	        File dirFile = new File(sPath);
	        //如果dir对应的文件不存在，或者不是一个目录，则退出
	        if (!dirFile.exists() || !dirFile.isDirectory()) {
	            return false;
	        }
	        
	        boolean flag = true;
	        //删除文件夹下的所有文件(包括子目录)
	        File[] files = dirFile.listFiles();
	        for (int i = 0; i < files.length; i++) {
	            //删除子文件
	            if (files[i].isFile()) {
	                flag = deleteFile(files[i].getAbsolutePath());
	                if (!flag) break;
	            } //删除子目录
	            else {
	                flag = deleteDirectory(files[i].getAbsolutePath());
	                if (!flag) break;
	            }
	        }
	        if (!flag) return false;
	        //删除当前目录
	        if (dirFile.delete()) {
	            return true;
	        } else {
	            return false;
	        }
	    }
	    
	    /***************************************************************************************************
	     * 文件读写
	     ***************************************************************************************************/
	    
	    /**  
	     * 将字节流转换成字符串返回  
	     *   
	     * @param is  
	     *            输入流  
	     * @return 字符串  
	     */  
	    public static String readFileByLines(InputStream is) {   
	        BufferedReader reader = null;   
	        StringBuffer sb = new StringBuffer();   
	        try {   
	            reader = new BufferedReader(new InputStreamReader(is));   
	            String tempString = null;   
	            while ((tempString = reader.readLine()) != null) {   
	                sb.append(tempString + "\n");   
	            }   
	            reader.close();   
	        } catch (IOException e) {   
	            e.printStackTrace();   
	        } finally {   
	            if (reader != null) {   
	                try {   
	                    reader.close();   
	                } catch (IOException e1) {   
	                }   
	            }   
	        }   
	        return sb.toString();   
	    }   
	  
	    /**  
	     * 将文件一行一行的读成List返回  
	     *   
	     * @param file 需要读取的文件  
	     * @return 文件的一行就是一个List的Item的返回  
	     */  
	    public static List<String> readFileToList(File file) {   
	        BufferedReader reader = null;   
	        List<String> list = new ArrayList<String>();   
	        try {   
	            reader = new BufferedReader(new FileReader(file));   
	            String tempString = null;   
	            while ((tempString = reader.readLine()) != null) {   
	                list.add(tempString);   
	            }   
	            reader.close();   
	        } catch (IOException e) {   
	            e.printStackTrace();   
	        } finally {   
	            if (reader != null) {   
	                try {   
	                    reader.close();   
	                } catch (IOException e1) {   
	                }   
	            }   
	        }   
	        return list;   
	    }   
	  
	    /**  
	     * 将文件按照一定的编码方式一行一行的读成List返回  
	     *   
	     * @param file  
	     *            需要读取的文件  
	     * @param encodType  
	     *            字符编码  
	     * @return 文件的一行就是一个List的Item的返回  
	     */  
	    public static List<String> readFileToList(File file, String encodType) {   
	        BufferedReader reader = null;   
	        List<String> list = new ArrayList<String>();   
	        try {   
	            reader = new BufferedReader(new InputStreamReader(   
	                    new FileInputStream(file), encodType));   
	            String tempString = null;   
	            while ((tempString = reader.readLine()) != null) {   
	                if (!(tempString.charAt(0) >= 'a' && tempString.charAt(0) <= 'z'))   
	                    tempString = tempString.substring(1);   
	                list.add(tempString);   
	            }   
	            reader.close();   
	        } catch (IOException e) {   
	            e.printStackTrace();   
	        } finally {   
	            if (reader != null) {   
	                try {   
	                    reader.close();   
	                } catch (IOException e1) {   
	                }   
	            }   
	        }   
	        return list;   
	    }   
	  
	    /**  
	     * 将指定的字符串内容以指定的方式写入到指定的文件中  
	     *   
	     * @param file  
	     *            需要写人的文件  
	     * @param content  
	     *            需要写入的内容  
	     * @param flag  
	     *            是否追加写入  
	     */  
	    public static void writeFile(File file, String content, Boolean flag) {   
	        try {   
	            if (!file.exists())   
	                file.createNewFile();   
	            FileWriter writer = new FileWriter(file, flag);   
	            writer.write(content);   
	            writer.close();   
	        } catch (IOException e) {   
	            e.printStackTrace();   
	        }   
	    }   
	  
	    /**  
	     * 将指定的字符串内容以指定的方式及编码写入到指定的文件中  
	     *   
	     * @param file  
	     *            需要写人的文件  
	     * @param content  
	     *            需要写入的内容  
	     * @param flag  
	     *            是否追加写入  
	     * @param encodType  
	     *            文件编码  
	     */  
	    public static void writeFile(File file, String content, Boolean flag,   
	            String encodType) {
	        try {   
	        	if (!file.exists())   
	        		file.createNewFile(); 
	            FileOutputStream writerStream = new FileOutputStream(file, flag);   
	            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(   
	                    writerStream, encodType));   
	            writer.write(content);   
	            writer.close();   
	        } catch (Exception e) {   
	            e.printStackTrace();   
	        }   
	    }   
	  
	    /**  
	     * 拷贝文件夹  
	     *   
	     * @param oldPath  
	     *            源目录  
	     * @param newPath  
	     *            目标目录  
	     */  
	    public static void copyFolder(String oldPath, String newPath) {   
	        try {   
	            (new File(newPath)).mkdirs();   
	            File a = new File(oldPath);   
	            String[] file = a.list();   
	            File temp = null;   
	            for (int i = 0; i < file.length; i++) {   
	                if (oldPath.endsWith(File.separator)) {   
	                    temp = new File(oldPath + file[i]);   
	                } else {   
	                    temp = new File(oldPath + File.separator + file[i]);   
	                }   
	                if (temp.isFile()) {   
	                    FileInputStream input = new FileInputStream(temp);   
	                    FileOutputStream output = new FileOutputStream(newPath   
	                            + "/" + (temp.getName()).toString());   
	                    byte[] b = new byte[1024 * 5];   
	                    int len;   
	                    while ((len = input.read(b)) != -1) {   
	                        output.write(b, 0, len);   
	                    }   
	                    output.flush();   
	                    output.close();   
	                    input.close();   
	                }   
	                if (temp.isDirectory()) {   
	                    copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);   
	                }   
	            }   
	        } catch (Exception e) {   
	            e.printStackTrace();   
	        }   
	    }   
	  
	    /**  
	     * 将文件重命名  
	     *   
	     * @param oldName  
	     *            源文件名  
	     * @param newName  
	     *            新文件名  
	     */  
	    public static void reName(String oldName, String newName) {   
	        File oldF = new File(oldName);   
	        File newF = new File(newName);   
	        oldF.renameTo(newF);   
	    }   
	  
	    /**  
	     * 将一个文件列表文件中所有文件拷贝到指定目录中  
	     *   
	     * @param listFile  
	     *            包含需要拷贝的文件的列表的文件，每个文件写在一行  
	     * @param targetFloder  
	     *            目标目录  
	     */  
	    public static void copyFilesFromList(String listFile, String targetFloder) {   
	        BufferedReader reader = null;   
	        try {   
	            reader = new BufferedReader(new FileReader(listFile));   
	            String tempString = null;   
	            while ((tempString = reader.readLine()) != null) {   
	                copyFile(tempString, targetFloder);   
	            }   
	            reader.close();   
	        } catch (IOException e) {   
	            e.printStackTrace();   
	        } finally {   
	            if (reader != null) {   
	                try {   
	                    reader.close();   
	                } catch (IOException e1) {   
	                }   
	            }   
	        }   
	    }   
	  
	    /**  
	     * 拷贝文件  
	     *   
	     * @param oldPath  
	     *            源文件  
	     * @param newPath  
	     *            目标文件  
	     */  
	    public static void copyFile(String oldPath, String newPath) {   
	        try {   
	            File temp = new File(oldPath);   
	            FileInputStream input = new FileInputStream(temp);   
	            FileOutputStream output = new FileOutputStream(newPath + "/"  
	                    + (temp.getName()).toString());   
	            byte[] b = new byte[1024 * 5];   
	            int len;   
	            while ((len = input.read(b)) != -1) {   
	                output.write(b, 0, len);   
	            }   
	            output.flush();   
	            output.close();   
	            input.close();   
	        } catch (Exception e) {   
	            e.printStackTrace();   
	        }   
	    }   
	  
	    /**  
	     * 删除文件列表  
	     *   
	     * @param files  
	     *            需要删除的文件/文件夹列表  
	     * @return 删除成功true，否则返回false  
	     */  
	    public static boolean deleteFiles(List<String> files) {   
	        boolean flag = true;   
	        for (String file : files) {   
	            flag = delete(file);   
	            if (!flag)   
	                break;   
	        }   
	        return flag;   
	    }   
	  
	    /**  
	     * 删除文件或文件夹  
	     *   
	     * @param fileName  
	     *            要删除的文件名  
	     * @return 删除成功返回true，否则返回false  
	     */  
	    public static boolean delete(String fileName) {   
	        File file = new File(fileName);   
	        if (!file.exists()) {   
	            return false;   
	        } else {   
	            if (file.isFile())   
	                return deleteFile(fileName);   
	            else  
	                return deleteDirectory(fileName);   
	        }   
	    }   
	  

}
