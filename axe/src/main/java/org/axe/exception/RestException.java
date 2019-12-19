/**
 * MIT License
 * 
 * Copyright (c) 2017 CaiDongyu
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.axe.exception;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * 异常类
 * @author CaiDongyu on 2016/4/8.
 */
public final class RestException extends RuntimeException implements HttpServletResponse{
	private static final long serialVersionUID = 1L;
	
	/**
	 * 请求执行失败
	 */
	public static final int SC_REQUEST_FAILED = 800;
	
	
	private int status;
	private String message;
	
	public RestException(String message) {
		this.status = SC_REQUEST_FAILED;
		this.message = message;
	}
	
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
