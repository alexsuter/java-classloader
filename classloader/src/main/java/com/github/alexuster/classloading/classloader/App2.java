package com.github.alexuster.classloading.classloader;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.List;

public class App2 {

	void main() throws Exception {
		IO.println("start");
	
		var pathA = Path.of("/home/alex/git/java-classloader/project-a/target/project-a-0.0.1-SNAPSHOT.jar");
		var urlA = pathA.toUri().toURL();
		var clA = new ProjectClassLoader(new URL[] {urlA});
		
		var pathB = Path.of("/home/alex/git/java-classloader/project-b/target/project-b-0.0.1-SNAPSHOT.jar");
		var urlB = pathB.toUri().toURL();
		var clB = new ProjectClassLoader(new URL[] {urlB});

		var cls = List.of(clA, clB);
		var composite = new CompositeClassLoader(cls);
		
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
		
		private final List<ProjectClassLoader> cls;
		
		public CompositeClassLoader(List<ProjectClassLoader> cls) {
			this.cls = cls;
		}
		
		@Override
		public Class<?> loadClass(String name) throws ClassNotFoundException {
		  for (var cl : cls) {
			   try {
				 return  cl.loadClass(name);
			   } catch (ClassNotFoundException ex) {
				  // silence
			   }
		  }
		  return super.loadClass(name);
		}
		
		@Override
		protected Class<?> findClass(String name) throws ClassNotFoundException {
			// TODO Auto-generated method stub
			  for (var cl : cls) {
				   try {
					 return  cl.findClass(name);
				   } catch (ClassNotFoundException ex) {
					  // silence
				   }
			  }
			  return super.findClass(name);
		}
	}
	
	
	public static class ProjectClassLoader extends URLClassLoader {
		public ProjectClassLoader(URL[] urls) {
			super(urls);
		}
		@Override
		public Class<?> findClass(String name) throws ClassNotFoundException {
			// TODO Auto-generated method stub
			return super.findClass(name);
		}
	}
}
