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
package org.axe.constant;

/**
 * Http Content Type类型
 * @author CaiDongyu on 2016年5月10日 上午8:29:06.
 */
public enum ContentType {
	
	APPLICATION_JSON("application/json"),
	APPLICATION_HTML("text/html; charset=utf-8"),
	APPLICATION_TXT("text/plain; charset=utf-8"),
	APPLICATION_XML("text/xml; charset=utf-8"),
	APPLICATION_CSS("text/css"),
	APPLICATION_JS("application/x-javascript"),
	IMAGE_ICON("image/x-icon"),
	IMAGE_JPEG("image/jpeg"),
	IMAGE_PNG("image/png"),
	IMAGE_GIF("image/gif"),
	FONT_WOFF2("font/woff2"),
	FONT_WOFF("font/woff"),
	FONT_TTF("font/ttf"),
	FONT_SVG("text/xml"),
	FONT_EOT("font/eot"),
	VIDEO_MPEG4("video/mpeg4"),
	ZIP_FILE("application/zip");
	
	public String CONTENT_TYPE;

	private ContentType(String CONTENT_TYPE) {
		this.CONTENT_TYPE = CONTENT_TYPE;
	}
	
}
