package com.github.alexuster.classloading.b;

import com.github.alexuster.classloading.a.A;

public class B {

	static {
		A.execute();
	}
	
	public static void execute() {
		IO.println("Hello from B");
		IO.println("and execute A");
		A.execute();
	}
}
