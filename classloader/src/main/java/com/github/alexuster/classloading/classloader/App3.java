package com.github.alexuster.classloading.classloader;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.List;

public class App3 {

	void main() throws Exception {
		IO.println("start");
	
		var composite = new CompositeClassLoader();
		
		var pathA = Path.of("/home/alex/git/java-classloader/project-a/target/project-a-0.0.1-SNAPSHOT.jar");
		var urlA = pathA.toUri().toURL();
		var clA = new ProjectClassLoader(new URL[] {urlA});
		//var clA = new ProjectClassLoader(new URL[] {urlA}, composite);
		clA.composite = composite;
		
		var pathB = Path.of("/home/alex/git/java-classloader/project-b/target/project-b-0.0.1-SNAPSHOT.jar");
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
		  return loadClass(name, this);
		}
		
		public Class<?> loadClass(String name, ClassLoader origin) throws ClassNotFoundException {
			  for (var cl : cls) {
				   try {
					 return  cl.loadClass(name, origin);
				   } catch (ClassNotFoundException ex) {
					  // silence
				   }
			  }
			  return super.loadClass(name);
			}
		
		@Override
		protected Class<?> findClass(String name) throws ClassNotFoundException {
			return findClass(name, this);
		}
		
		protected Class<?> findClass(String name, ClassLoader origin) throws ClassNotFoundException {
			// TODO Auto-generated method stub
			  for (var cl : cls) {
				   try {
					 return  cl.findClass(name, origin);
				   } catch (ClassNotFoundException ex) {
					  // silence
				   }
			  }
			  return super.findClass(name);
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
			return loadClass(name, this);
		}
		
		public Class<?> loadClass(String name, ClassLoader cl) throws ClassNotFoundException {
			
			try {
				return super.loadClass(name);
			} catch (ClassNotFoundException ex) {
				if (cl instanceof ProjectClassLoader pcl) {
					return composite.loadClass(name);
				}
				throw ex;
			}
		}
		
		
		@Override
		public Class<?> findClass(String name) throws ClassNotFoundException {
			return findClass(name, this);
		}
		
		public Class<?> findClass(String name, ClassLoader cl) throws ClassNotFoundException {
			// PL.findClass(ClassLoader origin) searches class inside its repos. If not found calls Composite.findClass(origin) if origin is not composite
			
			try {
				return super.findClass(name);
			} catch (ClassNotFoundException ex) {
				if (cl instanceof ProjectClassLoader pcl) {
					return composite.findClass(name);
				}
				throw ex;
			}
		}
	}
}
