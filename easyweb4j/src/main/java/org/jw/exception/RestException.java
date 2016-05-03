package org.jw.exception;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * 异常类
 * Created by CaiDongYu on 2016/4/8.
 */
public class RestException extends RuntimeException implements HttpServletResponse{
	private static final long serialVersionUID = 1L;

	private int status;
	private String message;
	
	
	public RestException(int status, String message) {
		this.status = status;
		this.message = message;
	}
	

	@Override
	public int getStatus() {
		return status;
	}
	
	@Override
	public String getMessage() {
		return message;
	}

	@Deprecated
	@Override
	public void flushBuffer() throws IOException {
	}

	@Deprecated
	@Override
	public int getBufferSize() {
		return 0;
	}

	@Deprecated
	@Override
	public String getCharacterEncoding() {
		return null;
	}

	@Deprecated
	@Override
	public String getContentType() {
		return null;
	}

	@Deprecated
	@Override
	public Locale getLocale() {
		return null;
	}

	@Deprecated
	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return null;
	}

	@Deprecated
	@Override
	public PrintWriter getWriter() throws IOException {
		return null;
	}

	@Deprecated
	@Override
	public boolean isCommitted() {
		return false;
	}

	@Deprecated
	@Override
	public void reset() {}

	@Deprecated
	@Override
	public void resetBuffer() {}

	@Deprecated
	@Override
	public void setBufferSize(int arg0) {}

	@Deprecated
	@Override
	public void setCharacterEncoding(String arg0) {}

	@Deprecated
	@Override
	public void setContentLength(int arg0) {}

	@Deprecated
	@Override
	public void setContentLengthLong(long arg0) {}

	@Deprecated
	@Override
	public void setContentType(String arg0) {}

	@Deprecated
	@Override
	public void setLocale(Locale arg0) {}

	@Deprecated
	@Override
	public void addCookie(Cookie arg0) {}

	@Deprecated
	@Override
	public void addDateHeader(String arg0, long arg1) {}

	@Deprecated
	@Override
	public void addHeader(String arg0, String arg1) {}

	@Deprecated
	@Override
	public void addIntHeader(String arg0, int arg1) {}

	@Deprecated
	@Override
	public boolean containsHeader(String arg0) {
		return false;
	}

	@Deprecated
	@Override
	public String encodeRedirectURL(String arg0) {
		return null;
	}

	@Deprecated
	@Override
	public String encodeRedirectUrl(String arg0) {
		return null;
	}

	@Deprecated
	@Override
	public String encodeURL(String arg0) {
		return null;
	}

	@Deprecated
	@Override
	public String encodeUrl(String arg0) {
		return null;
	}

	@Deprecated
	@Override
	public String getHeader(String arg0) {
		return null;
	}

	@Deprecated
	@Override
	public Collection<String> getHeaderNames() {
		return null;
	}

	@Deprecated
	@Override
	public Collection<String> getHeaders(String arg0) {
		return null;
	}


	@Deprecated
	@Override
	public void sendError(int arg0) throws IOException {}

	@Deprecated
	@Override
	public void sendError(int arg0, String arg1) throws IOException {}

	@Deprecated
	@Override
	public void sendRedirect(String arg0) throws IOException {}

	@Deprecated
	@Override
	public void setDateHeader(String arg0, long arg1) {}

	@Deprecated
	@Override
	public void setHeader(String arg0, String arg1) {}

	@Deprecated
	@Override
	public void setIntHeader(String arg0, int arg1) {}

	@Deprecated
	@Override
	public void setStatus(int arg0) {}

	@Deprecated
	@Override
	public void setStatus(int arg0, String arg1) {}
	
}
