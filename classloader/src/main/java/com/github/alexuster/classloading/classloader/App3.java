package com.github.alexuster.classloading.classloader;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.List;

public class App3 {

	void main() throws Exception {
		IO.println("start");
	
		var composite = new CompositeClassLoader();
		
		var pathA = Path.of("C:\\dev\\tryout\\java-classloader\\project-a\\target\\project-a-0.0.1-SNAPSHOT.jar");
		var urlA = pathA.toUri().toURL();
		var clA = new ProjectClassLoader(new URL[] {urlA});
		//var clA = new ProjectClassLoader(new URL[] {urlA}, composite);
		clA.composite = composite;
		
		var pathB = Path.of("C:\\dev\\tryout\\java-classloader\\project-b/target/project-b-0.0.1-SNAPSHOT.jar");
		var urlB = pathB.toUri().toURL();
		var clB = new ProjectClassLoader(new URL[] {urlB});
		//var clB = new ProjectClassLoader(new URL[] {urlB}, composite);
		clB.composite = composite;

		var cls = List.of(clA, clB);
		
		composite.cls = cls;
		
		var clazzA = composite.loadClass("com.github.alexuster.classloading.a.A");
		IO.println(clazzA.getName());
		var method = clazzA.getMethod("execute");
	    method.invoke(null);
	    
	    var clazzB = composite.loadClass("com.github.alexuster.classloading.b.B");
		IO.println(clazzB.getName());
		method = clazzB.getMethod("execute");
	    method.invoke(null);
	}
	
	public static class CompositeClassLoader extends ClassLoader {
		
		List<ProjectClassLoader> cls;
		
		
		@Override
		public Class<?> loadClass(String name) throws ClassNotFoundException {
			return loadClassFromApplication(name, this);
		}

		public Class<?> loadClassFromApplication(String name, ClassLoader origin) throws ClassNotFoundException {
			for (var cl : cls) {
				if (cl == origin) {
					continue;
				}
				try {
					return cl.loadlClassFromProject(name);
				} catch (ClassNotFoundException ex) {
					// silence
				}
			}
			return super.loadClass(name);
		}
	}
	
	public static class ProjectClassLoader extends URLClassLoader {
		
		CompositeClassLoader composite;
		
		public ProjectClassLoader(URL[] urls) {
			super(urls);
		}
		
		public ProjectClassLoader(URL[] urls, ClassLoader cl) {
			super(urls, cl);
		}
		
		@Override
		public Class<?> loadClass(String name) throws ClassNotFoundException {
			try {
				return composite.loadClassFromApplication(name, this);
			} catch (ClassNotFoundException ex) {
				return loadlClassFromProject(name);
			}
		}
			
		public Class<?> loadlClassFromProject(String name) throws ClassNotFoundException {
			return super.loadClass(name);
		}
	}
}
