// 예제 13.3 LogEventBroadcaster

public class LogEventBroadcaster {
    private final EventLoopGroup group;
    private final Bootstrap bootstrap;
    private final File file;

    public LogEventBroadcaster(InetSocketAddress address, File file) {
	group = new NioEventLoopGroup();
	bootstrap = new Bootstrap();
	bootstrap.group(group).channel(NioDatagramChannel.class)
	    .option(ChannelOption.SO_BROADCAST, true)
	    .handler(new LogEventEncoder(address));
	this.file = file;
    }

    public void run() throws Exception {
	Channel ch = bootstrap.bind(0).sync().channel();
	long pointer = 0;
	for (;;) {
	    long len = file.length();
	    if (len < pointer) {
		// 파일이 재설정됨
		pointer = len;
	    } else if (len > pointer) {
		// 콘텐츠가 주가됨
		RandomAccessFile raf = new RandomAccessFile(file, "r");
		raf.seek(pointer);
		String line;
		while ((line = raf.readLine()) != null) {
		    ch.writeAndFlush(new LogEvent(null, -1,
		    file.getAbsolutePath(), line));
		}
		pointer = raf.getFilePointer();
		raf.close();
	    }
	    try {
		Thread.sleep(1000);
	    } catch (InterruptedException e) {
		Thread.interrupted();
		break;
	    }
	}
    }

    public void stop() {
	group.shutdownGracefully();
    }

    public static void main(String[] args) throws Exception {
	if (args.length != 2) {
	    throw new IllegalArgumentException();
	}
	LogEventBroadcaster broadcaster = new LogEventBroadcaster(
	    new InetSocketAddress("255.255.255.255",
		Integer.parseInt(args[0])), new File(args[1]));
	try {
	    broadcaster.run();
	}
	finally {
	    broadcaster.stop();
	}
    }
}
