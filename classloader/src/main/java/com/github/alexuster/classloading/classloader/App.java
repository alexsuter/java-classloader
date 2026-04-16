package com.github.alexuster.classloading.classloader;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;

public class App {

	void main() throws Exception {
		IO.println("start");
	
		var pathA = Path.of("/home/alex/git/java-classloader/project-a/target/project-a-0.0.1-SNAPSHOT.jar");
		var urlA = pathA.toUri().toURL();
		try (var cl = new ProjectClassLoader(new URL[] {urlA})) {
			var clazzA = cl.loadClass("com.github.alexuster.classloading.a.A");
			IO.println(clazzA.getName());
			var method = clazzA.getMethod("execute");
	        method.invoke(null);
		}
		
		var pathB = Path.of("/home/alex/git/java-classloader/project-b/target/project-b-0.0.1-SNAPSHOT.jar");
		var urlB = pathB.toUri().toURL();
		try (var cl = new ProjectClassLoader(new URL[] {urlB})) {
			var clazzB = cl.loadClass("com.github.alexuster.classloading.b.B");
			IO.println(clazzB.getName());
			// funktioniert!
			
			// aber das funktioniert dann natürlich nicht.
			//var method = clazzB.getMethod("execute");
	        //method.invoke(null);
		}
		
	}
	
	
	public static class ProjectClassLoader extends URLClassLoader {
		public ProjectClassLoader(URL[] urls) {
			super(urls);
		}
	}
}
