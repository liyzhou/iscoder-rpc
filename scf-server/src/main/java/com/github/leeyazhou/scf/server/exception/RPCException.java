package com.github.leeyazhou.scf.server.exception;

import com.github.leeyazhou.scf.core.entity.ErrorState;
import com.github.leeyazhou.scf.core.entity.KeyValuePair;
import com.github.leeyazhou.scf.protocol.sdp.RequestProtocol;

/**
 * service frame exception use in proxy and proxyhandle
 * 
 */
public class RPCException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private ErrorState state;

	private String errorMsg;

	private String fromIP;

	private String toIP;

	private Object sdp;

	/**
	 * create exception
	 * 
	 * @param errorMsg
	 * @param fromIP
	 * @param toIP
	 * @param mb
	 * @param state
	 * @param cause
	 */
	public RPCException(String errorMsg, String fromIP, String toIP, Object sdp, ErrorState state,
			Throwable cause) {
		super(errorMsg, cause);
		this.setState(state);
		this.setErrorMsg(errorMsg);
		this.setFromIP(fromIP);
		this.setToIP(toIP);
		this.setSdp(sdp);
	}

	public RPCException(String errorMsg, ErrorState state, Throwable cause) {
		this(errorMsg, "", "", null, state, cause);
	}

	public RPCException(String errorMsg, ErrorState state) {
		this(errorMsg, "", "", null, state, null);
	}

	public RPCException(ErrorState state, Throwable cause) {
		this("", "", "", null, state, cause);
	}

	public RPCException(ErrorState state) {
		this("", "", "", null, state, null);
	}

	@Override
	public void printStackTrace() {
		System.out.println("-------------------------begin-----------------------------");
		System.out.println("fromIP:" + this.getFromIP());
		System.out.println("toIP:" + this.getToIP());
		System.out.println("state:" + this.getState().toString());
		System.out.println("errorMsg:" + this.getErrorMsg());
		System.out.println("MessageBodyBase:");

		if (sdp.getClass() == RequestProtocol.class) {
			RequestProtocol request = (RequestProtocol) this.sdp;
			System.out.println("Server.Lookup:" + request.getLookup());
			System.out.println("Server.MethodName:" + request.getMethodName());
			System.out.println("Server.ParaKVList:");
			for (KeyValuePair kv : request.getParaKVList()) {
				System.out.println("key:" + kv.getKey() + "---value:" + kv.getValue());
			}
		}
		super.printStackTrace();
		System.out.println("--------------------------end------------------------------");
	}

	public void setState(ErrorState state) {
		this.state = state;
	}

	public ErrorState getState() {
		return state;
	}

	public String getFromIP() {
		return fromIP;
	}

	public void setFromIP(String fromIP) {
		this.fromIP = fromIP;
	}

	public String getToIP() {
		return toIP;
	}

	public void setToIP(String toIP) {
		this.toIP = toIP;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public Object getSdp() {
		return sdp;
	}

	public void setSdp(Object sdp) {
		this.sdp = sdp;
	}
}