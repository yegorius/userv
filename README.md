# μServ
μServ = RestEasy + Undertow + Guice + Typesafe Config

```
public final class AppServer extends μServ {
	private AppServer() {
		application(MyApplication.class);
		pakcage("com.mycompany.app");
	}

	public static void main(String[] args) {
		final AppServer server = new AppServer();
		server.start();
		Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
	}
}
```
