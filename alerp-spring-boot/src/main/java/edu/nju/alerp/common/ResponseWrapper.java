package edu.nju.alerp.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class ResponseWrapper extends HttpServletResponseWrapper {

    private HttpServletResponse response;

    private ByteArrayOutputStream output;

    @Getter
    @Setter
    private String content;

    /**
     * Constructs a response adaptor wrapping the given response.
     *
     * @param response
     * @throws IllegalArgumentException if the response is null
     */
    public ResponseWrapper(HttpServletResponse response) {
        super(response);
        output = new ByteArrayOutputStream();
        this.response = response;
    }



    public String getBody() {
        content = new String(output.toByteArray());
        return content;
    }

    @Override
    public void addCookie(Cookie cookie) {
        this.response.addCookie(cookie);
    }

    @Override
    public ServletOutputStream getOutputStream() {
        return new ServletOutputStreamWrapper(this.output , this.response);
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(new OutputStreamWriter(this.output , content));
    }

    @Data
    @AllArgsConstructor
    private static class ServletOutputStreamWrapper extends ServletOutputStream {

        private ByteArrayOutputStream outputStream;
        private HttpServletResponse response;

        @Override
        public void write(int b) throws IOException {
            this.outputStream.write(b);
        }

        @Override
        public void flush() throws IOException {
            if (! this.response.isCommitted()) {
                byte[] body = this.outputStream.toByteArray();
                ServletOutputStream outputStream = this.response.getOutputStream();
                outputStream.write(body);
                outputStream.flush();
            }
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {

        }
    }

}
