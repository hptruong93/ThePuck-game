package testing;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class Test {

	private static ArrayList<Integer> numbers;
	private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private static final Lock read = lock.readLock();
	private static final Lock write = lock.writeLock();
	
	static {
		numbers = new ArrayList<Integer>();
		for (int i = 0; i < 100; i++) {
			numbers.add(i);
		}
	}

	private static void readAll(String name) {
		read.lock();
		try {
			for (Integer a : numbers) {
//				if (!name.equals("sub")) {
//					readAll("sub");
//				}
				System.out.println("Name is " + name +  " --> " + a);
			}
		} finally {
			read.unlock();
		}
	}
	
	private static void writeExtra() {
		write.lock();
		try {
			for (int i = 100; i < 200; i++) {
				numbers.add(i);
			}
		} finally {
			write.unlock();
		}
	}
	
	public static void main(String[] args) {
		Reader reader = new Reader("First");
		Reader reader2 = new Reader("Second");
		Writer write = new Writer();
		
		reader.start();
		write.start();
	}
	
	private static class Reader extends Thread {
		
		private final String name;
		
		private Reader(String name) {
			this.name = name;
		}
		
		@Override
		public void run() {
			readAll(name);
		}
	}
	
	private static class Writer extends Thread {
		@Override
		public void run() {
			writeExtra();
		}
	}
}