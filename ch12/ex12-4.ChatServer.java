// 예제 12.4 서버 부트스트랩

public class ChatServer {
    private final ChannelGroup channelGroup =
	new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
    private final EventLoopGroup group = new NioEventLoopGroup();
    private Channel channel;

    public ChannelFuture start(InetSocketAddress address) {
	ServerBootstrap bootstrap = new ServerBootstrap();
	bootstrap.group(group)
	    .channel(NioServerSocketchannel.class)
	    .childHandler(createInitializer(channelGroup));
	ChannelFuture future = bootstrap.bind(address);
	future.syncUninterruptibly();
	channel = future.channel();
	return future;
    }

    protected ChannelInitializer<Channel> createInitializer(
	ChannelGroup group) {
	return new ChatServerInitializer(group);
    }

    public void destroy() {
	it (channel != null) {
	    channel.close();
	}
	channelGroup.close();
	group.shutdownGracefully();
    }

    public static void main(String[] args) throws Exception {
	if (args.length != 1) {
	    System.err.println("Please give port as argument");
	    System.exit(1);
	}
	int port = Integer.parseInt(args[0]);
	final ChatServer endpoint = new ChatServer();
	ChannelFuture future = endpoint.start(
	    new InetSocketAddress(port));
	Runtime.getRuntime().addShutdownHook(new Thread() {
	    @Override
	    public void run() {
		endpoint.destroy();
	    }
	});
	future.channel().closeFuture().syncUninterruptibly();
    }
}
