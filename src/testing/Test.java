package testing;

public class Test {
	
	public static void main(String[] args) {
		A temp = new A();
		System.out.println(temp.b.a.name);
	}
	
	static class A {
		B b;
		String name;
		
		
		public A() {
			this.b = new B(this);
			this.name = "ASDASDASD";
		}
	}
	
	static class B {
		A a;
		
		public B(A a) {
			this.a = a;
		}
	}
}