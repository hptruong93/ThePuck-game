package features;

public interface ReactiveRunnable {
	public abstract void start();
	public abstract void stop();
	public abstract boolean isStopped();
}
