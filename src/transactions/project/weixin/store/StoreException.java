package transactions.project.weixin.store;

@SuppressWarnings("serial")
public class StoreException extends Throwable {
	
	public StoreException() {
		super();
	}

	public StoreException(String msg) {
		super(msg);
	}

	public StoreException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public StoreException(Throwable cause) {
		super(cause);
	}
}