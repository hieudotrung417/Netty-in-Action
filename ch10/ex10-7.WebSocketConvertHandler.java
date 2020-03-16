// 예제 10.7 MessageToMessageCodec을 이용하는 방법

public class WebSocketConvertHandler extends
    MessageToMessageCodec<WebSocketFrame,
    WebSocketConvertHandler.MyWEbSocketFrame> {
    @Override
    protected void encode(ChannelHandlerContext ctx,
	WebSocketConvertHandler.MyWebSocketFrame msg,
	List<Object> out) throws Exception {
	ByteBuf payload = msg.getData().duplicate().retain();
	switch (msg.getType()) {
	    case BINARY:
		out.add(new BinaryWebSocketFrame(payload));
		break;
	    case TEXT:
		out.add(new TextWebSocketFrame(payload));
		break;
	    case CLOSE:
		out.add(new CloseWebSocketFrame(true, 0, payload));
		break;
	    case CONTINUATION:
		out.add(new ContinuationWebSocketFrame(payload));
		break;
	    case PONG:
		out.add(new PongWebSocketFrame(payload));
		break;
	    case PING:
		out.add(new PingWebSocketFrame(payload));
		break;
	    default:
		throw new IllegalStateException(
		    "Unsupported websocket msg " + msg);
	}
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, WebSocketFrame msg,
	List<Object> out) throws Exception {
	ByteBuf payload = msg.getData().duplicate().retain();
	if (msg instanceof BinaryWebSocketFrame) {
	    out.add(new MyWebSocketFrame(
		MyWebSocketFrame.FrameType.BINARY, payload));
	} else
	if (msg instanceof CloseWebSocketFrame) {
	    out.add(new MyWebSocketFrame(
		MyWebSocketFrame.FrameType.CLOSE, payload));
	} else
	if (msg instanceof PingWebSocketFrame) {
	    out.add(new MyWebSocketFrame(
		MyWebSocketFrame.FrameType.PING, payload));
	} else
	if (msg instanceof PongWebSocketFrame) {
	    out.add(new MyWebSocketFrame(
		MyWebSocketFrame.FrameType.PONG, payload));
	} else
	if (msg instanceof TextWebSocketFrame) {
	    out.add(new MyWebSocketFrame(
		MyWebSocketFrame.FrameType.TEXT, payload));
	} else
	if (msg instanceof ContinuationWebSocketFrame) {
	    out.add(new MyWebSocketFrame(
		MyWebSocketFrame.FrameType.CONTINUATION, payload));
	} else
        {
	    throw new IllegalStateException(
		"Unsupported websocket msg " + msg);
	}
    }

    public static final class MyWebSocketFrame {
	public enum FrameType {
	    BINARY,
	    CLOSE,
	    PING,
	    PONG,
	    TEXT,
	    CONTINUATION
	}
	private final FrameType type;
	private final ByteBuf data;

	public WebSocketFrame(FrameType type ByteBuf data) {
	    this.type = type;
	    this.data = data;
	}
	public FrameType getType() {
	    return type;
	}
	public ByteBuf getData() {
	    return data;
	}
    }
}