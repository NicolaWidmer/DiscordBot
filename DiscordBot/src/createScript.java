import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Scanner;

public class createScript {

	public static void main(String[] args) throws IOException {
		String script="scripts/discordscript.sh/";
		File outfile = new File(script);
		try {
			outfile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		OutputStream stream =new FileOutputStream(script);
		String str = "libs";
		File file = new File(str);
	 
		StringBuilder sb = new StringBuilder();
	 
		File[] arr = file.listFiles();
		for(File f: arr){
			if(f.getName().endsWith(".jar")){
				sb.append(f.getAbsolutePath() + " ");
			}
		}
		
	 
		String libs = sb.toString();
		libs = libs.substring(0, libs.length()-1);
		
		sb=new StringBuilder();
		str= "src";
		file =new File(str);
		rec(sb,file);
		
		String src= sb.toString();
		src = src.substring(0, src.length()-1);
		
		stream.write("#!/bin/bash\n".getBytes());
		stream.write("rm -r ../build\n".getBytes());
		stream.write("javac -d ../build -cp ".getBytes());
		stream.write(libs.getBytes());
		stream.write(src.getBytes());
		stream.write("\n".getBytes());
		libs=libs.replace(" ",":");
		stream.write("java -cp ../build:".getBytes());
		stream.write(libs.getBytes());
		stream.write(" Main".getBytes());

	}
	
	public static void rec(StringBuilder sb, File file) {
		
		File[] arr = file.listFiles();
		for(File f: arr){
			if(f.getName().contains("createScript")||f.getName().equals("build"))continue;
			if(f.getName().endsWith(".java")){
				sb.append(f.getAbsolutePath() + " ");
			}
			else {
				rec(sb,f);
			}
		}
	}

}
